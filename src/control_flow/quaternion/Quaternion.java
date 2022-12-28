package control_flow.quaternion;

import control_flow.BasicBlock;

import java.util.ArrayList;

public abstract class Quaternion {
    private QuaternionKind quaternionKind;
    protected String defineName;
    protected ArrayList<String> useNames;

    public Quaternion(QuaternionKind quaternionKind) {
        this.quaternionKind = quaternionKind;
    }

    public abstract void assembly();

    public abstract void printIR();

    public QuaternionKind getQuaternionKind() {
        return quaternionKind;
    }

    public abstract String getDefine();

    public abstract ArrayList<String> getVars();

    public ArrayList<String> getUse() {
        ArrayList<String> ret = getVars();
        String define = getDefine();
        ret.remove(define);
        return ret;
    }

    public void changeDefine(String name) {
        defineName = name;
    }


    public void changeUse(ArrayList<String> names) {
        useNames = names;
    }

    public BasicBlock getJumpBlock() {return null;}
}
