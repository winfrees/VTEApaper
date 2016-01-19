 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package MicroProtocol;

import Exploration.SimpleThresholdDataModel;
import Objects.layercake.microVolume;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import java.util.ArrayList;
import java.util.ListIterator;

/**
 *
 * @author vinfrais
 */
public class MicroFolder extends java.lang.Object {

    //class to organize the microbuilder classes
    /**
     * Details content breakdown
     *
     * [0] primary channel; method array list for primary channel segmentation
     * [1] secondary channel; method array list [2] secondary channel; method
     * array list [3] etc...
     *
     * method arraylist [0] channel [1] key for method [2] field key [3] field1
     * [4] field2 [5] etc...
     *
     * @param details
     */
    private ArrayList protocol;
    private ArrayList volumes;
    private float[] minConstants;
    private ImageStack[] imagedata;

    // 0: minObjectSize, 1: maxObjectSize, 2: minOverlap, 3: minThreshold
    MicroFolder(ImagePlus imp, ArrayList details) {
        protocol = details;
        imagedata = getInterleavedStacks(imp);
        Process();
    }

    private void Process() {

        ArrayList mask = new ArrayList();
        mask = (ArrayList) protocol.get(0);

        ArrayList derived = new ArrayList();

        switch ((Integer) mask.get(1)) {

            case 1:
                SimpleThresholdDataModel stdm = new SimpleThresholdDataModel(imagedata, protocol);
                volumes = stdm.getObjects(); // 
                break;
            default: ;
                break;
        }
    }

    public ArrayList getVolumes() {
        return volumes;
    }

    public ArrayList getAvailableData() {

        ArrayList al = new ArrayList();

        //microVolume.Analytics;
        protocol.size();
        for (int i = 0; i <= protocol.size() - 1; i++) {
            //((ArrayList)protocol.get(i)).get(1);
            for (int c = 0; c <= microVolume.Analytics.length - 1; c++) {
                String derived = new String();
                String text = new String();
                if (i == 0) {
                    derived = "";
                } else {
                    derived = "D_" + ((ArrayList) protocol.get(i)).get(1);
                }
                text = "CH_" + ((ArrayList) protocol.get(i)).get(0) + "_" + derived + "_" + microVolume.Analytics[c];
                al.add(text);
            }
        }
        return al;
    }

    private ImageStack[] getInterleavedStacks(ImagePlus imp) {
        ImageStack[] stacks = new ImageStack[imp.getNChannels()];
        ImageStack stack = imp.getImageStack();
        for (int m = 0; m <= imp.getNChannels() - 1; m++) {
            stacks[m] = new ImageStack(imp.getWidth(), imp.getHeight());
            for (int n = m; n <= imp.getStackSize() - 1; n += imp.getNChannels()) {
                stacks[m].addSlice(stack.getProcessor(n + 1));
            }
        }
//		IJ.log("microSetup::getInterleavedStacks           Generated stack array.");
//		IJ.log("        ImagePlus height:  " + imp.getStackSize());
//		IJ.log("        Interleaved height:  " + interleavedHeight);
        //IJ.log("        Channel count:  " + channelCount);
        //IJ.log("        Stack height:  " + stacks[0].getSize());	
        return stacks;
    }
}
