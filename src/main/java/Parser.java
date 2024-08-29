import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.io.*;

public class Parser {

    private static BufferedReader sourceFile;
    private String currentCommand = "" ;
    private String nextCommand;

    /*Return true if a string is a valid line
    * A valid lines is no blank, and not a comment line*/
    private boolean isValidLine(String line){
        line = line.strip();
        if (line.isBlank()) return false;
        String [] parts =  line.split("//");
        return !parts[0].isBlank();


    }




    /*Constructor
    * Opens the input file, and gets ready to parse it*/
    public Parser(Path inFile) throws IOException {
            sourceFile =  Files.newBufferedReader(inFile);
            nextCommand = sourceFile.readLine();
    }


    public boolean hasMoreLines(){
        return nextCommand != null;
    }


    /*Reads the next command from the input and makes it the current command*/
    public void advance() throws IOException{
        currentCommand = nextCommand;
        nextCommand = sourceFile.readLine();
        if (!isValidLine(currentCommand)) {
            advance();
        }



    }




    /*Returns a constant representing the type of the current command*/
    public String commandType(){
        Set<String> arithmeticCommands = new HashSet<>(List.of("add", "sub", "neg", "eq", "gt",
                "lt", "and", "or", "not"));

        if (currentCommand.contains("push")) return "C_PUSH";
        if (currentCommand.contains("pop")) return "C_POP";
        for (String ar : arithmeticCommands){
            if (currentCommand.contains(ar)) return "C_ARITHMETIC";
        }
        return null;
    }


    /*Returns the first argument of the current command*/
    public String arg1(){
        if (commandType().equals("C_ARITHMETIC")) return currentCommand;
        //when is pop or push
        String [] parts = currentCommand.split(" ");
        return parts[1];
    }


    /*Returns the second argument of the current command*/
    public int arg2(){
        if (!commandType().equals("C_ARITHMETIC")){
            String[] parts = currentCommand.split(" ");
            return Integer.parseInt(parts[2]);
        }
        return -1;
    }





    }

