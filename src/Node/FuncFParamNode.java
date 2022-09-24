package Node;

public class FuncFParamNode extends Node {

    public FuncFParamNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
