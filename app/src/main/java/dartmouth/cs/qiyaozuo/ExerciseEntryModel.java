package dartmouth.cs.qiyaozuo;


import android.util.Log;

import java.text.DecimalFormat;

public class ExerciseEntryModel {
    private static final String TAG = "FLOAT";
    private long id = 0;
    private String comment = "";
    private String date = "";
    private String type;

    DecimalFormat df = new DecimalFormat("0.0000");

    private String inputType = "";
    private String unit = "";
    private float distance = 0;
    private int calories, heartRate = 0;
    private float duration;
    private float speed;
    private String location = "";

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getClimb() {
        return climb;
    }

    public void setClimb(float climb) {
        this.climb = climb;
    }

    private float climb = 0;

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getDuration() {
        return duration;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public int getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(int heartRate) {
        this.heartRate = heartRate;
    }


    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String toString(Boolean isKm, String unit) {
        //convert float duration to minutes and seconds
        int min = (int) duration;
        int sec = (int) ((duration - (float) min) * 60);

        //convert between imperial and metric
        if (isKm) {
            //is kms display
            if (unit.equals("imperial")) {
                return df.format(distance * 1.6) + " KM,"
                        + min + " minutes "
                        + sec + " secs";
            } else {
                return df.format(distance) + " KM,"
                        + min + " minutes "
                        + sec + " secs";
            }

        } else {
            //is miles display
            if (unit.equals("metric")) {
                return df.format(distance / 1.6) + " Miles,"
                        + min + " minutes "
                        + sec + " secs";
            } else {
                return df.format(distance) + " Miles,"
                        + min + " minutes "
                        + sec + " secs";
            }

        }

    }
}
