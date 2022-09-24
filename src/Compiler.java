import Lexer.Lexer;
import Parser.Parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Compiler {
    public static void main(String[] args) throws FileNotFoundException {
        PrintStream print=new PrintStream("output.txt"); //写好输出位置文件；
        System.setOut(print);
        String testFileSource = "testfile.txt";
        File file = new File(testFileSource);
        Lexer lexer = new Lexer(file);
        lexer.run();
//        lexer.printLexer();
        Parser parser = new Parser(lexer);
        parser.run();
        parser.print();
    }
}
