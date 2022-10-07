package node;

import error.ErrorRet;
import lexer.Token;

public class BlockNode extends Node{
    public BlockNode(SyntaxKind input) {
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
        for (int i = 0;i < getChildren().size();i++) {
            Node each = getChildren().get(i);
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (i == getChildren().size()-2 && each instanceof BlockItemNode && each.getChildren().get(0).getChildren().get(0) instanceof ReturnStmtNode) {
                ret.hasReturn = ret.hasReturn || tmp.hasReturn;
            }
            if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.RBRACE)) {
                ret.rBraceLine = ((TerminalTkNode) each).getWord().getLine();
            }
        }
        symbol.endBlock();
        return ret;
    }
}
