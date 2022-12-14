package com.grandma.buzzmate.modules;

    public class Coordinates {
        private double lat;
        private double lon;

        public Coordinates() {
        }

        public double getLat() {
            return lat;
        }

        public void setLat(double lat) {
            this.lat = lat;
        }

        public double getLon() {
            return lon;
        }

        public void setLon(double lon) {
            this.lon = lon;
        }

        @Override
        public String toString() {
            return "Coordinates{" +
                    "lat=" + lat +
                    ", lon=" + lon +
                    '}';
        }
    }
