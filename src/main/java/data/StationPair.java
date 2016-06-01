package data;

import db.SourceConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by I322233 on 6/1/2016.
 */
public class StationPair {
    private static SourceConnectionPool sourceConnectionPool = SourceConnectionPool.getInstance();
    private Station start;

    public Station getEnd() {
        return end;
    }

    public void setEnd(Station end) {
        this.end = end;
    }

    public Station getStart() {
        return start;
    }

    public void setStart(Station start) {
        this.start = start;
    }

    private Station end;

    private String lineString="";

    public double getLength() {
        return length;
    }

    public void setLength(double length) {
        this.length = length;
    }

    public String getLineString() {
        return lineString;
    }

    public void setLineString(String lineString) {
        this.lineString = lineString;
    }

    private double length;

    public void convert(String line){
        String startStr = start.getPoint().toString();
        String endStr = end.getPoint().toString();
        try {
            Connection connection = sourceConnectionPool.getConnection();
            Statement st = connection.createStatement();
            String sql = "SELECT ST_Length(ST_LineSubstring( line, ST_LineLocatePoint(line, pta), ST_LineLocatePoint(line, ptb))::geography) FROM ( SELECT 'SRID=4326;"+line+"'::geometry line, 'SRID=4326;"+startStr+"'::geometry pta, 'SRID=4326;"+endStr+"'::geometry ptb ) data;";
            ResultSet rs = st.executeQuery(sql);
            while(rs.next()){
                this.length = rs.getDouble(1);
                System.out.println("pair "+start.getName()+","+end.getName()+" length: "+length);
            }
            sql = "SELECT ST_AsText(ST_LineSubstring( line, ST_LineLocatePoint(line, pta), ST_LineLocatePoint(line, ptb))::geography) FROM ( SELECT 'SRID=4326;"+line+"'::geometry line, 'SRID=4326;"+startStr+"'::geometry pta, 'SRID=4326;"+endStr+"'::geometry ptb ) data;";
            rs = st.executeQuery(sql);
            while (rs.next()){
                this.lineString = rs.getString(1);
                System.out.println("pair "+start.getName()+","+end.getName()+" linestring: "+lineString);
            }
            sourceConnectionPool.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

}
