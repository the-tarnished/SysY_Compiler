package control_flow.quaternion;

import control_flow.BasicBlock;

import java.util.ArrayList;

public class Jwf extends Quaternion{
    private String cond;
    private BasicBlock block;

    public Jwf(String cond,BasicBlock block) {
        super(QuaternionKind.Jwf);
        this.cond = cond;
        this.block = block;
    }

    @Override
    public void assembly() {
        if (useNames.get(0).charAt(0) == '$') {
            System.out.printf("beqz %s %s\n",useNames.get(0),block.getLabel());
        } else {
            System.out.printf("lw $27 %s($sp)\n",useNames.get(0).substring(2));
            System.out.printf("beqz %s %s\n","$27",block.getLabel());
        }
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind() + " " + cond + " " + block.getLabel());
    }

    @Override
    public String getDefine() {
        return null;
    }

    @Override
    public ArrayList<String> getVars() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(cond);
        return ret;
    }

    @Override
    public BasicBlock getJumpBlock() {
        return block;
    }
}
