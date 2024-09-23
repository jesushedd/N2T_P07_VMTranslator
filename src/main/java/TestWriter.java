import org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.StringWriter;

public class TestWriter {
    private CodeWriter codeWriter;

    @Test
    public void testWriteArithmetic() throws IOException {
        StringWriter out = new StringWriter();
        // neg not
        codeWriter = new CodeWriter(out, "myClass");
        codeWriter.writeArithmetic("neg");
        codeWriter.writeArithmetic("not");
        codeWriter.close();
        assertEquals(  "// neg\r\n" +
                                "@SP\r\n" +
                                "A=M-1\r\n" +
                                "M=-M\r\n" +
                                "// not\r\n" +
                                "@SP\r\n" +
                                "A=M-1\r\n" +
                                "M=!M\r\n",
                                out.toString());

    }

}
