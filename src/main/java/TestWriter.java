import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

public class TestWriter {
    private CodeWriter codeWriter;

    @Test
    public void testWriteArithmetic() throws IOException {
        StringWriter out = new StringWriter();
        codeWriter = new CodeWriter(out);
        codeWriter.writeArithmetic("neg");
        codeWriter.writeArithmetic("not");
        codeWriter.close();
        assertEquals("// neg\r\n@SP\r\nA=M-1\r\nM=-M\r\n// not\r\n@SP\r\nA=M-1\r\nM=!M\r\n", out.toString());




    }

}
