package control_flow;

import control_flow.quaternion.Alloc;
import control_flow.quaternion.Quaternion;
import error.Symbol;

import java.util.*;
import java.util.stream.Collectors;

import static control_flow.ControlFlowBuilder.OPT;

public class Func {
    private ArrayList<BasicBlock> basicBlocks;
    private String label;
    private int paramNumber;
    private HashSet<String> varSet;
    private int memory;
    private HashMap<String,Integer> varInMem;
    private HashMap<String,Integer> arrayInMem;
    private HashMap<Integer,Integer> regInMem;
    public static final int[] envReg = {8,9,10,11,12,13,14,15,16,17,18,19,20,21,22,23,24,25,31};
    public HashMap<String, Integer> var2Usage; // 每个变量的使用情况
    public RegisterAlloc registerAlloc;
    private HashMap<String,Integer> variableWeight;
    private ConflictGraph conflictGraph;

    public Func (String label,int paramNumber) {
        this.label = label;
        this.paramNumber = paramNumber;
        memory += paramNumber*4;
        basicBlocks = new ArrayList<>();
        varSet = new HashSet<>();
        regInMem = new HashMap<>();
        varInMem = new HashMap<>();
        arrayInMem = new HashMap<>();
        var2Usage = new HashMap<>();
        registerAlloc = new RegisterAlloc(this);
        variableWeight = new HashMap<>();
        conflictGraph = new ConflictGraph(variableWeight);
    }

    public void insertBasicBlock(BasicBlock basicBlock) {
        basicBlocks.add(basicBlock);
    }

    public void print() {
        System.out.println(label+":");
        basicBlocks.forEach(BasicBlock::print);
    }

    public void organizeRegister() {
        registerAlloc.OptRegisterAllocate(conflictGraph);
    }

    public void organizeMem() {
        // 变量区栈空间分配,这里不管全局变量
        for (BasicBlock i : basicBlocks) {
            for (Quaternion j :i.getQuaternions()) {
                String define = j.getDefine();
                if (Symbol.getInstance().isGlobalVar(define)) {
                    continue;
                }
                if (define != null && !varSet.contains(define)) {
                    varSet.add(define);
                    if (j instanceof Alloc) {
                        memory += ((Alloc) j).getSize() * 4;
                        arrayInMem.put(define,memory);
                    } else {
                        memory += 4;
                        varInMem.put(define,memory);
                    }
                }
            }
        }

        // 寄存器区栈空间分配,只分配t0-t9 s0-s7 ra 共19个寄存器,但是如果是main函数没有人调用它,所以不会save register
        if (!label.equals("func_main")) {
            for (int tmp : envReg) {
                memory += 4;
                regInMem.put(tmp, memory);

            }
        } else {
            memory += 4;
            regInMem.put(31, memory);

        }
    }

    public void pageOptStrategyInit() {  // 引用计数的opt算法,只用于非opt情况,大概优化的话尝试 ssa + 图着色吧??或许吧,乐,先获得每一个的使用情况
        for (BasicBlock i : basicBlocks) {
            for (Quaternion j :i.getQuaternions()) {
                ArrayList<String> vars = j.getVars();
                for (String var :vars) {
                    if (!isLocalVar(var))
                        continue;
                    if (var2Usage.containsKey(var)) {
                        var2Usage.put(var,var2Usage.get(var) + 1);
                    } else {
                        var2Usage.put(var,1);
                    }
                }
            }
        }
        List<Integer> times = var2Usage.values().stream().sorted().collect(Collectors.toList());
        ArrayList<Integer> selectedTimes = new ArrayList<>();
        for (int i = times.size()-1;i >= 0 && i >= times.size()-RegisterAlloc.REGISTER_NUM;i--) {
            selectedTimes.add(times.get(i));
        }

        HashSet<String> selectedVars = new HashSet<>();
        for (Integer each : selectedTimes) {
            for (Map.Entry<String,Integer> entry:var2Usage.entrySet()) {
                if (entry.getValue().equals(each) && !selectedVars.contains(entry.getKey())) {
                    selectedVars.add(entry.getKey());
                    break;
                }
            }
        }
        registerAlloc.initRegisterAllocate(selectedVars);

    }

