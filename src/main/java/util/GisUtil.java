package util;

import data.Point;
import db.SourceConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by I322233 on 6/2/2016.
 */
public class GisUtil {
    private static SourceConnectionPool sourceConnectionPool = SourceConnectionPool.getInstance();
    public static double calculateLength(String line, Point a, Point b){
        double length = 0;
        String startStr = a.toString();
        String endStr = b.toString();
        try {
            Connection connection = sourceConnectionPool.getConnection();
            Statement st = connection.createStatement();
            String sql = "SELECT ST_Length(ST_LineSubstring( line, ST_LineLocatePoint(line, pta), ST_LineLocatePoint(line, ptb))::geography) FROM ( SELECT 'SRID=0;"+line+"'::geometry line, 'SRID=0;"+startStr+"'::geometry pta, 'SRID=0;"+endStr+"'::geometry ptb ) data;";
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                length = rs.getDouble(1);
            }
            sourceConnectionPool.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return length;
    }
    public static String getLineString(String line, Point a, Point b){
        String lineString = "";
        String startStr = a.toString();
        String endStr = b.toString();
        try {
            Connection connection = sourceConnectionPool.getConnection();
            Statement st = connection.createStatement();
            String sql = "SELECT ST_AsText(ST_LineSubstring( line, ST_LineLocatePoint(line, pta), ST_LineLocatePoint(line, ptb))::geometry) FROM ( SELECT 'SRID=0;"+line+"'::geometry line, 'SRID=0;"+startStr+"'::geometry pta, 'SRID=0;"+endStr+"'::geometry ptb ) data;";
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()){
                lineString = rs.getString(1);
            }
            sourceConnectionPool.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lineString;
    }
}
