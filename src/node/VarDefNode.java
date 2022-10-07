package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;
import lexer.Token;

import java.util.ArrayList;

public class VarDefNode extends Node{
    public VarDefNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        String name = null;
        int line = 0;
        ArrayList<Integer> dimension = new ArrayList<>();
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof TerminalTkNode) {
                if (((TerminalTkNode) each).getTokenType() == Token.IDENFR) {
                    name = ((TerminalTkNode) each).getWord().getText();
                    line = ((TerminalTkNode) each).getWord().getLine();
                }
            }
            if (each instanceof ConstExpNode) {
                dimension.addAll(tmp.value);
            }
        }
        if (!symbol.addVar(symbol.isConst(), name,dimension)) {
            ret.errorList.add(new Pair<>(ErrorKind.Redefined, line));
        }
        return ret;
    }
}
