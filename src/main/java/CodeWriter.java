import javax.imageio.IIOException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CodeWriter {

    private final BufferedWriter outWriter;

    private final Map<String, String> twoValueCommands = new HashMap<>();

    private final Map<String, String> oneValueCommands = new HashMap<>();

    private void fillMaps(){
        //fill commands mapping
        twoValueCommands.put("add", "+");
        twoValueCommands.put("sub", "-");
        twoValueCommands.put("eq", "==");
        twoValueCommands.put("gt", ">");
        twoValueCommands.put("lt", "<");
        twoValueCommands.put("and", "&");
        twoValueCommands.put("or", "|");

        oneValueCommands.put("neg", "-");
        oneValueCommands.put("not","!");

    }

    public CodeWriter(Path outFile) throws IOException {
        //open file ready to write
        outWriter = Files.newBufferedWriter(outFile);
        fillMaps();
    }

    CodeWriter(Writer writer){
        outWriter = new BufferedWriter(writer);
        fillMaps();
    }

    public void writeArithmetic(String arithmeticCommand) throws IOException{
        // write comment
        outWriter.write("// " + arithmeticCommand );
        outWriter.newLine();

        //TODO write asm instruction
        String asmInstruction = assembleArithmetic(arithmeticCommand);
        outWriter.write(asmInstruction);
    }

    /*
    Translate an arithmetic command to Hack assembler instructions  St*/
    private String assembleArithmetic(String arithmeticCommand){

        String asmInstructions = null;
        if (oneValueCommands.containsKey(arithmeticCommand)){ //for commands that require only the most top value on Stack
            //TODO
            asmInstructions = "@SP\r\n" + //select stack pointer
                              "A=M-1\r\n" + // dereference it and subtract 1 , select top stack top value
                              "M=" + oneValueCommands.get(arithmeticCommand) + "M\r\n";  // update top value with new value commanded
        } else if (twoValueCommands.containsKey(arithmeticCommand)) { //for commands that require the 2 most top values on Stack
            //TODO

        } else {
            throw new IllegalArgumentException();
        }
        //command sp[-2] =  sp[-2] sp[-1]
        return asmInstructions;

    }

    public void writePushPop (String pushOrPopCommand, String memorySegment, int index) throws IIOException {
        return;

    }

    public void close() throws IOException{
        outWriter.close();
    }


}
