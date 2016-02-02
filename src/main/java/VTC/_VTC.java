package VTC;

import Exploration.listeners.UpdateAnalysisTableListener;
import Exploration.listeners.UpdatePlotTableListener;
import MicroProtocol.protocolManager;
import MicroProtocol.ProtocolManagerMulti;
import ij.ImageJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.plugin.PlugIn;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class _VTC implements PlugIn, ImageListener, ActionListener, ImageSelectionListener {

    public static Color BACKGROUND = new Color(204, 204, 204);
    public static Color BUTTONBACKGROUND = new Color(200, 200, 200);
    public static Color ACTIONPANELBACKGROUND = new Color(240, 240, 240);
    public static Color INACTIVETEXT = new Color(153,153,153);
    public static Color ACTIVETEXT = new Color(0,0,0);
    public static Dimension SMALLBUTTONSIZE = new Dimension(32, 32);
    public static Dimension BLOCKSETUP = new Dimension(370, 350);
    public static Dimension BLOCKSETUPPANEL = new Dimension(340, 100);
    public static String VERSION = new String("0.9.0_a1");
    
    public static String[] PROCESSOPTIONS = {"Select Method", "LayerCake 3D", "FloodFill 3D", "Assisted Detection 3D", "Auto Detection 3D"};
    
    //public static Color ButtonBackground = new java.awt.Color(102, 102, 102);

    public static void main(String[] args) {
         //set the plugins.dir property to make the plugin appear in the Plugins menu
        Class<?> clazz = _VTC.class;
        String url = clazz.getResource("/" + clazz.getName().replace('.', '/') + ".class").toString();
        String pluginsDir = url.substring(5, url.length() - clazz.getName().length() - 6);
        System.setProperty("plugins.dir", pluginsDir);

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {new ImageJ(); }
        });
    }

    protected ImagePlus imp1;
    private int folioCount = 0;
    //private ArrayList<UpdateAnalysisTableListener> listenersAnalysisTable = new ArrayList<UpdateAnalysisTableListener>();
    //private ArrayList<UpdatePlotTableListener> listenersPlotTable = new ArrayList<UpdatePlotTableListener>();

    //public protocolManager protocolWindow = new protocolManager();
    public ProtocolManagerMulti protocolWindow = new ProtocolManagerMulti();


    public void setup(String arg, ImagePlus imp1) {}

    // @Override
    @Override
public void run(String arg) {
    protocolWindow.setVisible(true);
    //protocolWindowTest.setVisible(true);
    ImagePlus.addImageListener(this);
}

    @Override
    public void actionPerformed(java.awt.event.ActionEvent evt) {

    }

    @Override
    public void imageOpened(ImagePlus imp) {
        //IJ.showMessage("Event", "New image added");
        protocolWindow.UpdateImageList();
        //protocolWindowTest.UpdateImageList();
    }

    @Override
    public void imageClosed(ImagePlus imp) {
        protocolWindow.UpdateImageList();
        //protocolWindowTest.UpdateImageList();
    }

    @Override
    public void imageUpdated(ImagePlus imp) {
        protocolWindow.UpdateImageList();
        //protocolWindowTest.UpdateImageList();
    }

    @Override
    public void onSelect(ImagePlus imp2, int tab) {

    }

}
