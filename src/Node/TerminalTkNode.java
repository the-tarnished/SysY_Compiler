package Node;

import Lexer.Token;

public class TerminalTkNode extends Node{
    private final Token tokenType;
    private final String word;
    public TerminalTkNode(SyntaxKind input,Token tokenType,String word) {
        super(input);
        this.tokenType = tokenType;
        this.word = word;
    }

    @Override
    public void print() {
        printTokenAndWord(tokenType,word);
    }
}
