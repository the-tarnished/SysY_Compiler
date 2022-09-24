package Parser;

import Lexer.*;
import Node.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Parser {
    private final Words words;
    private final Builder builder;

    public Parser(Lexer lexer) {
        this.words = new Words(lexer);
        builder = new Builder();
    }

    public void run() {
        compUnit();
    }

    public void print() {
        builder.getRoot().print();
    }

    private void compUnit() {
        builder.beginBuild();
        CompUnitNode node = new CompUnitNode(SyntaxKind.CompUnit);
        while(words.hasNext()) {
            switch (words.getNextKind()) {
                case CONSTTK:
                    decl();
                    break;
                case VOIDTK:
                    funcDef();
                    break;
                case INTTK:
                    if (words.getNthKind(1).equals(Token.MAINTK)) { // int main
                        mainFuncDef();
                    } else if (words.getNthKind(1).equals(Token.IDENFR)) {
                        if (words.getNthKind(2).equals(Token.LPARENT)) { // int ident () -> func
                            funcDef();
                        } else { // int ident -> decl
                            decl();
                        }
                    } else {
                        break; // todo error handler
                    }
                    break;
                default: //todo error handler
                    break;
            }
        }
        builder.endBuild(node);
    }

    private void decl() {
        builder.beginBuild();
        DeclNode node = new DeclNode(SyntaxKind.Decl);
        switch (words.getNextKind()) {
            case CONSTTK:
                constDecl();
                break;
            case INTTK:
                varDecl();
                break;
            default: // todo error handler
                break;
        }
        builder.endBuild(node);
    }

    private void constDecl() {
        builder.beginBuild();
        ConstDeclNode node = new ConstDeclNode(SyntaxKind.ConstDecl);
        terminalTk(Token.CONSTTK); // eat const
        BType();
        do {
            if (words.getNextKind().equals(Token.COMMA)) {
                terminalTk(Token.COMMA);
            }
            constDef();
        } while(words.getNextKind().equals(Token.COMMA));
        terminalTk(Token.SEMICN); // eat ;
        builder.endBuild(node);
    }

    private void BType() {
        builder.beginBuild();
        BTypeNode node = new BTypeNode(SyntaxKind.BType);
        terminalTk(Token.INTTK); // eat int
        builder.endBuild(node);
    }

    private void constDef() {
        builder.beginBuild();
        Node node = new ConstDefNode(SyntaxKind.ConstDef);
        terminalTk(Token.IDENFR);
        while (words.getNextKind().equals(Token.LBRACK)) {
            terminalTk(Token.LBRACK);
            constExp();
            terminalTk(Token.RBRACK);
        }
        terminalTk(Token.ASSIGN);
        constInitVal();
        builder.endBuild(node);
    }

    private void constInitVal() {
        builder.beginBuild();
        Node node = new ConstInitValNode(SyntaxKind.ConstInitVal);
        if (words.getNextKind() == Token.LBRACE) {
            terminalTk(Token.LBRACE);
            do {
                if (words.getNextKind().equals(Token.COMMA)) {
                    terminalTk(Token.COMMA);
                }
                constInitVal();
            } while (words.getNextKind().equals(Token.COMMA));
            terminalTk(Token.RBRACE);
        } else {
            constExp();
        }
        builder.endBuild(node);
    }

    private void varDecl() {
        builder.beginBuild();
        Node node = new VarDeclNode(SyntaxKind.VarDecl);
        BType();
        do {
            if (words.getNextKind().equals(Token.COMMA)) {
                terminalTk(Token.COMMA);
            }
            varDef();
        }while (words.getNextKind().equals(Token.COMMA));
        terminalTk(Token.SEMICN);
        builder.endBuild(node);
    }

    private void varDef() {
        builder.beginBuild();
        Node node = new VarDefNode(SyntaxKind.VarDef);
        terminalTk(Token.IDENFR);
        while (words.getNextKind().equals(Token.LBRACK)) {
            terminalTk(Token.LBRACK);
            constExp();
            terminalTk(Token.RBRACK);
        }
        if (words.getNextKind().equals(Token.ASSIGN)) {
            terminalTk(Token.ASSIGN);
            initVal();
        }
        builder.endBuild(node);
    }

    private void initVal() {
        builder.beginBuild();
        Node node = new InitValNode(SyntaxKind.InitVal);
        if (words.getNextKind().equals(Token.LBRACE)) {
            terminalTk(Token.LBRACE);
            do {
                if (words.getNextKind().equals(Token.COMMA)) {
                    terminalTk(Token.COMMA);
                }
                initVal();
            } while (words.getNextKind().equals(Token.COMMA));
            terminalTk(Token.RBRACE);
        } else {
            exp();
        }
        builder.endBuild(node);
    }

    private void funcDef() {
        builder.beginBuild();
        Node node = new FuncDefNode(SyntaxKind.FuncDef);
        funcType();
        terminalTk(Token.IDENFR);
        terminalTk(Token.LPARENT);
        if (!words.getNextKind().equals(Token.RPARENT)) {
            funcFParams();
        }
        terminalTk(Token.RPARENT);
        block();
        builder.endBuild(node);
    }

    private void mainFuncDef() {
        builder.beginBuild();
        Node node = new MainFuncDefNode(SyntaxKind.MainFuncDef);
        terminalTk(Token.INTTK);
        terminalTk(Token.MAINTK);
        terminalTk(Token.LPARENT);
        terminalTk(Token.RPARENT);
        block();
        builder.endBuild(node);
    }

    private void funcType() {
        builder.beginBuild();
        Node node = new FuncTypeNode(SyntaxKind.FuncType);
        if (words.getNextKind().equals(Token.INTTK)) {
            terminalTk(Token.INTTK);
        } else if (words.getNextKind().equals(Token.VOIDTK)) {
            terminalTk(Token.VOIDTK);
        }
        builder.endBuild(node);
    }

    private void funcFParams() {
        builder.beginBuild();
        Node node = new FuncFParamsNode(SyntaxKind.FuncFParams);
        do {
            if (words.getNextKind().equals(Token.COMMA)) {
                terminalTk(Token.COMMA);
            }
            funcFParam();
        } while (words.getNextKind().equals(Token.COMMA));
        builder.endBuild(node);
    }

    private void funcFParam() {
        builder.beginBuild();
        Node node = new FuncFParamNode(SyntaxKind.FuncFParam);
        BType();
        terminalTk(Token.IDENFR);
        if (words.getNextKind().equals(Token.LBRACK)) {
            terminalTk(Token.LBRACK);
            terminalTk(Token.RBRACK);
            if (words.getNextKind().equals(Token.LBRACK)) {
                terminalTk(Token.LBRACK);
                constExp();
                terminalTk(Token.RBRACK);
            }
        }
        builder.endBuild(node);
    }

    private void block() {
        builder.beginBuild();
        Node node = new BlockNode(SyntaxKind.Block);
        terminalTk(Token.LBRACE);
        do {
            blockItem();
        } while (!words.getNextKind().equals(Token.RBRACE));
        terminalTk(Token.RBRACE);
        builder.endBuild(node);
    }

    private void blockItem() {
        builder.beginBuild();
        Node node = new BlockItemNode(SyntaxKind.BlockItem);
        if (words.getNextKind().equals(Token.INTTK) || words.getNextKind().equals(Token.CONSTTK)) {
            decl();
        } else if (!words.getNextKind().equals(Token.RBRACE)) {
            stmt();
        }
        builder.endBuild(node);
    }

    private void stmt() {
        builder.beginBuild();
        Node node = new StmtNode(SyntaxKind.Stmt);
        switch (words.getNextKind()) {
            case IDENFR:  //assignStmt or getint or exp
                boolean isGetInt = false;
                boolean isAssign = false;
                for (int i = 1;;i++) {
                    Token kind = words.getNthKind(i);
                    if (kind.equals(Token.GETINTTK)) {
                        isGetInt = true;
                        break;
                    } else if (kind.equals(Token.ASSIGN)) {
                        isAssign = true;
                    } else if (kind.equals(Token.SEMICN)) {
                        break;
                    }
                }
                if (isGetInt) {
                    getIntStmt();
                } else if (isAssign) {
                    assignStmt();
                } else {
                    exp();
                    terminalTk(Token.SEMICN);
                }
                break;
            case IFTK:
                ifStmt();
                break;
            case WHILETK:
                whileStmt();
                break;
            case BREAKTK:
                breakStmt();
                break;
            case CONTINUETK:
                continueStmt();
                break;
            case RETURNTK:
                returnStmt();
                break;
            case PRINTFTK:
                printStmt();
                break;
            case LBRACE:
                block();
                break;
            default:
                if (!words.getNextKind().equals(Token.SEMICN)) {
                    exp();
                }
                terminalTk(Token.SEMICN);
        }
        builder.endBuild(node);
    }

    private void assignStmt() {
        builder.beginBuild();
        Node node = new AssignStmtNode(SyntaxKind.AssignStmt);
        lVal();
        terminalTk(Token.ASSIGN);
        exp();
        terminalTk(Token.SEMICN);
        builder.endBuild(node);
    }

    private void ifStmt() {
        builder.beginBuild();
        Node node = new IfStmtNode(SyntaxKind.IfStmt);
        terminalTk(Token.IFTK);
        terminalTk(Token.LPARENT);
        cond();
        terminalTk(Token.RPARENT);
        stmt();
        if (words.getNextKind().equals(Token.ELSETK)) {
            terminalTk(Token.ELSETK);
            stmt();
        }
        builder.endBuild(node);
    }

    private void whileStmt() {
        builder.beginBuild();
        Node node = new WhileStmtNode(SyntaxKind.WhileStmt);
        terminalTk(Token.WHILETK);
        terminalTk(Token.LPARENT);
        cond();
        terminalTk(Token.RPARENT);
        stmt();
        builder.endBuild(node);
    }

    private void breakStmt() {
        builder.beginBuild();
        Node node = new BreakStmtNode(SyntaxKind.BreakStmt);
        terminalTk(Token.BREAKTK);
        terminalTk(Token.SEMICN);
        builder.endBuild(node);
    }

    private void continueStmt() {
        builder.beginBuild();
        Node node = new ContinueStmtNode(SyntaxKind.ContinueStmt);
        terminalTk(Token.CONTINUETK);
        terminalTk(Token.SEMICN);
        builder.endBuild(node);
    }

    private void returnStmt() {
        builder.beginBuild();
        Node node = new ReturnStmtNode(SyntaxKind.ReturnStmt);
        terminalTk(Token.RETURNTK);
        if (!words.getNextKind().equals(Token.SEMICN)) {
            exp();
        }
        terminalTk(Token.SEMICN);
        builder.endBuild(node);
    }

    private void getIntStmt() {
        builder.beginBuild();
        Node node = new GetIntStmtNode(SyntaxKind.GetIntStmt);
        lVal();
        terminalTk(Token.ASSIGN);
        terminalTk(Token.GETINTTK);
        terminalTk(Token.LPARENT);
        terminalTk(Token.RPARENT);
        terminalTk(Token.SEMICN);
        builder.endBuild(node);
    }

    private void printStmt() {
        builder.beginBuild();
        Node node = new PrintStmtNode(SyntaxKind.PrintStmt);
        terminalTk(Token.PRINTFTK);
        terminalTk(Token.LPARENT);
        terminalTk(Token.STRCON);
        while (words.getNextKind().equals(Token.COMMA)) {
            terminalTk(Token.COMMA);
            exp();
        }
        terminalTk(Token.RPARENT);
        terminalTk(Token.SEMICN);
        builder.endBuild(node);
    }

    private void exp() {
        builder.beginBuild();
        Node node = new ExpNode(SyntaxKind.Exp);
        addExp();
        builder.endBuild(node);
    }

    private void cond() {
        builder.beginBuild();
        Node node = new CondNode(SyntaxKind.Cond);
        lOrExp();
        builder.endBuild(node);
    }

    private void lVal() {
        builder.beginBuild();
        Node node = new LValNode(SyntaxKind.LVal);
        terminalTk(Token.IDENFR);
        while (words.getNextKind().equals(Token.LBRACK)) {
            terminalTk(Token.LBRACK);
            exp();
            terminalTk(Token.RBRACK);
        }
        builder.endBuild(node);
    }

    private void primaryExp() {
        builder.beginBuild();
        Node node = new PrimaryExpNode(SyntaxKind.PrimaryExp);
        switch (words.getNextKind()) {
            case IDENFR:
                lVal();
                break;
            case LPARENT:
                terminalTk(Token.LPARENT);
                exp();
                terminalTk(Token.RPARENT);
                break;
            case INTCON:
                number();
                break;
        }
        builder.endBuild(node);
    }

    private void number() {
        builder.beginBuild();
        Node node = new NumberNode(SyntaxKind.Number);
        terminalTk(Token.INTCON);
        builder.endBuild(node);
    }

    private void unaryExp() {
        builder.beginBuild();
        Node node = new UnaryExpNode(SyntaxKind.UnaryExp);
        switch (words.getNextKind()) {
            case IDENFR:
                if (words.getNthKind(1).equals(Token.LPARENT)) {
                    terminalTk(Token.IDENFR);
                    terminalTk(Token.LPARENT);
                    if (!words.getNextKind().equals(Token.RPARENT)) {
                        funcRParams();
                    }
                    terminalTk(Token.RPARENT);
                } else {
                    primaryExp();
                }
                break;
            case PLUS:
            case MINU:
            case NOT:
                unaryOp();
                unaryExp();
                break;
            default:
                primaryExp();
                break;
        }
        builder.endBuild(node);
    }

    private void unaryOp() {
        builder.beginBuild();
        Node node = new UnaryOpNode(SyntaxKind.UnaryOp);
        switch (words.getNextKind()) {
            case PLUS:
                terminalTk(Token.PLUS);
                break;
            case MINU:
                terminalTk(Token.MINU);
                break;
            case NOT:
                terminalTk(Token.NOT);
                break;
        }
        builder.endBuild(node);
    }

    private void funcRParams() {
        builder.beginBuild();
        Node node = new FuncRParamsNode(SyntaxKind.FuncRParams);
        do {
            if (words.getNextKind().equals(Token.COMMA)) {
                terminalTk(Token.COMMA);
            }
            exp();
        } while (words.getNextKind().equals(Token.COMMA));
        builder.endBuild(node);
    }

    private void mulExp() {
        builder.beginBuild();
        Node node = new MulExpNode(SyntaxKind.MulExp);
        ArrayList<Token> list = new ArrayList<>(Arrays.asList(Token.MULT, Token.DIV, Token.MOD));
        int point = builder.getPoint();
        do {
            if (list.contains(words.getNextKind())) {
                builder.leftRecursionInsert(point);
                Node tmp= new MulExpNode(SyntaxKind.MulExp);
                builder.endBuild(tmp);
                terminalTk(words.getNextKind());
            }
            unaryExp();
        } while (list.contains(words.getNextKind()));
        builder.endBuild(node);
    }

    private void addExp() {
        builder.beginBuild();
        Node node = new AddExpNode(SyntaxKind.AddExp);
        boolean hasLeftRecur = false;
        ArrayList<Token> list = new ArrayList<>(Arrays.asList(Token.PLUS, Token.MINU));
        int point = builder.getPoint();
        do {
            if (list.contains(words.getNextKind()) && hasLeftRecur) {
                builder.leftRecursionInsert(point);
                Node tmp= new AddExpNode(SyntaxKind.AddExp);
                builder.endBuild(tmp);
                terminalTk(words.getNextKind());
            }
            mulExp();
            hasLeftRecur = true;
        } while (list.contains(words.getNextKind()));
        builder.endBuild(node);
    }

    private void relExp() {
        builder.beginBuild();
        Node node = new RelExpNode(SyntaxKind.RelExp);
        ArrayList<Token> list = new ArrayList<>(Arrays.asList(Token.GRE, Token.LSS, Token.GEQ, Token.LEQ));
        int point = builder.getPoint();
        do {
            if (list.contains(words.getNextKind())) {
                builder.leftRecursionInsert(point);
                Node tmp= new RelExpNode(SyntaxKind.RelExp);
                builder.endBuild(tmp);
                terminalTk(words.getNextKind());
            }
            addExp();
        } while (list.contains(words.getNextKind()));
        builder.endBuild(node);
    }

    private void eqExp() {
        builder.beginBuild();
        Node node = new EqExpNode(SyntaxKind.EqExp);
        ArrayList<Token> list = new ArrayList<>(Arrays.asList(Token.EQL, Token.NEQ));
        int point = builder.getPoint();
        do {
            if (list.contains(words.getNextKind())) {
                builder.leftRecursionInsert(point);
                Node tmp= new EqExpNode(SyntaxKind.EqExp);
                builder.endBuild(tmp);
                terminalTk(words.getNextKind());
            }
            relExp();
        } while (list.contains(words.getNextKind()));
        builder.endBuild(node);
    }

    private void lAndExp() {
        builder.beginBuild();
        Node node = new LAndExpNode(SyntaxKind.LAndExp);
        ArrayList<Token> list = new ArrayList<>(Collections.singletonList(Token.AND));
        int point = builder.getPoint();
        do {
            if (list.contains(words.getNextKind())) {
                builder.leftRecursionInsert(point);
                Node tmp= new LAndExpNode(SyntaxKind.LAndExp);
                builder.endBuild(tmp);
                terminalTk(words.getNextKind());
            }
            eqExp();
        } while (list.contains(words.getNextKind()));
        builder.endBuild(node);
    }

    private void lOrExp() {
        builder.beginBuild();
        Node node = new LOrExpNode(SyntaxKind.LOrExp);
        ArrayList<Token> list = new ArrayList<>(Collections.singletonList(Token.OR));
        int point = builder.getPoint();
        do {
            if (list.contains(words.getNextKind())) {
                builder.leftRecursionInsert(point);
                Node tmp= new LOrExpNode(SyntaxKind.LOrExp);
                builder.endBuild(tmp);
                terminalTk(words.getNextKind());
            }
            lAndExp();
        } while (list.contains(words.getNextKind()));
        builder.endBuild(node);
    }

    private void constExp() {
        builder.beginBuild();
        Node node = new ConstExpNode(SyntaxKind.ConstExp);
        addExp();
        builder.endBuild(node);
    }

    private void terminalTk(Token token) {
        if (token.equals(words.getNextKind())) {
            builder.buildTerminal(words.getNextWord(), words.getNextKind());
            words.next();
        }  else {
            System.out.println("Error Token");
            // todo error handler
        }
    }

}
