package node;

import control_flow.quaternion.Eql;
import control_flow.quaternion.Neq;
import error.Context;
import error.IRRet;

import java.util.ArrayList;

public class EqExpNode extends Node{
    public EqExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        if (getChildren().size() == 1) {
            getChildren().get(0).buildIR(ctx,ret);
        } else {
            ArrayList<IRRet> rets = new ArrayList<>();
            String tmpVal = null;
            for (Node each:getChildren()) {
                IRRet tmp = new IRRet();
                each.buildIR(ctx,tmp);
                rets.add(tmp);
            }
            boolean canCal = isDigit(rets.get(0).ret) && isDigit(rets.get(2).ret);
            if (canCal) {
                switch (rets.get(1).ret) {
                    case "==":
                        tmpVal = Integer.parseInt(rets.get(0).ret) == Integer.parseInt(rets.get(2).ret) ? "1" : "0";
                        break;
                    case "!=":
                        tmpVal = Integer.parseInt(rets.get(0).ret) != Integer.parseInt(rets.get(2).ret) ? "1" : "0";
                        break;
                    default:
                        break;
                }
            } else {
                tmpVal = controlFlowBuilder.getTmpVar();
                switch (rets.get(1).ret) {
                    case "==":
                        controlFlowBuilder.insertQuaternion(new Eql(tmpVal, rets.get(0).ret, rets.get(2).ret));
                        break;
                    case "!=":
                        controlFlowBuilder.insertQuaternion(new Neq(tmpVal, rets.get(0).ret, rets.get(2).ret));
                        break;
                    default:
                        break;
                }
            }
            ret.ret = tmpVal;
        }
    }
}
