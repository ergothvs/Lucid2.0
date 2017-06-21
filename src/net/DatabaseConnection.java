/*
 This file is part of the OdinMS Maple Story Server
 Copyright (C) 2008 ~ 2010 Patrick Huy <patrick.huy@frz.cc> 
 Matthias Butz <matze@odinms.de>
 Jan Christian Meyer <vimes@odinms.de>

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License version 3
 as published by the Free Software Foundation. You may not use, modify
 or distribute this program under any other version of the
 GNU Affero General Public License.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;

import constants.ServerConfig;

/**
 * All servers maintain a Database Connection. This class therefore
 * "singletonices" the connection per process.
 *
 *
 * @author Frz (original method)
 * @author BlackRabbit (explaining the "too many connections" problem, and showing an example to fix it)
 * @author Novak (Implementing BlackRabbit's fix in this source's connection)
 */
public class DatabaseConnection {

    private static final ThreadLocal<Connection> con = new DatabaseConnection.ThreadLocalConnection();
    public static final int CLOSE_CURRENT_RESULT = 1;
    /**
     * The constant indicating that the current <code>ResultSet</code> object
     * should not be closed when calling <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int KEEP_CURRENT_RESULT = 2;
    /**
     * The constant indicating that all <code>ResultSet</code> objects that have
     * previously been kept open should be closed when calling
     * <code>getMoreResults</code>.
     *
     * @since 1.4
     */
    public static final int CLOSE_ALL_RESULTS = 3;
    /**
     * The constant indicating that a batch statement executed successfully but
     * that no count of the number of rows it affected is available.
     *
     * @since 1.4
     */
    public static final int SUCCESS_NO_INFO = -2;
    /**
     * The constant indicating that an error occured while executing a batch
     * statement.
     *
     * @since 1.4
     */
    public static final int EXECUTE_FAILED = -3;
    /**
     * The constant indicating that generated keys should be made available for
     * retrieval.
     *
     * @since 1.4
     */
    public static final int RETURN_GENERATED_KEYS = 1;
    /**
     * The constant indicating that generated keys should not be made available
     * for retrieval.
     *
     * @since 1.4
     */
    public static final int NO_GENERATED_KEYS = 2;


    static {
        try {
            Class.forName("com.mysql.jdbc.Driver"); // touch the mysql driver
        } catch (ClassNotFoundException e) {
            System.out.println("[SEVERE] SQL Driver Not Found. Consider death by clams.");
            e.printStackTrace();
        }
    }


    public static Connection getConnection() {
        Connection c = con.get();
        try {
            c.getMetaData();
        } catch (SQLException e) { // connection is dead, therefore discard old object
            con.remove();
            c = con.get();
        }
        return c;
    }

    public static void closeAll() throws SQLException {
        for (final Connection connection : DatabaseConnection.ThreadLocalConnection.allConnections) {
            if (connection != null) {
                connection.close();
            }
        }
    }

    private static class ThreadLocalConnection extends ThreadLocal<Connection> {
        public static final Collection<Connection> allConnections = new LinkedList<>();

        @Override
        protected Connection initialValue() {
            try {
                return DriverManager.getConnection("jdbc:mysql://127.0.0.1:" + ServerConfig.SQL_PORT + "/" + ServerConfig.SQL_DATABASE + "?autoReconnect=true&useSSL=false", ServerConfig.SQL_USER, ServerConfig.SQL_PASS);
            } catch (SQLException e) {
                System.out.println("[SEVERE] Unable to make net.db connection.");
                e.printStackTrace();
                return null;
            }
        }
    }
}