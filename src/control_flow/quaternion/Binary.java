package control_flow.quaternion;

import node.Node;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class Binary extends Quaternion{
    protected String arg1,arg2,arg3;

    public Binary(String arg1,String arg2,String arg3,QuaternionKind quaternionKind) {
        super(quaternionKind);
        this.arg1 = arg1;
        this.arg2 = arg2;
        this.arg3 = arg3;
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind() + " " + arg1 + " " + arg2 + " " + arg3);
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
        if (!Node.isDigit(arg3)) {
            ret.add(arg3);
        }
        return ret;
    }

    @Override
    public void assembly() {
        int flag = 0;
        boolean singleIns = true;
        this.arg2 =  Node.isDigit(this.arg2)?this.arg2 :useNames.get(flag++);
        this.arg3 = Node.isDigit(this.arg3)? this.arg3:useNames.get(flag++);
        this.arg1 = defineName;
        if (Node.isDigit(arg2)) {
            System.out.printf("li $27 %s\n",arg2);
            arg2 = "$27";
        } else if (arg2.charAt(0) == 's') {
            System.out.printf("lw $27 %s($sp)\n",arg2.substring(2));
            arg2 = "$27";
        }
        if (Node.isDigit(arg3)) {
            System.out.printf("li $28 %s\n",arg3);
            arg3 = "$28";
        }  else if (arg3.charAt(0) == 's') {
            System.out.printf("lw $28 %s($sp)\n",arg3.substring(2));
            arg3 = "$28";
        }
        String op = "";
        switch (this.getQuaternionKind()) {
            case Add:
                op = "addu";
                break;
            case Sub:
                op = "subu";
                break;
            case And:
                op = "and";
                break;
            case Or:
                op = "or";
                break;
            case Eql:
                op = "seq";
                break;
            case Neq:
                op = "sne";
                break;
            case Gt:
                op = "sgt";
                break;
            case Ge:
                op = "sge";
                break;
            case Lt:
                op = "slt";
                break;
            case Le:
                op = "sle";
                break;
            case Mul:
                op = "mulu";
                break;
            case Div:
                op = "div";
                singleIns = false;
                System.out.printf("div %s %s\n", arg2, arg3);
                if (arg1.charAt(0) == '$') {
                    System.out.printf("mflo %s\n", arg1);
                } else {
                    System.out.println("mflo $27");
                    System.out.printf("sw $27 %s($sp)\n", arg1.substring(2));
                }
                break;
            case Mod:
                singleIns = false;
                System.out.printf("div %s %s\n", arg2, arg3);
                if (arg1.charAt(0) == '$') {
                    System.out.printf("mfhi %s\n", arg1);
                } else {
                    System.out.println("mfhi $27");
                    System.out.printf("sw $27 %s($sp)\n", arg1.substring(2));
                }
                break;
            default:
                break;
        }
        if (singleIns) {
            if (arg1.charAt(0) == '$') {
                System.out.printf("%s %s %s %s\n", op, arg1, arg2, arg3);
            } else {
                System.out.printf("%s $27 %s %s\n", op, arg2, arg3);
                System.out.printf("sw $27 %s($sp)\n", arg1.substring(2));
            }
        }
    }
}
