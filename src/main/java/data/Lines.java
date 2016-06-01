package data;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Lines {
    private ArrayList<Line> lines = new ArrayList<Line>();
    public void parseLineJson(JSONObject busData){
        for (String lineId:busData.keySet()
                ) {
            Line line = new Line();
            JSONArray singleLine = busData.optJSONArray(lineId);
            JSONObject marker = singleLine.getJSONObject(0);
            JSONObject polyline = singleLine.getJSONObject(1);
            JSONArray markerList = marker.optJSONArray("list");
            for(int i = 0;i<markerList.length();i++){
                JSONObject stationJson = markerList.getJSONObject(i);
                Station station = new Station();
                station.setName(stationJson.optString("name"));
                Point point = new Point();
                point.setLat(stationJson.optJSONObject("location").optDouble("lat"));
                point.setLng(stationJson.optJSONObject("location").optDouble("lng"));
                station.setPoint(point);
                line.addStation(station);
                if(i > 0){
                    Station lastStation = line.getStation(i-1);
                    StationPair pair = new StationPair();
                    pair.setStart(lastStation);
                    pair.setEnd(station);
                    line.addPair(pair);
                }
            }
            JSONArray polylineList = polyline.optJSONArray("list");
            JSONObject polylineDetail = polylineList.getJSONObject(0);
            JSONArray path = polylineDetail.optJSONArray("path");
            String lineString = "LINESTRING(";
            for(int i = 0;i<path.length();i++){
                JSONObject pointJson = path.getJSONObject(i);
                Point point = new Point();
                point.setLng(pointJson.optDouble("lng"));
                point.setLat(pointJson.optDouble("lat"));
                line.addPoint(point);
                lineString+=point.getLng()+" "+point.getLat()+",";
            }
            lineString = lineString.substring(0,lineString.length()-1);
            lineString+=")";
            line.setLineString(lineString);
            lines.add(line);
        }
    }
    public void convertPairs(){
        for (Line line:lines
             ) {
            line.convertPairs();
        }
    }
}
