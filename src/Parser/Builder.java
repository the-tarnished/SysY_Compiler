package Parser;

import Lexer.Token;
import Lexer.Word;
import Node.*;
import java.util.Stack;

public class Builder {
    private final Stack<Node> children;
    private final Stack<Integer> parents; // 其值表示该 parent 入栈的时候 children的数量,当结束 node build 的时候,多余的孩子都是起直系 child

    public Builder() {
        children = new Stack<>();
        parents = new Stack<>();
    }

    public Node getRoot() {
        return children.firstElement();
    }

    public void beginBuild() {
        parents.push(children.size());
    }

    public void leftRecursionInsert(int point) {
        parents.push(point);
    }

    public int getPoint() {
        return children.size();
    }

    public void endBuild(Node node) {
        Integer parent = parents.pop();
        while(children.size() > parent) {
            node.addChild(children.pop());
        }
        children.push(node);
    }

    public void buildTerminal(Word word, Token token) {
        children.push(new TerminalTkNode(SyntaxKind.TerminalTk,token,word));
    }
}
