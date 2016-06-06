package data;

import dataHana.*;
import dataHana.Line;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * Created by I322233 on 6/6/2016.
 */
public class Mapping {
    //lineid->line in hana
    private Map<String, dataHana.Line> map = new HashMap<String, Line>();
    public void loadMapData(String fileName,String deli, boolean headExist){
        System.out.println("从csv文件加载mapping数据...");
        String fullFileName = "src/main/resources/"+fileName;

        File file = new File(fullFileName);
        Scanner scanner = null;
        int i = 0;
        try {
            scanner = new Scanner(file, "utf-8");
            while (scanner.hasNextLine()) {
                if(headExist && i==0){
                    String line = scanner.nextLine();
                    i++;
                    continue;
                }
                String line = scanner.nextLine();
                String[] strings = line.split(deli);
                String lineId = strings[0];
                String lineCode = strings[1];
                String serviceId = strings[2];
                dataHana.Line line1 = new Line();
                line1.setServiceId(serviceId);
                line1.setLineCode(lineCode);
                map.put(lineId,line1);
            }

        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block

        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }
    public Line getLine(String lineId){
        return map.get(lineId);
    }
}
