package node;

import control_flow.quaternion.PrintInt;
import control_flow.quaternion.PrintStr;
import error.*;
import lexer.FormatStr;
import lexer.Token;

import java.util.ArrayList;

public class PrintStmtNode extends Node{

    public PrintStmtNode(SyntaxKind input) {
        super(input);
    }

    @Override
    public void print() {
        printChildren();
    }

    @Override
    public ErrorRet check() {
        ErrorRet ret = new ErrorRet();
        int cnt = 0;
        int numOfParam = 0;
        int line = 0;
        int strLine = 0;
        for (Node each: getChildren()) {
            ErrorRet  tmp = each.check();
            ret.errorList.addAll(tmp.errorList);
            if (each instanceof TerminalTkNode) {
                if (((TerminalTkNode) each).getTokenType().equals(Token.PRINTFTK)) {
                    line = ((TerminalTkNode) each).getWord().getLine();
                }
                if (((TerminalTkNode) each).getTokenType().equals(Token.COMMA)) {
                    cnt++;
                } else if (((TerminalTkNode) each).getTokenType().equals(Token.STRCON)) {
                    numOfParam =((FormatStr) ((TerminalTkNode) each).getWord()).getParam();
                    strLine = ((TerminalTkNode) each).getWord().getLine();
                    String str = ((FormatStr) ((TerminalTkNode) each).getWord()).getStr();
                    for (int i = 0;i < str.length();i++) {
                        char cha = str.charAt(i);
                        if (!isValidChar(cha,i+1 < str.length() ? str.charAt(i+1) : '%')) {
                            ret.errorList.add(new Pair<>(ErrorKind.Invalid_Character, strLine));
                            break;
                        }
                    }
                }
            }
        }
        if (numOfParam != cnt) {
            ret.errorList.add(new Pair<>(ErrorKind.Format_Params_Mismatch,line));
        }
        return ret;
    }

    private boolean isValidChar(char cha,char nxtCha) {
        int ascii = (int)cha;
        if ((ascii <= 126 && ascii >= 40 && ascii != 92) || ascii == 32 || ascii == 33) {
            return true;
        }
        if (nxtCha == 'n' && ascii == 92) {
            return true;
        }
        if (cha == '%' && nxtCha == 'd') {
            return true;
        }
        return false;
    }

    @Override
    public void buildIR(Context ctx, IRRet ret) {
        ArrayList<String> formatArgs = new ArrayList<>();
        String formatStr = " ";
        for (Node each:getChildren()) {
            if (each instanceof TerminalTkNode && ((TerminalTkNode) each) .getTokenType().equals(Token.STRCON)) {
                formatStr = ((FormatStr)((TerminalTkNode) each).getWord()).getStr();
            } else if (each instanceof ExpNode) {
                IRRet tmp = new IRRet();
                each.buildIR(ctx,tmp);
                formatArgs.add(tmp.ret);
            }
        }
        StringBuilder cursor = new StringBuilder();
        int j = 0;
        for (int i = 0;i < formatStr.length();i++) {
            char cha = formatStr.charAt(i);
            if (cha == '%' && formatStr.charAt(i+1) == 'd') {
                if (cursor.length() != 0) {
                    String label = controlFlowBuilder.getStrLabel();
                    symbol.addConstStr(label, cursor.toString());
                    cursor = new StringBuilder();
                    controlFlowBuilder.insertQuaternion(new PrintStr(label));
                }
                controlFlowBuilder.insertQuaternion(new PrintInt(formatArgs.get(j)));
                i++;
                j++;
            } else {
                cursor.append(cha);
            }
        }
        // 给 formatStr 收个尾
        if (cursor.length() != 0) {
            String label = controlFlowBuilder.getStrLabel();
            symbol.addConstStr(label, cursor.toString());
            controlFlowBuilder.insertQuaternion(new PrintStr(label));
        }
    }
}
