package lexer;

import java.util.regex.*;
public class FormatStr extends Word{
    public FormatStr(String text, int line) {
        super(text, line);
    }

    public int getParam() {
        String patternStr = "%d";
        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(getText());
        int ret = 0;
        while (matcher.find()) {
            ret++;
        }
        return ret;
    }

    public String getStr() {
        return getText().substring(1,getText().length()-1);
    }
}
