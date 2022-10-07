package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;
import lexer.Token;

import java.util.ArrayList;

public class FuncFParamNode extends Node {

    public FuncFParamNode(SyntaxKind input) {
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
        ArrayList<Integer> dimension = new ArrayList<>();
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (isIdent(each)) {
                name = ((TerminalTkNode)each).getWord().getText();
                line = ((TerminalTkNode)each).getWord().getLine();
            } else if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.LBRACK)) {
                dimension.add(0);
            } else if (each instanceof ConstExpNode) {
                dimension.set(dimension.size()-1,tmp.value.get(0));
            }
        }
        if (!symbol.addVar(false,name,dimension)) {
            ret.errorList.add(new Pair<>(ErrorKind.Redefined,line));
        }
        ret.dimension.addAll(dimension);
        return ret;
    }
}
