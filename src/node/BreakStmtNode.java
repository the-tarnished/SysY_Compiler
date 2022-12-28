package node;

import control_flow.quaternion.J;
import error.*;

public class BreakStmtNode extends Node{
    public BreakStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = super.check();
        int line;
        line = ((TerminalTkNode)getChildren().get(0)).getWord().getLine();
        if (!symbol.isInLoop()) {
            ret.errorList.add(new Pair<>(ErrorKind.Break_Continue_Out_Loop,line));
        }
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        controlFlowBuilder.insertQuaternion(new J(ctx.breakBlock));
        controlFlowBuilder.insertBasicBlock(controlFlowBuilder.getNewBasicBlock());
    }
}
