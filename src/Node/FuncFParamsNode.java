package Node;

public class FuncFParamsNode extends Node{
    public FuncFParamsNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
