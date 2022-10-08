package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;
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
}
