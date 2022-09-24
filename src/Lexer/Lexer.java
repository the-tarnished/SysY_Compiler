package Lexer;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class Lexer {
    private final Source source;
    private final ArrayList<String> words;
    private final HashMap<String, Token> word2Token;
    private int line;

    public Lexer (File file) {
        this.source = new Source(file);
        this.words = new ArrayList<>();
        this.word2Token = new HashMap<>();
        word2Token.put("main",Token.MAINTK);
        word2Token.put("const",Token.CONSTTK);
        word2Token.put("int",Token.INTTK);
        word2Token.put("break",Token.BREAKTK);
        word2Token.put("continue",Token.CONTINUETK);
        word2Token.put("if",Token.IFTK);
        word2Token.put("else",Token.ELSETK);
        word2Token.put("!",Token.NOT);
        word2Token.put("&&",Token.AND);
        word2Token.put("||",Token.OR);
        word2Token.put("while",Token.WHILETK);
        word2Token.put("getint",Token.GETINTTK);
        word2Token.put("printf",Token.PRINTFTK);
        word2Token.put("return",Token.RETURNTK);
        word2Token.put("+",Token.PLUS);
        word2Token.put("-",Token.MINU);
        word2Token.put("void",Token.VOIDTK);
        word2Token.put("*",Token.MULT);
        word2Token.put("/",Token.DIV);
        word2Token.put("%",Token.MOD);
        word2Token.put("<",Token.LSS);
        word2Token.put("<=",Token.LEQ);
        word2Token.put(">",Token.GRE);
        word2Token.put(">=",Token.GEQ);
        word2Token.put("==",Token.EQL);
        word2Token.put("!=",Token.NEQ);
        word2Token.put("=",Token.ASSIGN);
        word2Token.put(";",Token.SEMICN);
        word2Token.put(",",Token.COMMA);
        word2Token.put("(",Token.LPARENT);
        word2Token.put(")",Token.RPARENT);
        word2Token.put("[",Token.LBRACK);
        word2Token.put("]",Token.RBRACK);
        word2Token.put("{",Token.LBRACE);
        word2Token.put("}",Token.RBRACE);
        line = 1;
    }

    public void run() {
        while (source.hasNext()) {
            char cursor = source.getNextChar();
            switch (cursor) {
                case '/':
                    if (source.getNthChar(1) == '/') {
                        this.eatLineComment();
                    } else if (source.getNthChar(1) == '*') {
                        this.eatBlockComment();
                    } else {
                        words.add(String.valueOf(cursor));
                        source.next();
                    }
                    break;
                case ',':
                case '+':
                case '-':
                case '*':
                case '%':
                case '{':
                case '}':
                case '[':
                case ']':
                case '(':
                case ')':
                case ';':
                    source.next();
                    words.add(String.valueOf(cursor));
                    break;
                case '!' :
                    if (source.getNthChar(1) == '=') {
                        words.add("!=");
                        source.next();
                    } else {
                        words.add("!");
                    }
                    source.next();
                    break;
                case '=':
                    if (source.getNthChar(1) == '=') {
                        words.add("==");
                        source.next();
                    } else {
                        words.add("=");
                    }
                    source.next();
                    break;
                case '<':
                    if (source.getNthChar(1) == '=') {
                        words.add("<=");
                        source.next();
                    } else {
                        words.add("<");
                    }
                    source.next();
                    break;
                case '>':
                    if (source.getNthChar(1) == '=') {
                        words.add(">=");
                        source.next();
                    } else {
                        words.add(">");
                    }
                    source.next();
                    break;
                case '&':
                    if (source.getNthChar(1) == '&') {
                        words.add("&&");
                        source.next();
                    }
                    source.next();
                    //todo 错误处理
                    break;
                case '|':
                    if (source.getNthChar(1) == '|') {
                        words.add("||");
                        source.next();
                    }
                    source.next();
                    //todo 错误处理
                    break;
                case '"':
                    String getString = eatString();
                    addToken(getString,Token.STRCON);
                    words.add(getString);
                    break;
                default:
                    StringBuilder sb = new StringBuilder();
                    sb.append(cursor);
                    Token tokenType;
                    source.next();
                    if (Character.isDigit(cursor)) {
                        tokenType = Token.INTCON;
                        while (source.hasNext() && Character.isDigit(source.getNextChar())) {
                            sb.append(source.getNextChar());
                            source.next();
                        }
                        addToken(sb.toString(),tokenType);
                        words.add(sb.toString());
                    } else if (isIdentBegin(cursor)){
                        tokenType = Token.IDENFR;
                        while (source.hasNext() && isIdentContinue(source.getNextChar())) {
                            sb.append(source.getNextChar());
                            source.next();
                        }
                        addToken(sb.toString(),tokenType);
                        words.add(sb.toString());
                    }
                    // todo 错误处理
                    break;
            }
        }
    }

    public static boolean isAlpha(char c) {
        return Character.isLowerCase(c) || Character.isUpperCase(c);
    }

    public static boolean isIdentBegin(char c) {
        return isAlpha(c) || c == '_';
    }

    public static boolean isIdentContinue(char c) {
        return isIdentBegin(c) || Character.isDigit(c);
    }

    public void eatLineComment() {
        line++;
        while(source.hasNext() && source.getNextChar() != '\n') {
            source.next();
        }
        source.next();
    }

    public void eatBlockComment() {
        while (source.hasNext() && !(source.getNextChar() == '*' && source.getNthChar(1) == '/')) {
            if(source.getNextChar() == '\n') {
                line++;
            }
            source.next();
        }
        source.next();
        source.next();
    }

    public ArrayList<String> getWords() {
        return words;
    }

    public HashMap<String, Token> getWord2Token() {
        return word2Token;
    }

    public String eatString() {
        StringBuilder sb = new StringBuilder();
        do {
            sb.append(source.getNextChar());
            source.next();
        } while(source.hasNext() && source.getNextChar() != '"');
        sb.append(source.getNextChar());
        source.next();
        return sb.toString();
    }

    public void addToken(String token,Token kind) {
        if (!word2Token.containsKey(token)) {
            word2Token.put(token,kind);
        }
        //System.out.println(token);
    }

    public void printLexer() {
        for (String word: words) {
            System.out.println(word2Token.get(word) + " " + word);
        }
    }
}
