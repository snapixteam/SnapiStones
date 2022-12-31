package ru.mcsnapix.snapistones.mysql;

import ru.mcsnapix.snapistones.utils.Utils;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class MySQL {

    private final String host;
    private final String database;
    private final String username;
    private final String password;
    private final int port;
    private Connection con;

    public MySQL(String host, int port, String database, String username, String password) throws SQLException {
        this.host = host;
        this.port = port;
        this.database = database;
        this.username = username;
        this.password = password;
        openConnection();
    }

    private void openConnection() throws SQLException {
        if (isConnected()) return;
        con = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
    }

    public void closeConnection() throws SQLException {
        if (isConnected()) con.close();
    }

    private boolean isConnected() throws SQLException {
        return con != null && !con.isClosed();
    }

    public void execute(String query, Object... vars) throws SQLException {
        try (PreparedStatement ps = prepareStatement(query, vars)) {
            if (ps == null) return;
            ps.execute();
        }
    }

    private PreparedStatement prepareStatement(String query, Object... vars) throws SQLException {
        try (PreparedStatement ps = con.prepareStatement(query)) {
            int i = 0;
            if (query.contains("?")) {
                for (Object obj : vars) {
                    i++;
                    ps.setObject(i, obj);
                }
            }
            return ps;
        } catch (SQLException e) {
            Utils.logError(e);
        }

        return null;
    }

    public CachedRowSet getCRS(String query, Object... vars) throws SQLException {
        ResultSet rs;
        PreparedStatement ps = null;
        CachedRowSet crs;
        try {
            ps = prepareStatement(query, vars);
            if (ps == null) return null;
            rs = ps.executeQuery();
            crs = RowSetProvider.newFactory().createCachedRowSet();
            crs.populate(rs);
            return crs;
        } catch (SQLException e) {
            Utils.logError(e);
        } finally {
            if (ps != null) {
                ps.close();
            }
        }

        return null;
    }
}
