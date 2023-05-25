package me.solarbakha.solarlevel.manager.SQLite;

import me.solarbakha.solarlevel.SolarLevel;
import me.solarbakha.solarlevel.manager.misc.ConvLib;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;


public class SQLite extends Database {

    private static final String dbname = "levels";

    public SQLite(SolarLevel instance) {
        super(instance);
    }

    /**
     * Get the connection to the database file.
     */
    public Connection getSQLConnection() {

        File dbFile = new File(plugin.getDataFolder(), dbname + ".db");
        if (!dbFile.exists()) {
            try {
                dbFile.createNewFile();
            } catch (IOException e) {
                ConvLib.logExc("File write error: %1$s.db\n".formatted(dbname), e);
            }
        }
        try {
            if (connection != null && !connection.isClosed()) return connection;
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + dbFile);
        } catch (SQLException ex) {
            ConvLib.logExc("SQLite exception on initialize", ex);
        } catch (ClassNotFoundException ex) {
            ConvLib.logExc("SQLite JBDC library not found.", ex);
        }
        return null;
    }

    /**
     * Load the database.
     */
    public void load() {
        connection = getSQLConnection();
        try {
            Statement s = connection.createStatement();
            String SQLiteCreateTokensTable = "CREATE TABLE IF NOT EXISTS levels (" +
                    "`player` varchar(36) NOT NULL," +
                    "`xp` int(11) NOT NULL," +
                    "PRIMARY KEY (`player`), UNIQUE (`player`)" +
                    ");";
            s.executeUpdate(SQLiteCreateTokensTable);
            s.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        initialize();
    }
}

