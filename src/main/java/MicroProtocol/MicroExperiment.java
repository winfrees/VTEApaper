/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MicroProtocol;

import Exploration.MicroExplorer;
import Exploration.plottools.panels.DefaultPlotPanels;
import Exploration.plottools.panels.XYExplorationPanel;
import ij.ImagePlus;
import java.util.ArrayList;
import java.util.HashMap;
import preprocessing.MicroProtocolPreProcessing;

/**
 *
 * @author vinfrais
 *
 * Class for organizing both the folder classes-> data source and processing and
 * the explorer classes-> connected exploration classes.
 */
public class MicroExperiment {

    ArrayList FolderDrawer = new ArrayList();
    ArrayList ExploreDrawer = new ArrayList();
    MicroProtocolPreProcessing Process;

    MicroExperiment() {
    }

    public void addFolder(ImagePlus imp, ArrayList details) {
        for (int i = 0; i <= details.size() - 1; i++) {
            MicroFolder mf = new MicroFolder(imp, (ArrayList) details.get(i));
            FolderDrawer.add(mf);
        }
    }
    
    public void addProcessing(ImagePlus imp, String title, MicroProtocolPreProcessing protocol){
        
        Process = new MicroProtocolPreProcessing(imp, protocol.getSteps());
        
    }

    public void addExplore(ImagePlus imp, String title, ArrayList alvolumes, ArrayList AvailableData) {
        
        

//    ImagePlus localimp;
//ArrayList alVolumes;
//float[] x1;
//float[] y1;
//String x_axis;
//String y_axis;
//String root;
        int[] plotDataReference = new int[5];

        plotDataReference[0] = 0;
        plotDataReference[1] = 0;
        plotDataReference[2] = 0;
        plotDataReference[3] = 0;
        plotDataReference[4] = 0;

//         plotAllDataReference[plotCount][0] = xChannel;
//         plotAllDataReference[plotCount][1] = xAnalytic;
//         plotAllDataReference[plotCount][2] = yChannel;
//         plotAllDataReference[plotCount][3] = yAnalytic;
//         plotAllDataReference[plotCount][4] = MaskChannel;
        ArrayList plotvalues = new ArrayList();

        plotvalues.add(imp);
        plotvalues.add(alvolumes);
        plotvalues.add(0.0);
        plotvalues.add(0.0);
        plotvalues.add("x_axis");
        plotvalues.add("y_axis");
        plotvalues.add(imp.getTitle());
        plotvalues.add(plotDataReference);

        //imageplus
        //volumes per microvolumes
        //float x, deprecated
        //float y, deprecated
        //x title
        //y title
        //imageplus title
        //plot data reference
        
        HashMap<Integer, String> hm = new HashMap<Integer,String>();
        
        for(int i = 0; i <= AvailableData.size()-1; i++){hm.put(i, AvailableData.get(i).toString());}
       
        XYExplorationPanel XY = new XYExplorationPanel(plotvalues, hm);
    
        
        DefaultPlotPanels DPP = new DefaultPlotPanels();
        MicroExplorer me = new MicroExplorer();
        me.setTitle(imp.getTitle().replace("DUP_", ""));
        me.setTitle(me.getTitle().replace(".tif", ""));
        new Thread(me).start();
        me.process(imp, title, plotvalues, XY, DPP, AvailableData);
       
        ExploreDrawer.add(me);
    }

    public ArrayList getVolumes(int i) {
        MicroFolder mf;
        mf = (MicroFolder) FolderDrawer.get(i);
        return mf.getVolumes();
    }

    public ArrayList getAvailableData(int i) {
        MicroFolder mf;
        mf = (MicroFolder) FolderDrawer.get(i);
        return mf.getAvailableData();
    }
    
    public ArrayList getProcess() {
        return this.Process.getSteps();
    }
}
