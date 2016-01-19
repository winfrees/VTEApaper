/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exploration.plottools.panels;

import Exploration.MicroExplorer;
import Exploration.plotgatetools.gates.Gate;
import Exploration.plotgatetools.gates.GateLayer;
import Exploration.plotgatetools.gates.PolygonGate;
import Exploration.plotgatetools.listeners.ChangePlotAxesListener;
import Exploration.plotgatetools.listeners.ImageHighlightSelectionListener;
import Exploration.plotgatetools.listeners.PolygonSelectionListener;
import Exploration.plotgatetools.listeners.MakeImageOverlayListener;
import Exploration.plotgatetools.listeners.ResetSelectionListener;
import ij.IJ;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import javax.swing.JComponent;
//import javax.swing.JLayer;
import javax.swing.JPanel;
import org.jdesktop.jxlayer.JXLayer;
import org.jfree.chart.ChartPanel;

/**
 *
 * @author vinfrais
 */
public class XYExplorationPanel extends DefaultExplorationPanel implements PolygonSelectionListener, ImageHighlightSelectionListener, ChangePlotAxesListener {

    public XYExplorationPanel(ArrayList li, HashMap<Integer, String> hm) {
        super();
        IJ.log("XYExplorationPanel: setting up default XYExplorationPanel...");
        this.plotvalues = li;
        this.hm = hm;
        //default plot 
        this.addPlot(MicroExplorer.XSTART, MicroExplorer.YSTART, MicroExplorer.LUTSTART, hm.get(1), hm.get(4), hm.get(2)); 
    }

    private XYChartPanel createChartPanel(int x, int y, int l, String xText, String yText, String lText) {
        return new XYChartPanel(plotvalues, x, y, l, xText, yText, lText);
    }

    @Override
    public void addMakeImageOverlayListener(MakeImageOverlayListener listener) {
        overlaylisteners.add(listener);
    }

    @Override
    public void notifyMakeImageOverlayListeners(ArrayList gates) {
        for (MakeImageOverlayListener listener : overlaylisteners) {
            listener.makeOverlayImage(gates, currentX, currentY);
        }
    }

    @Override
    public JPanel getPanel() {
        return CenterPanel;
    }

    @Override
    public JPanel addPlot(int x, int y, int l, String xText, String yText, String lText) {

        currentX = x;
        currentY = y;
        currentL = l;

        CenterPanel.removeAll();

        //setup chart values
        XYChartPanel cpd = new XYChartPanel(plotvalues, x, y, l, xText, yText, lText);
        this.chart = cpd.getChartPanel();
        this.chart.setOpaque(false);

        //setup chart layer
        CenterPanel.setOpaque(false);
        CenterPanel.setBackground(new Color(0, 0, 0, 0));
        CenterPanel.setPreferredSize(chart.getPreferredSize());

        //add overlay 
        this.gl = new GateLayer();

        gl.addPolygonSelectionListener(this);
        gl.addImageHighLightSelectionListener(this);
        gl.msActive = false;

        //this.gl = gl_local;
        this.gates = new ArrayList();

        JXLayer<JComponent> gjlayer = gl.createLayer(chart, gates);

        gjlayer.setLocation(0, 0);

        CenterPanel.add(gjlayer);

        validate();
        repaint();
        pack();

        return CenterPanel;
    }
    
    @Override
    public void addExplorationGroup() {
        ArrayList al = new ArrayList();
        al.add(currentX + "_" + currentY + "_" + currentL);
        al.add(this.chart);
        al.add(gl.getGates());
        ExplorationItems.add(al);
    }

    @Override
    public void updatePlot(int x, int y, int l) {

        if (!(isMade(currentX, currentY, currentL))) {
            addExplorationGroup();
        }

        if (!(isMade(x, y, l))) {
            addPlot(x, y, l, hm.get(x), hm.get(y), hm.get(l));
            //System.out.println("Change Axes, new plot: " + x + ", " + y);  
        } else { 
            showPlot(x, y, l, hm.get(x), hm.get(y), hm.get(l));
            System.out.println("Change Axes: " + x + ", " + y + ", " + l);
        }

    }

