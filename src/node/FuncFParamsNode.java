package node;

import error.Context;
import error.ErrorRet;
import error.IRRet;

import java.util.ArrayList;

public class FuncFParamsNode extends Node{
    public FuncFParamsNode(SyntaxKind input) {
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
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (!(each instanceof TerminalTkNode)) {
                ret.paramDimension.add(tmp.dimension);
            }
        }
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        ArrayList<String> args = new ArrayList<>();
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof FuncFParamNode) {
                args.add(tmp.ret);
            }
        }
        ret.args = args; // 为什么这里不给 load 呢,因为环境切换是在 funcDef做的(毕竟需要func的名字咩,func的声明又需要参数个数咩,所以这个结束了,func才能声明,才能load咩)
    }
}
