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
    private String commandType;

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
        if (isValidLine(currentCommand)){
            sanitizeCurrentCommand();
            setCommandType();
        }else advance();
    }

    private void sanitizeCurrentCommand(){
        String[] parts  = currentCommand.split("//");
        currentCommand = parts[0];
        currentCommand = currentCommand.strip();
    }



    /*Returns a constant representing the type of the current command*/
    private void  setCommandType(){
        Set<String> arithmeticCommands = new HashSet<>(List.of("add", "sub", "neg", "eq", "gt",
                "lt", "and", "or", "not"));

        String[] commandsParts = currentCommand.split(" ");
        String keyWord = commandsParts[0];

        for (String ar : arithmeticCommands){
            if (keyWord.contains(ar)) {
                commandType = "C_ARITHMETIC";
                return;
            }
        }

        if (currentCommand.contains("push")) commandType = "C_PUSH";
        else if (keyWord.contains("pop")) commandType = "C_POP";
        else if (keyWord.contains("label")) commandType = "LABEL";
        else if (keyWord.contains("function")) commandType = "FUNCTION";
        else if (keyWord.contains("if-goto")) commandType = "IF-GOTO";
        else if (keyWord.contains("goto")) commandType = "GOTO";
        else if (keyWord.contains("call")) commandType = "CALLING";
        else if (keyWord.contains("return")) commandType = "RETURN";
    }

    public String commandType() {
        return commandType;
    }



    public String pushOrPop(){
        String [] parts = currentCommand.split(" ");
        return parts[0];
    }


    /*Returns the first argument of the current command*/
    public String arg1(){
        if (commandType().equals("C_ARITHMETIC")) return currentCommand;
        //when is pop or push
        String [] parts = currentCommand.split(" ");
        return parts[1];
    }


    /*Returns the second argument of the current command
    * call it only if current command is push or pop*/
    public int arg2(){
        String[] parts = currentCommand.split(" ");
        return Integer.parseInt(parts[2]);
    }



    public String getLabel() throws Exception {
        if (commandType.equals("LABEL") | commandType.equals("GOTO") | commandType.equals("IF-GOTO") | commandType.equals("FUNCTION")){
            String[] parts = currentCommand.split(" ");
            return parts[1];
        } else throw new Exception("No label type command");
    }

    public int nVars() {
        if (!commandType.equals("FUNCTION")){
            throw new RuntimeException("Not a function declaration command :|");
        }
        String[] commandParts = currentCommand.split(" ");
        return Integer.parseInt(commandParts[2]);
    }


    public void close() throws IOException {
        sourceFile.close();
    }


}

