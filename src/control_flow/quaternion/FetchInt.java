package control_flow.quaternion;

import java.util.ArrayList;

public class FetchInt extends Quaternion{

    public FetchInt() {
        super(QuaternionKind.FetchInt);
    }

    @Override
    public void assembly() {
        System.out.println("li $v0, 5");
        System.out.println("syscall");
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind());
    }

    @Override
    public String getDefine() {
        return null;
    }

    @Override
    public ArrayList<String> getVars() {
        return new ArrayList<>();
    }
}
