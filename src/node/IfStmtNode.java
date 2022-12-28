package node;

import control_flow.BasicBlock;
import control_flow.quaternion.J;
import control_flow.quaternion.Jwf;
import error.Context;
import error.IRRet;

public class IfStmtNode extends Node{

    public IfStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        boolean hasElse = getChildren().stream().anyMatch(tmp -> tmp instanceof TerminalTkNode && ((TerminalTkNode) tmp).getWord().getText().equals("else"));
        BasicBlock finalBlock = controlFlowBuilder.getNewBasicBlock();
        BasicBlock elseBlock = hasElse?controlFlowBuilder.getNewBasicBlock():null;
        BasicBlock inBlock = controlFlowBuilder.getNewBasicBlock();
        IRRet tmp = new IRRet();
        ctx.inBlock = inBlock;
        ctx.outBlock = hasElse? elseBlock:finalBlock;
        getChildren().get(2).buildIR(ctx,tmp); // cond buildIR
        if(isDigit(tmp.ret)) {
            if (Integer.parseInt(tmp.ret) == 0) {
                if (hasElse) { // 直接去else
                    controlFlowBuilder.insertBasicBlock(elseBlock);
                    getChildren().get(6).buildIR(ctx,ret); // stmt2 buildIR
                } else {
                    controlFlowBuilder.insertBasicBlock(finalBlock);
                }
            } else { // 直接去in
                controlFlowBuilder.insertBasicBlock(inBlock);
                getChildren().get(4).buildIR(ctx,ret); // stmt1 buildIR
            }
        } else { // 不可确定
            controlFlowBuilder.insertBasicBlock(inBlock);
            getChildren().get(4).buildIR(ctx,ret); // stmt1 buildIR
            if (hasElse) {
                controlFlowBuilder.insertQuaternion(new J(finalBlock));
                controlFlowBuilder.insertBasicBlock(elseBlock);
                getChildren().get(6).buildIR(ctx,ret); // stmt2 buildIR
            }
            controlFlowBuilder.insertBasicBlock(finalBlock);
        }
    }
}
