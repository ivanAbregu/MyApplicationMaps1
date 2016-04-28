package com.example.igabregu.myapplicationmaps1;

import org.json.JSONObject;

/**
 * Created by igabregu on 26/04/2016.
 * clase que representa un item
 */
public class classItem
{


    private  Double locationLatitude;
    private Double locationLongitude;
    private int radius;
    private String code;
    private int radiusInMeter;
    private String kind;


    public classItem(Double locationLatitude, Double locationLongitude, String code, int radius, int radiusInMeter, String kind) {
        this.locationLatitude = locationLatitude;
        this.locationLongitude = locationLongitude;
        this.code = code;
        this.radius = radius;
        this.radiusInMeter = radiusInMeter;
        this.kind = kind;
    }

    public classItem(JSONObject json) {
        try{
            JSONObject jsLocation = json.getJSONObject("location");
            this.locationLatitude = jsLocation.getDouble("latitude");
            this.locationLongitude = jsLocation.getDouble("longitude");
            this.radius = json.getInt("radius");
            this.code = json.getString("code");
            this.radiusInMeter =json.getInt("radiusInMeter");
            this.kind = json.getString("kind");

        }catch(Exception ex){
            this.kind="error class "+ex.toString();
        }
    }

    public void setKind(String kind)
    {
        this.kind = kind;
    }

    public String getKind()
    {
        return kind;
    }

    public void setRadiusInMeter(int radiusInMeter)
    {
        this.radiusInMeter = radiusInMeter;
    }

    public int getRadiusInMeter()
    {
        return radiusInMeter;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

    public String getCode()
    {
        return code;
    }

    public void setRadius(int radius)
    {
        this.radius = radius;
    }

    public int getRadius()
    {
        return radius;
    }

    public Double getLocationLatitude() {
        return locationLatitude;
    }

    public void setLocationLatitude(Double locationLatitude) {
        this.locationLatitude = locationLatitude;
    }

    public Double getLocationLongitude() {
        return locationLongitude;
    }

    public void setLocationLongitude(Double locationLongitude) {
        this.locationLongitude = locationLongitude;
    }
}
