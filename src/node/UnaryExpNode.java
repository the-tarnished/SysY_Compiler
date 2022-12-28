package node;

import control_flow.quaternion.*;
import error.*;
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

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        String op = " ";
        FuncEntry func = new FuncEntry();
        ArrayList<String> args = new ArrayList<>();
        String tmpVar = "";
        switch (getChildren().get(0).getSyntaxKind()) {
            case TerminalTk: // 函数调用啦
                for (Node each:getChildren()) {
                    IRRet tmp = new IRRet();
                    each.buildIR(ctx,tmp);
                    if (isIdent(each)) {
                        func = symbol.getFunc(((TerminalTkNode) each).getWord().getText());
                    }
                    if (each instanceof FuncRParamsNode) {
                        args = tmp.args;
                    }
                }
                // 压参数
                for (int i = 0;i < args.size();i++) {
                    controlFlowBuilder.insertQuaternion(new Push(args.get(i),i));
                }
                // 函数调用
                String funcName = controlFlowBuilder.getFuncName(func.getName());
                controlFlowBuilder.insertQuaternion(new Call(funcName));
                // 获得返回值
                controlFlowBuilder.insertBasicBlock(controlFlowBuilder.getNewBasicBlock());
                if (func.isVoid()) {
                    return;
                }
                tmpVar = controlFlowBuilder.getTmpVar();
                controlFlowBuilder.insertQuaternion(new GetReturn(tmpVar));
                ret.ret = tmpVar;
                break;
            case PrimaryExp:
                getChildren().get(0).buildIR(ctx,ret);
                break;
            case UnaryOp:
                for (Node each:getChildren()) {
                    IRRet tmp = new IRRet();
                    each.buildIR(ctx,tmp);
                    if (each instanceof UnaryOpNode) {
                        op = tmp.ret;
                        continue;
                    }
                    if (isDigit(tmp.ret)) {
                        int value = 0;
                        if (op.equals("+") | op.equals("-")) {
                            value = Integer.parseInt(tmp.ret) * Integer.parseInt(op + "1");
                        } else {
                            value = Integer.parseInt(tmp.ret) == 0?1:0;
                        }
                        ret.ret = String.valueOf(value);
                    } else {
                        tmpVar = null;
                        switch (op) {
                            case "-":
                                tmpVar = controlFlowBuilder.getTmpVar();
                                controlFlowBuilder.insertQuaternion(new Neg(tmpVar,tmp.ret));
                                break;
                            case "!":
                                tmpVar = controlFlowBuilder.getTmpVar();
                                controlFlowBuilder.insertQuaternion(new Not(tmpVar,tmp.ret));
                                break;
                            case "+":
                                tmpVar = tmp.ret;
                               break;
                        }
                        ret.ret = tmpVar;
                    }
                }
                break;
        }
    }
}
