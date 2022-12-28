package node;

import control_flow.quaternion.PlayBack;
import control_flow.quaternion.Return;
import error.*;

public class ReturnStmtNode extends Node{
    public ReturnStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        boolean hasExp = false;
        int line = ((TerminalTkNode)getChildren().get(0)).getWord().getLine();
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            hasExp = hasExp || each instanceof ExpNode;
        }
        if (symbol.isVoid() && hasExp) {
            ret.errorList.add(new Pair<>(ErrorKind.Void_Return_Invalid,line));
        }
        ret.hasReturn = true;
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        String retValue = ret.ret;
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof ExpNode) {
                retValue = tmp.ret;
            }
        }
        controlFlowBuilder.insertQuaternion(new Return(retValue));
        controlFlowBuilder.insertQuaternion(new PlayBack());
        ret.hasReturn = true;
    }
}