    @Override
    public boolean isMade(int x, int y, int l) {
        ListIterator<ArrayList> itr = ExplorationItems.listIterator();
        String test;
        String key = x + "_" + y + "_" + l;
        while (itr.hasNext()) {
            test = itr.next().get(0).toString();
            if (key.equals(test)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public XYChartPanel getPanel(int x, int y, int l, String xText, String yText, String lText) {
        String key = x + "_" + y + "_" + l;
        if (isMade(x, y, l)) {
            ListIterator<ArrayList> itr = ExplorationItems.listIterator();
            String test;
            while (itr.hasNext()) {
                test = itr.next().get(0).toString();
                if (key.equals(test)) {
                    return (XYChartPanel) itr.next().get(1);
                }
            }
        }
        return this.createChartPanel(x, y, l, xText, yText, lText);
    }

    @Override
    public Gate getGates(int x, int y, int l) {
        String key = x + "_" + y;
        if (isMade(x, y, l)) {
            ListIterator<ArrayList> itr = ExplorationItems.listIterator();
            String test;
            while (itr.hasNext()) {
                test = itr.next().get(0).toString();
                if (key.equals(test)) {
                    return (Gate) itr.next().get(1);
                }
            }
        }
        return null;
    }

    @Override
    public void showPlot(int x, int y, int l, String xText, String yText, String lText) {

        //System.out.println("Exploration Panel Showing plot: " + x + ", " + y);

        currentX = x;
        currentY = y;
        currentL = l;

        CenterPanel.removeAll();

        ArrayList current = this.ExplorationItems.get(keyLookUp(x, y, l));
        
        

        this.gates = new ArrayList();
        this.chart = (ChartPanel) current.get(1);
        this.gates = (ArrayList) current.get(2);
        this.chart.setOpaque(false);
        CenterPanel.setOpaque(false);
        CenterPanel.setBackground(new Color(0, 0, 0, 0));
        CenterPanel.setPreferredSize(chart.getPreferredSize());

       JXLayer<JComponent> gjlayer = gl.createLayer(chart, gates);
        gjlayer.setLocation(0, 0);
        CenterPanel.add(gjlayer);
        validate();
        repaint();
        pack();
    }

    @Override
    public JPanel addSelectionToPlot() {
        gl.msActive = true;
        System.out.println("XYExploration Panel Selection Active: " + gl.msActive);
        //updatePlot();
        validate();
        repaint();
        pack();
        return CenterPanel;
    }
    //Listener overrides

    @Override
    public void polygonGate(ArrayList points) {

        PolygonGate pg = new PolygonGate(points);
        System.out.println("Current points to add: " + points);

        pg.createInChartSpace(chart);
        System.out.println("Current points in gui space:" + pg.getGateAsPoints());
        System.out.println("Current points in chart space: " + pg.getGateAsPointsInChart());
        gates.add(pg);

        System.out.println("Current gate count: " + gates.size());
        System.out.println("Current gate points: " + gates.get(gates.indexOf(pg)).getGateAsPoints());
        //reset microExplorer interface
        this.notifyResetSelectionListeners();
        //addPlot(plotvalues);
    }

    @Override
    public void imageHighLightSelection(ArrayList gates) {
        this.notifyMakeImageOverlayListeners(gates);
    }

    @Override
    public void onChangeAxes(int x, int y, int l) {

        if (!(isMade(currentX, currentY, currentL))) {
            addExplorationGroup();
        }

        if (!(isMade(x, y, l))) {
            addPlot(x, y, l, hm.get(x), hm.get(y), hm.get(l));
            System.out.println("Change Axes, new plot: " + x + ", " + y + ", " + l);
        } else {
            showPlot(x, y, l,  hm.get(x), hm.get(y), hm.get(l));
            System.out.println("Change Axes: " + x + ", " + y + ", " + l);
        }

    }



    @Override
    public void addResetSelectionListener(ResetSelectionListener listener) {
       resetselectionlisteners.add(listener);
    }

    @Override
    public void notifyResetSelectionListeners() {
        for (ResetSelectionListener listener : resetselectionlisteners) {
            listener.resetGateSelection();
        }     
    }

    @Override
    public void stopGateSelection() {
        gl.cancelSelection();
    }

    

}
