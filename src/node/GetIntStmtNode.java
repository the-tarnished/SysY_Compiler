package node;

import control_flow.quaternion.Assign;
import control_flow.quaternion.FetchInt;
import control_flow.quaternion.GetInt;
import control_flow.quaternion.Store;
import error.Context;
import error.ErrorRet;
import error.IRRet;

public class GetIntStmtNode extends Node{
    public GetIntStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        symbol.setLVal(true);
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof LValNode) {
                symbol.setLVal(false);
            }
        }
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        ctx.isLVal = true;
        boolean isArray = false;
        String lVal = " ";
        String rVal = " ";
        String offset = " ";
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof LValNode) {
                ctx.isLVal = false;
                isArray = tmp.isArray;
                lVal = tmp.ret;
                offset = tmp.offset;
            }
        }
        controlFlowBuilder.insertQuaternion(new FetchInt());
        if (isArray) { // store word
            rVal = controlFlowBuilder.getTmpVar();
            controlFlowBuilder.insertQuaternion(new GetInt(rVal));
            controlFlowBuilder.insertQuaternion(new Store(rVal,lVal,offset));
        } else {
            controlFlowBuilder.insertQuaternion(new GetInt(lVal));
        }
    }
}
