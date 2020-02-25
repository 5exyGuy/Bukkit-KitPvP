package eu.spikedpvp.kitpvp.database;

import com.google.inject.Inject;
import eu.spikedpvp.kitpvp.KitPvP;
import eu.spikedpvp.kitpvp.utilities.Console;

import java.sql.*;
import java.util.Arrays;

public final class MySQL {

    private final KitPvP plugin;
    private Connection connection;

    @Inject
    public MySQL(KitPvP plugin) {
        this.plugin = plugin;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public void setConnection(final String host, final String user, final String password, final String database, final String port) {
        if (host == null || user == null || password == null || database == null) {
            return;
        }
        disconnect(false);
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database +
                    "?autoReconnect=true&useSSL=" + plugin.getCustomConfig().getMySQL().getSSL(), user, password);
            Console.sendSuccess("Successfully connected into database");
        }
        catch (Exception e) {
            Console.sendError(e.getMessage());
        }
    }

    public void connect() {
        connect(true);
    }

    private void connect(final boolean message) {
        final String host = this.plugin.getCustomConfig().getMySQL().getHost();
        final String user = this.plugin.getCustomConfig().getMySQL().getUsername();
        final String password = this.plugin.getCustomConfig().getMySQL().getPassword();
        final String database = this.plugin.getCustomConfig().getMySQL().getDatabase();
        final String port = this.plugin.getCustomConfig().getMySQL().getPort();

        for (String col : Arrays.asList(new String[] { host, user, password, database, port })) {
            if (col.isEmpty()) {
                Console.sendError("Configuration file is invalid");
                return;
            }
        }

        if (isConnected()) {
            if (message) {
                Console.sendError("Could not connect to database because it is already connected");
            }
        } else {
            this.setConnection(host, user, password, database, port);
        }

    }

    public void disconnect() {
        disconnect(true);
    }

    private void disconnect(final boolean message) {
        try {
            if (isConnected()) {
                connection.close();
                if (message) {
                    Console.sendSuccess("Successfully logged out of database");
                }
            } else if (message) {
                Console.sendError("Failed to disconnect because the connection does not exist");
            }
        }
        catch (Exception e) {
            if (message) {
                Console.sendError(e.getMessage());
            }
        }
        connection = null;
    }

    public void reconnect() {
        this.disconnect();
        this.connect();
    }

    public boolean isConnected() {
        if (this.connection != null) {
            try {
                return !this.connection.isClosed();
            }
            catch (Exception e) {
                Console.sendError(e.getMessage());
            }
        }
        return false;
    }

    public boolean update(final String command) {
        if (command == null) {
            return false;
        }
        boolean result = false;
        this.connect(false);
        try {
            final Statement st = getConnection().createStatement();
            st.executeUpdate(command);
            st.close();
            result = true;
        }
        catch (Exception e) {
            Console.sendError(e.getMessage());
        }
        this.disconnect(false);
        return result;
    }

    public ResultSet query(final String command) {
        if (command == null) {
            return null;
        }
        this.connect(false);
        ResultSet rs = null;
        try {
            final Statement st = getConnection().createStatement();
            rs = st.executeQuery(command);
        }
        catch (Exception e) {
            Console.sendError(e.getMessage());
        }
        return rs;
    }

}
