import error.ErrorKind;
import error.ErrorRet;
import error.Pair;
import error.Symbol;
import lexer.Lexer;
import parser.Parser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        PrintStream print=new PrintStream("error.txt"); //写好输出位置文件；
        System.setOut(print);
        String testFileSource = "testfile.txt";
        File file = new File(testFileSource);
        Lexer lexer = new Lexer(file);
        lexer.run();
//        lexer.printLexer();
        Parser parser = new Parser(lexer);
        parser.run();
//        parser.print();
        ErrorRet ret = parser.getRoot().check();
        ret.errorList.sort(Comparator.naturalOrder());
        if(ret.errorList.size() != 0) {
            for (Pair<ErrorKind> each:ret.errorList) {
                Symbol.printError(each.getKey(),each.getValue());
            }
        }

    }
}
