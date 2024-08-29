
import org.apache.commons.io.FilenameUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import static org.apache.commons.io.FilenameUtils.*;

public class VMTranslator {

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
            Parser parser = new Parser(inFile);
            //set outfile name and initialize code writer
            String outName = FilenameUtils.getBaseName(inFile.toString()) + ".asm";
            Path outFile = Path.of(outName);



        } catch (IOException e) {
            System.out.println("Couldn't read the file :(.");
        }
    }
}
