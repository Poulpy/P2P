package util;

/**
 * Classe stockant les codes de succès et d'erreur.
 * TODO Messages génériques ? 31 Wrong password
 */
public class Codes {
    // Success : 2x
    public static final int SUCCESS = 21;
    public static final int UNKNOWN_USER = 22;
    public static final int PASSWORD_CORRECT = 23;
    public static final int NEW_USER = 24;

    // Error Client Side : 3x
    public static final int WRONG_PASSWORD = 31;
    public static final int ACTION_UNKNOWN = 32;
}

