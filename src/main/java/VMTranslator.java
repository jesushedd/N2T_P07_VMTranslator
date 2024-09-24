import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

import static org.apache.commons.io.FilenameUtils.*;

public class VMTranslator {

    private static CodeWriter codeWriter;

    private static void translateFullDirectory(Path directoryPath) throws IOException {
        //get all .vm files as Path inside  a List
        Stream<Path> paths = null;
        try {
            paths = Files.walk(directoryPath, 1);
        } catch (IOException e) {
            System.out.println("Failed to identify files in directory :(");
            throw e;
        }

        final  List<Path> vmFiles = new ArrayList<>();

        Iterator<Path> iter = paths.iterator();
        iter.next(); //for base path
        while (iter.hasNext()){
            Path filePath = iter.next();
            if (isExtension(filePath.toString(), "vm")){
                vmFiles.add(filePath);
            }
        }
        //set outfile name and initialize code writer
        String outName = getBaseName( directoryPath.toString() ) + ".asm";
        Path outFile = Path.of(outName);
        codeWriter = new CodeWriter(outFile);
        //for each start translating
        for (Path vmClassFile : vmFiles){
            translateSingleFile(vmClassFile);
        }
        paths.close();

    }

    private static void translateSingleFile(Path filePath) throws IOException {
        Parser parser;
        if (!isExtension(filePath.toString(), "vm")) {
            System.out.println("Invalid file extension.");
            throw new RuntimeException();
        } else {
            try {
                //initialize parser
                parser = new Parser(filePath);
            } catch (IOException e) {
                System.out.println("Couldn't read the file named: " + filePath);
                throw e;
            }
        }
        //set outfile name and initialize code writer
        if (codeWriter == null) {
            String outName = getBaseName(filePath.toString()) + ".asm";
            Path outFile = Path.of(outName);
            codeWriter = new CodeWriter(outFile);
        }

        //set class Name
        codeWriter.setFileName(getBaseName(filePath.toString()));
        //Start parsing and writing
        while (parser.hasMoreLines()) {

            parser.advance();

            String currentCommandType = parser.commandType();

            if (currentCommandType.equals("C_ARITHMETIC")) {
                codeWriter.writeArithmetic(parser.arg1());
            } else if (currentCommandType.equals("C_PUSH") | currentCommandType.equals("C_POP")) {
                codeWriter.writePushPop(parser.pushOrPop(), parser.arg1(), parser.arg2());
            } else if (currentCommandType.equals("LABEL")) {
                codeWriter.writeLabel(parser.getLabel());
            } else if (currentCommandType.equals("IF-GOTO")) {
                codeWriter.writeIf(parser.getLabel());
            } else if (currentCommandType.equals("GOTO")) {
                codeWriter.writeGoto(parser.getLabel());
            } else if (currentCommandType.equals("FUNCTION")) {
                codeWriter.writeFunction(parser.getLabel(), parser.nVars());
            } else if (currentCommandType.equals("RETURN")) {
                codeWriter.writeReturn();
            } else if (currentCommandType.equals("CALLING")){
                codeWriter.writeCall(parser.getFunName(), parser.nArgs());
            }
        }
        parser.close();
    }


    public static void main(String[] args) {
        //get src path and check if exist
        Path inFile = Path.of(args[0]).toAbsolutePath();
        if (!Files.exists(inFile)) {
            System.out.println("The input file doesn't exist :(.");
            return;
        }


        //check if given path argument is a file or folder
        try {
            if (Files.isDirectory(inFile)) {
                translateFullDirectory(inFile);
            } else if (Files.exists(inFile)) {
                translateSingleFile(inFile);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                codeWriter.close();
            } catch (IOException e) {
                System.out.println("Couldn't close the output file.");
                e.printStackTrace();
            }
        }
    }
}