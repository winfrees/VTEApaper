/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exploration;


import Exploration.plottools.panels.VerticalLabelUI;
import Exploration.plotgatetools.gates.Gate;
import Exploration.plotgatetools.listeners.ChangePlotAxesListener;
import Exploration.plotgatetools.listeners.MakeImageOverlayListener;
import Exploration.plotgatetools.listeners.PopupMenuAxisListener;
import Exploration.plotgatetools.listeners.ResetSelectionListener;
import Exploration.plotgatetools.listeners.PopupMenuAxisLUTListener;
import Exploration.plotgatetools.listeners.PopupMenuLUTListener;
import Exploration.plottools.panels.ExplorationCenter;
import Exploration.plottools.panels.PlotAxesPanels;
import Exploration.plottools.panels.XYPanels;
import Objects.layercake.microRegion;
import Objects.layercake.microVolume;
import ij.IJ;
import ij.ImageListener;
import ij.ImagePlus;
import ij.gui.ImageRoi;
import ij.gui.Overlay;
import ij.gui.TextRoi;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;

/**
 *
 * @author vinfrais
 *
 */
public class MicroExplorer extends javax.swing.JFrame implements MakeImageOverlayListener, ChangePlotAxesListener, ImageListener, ResetSelectionListener, PopupMenuAxisListener, PopupMenuLUTListener, PopupMenuAxisLUTListener, Runnable {

    private XYPanels DefaultXYPanels;
    private static final Dimension MainPanelSize = new Dimension(630, 640);
    private static final int POLYGONGATE = 10;
    private static final int RECTANGLEGATE = 11;
    private static final int QUADRANTGATE = 12;
    
    private static final int XAXIS = 0;
    private static final int YAXIS = 1;
    private static final int LUTAXIS = 2;
    
    public static final int XSTART = 0;
    public static final int YSTART = 4;
    public static final int LUTSTART = 1;

    List plotvalues;
    ExplorationCenter ec;
    PlotAxesPanels pap;
    JPanel HeaderPanel;
    ImagePlus imp;
    ImagePlus impoverlay;
    String title;
    ArrayList availabledata;
    
    JLabel xLabel;
    JLabel yLabel;
    JLabel lLabel;
    
    GatePercentages ResultsWindow;

    HashMap<Integer, JToggleButton> GateButtonsHM = new HashMap<Integer, JToggleButton>();
    HashMap<Integer, String> AvailableDataHM = new HashMap<Integer, String>();

    ArrayList<ExplorationCenter> ExplorationPanels = new ArrayList<ExplorationCenter>();
    /**
     * Creates new form MicroExplorer
     */
    public MicroExplorer() {IJ.log("MicroExplorer: object created.");
    }

