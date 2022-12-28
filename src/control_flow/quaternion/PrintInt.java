package control_flow.quaternion;

import node.Node;

import java.util.ArrayList;

public class PrintInt extends Quaternion{
    private String arg1;

    public PrintInt(String arg1) {
        super(QuaternionKind.PrintInt);
        this.arg1 = arg1;
    }

    @Override
    public void assembly() {
        int flag = 0;
        String param = Node.isDigit(arg1) ? arg1:useNames.get(flag);
        if (Node.isDigit(param)) {
            System.out.printf("li $a0 %s\n",param);
        } else if(param.charAt(0) == '$') {
            System.out.printf("move $a0 %s\n",param);
        } else {
            System.out.printf("lw $a0 %s($sp)\n",param.substring(2));
        }
        System.out.println("li $v0, 1");
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
        ArrayList<String> ret = new ArrayList<>();
        if (!Node.isDigit(arg1)) {
            ret.add(arg1);
        }
        return ret;
    }
}
