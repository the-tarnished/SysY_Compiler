package control_flow.quaternion;

import node.Node;

import java.math.BigInteger;

import static control_flow.ControlFlowBuilder.OPT;

public class Div extends Binary{
    private int leftOneBit;
    private int shiftPos;

    public Div(String arg1, String arg2, String arg3) {
        super(arg1, arg2, arg3, QuaternionKind.Div);
        leftOneBit = 0;
        shiftPos = 0;
    }

    @Override
    public void assembly() {
        if (true) {
            super.assembly();
            return;
        }
        int flag = 0;
        String arg1 =  Node.isDigit(this.arg2)?this.arg2 :useNames.get(flag++);
        String arg2 = Node.isDigit(this.arg3)? this.arg3:useNames.get(flag++);
        String lVal = defineName;
        if (Node.isDigit(arg1) && Node.isDigit(arg2)) {
            System.out.printf("li %s %s\n",lVal,Integer.parseInt(arg1) / Integer.parseInt(arg2)); // 俩都是数了,为啥不直接除法
        } else {
            if (Node.isDigit(arg1)) {
                System.out.printf("li $27 %s\n",arg1);
                arg1 = "$27";
            } else if (Node.isDigit(arg2)) { // 除法优化得思路就是先认为除数是正数,除了之后再符号转换,
                long m = getMagicValue(arg2);
                if (Integer.parseInt(arg2) == 1) {
                    System.out.printf("move %s %s\n",defineName,arg1);
                } else if (Integer.parseInt(arg2) == -1) {
                    System.out.printf("sub %s $0 %s\n",arg1,arg1);
                    System.out.printf("move %s %s\n",defineName,arg1);
                } else if (Integer.bitCount(Integer.parseInt(arg2)) == 1) {
                    System.out.printf("srl $28 %s %d\n",arg1,31); // 获得符号位
                    System.out.printf("addu %s $28 %s\n",defineName,arg1); // 如果为负数,需要+1来向0取整
                    System.out.printf("sra %s %s %d\n",defineName,defineName,leftOneBit);
                } else {
                    System.out.printf("li $28 %s\n",m);
                    System.out.printf("mult $28 %s\n",arg1);
                    System.out.println("mfhi $28");
                    System.out.printf("sra $28 $28 %d\n",shiftPos);
                    System.out.println("slt $at $28 $0");
                    System.out.printf("addu %s $at $28\n",defineName);
                }
                return;
            }
            System.out.printf("div %s %s\n",arg1,arg2);
            System.out.printf("mflo %s\n",lVal);
        }
    }

    private long getMagicValue(String divImm) {
        BigInteger imm = new BigInteger(String.valueOf(Math.abs(Integer.parseInt(divImm))));
        while (imm.compareTo(BigInteger.valueOf(1).shiftLeft(leftOneBit)) > 0) {  // 尽可能使 2^n 大
            leftOneBit++;
        }
        long low = BigInteger.valueOf(1).shiftLeft(32 + leftOneBit).divide(imm).longValue();
        long high = BigInteger.valueOf(1).shiftLeft(32 + leftOneBit).add(BigInteger.valueOf(1)
                .shiftLeft(1 + leftOneBit)).divide(imm).longValue();
        shiftPos = leftOneBit;
        while (high >> 1 > low >> 1 && shiftPos > 0) {
            high >>= 1;
            low >>= 1;
            shiftPos--;
        }
        return high;
    }
}
