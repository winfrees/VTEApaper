/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exploration.plottools.panels;

import Exploration.plotgatetools.gates.Gate;
import Exploration.plotgatetools.gates.MicroSelection;
import Exploration.plotgatetools.listeners.MakeImageOverlayListener;
import Exploration.plotgatetools.listeners.ResetSelectionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 *
 * @author vinfrais
 */
public interface ExplorationCenter {

    ArrayList<MakeImageOverlayListener> overlaylisteners = new ArrayList<MakeImageOverlayListener>();
    
    ArrayList<ResetSelectionListener> resetselectionlisteners = new ArrayList<ResetSelectionListener>();

    //public JPanel createPanel(List li);
    public JPanel getPanel();

    public JPanel addSelectionToPlot();

    public JPanel addPlot(int x, int y, int l, String xText, String yText, String LUTText);

    public void showPlot(int x, int y, int l, String xText, String yText, String lText);

    public void updatePlot(int x, int y, int l);

    public boolean isMade(int x, int y, int l);

    public void addExplorationGroup();

    public XYChartPanel getPanel(int x, int y, int l, String xText, String yText, String lText);

    public Gate getGates(int x, int y, int l);
    
    public void stopGateSelection();

    public void addMakeImageOverlayListener(MakeImageOverlayListener listener);

    public void notifyMakeImageOverlayListeners(ArrayList gates);
    
    public void addResetSelectionListener(ResetSelectionListener listener);

    public void notifyResetSelectionListeners();
    
    

}
