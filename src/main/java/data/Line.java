package data;

import org.json.JSONArray;
import org.json.JSONObject;
import util.GisUtil;
import util.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Line {
    public ArrayList<Station> getStations() {
        return stations;
    }

    public void setStations(ArrayList<Station> stations) {
        this.stations = stations;
    }

    public ArrayList<StationPair> getPairs() {
        return pairs;
    }

    public void setPairs(ArrayList<StationPair> pairs) {
        this.pairs = pairs;
    }

    private ArrayList<Station> stations = new ArrayList<Station>();
    private ArrayList<StationPair> pairs = new ArrayList<StationPair>();
    private ArrayList<Point> path = new ArrayList<Point>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private String id;
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name = "";

    public double getTotalLength() {
        return totalLength;
    }

    public void setTotalLength(double totalLength) {
        this.totalLength = totalLength;
    }

    private double totalLength = 0;

    public String getLineString() {
        return lineString;
    }

    public void setLineString(String lineString) {
        this.lineString = lineString;
    }

    private String lineString = "";
    public void addStation(Station station){
        stations.add(station);
    }
    public void addPair(StationPair pair){
        pairs.add(pair);
    }
    public void addPoint(Point point){
        path.add(point);
    }
    public Station getStation(int index){
        return stations.get(index);
    }

    public void convertPairs(){
        for (StationPair pair:pairs
             ) {
            pair.convert(lineString);
            totalLength+=pair.getLength();
        }
        System.out.println("totalLength: "+totalLength);
    }
    public void calculateTotalLength(){
        Station start = stations.get(0);
        Station end = stations.get(stations.size()-1);
        System.out.println("totalLength: "+ GisUtil.calculateLength(lineString,start.getPoint(),end.getPoint()));
    }



}
