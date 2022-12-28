package control_flow.quaternion;

import node.Node;

import java.util.ArrayList;

public class Push extends Quaternion{
    private String arg1;
    private int num;

    public Push(String arg1,int num) {
        super(QuaternionKind.Push);
        this.arg1 = arg1;
        this.num = num;
    }

    @Override
    public void assembly() {
        int flag = 0;
        String param = Node.isDigit(arg1) ? arg1:useNames.get(flag);
        if (num < 4) {
            if (Node.isDigit(param)) {
                System.out.printf("li $a%d %s\n",num,param);
            } else if(param.charAt(0) == '$') {
                System.out.printf("move $a%d %s\n",num,param);
            } else {
                System.out.printf("lw $a%d %s($sp)\n",num,param.substring(2));
            }
        } else {
            if (Node.isDigit(param)) {
                System.out.printf("li $27 %s\n",param);
                param = "$27";
            } else if (param.charAt(0) != '$') {
                System.out.printf("lw $27 %s($sp)\n",param.substring(2));
                param = "$27";
            }
            System.out.printf("sw %s %d($sp)\n",param,num*-4);
        }
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
