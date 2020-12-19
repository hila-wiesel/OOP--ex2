package gameClient;

import api.directed_weighted_graph;
import api.edge_data;
import api.geo_location;
import api.node_data;
import gameClient.util.Point3D;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.*;
import java.util.List;

public class MyPanel extends JPanel {

    private gameClient.util.Range2Range _w2f;
    private Arena _ar;


    public MyPanel(Arena _ar) {
        this.setBackground(Color.pink);
        this._ar = _ar;
        updatePanel();
    }

    private void updatePanel() {

        Range rx = new Range(800,this.getWidth()+100);
        Range ry = new Range(this.getHeight()+50,500);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
    }

    public void paint(Graphics g) {
        int w = this.getWidth();
        int h = this.getHeight();
        g.clearRect(0, 0, w, h);
        updateFrame();
        draw_gameDetails(g);
        drawGraph(g);
        drawAgants(g);
        drawPokemons(g);
        drawInfo(g);
        //paintComponent(g);

    }

    private void updateFrame() {
        Range rx = new Range(20,this.getWidth()-20);
        Range ry = new Range(this.getHeight()-10,100);
        Range2D frame = new Range2D(rx,ry);
        directed_weighted_graph g = _ar.getGraph();
        _w2f = Arena.w2f(g,frame);
    }

    private void draw_gameDetails (Graphics g) {
        Font font = g.getFont().deriveFont( 20.0f );
        g.setFont( font );
        long time = _ar.getGame().timeToEnd();
        time = time/1000;
        String  timeStr = String.valueOf(time);
        g.drawString("time left: "+timeStr, 40,20);

        ArrayList<Integer> gameDetails = Arena.read_gameDetails(_ar.getGame().toString());
        int numOf_pokemons = gameDetails.get(0);
        int numOf_agents=  gameDetails.get(1);
        int numOf_moves=  gameDetails.get(2);
        int grade=  gameDetails.get(3);
        g.drawString("number of pokemons: " +numOf_pokemons, 40,40);
        g.drawString("\nnumber of moves: " + numOf_moves, 40,60);
        g.drawString("grade: "+ grade, 40,80);



    }

    private void drawInfo(Graphics g) {
        List<String> str = _ar.get_info();
        String dt = "none";
        for(int i=0;i<str.size();i++) {
            g.drawString(str.get(i)+" dt: "+dt,100,60+i*20);
        }
    }

    private void drawGraph(Graphics g) {
        directed_weighted_graph gg = _ar.getGraph();
        Iterator<node_data> iter = gg.getV().iterator();
        while(iter.hasNext()) {
            node_data n = iter.next();
            g.setColor(Color.pink);
            drawNode(n,7,g);
            Iterator<edge_data> itr = gg.getE(n.getKey()).iterator();
            while(itr.hasNext()) {
                edge_data e = itr.next();
                g.setColor(Color.GREEN);
                drawEdge(e, g);
            }
        }
    }
    private void drawPokemons(Graphics g) {
        Font font2 = g.getFont().deriveFont( 10.0f );
        g.setFont( font2 );
        TreeMap<Pokemon, Double> pokemons = _ar.getPokemons();
        if(pokemons!=null) {    //why ?
            for (Pokemon pokemon : pokemons.keySet()){
                Point3D pos = pokemon.getPos();
                int r=10;
                g.setColor(Color.green);
                if(pokemon.getType()==-1) {g.setColor(Color.orange);}
                if(pos!=null) {    //why ?
                    geo_location geo = this._w2f.world2frame(pos);
                    g.fillOval((int)geo.x()-r, (int)geo.y()-r, 2*r, 2*r);
                    g.drawString(""+pokemon.getValue(), (int)geo.x(), (int)geo.y()-2*r);
                    //g.drawString(""+n.getKey(), fp.ix(), fp.iy()-4*r);
                }
            }
        }
    }
    private void drawAgants(Graphics g) {
        Font font2 = g.getFont().deriveFont( 10.0f );
        g.setFont( font2 );
        HashMap<Integer,Agent> agents = _ar.getAgents();
        g.setColor(Color.red);
        int i=0;
        for (Agent agent : agents.values() ){
            geo_location pos = agent.getPos();
            int r=8;
            if(pos!=null) {
                geo_location fp = this._w2f.world2frame(pos);
                g.fillOval((int)fp.x()-r, (int)fp.y()-r, 2*r, 2*r);
                g.drawString(agent.getId() + " to -> " + agent.getNextNode() + " from "+ agent.getSrc_id(), (int)fp.x(), (int)fp.y()-4*r);

            }
        }
    }

    private void drawNode(node_data n, int r, Graphics g) {
        Font font2 = g.getFont().deriveFont( 10 );
        g.setFont( font2 );
        geo_location pos = n.getLocation();
        geo_location geo = this._w2f.world2frame(pos);
        g.fillOval((int)geo.x()-r, (int)geo.y()-r, 2*r, 2*r);
        g.drawString(""+n.getKey(), (int)geo.x(), (int)geo.y()-2*r);
    }
    private void drawEdge(edge_data e, Graphics g) {
        Font font2 = g.getFont().deriveFont( 10.0f );
        g.setFont( font2 );
        g.setColor(Color.blue);
        directed_weighted_graph gg = _ar.getGraph();
        geo_location s = gg.getNode(e.getSrc()).getLocation();
        geo_location d = gg.getNode(e.getDest()).getLocation();
        geo_location s0 = this._w2f.world2frame(s);
        geo_location d0 = this._w2f.world2frame(d);
        g.drawLine((int)s0.x(), (int)s0.y(), (int)d0.x(), (int)d0.y());
        //draw weigh:
        g.setColor(Color.gray);
        DecimalFormat df = new DecimalFormat("#.#");
        g.drawString("w: " + df.format(e.getWeight()), ((int)d0.x()+(int)s0.x())/2, ((int)d0.y()+(int)s0.y())/2);
    }

}


