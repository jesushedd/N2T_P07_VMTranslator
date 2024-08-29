import org.apache.commons.io.function.IOSpliterator;

import javax.imageio.IIOException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class CodeWriter {

    private final BufferedWriter outWriter;

    private final Set<String> twoValuesCommands = new HashSet<>();

    private final Set<String> oneValueCommands = new HashSet<>();

    public CodeWriter(Path outFile) throws IOException {
        //open file ready to write
        outWriter = Files.newBufferedWriter(outFile);
        //fill commands sets
        twoValuesCommands.addAll(List.of("add", "sub", "eq", "gt", "lt", "and", "or"));
        oneValueCommands.addAll(List.of("neg", "not"));
    }

    public void writeArithmetic(String arithmeticCommand) throws IOException{
        // TODO write comment
        outWriter.write(arithmeticCommand);
        outWriter.flush();
        notify();close();
        //
        if (oneValueCommands.contains(arithmeticCommand)){
            //TODO
        } else if (twoValuesCommands.contains(arithmeticCommand)) {
            //TODO

        } else {
            throw new IllegalArgumentException();
        }

        //command sp[-2] =  sp[-2] sp[-1]
        return;
    }

    public void writePushPop (String pushOrPopCommand, String memorySegment, int index) throws IIOException {
        return;

    }

    public void close(){
        return;
    }


}
