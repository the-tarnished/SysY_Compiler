package Node;

import Lexer.Token;
import Lexer.Word;

import java.util.ArrayList;

public abstract class Node {

    private final ArrayList<Node> children;
    private final SyntaxKind syntaxKind;

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
}
