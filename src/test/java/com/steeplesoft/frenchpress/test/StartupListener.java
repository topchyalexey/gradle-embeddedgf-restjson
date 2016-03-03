/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package com.steeplesoft.frenchpress.test;

import com.steeplesoft.frenchpress.service.PostService;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.inject.Inject;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

/**
 *
 * @author jdlee
 */
@WebListener
public class StartupListener implements Serializable, ServletContextListener {

    private static boolean initialized = false;
    private static final long serialVersionUID = 1L;
    @Resource(mappedName = "jdbc/frenchpress")
    private DataSource dataSource;
    @Inject
    PostService ps;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        validateDatabase();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

    public synchronized void validateDatabase() {
        if (!initialized) {
            try (Connection connection = dataSource.getConnection();) {
//            dumpMetadata(connection);
//                try {
//                    processSqlFile(connection, "drop.sql");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//                processSqlFile(connection, "create-derby.sql");
                processSqlFile(connection, "sampledata.sql");
                initialized = true;
            } catch (Exception ex) {
                Logger.getLogger(StartupListener.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void processSqlFile(Connection connection, String fileName) throws FileNotFoundException, URISyntaxException, IOException, SQLException {

        URL file = getClass().getResource("/sql/" + fileName);
        if (file == null) {
            throw new RuntimeException("Could not find the specified SQL file: " + fileName);
        }

        String vendor = getDatabaseVendor(connection);
        boolean isPsql = vendor.contains("PostgreSQL");
        boolean isDerby = vendor.contains("Derby");

        try (Statement stmt = connection.createStatement();
                BufferedReader reader = new BufferedReader(new FileReader(new File(file.toURI())))) {
            String line = reader.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    if (isPsql) {
                        line = line.replace("LONGBLOB", "bytea");
                    } else if (isDerby) {
                        if (line.endsWith(";")) {
                            line = line.substring(0, line.length() - 1);
                        }
                    }
                    stmt.execute(line);
                }
                line = reader.readLine();
            }
        }
    }

    private String getDatabaseVendor(Connection conn) throws SQLException {
        DatabaseMetaData md = conn.getMetaData();
        String name = md.getDatabaseProductName();
        return name;
    }

    private void dumpMetadata(Connection conn) throws SQLException {
        DatabaseMetaData dbmd = conn.getMetaData();
        ResultSet drs = dbmd.getTables(null, null, "", new String[]{"TABLE"});
        while (drs.next()) {
            String table = drs.getString("TABLE_NAME");

            StringBuilder result = new StringBuilder();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT * FROM ");
            sql.append(table);
            ResultSet rs = conn.createStatement().executeQuery(sql.toString());
            ResultSetMetaData md = rs.getMetaData();
            result.append("CREATE TABLE ");
            result.append(table);
            result.append(" ( ");
            for (int i = 1; i <= md.getColumnCount(); i++) {
                if (i != 1) {
                    result.append(',');
                }
                result.append(md.getColumnName(i));
                result.append(' ');
                String type = md.getColumnTypeName(i) + " " + md.getPrecision(i);
                result.append(type);
                if (md.getPrecision(i) < 65535) {
                    result.append('(');
                    result.append(md.getPrecision(i));
                    if (md.getScale(i) > 0) {
                        result.append(',');
                        result.append(md.getScale(i));
                    }
                    result.append(") ");
                } else {
                    result.append(' ');
                }

                if (md.isNullable(i)
                        == ResultSetMetaData.columnNoNulls) {
                    result.append("NOT NULL ");
                } else {
                    result.append("NULL ");
                }
                if (md.isAutoIncrement(i)) {
                    result.append(" auto_increment");
                }
            }
            result.append(" ); ");
            System.out.println(result.toString());
        }
    }
}
