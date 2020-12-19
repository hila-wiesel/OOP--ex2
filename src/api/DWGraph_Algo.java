package api;

import com.google.gson.*;
import java.io.*;
import java.util.*;


/**
 * This class implement the interface of graph algorithms.
 * Each object that created in this class has private field - graph, that the object can do changes on it.
 * Using the function: init(), copy(), isConnected(), shortestPathDist(), shortestPath(),
 * saving to a file and load a graph from a file.
 */
public class DWGraph_Algo implements dw_graph_algorithms{
    private directed_weighted_graph graph;



    @Override
    /** initialize the graph_algorithms object a specific graph to work on */
    public void init(directed_weighted_graph g) {
        this.graph = g;

    }

    @Override
    public directed_weighted_graph getGraph() {
        return graph;
    }

    @Override
    /** Making a deep copying for the graph, by copy constructor that coping all the nodes first, and than creating the fitting edges, and fitting goe_location objects. */
    public directed_weighted_graph copy() {
        directed_weighted_graph copy = new DWGraph_DS(this.graph);
        return copy;
    }

    @Override
    /**  This function return whether the graph is connect or not.
     * using the helper function- Dijkstra to get the distance from each node to all of the rest,
     * and checking if there in a node that the distance between them is infinite (no way between them).
     */
    public boolean isConnected() {
        Collection<node_data> nodes = graph.getV();
        if(nodes.size()==0 )        //  empty graph is connected
            return true;
        Iterator<node_data> iterator = nodes.iterator();
        while(iterator.hasNext()) {
            node_data src = iterator.next();
            Pair_ParentDistance pair =Dijkstra(graph,src.getKey());
            HashMap<Integer, NodeDistance> distances= pair.getRight();
            if(distances.size() != nodes.size())
                return false;
        }
        return true;
    }

    @Override
    /** This function return the length of the shortest path from src node to dest node
     * using the helper function- Dijkstra to get the distance from src to dest
     * return -1 if there is no path between the two.
     */
    public double shortestPathDist(int src, int dest) {
        Pair_ParentDistance pair =Dijkstra(graph,src);
        HashMap<Integer, NodeDistance> distances= pair.getRight();
        if(distances.get(dest)==null){   // no path
            return -1;
        }
        return distances.get(dest).getDistance();
    }

    @Override
    /** This function return the shortest path from src node to dest node
     * using the helper function- Dijkstra to get the the previously node from each node that in the shortest path back from dest to src
     */
    public List<node_data> shortestPath(int src, int dest) {
        if (graph.getNode(src) == null || graph.getNode(dest) == null)
            return null;
        Pair_ParentDistance pair = Dijkstra(graph, src);
        HashMap<Integer,Integer> parents = pair.getLeft();
        HashMap<Integer, NodeDistance> distances=pair.getRight();
        if (distances.get(dest)== null) {        //	there is no way to get dest from src
            return null;
        }
        //System.out.println("distance= "+distances.get(dest).getDistance());

        List<node_data> path = new ArrayList<node_data>();
        if (src==dest) {
            path.add(graph.getNode(src));
            return path;
        }

        int p = dest;
        while(parents.get(p) != null) {	 	//there is a node before him in the path
            path.add(graph.getNode(p));
            p = parents.get(p);
        }
        path.add(graph.getNode(p));     //add the lest node (src)

        Collections.reverse(path);
        return path;

    }

    @Override
    /** This function save the graph  in JSON format to a new file in the given string name file. */
    public boolean save(String file) {
        Gson gson = new GsonBuilder().registerTypeAdapter(DWGraph_DS.class, new GraphJson()).create();
        String graphJson = gson.toJson(this.graph);
        try{
            File newFile = new File(file);
            FileWriter fw =new FileWriter(newFile);
            fw.write(graphJson);
            fw.close();
            return true;
        }
        catch (IOException e){
            return false;
        }
    }

