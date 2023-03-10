package ru.mcsnapix.snapistones.mysql;

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
        try (PreparedStatement ps = prepareStatement(query, vars)){
            ps.execute();
        }
    }

    public PreparedStatement prepareStatement(String query, Object... vars) throws SQLException {
        if (!isConnected()) openConnection();
        PreparedStatement ps = con.prepareStatement(query);
        int i = 0;
        if (query.contains("?")) {
            for (Object obj : vars) {
                i++;
                ps.setObject(i, obj);
            }
        }
        return ps;
    }

    public CachedRowSet getCRS(String query, Object... vars) throws SQLException {
        PreparedStatement ps = prepareStatement(query, vars);
        try (ps; ResultSet rs = ps.executeQuery(); CachedRowSet crs = RowSetProvider.newFactory().createCachedRowSet()) {
            crs.populate(rs);
            return crs;
        }
    }
}
