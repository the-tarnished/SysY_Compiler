package Node;

public class MainFuncDefNode extends Node{
    public MainFuncDefNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }
}
