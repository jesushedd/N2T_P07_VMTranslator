import javax.imageio.IIOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class CodeWriter {

    private final BufferedWriter outWriter;

    private final Map<String, String> operate2Values = new HashMap<>();

    public CodeWriter(Path outFile) throws IOException {
        //open file ready to write
        outWriter = Files.newBufferedWriter(outFile);
        //fill mappings
        operate2Values.put()
    }

    public void writeArithmetic(String arithmeticCommand) throws IIOException{
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
