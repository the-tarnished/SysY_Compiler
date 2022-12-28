package node;

import control_flow.quaternion.Add;
import control_flow.quaternion.Sub;
import error.Context;
import error.ErrorRet;
import error.IRRet;

import java.util.ArrayList;
import java.util.Objects;

public class AddExpNode extends Node{
    public AddExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        boolean flag = true;
        boolean canCal = false;
        String  op = "";
        if (getChildren().size() == 1) {
            ret = getChildren().get(0).check();
        } else {
            for (Node node:getChildren()) {
                ErrorRet tmp = node.check();
                ret.errorList.addAll(tmp.errorList);
                ret.dimension.addAll(tmp.dimension);
                if (flag) {
                    ret.value = tmp.value;
                    flag = false;
                }
                if (canCal) { // 一定可以计算出来
                    switch (op) {
                        case "+":
                            ret.value.set(0,ret.value.get(0) +  tmp.value.get(0));
                            break;
                        case "-":
                            ret.value.set(0,ret.value.get(0) -  tmp.value.get(0));
                            break;
                        default:
                            break;
                    }
                }
                if (node instanceof TerminalTkNode && symbol.isConst()) { // 一定可以计算出来
                    canCal =  true;
                    op = ((TerminalTkNode) node).getWord().getText();
                }
            }
        }
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        String  op = "";
        if (getChildren().size() == 1) {
            getChildren().get(0).buildIR(ctx,ret);
        } else {
            // 如果是数字就计算算了,不想判断是不是const里面了,头疼
            ArrayList<IRRet> tmps = new ArrayList<>();
            for (Node each:getChildren()) {
                IRRet tmp = new IRRet();
                each.buildIR(ctx,tmp);
                tmps.add(tmp);
                if (each instanceof TerminalTkNode) { // 一定可以计算出来
                    op = ((TerminalTkNode) each).getWord().getText();
                }
            }
            if (isDigit(tmps.get(0).ret) && isDigit(tmps.get(2).ret)) {
                int val = Objects.equals(op, "+") ? Integer.parseInt(tmps.get(0).ret) + Integer.parseInt(tmps.get(2).ret) : Integer.parseInt(tmps.get(0).ret) - Integer.parseInt(tmps.get(2).ret);
                ret.ret = Integer.toString(val);
            } else { // 寄咯
                ret.ret = controlFlowBuilder.getTmpVar();
                switch (op) {
                    case "+":
                        controlFlowBuilder.insertQuaternion(new Add(ret.ret,tmps.get(0).ret,tmps.get(2).ret));
                        break;
                    case "-":
                        controlFlowBuilder.insertQuaternion(new Sub(ret.ret,tmps.get(0).ret,tmps.get(2).ret));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
