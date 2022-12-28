package control_flow.quaternion;

import error.IRRet;
import node.Node;

import java.util.ArrayList;

public class Return extends Quaternion{
    private String arg1;

    public Return(String arg1) {
        super(QuaternionKind.Return);
        this.arg1 = arg1;
    }

    @Override
    public void assembly() {
        if (arg1.equals(IRRet.DEFAULT)) {
            return;
        }
        int flag = 0;
        String ret = Node.isDigit(arg1) ? arg1:useNames.get(flag);
        if (Node.isDigit(ret)) {
            System.out.printf("li $v0 %s\n",ret);
        } else if (ret.charAt(0) == '$'){
            System.out.printf("move $v0 %s\n",ret);
        } else {
            System.out.printf("lw $v0 %s($sp)\n",ret.substring(2));
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
        if(!Node.isDigit(arg1) && !arg1.equals(IRRet.DEFAULT)) {
            ret.add(arg1);
        }
        return ret;
    }
}
