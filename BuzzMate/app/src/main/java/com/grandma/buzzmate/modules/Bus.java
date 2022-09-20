package com.grandma.buzzmate.modules;

public class Bus {
    private String busID;
    private double Latitude;
    private double Longitude;
    private int nextStop;

    public Bus() {
    }

    public String getBusID() {
        return busID;
    }

    public void setBusID(String busID) {
        this.busID = busID;
    }

    @Override
    public String toString() {
        return "Bus{" + "busID='" + busID + "' Latitude='" + Latitude + "' Longitude='" + Longitude + '\'' + "' nextStop='"+nextStop+ "'}";
    }

    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public int getNextStop() {
        return nextStop;
    }

    public void setNextStop(int nextStop) {
        this.nextStop = nextStop;
    }
}
