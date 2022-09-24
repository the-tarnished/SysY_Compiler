package Parser;

import Lexer.Lexer;
import Lexer.Token;
import java.util.ArrayList;
import java.util.HashMap;

public class Words {
    private final ArrayList<String> words;
    private final HashMap<String,Token> word2Token;
    private int cursor;
    private final int end;
    private final int begin;
    public Words(Lexer lexer) {
        this.words = lexer.getWords();
        this.word2Token = lexer.getWord2Token();
        begin = 0;
        cursor = begin;
        end = words.size();
    }

    public String getNthWord(int nth) {
        return words.get(cursor + nth);
    }

    public String getNextWord() {
        return getNthWord(0);
    }

    public Token getNextKind() {
        return word2Token.get(getNextWord());
    }

    public Token getNthKind(int nth) {
        return word2Token.get(getNthWord(nth));
    }

    public Token getWordKind(String word) {
        return word2Token.get(word);
    }

    public boolean hasNext() {
        return cursor < end;
    }

    public void next() {
        cursor++;
    }
}
