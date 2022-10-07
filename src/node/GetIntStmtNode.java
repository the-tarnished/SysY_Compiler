package node;

public class GetIntStmtNode extends Node{
    public GetIntStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }
}
