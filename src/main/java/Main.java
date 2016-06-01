import data.Lines;
import org.json.JSONArray;
import org.json.JSONObject;
import util.HttpRequest;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Main {
    public static void main(String[] args){
        String url = "http://ditu.amap.com/service/poiInfo";
        String params = "query_type=TQUERY&city=320100&keywords=1%E8%B7%AF&pagesize=20&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&geoobj=118.637443%7C32.033037%7C118.912102%7C32.10577";
        Map<String,String> headers = new HashMap<String, String>();
        String res = HttpRequest.sendGet(url,params,headers);
//        System.out.println(res);
        JSONObject jsonObject = new JSONObject(res);
        JSONObject busData = jsonObject.optJSONObject("busData");

        Lines lines = new Lines();
        lines.parseLineJson(busData);
        lines.convertPairs();

    }
}
