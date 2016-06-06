package dataHana;

import data.Station;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by I322233 on 6/3/2016.
 */
public class Line {
    private String lineName;
    private String lineCode;
    private String serviceId;

    public String getServiceCode() {
        return serviceCode;
    }

    public void setServiceCode(String serviceCode) {
        this.serviceCode = serviceCode;
    }

    private String serviceCode;

    private List<Station> stations = new ArrayList<Station>();

    public void addStation(Station station){
        stations.add(station);
    }
    

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getLineCode() {
        return lineCode;
    }

    public void setLineCode(String lineCode) {
        this.lineCode = lineCode;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public Station getStartStation(){
        return stations.get(0);
    }
    public Station getEndStation(){
        return stations.get(stations.size()-1);
    }


}
