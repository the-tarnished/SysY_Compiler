package Node;

import Lexer.Token;
import Lexer.Word;

public class TerminalTkNode extends Node{
    private final Token tokenType;
    private final Word word;
    public TerminalTkNode(SyntaxKind input,Token tokenType,Word word) {
        super(input);
        this.tokenType = tokenType;
        this.word = word;
    }

    @Override
    public void print() {
        printTokenAndWord(tokenType,word);
    }
}
