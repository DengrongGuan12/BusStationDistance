package data;

import db.SourceConnectionPool;
import util.GisUtil;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created by I322233 on 6/1/2016.
 */
public class StationPair {

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
        length = GisUtil.calculateLength(line,start.getPoint(),end.getPoint());
        System.out.println("pair: "+length);
        lineString = GisUtil.getLineString(line,start.getPoint(),end.getPoint());

    }

}
