package control_flow;

import control_flow.quaternion.Quaternion;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ControlFlowBuilder {
    private static ControlFlowBuilder instance = null;
    private int tmpCnt;
    private int strCnt;
    private int basicBlockCnt;
    private final Set<Func> funcs;
    private ArrayList<BasicBlock> basicBlocks;
    private BasicBlock curBlock;
    private Func curFunc;
    public static boolean IR = true,OPT = true;


    private ControlFlowBuilder() {
        tmpCnt = 0;
        strCnt = 0;
        basicBlockCnt = 0;
        funcs = new HashSet<>();
        basicBlocks = new ArrayList<>();
        curBlock = null;
        curFunc = null;
    }

    public static ControlFlowBuilder getInstance() {
        if(instance == null) {
            instance = new ControlFlowBuilder();
        }

        return instance;
    }

    public String getTmpVar() {
        tmpCnt++;
        return "tmp_" + tmpCnt;
    }

    public String getStrLabel() {
        strCnt++;
        return "str_" + strCnt;
    }

    public static String getFuncName(String func) {
        return "func_" + func;
    }

    public static String getVarName(String var,int id) {
        return "var_" + var + "_" + id;
    }

    public void insertQuaternion(Quaternion quaternion) {
        curBlock.insertQuaternion(quaternion);
    }

    public void insertFunc(Func func) {
        funcs.add(func);
        curFunc = func;
    }

    public BasicBlock getNewBasicBlock() {
        basicBlockCnt++;
        return new BasicBlock("label"+basicBlockCnt);
    }

    public void insertBasicBlock(BasicBlock basicBlock) {
        curFunc.insertBasicBlock(basicBlock);
        curBlock = basicBlock;
    }

    public void print() {
        funcs.forEach(Func::print);

    }

    public void assembly() {
        for (Func func:funcs) {
            func.assembly();
        }
    }

}
