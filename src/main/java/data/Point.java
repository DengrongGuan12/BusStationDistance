package data;

/**
 * Created by I322233 on 6/1/2016.
 */
public class Point {
    private double lat;

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    private double lng;

    public String toString(){
        String str = "POINT("+lng+" "+lat+")";
        return str;
    }

}
