package node;

import error.ErrorKind;
import error.ErrorRet;
import error.Pair;
import lexer.FormatStr;
import lexer.Token;

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
}
