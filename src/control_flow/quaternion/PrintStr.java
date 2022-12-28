package control_flow.quaternion;

import java.util.ArrayList;

public class PrintStr extends Quaternion{
    private String arg1;

    public PrintStr(String arg1) {
        super(QuaternionKind.PrintStr);
        this.arg1 = arg1;
    }

    @Override
    public void assembly() {
        System.out.printf("la $a0 %s\n",arg1);
        System.out.println("li $v0, 4");
        System.out.println("syscall");
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