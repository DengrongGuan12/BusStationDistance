import data.Line;
import data.Lines;
import data.Mapping;
import dataHana.BusLineStation;
import org.json.JSONArray;
import org.json.JSONObject;
import util.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Main {
    public static void main(String[] args){

//        System.getProperties().setProperty("http.proxyHost", "proxy.pal.sap.corp");
//        System.getProperties().setProperty("http.proxyPort", "8080");

        Lines lines = new Lines();
//        lines.readNameFromFile();
        lines.loadBusDataFromJson("busLineAmap1234.json");
        lines.convertPairs();
        BusLineStation busLineStation = new BusLineStation();
        busLineStation.readData();
//        lines.matchLineData(busLineStation.getNameLines());

        Mapping mapping = new Mapping();
        mapping.loadMapData("map.csv",";",true);
        lines.writeToHana(mapping,"BUS_LINE_STATION_DISTANCE","SAP_TRAFFIC_DATA");

//
//        lines.writeToCsv();


    }
}
