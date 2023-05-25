package me.solarbakha.solarlevel.manager.SQLite.Error;

import me.solarbakha.solarlevel.manager.misc.ConvLib;

public class Error {
    public static void execute(Exception ex){
        ConvLib.logExc("Couldn't execute MySQL statement: ", ex);
    }
    public static void close(Exception ex){
        ConvLib.logExc("Failed to close MySQL connection: ", ex);
    }
}

