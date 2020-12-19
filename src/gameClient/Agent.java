package gameClient;

import api.directed_weighted_graph;
import api.node_data;
import gameClient.util.Point3D;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/** This class represent a agent in a pokemon game, that is goal is to eat the pokemon that exist in the game. */
 public class Agent {
    private int id;
    private double value;
    private Point3D pos;
    private int src_id;
    private int dest_id;
    private double distance_fromSrc;
    private double speed;

    public Agent(int id, double value, int src, int dest, double speed, Point3D pos) {
        this.id = id;
        this.value = value;
        this.src_id = src;
        this.dest_id = dest;
        this.speed = speed;
        this.pos = pos;
    }

    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }
    public void setValue(double value) {
        this.value = value;
    }

    public Point3D getPos() {
        return pos;
    }
    public void setPos(Point3D pos) {
        this.pos = pos;
    }

    public int getSrc_id() {
        return src_id;
    }
    public void setSrc_id(int src_id) {
        this.src_id = src_id;
    }

    public int getNextNode() {
        return dest_id;
    }
    public void setNextNode(int dest_id) {
        this.dest_id = dest_id;
    }

    public double getDistance_fromSrc() {
        return distance_fromSrc;
    }
    public void setDistance_fromSrc(double distance_fromSrc) {
        this.distance_fromSrc = distance_fromSrc;
    }

    public double getSpeed() {
        return speed;
    }
    public void setSpeed(double speed) {
        this.speed = speed;
    }

}
