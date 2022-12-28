package control_flow.quaternion;

import control_flow.BasicBlock;

import java.util.ArrayList;

public class J extends Quaternion{
    private BasicBlock block;

    public J(BasicBlock block) {
        super(QuaternionKind.J);
        this.block = block;
    }

    @Override
    public void assembly() {
        System.out.printf("j %s\n",block.getLabel());
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind() + " " + block.getLabel());
    }

    @Override
    public String getDefine() {
        return null;
    }

    @Override
    public ArrayList<String> getVars() {
        return new ArrayList<>();
    }

    @Override
    public BasicBlock getJumpBlock() {
        return block;
    }
}
