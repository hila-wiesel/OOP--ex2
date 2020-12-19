package api;

//import java.util.HashMap;

/**
 * This class implement the interface node_data.
 * Each object that created in this class has private fields: key, neighbors; info and tag.
 * Using the function: getKey(), getInfo(), setInfo(),  getLocation(), setLcation(), getWeight(), setWeight(), getTag() and setTag().
 */
public class NodeData implements  node_data{

    private int key;
    private String info;
    private int tag;
    private geo_location location;
    private double weight;
    static int numNode=0;


    public NodeData(){
        key=numNode++;
        info = "";
        tag = 0;
        location = new GeoLocation(0,0,0);
        weight =0.0;
        }

    public NodeData(node_data n){
       key = n.getKey();
       info = n.getInfo();
       tag = n.getTag();
       location = new GeoLocation(n.getLocation());
       weight = n.getWeight();
    }

    public NodeData(int k, String i, int t, geo_location l, double w ){
        key = k;
        info = i;
        tag = t;
        location =l;
        weight =w;
    }



    @Override
    public int getKey() {
        return key;
    }

    @Override
    public geo_location getLocation() {     ///
        return location;
    }

    @Override
    public void setLocation(geo_location p) {     ///
        location=p;
    }

    @Override
    public double getWeight() {
        return weight;
    }

    @Override
    public void setWeight(double w) {
        weight=w;
    }

    @Override
    public String getInfo() {
        return info;
    }

    @Override
    public void setInfo(String s) {
        info=s;
    }

    @Override
    public int getTag() {
        return tag;
    }

    @Override
    public void setTag(int t) {
        tag=t;
    }

/*
    @Override
    public int compareTo(node_data other) {
        if(this.equals(other))
            return 0;
        else if(this.tag > other.getTag())
            return 1;
        else return -1;
    }*/

    @Override
    public String toString() {
        return "NodeData{" +
                "key=" + key +
                ", info='" + info + '\'' +
                ", tag=" + tag +
                ", location=" + location +
                ", weight=" + weight +
                '}';
    }
}
