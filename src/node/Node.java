package node;

import control_flow.ControlFlowBuilder;
import error.Context;
import error.ErrorRet;
import error.IRRet;
import error.Symbol;
import lexer.Token;
import lexer.Word;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public abstract class Node {

    private final ArrayList<Node> children;
    private final SyntaxKind syntaxKind;
    public static Symbol symbol = Symbol.getInstance();
    public static ControlFlowBuilder controlFlowBuilder = ControlFlowBuilder.getInstance();

    public Node(SyntaxKind input) {
        children = new ArrayList<>();
        this.syntaxKind = input;
    }

    public abstract void print();

    public void addChild(Node child) {
        this.children.add(0,child);
    }

    public ArrayList<Node> getChildren() {
        return this.children;
    }

    public SyntaxKind getSyntaxKind() {
        return syntaxKind;
    }

    public void printChildren() {
        for (Node child:children) {
             child.print();
        }
    }

    public void printSyntaxKind() {
        System.out.printf("<%s>%n",this.getSyntaxKind());
    }

    public void printTokenAndWord(Token tokenType, Word word) {
        System.out.println(tokenType+" "+word.getText());
    }

    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        for (Node each:getChildren()) {
            if (each instanceof BlockNode) {
                symbol.startBlock();
            }
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            ret.dimension.addAll(tmp.dimension);
            ret.value.addAll(tmp.value);
            ret.paramDimension.addAll(tmp.paramDimension);
            ret.hasReturn = ret.hasReturn || tmp.hasReturn;
        }
        return ret;
    }

    public boolean isIdent(Node tmp) {
        return tmp instanceof TerminalTkNode && ((TerminalTkNode) tmp).getTokenType().equals(Token.IDENFR);
    }

    public void buildIR(Context ctx, IRRet ret) {
        if (getChildren().size() == 1) {
            if (getChildren().get(0) instanceof BlockNode) {
                symbol.startBlock();
            }
            getChildren().get(0).buildIR(ctx, ret);
        } else {
            for (Node each : getChildren()) {
                IRRet tmp = new IRRet();
                if (each instanceof BlockNode) {
                    symbol.startBlock();
                }
                each.buildIR(ctx, tmp);
            }
        }
    }

    public static boolean isDigit(String str) {
        return str != null && str.matches("[\\-\\+]*\\d+");
    }

}
