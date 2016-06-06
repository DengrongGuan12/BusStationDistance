package dataHana;

import data.Point;
import data.Station;
import db.HanaConnectionPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by I322233 on 6/3/2016.
 */
public class BusLineStation {
    public static String schema = "SAP_TRAFFIC_DATA";
    public static String table = "BUS_LINE_STATION";
    private Map<String,Map<String,Line>> nameLines = new HashMap<String, Map<String, Line>>();
    private Map<String,List<Line>> stationNameLines = new HashMap<String, List<Line>>();
    public void readData(){
        System.out.println("从hana 中加载数据...");
        Connection conn = null;
        try {
            conn = HanaConnectionPool.getInstance().getConnection();
        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            Statement statement = conn.createStatement();
            String sql = "SELECT * FROM \""+schema+"\".\""+table+"\" ORDER BY LINE_CODE, SERVICE_ID, cast(STATION_NUMBER as INTEGER)";
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String lineCode = resultSet.getString(1);
                String lineName = resultSet.getString(2);
                String lineId = resultSet.getString(3);
                String serviceName = resultSet.getString(4);
                String serviceId = resultSet.getString(5);
                int stationNumber = resultSet.getInt(6);
                String stationName = resultSet.getString(7);
                String stationId = resultSet.getString(8);
                double lng = resultSet.getDouble(9);
                double lat = resultSet.getDouble(10);
                Map<String,Line> lines = nameLines.get(lineName);
                if(lines == null){
                    lines = new HashMap<String, Line>();
                    Line line = new Line();
                    line.setLineCode(lineCode);
                    line.setLineName(lineName);
                    line.setServiceId(serviceId);
                    Station station = new Station();
                    station.setId(stationId);
                    station.setSequence(stationNumber+"");
                    station.setName(stationName);
                    Point point = new Point();
                    point.setLat(lat);
                    point.setLng(lng);
                    station.setPoint(point);
                    line.addStation(station);
                    lines.put(serviceId,line);
                    nameLines.put(lineName,lines);
                }else{
                    Line line = lines.get(serviceId);
                    if(line == null){
                        line = new Line();
                        line.setLineCode(lineCode);
                        line.setLineName(lineName);
                        line.setServiceId(serviceId);
                        Station station = new Station();
                        station.setId(stationId);
                        station.setSequence(stationNumber+"");
                        station.setName(stationName);
                        Point point = new Point();
                        point.setLat(lat);
                        point.setLng(lng);
                        station.setPoint(point);
                        line.addStation(station);
                        lines.put(serviceId,line);
                    }else{
                        Station station = new Station();
                        station.setId(stationId);
                        station.setSequence(stationNumber+"");
                        station.setName(stationName);
                        Point point = new Point();
                        point.setLat(lat);
                        point.setLng(lng);
                        station.setPoint(point);
                        line.addStation(station);
                    }
                }
            }
            HanaConnectionPool.getInstance().closeConnection(conn);

        } catch (SQLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        try {
            HanaConnectionPool.getInstance().closeConnection(conn);
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Map<String,Map<String,Line>> getNameLines(){
        return this.nameLines;
    }

    public static List<Map<String,List<Line>>> genStationNameLines(Map<String,Map<String, dataHana.Line>> hanaLines){
        Map<String,List<Line>> startNamesLines = new HashMap<String, List<Line>>();
        Map<String,List<Line>> endNamesLines = new HashMap<String, List<Line>>();
        List<Map<String,List<Line>>> list = new ArrayList<Map<String, List<Line>>>();
        for (String name:hanaLines.keySet()
             ) {
            Map<String,Line> serviceLines = hanaLines.get(name);
            for (String serviceId:serviceLines.keySet()
                 ) {
                Line line = serviceLines.get(serviceId);
                String startName = line.getStartStation().getName();
                List<Line> startLines = startNamesLines.get(startName);
                if(startLines == null){
                    startLines = new ArrayList<Line>();
                    startNamesLines.put(startName,startLines);
                }
                startLines.add(line);
                String endName = line.getEndStation().getName();
                List<Line> endLines = endNamesLines.get(endName);
                if(endLines == null){
                    endLines = new ArrayList<Line>();
                    endNamesLines.put(endName,endLines);
                }
                endLines.add(line);
            }
        }
        list.add(startNamesLines);
        list.add(endNamesLines);
        return list;
    }

}
