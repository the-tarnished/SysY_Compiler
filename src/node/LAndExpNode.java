package node;

import control_flow.BasicBlock;
import control_flow.quaternion.And;
import control_flow.quaternion.J;
import control_flow.quaternion.Jwf;
import error.Context;
import error.IRRet;

import java.util.ArrayList;

public class LAndExpNode extends Node{
    public LAndExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        boolean record = getChildren().size() == 1;
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof LAndExpNode) {
                if (isDigit(tmp.ret)) {
                    if (Integer.parseInt(tmp.ret) == 0) {
                        ret.ret = "0";
                        return;
                    } else {
                        record = true;
                    }
                }
            } else if (each instanceof EqExpNode) {
                if (isDigit(tmp.ret)) {
                    if (Integer.parseInt(tmp.ret) == 0) {
                        ret.ret = "0";
                        controlFlowBuilder.insertQuaternion(new J(ctx.outBlock));
                        controlFlowBuilder.insertBasicBlock(controlFlowBuilder.getNewBasicBlock());
                    } else {
                        if (record) { // 两个都是真,那直接为真了
                            ret.ret = "1";
                        }// 有点寄哟xd,变量和1,那什么都不做
                    }
                    return;
                } else { // 每一个EqExpNode则来一次jwf然后切换环境,对于LAndExpNode只需要判断
                    controlFlowBuilder.insertQuaternion(new Jwf(tmp.ret,ctx.outBlock));
                    controlFlowBuilder.insertBasicBlock(controlFlowBuilder.getNewBasicBlock());
                }
            }
        }
    }
}
