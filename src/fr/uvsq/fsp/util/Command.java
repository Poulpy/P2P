package fr.uvsq.fsp.util;

public class Command {

    // USER, PASS
    public String command;
    // toto
    public String content;

    public Command(String cmd, String cont) {
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
    public static Command parseCommand(String str) {
        int index;
        String type;
        String desc;

        if (str.contains(" ")) {
            index = str.indexOf(' ');
            type = str.substring(0, index);
            desc = str.substring(index + 1, str.length());
            return new Command(type, desc);
        } else {
            return new Command(str, "");
        }
    }
}

