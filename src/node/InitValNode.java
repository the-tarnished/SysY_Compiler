package node;

import error.Context;
import error.IRRet;

import java.util.ArrayList;

public class InitValNode extends Node{
    public InitValNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        ArrayList<String> init = new ArrayList<>();
        for (Node each:getChildren()) {
            if (each instanceof ExpNode) {
                IRRet tmp = new IRRet();
                each.buildIR(ctx,tmp);
                init.add(tmp.ret);
            } else if (each instanceof InitValNode) {
                IRRet tmp = new IRRet();
                each.buildIR(ctx,tmp);
                init.addAll(tmp.initVal);
            }
        }
        ret.initVal.addAll(init);
    }
}
