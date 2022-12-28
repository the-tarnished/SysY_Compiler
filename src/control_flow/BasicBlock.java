package control_flow;

import control_flow.quaternion.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;

public class BasicBlock {
    private String label;
    private ArrayList<Quaternion> quaternions;
    private Quaternion tail;
    private ArrayList<BasicBlock> successor;
    private ArrayList<BasicBlock> predecessor;
    private HashSet<String> def;
    private HashSet<String> use;
    private HashSet<String> inVar;
    private HashSet<String> outVar;


    public void insertDef(Quaternion q, Func func) {
        String define = q.getDefine();
        if (define != null && !use.contains(define) && func.isLocalVar(define)) {
            def.add(define);
        }
    }

    public void insertUse(Quaternion q, Func func) {
        ArrayList<String> uses = q.getUse();
        for (String var:uses) {
            if (!def.contains(var) && func.isLocalVar(var)) {
                use.add(var);
            }
        }
    }

    public HashSet<String> getDef() {
        return def;
    }

    public HashSet<String> getUse() {
        return use;
    }

    public void insertIn(String var) {
        inVar.add(var);
        for (BasicBlock basicBlock:predecessor) {
            basicBlock.insertOut(var);
        }
    }

    public void insertOut(String var) {
        outVar.add(var);
    }

    public HashSet<String> getInVar() {
        return inVar;
    }

    public HashSet<String> getOutVar() {
        return outVar;
    }

    public BasicBlock(String label) {
        this.label = label;
        quaternions = new ArrayList<>();
        tail = null;
        successor = new ArrayList<>();
        predecessor = new ArrayList<>();
        def = new HashSet<>();
        use = new HashSet<>();
        inVar = new HashSet<>();
        outVar = new HashSet<>();
    }

    public String getLabel() {
        return label;
    }

    public void insertQuaternion(Quaternion quaternion) {
        this.quaternions.add(quaternion);
        tail = quaternion;
    }

    public void print() {
        System.out.println(label+":");
        quaternions.forEach(Quaternion::printIR);
    }

    public ArrayList<Quaternion> getQuaternions() {
        return quaternions;
    }

    public void assembly(RegisterAlloc registerAlloc, Func func) {

        System.out.println(label+":");
        for(Quaternion quaternion:quaternions) {
            // 方便debug,输出中间代码
            System.out.print("# ");
            quaternion.printIR();
            // 输出mips
            String define = quaternion.getDefine();
            quaternion.changeDefine(registerAlloc.registerAllocate(define));
            ArrayList<String> uses = quaternion.getUse();
            ArrayList<String> useNames = new ArrayList<>();
            for (String name:uses) {
                useNames.add(registerAlloc.registerAllocate(name));
            }
            quaternion.changeUse(useNames);
            quaternion.assembly();
            if (quaternion instanceof Pop) {
                Pop(((Pop) quaternion).getNum(),((Pop) quaternion).getArg1(),func);
            } else if (quaternion instanceof PlayBack) {
                func.funcLoad();
            } else if (quaternion instanceof Alloc) {
                //判断分到了寄存器了吗,分到了那么给地址,没分到就算了
                define = quaternion.getDefine();
                String reg = registerAlloc.registerAllocate(define);
                if (reg.charAt(0) == '$') {
                    System.out.printf("addiu %s $sp %d\n", reg , func.getOffset(func.getArrayInMem().get(define)));
                }
            }
        }
    }

    private void Pop(int num,String param,Func func) {
        if (num < 4) {
            if (param.charAt(0) == '$'){
                System.out.printf("move %s $a%s\n",param,num);
            } else {
                System.out.printf("move $27 $a%s\n",num);
                System.out.printf("sw $27 %d($sp)\n",Integer.parseInt(param.substring(2)));
            }
        } else {
            if (param.charAt(0) == '$') {
                System.out.printf("lw %s %d($sp)\n", param, func.getMemory() - num * 4);
            }else {
                System.out.printf("lw $27 %d($sp)\n", func.getMemory() - num * 4);
                System.out.printf("sw $27 %s($sp)\n",param.substring(2));
            }
        }
    }

    public boolean accessToNextBlock() {
        return tail == null || ( !(tail instanceof Return) &&  !(tail instanceof J) && !(tail instanceof PlayBack));
    }

    public void addSuccessor(BasicBlock tmp) {
        successor.add(tmp);
    }

    public void addPredecessor(BasicBlock tmp) {
        predecessor.add(tmp);
    }

    public BasicBlock getJumpBlock() {
        if (tail == null) {
            return null;
        }
        return tail.getJumpBlock();
    }

    public void InitUseDef(Func func) {
        for (Quaternion q : quaternions) {
            insertUse(q,func);
            insertDef(q,func);
        }
    }

    public void getVariableWeight(HashMap<String,Integer> variableWeight,Func func) {
        for (Quaternion q:quaternions) {
            ArrayList<String> vars = q.getVars();
            for (String var :vars) {
                if (!func.isLocalVar(var))
                    continue;
                if (variableWeight.containsKey(var)) {
                    variableWeight.put(var,variableWeight.get(var) + 1);
                } else {
                    variableWeight.put(var,1);
                }
            }
        }
    }

    public void constructConflictGraph(ConflictGraph conflictGraph, Func func) {
        HashSet<String> activeVar = new HashSet<>(outVar);
        for (int i = quaternions.size()-1;i>=0;i--) {
            Quaternion q = quaternions.get(i);
            String define = q.getDefine();
            // 集合处理,def集合中只留下localVar && not use && notNull,use集合中留下 localVar
            HashSet<String> useSet = new HashSet<>(q.getUse()).stream().filter(func::isLocalVar).collect(HashSet::new,
                    HashSet::add,
                    HashSet::addAll
            );
            HashSet<String> defSet = new HashSet<>();
            if (define != null && func.isLocalVar(define)) {
                defSet.add(define);
            }
            useSet.forEach(defSet::remove);
            defSet.forEach(each -> {
                activeVar.forEach(each2 -> {
                    conflictGraph.connect(each,each2);
                });
            });
            activeVar.removeAll(defSet);
            activeVar.addAll(useSet);
        }
    }

    public void initConflictGraph(ConflictGraph conflictGraph,Func func) {
        for (Quaternion q:quaternions) {
            if (q instanceof Alloc)
                return;
            String def = q.getDefine();
            HashSet<String> uses = new HashSet<>(q.getUse());
            if (def != null && func.isLocalVar(def)) {
                conflictGraph.addNode(def);
            }
            for (String tmp:uses) {
                if (func.isLocalVar(tmp)) {
                    conflictGraph.addNode(tmp);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BasicBlock that = (BasicBlock) o;

        return Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return label != null ? label.hashCode() : 0;
    }
}
