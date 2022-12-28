package control_flow.quaternion;

import node.Node;

import java.util.ArrayList;

public class Pop extends Quaternion{
    private String arg1;
    private int num;

    public Pop(String arg1, int num) {
        super(QuaternionKind.Pop);
        this.arg1 = arg1;
        this.num = num;
    }

    public String getArg1() {
        return arg1;
    }

    public int getNum() {
        return num;
    }

    @Override
    public void assembly() {
        int flag = 0;
        arg1 = Node.isDigit(arg1) ? arg1:defineName;
    }

    @Override
    public void printIR() {
        System.out.println(getQuaternionKind() + " " + arg1);
    }

    @Override
    public String getDefine() {
        return arg1;
    }

    @Override
    public ArrayList<String> getVars() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(arg1);
        return ret;
    }
}
