package gameClient;

import api.directed_weighted_graph;
import gameClient.util.Range;
import gameClient.util.Range2D;

import javax.swing.*;
import java.awt.*;

public class MyFrame extends JFrame {
    private Arena _ar;
/*
    MyFrame(String a) {
        super(a);
        int _ind = 0;
    }*/

    public MyFrame(String title, Arena _ar) {
        super(title);
        this._ar = _ar;
        initFrame();
        initPanel();

    }

    public void update (Arena _ar){
        this._ar = _ar;
        //updateFrame();
    }

    private void initFrame() {
        this.setSize(1000, 1000);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private void initPanel() {
        MyPanel myPanel = new MyPanel(_ar);
        this.add(myPanel);
    }



}