    public void process(ImagePlus imp, String title, List plotvalues, ExplorationCenter ec, PlotAxesPanels pap, ArrayList AvailableData) {

        IJ.log("MicroExplorer: initializing window for " + imp.getTitle());
        this.availabledata = AvailableData;
        initComponents();

        //make HashMap, AvailableDataHM, for PopupMenu list,  nesting for easier popupmenu navigation?
        AvailableDataHM = makeAvailableDataHM(availabledata);
          
        final SelectPlottingDataMenu PlottingPopupXaxis = new SelectPlottingDataMenu(availabledata,MicroExplorer.XAXIS);
        final SelectPlottingDataMenu PlottingPopupYaxis = new SelectPlottingDataMenu(availabledata,MicroExplorer.YAXIS);
        final SelectPlottingDataMenu PlottingPopupLUTaxis = new SelectPlottingDataMenu(availabledata,MicroExplorer.LUTAXIS);

        PlottingPopupXaxis.addPopupMenuAxisListener(this);
        PlottingPopupYaxis.addPopupMenuAxisListener(this);
        PlottingPopupLUTaxis.addPopupMenuAxisListener(this);
        
        GateButtonsHM.put(POLYGONGATE, this.addPolygonGate);
        GateButtonsHM.put(RECTANGLEGATE, this.addRectangularGate);
        GateButtonsHM.put(QUADRANTGATE, this.addQuadrantGate);
        
        xLabel = new JLabel("X_axis");
        xLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 16));
        yLabel = new JLabel("Y_axis");
        
        yLabel.setUI(new VerticalLabelUI(false));
        yLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 16));
        
        lLabel = new JLabel("No LUT");
        lLabel.setFont(new Font("Lucidia Grande", Font.BOLD, 16));
        
        xLabel.addMouseListener(new java.awt.event.MouseListener(){

            @Override
            public void mouseClicked(MouseEvent me) {
                       if(SwingUtilities.isRightMouseButton(me)) {        
                        PlottingPopupXaxis.show(me.getComponent(), me.getX(), me.getY());}
            }

            @Override
            public void mousePressed(MouseEvent me) {}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) {}

            @Override
            public void mouseExited(MouseEvent me) {}
        });
        yLabel.addMouseListener(new java.awt.event.MouseListener(){

            @Override
            public void mouseClicked(MouseEvent me) {
                       if(SwingUtilities.isRightMouseButton(me)) {        
                        PlottingPopupYaxis.show(me.getComponent(), me.getX(), me.getY());}
            }

            @Override
            public void mousePressed(MouseEvent me) {}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) {}

            @Override
            public void mouseExited(MouseEvent me) {}
        });       
        lLabel.addMouseListener(new java.awt.event.MouseListener(){

            @Override
            public void mouseClicked(MouseEvent me) {
                       if(SwingUtilities.isRightMouseButton(me)) { 
                        PlottingPopupLUTaxis.show(me.getComponent(), me.getX(), me.getY());}
            }

            @Override
            public void mousePressed(MouseEvent me) {}

            @Override
            public void mouseReleased(MouseEvent me) {}

            @Override
            public void mouseEntered(MouseEvent me) {}

            @Override
            public void mouseExited(MouseEvent me) {}
        });
      
        this.imp = imp;
        this.impoverlay = imp.duplicate();
        this.impoverlay.setOpenAsHyperStack(true);
       
        DefaultXYPanels = new XYPanels(AvailableData);
        DefaultXYPanels.addChangePlotAxesListener(this);
        
        this.getContentPane().setBackground(VTC._VTC.BACKGROUND);
        this.getContentPane().setPreferredSize(new Dimension(600,600));
 
        Main.setBackground(VTC._VTC.BACKGROUND);
             
        ec.addResetSelectionListener(this);
        ExplorationPanels.add(ec);

        //load default view
        setPanels(plotvalues, ExplorationPanels.get(0), pap);  
        this.addAxesLabels(AvailableData.get(this.XSTART).toString(), AvailableData.get(this.YSTART).toString(), AvailableData.get(this.LUTSTART).toString());
        this.displayXYView();
        this.repaint();
        this.pack();
        this.setVisible(true);

        IJ.log("MicroExplorer:  Explorer window made.");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        North = new javax.swing.JPanel();
        toolbarPlot = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jComboBoxXaxis = new javax.swing.JComboBox();
        jLabel2 = new javax.swing.JLabel();
        jComboBoxYaxis = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jComboBoxLUTPlot = new javax.swing.JComboBox();
        toolbarGate = new javax.swing.JToolBar();
        jLabel4 = new javax.swing.JLabel();
        addRectangularGate = new javax.swing.JToggleButton();
        addPolygonGate = new javax.swing.JToggleButton();
        addQuadrantGate = new javax.swing.JToggleButton();
        jToggleButton1 = new javax.swing.JToggleButton();
        jLabel6 = new javax.swing.JLabel();
        jToggleButton4 = new javax.swing.JToggleButton();
        jToggleButton3 = new javax.swing.JToggleButton();
        jToggleButton2 = new javax.swing.JToggleButton();
        jPanel2 = new javax.swing.JPanel();
        saveGates = new javax.swing.JButton();
        loadGates = new javax.swing.JButton();
        WestPanel = new javax.swing.JPanel();
        FlipAxes = new javax.swing.JButton();
        yTextPanel = new javax.swing.JPanel();
        SouthPanel = new javax.swing.JPanel();
        Main = new javax.swing.JPanel();

        setTitle(getTitle());
        setBackground(VTC._VTC.BACKGROUND);
        addContainerListener(new java.awt.event.ContainerAdapter() {
            public void componentAdded(java.awt.event.ContainerEvent evt) {
                formComponentAdded(evt);
            }
        });

        North.setMinimumSize(new java.awt.Dimension(600, 75));
        North.setPreferredSize(new java.awt.Dimension(600, 75));
        North.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 2, 5));

        toolbarPlot.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolbarPlot.setFloatable(false);
        toolbarPlot.setRollover(true);
        toolbarPlot.setMinimumSize(new java.awt.Dimension(600, 30));
        toolbarPlot.setPreferredSize(new java.awt.Dimension(600, 30));

        jLabel3.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel3.setText("Plot:  ");
        toolbarPlot.add(jLabel3);

        jLabel1.setText("X axis");
        toolbarPlot.add(jLabel1);

        jComboBoxXaxis.setModel(new DefaultComboBoxModel(this.availabledata.toArray()));
        jComboBoxXaxis.setSelectedIndex(this.XSTART);
        jComboBoxXaxis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxXaxisActionPerformed(evt);
            }
        });
        toolbarPlot.add(jComboBoxXaxis);

        jLabel2.setText("Y axis");
        toolbarPlot.add(jLabel2);

        jComboBoxYaxis.setModel(new DefaultComboBoxModel(this.availabledata.toArray()));
        jComboBoxYaxis.setSelectedIndex(this.YSTART);
        jComboBoxYaxis.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxYaxisActionPerformed(evt);
            }
        });
        toolbarPlot.add(jComboBoxYaxis);

        jLabel5.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel5.setText("LUT:");
        toolbarPlot.add(jLabel5);

        jComboBoxLUTPlot.setModel(new DefaultComboBoxModel(this.availabledata.toArray()));
        jComboBoxLUTPlot.setSelectedIndex(this.LUTSTART);
        jComboBoxLUTPlot.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxLUTPlotActionPerformed(evt);
            }
        });
        toolbarPlot.add(jComboBoxLUTPlot);

        North.add(toolbarPlot);

        toolbarGate.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        toolbarGate.setFloatable(false);
        toolbarGate.setRollover(true);
        toolbarGate.setPreferredSize(new java.awt.Dimension(600, 30));

        jLabel4.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel4.setText("Gates:  ");
        toolbarGate.add(jLabel4);

        addRectangularGate.setText("RECTANGLE");
        addRectangularGate.setFocusable(false);
        addRectangularGate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addRectangularGate.setPreferredSize(new java.awt.Dimension(78, 30));
        addRectangularGate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addRectangularGate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addRectangularGateActionPerformed(evt);
            }
        });
        toolbarGate.add(addRectangularGate);
        addRectangularGate.getAccessibleContext().setAccessibleName("AddRectangleGate");

        addPolygonGate.setText("POLYGON");
        addPolygonGate.setFocusable(false);
        addPolygonGate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addPolygonGate.setMaximumSize(new java.awt.Dimension(67, 30));
        addPolygonGate.setMinimumSize(new java.awt.Dimension(67, 30));
        addPolygonGate.setPreferredSize(new java.awt.Dimension(67, 30));
        addPolygonGate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addPolygonGate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addPolygonGateActionPerformed(evt);
            }
        });
        toolbarGate.add(addPolygonGate);

        addQuadrantGate.setText("QUAD");
        addQuadrantGate.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        addQuadrantGate.setBorderPainted(false);
        addQuadrantGate.setFocusable(false);
        addQuadrantGate.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addQuadrantGate.setMaximumSize(new java.awt.Dimension(45, 30));
        addQuadrantGate.setMinimumSize(new java.awt.Dimension(45, 30));
        addQuadrantGate.setPreferredSize(new java.awt.Dimension(45, 30));
        addQuadrantGate.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addQuadrantGate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addQuadrantGateActionPerformed(evt);
            }
        });
        toolbarGate.add(addQuadrantGate);

        jToggleButton1.setText("OVERLAY ALL");
        jToggleButton1.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        jToggleButton1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton1.setMaximumSize(new java.awt.Dimension(90, 30));
        jToggleButton1.setMinimumSize(new java.awt.Dimension(90, 30));
        jToggleButton1.setPreferredSize(new java.awt.Dimension(90, 30));
        jToggleButton1.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToggleButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jToggleButton1ActionPerformed(evt);
            }
        });
        toolbarGate.add(jToggleButton1);

        jLabel6.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        jLabel6.setText("Apply:  ");
        toolbarGate.add(jLabel6);

        jToggleButton4.setText("ALL");
        jToggleButton4.setFocusable(false);
        jToggleButton4.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton4.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarGate.add(jToggleButton4);

        jToggleButton3.setText("S1");
        jToggleButton3.setFocusable(false);
        jToggleButton3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton3.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarGate.add(jToggleButton3);

        jToggleButton2.setText("S2");
        jToggleButton2.setFocusable(false);
        jToggleButton2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jToggleButton2.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarGate.add(jToggleButton2);

        jPanel2.setMaximumSize(new java.awt.Dimension(100, 30));
        jPanel2.setMinimumSize(new java.awt.Dimension(20, 30));
        jPanel2.setPreferredSize(new java.awt.Dimension(20, 30));
        toolbarGate.add(jPanel2);

        saveGates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-save-2_24.png"))); // NOI18N
        saveGates.setFocusable(false);
        saveGates.setMaximumSize(new java.awt.Dimension(60, 30));
        saveGates.setMinimumSize(new java.awt.Dimension(60, 30));
        saveGates.setPreferredSize(new java.awt.Dimension(35, 30));
        saveGates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveGatesActionPerformed(evt);
            }
        });
        toolbarGate.add(saveGates);

        loadGates.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/document-open-folder_24.png"))); // NOI18N
        loadGates.setFocusable(false);
        loadGates.setMaximumSize(new java.awt.Dimension(60, 30));
        loadGates.setMinimumSize(new java.awt.Dimension(60, 30));
        loadGates.setPreferredSize(new java.awt.Dimension(35, 30));
        loadGates.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadGatesActionPerformed(evt);
            }
        });
        toolbarGate.add(loadGates);

        North.add(toolbarGate);

        getContentPane().add(North, java.awt.BorderLayout.NORTH);

        WestPanel.setMinimumSize(new java.awt.Dimension(44, 530));
        WestPanel.setPreferredSize(new java.awt.Dimension(44, 530));
        WestPanel.setLayout(new java.awt.BorderLayout());

        FlipAxes.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/view-refresh-5.png"))); // NOI18N
        FlipAxes.setFocusable(false);
        FlipAxes.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        FlipAxes.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        FlipAxes.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                FlipAxesActionPerformed(evt);
            }
        });
        WestPanel.add(FlipAxes, java.awt.BorderLayout.PAGE_END);

        yTextPanel.setLayout(new java.awt.BorderLayout());
        WestPanel.add(yTextPanel, java.awt.BorderLayout.CENTER);

        getContentPane().add(WestPanel, java.awt.BorderLayout.WEST);

        SouthPanel.setPreferredSize(new java.awt.Dimension(530, 30));
        getContentPane().add(SouthPanel, java.awt.BorderLayout.SOUTH);

        Main.setBackground(new java.awt.Color(255, 255, 255));
        Main.setMinimumSize(new java.awt.Dimension(600, 630));
        Main.setName(""); // NOI18N
        Main.setPreferredSize(new java.awt.Dimension(630, 600));
        Main.setLayout(new java.awt.BorderLayout());
        getContentPane().add(Main, java.awt.BorderLayout.CENTER);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void formComponentAdded(java.awt.event.ContainerEvent evt) {//GEN-FIRST:event_formComponentAdded
this.setExtendedState(MAXIMIZED_BOTH);        // TODO add your handling code here:
    }//GEN-LAST:event_formComponentAdded

    private void jComboBoxXaxisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxXaxisActionPerformed
        onPlotChangeRequest(jComboBoxXaxis.getSelectedIndex(), jComboBoxYaxis.getSelectedIndex(), jComboBoxLUTPlot.getSelectedIndex());
    }//GEN-LAST:event_jComboBoxXaxisActionPerformed

    private void saveGatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveGatesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_saveGatesActionPerformed

    private void loadGatesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadGatesActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_loadGatesActionPerformed

    private void jComboBoxYaxisActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxYaxisActionPerformed
        onPlotChangeRequest(jComboBoxXaxis.getSelectedIndex(), jComboBoxYaxis.getSelectedIndex(), jComboBoxLUTPlot.getSelectedIndex());
    }//GEN-LAST:event_jComboBoxYaxisActionPerformed

    private void addQuadrantGateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addQuadrantGateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addQuadrantGateActionPerformed

    private void addPolygonGateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addPolygonGateActionPerformed
       if(this.addPolygonGate.isSelected()){makePolygonGate();}
       else{ec.stopGateSelection(); this.activationGateTools(0);}
    }//GEN-LAST:event_addPolygonGateActionPerformed

    private void addRectangularGateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addRectangularGateActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_addRectangularGateActionPerformed

    private void jToggleButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jToggleButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jToggleButton1ActionPerformed

    private void FlipAxesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_FlipAxesActionPerformed
       flipAxes();
    }//GEN-LAST:event_FlipAxesActionPerformed

    private void jComboBoxLUTPlotActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxLUTPlotActionPerformed
        onPlotChangeRequest(jComboBoxXaxis.getSelectedIndex(), jComboBoxYaxis.getSelectedIndex(), jComboBoxLUTPlot.getSelectedIndex());
    }//GEN-LAST:event_jComboBoxLUTPlotActionPerformed

    /**
     * @param args the command line arguments
     */
