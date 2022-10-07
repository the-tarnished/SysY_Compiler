package node;

import error.ErrorKind;
import error.ErrorRet;
import error.FuncEntry;
import error.Pair;
import lexer.Token;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UnaryExpNode extends Node{
    public UnaryExpNode(SyntaxKind input) {
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
        String op = null;
        ArrayList<ArrayList<Integer>> paramDimension = new ArrayList<>();
        FuncEntry func = new FuncEntry();
        int line = 0;
        boolean old = false;
        switch (getChildren().get(0).getSyntaxKind()) {
            case TerminalTk: // 这算个鸡毛value,建议直接remake,不算了,返回有没有问题差不多得了
                boolean getParams = false;
                for (Node each:getChildren()) {
                    ErrorRet tmp = each.check();
                    ret.errorList.addAll(tmp.errorList);
                    if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.IDENFR)) {
                        func = symbol.getFunc(((TerminalTkNode) each).getWord().getText()); // 注意,这个函数叫小花,如果在RParam里面,那就不能是void(
                        old = symbol.isNeedParam();
                        line = ((TerminalTkNode) each).getWord().getLine();
                        if (func == null) {
                            ret.errorList.add(new Pair<>(ErrorKind.Undefined,((TerminalTkNode) each).getWord().getLine()));
                            getParams = true;
                            break;
                        }
                        if (func.isVoid() && symbol.isInRealParam() && symbol.isNeedParam()) {
                            ret.errorList.add(new Pair<>(ErrorKind.Params_Type_MisMatch,line));
                        }
                        symbol.setNeedParam(func.needParam());
                    } else if (each instanceof FuncRParamsNode) {
                        getParams = true;
                        paramDimension = tmp.paramDimension;
                        if (func.matchParams(paramDimension) == -1) {
                            ret.errorList.add(new Pair<>(ErrorKind.Params_Num_Mismatch,line));
                        } else if (func.matchParams(paramDimension) == -2) {
                            if (!ret.errorList.contains(new Pair<>(ErrorKind.Params_Type_MisMatch,line))) {
                                ret.errorList.add(new Pair<>(ErrorKind.Params_Type_MisMatch, line));
                            }
                        }
                    }
                }
                if (!getParams && func.needParam()) {
                    ret.errorList.add(new Pair<>(ErrorKind.Params_Num_Mismatch,line));
                }
                break;
            case PrimaryExp:
                ret = getChildren().get(0).check();
                break;
            case UnaryOp:  // 这能算一下value,判断一下const吧,可惜
                for (Node each:getChildren()) {
                    ErrorRet tmp = each.check();
                    if (op != null && symbol.isConst()) {
                        switch (op) {
                            case "+":
                                ret.value = tmp.value;
                                break;
                            case "-":
                                ret.value = (ArrayList<Integer>) tmp.value.stream().map(x -> x * -1).collect(Collectors.toList());
                                break;
                            default:
                                break;
                        }
                    }
                    if (each instanceof UnaryOpNode) {
                        op = tmp.str;
                    }
                }
                break;
        }
        symbol.setNeedParam(old);
        return ret;
    }
}
