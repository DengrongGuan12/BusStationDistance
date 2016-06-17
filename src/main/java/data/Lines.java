package data;

import dataHana.BusLineStation;
import db.HanaConnectionPool;
import org.json.JSONArray;
import org.json.JSONObject;
import util.HttpRequest;

import java.io.*;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Lines {
    private static String url = "http://ditu.amap.com/service/poiInfo";
    private ArrayList<String> lineNames = new ArrayList<String>();
    private ArrayList<Line> lines = new ArrayList<Line>();

    public void matchLineData(Map<String,Map<String, dataHana.Line>> hanaLines){
        List<Line> noMatchedGaodeLines = new ArrayList<Line>();
        List<dataHana.Line> findedLines = new ArrayList<dataHana.Line>();
//        System.out.println("根据名称寻找匹配....");
        for (Line line:lines
             ) {
            Station gaodeStart = line.getStartStation();
            Station gaodeEnd = line.getEndStation();
            String name = line.getName();
            String[] names = name.split("\\(");
            Map<String, dataHana.Line> map = hanaLines.get(names[0]);
            if(map == null){
//                System.out.println("高德道路 "+name+" 根据名称未找到匹配!");
                noMatchedGaodeLines.add(line);
            }else{
                for (String serviceId: map.keySet()
                     ) {
                    dataHana.Line hanaLine = map.get(serviceId);
                    Station hanaStart = hanaLine.getStartStation();
                    Station hanaEnd = hanaLine.getEndStation();
                    if(hanaStart.getName().equals(gaodeStart.getName()) || hanaEnd.getName().equals(gaodeEnd.getName())){
//                        System.out.println("高德道路 "+name+" 根据名称找到匹配: serviceId 为 "+serviceId+" ; lineCode 为 "+hanaLine.getLineCode());
                        //删除匹配成功的
                        System.out.println(line.getId()+";"+hanaLine.getLineCode()+";"+hanaLine.getServiceId());
                        map.remove(serviceId);
                        if(map.keySet().size() == 0){
                            hanaLines.remove(names[0]);
                        }
                        break;
                    }
                }
            }
        }
//        System.out.println("---------------------------------------------------根据起点终点寻找匹配....----------------------------------------------");
        List<Map<String,List<dataHana.Line>>> list = BusLineStation.genStationNameLines(hanaLines);
        Map<String,List<dataHana.Line>> startNames = list.get(0);
        Map<String,List<dataHana.Line>> endNames = list.get(1);
        int num = 0;
        for (Line line:noMatchedGaodeLines
             ) {
            String name = line.getName();
            String startName = "";
            String endName = "";
            startName = line.getStartStation().getName();
            endName = line.getEndStation().getName();
//            System.out.println("查找: "+name+" "+startName+"--"+endName);

            List<dataHana.Line> lines = startNames.get(startName);
            if (lines != null){
                boolean findMatch = false;
                for (dataHana.Line hanaLine:lines
                     ) {
//                    System.out.println("根据起点找到 "+hanaLine.getLineName()+" 终点为: "+hanaLine.getEndStation().getName());
                    if (hanaLine.getEndStation().getName().equals(endName) || hanaLine.getEndStation().getName().equals(endName+"1")){
//                        System.out.println("高德道路 "+line.getName()+" 根据起点找到匹配: serviceId 为 "+hanaLine.getServiceId()+" ; lineCode 为 "+hanaLine.getLineCode());
                        findMatch = true;
                        System.out.println(line.getId()+";"+hanaLine.getLineCode()+";"+hanaLine.getServiceId());
                        break;
                    }
                }
                if(findMatch){
                    continue;
                }
            }
            List<dataHana.Line> endLines = endNames.get(endName);
            if(endLines != null){
                boolean findMatch = false;
                for (dataHana.Line hanaLine:endLines
                        ) {
//                    System.out.println("根据终点找到 "+hanaLine.getLineName()+" 终点为: "+hanaLine.getEndStation().getName());
                    if (hanaLine.getStartStation().getName().equals(startName) || hanaLine.getStartStation().getName().equals(startName+"1")){
//                        System.out.println("高德道路 "+line.getName()+" 根据终点找到匹配: serviceId 为 "+hanaLine.getServiceId()+" ; lineCode 为 "+hanaLine.getLineCode());
                        findMatch = true;
                        System.out.println(line.getId()+";"+hanaLine.getLineCode()+";"+hanaLine.getServiceId());
                        break;
                    }
                }
                if(findMatch){
                    continue;
                }
            }
            num++;
//            System.out.println("高德道路 "+line.getName()+" 根据起点终点未找到匹配!");
        }
//        System.out.println("高德总线路数: "+lines.size());
//        System.out.println("未在hana 中找到匹配的数量: "+num);

    }

    public void parseLineJson(JSONObject busData){
        for (String lineId:busData.keySet()
                ) {
            Line line = new Line();
            line.setId(lineId);
            JSONArray singleLine = busData.optJSONArray(lineId);
            JSONObject marker = singleLine.getJSONObject(0);
            JSONObject polyline = singleLine.getJSONObject(1);
            JSONArray markerList = marker.optJSONArray("list");
            for(int i = 0;i<markerList.length();i++){
                JSONObject stationJson = markerList.getJSONObject(i);
                Station station = new Station();
                station.setId(stationJson.optString("id"));
                station.setSequence(stationJson.optString("sequence"));
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
            line.setName(polylineDetail.optString("key_name"));
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
        System.out.println("pair 转换...");
        for (Line line:lines
             ) {
            line.convertPairs();
            line.calculateTotalLength();
        }
    }

    public void readNameFromFile(){
        File file = new File("src/main/resources/linenames.txt");
        try {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            String line = "";
            while ((line = bufferedReader.readLine())!=null){
                lineNames.add(line.trim());
            }
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void crawLineData(){
        for(String name:lineNames){
            String encodeLineName = null;
            try {
                encodeLineName = URLEncoder.encode(name,"utf-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            String params = "query_type=TQUERY&city=320100&keywords="+encodeLineName+"&pagesize=20&pagenum=1&qii=true&cluster_state=5&need_utd=true&utd_sceneid=1000&div=PC1000&addr_poi_merge=true&is_classify=true&geoobj=118.637443%7C32.033037%7C118.912102%7C32.10577";
            Map<String,String> headers = new HashMap<String, String>();
            String res = HttpRequest.sendGet(url,params,headers);
            System.out.println(res);
            JSONObject jsonObject = new JSONObject(res);
            JSONObject busData = jsonObject.optJSONObject("busData");
            if(busData == null){
                continue;
            }
            parseLineJson(busData);
            break;
        }
//        JSONObject jsonObject = new JSONObject(resp);
//        JSONObject busData = jsonObject.optJSONObject("busData");
//        parseLineJson(busData);
    }

    public void loadBusDataFromJson(String filename){
        System.out.println("从json文件加载数据...");
        String fullFileName = "src/main/resources/"+filename;

        File file = new File(fullFileName);
        Scanner scanner = null;
        StringBuilder buffer = new StringBuilder();
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                buffer.append(scanner.nextLine());
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block

        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        JSONArray jsonArray = new JSONArray(buffer.toString());
        parseLinesArrayJson(jsonArray);

    }
    public void parseLinesArrayJson(JSONArray jsonArray){
        for (int j =0;j<jsonArray.length();j++) {
            JSONObject busData = jsonArray.getJSONObject(j);
            Line line = new Line();
            line.setId(busData.optString("id"));
            System.out.println("line_id:"+line.getId());
            JSONArray stopList = busData.optJSONArray("via_stops");
            for(int i = 0;i<stopList.length();i++){
                JSONObject stationJson = stopList.getJSONObject(i);
                Station station = new Station();
                station.setId(stationJson.optString("id"));
                station.setSequence(stationJson.optString("sequence"));
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
//            JSONArray polylineList = polyline.optJSONArray("list");
//            JSONObject polylineDetail = polylineList.getJSONObject(0);
            JSONArray path = busData.optJSONArray("path");
            line.setName(busData.optString("name"));
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

    public void writeToCsv(){
        try {
            String lineInfoPath = "src/main/resources/line_info.csv";
            String pairInfoPath = "src/main/resources/pair_info.csv";
            String stationInfoPath = "src/main/resources/station_info.csv";
            File lineInfo = new File(lineInfoPath);
            File pairInfo = new File(pairInfoPath);
            File stationInfo = new File(stationInfoPath);

            if (!lineInfo.exists()) {
                lineInfo.createNewFile();
            }
            if(!pairInfo.exists()){
                pairInfo.createNewFile();
            }
            if(!stationInfo.exists()){
                stationInfo.createNewFile();
            }
            FileOutputStream lineOutputStream = new FileOutputStream(lineInfo);
            FileOutputStream pairOutputStream = new FileOutputStream(pairInfo);
            FileOutputStream stationOutputStream = new FileOutputStream(stationInfo);

            BufferedWriter lineOutput = new BufferedWriter(new OutputStreamWriter(lineOutputStream));
            BufferedWriter pairOutput = new BufferedWriter(new OutputStreamWriter(pairOutputStream));
            BufferedWriter stationOutput = new BufferedWriter(new OutputStreamWriter(stationOutputStream));

            lineOutput.write("id;name;length;linestring\r\n");
            pairOutput.write("line_id;length;from_station;to_station;linestring\r\n");
            stationOutput.write("id;line_id;lat;lng;sequence;name\r\n");

            for (Line line:lines
                 ) {
                lineOutput.write(line.getId()+";"+line.getName()+";"+line.getTotalLength()+";"+line.getLineString()+"\r\n");
                for(Station station:line.getStations()){
                    stationOutput.write(station.getId()+";"+line.getId()+";"
                            +station.getPoint().getLat()+";"+station.getPoint().getLng()+";"+station.getSequence()+";"+station.getName()+"\r\n");
                }
                for(StationPair pair:line.getPairs()){
                    pairOutput.write(line.getId()+";"+pair.getLength()+";"+pair.getStart().getId()+";"+pair.getEnd().getId()+";"+pair.getLineString()+"\r\n");
                }

            }

            lineOutput.close();
            pairOutput.close();
            stationOutput.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeToHana(Mapping map,String table,String schema){
        System.out.println("向hana 导入数据...");
        Connection connection = null;
        try {
            connection = HanaConnectionPool.getInstance().getConnection();
            String truncateSql = "truncate table \""+schema+"\".\""+table+"\"";
            Statement statement = connection.createStatement();
            statement.execute(truncateSql);
            statement.close();
            String sql = "INSERT INTO \""+schema+"\".\""+table+"\" VALUES(?,?,?,?,?,?)";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (Line line:lines
                    ) {
                String lineId = line.getId();
                dataHana.Line hanaLine = map.getLine(lineId);
                if(hanaLine == null){
                    continue;
                }
                String lineCode = hanaLine.getLineCode();
                String serviceId = hanaLine.getServiceId();
                for(StationPair pair:line.getPairs()){
                    preparedStatement.setString(1,lineCode);
                    preparedStatement.setString(2,serviceId);
                    preparedStatement.setString(3,pair.getStart().getSequence());
                    preparedStatement.setString(4,pair.getEnd().getSequence());
                    preparedStatement.setDouble(5,pair.getLength());
                    preparedStatement.setString(6,pair.getLineString());
                    preparedStatement.execute();
                }
            }
            HanaConnectionPool.getInstance().closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


}
