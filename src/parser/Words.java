package parser;

import lexer.Lexer;
import lexer.Token;
import lexer.Word;
import java.util.ArrayList;
import java.util.HashMap;

public class Words {
    private final ArrayList<Word> words;
    private final HashMap<String,Token> word2Token;
    private int cursor;
    private final int end;
    private final int begin;
    private int env;
    public Words(Lexer lexer) {
        this.words = lexer.getWords();
        this.word2Token = lexer.getWord2Token();
        begin = 0;
        cursor = begin;
        end = words.size();
        env = 0;
    }

    public Word getNthWord(int nth) {
        return words.get(cursor + nth);
    }

    public Word getNextWord() {
        return getNthWord(0);
    }

    public Token getNextKind() {
        return word2Token.get(getNextWord().getText());
    }

    public Token getNthKind(int nth) {
        return word2Token.get(getNthWord(nth).getText());
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

    public void saveEnv() {
        env = cursor;
    }

    public void loadEnv() {
        cursor = env;
    }
}
