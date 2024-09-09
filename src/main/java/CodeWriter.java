import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CodeWriter {

    private final BufferedWriter outWriter;

    /*AArithmetic and Logic Commands that operate 2 values to asm*/
    private final Map<String, String> asmTwoOperators = new HashMap<>();

    /*AArithmetic and Logic Commands that operate 1 value to asm*/
    private final Map<String, String> asmOneOperators = new HashMap<>();

    /*Relational Commands that operate 2 values to asm*/
    private final Map<String, String> asmRelational = new HashMap<>();

    /*Mapping access pointer for memory segments : local, argument, temp, this, that*/
    private final Map<String, String> segmentPointers = new HashMap<>();

    /*Mapping for asn Snippets*/
    private final Map<String, String> asmSnippets = new HashMap<>();

    private final String className;

    private int relationalCounter;



    /*Constructor*/
    public CodeWriter(Path outFile) throws IOException {
        //open file ready to write
        outWriter = Files.newBufferedWriter(outFile);
        //save class name
        className = FilenameUtils.getBaseName(outFile.toString());
        relationalCounter = 0;
        fillMaps();

    }


    /*Constructor for testing*/
    CodeWriter(Writer writer, String nameOfClass) {
        outWriter = new BufferedWriter(writer);
        className = nameOfClass;
        relationalCounter = 0;
        fillMaps();
    }

    private void fillMaps() {
        //fill arithmetic and logic mapping
        asmTwoOperators.put("add", "+");
        asmTwoOperators.put("sub", "-");
        asmTwoOperators.put("and", "&");
        asmTwoOperators.put("or", "|");

        //fill relational mapping
        asmRelational.put("eq", "JEQ");
        asmRelational.put("gt", "JGT");
        asmRelational.put("lt", "JLT");

        //fil one value mapping
        asmOneOperators.put("neg", "-");
        asmOneOperators.put("not", "!");

        //fill memory segments mapping
        segmentPointers.put("argument", "@ARG");
        segmentPointers.put("local", "@LCL");
        segmentPointers.put("this", "@THIS");
        segmentPointers.put("that", "@THAT");
        segmentPointers.put("temp", "@5");

        //fill asm Snippets
        asmSnippets.put("D=pop()",  "@SP\r\n" + //select stack pointer
                                    "AM=M-1\r\n" + //dereference it and subtract 1, select top stack value ,update stack pointer by -1
                                    "D=M\r\n" ); //save top stack value

        asmSnippets.put("@(SP-1)",  "@SP\r\n" + // select new stack pointer (top value)
                                    "A=M-1\r\n" );// dereference it and subtract 1, finally select this new memory block

        asmSnippets.put("SP* = D",  "@SP\r\n" + //select stack pointer
                                    "A=M\r\n" + //dereference it
                                    "M=D\r\n" ); // save D register value on it

        asmSnippets.put("SP++",     "@SP\r\n" +
                                    "M=M+1\r\n");
    }

    public void writeArithmetic(String arithmeticCommand) throws IOException {
        // write comment
        outWriter.write("// " + arithmeticCommand);
        outWriter.newLine();

        //write asm instructions
        String asmInstruction = assembleArithmetic(arithmeticCommand);
        outWriter.write(asmInstruction);
    }

    /*
    Translate an arithmetic command to Hack assembler instructions  St*/
    private String assembleArithmetic(String commandLogArith) {

        String asmInstructions;
        if (asmOneOperators.containsKey(commandLogArith)) { //for commands that require only the most top value on Stack
            asmInstructions =   asmSnippets.get("@(SP-1)") +
                                "M=" + asmOneOperators.get(commandLogArith) + "M\r\n";  // update top value with new value commanded
        } else if (asmTwoOperators.containsKey(commandLogArith)) { //for commands that require the 2 most top values on Stack

            asmInstructions =   asmSnippets.get("D=pop()") +
                                asmSnippets.get("@(SP-1)") +

                            // execute operation and save it
                            "M=M" + asmTwoOperators.get(commandLogArith) + "D\r\n"; // save result of operation
        } else if (asmRelational.containsKey(commandLogArith)) {
            asmInstructions = //get top value (y)
                            asmSnippets.get("D=pop()") +
                            //get x
                            asmSnippets.get("@(SP-1)") +
                            //save comparison
                            "D=M-D\r\n" +
                            // if condition given is true; jump to save true
                            "@SAVE_TRUE" + relationalCounter +"\r\n" +
                            "D;" + asmRelational.get(commandLogArith) + "\r\n" +
                            //if not save false on new stack top value
                                    asmSnippets.get("@(SP-1)") +
                            "M=0\r\n" +

                            "@CONTINUE"+ relationalCounter +"\r\n" +
                            "0;JMP\r\n" +

                            "(SAVE_TRUE" +  relationalCounter +")\r\n" +
                            asmSnippets.get("@(SP-1)")+
                            "M=-1\r\n" +

                            "(CONTINUE" + relationalCounter + ")\r\n";
            relationalCounter++;
        } else throw new IllegalArgumentException();
        return asmInstructions;

    }

    public void writePushPop(String pushOrPopCommand, String memorySegment, int index) throws IOException {
        //write comment
        outWriter.write("//" + pushOrPopCommand + " " + memorySegment + " " + index + "\r\n");

        String asmInstructions;

        if (segmentPointers.containsKey(memorySegment)) {
            asmInstructions = assembleUsingPointer(pushOrPopCommand, memorySegment, index);
        } else if (memorySegment.equals("constant")){
            asmInstructions = assembleConstant(index);
        } else if (memorySegment.equals("static")) {
            asmInstructions = assembleStatic(pushOrPopCommand, index);

        } else if (memorySegment.equals("pointer")){
            asmInstructions = assemblePointer(pushOrPopCommand, index);
        } else throw new IllegalArgumentException();

        //write asm instructions
        outWriter.write(asmInstructions);


    }

    private String assemblePointer(String pushOrPopCommand, int index) {
        String asmInstructions ;
        if (!(index == 0 | index == 1)) throw  new IllegalArgumentException();

        String thisOrThat = index == 0 ? "@THIS\r\n" : "@THAT\r\n";
        if (pushOrPopCommand.equals("pop")){
            asmInstructions =   asmSnippets.get("D=pop()") +
                                thisOrThat +
                                "M=D\r\n";
        } else if (pushOrPopCommand.equals("push")) {
            asmInstructions =   thisOrThat +
                                "D=M\r\n" +

                                asmSnippets.get("SP*=D") +

                                asmSnippets.get("SP++");
        } else throw new IllegalArgumentException();
        return  asmInstructions;
    }

    private String assembleStatic(String pushOrPop, int index) {
        String asmInstruction;
        if (pushOrPop.equals("pop")){
            asmInstruction =    asmSnippets.get("D=pop()") +
                                //save on @className.i
                                "@" + className + "." + index + "\r\n" +
                                "M=D\r\n";
        } else if (pushOrPop.equals("push")) {
            asmInstruction =    "@" + className + "." + index + "\r\n" + //get vale from static i
                                "D=M\r\n" +

                                asmSnippets.get("SP*=D") +

                                asmSnippets.get("SP++");
        } else throw new IllegalArgumentException();
        return asmInstruction;
    }



    private String assembleConstant(int index) {
        return                      "@" + index + "\r\n" +
                                    "D=A\r\n" +

                                    asmSnippets.get("SP*=D") +

                                    asmSnippets.get("SP++");
    }

    private String assembleUsingPointer(String popPush, String memorySegment, int index) {
        String asmInstructions;
        String deReference = memorySegment.equals("temp") ? "A" : "M";

        if (popPush.equals("pop")){
            asmInstructions =   "@" + index + "\r\n" +
                                "D=A\r\n" +
                                segmentPointers.get(memorySegment) + "\r\n" +
                                "D=" + deReference + "+D\r\n" +
                                "@addr\r\n" +
                                "M=D\r\n" +

                                asmSnippets.get("D=pop()") +

                                "@addr\r\n" +
                                "A=M\r\n" +
                                "M=D\r\n";
        } else if (popPush.equals("push")){
            asmInstructions =   "@" + index + "\r\n" +
                                "D=A\r\n" +
                                segmentPointers.get(memorySegment) + "\r\n" +
                                "A="+ deReference + "+D\r\n" +
                                "D=M\r\n" +

                                asmSnippets.get("SP*=D") +

                                asmSnippets.get("SP++");
        } else throw new IllegalArgumentException();
        return  asmInstructions;
    }


    public void close() throws IOException{
        outWriter.close();
    }


}
