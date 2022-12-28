package node;

import control_flow.ControlFlowBuilder;
import control_flow.quaternion.Alloc;
import control_flow.quaternion.Assign;
import control_flow.quaternion.Store;
import error.*;
import lexer.Token;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class VarDefNode extends Node{
    public VarDefNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        String name = null;
        int line = 0;
        ArrayList<Integer> dimension = new ArrayList<>();
        for (Node each:getChildren()) {
            ErrorRet tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof TerminalTkNode) {
                if (((TerminalTkNode) each).getTokenType() == Token.IDENFR) {
                    name = ((TerminalTkNode) each).getWord().getText();
                    line = ((TerminalTkNode) each).getWord().getLine();
                }
            }
            if (each instanceof ConstExpNode) {
                dimension.addAll(tmp.value);
            }
        }
        if (symbol.addVar(symbol.isConst(), name,dimension) == 0) {
            ret.errorList.add(new Pair<>(ErrorKind.Redefined, line));
        }
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        String name = null;
        ArrayList<Integer> dimension = new ArrayList<>();
        ArrayList<Integer> initVal = new ArrayList<>();
        ArrayList<String> init = new ArrayList<>();
        for (Node each:getChildren()) {
            IRRet tmp = new IRRet();
            each.buildIR(ctx,tmp);
            if (each instanceof TerminalTkNode) {
                if (((TerminalTkNode) each).getTokenType() == Token.IDENFR) {
                    name = ((TerminalTkNode) each).getWord().getText();
                }
            }
            if (each instanceof ConstExpNode) {
                dimension.add(Integer.parseInt(tmp.ret));
            } else if (each instanceof InitValNode) {
                if (ctx.isGlobal) {
                    initVal.addAll(tmp.initVal.stream().map(Integer::parseInt).collect(Collectors.toList()));
                } else {
                    init = tmp.initVal;
                }
            }
        }
        int id = symbol.addVar(false,name,dimension,initVal);
        if (ctx.isGlobal) { // 这个,啊,是一个全局变量,所以,阿,丢给汇编苦恼捏
            return;
        }
        if (dimension.size() == 0) {// 变量声明
            String varName = ControlFlowBuilder.getVarName(name,id);
            if (init.size() == 0) {
                controlFlowBuilder.insertQuaternion(new Assign(varName,"20373057"));
            } else {
                controlFlowBuilder.insertQuaternion(new Assign(varName,init.get(0)));
            }
        } else { // 数组声明
            String pointer = ControlFlowBuilder.getVarName(name,id);
            AtomicInteger size = new AtomicInteger(1);
            dimension.forEach(tmp -> size.updateAndGet(v -> v * tmp));
            controlFlowBuilder.insertQuaternion(new Alloc(ControlFlowBuilder.getVarName(name,id),String.valueOf(size)));
            if (init.size() != 0) {
                for (int i = 0;i<init.size();i++) {
                    controlFlowBuilder.insertQuaternion(new Store(init.get(i),pointer,String.valueOf(i)));
                }
            }
        }
    }
}
