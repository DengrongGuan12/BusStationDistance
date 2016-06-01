package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by DengrongGuan on 2016/1/20.
 */
public class SourceConnectionPool {
    private static String dbUrl= "jdbc:postgresql://localhost:5432/postgis_22_sample?user=postgres&password=123456";
    private final static ArrayList<Connection> conns = new ArrayList<Connection>();

    private static SourceConnectionPool instance;

    public static void setDBUrl(String ip,String port,String user,String passwd){
        dbUrl = "jdbc://"+ip+":"+port+"/?user="+user+"&password="+passwd;
//		System.out.println(dbUrl);
    }


    private static int maxSize = 0;

    public SourceConnectionPool(int poolSize) {
        maxSize = poolSize;
    }

    public static SourceConnectionPool getInstance() {
        if (instance == null) {
            instance = new SourceConnectionPool(100);
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (conns.size() == 0 )
        {
            try {
                Class.forName("org.postgresql.Driver");

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return java.sql.DriverManager.getConnection(dbUrl);
        }
        else
        {
            int index = conns.size() - 1;
            return conns.remove(index);
        }
    }

    public synchronized void closeConnection(Connection conn) throws SQLException {
        if (conns.size() == maxSize) {
            conn.close();
        }
        else
        {
            conns.add(conn);
        }
    }
}