//    public static void main(String args[]) {
//        /* Set the Nimbus look and feel */
//        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
//        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
//         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html
//         */
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Nimbus".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException ex) {
//            java.util.logging.Logger.getLogger(MicroExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (InstantiationException ex) {
//            java.util.logging.Logger.getLogger(MicroExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (IllegalAccessException ex) {
//            java.util.logging.Logger.getLogger(MicroExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
//            java.util.logging.Logger.getLogger(MicroExplorer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
//        }
//        //</editor-fold>
//
//        /* Create and display the form */
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new MicroExplorer().setVisible(true);
//            }
//        });
//    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton FlipAxes;
    protected javax.swing.JPanel Main;
    private javax.swing.JPanel North;
    private javax.swing.JPanel SouthPanel;
    private javax.swing.JPanel WestPanel;
    protected javax.swing.JToggleButton addPolygonGate;
    protected javax.swing.JToggleButton addQuadrantGate;
    protected javax.swing.JToggleButton addRectangularGate;
    protected javax.swing.JComboBox jComboBoxLUTPlot;
    protected javax.swing.JComboBox jComboBoxXaxis;
    protected javax.swing.JComboBox jComboBoxYaxis;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToggleButton jToggleButton1;
    private javax.swing.JToggleButton jToggleButton2;
    private javax.swing.JToggleButton jToggleButton3;
    private javax.swing.JToggleButton jToggleButton4;
    private javax.swing.JButton loadGates;
    private javax.swing.JButton saveGates;
    private javax.swing.JToolBar toolbarGate;
    private javax.swing.JToolBar toolbarPlot;
    private javax.swing.JPanel yTextPanel;
    // End of variables declaration//GEN-END:variables

    private void setPanels(List plotvalues, ExplorationCenter ec, PlotAxesPanels pap) {

        this.plotvalues = plotvalues;
        this.ec = ec;
        this.ec.addMakeImageOverlayListener(this);
        this.pap = pap;
        Main.removeAll();
        Main.add(ec.getPanel());
        updateBorderPanels(pap);

        pack();
    }
    
    public void updateCenterPanel(List plotvalues, ExplorationCenter ec) {
        Main.removeAll();
        Main.add(ec.getPanel());
        pack();
    }

    public void updateBorderPanels(PlotAxesPanels pap) {
        
        //Main.add(pap.getBorderPanelFooter(), BorderLayout.SOUTH);
        //Main.add(pap.getBorderPanelLeft(), BorderLayout.WEST);
        //Main.add(pap.getBorderPanelHeader(), BorderLayout.NORTH);
        
        pack();
    }

    public void displayXYView() {
        updateBorderPanels(DefaultXYPanels);
        //this.repaint();
        this.pack();
    }

    private void redrawCenterPanel() {
        updateCenterPanel(this.plotvalues, this.ec);
    }

    static public Dimension getMainDimension() {
        return MicroExplorer.MainPanelSize;
    }

    private void makePolygonGate() {
        activationGateTools(POLYGONGATE);
        ec.addSelectionToPlot();
        pack();
    }
    
    private HashMap makeAvailableDataHM(ArrayList al){       
        HashMap<Integer, String> hm = new HashMap<Integer, String>();       
        ListIterator<String> itr = al.listIterator();    
        while (itr.hasNext()) {
            hm.put(itr.nextIndex(), itr.next());
        }       
        return hm;
    }
    
    private void flipAxes() {updatePlotByPopUpMenu(this.jComboBoxYaxis.getSelectedIndex(), this.jComboBoxXaxis.getSelectedIndex(), this.jComboBoxLUTPlot.getSelectedIndex());}
    
    private void activationGateTools(int activeGate){
        if(activeGate > 5){
         for(int i = 10; i <= 12; i++){
            if(i != activeGate){this.GateButtonsHM.get(i).setEnabled(false);
        }
        }}
        else{for(int i = 10; i <= 12; i++){
            this.GateButtonsHM.get(i).setEnabled(true);
            this.GateButtonsHM.get(i).setSelected(false);
        }}
    }   
        
    public void onPlotChangeRequest(int x, int y, int z) {
        //ec.addPlot(x, y);
        Main.removeAll();
        ec.updatePlot(x, y, z);
//    if(ec.isMade(x, y)) {ec.showPlot(x, y);}
//    else {ec.addPlot(x, y);}
        Main.add(ec.getPanel());
        updateBorderPanels(DefaultXYPanels);
        updateAxesLabels(jComboBoxXaxis.getSelectedItem().toString(), jComboBoxYaxis.getSelectedItem().toString(), jComboBoxLUTPlot.getSelectedItem().toString());
        pack();

    }
    
    private void addAxesLabels(String xText, String yText, String lText){
        
        yTextPanel.setPreferredSize(new Dimension(40,40));
        yLabel.setText(yText);
        yTextPanel.add(yLabel, BorderLayout.CENTER);
        
        SouthPanel.setLayout(new FlowLayout());
        xLabel.setText(xText);
        lLabel.setText("LUT: "+lText+"           "); 
        SouthPanel.add(lLabel);
        SouthPanel.add(xLabel);
        pack();
    }
    
    private void updateAxesLabels(String xText, String yText, String lText){      
        yLabel.setText(yText);
        xLabel.setText(xText);
        lLabel.setText("LUT: "+lText+"           ");   
        yTextPanel.removeAll();
        SouthPanel.removeAll();    
        addAxesLabels(xText, yText, lText); 
    }
    
    private void updatePlotByPopUpMenu(int x, int y, int l){
        Main.removeAll();
        ec.updatePlot(x, y, l);
        Main.add(ec.getPanel());
        jComboBoxXaxis.setSelectedIndex(x);
        jComboBoxYaxis.setSelectedIndex(y);
        jComboBoxLUTPlot.setSelectedIndex(l);
        updateBorderPanels(DefaultXYPanels);
        pack();
    }

    private Number processPosition(int a, microVolume volume) {
        ArrayList ResultsPointer = volume.getResultPointer();
        int size = ResultsPointer.size();
        //System.out.println("to convert: " + a);

        if (a <= 10) {
            return (Number) volume.getAnalysisMaskVolume()[a];
        } else {
            int row = ((a)/11)-1;
            int column = a%11;
            
//            int row = (Integer) ResultsPointer.get(((int) Math.ceil((a + 1) / 11)));
//            int column = (((a + 1) - (row + 1) * 11) - 1);
//            //System.out.println("Row: " + row);//row as in object array position.
//            //System.out.println("Column: " + column);
            return (Number) volume.getAnalysisResultsVolume()[row][column];
        }
    }

    //SimpleThresholdDataModel dependent....
    //ImageJ1 specific code
    @Override
    public void makeOverlayImage(ArrayList gates, int xAxis, int yAxis) {
        //convert gate to chart x,y path
        Gate gate;
 
        System.out.println("makeOverlayImage, making overlay image...");  
        System.out.println("For axes: " + xAxis + " and " + yAxis);
        ListIterator<Gate> gate_itr = gates.listIterator();
   
        int selected = 0;
        int total = 0;      
        
        int gatecount = gates.size();

        while(gate_itr.hasNext())
        {
        gate = gate_itr.next();
        if(gate.getSelected()){
        Path2D path = gate.createPath2DInChartSpace();

        ArrayList<microVolume> result = new ArrayList<microVolume>();
        ArrayList<microVolume> volumes = (ArrayList) this.plotvalues.get(1);
        microVolume volume;

        double xValue = 0;
        double yValue = 0;

        //ArrayList resultalVolumes = new ArrayList();
        ListIterator<microVolume> it = volumes.listIterator();
        while (it.hasNext()) {
            volume = (microVolume) it.next();
            if (volume != null) {
                xValue = ((Number) processPosition(xAxis, volume)).doubleValue();
                yValue = ((Number) processPosition(yAxis, volume)).doubleValue();

                if (path.contains(xValue, yValue)) {
                    result.add(volume);
                }
            }
        }
        //System.out.println("microPlot::getContainedVolumes                        found volumes," + result.size());
        //System.out.println("Gates for highlight:" + path);
        // make a buffered image with the selection in a new shade.  use the banks of SampleModel for individual slices

        //cycle through volumes and plot overlays for regions in z
        //impoverlay.addImageListener(this);
//ImageRoi[] ir = new ImageRoi[impoverlay.getNSlices()];
//BufferedImage[] selections = new BufferedImage[impoverlay.getNSlices()];
        //impoverlay.
        Overlay overlay = new Overlay();

        int count = 0;
        BufferedImage placeholder = new BufferedImage(impoverlay.getWidth(), impoverlay.getHeight(), BufferedImage.TYPE_INT_ARGB);
        
        selected = result.size();
        
        //error in gate selection need to find the extra 1!
        total = volumes.size()-1;

        for (int i = 0; i <= imp.getNSlices(); i++) {
        BufferedImage selections = new BufferedImage(impoverlay.getWidth(), impoverlay.getHeight(), BufferedImage.TYPE_INT_ARGB);
            
            Graphics2D g2 = selections.createGraphics();

            ImageRoi ir = new ImageRoi(0, 0, placeholder);
            ListIterator<microVolume> vitr = result.listIterator();
            while (vitr.hasNext()) {
                microVolume vol = (microVolume) vitr.next();

                ArrayList<microRegion> regions = vol.getRegions();
                ListIterator<microRegion> ritr = regions.listIterator();
                while (ritr.hasNext()) {
                    microRegion region = ritr.next();

                    if (region.getZPosition() == i) {
                        for (int c = 0; c <= region.getPixelCount(); c++) {
                            g2.setColor(gate.getColor());
                            g2.drawRect(region.getPixelsX()[c], region.getPixelsY()[c], 1, 1);
                        }
                        //g2.drawPolygon(region.getPixelsX(), region.getPixelsY(), region.getPixelCount());
                        //System.out.println("X pixels: " + region.getPixelsX() + " Y pixels: " + region.getPixelsY());
                        ir = new ImageRoi(0, 0, selections);                      
                        count++;
                        //overlay.add(new PolygonRoi(region.getPixelsX(), region.getPixelsY(),region.getPixelCount(),Roi.POLYGON ));
                    }
                }
            }
            //ir.setZeroTransparent(true);       
            ir.setPosition(i);
            ir.setOpacity(0.3);
            overlay.add(ir);   
            
            TextRoi text = new TextRoi(15, 20, selected + "/" + total + " objects (" + 100 * ((new Double(selected).doubleValue() / (new Double(total)).doubleValue())) + "%)");          
            text.setPosition(i);
            overlay.add(text);
            
            
        } 
        impoverlay.setOverlay(overlay);
        }
        
        IJ.log(impoverlay.getTitle() + "," + selected + "," + total);
       
        
        impoverlay.draw();
        impoverlay.setZ(Math.round(impoverlay.getNSlices()/2));
        impoverlay.show();        
//map ArrayList of volumes into pixel space for the current stack position
//add as overlay with a buffered image
        }
    }

    @Override
    public void onChangeAxes(int x, int y, int l) {
      updatePlotByPopUpMenu(x, y, l);
    }
    
    @Override
    public void changeLUT(int x) {
      //updatePlotByPopUpMenu(x, y);
    }
    
     @Override
    public void changeAxisLUT(String str){
       
    }
 

    @Override
    public void imageOpened(ImagePlus ip) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void imageClosed(ImagePlus ip) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void imageUpdated(ImagePlus ip) {
        //update graphics overlay
    }

    @Override
    public void resetGateSelection() {
        activationGateTools(0);
    }

    @Override
    public void changeAxes(int axis, String position) {
        if(axis == MicroExplorer.XAXIS){updatePlotByPopUpMenu(this.availabledata.indexOf(position), this.jComboBoxYaxis.getSelectedIndex(), this.jComboBoxLUTPlot.getSelectedIndex());}
        else if(axis == MicroExplorer.YAXIS){updatePlotByPopUpMenu(this.jComboBoxXaxis.getSelectedIndex(), this.availabledata.indexOf(position), this.jComboBoxLUTPlot.getSelectedIndex());}
        else if(axis == MicroExplorer.LUTAXIS){updatePlotByPopUpMenu(this.jComboBoxXaxis.getSelectedIndex(), this.jComboBoxYaxis.getSelectedIndex(),this.availabledata.indexOf(position));}
    }

    @Override
    public void run() {}  
    
    class SelectPlottingDataMenu extends JPopupMenu implements ActionListener{
        HashMap<Integer, String> hm_position;
        HashMap<String, Integer> hm_string;      
        int Axis;
        String CurrentSelection;      
        private ArrayList<PopupMenuAxisListener> listeners = new ArrayList<PopupMenuAxisListener>();
                
        public SelectPlottingDataMenu(ArrayList<String> AvailableData, int Axis){
                    
                    this.Axis = Axis;
                    
                    String tempString;
                    Integer tempInteger;

                    //iterate through list and add JMenu items
                    ListIterator<String> itr = AvailableData.listIterator();
                    HashMap<String, Integer> hm_string = new HashMap<String, Integer> ();
                    HashMap<Integer, String> hm_position = new HashMap<Integer, String> ();
                    
                    while(itr.hasNext()){ 
                        tempString = itr.next();
                        tempInteger = itr.nextIndex();
                        this.add(new JMenuItem(tempString)).addActionListener(this);
                        hm_string.put(tempString, tempInteger);
                        
                        //IJ.log("HashMap key: " + tempString + ". Value: " + hm_string.get(tempString) + " at, " + tempInteger);
                        //IJ.log("HashMap size: " + hm_string.size());
                        //hm_position.put(tempInteger, tempString);
                    }  
                }
        
    
                

    @Override
    public void actionPerformed(ActionEvent ae) {
        notifyPopupMenuAxisListeners(Axis, ae.getActionCommand()); 
    }  
    
    public void addPopupMenuAxisListener(PopupMenuAxisListener listener) {
        listeners.add(listener);
    }

    public void notifyPopupMenuAxisListeners(int axis, String position) {
        for (PopupMenuAxisListener listener : listeners) {
            listener.changeAxes(Axis, position);
        }
    }
}

