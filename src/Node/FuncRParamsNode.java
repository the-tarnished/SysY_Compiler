package Node;

public class FuncRParamsNode extends Node{

    public FuncRParamsNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
