import data.Lines;
import org.json.JSONArray;
import org.json.JSONObject;
import util.HttpRequest;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Main {
    public static void main(String[] args){

        System.getProperties().setProperty("http.proxyHost", "proxy.pal.sap.corp");
        System.getProperties().setProperty("http.proxyPort", "8080");
        Lines lines = new Lines();
        lines.readNameFromFile();
//        lines.crawLineData();
        lines.loadBusDataFromJson();
        lines.convertPairs();
        lines.writeToCsv();
//        lines.parseLineJson(busData);
//        lines.convertPairs();

    }
}