//    class SelectLUTMenu extends JPopupMenu implements ActionListener{
//
//        HashMap<Integer, String> hm_position;
//        HashMap<String, Integer> hm_string;      
//        int Axis;
//        String CurrentSelection;
//        
//        private ArrayList<PopupMenuAxisLUTListener> listeners = new ArrayList<PopupMenuAxisLUTListener>();
//                
//        public SelectLUTMenu(ArrayList<String> AvailableData, int Axis){
//                    
//                    this.Axis = Axis;                   
//                    String tempString;
//                    Integer tempInteger;                        
//                    //iterate through list and add JMenu items
//                    ListIterator<String> itr = AvailableData.listIterator();
//                    HashMap<String, Integer> hm_string = new HashMap<String, Integer> ();
//                    HashMap<Integer, String> hm_position = new HashMap<Integer, String> ();
//                    
//                    while(itr.hasNext()){ 
//                        tempString = itr.next();
//                        tempInteger = itr.nextIndex();
//                        this.add(new JMenuItem(tempString)).addActionListener(this);
//                        hm_string.put(tempString, tempInteger);
//                        
//                        //IJ.log("HashMap key: " + tempString + ". Value: " + hm_string.get(tempString) + " at, " + tempInteger);
//                        //IJ.log("HashMap size: " + hm_string.size());
//                        //hm_position.put(tempInteger, tempString);
//                    }  
//                }
//        @Override
//    public void actionPerformed(ActionEvent ae) {
//            //get location?
//    
//       // notifyPopupMenuAxisListeners(Axis, ae.getActionCommand());
//    } 
//    
//    public void addPopupMenuAxisLUTListener(PopupMenuAxisLUTListener listener) {
//        listeners.add(listener);
//    }
//
//    public void notifyPopupMenuAxisLUTListener(String position) {
//        for (PopupMenuAxisLUTListener listener : listeners) {
//            listener.changeAxisLUT(position);
//        }
//    }
//    }
}
