package node;

import error.ErrorKind;
import error.ErrorRet;
import error.VarEntry;
import error.Pair;
import lexer.Token;

import java.util.ArrayList;

public class LValNode extends Node{

    public LValNode(SyntaxKind input) {
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
        ArrayList<Integer> dimension;
        ArrayList<Integer> value;
        VarEntry var = null;
        boolean flag = false;
        int cnt = 0;
        ArrayList<Integer> index = new ArrayList<>();
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.IDENFR)) {
                var = symbol.getVar(((TerminalTkNode) each).getWord().getText());
                if (var == null) {
                    ret.errorList.add(new Pair<>(ErrorKind.Undefined,((TerminalTkNode) each).getWord().getLine()));
                } else if (var.isConst() && symbol.isLVal()) {
                    ret.errorList.add(new Pair<>(ErrorKind.Modify_Const_Error,((TerminalTkNode) each).getWord().getLine()));
                }
                symbol.setLVal(false);
                flag = var != null && var.isConst() && symbol.isConst();// 可以求出value
            } else if (!(each instanceof TerminalTkNode || each instanceof ErrorNode)) { // 是 exp
                // 求dimension
                cnt++;
                index.addAll(tmp.value);
            }
        }
        if (flag) {
            ret.value.add(var.getValue(index));
        }

        ret.dimension.addAll(var != null ? var.getDimension().subList(cnt,var.getDimension().size()) : new ArrayList<>());
        return ret;
    }
}
