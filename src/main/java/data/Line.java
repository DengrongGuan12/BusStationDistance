package data;

import java.util.ArrayList;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Line {
    private ArrayList<Station> stations = new ArrayList<Station>();
    private ArrayList<StationPair> pairs = new ArrayList<StationPair>();
    private ArrayList<Point> path = new ArrayList<Point>();

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

}
