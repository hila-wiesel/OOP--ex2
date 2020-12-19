package api;

import java.util.HashMap;

public class Pair_ParentDistance{
    private HashMap<Integer, Integer> left;
    private HashMap<Integer, NodeDistance> right;

    public Pair_ParentDistance(HashMap<Integer, Integer> left, HashMap<Integer, NodeDistance> right){
        this.right = right;
        this.left = left;
    }

    public HashMap<Integer, Integer> getLeft(){
        return left;
    }

    public HashMap<Integer, NodeDistance> getRight(){
        return right;
    }




}