    public void funcSave() {
        // 栈空间开辟
        System.out.printf("subi $sp $sp %s \n",memory);

        // 寄存器环境保存
        for (Map.Entry<Integer,Integer> entry : regInMem.entrySet()) {
            System.out.printf("sw $%s %s($sp)\n", entry.getKey(), getOffset(entry.getValue()));
        }
    }

    public void funcLoad() {
            // 寄存器环境恢复
        for (Map.Entry<Integer,Integer> entry : regInMem.entrySet()) {
            System.out.printf("lw $%s %s($sp)\n", entry.getKey(), getOffset(entry.getValue()));
        }
        // 栈空间恢复
        System.out.printf("addi $sp $sp %s \n",memory);
        System.out.println("jr $ra");
    }

    public void assembly() {
        System.out.printf("%s: \n",label);
        organizeMem();
        funcSave();
        if (OPT) {
            addEdges();
            getOutVariable();
            getVariableWeight();
            initConflictGraph();
            constructConflictGraph();
            organizeRegister();// 庆贺吧,图着色!
            for (BasicBlock basicBlock : basicBlocks) {
                basicBlock.assembly(registerAlloc,this);
            }
        } else {
            pageOptStrategyInit();
            for (BasicBlock basicBlock : basicBlocks) {
                basicBlock.assembly(registerAlloc,this);
            }
        }
    }

    public HashMap<String, Integer> getVarInMem() {
        return varInMem;
    }

    public HashMap<String, Integer> getArrayInMem() {
        return arrayInMem;
    }

    public int getMemory() {
        return memory;
    }

    public int getOffset(int offset) {
        return memory - offset;
    }

    public void addEdges() {
        for (int i = 0;i < basicBlocks.size() ;i++) {
            BasicBlock tmp = basicBlocks.get(i);
            if (tmp.accessToNextBlock()) {
                tmp.addSuccessor(basicBlocks.get(i+1));
                basicBlocks.get(i+1).addPredecessor(tmp);
            }
            BasicBlock jumpBlock = tmp.getJumpBlock();
            if (jumpBlock != null) {
                tmp.addSuccessor(jumpBlock);
                jumpBlock.addPredecessor(tmp);
            }
        }
    }

    private void getOutVariable() {
        for (BasicBlock basicBlock : basicBlocks) {
            basicBlock.InitUseDef(this);
        }
        boolean flag =  true;
        int unChanged = 0;
        while (flag) {
            for (BasicBlock basicBlock : basicBlocks) {
                HashSet<String> preInVar = new HashSet<>(basicBlock.getInVar());
                HashSet<String> preOutVar = new HashSet<>(basicBlock.getOutVar());
                basicBlock.getUse().forEach(basicBlock::insertIn);
                for (String var:preOutVar) {
                    if (!basicBlock.getDef().contains(var)) {
                        basicBlock.insertIn(var);
                    }
                }
                if (preInVar.equals(basicBlock.getInVar()) && preOutVar.equals(basicBlock.getOutVar())) {
                    unChanged++;
                }
            }
            if (unChanged == basicBlocks.size()) {
                flag = false;
            } else {
                unChanged = 0;
            }
        }
    }

    private void getVariableWeight() {
        basicBlocks.forEach(each -> each.getVariableWeight(variableWeight, this));
    }

    private void constructConflictGraph() {
        basicBlocks.forEach(basicBlock -> basicBlock.constructConflictGraph(conflictGraph,this));
    }

    public boolean isLocalVar(String var) {
        return varInMem.containsKey(var)||arrayInMem.containsKey(var);
    }

    public void initConflictGraph() {
        basicBlocks.forEach(basicBlock -> basicBlock.initConflictGraph(conflictGraph,this));
    }


}
