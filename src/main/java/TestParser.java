import  org.junit.Test;
import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Path;

public class TestParser {

    static Parser parser;
    static Path inFile = Path.of("I:\\OSSU\\sistemas\\nan_tetris\\projects\\7\\VMTranslator\\BasicTest.vm");


    //testAdvance including all lines even blanks and comments lines
    /* @Test
    public void testAdvance() throws IOException{
        parser =  new Parser(path");
        parser.advance();
        String actual = parser.currentLine;
        String expected = "// This file is part of www.nand2tetris.org";
        assertEquals(expected, actual);

        for (int i = 0; i < 4; i++) {
            parser.advance();
        }
        actual = parser.currentLine;
        expected = "";
        assertEquals(expected, actual);

        for (int i = 0; i < 27; i++) {
            parser.advance();

        }
        actual = parser.currentLine;
        expected = "add";
        assertEquals(expected, actual);
    }*/


    @Test
    public void testAdvance_HasMoreLines() throws IOException{
        parser =  new Parser(inFile);
        for (int i = 0; i < 25; i++) {
            parser.advance();
        }
        assertFalse(parser.hasMoreLines());

    }


    @Test
    public void testCommandType() throws IOException {
        parser =  new Parser(inFile);
        parser.advance();
        String actual = parser.commandType();
        String expected = "C_PUSH";
        assertEquals(expected, actual);
        for (int i = 0; i < 16; i++) {
            parser.advance();
        }
        actual = parser.commandType();
        expected = "C_ARITHMETIC";
        assertEquals(expected, actual);
        parser.advance();
        parser.advance();
        assertEquals(expected, parser.commandType());


    }

    @Test
    public void testArgs1_2() throws IOException {
        parser =  new Parser(inFile);
        parser.advance();
        assertEquals("constant", parser.arg1());
        assertEquals(10, parser.arg2());
        parser.advance();
        assertEquals("local", parser.arg1());
        assertEquals(0, parser.arg2());
    }

}
