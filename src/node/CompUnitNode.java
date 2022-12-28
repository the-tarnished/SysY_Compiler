package node;

import error.Context;
import error.ErrorRet;
import error.IRRet;
import error.Symbol;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;

import static control_flow.ControlFlowBuilder.IR;

public class CompUnitNode extends Node{

    public CompUnitNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
        printSyntaxKind();
    }

    public ErrorRet check(){
        symbol.startBlock();
        ErrorRet ret = new ErrorRet();
        for (Node i : getChildren()) {
            ret.errorList.addAll(i.check().errorList);
        }
        symbol.endBlock();
        Symbol.clear();
        symbol = Symbol.getInstance();
        return ret;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret){
        symbol.startBlock();
        ctx.isGlobal = true;
        for (Node each:getChildren()) {
            if (each instanceof MainFuncDefNode || each instanceof FuncDefNode) {
                ctx.isGlobal = false;
            } else {
                ctx.isGlobal = true;
            }
            each.buildIR(ctx,ret);
        }
        if (IR) {
            try {
                PrintStream print = new PrintStream("ir.txt"); //写好输出位置文件；
                System.setOut(print);
                symbol.printGlobalVar();
                System.out.println();
                controlFlowBuilder.print();
            } catch (IOException err) {}
        } else {
            try {
                PrintStream print = new PrintStream("mips.txt"); //写好输出位置文件；
                System.setOut(print);
                System.out.println(".data");
                symbol.printGlobalVar();
                System.out.println(".text");
                System.out.println("jal func_main");
                System.out.println("li $v0 10");
                System.out.println("syscall");
                controlFlowBuilder.assembly();
            } catch (IOException err) {}
        }
    }
}
