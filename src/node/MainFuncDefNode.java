package node;

import control_flow.ControlFlowBuilder;
import control_flow.Func;
import control_flow.quaternion.PlayBack;
import control_flow.quaternion.Pop;
import error.*;
import lexer.Token;

import java.util.ArrayList;

public class MainFuncDefNode extends Node{
    public MainFuncDefNode(SyntaxKind input) {
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
        int line = 0;
        int rBraceLine = 0;
        boolean hasReturn = false;
        symbol.setVoid(false);
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (isIdent(each)) {
                line = ((TerminalTkNode) each).getWord().getLine();
            } else if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.LPARENT)) {
                symbol.startBlock();
            } else if (each instanceof BlockNode) {
                if (tmp.hasReturn) {
                    hasReturn = true;
                }
                rBraceLine = tmp.rBraceLine;
            }else if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.RBRACE)) {
                rBraceLine = ((TerminalTkNode) each).getWord().getLine();
            }
        }
        if (!symbol.addFunc("main",false,new ArrayList<>())) {
            ret.errorList.add(new Pair<>(ErrorKind.Redefined,line));
        }
        if (!hasReturn) {
            ret.errorList.add(new Pair<>(ErrorKind.Return_Absence,rBraceLine));
        }
        symbol.setVoid(true);
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        String name = "main";
        boolean isVoid = false;
        for (Node each:getChildren()) {
            if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.LPARENT)) {
                symbol.startBlock();
            } else if (each instanceof BlockNode) {
                symbol.addFunc(name,false,new ArrayList<>());
                // 新的基本块,该切换环境了,别急
                controlFlowBuilder.insertFunc(new Func(ControlFlowBuilder.getFuncName(name),0));
                controlFlowBuilder.insertBasicBlock(controlFlowBuilder.getNewBasicBlock());
            }
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
        }
    }
}
