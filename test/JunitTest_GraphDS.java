import api.*;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.Duration;


public class JunitTest_GraphDS {

    public directed_weighted_graph graphBuilder(int size) {
        directed_weighted_graph g = new DWGraph_DS();
        node_data  tempNode;
        for (int i = 0; i < size; ++i) {
            tempNode = new NodeData();
            g.addNode(tempNode);
        }
        return g;
    }

    @Test
    public void test_getNode() {
        directed_weighted_graph g = graphBuilder(10);
        for (int i = 0; i < 10; ++i)
            Assertions.assertNotNull(g.getNode(i));
    }

    @Test
    public void test_getNullNode() {
        directed_weighted_graph g = graphBuilder(0);
        Assertions.assertNull(g.getNode(0));
    }

    @Test
    public void connect1() {
        directed_weighted_graph g = graphBuilder(10);
        for (int i = 0; i<10;i = i + 2){
            g.connect(i, i + 2, i);
        }
        for (int j = 0, k = 1; j < 8 && k < 10; j = j + 2, k = k + 2) {
            Assertions.assertNotNull(g.getEdge(j, j + 2));
            Assertions.assertNull(g.getEdge(k, k + 2));
        }
    }

    @Test
    public void connectSame() {
        directed_weighted_graph g = graphBuilder(10);
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(0, 3, 1);
        int expect = g.getE(0).size();
        g.connect(0, 0, 1);
        g.connect(0, 1, 2);
        Assertions.assertEquals(expect, g.getE(0).size());
    }

    @Test
    public void connectNotExist() {
        directed_weighted_graph g = graphBuilder(1);
        g.connect(0, 1, 2);
        Assertions.assertEquals(1, g.getMC());
    }

    @Test
    public void remove1(){
        directed_weighted_graph g = graphBuilder(10);
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(0, 3, 1);
        g.connect(5, 0, 1);
        g.connect(5, 3, 1);
        g.removeNode(0);
        Assertions.assertEquals(1, g.edgeSize());
    }

    @Test
    public void remove2_null(){
        directed_weighted_graph g = graphBuilder(0);
        Assertions.assertNull(g.removeNode(0));
    }

    @Test
    public void remove3(){
        directed_weighted_graph g = graphBuilder(10);
        g.connect(0, 1, 1);
        g.connect(0, 2, 1);
        g.connect(3, 0, 1);
        g.removeEdge(0,1);
        g.removeEdge(2,3);
        Assertions.assertEquals(2, g.edgeSize());

    }


    @Test
    public  void graphTest_BuildRunTime()
    {
        Assertions.assertTimeoutPreemptively(Duration.ofMillis(10000), () -> {

            directed_weighted_graph g=graphBuilder(1000000);
            for (int i = 0; i <g.nodeSize() ; i++)
                for (int j = 0; j <11 ; j++)
                    g.connect(i,j,1);
        });
    }
}