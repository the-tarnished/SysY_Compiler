package node;

import error.*;
import lexer.Token;

import java.util.ArrayList;
import java.util.Collections;
import java.util.stream.Collectors;

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
        if (symbol.addVar(true, name,dimension,initVal) == 0) {
            ret.errorList.add(new Pair<>(ErrorKind.Redefined, line));
        }
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        String name = null;
        ArrayList<Integer> dimension = new ArrayList<>();
        ArrayList<Integer> initVal = new ArrayList<>();
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof TerminalTkNode) {
                if (((TerminalTkNode) each).getTokenType() == Token.IDENFR) {
                    name = ((TerminalTkNode) each).getWord().getText();
                }
            }
            if (each instanceof ConstExpNode) {
                dimension.add(Integer.parseInt(tmp.ret));
            } else if (each instanceof ConstInitValNode) {
                initVal.addAll(tmp.initVal.stream().map(Integer::parseInt).collect(Collectors.toList()));
            }
        }
        int id = symbol.addVar(true,name,dimension,initVal); // 本来应该有一些啊,那个啥赋值操作,但是捏,这是const,应该放到 .data 去,所以,啊,就没必要了,丢给我的汇编去苦恼吧
    }
}
