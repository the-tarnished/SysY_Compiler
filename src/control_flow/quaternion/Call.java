package control_flow.quaternion;

import java.util.ArrayList;

public class Call extends Quaternion{

    private String arg1;

    public Call(String arg1) {
        super(QuaternionKind.Call);
        this.arg1 = arg1;
    }

    @Override
    public void assembly() {
        System.out.printf("jal %s\n",arg1);
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind() + " " + arg1);
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
