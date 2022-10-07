package node;

import error.ErrorRet;
import lexer.Token;
import lexer.Word;
import org.omg.CORBA.PUBLIC_MEMBER;

public class TerminalTkNode extends Node{
    private final Token tokenType;
    private final Word word;
    public TerminalTkNode(SyntaxKind input,Token tokenType,Word word) {
        super(input);
        this.tokenType = tokenType;
        this.word = word;
    }

    public Token getTokenType() {
        return tokenType;
    }

    public Word getWord() {
        return word;
    }

    @Override
    public void print() {
        printTokenAndWord(tokenType,word);
    }

    @Override
    public ErrorRet check() {
        return super.check();
    }
}