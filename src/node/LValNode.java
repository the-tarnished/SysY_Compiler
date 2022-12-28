package node;

import control_flow.ControlFlowBuilder;
import control_flow.quaternion.Load;
import control_flow.quaternion.LoadPtr;
import error.*;
import lexer.Token;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class LValNode extends Node{

    public LValNode(SyntaxKind input) {
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
        ArrayList<Integer> dimension;
        ArrayList<Integer> value;
        VarEntry var = null;
        boolean flag = false;
        int cnt = 0;
        ArrayList<Integer> index = new ArrayList<>();
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof TerminalTkNode && ((TerminalTkNode) each).getTokenType().equals(Token.IDENFR)) {
                var = symbol.getVar(((TerminalTkNode) each).getWord().getText());
                if (var == null) {
                    ret.errorList.add(new Pair<>(ErrorKind.Undefined,((TerminalTkNode) each).getWord().getLine()));
                } else if (var.isConst() && symbol.isLVal()) {
                    ret.errorList.add(new Pair<>(ErrorKind.Modify_Const_Error,((TerminalTkNode) each).getWord().getLine()));
                }
                symbol.setLVal(false);
                flag = var != null && var.isConst() && symbol.isConst();// 可以求出value
            } else if (!(each instanceof TerminalTkNode || each instanceof ErrorNode)) { // 是 exp
                // 求dimension
                cnt++;
                index.addAll(tmp.value);
            }
        }
        if (flag) {
            ret.value.add(var.getValue(index));
        }

        ret.dimension.addAll(var != null ? var.getDimension().subList(cnt,var.getDimension().size()) : new ArrayList<>());
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        boolean isLVal = ctx.isLVal;
        String name = " ";
        VarEntry varEntry = null;
        ArrayList<String> offset = new ArrayList<>();
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (isIdent(each)) {
                name = tmp.ret;
                varEntry = symbol.getVar(name);
                ctx.isLVal = false; // 防止exp求值的时候给了lval
            } else if (each instanceof ExpNode) {
                offset.add(tmp.ret);
            }
        }
        // 是左值,返回应该是一个变量名字,或者pointer,如果是pointer记得传isArray,让上面的xd往地址写结果
        // 算个offset
        if (isLVal) {
            String fixOffset = varEntry.calOffset(offset);
            ret.ret = ControlFlowBuilder.getVarName(name, varEntry.getId());
            ret.isArray = varEntry.getDimension().size() != 0 || varEntry.isGlobal();
            ret.offset = fixOffset;
            return;
        }
        // 求值 这个时候不能是左值
        boolean canCal = offset.stream().allMatch(Node::isDigit);
        // 如果能求出值,则ret就是值,how to do it?
        // var是一个常量或者全局变量,则结果是已知的,并且offset都是数字,可求出来的
        // 当前上下文在全局变量中,这个时候都是可求得,但是貌似如果是全局,那么这个变量一定是全局,并且offset都可求
        // 所以上下文是全局变量是可求情况的子集麻?
        // 意识到全局变量可能会被改变,所以应该是常量或者全局情况中
        if (canCal && (varEntry.isConst() || ctx.isGlobal)) {
            ret.ret = String.valueOf(varEntry.getValue(offset.stream().map(Integer::parseInt).collect(ArrayList::new,
                    ArrayList::add,
                    ArrayList::addAll)));
        } else { // 嗨呀求不出值了,怎么办捏,如果是变量就变量名给出去就行,如果不是的话,哈哈,传指针吧,略
            if (varEntry.getDimension().size() == 0 && !varEntry.isGlobal()) {// 变量捏
                ret.ret = ControlFlowBuilder.getVarName(name, varEntry.getId());
            } else {
                String fixOffset = varEntry.calOffset(offset);
                ret.ret = controlFlowBuilder.getTmpVar();
                String varName = ControlFlowBuilder.getVarName(name, varEntry.getId());
                if (offset.size() == varEntry.getDimension().size() ) {
                    controlFlowBuilder.insertQuaternion(new Load(ret.ret,varName,fixOffset));
                } else {
                    controlFlowBuilder.insertQuaternion(new LoadPtr(ret.ret,varName,fixOffset));
                }
            }
        }
    }
}
