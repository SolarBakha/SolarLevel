package me.solarbakha.solarlevel.manager.SQLite;


import me.solarbakha.solarlevel.SolarLevel;
import me.solarbakha.solarlevel.manager.SQLite.Error.Error;
import me.solarbakha.solarlevel.manager.SQLite.Error.Errors;
import me.solarbakha.solarlevel.manager.misc.ConvLib;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;


public abstract class Database {
    private static final ExecutorService THREADPOOL = Executors.newSingleThreadExecutor();
    static SolarLevel plugin;
    protected static Connection connection;
    protected static String dbName = "levels";

    public Database(SolarLevel instance) {
        plugin = instance;
    }

    public abstract Connection getSQLConnection();

    public abstract void load();

    public void initialize() {
        connection = getSQLConnection();
        try {
            PreparedStatement ps = connection.prepareStatement("SELECT * FROM " + dbName);
            ResultSet rs = ps.executeQuery();
            close(ps, rs);

        } catch (SQLException ex) {
            plugin.getLogger().log(Level.SEVERE, "Unable to retrieve connection", ex);
        }
    }


    /**
     * <p>
     * Execute any statement using this method. This will return a success or
     * failure boolean.
     * </p>
     *
     * @param statement The statement to execute.
     * @return the {@link Database}'s success or failure (true/false).
     */
    public Boolean executeStatement(String statement) {
        Future<Boolean> future = THREADPOOL.submit(() -> {
        Connection conn = null;
        PreparedStatement ps = null;
        try {
            conn = getSQLConnection();
            ps = conn.prepareStatement(statement);
            return !ps.execute();  // todo: thread dis
        } catch (SQLException ex) {
            ConvLib.logExc(Errors.sqlConnectionExecute(), ex);
            return false;
        } finally {
            try {
                if (ps != null) ps.close();
                if (conn != null) conn.close();
            } catch (SQLException ex) {
                ConvLib.logExc(Errors.sqlConnectionClose(), ex);
            }
        }
        });
        try{
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Get a single value from the database. Your If your statement returns multiple
     * values, only the first value will return. Use queryRow for multiple values in
     * 1 row.
     *
     * @param string The statement to execute.
     * @param row    The row you would like to store data from.
     * @return the {@link Database}'s Query in Object format. Casting required to
     * change variables into their original form.
     */
    public Object queryValue(String string, String row) {
        Future<Object> future = THREADPOOL.submit(() -> {
            Connection conn = null;
            PreparedStatement ps = null;
            ResultSet rs;
            try {
                conn = getSQLConnection();
                ps = conn.prepareStatement("SELECT * FROM " + dbName + " WHERE player = '" + string + "';");

                rs = ps.executeQuery();
                if (rs.next()) return rs.getObject(row);
            } catch (SQLException ex) {
                ConvLib.logExc(Errors.sqlConnectionExecute(), ex);
            } finally {
                try {
                    if (ps != null) ps.close();
                    if (conn != null) conn.close();
                } catch (SQLException ex) {
                    ConvLib.logExc(Errors.sqlConnectionClose(), ex);
                }
            }

            return null;
        });
        try{
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Close the current connection of the statement to the database.
     *
     * @param ps The statement previously used.
     * @param rs The result set that was returned from the statement.
     */
    public void close(PreparedStatement ps, ResultSet rs) {
        try {
            if (ps != null) ps.close();
            if (rs != null) rs.close();
        } catch (SQLException ex) {
            Error.close(ex);
        }
    }


    /**
     * Close the current connection to the database.
     *
     * The database will need to be re-initialized if this is used. When
     * initializing using the main class, it will delete this current object
     * and create a new object connected to the db. If you'd like to reload
     * this db without trashing the database object, invoke the {@link #load()}
     * method through the global map of databases.
     *
     * Ex: getDatabase("name").load();
     */
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException ex) {
            Error.close(ex);
        }
    }
}

