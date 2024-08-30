
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.apache.commons.io.FilenameUtils.*;

public class VMTranslator {

    private static Parser parser;

    public static void main(String[] args)  {
        try {
            //get src path and check if exist
            Path inFile = Path.of(args[0]);
            if (!Files.exists(inFile)) {
                System.out.println("The input file doesn't exist :(.");
                return;
            }
            if (!isExtension(inFile.toString(),"vm")){
                System.out.println("Invalid file extension.");
                return;
            }
            //initialize parser
            parser = new Parser(inFile);
            //set outfile name and initialize code writer
            String outName = FilenameUtils.getBaseName(inFile.toString()) + ".asm";
            Path outFile = Path.of(outName);
            CodeWriter codeWriter = new CodeWriter(outFile);
            //Start parsing and writing
            while (parser.hasMoreLines()){
                parser.advance();
                if (parser.commandType().equals("C_ARITHMETIC")){
                    codeWriter.writeArithmetic(parser.arg1());
                } else {
                    codeWriter.writePushPop(parser.pushOrPop(), parser.arg1(), parser.arg2());
                }
            }
        } catch (IOException e) {
            System.out.println("Couldn't read the file :(.");
        } finally {
            try {
                parser.close();
            } catch (IOException e) {
                System.out.println("Couldn't close the file :(.");
                e.printStackTrace();
            }
        }
    }
}
