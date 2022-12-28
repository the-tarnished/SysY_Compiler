package control_flow.quaternion;

import node.Node;

import java.util.ArrayList;

public abstract class Single extends Quaternion{

    protected String arg1, arg2;

    public Single(String arg1,String arg2, QuaternionKind quaternionKind) {
        super(quaternionKind);
        this.arg1 = arg1;
        this.arg2 = arg2;
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind() + " " + arg1 + " " + arg2);
    }

    @Override
    public String getDefine() {
        return arg1;
    }

    @Override
    public ArrayList<String> getVars() {
        ArrayList<String> ret = new ArrayList<>();
        ret.add(getDefine());
        if (!Node.isDigit(arg2)) {
            ret.add(arg2);
        }
        return ret;
    }

    @Override
    public final void assembly() {
        if(this.getQuaternionKind().equals(QuaternionKind.Alloc)) {
            // 没分到寄存器,emmm 那地址就在表里面存着,如果分到了寄存器,那么要你懂的,给寄存器地址

            return;
        }
        int flag = 0;
        this.arg2 =  Node.isDigit(this.arg2)?this.arg2 :useNames.get(flag++);
        this.arg1 = defineName;
        if (Node.isDigit(arg2)) {
            System.out.printf("li $27 %s\n",arg2);
            arg2 = "$27";
        } else if (arg2.charAt(0) != '$') {
            System.out.printf("lw $27 %s($sp)\n",arg2.substring(2));
            arg2 = "$27";
        }
        String op = "";
        switch (this.getQuaternionKind()) {
            case Neg:
                if (arg1.charAt(0) == '$') {
                    System.out.printf("subu %s $0 %s\n",arg1,arg2);
                } else {
                    System.out.printf("subu $28 $0 %s\n",arg2);
                    System.out.printf("sw $28 %s($sp)\n",arg1.substring(2));
                }
                break;
            case Not:
                if (arg1.charAt(0) == '$') {
                    System.out.printf("seq %s %s $0\n",arg1,arg2);
                } else {
                    System.out.printf("seq $28 %s $0\n",arg2);
                    System.out.printf("sw $28 %s($sp)\n",arg1.substring(2));
                }
                break;
            case Assign:
                if (arg1.charAt(0) == '$') {
                    System.out.printf("move %s %s\n",arg1,arg2);
                } else {
                    System.out.printf("sw %s %s($sp)\n",arg2,arg1.substring(2));
                }
                break;
            default:
                break;
        }
    }
}
