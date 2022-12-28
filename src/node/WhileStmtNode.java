package node;

import control_flow.BasicBlock;
import control_flow.quaternion.J;
import control_flow.quaternion.Jwf;
import error.Context;
import error.ErrorRet;
import error.IRRet;

public class WhileStmtNode extends Node{

    private Context ctx;

    public WhileStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        boolean flag = symbol.isInLoop();
        if (!flag) {
            symbol.setInLoop(true);
        }
        ErrorRet ret = super.check();
        if (!flag) {
            symbol.setInLoop(false);
        }
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        // 保存一下上一个哥们的循环环境,乐
        BasicBlock breakBlock = ctx.breakBlock;
        BasicBlock continueBlock = ctx.continueBlock;
        BasicBlock newBreakBlock = controlFlowBuilder.getNewBasicBlock();
        BasicBlock newContinueBlock = controlFlowBuilder.getNewBasicBlock();
        BasicBlock inBlock = controlFlowBuilder.getNewBasicBlock();
        ctx.breakBlock = newBreakBlock;
        ctx.continueBlock = newContinueBlock;
        ctx.inBlock = inBlock;
        ctx.outBlock = newBreakBlock;

        controlFlowBuilder.insertBasicBlock(newContinueBlock);
        IRRet tmp = new IRRet();
        getChildren().get(2).buildIR(ctx,tmp); // stmt buildIR
        if (!(isDigit(tmp.ret) && Integer.parseInt(tmp.ret) == 0)) {
            controlFlowBuilder.insertBasicBlock(inBlock);
            getChildren().get(4).buildIR(ctx,ret); // block buildIR
            controlFlowBuilder.insertQuaternion(new J(newContinueBlock));
        }
        controlFlowBuilder.insertBasicBlock(newBreakBlock);

        ctx.breakBlock = breakBlock;
        ctx.continueBlock = continueBlock;
    }
}
