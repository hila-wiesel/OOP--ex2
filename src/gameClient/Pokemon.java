package gameClient;

import api.*;
import gameClient.util.Point3D;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.TreeMap;

public class Pokemon implements Comparable<Pokemon>{
    public static final double EPS1 = 0.001, EPS2=EPS1*EPS1, EPS=EPS2;
    private edge_data _edge;
    private double _value;
    private int _type;
    private Point3D _pos;
    private int dest_id;
    private int src_id;
    private double distance_fromSrc;
    private Boolean agentOnTheWay;

    public Pokemon(double _value, int _type, Point3D _pos){
        this._value = _value;
        this._type = _type;
        this._pos = _pos;
        this.agentOnTheWay=false;
    }


    /**Return true if anf only if there is an agent on the way to eat this pokemon*/
    public Boolean getAgentOnTheWay() {
        return agentOnTheWay;
    }

    /**Allow to change the boolean value in "AgentOnTheWay", use by algorithms, in the accurate time ( after agent set to go toward this pokemon).*/
    public void setAgentOnTheWay(Boolean agentOnTheWay) {
        this.agentOnTheWay = agentOnTheWay;
    }

    /**Return the value of this pokemon*/
    public double getValue() {
        return _value;
    }

    /**Return the type of this pokemon- for knowing which side on the edge the pokemon stand (directed graph)
     *  1 if he is on the edge fron the lower id (node) to the higher id (node).  -1 for the reversed edge.
     *  */
    public int getType() {
        return _type;
    }

    public Point3D getPos() {
        return _pos;
    }

    public edge_data getEdge() {
        return _edge;
    }

    public void setEdge(edge_data _edge) {
        this._edge = _edge;
    }

    public int getDest_id() {
        return dest_id;
    }
    public void setDest_id(int dest_id) {
        this.dest_id = dest_id;
    }

    public int getSrc_id() {
        return src_id;
    }
    public void setSrc_id(int src_id) {
        this.src_id = src_id;
    }

    public double getDistance_fromSrc() {
        return distance_fromSrc;
    }
    public void setDistance_fromSrc(double distance_fromSrc) {
        this.distance_fromSrc = distance_fromSrc;
    }

    /** Find which edge this pokemon stand on. by using function "isOnEdge" for all the edges of this graph*/
    public edge_data findEdge (directed_weighted_graph graph){
        int i=0;
        for (node_data n : graph.getV()){
            for (edge_data e : graph.getE(n.getKey())){
                if(this.isOnEdge(e.getSrc(), e.getDest(), graph)){
                    if(((this.getType()==1) && (e.getSrc()<e.getDest())) || ((this.getType()==-1) && (e.getSrc()>e.getDest()))){
                        return e;
                    }
                }
            }
        }
        return null;
    }

    /** Return true or false whether this pokemon stand in this edge*/
    private boolean isOnEdge( geo_location src, geo_location dest ) {
        geo_location p = this._pos;
        boolean ans = false;
        double dist = src.distance(dest);
        double d1 = src.distance(p) + p.distance(dest);
        if(d1 <= dist+EPS2) {ans = true;}
        //System.out.println(ans);
        return ans;
    }
    private boolean isOnEdge( int s, int d, directed_weighted_graph g) {
        geo_location p = this._pos;
        geo_location src = g.getNode(s).getLocation();
        geo_location dest = g.getNode(d).getLocation();
        return this.isOnEdge(src,dest);
    }

    @Override
    public int compareTo(@NotNull Pokemon other) {
        if(this.equals(other))
            return 0;
        else if(this.getValue() > other.getValue())
            return -1;
        else return 1;
    }
    @Override
    public String toString() {
        return "Pokemon{" +
                "_edge=" + _edge +
                ", _value=" + _value +
                ", _type=" + _type +
                ", _pos=" + _pos +
                ", dest_id=" + dest_id +
                ", src_id=" + src_id +
                ", distance_fromSrc=" + distance_fromSrc +
                //", graph=" + graph +
                '}';
    }
}


