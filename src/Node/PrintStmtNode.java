package Node;

public class PrintStmtNode extends Node{

    public PrintStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
