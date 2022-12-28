package control_flow.quaternion;

import node.Node;

import java.util.ArrayList;

public class LoadPtr extends Quaternion{

    private String arg1,base,offset;

    public LoadPtr(String arg1,String base,String offset) {
        super(QuaternionKind.LoadPtr);
        this.arg1 = arg1;
        this.base = base;
        this.offset = offset;
    }

    @Override
    public void assembly() {
        arg1 = defineName;
        int flag = 0;
        offset = Node.isDigit(offset)?offset:useNames.get(flag++);
        base = Node.isDigit(base)?base:useNames.get(flag++);

        if (base.charAt(0) == 's') {
            System.out.printf("lw $28 %s($sp)\n",base.substring(2));
            base = "$28";
        } else if (base.charAt(0) == 'v') { // 全局变量
            System.out.printf("la $28 %s\n",base);
            base = "$28";
        } else if (base.charAt(0) == 'a') { // 局部数组
            System.out.printf("addiu $28 $sp %s\n",base.substring(2));
            base = "$28";
        }

        if (Node.isDigit(offset)) {
            offset = String.valueOf(Integer.parseInt(offset) * 4);
            System.out.printf("li $27 %s\n",offset);
        } else if (offset.charAt(0) != '$') {
            System.out.printf("lw $27 %s($sp)\n",offset.substring(2));
            System.out.print("sll $27 $27 2\n");
        } else { // 是寄存器的值,不会是全局变量
            System.out.printf("sll $27 %s 2\n",offset);
        }
        offset = "$27";

        if (arg1.charAt(0) != '$') {
            System.out.printf("addu $27 %s %s\n",offset,base);
            System.out.printf("sw $27 %s($sp)\n",arg1.substring(2));
        } else {
            System.out.printf("addu %s %s %s\n",arg1,offset,base);
        }
    }

    @Override
    public void printIR() {
        System.out.println(this.getQuaternionKind() + " " + arg1 + " " + offset + "(" + base +")");
    }

    @Override
    public String getDefine() {
        return arg1;
    }

    @Override
    public ArrayList<String> getVars() {
        ArrayList<String> ret = new ArrayList<>();
        if (!Node.isDigit(arg1)) {
            ret.add(arg1);
        }
        if (!Node.isDigit(offset)) {
            ret.add(offset);
        }
        if (!Node.isDigit(base)) {
            ret.add(base);
        }
        return ret;
    }
}