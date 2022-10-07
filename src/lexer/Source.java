package lexer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;


public class Source {
    private final String text;
    private int cursor;
    private final int begin;
    private final int end;

    public Source (File file) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
             reader = new BufferedReader(new FileReader(file));
            String tempString;
             while ( (tempString = reader.readLine()) != null) {
                sb.append(tempString).append("\n");
             }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        text = sb.toString();
        begin = 0;
        end = text.length();
    }

    public Character getNthChar(int nth) {
        if (cursor + nth >= end) {
            return null;
        }
        return text.charAt(cursor + nth);
    }

    public void next() {
        cursor++;
    }

    public char getNextChar() {
        return getNthChar(0);
    }

    public boolean hasNext() {
        return cursor < end;
    }
}
