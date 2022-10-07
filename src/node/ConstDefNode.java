package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;
import lexer.Token;

import java.util.ArrayList;

public class ConstDefNode extends Node{
    public ConstDefNode(SyntaxKind input) {
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
        ArrayList<Integer> initVal = new ArrayList<>();
        for (Node each:getChildren()) {
            if (each instanceof TerminalTkNode) {
                if (((TerminalTkNode) each).getTokenType() == Token.IDENFR) {
                    name = ((TerminalTkNode) each).getWord().getText();
                    line = ((TerminalTkNode) each).getWord().getLine();
                }
            }
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof ConstExpNode) {
                dimension.addAll(tmp.value);
            } else if (each instanceof ConstInitValNode) {
                initVal.addAll(tmp.value);
            }
        }
        if (!symbol.addVar(true, name,dimension,initVal)) {
            ret.errorList.add(new Pair<>(ErrorKind.Redefined, line));
        }
        return ret;
    }
}
