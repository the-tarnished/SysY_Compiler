package control_flow.quaternion;

import control_flow.ControlFlowBuilder;
import node.Node;

import static control_flow.ControlFlowBuilder.OPT;

public class Mul extends Binary{

    private int onePos;
    private int value;
    public Mul(String arg1, String arg2, String arg3) {
        super(arg1, arg2, arg3, QuaternionKind.Mul);
        onePos = 0;
    }

    @Override
    public void assembly() {
        super.assembly();
    }

    public int getNextOnePos() {
        while((value & 1) != 1) {
            value >>= 1;
            onePos++;
        }
        int ret = onePos;
        value >>= 1;
        onePos++;
        return ret;
    }

}
