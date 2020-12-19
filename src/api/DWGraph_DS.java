package api;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

/**
 * This class implement the interface of directed weighted graph.
 * Each object that created in this class has private fields: nodes, edgesOut ,edgesIn, numEdge and numNode.
 * and ModeCount, that keeps the number of changes that happened in the graph.
 * Using the function: getNode(), getEdge(), addNode(), connect(), getV(),getE(), removeNode(), removeEdge(), nodeSize(), edgeSize() and getMC()
 */

public class DWGraph_DS implements directed_weighted_graph{
    private HashMap<Integer, node_data> nodes;     //  Integer = key
    private  HashMap<Integer, HashMap<Integer,edge_data>> edgesOut;   // < src key , <dest key, edge>>
    private  HashMap<Integer, HashMap<Integer,Integer>> edgesIn;   // < dest key , <src key, dest key>>

    private int numEdge;
    private int numNode;
    private int ModeCount = 0;


    public DWGraph_DS(){
        nodes =  new HashMap<>();
        edgesOut = new HashMap<>();
        edgesIn = new HashMap<>();
        numEdge=0;
        numNode=0;
        ModeCount = 0;
    }

    public DWGraph_DS( directed_weighted_graph g){
        numEdge=0;
        numNode=0;
        ModeCount = 0;
        nodes =  new HashMap<>();
        edgesOut = new HashMap<>();
        edgesIn = new HashMap<>();

        Collection<node_data> nodesCol = g.getV();
        Iterator<node_data> iter1 = nodesCol.iterator();
        while(iter1.hasNext()) {
            node_data temp = new NodeData(iter1.next());
            this.addNode(temp);
        }
        Iterator<node_data> iter2 = nodesCol.iterator();
        while(iter2.hasNext()) {
            node_data temp = iter2.next();
            Collection<edge_data> tempE = g.getE(temp.getKey());
            Iterator<edge_data> iter3 = tempE.iterator();
            while(iter3.hasNext()){
                edge_data edge = new EdgeData(iter3.next());
                int src = edge.getSrc();
                int dest = edge.getDest();
                edgesOut.get(src).put(dest,edge);
                HashMap<Integer,Integer> m = edgesIn.get(dest);
                m.put(src,dest);
                edgesIn.put(dest,m);
                ++numEdge;

            }
        }
    }


    @Override
    public node_data getNode(int key) {
        if (!nodes.containsKey(key))
            return null;
        return nodes.get(key);
    }

    @Override
    public edge_data getEdge(int src, int dest) {   //
        HashMap<Integer,edge_data> ni = edgesOut.get(src);
        if (!ni.containsKey(dest))
            return null;
        return ni.get(dest);
    }

    @Override
    public void addNode(node_data n) {
        nodes.put(n.getKey(),n);
        edgesOut.put(n.getKey(),new HashMap<>());
        edgesIn.put(n.getKey(), new HashMap<>());
        ++ModeCount;
        ++numNode;
    }

    @Override
    public void connect(int src, int dest, double w) {
        if(src==dest || w<0) { return;}
        if(!nodes.containsKey(src) || !nodes.containsKey(dest)) { return;}
        edge_data e = getEdge(src,dest);
        if(e!=null){    //  there is an edge between them
            if(e.getWeight() ==w) {
                return;
            }    //this edge is already exist
            --numEdge;
        }
        edge_data newEdge = new EdgeData(src, dest, w);
        edgesOut.get(src).put(dest,newEdge);
        edgesIn.get(dest).put(src,dest);
        ++ModeCount;
        ++numEdge;
    }

    @Override
    public Collection<node_data> getV() {
        return nodes.values();  //?
    }

    @Override
    public Collection<edge_data> getE(int node_id) {
        if (!nodes.containsKey(node_id))
            return null;
        return edgesOut.get(node_id).values();
    }

    @Override
    public node_data removeNode(int key) {
        if(nodes.containsKey(key)) {
            node_data deleteNode = nodes.remove(key); 			//remove the node from the graph
            --numNode;
            ++ModeCount;
            //this node might has outgoing edges as src, lets remove its:
            int numEdgeDelete = edgesOut.get(key).size();
            edgesOut.remove(key);  //remove the node from the outgoing edges
            //this node might has entered edges as dest, lets remove its:
            numEdgeDelete = numEdgeDelete + edgesIn.get(key).size();
            edgesIn.remove(key);  //remove the node from the entered edges

            numEdge = numEdge-numEdgeDelete;
            ModeCount = ModeCount-numEdgeDelete;
            return deleteNode;
        }
        return null;

    }

    @Override
    public edge_data removeEdge(int src, int dest) {
        if(!edgesOut.get(src).containsKey(dest))    //  this edge doesnt exist
            return null;
        --numEdge;
        ++ModeCount;
        return edgesOut.get(src).remove(dest);
    }

    @Override
    public int nodeSize() {
        return numNode;
    }

    @Override
    public int edgeSize() {
        return numEdge;
    }

    @Override
    public int getMC() {
        return ModeCount;
    }

    @Override
    public String toString() {
        return "DWֹGraph_DS{" +
                "nodes=" + nodes +
                ", edgesOut=" + edgesOut +
                ", edgesIn=" + edgesIn +
                ", numEdge=" + numEdge +
                ", numNode=" + numNode +
                ", ModeCount=" + ModeCount +
                '}';
    }
}
