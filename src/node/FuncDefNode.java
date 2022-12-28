package node;

import control_flow.BasicBlock;
import control_flow.ControlFlowBuilder;
import control_flow.Func;
import control_flow.quaternion.PlayBack;
import control_flow.quaternion.Pop;
import error.*;
import lexer.Token;

import java.util.ArrayList;

public class FuncDefNode extends Node{
    public FuncDefNode(SyntaxKind input) {
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
        String name = null;
        int line = 0;
        int rBraceLine = 0;
        boolean isVoid = false;
        ArrayList<ArrayList<Integer>> paramsDimension = new ArrayList<>();
        boolean hasReturn = false;
        for (Node each:getChildren()) {
            if (each instanceof BlockNode) {
                if (!symbol.addFunc(name,isVoid,paramsDimension)) {
                    ret.errorList.add(new Pair<>(ErrorKind.Redefined,line));
                }
            }
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (isIdent(each)) {
                name = ((TerminalTkNode) each).getWord().getText();
                line = ((TerminalTkNode) each).getWord().getLine();
            } else if (each instanceof FuncTypeNode) {
                isVoid = ( (TerminalTkNode) each.getChildren().get(0)).getTokenType().equals(Token.VOIDTK);
                symbol.setVoid(isVoid);
            } else if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.LPARENT)) {
                symbol.startBlock();
            } else if (each instanceof BlockNode) {
                if (tmp.hasReturn) {
                    hasReturn = true;
                }
                rBraceLine = tmp.rBraceLine;
            } else if (each instanceof FuncFParamsNode) {
                paramsDimension.addAll(tmp.paramDimension);
            }
        }
        if (!hasReturn && !isVoid) {
            ret.errorList.add(new Pair<>(ErrorKind.Return_Absence,rBraceLine));
        }
        symbol.setVoid(true);
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        String name = " ";
        boolean isVoid = false;
        ArrayList<String> params = new ArrayList<>();
        for (Node each:getChildren()) {
            if (isIdent(each)) {
                name = ((TerminalTkNode) each).getWord().getText();
            } else if (each instanceof FuncTypeNode) {
                isVoid = ((TerminalTkNode) each.getChildren().get(0)).getTokenType().equals(Token.VOIDTK);
            } else if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.LPARENT)) {
                symbol.startBlock();
            } else if (each instanceof BlockNode) {
                symbol.addFunc(name,isVoid,new ArrayList<>());
                // 新的基本块,该切换环境了,别急
                controlFlowBuilder.insertFunc(new Func(ControlFlowBuilder.getFuncName(name),params.size()));
                controlFlowBuilder.insertBasicBlock(controlFlowBuilder.getNewBasicBlock());
                // 该把参数load一下了捏
                for (int i = 0;i < params.size();i++) {
                    controlFlowBuilder.insertQuaternion(new Pop(params.get(i),i));
                }
            }
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof FuncFParamsNode) {
                params.addAll(tmp.args);
            } else if(each instanceof BlockNode && !tmp.hasReturn) {
                controlFlowBuilder.insertQuaternion(new PlayBack());
            }
        }
    }
}
