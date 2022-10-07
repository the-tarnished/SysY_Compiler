package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;
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
}
