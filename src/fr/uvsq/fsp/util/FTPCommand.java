package util;

public class FTPCommand {

    // USER, PASS
    public String command;
    // toto
    public String content;

    public FTPCommand(String cmd, String cont) {
        command = cmd;
        content = cont;
    }

    public String toString() {
        return command + " " + content;
    }

    /**
     * Analyse un texte comme :
     * "USER toto" ou "PASS bidule"
     */
    public static FTPCommand parseCommand(String str) {
        int index;
        String type;
        String desc;

        index = str.indexOf(' ');
        type = str.substring(0, index);
        desc = str.substring(index + 1, str.length());

        return new FTPCommand(type, desc);
    }
}

