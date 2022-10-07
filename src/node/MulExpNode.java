package node;

import error.ErrorRet;

public class MulExpNode extends Node{
    public MulExpNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        boolean flag = true;
        boolean canCal = false;
        String  op = "";
        if (getChildren().size() == 1) {
            ret = getChildren().get(0).check();
        } else {
            for (Node node:getChildren()) {
                ErrorRet tmp = node.check();
                ret.errorList.addAll(tmp.errorList);
                ret.dimension.addAll(tmp.dimension);
                if (flag) {
                    ret.value = tmp.value;
                    flag = false;
                }
                if (canCal) {
                    switch (op) {
                        case "*":
                            ret.value.set(0, ret.value.get(0) * tmp.value.get(0));
                            break;
                        case "/":
                            ret.value.set(0, ret.value.get(0) / tmp.value.get(0));
                            break;
                        case "%":
                            ret.value.set(0, ret.value.get(0) % tmp.value.get(0));
                            break;
                        default:
                            break;
                    }
                }
                if (node instanceof TerminalTkNode && symbol.isConst()) { // 一定可以计算出来
                    canCal =  true;
                    op = ((TerminalTkNode) node).getWord().getText();
                }
            }
        }
        return ret;
    }
}
