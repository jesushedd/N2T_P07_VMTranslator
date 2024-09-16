import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.apache.commons.io.FilenameUtils.*;

public class VMTranslator {

    private static Parser parser;
    private static CodeWriter codeWriter;

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
            String outName = getBaseName(inFile.toString()) + ".asm";
            Path outFile = Path.of(outName);
            codeWriter = new CodeWriter(outFile);
            //Start parsing and writing
            while (parser.hasMoreLines()){

                parser.advance();

                if (parser.commandType().equals("C_ARITHMETIC")){
                    codeWriter.writeArithmetic(parser.arg1());
                } else if (parser.commandType().equals("C_PUSH") | parser.commandType().equals("C_POP")){
                    codeWriter.writePushPop(parser.pushOrPop(), parser.arg1(), parser.arg2());
                } else if (parser.commandType().equals("LABEL")) {
                    codeWriter.writeLabel(parser.getLabel());
                } else if (parser.commandType().equals("IF-GOTO")) {
                    codeWriter.writeIf(parser.getLabel());
                } else if (parser.commandType().equals("GOTO")) {
                    codeWriter.writeGoto(parser.getLabel());
                }
            }
        } catch (IOException e) {
            System.out.println("Couldn't read the file :(.");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                parser.close();
                codeWriter.close();
            } catch (IOException e) {
                System.out.println("Couldn't close the file :(.");
                e.printStackTrace();
            }
        }
    }
}
