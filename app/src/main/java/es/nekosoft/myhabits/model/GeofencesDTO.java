package es.nekosoft.myhabits.model;

/**
 * Created by ladysun on 4/18/2016.
 */
public class GeofencesDTO {

    public static final int TYPE_UNIVERSITY = 101;
    public static final int TYPE_PARKING = 102;
    public static final int TYPE_SUPERMARKET = 103;
    public static final int TYPE_CAFEBAR = 104;
    public static final int TYPE_RESTAURANT = 105;
    public static final int TYPE_LIBRARY = 106;
    public static final int TYPE_TRAM = 107;

    public static final float RADIUS_SMALL = 10;
    public static final float RADIUS_NORMAL = 100;
    public static final float RADIUS_LARGE = 200;

    private String id;
    private double latitude;
    private double longitude;
    private float radius;
    private int type;

    public GeofencesDTO(String id, double latitude, double longitude, float radius, int type) {

        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
