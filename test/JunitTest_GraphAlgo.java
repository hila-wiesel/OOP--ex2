
import api.*;
import com.google.gson.*;
/*import com.google.gson.Gson;
import com.google.gson.GsonBuilder;*/
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.util.List;

public class JunitTest_GraphAlgo {

    public directed_weighted_graph graphBuilder(int size) {
        directed_weighted_graph g = new DWGraph_DS();
        node_data  tempNode;
        for (int i = 0; i < size; ++i) {
            tempNode = new NodeData();
            tempNode.setLocation(new GeoLocation(i,i,i+1));
            g.addNode(tempNode);
        }
        return g;
    }

    @Test
    public void test_saveLoad_node(){
        //write to file:
        {
            node_data node = new NodeData();
            node.setLocation(new GeoLocation(1, 2, 3.5));
            node_data copy = new NodeData(node);
            Gson gson = new GsonBuilder().registerTypeAdapter(NodeData.class, new NodeJson()).create();
            String nodeJson = gson.toJson(copy);
            try {
                File newFile = new File("test_node");
                FileWriter fw = new FileWriter(newFile);
                fw.write(nodeJson);
                fw.close();
                System.out.println("pass writting ");
            } catch (IOException e) {
                System.out.println("fail writting");
            }
        }
        //write edges:
        {
            node_data node = new NodeData();
            node_data node2 = new NodeData();
            edge_data edge = new EdgeData(node.getKey(), node2.getKey(), 3);
            Gson gson_ = new GsonBuilder().registerTypeAdapter(EdgeData.class, new EdgeJson()).create();
            String edgeJson = gson_.toJson(edge);
            try {
                File newFile = new File("test_edge");
                FileWriter fw = new FileWriter(newFile);
                fw.write(edgeJson);
                fw.close();
                System.out.println("pass writting ");
            } catch (IOException e) {
                System.out.println("fail writting");
            }
        }

        //read from file:

        /*try{
            GsonBuilder builder = new GsonBuilder();
            builder.registerTypeAdapter(node_data.class, new NodeData()); //   class or interface?
            Gson gson2 = builder.create();
            FileReader reader = new FileReader("test_node");
            node_data node_from_file  = gson2.fromJson(reader, node_data.class);
            System.out.println("node_from_file= "+node_from_file);
            System.out.println("pass reading ");
        }
        catch (IOException e){  //FileNotFoundException
            System.out.println("fail reading ");
        }*/

    }

    @Test
    public void test_save (){
        directed_weighted_graph graph = graphBuilder(10);
        graph.connect(0,1,2);
        graph.connect(1,2,2);
        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(graph);
        Assertions.assertTrue(algo.save("graph_test"));
        Assertions.assertTrue(algo.load("graph_test"));


    }

    @Test
    public void test_saveLoad (){
        directed_weighted_graph graph = graphBuilder(10);
        graph.connect(0,1,1);
        graph.connect(0,2,1);
        graph.connect(5,2,9);
        graph.connect(2,5,5);
        dw_graph_algorithms algo = new DWGraph_Algo();

        algo.init(graph);
        System.out.println("original graph: " + algo.getGraph().toString());

        Assertions.assertTrue(algo.save("graph1"));
        Assertions.assertTrue(algo.load("graph1"));
        System.out.println("load graph: " + algo.getGraph().toString());
        directed_weighted_graph graph1 = algo.getGraph();
        //System.out.println(algo.toString());

        Assertions.assertTrue(algo.save("graph1_new"));
        Assertions.assertEquals(graph.nodeSize(), graph1.nodeSize());
        Assertions.assertEquals(graph.edgeSize(), graph1.edgeSize());

    }

    @Test
    public void test_copy() {
        directed_weighted_graph graph = graphBuilder(10);
        graph.connect(0,1,2);
        graph.connect(1,2,2);
        graph.connect(1,2,3);
        graph.connect(9,2,3);
        graph.connect(0,7,1);
        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(graph);
        directed_weighted_graph copy = algo.copy();

        Assertions.assertEquals(graph.nodeSize(), copy.nodeSize());
        Assertions.assertEquals(graph.edgeSize(), copy.edgeSize());
        Assertions.assertEquals(graph.getE(0).size(), copy.getE(0).size());
    }

    @Test
    public void test_connected() {
        directed_weighted_graph graph = graphBuilder(10);
        for (int i = 0; i < 9; ++i){
            graph.connect(i, i + 1, i/2+1);
        }
        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(graph);
        Assertions.assertFalse(algo.isConnected());
        for (int i = 0; i < 10; ++i) {
            graph.connect(i + 1, i, i / 2 + 1);
        }
        Assertions.assertTrue(algo.isConnected());
        Assertions.assertEquals(10, algo.shortestPath(9,0).size());

        printPath(algo.shortestPath(0,5));
        printPath(algo.shortestPath(9,3));

    }

    @Test
    public void test_shortestPath1() {
        directed_weighted_graph graph = graphBuilder(10);
        for (int i = 0; i<99;i = i + 2){
            graph.connect(i, i + 2, i+1);
        }
        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(graph);
        Assertions.assertNotNull(algo.shortestPath(0,2));
        Assertions.assertNull(algo.shortestPath(0,1));
        Assertions.assertFalse((algo.isConnected()));
        int src=0, dest=6;
        printPath(algo.shortestPath(src,dest));
        System.out.println("\ndistance between "+src+" to " + dest + " is: " + algo.shortestPathDist(src,dest));
        System.out.println("\ndistance between 0 to 1 not exist do it is: " + algo.shortestPathDist(0,1));
        //System.out.println(algo.toString(algo.getGraph()));
    }

    @Test
    void test_Time() {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(10000), () -> {
            directed_weighted_graph g = graphBuilder(1000000);
            for (int i = 0; i < g.nodeSize(); i++)
                for (int j = i; j < i+10; j++) {
                    g.connect(i, j, 1);
                }
        });
    }

    @Test
    void test_copyTime() {
        directed_weighted_graph g = graphBuilder(1000000);
        for (int i = 0; i < g.nodeSize(); i++) {
            for (int j = i; j < i+4; j++) {
                g.connect(i, j, 1);
            }
        }

        dw_graph_algorithms algo = new DWGraph_Algo();
        algo.init(g);
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(10000), () -> {
            directed_weighted_graph gc = algo.copy();
        });
    }


    //helper function:
    private void printPath(List<node_data> path) {
        if (path == null) {
            System.out.println("Path doesnt exist\n ");
            return;
        }
        System.out.println("Path between " + path.get(0).getKey()+ " to " + path.get(path.size()-1).getKey() + ": ");
        for (int i = 0; i < path.size() - 1; ++i) {
            System.out.print(path.get(i).getKey() + ", ");
        }
        System.out.print(path.get(path.size()-1).getKey()+"\n");
    }
}