    @Override
    /** This function load the details from the given file and create a new graph in accordance to it,
     * and init the graph to the WGraph_Algo. */
    public boolean load(String file) {
        try{
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(directed_weighted_graph.class, new GraphJson());
            Gson gson = builder.create();
            FileReader reader = new FileReader(file);
            directed_weighted_graph newGraph  = gson.fromJson(reader, directed_weighted_graph.class);
            //System.out.println(graph);      //debug?
            this.init(newGraph);        //
            return true;
        }
        catch (IOException e){  //FileNotFoundException
            return false;
       }

    }



    //helper functions:

    /**
     * this private function use in this class for help an other algorithms get the shortest path between two nodes, the shortest path length and decide whether the graph is connect or not
     * by moving from the src node to its neighbors and set their tag (that save distance from src) in accordance to the weight between them,
     * and then moving to each of their neighbors and set their tag accordance to the weight between them plus their "parents" tag cetera.
     * at the same time Dijkstra saves the parent (previously node) for each node, for using in in other function.
     * @param g, src
     * @return Pair_ParentDistance - pair that include hashMap of parents and hashMap of distances
     */
    private Pair_ParentDistance Dijkstra(directed_weighted_graph g, int src){
        restartDis(graph.getV());
        HashMap<Integer, NodeDistance> distances = new HashMap<>();
        HashMap<Integer, NodeDistance> allNewDis = new HashMap<>();
        PriorityQueue<NodeDistance> pQueue = new PriorityQueue<NodeDistance>();
        HashMap<Integer, Integer> parents = new  HashMap<Integer, Integer>();
        parents.put(src, null);
        NodeDistance srcDis = new NodeDistance(src);
        allNewDis.put(src, srcDis);
        //System.out.println("creat first (src) NodeDistance with key " + src + "\n");

        distances.put(src,srcDis);
        srcDis.setDistance(0);
        g.getNode(src).setInfo("visited");  ///?

        pQueue.add(srcDis);
        while(!pQueue.isEmpty()){
            NodeDistance predDis = pQueue.poll();
            //NodeDistance predDis = new NodeDistance(pred.getKey());
            Collection<edge_data> ni = g.getE(predDis.getKey())   ;
            Iterator<edge_data> iter1 = ni.iterator();
            while (iter1.hasNext()) {           //add the queue all the neighbor
                edge_data tempEdge = iter1.next();
                node_data tempDest = g.getNode(tempEdge.getDest());
                if(tempDest.getInfo() != "visited"){
                    NodeDistance tempDis;
                    if(!distances.containsKey(tempDest.getKey())) {
                        tempDis = new NodeDistance(tempDest.getKey());
                        allNewDis.put(tempDest.getKey(), tempDis);
                        //System.out.println("creat NodeDistance with key " + tempDest.getKey() + "\n");
                        distances.put(tempDest.getKey(), tempDis);
                    }
                    else{
                        tempDis=distances.get(tempDest.getKey());
                    }
                    pQueue.add(tempDis);
                }
            }
            Iterator<edge_data> iter2 = ni.iterator();
            while (iter2.hasNext()) {               //  updating the right distance in the neighbors
                edge_data tempEdge = iter2.next();
                node_data tempDest = g.getNode(tempEdge.getDest());
                if (tempDest.getInfo() != "visited") {
                    NodeDistance tempDis = allNewDis.get(tempDest.getKey());
                    double distance = predDis.getDistance()  +  tempEdge.getWeight();
                    if(tempDis == null) {
                    }
                    if (distance < tempDis.getDistance()) {///
                        tempDis.setDistance(distance);
                        parents.put(tempDest.getKey(), predDis.getKey());
                    }
                }
            }
            g.getNode(predDis.getKey()).setInfo("visited");
        }
        Pair_ParentDistance ans = new Pair_ParentDistance(parents, distances);
        return ans;
    }

    /**restart the info - necessary before using the Dijkstra algorithm (that change info)*/
    private void restartDis (Collection<node_data> nodes){
        Iterator<node_data> iter = nodes.iterator();
        while (iter.hasNext()) {
            node_data temp = iter.next();
            temp.setInfo("not visited");
        }

    }

    @Override
    public String toString() {
        return "DWGraph_Algo{" +
                "graph=" + graph +
                '}';
    }
}
