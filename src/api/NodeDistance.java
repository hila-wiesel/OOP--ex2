package api;

import java.util.HashMap;

public class NodeDistance implements Comparable<NodeDistance>{
    private int key;
    private double distance;
    static HashMap<Integer,NodeDistance> allDis;

    public NodeDistance(int key){
        this.key=key;
        distance=Integer.MAX_VALUE;
        allDis=new HashMap<>();
        allDis.put(key,this);       //////////////delete?
    }

    public static NodeDistance getDisObject(int key){
        return allDis.get(key);

    }

    public int getKey() {
        return key;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public double getDistance() {
        return distance;
    }


    @Override
    public int compareTo(NodeDistance other) {
        if(this.equals(other))
            return 0;
        else if(this.distance > other.getDistance())
            return 1;
        else return -1;
    }
}
