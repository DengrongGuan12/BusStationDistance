package db;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by DengrongGuan on 2016/1/20.
 */
public class HanaConnectionPool {
    private static String dbUrl= "jdbc:sap://10.128.170.56:30315/?user=DATA_DEVELOPER&password=Sap12345";
    private final static ArrayList<Connection> conns = new ArrayList<Connection>();

    private static HanaConnectionPool instance;

    public static void setDBUrl(String ip,String port,String user,String passwd){
        dbUrl = "jdbc:sap://"+ip+":"+port+"/?user="+user+"&password="+passwd;
//		System.out.println(dbUrl);
    }


    private static int maxSize = 0;

    public HanaConnectionPool(int poolSize) {
        maxSize = poolSize;
    }

    public static HanaConnectionPool getInstance() {
        if (instance == null) {
            instance = new HanaConnectionPool(100);
        }
        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (conns.size() == 0 )
        {
            try {
                Class.forName("com.sap.db.jdbc.Driver");

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
