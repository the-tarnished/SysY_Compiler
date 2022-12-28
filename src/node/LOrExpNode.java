package node;

import control_flow.BasicBlock;
import control_flow.quaternion.And;
import control_flow.quaternion.J;
import control_flow.quaternion.Jwf;
import control_flow.quaternion.Or;
import error.Context;
import error.IRRet;

import java.util.ArrayList;

public class LOrExpNode extends Node{
    private IRRet tmp;

    public LOrExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        boolean record = getChildren().size() != 1;
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            if (each instanceof LOrExpNode) {
                BasicBlock tmpBasicBlock = ctx.outBlock; // 切换新的outBlock为 LAndExp所在的block
                ctx.outBlock = controlFlowBuilder.getNewBasicBlock();
                each.buildIR(ctx,tmp);
                if (isDigit(tmp.ret)) {
                    if (Integer.parseInt(tmp.ret) != 0) {
                        ret.ret = "1";
                        return;
                    } else {
                        record = false;
                        controlFlowBuilder.insertBasicBlock(ctx.outBlock);
                        ctx.outBlock = tmpBasicBlock;
                    }
                } else { // 不是常数,变量的话一定里面有了jwf,所以塞入一个j指令,然后切换基本块
                    controlFlowBuilder.insertQuaternion(new J(ctx.inBlock));
                    controlFlowBuilder.insertBasicBlock(ctx.outBlock);
                    ctx.outBlock = tmpBasicBlock;
                }
            } else if (each instanceof LAndExpNode) {
                each.buildIR(ctx,tmp);
                if (isDigit(tmp.ret)) {
                    if (Integer.parseInt(tmp.ret) != 0) {
                        ret.ret = "1";
                    } else { // 说明这里是0
                        if (!record) { // 两个都是假,那直接为假了,直接跳出 block块,如果一个为变量,一个为假,那给一个无条件跳转,使得进入这个cond的直接进入下一个 cond 判断,或者跳出 begin 块
                            ret.ret = "0";
                        }
                    }
                }
            }
        }
    }
}
