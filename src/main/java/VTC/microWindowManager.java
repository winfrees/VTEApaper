package VTC;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.WindowManager;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.DefaultListModel;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vinfrais
 */
public class microWindowManager extends javax.swing.JFrame {

    private int tab;
    /**
     * Creates new form microWindowManager
     */
    public microWindowManager(javax.swing.JList images) {

        OpenImages = images;
        

        GuiSetup();
        initComponents();
        
        comment.setVisible(false);

        //plotNames[0] = "NO PLOTS CREATED"; 
        //analysisNames[0] = "NO ANALYSES";
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jScrollPane10 = new javax.swing.JScrollPane();
        OpenImages = new javax.swing.JList();
        SelectImage = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        comment = new javax.swing.JLabel();

        setTitle("VTC-Select Image");
        setBackground(VTC._VTC.BACKGROUND);
        setIconImages(null);
        setMinimumSize(new java.awt.Dimension(217, 320));
        setName("microWindowManager"); // NOI18N
        setResizable(false);

        jPanel1.setBackground(VTC._VTC.BACKGROUND);
        jPanel1.setMaximumSize(new java.awt.Dimension(305, 180));
        jPanel1.setMinimumSize(new java.awt.Dimension(305, 180));
        jPanel1.setPreferredSize(new java.awt.Dimension(305, 180));

        OpenImages.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        OpenImages.setModel(new javax.swing.AbstractListModel() {
            String[] strings = populateImages();
            public int getSize() { return strings.length; }
            public Object getElementAt(int i) { return strings[i]; }
        }
    );
    OpenImages.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
    jScrollPane10.setViewportView(OpenImages);

    SelectImage.setText("Select");
    SelectImage.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
    SelectImage.addActionListener(new java.awt.event.ActionListener() {
        public void actionPerformed(java.awt.event.ActionEvent evt) {
            SelectImageActionPerformed(evt);
        }
    });

    comment.setFont(new java.awt.Font("Lucida Grande", 0, 10)); // NOI18N
    comment.setText("Select multiple for batch");

    javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
    jPanel1.setLayout(jPanel1Layout);
    jPanel1Layout.setHorizontalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(comment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(SelectImage, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(jSeparator1)))
            .addGap(0, 0, Short.MAX_VALUE))
    );
    jPanel1Layout.setVerticalGroup(
        jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(jPanel1Layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jScrollPane10, javax.swing.GroupLayout.DEFAULT_SIZE, 195, Short.MAX_VALUE)
            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                .addComponent(SelectImage)
                .addComponent(comment))
            .addGap(1, 1, 1)
            .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 13, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addContainerGap())
    );

    javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
    getContentPane().setLayout(layout);
    layout.setHorizontalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addGap(6, 6, 6)
            .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 201, Short.MAX_VALUE)
            .addContainerGap())
    );
    layout.setVerticalGroup(
        layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
        .addGroup(layout.createSequentialGroup()
            .addContainerGap()
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 256, Short.MAX_VALUE)
            .addContainerGap())
    );

    pack();
    }// </editor-fold>//GEN-END:initComponents

    private void SelectImageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SelectImageActionPerformed

     ListActionPerformed(evt);
    }//GEN-LAST:event_SelectImageActionPerformed

    protected void ListActionPerformed(ActionEvent evt){
               int i = OpenImages.getSelectedIndex();
        notifyImageSelectionListeners(i, this.tab);
        this.setVisible(false);
        //IJ.log("Image number: " + i + ", for tab#: " + (tab)); 
    }
    
    protected String[] populateImages() {
        int[] windowList = WindowManager.getIDList();

        if (windowList == null) {
            String[] titles = new String[1];
            titles[0] = "NO OPEN IMAGES";
            //IJ.log("NO IMAGES...  :(");
            return titles;
        }

        String[] titles = new String[windowList.length];

        for (int i = 0; i < windowList.length; i++) {
            //if(WindowManager.getUn   )
            ImagePlus imp_temp = WindowManager.getImage(windowList[i]);
            if (!imp_temp.getTitle().contains("Plot")) {
                titles[i] = imp_temp != null ? imp_temp.getTitle() : "";
            }
        }
        return titles;
    }

    public void updateImages() {
        OpenImages.setModel(new javax.swing.AbstractListModel() {
            String[] strings = populateImages();

            @Override
            public int getSize() {
                return strings.length;
            }

            @Override
            public Object getElementAt(int i) {
                return strings[i];
            }
        });
    }

    private ImagePlus getSelectedImagePlus() {
        int i = OpenImages.getSelectedIndex();
        ImagePlus imp = WindowManager.getImage(i + 1);
        //IJ.log("Getting image file... at:" + i + 1);
        return imp;

    }

    private Color getBackgroundColor() {

        return ImageJ.backgroundColor;
    }

//Analyze button events
    public void addImageSelectionListener(ImageSelectionListener listener) {
        listeners.add(listener);
        //IJ.log("Listener added: "+ listener.toString());
        //IJ.log("There are now: " + listeners.size() + " listeners.");
        
    }

    private void notifyImageSelectionListeners(int i, int tab) {
         //IJ.log("Getting image file..." + (i+1) + " for " + tab);
         //IJ.log("Listener count: " + listeners.size());
        for (ImageSelectionListener listener : listeners) {
            
            ImagePlus imp = WindowManager.getImage(i + 1);
            listener.onSelect(imp, tab);

        }
    }

   public void getImageFile(int i){
       this.setVisible(true);
       this.tab = i;
   }

  


    //analysis table and plot table update events
    /**
     *
     * @param result
     */
    //   public void onUpdatePlots(DefaultListModel result){IJ.log("Updating plot lists.."); this.plots = result; OpenPlots.setModel(plots);}
            /**
             *
             * @param result
             */
            //    @Override
            //   public void onUpdateAnalysis(DefaultListModel result){IJ.log("Updating analysis lists.."); this.analysis = result; //Analyses.setModel(analysis);
            //   }
            /**
             * @param result
             */

    private void GuiSetup() {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    //javax.swing.UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");

                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(microWindowManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(microWindowManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(microWindowManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(microWindowManager.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                //new microWindowManager().setVisible(true);
            }
        });

    }

    private DefaultListModel plots = new DefaultListModel();
    private DefaultListModel analysis = new DefaultListModel();

    //private String[] plotNames = new String[10];
    //private String[] analysisNames = new String[10];
    protected ArrayList<ImageSelectionListener> listeners = new ArrayList<ImageSelectionListener>();

    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JList OpenImages;
    protected javax.swing.JButton SelectImage;
    protected javax.swing.JLabel comment;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane10;
    private javax.swing.JSeparator jSeparator1;
    // End of variables declaration//GEN-END:variables
}