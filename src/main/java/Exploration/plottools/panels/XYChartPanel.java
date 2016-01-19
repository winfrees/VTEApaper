/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exploration.plottools.panels;

import Objects.layercake.microVolume;
import VTC._VTC;
import ij.IJ;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Shape;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.axis.AxisLocation;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.LookupPaintScale;
import org.jfree.chart.renderer.xy.XYDotRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYShapeRenderer;
import org.jfree.chart.title.PaintScaleLegend;
import org.jfree.data.xy.DefaultXYZDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYZDataset;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author vinfrais
 */
public class XYChartPanel {

    private static final String title = "XY Chart";
    private ChartPanel chartPanel;
    private List plotValues = new ArrayList();
    
    public static int XAXIS = 1;
    public static int YAXIS = 2;
    
    static Color ZEROPERCENT = new Color(0,0,0);
    static Color TENPERCENT = new Color(0,0,82);
    static Color TWENTYPERCENT = new Color(61,0,178);
    static Color THIRTYPERCENT = new Color(122,0,227);
    static Color FORTYPERCENT = new Color(178,0,136);
    static Color FIFTYPERCENT = new Color(213,27,45);
    static Color SIXTYPERCENT = new Color(249,95,0);
    static Color SEVENTYPERCENT = new Color(255,140,0);
    static Color EIGHTYPERCENT = new Color(255,175,0);
    static Color NINETYPERCENT = new Color(255,190,0);
    static Color ALLPERCENT = new Color(255,250,50);

    public XYChartPanel() {
    }

    public XYChartPanel(List li, int x, int y, int l, String xText, String yText, String lText) {
        plotValues = li;
        chartPanel = createChart(x, y, l, xText, yText, lText);

        JFrame f = new JFrame(title);
        f.setTitle(title);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setLayout(new BorderLayout(0, 5));
        f.add(chartPanel, BorderLayout.CENTER);
        chartPanel.setOpaque(false);
        chartPanel.setMouseWheelEnabled(false);
        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        chartPanel.setPreferredSize(new Dimension(550,485));
        chartPanel.setBackground(new Color(0, 0, 0, 0));
        chartPanel.repaint();
        //chartPanel.set
        
        
   
        chartPanel.addChartMouseListener(new ChartMouseListener() {

            @Override
            public void chartMouseClicked(ChartMouseEvent cme) {
                chartPanel.getParent().repaint();
            }

            @Override
            public void chartMouseMoved(ChartMouseEvent cme) {
            }
        });

        chartPanel.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent e) {
                chartPanel.getParent().repaint();

            }

            @Override
            public void mouseMoved(MouseEvent e) {
                chartPanel.getParent().repaint();
            }
        });

        chartPanel.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mousePressed(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void mouseExited(MouseEvent e) {
                //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }

        });

        f.pack();

    }
    
    private ChartPanel createChart(int x, int y, int l, String xText, String yText, String lText) {
        
        XYShapeRenderer renderer = new XYShapeRenderer();
        double max = getMaximumOfData((ArrayList) plotValues.get(1), l);
        double min = this.getMinimumOfData((ArrayList) plotValues.get(1), l);
        double range = max-min;
        //double min = max - range;
        
        IJ.log("Min: " + min);
        IJ.log("Max: " + max);
        IJ.log("Range: " + range);
        
        if(max == 0){max = 1;}//capture zero range error for ps
        
        LookupPaintScale ps = new LookupPaintScale(min,max, new Color(0,0,0));

        renderer.setPaintScale(ps);
        Ellipse2D shape = new Ellipse2D.Double(0,0,6,6);
        renderer.setBaseShape(shape);
        ps.add(min, this.TENPERCENT);
        ps.add(min+(1*(range/10)), this.TENPERCENT);
        ps.add(min+(2*(range/10)), this.TWENTYPERCENT);
        ps.add(min+(3*(range/10)), this.THIRTYPERCENT);
        ps.add(min+(4*(range/10)), this.FORTYPERCENT);
        ps.add(min+(5*(range/10)), this.FIFTYPERCENT);
        ps.add(min+(6*(range/10)), this.SIXTYPERCENT);
        ps.add(min+(7*(range/10)), this.SEVENTYPERCENT);
        ps.add(min+(8*(range/10)), this.EIGHTYPERCENT);
        ps.add(min+(9*(range/10)), this.NINETYPERCENT);
        ps.add(max, this.ALLPERCENT);
        
        NumberAxis xAxis = new NumberAxis("");
        NumberAxis yAxis = new NumberAxis("");
 
        xAxis.setAutoRangeIncludesZero(false);
        yAxis.setAutoRangeIncludesZero(false);
      
        XYPlot plot = new XYPlot(createXYZDataset((ArrayList) plotValues.get(1), x, y, l), xAxis, yAxis, renderer);
        
        plot.setDomainPannable(false);
        plot.setRangePannable(false);

        try{
        if(getRangeofData((ArrayList) plotValues.get(1), x) > 16384){
            LogAxis logAxisX = new LogAxis();
            logAxisX.setAutoRange(true);
            plot.setDomainAxis(logAxisX);
        }
        
        if(getRangeofData((ArrayList) plotValues.get(1), y) > 16384){
             LogAxis logAxisY = new LogAxis();
             logAxisY.setAutoRange(true);
            plot.setRangeAxis(logAxisY);
        }}
        catch(NullPointerException e){};
        
        //JFreeChart chart = ChartFactory.createScatterPlot("Plot of " + xText + " vs. "+ yText, "", "", createDataset((ArrayList) plotValues.get(1), x, y), PlotOrientation.VERTICAL, false, true, false);
        
        JFreeChart chart = new JFreeChart("Plot of " + xText + " vs. "+ yText, plot);
        
        chart.removeLegend();
        
        
        NumberAxis lAxis = new NumberAxis(lText);
        lAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        PaintScaleLegend psl = new PaintScaleLegend(ps, lAxis);
        psl.setBackgroundPaint(VTC._VTC.BACKGROUND);
        psl.setPosition(RectangleEdge.RIGHT);
        psl.setMargin(4,4,40,4);
        psl.setAxisLocation(AxisLocation.BOTTOM_OR_RIGHT);
        
        
        chart.addSubtitle(psl);
        
        return new ChartPanel(chart, true, true, false, false, true);
    }
    
    private double getRangeofData(ArrayList alVolumes, int x){
        
        
        
        ListIterator litr = alVolumes.listIterator();
        
        
        ArrayList<Number> al = new ArrayList<Number>();
        
        Number low = 0;
        Number high = 0;       
        Number test;

        while (litr.hasNext()) {
            try {
                microVolume volume = (microVolume) litr.next();
                Number Corrected = processPosition(x, volume);
                al.add(Corrected);
                
                if(Corrected.floatValue() < low.floatValue()){low = Corrected;}
                if(Corrected.floatValue() > high.floatValue()){high = Corrected;}
                //Number yCorrected = processPosition(y, volume);

                //IJ.log("Plotted dataset: " + xCorrected + ", " + yCorrected);
                
            } catch (NullPointerException e) {
            }
        }
        
        return high.longValue()-low.longValue();
        
        
    }
    
    private double getMaximumOfData(ArrayList alVolumes, int x){
        
        ListIterator litr = alVolumes.listIterator();
        
       
        
        //ArrayList<Number> al = new ArrayList<Number>();

        Number high = 0;       

        while (litr.hasNext()) {
            try {
                microVolume volume = (microVolume) litr.next();
                Number Corrected = processPosition(x, volume);
                if(Corrected.floatValue() > high.floatValue()){high = Corrected;}          
            } catch (NullPointerException e) {
            }
        }     
        return high.longValue();

    }
    
    private double getMinimumOfData(ArrayList alVolumes, int x){
        
        ListIterator litr = alVolumes.listIterator();
        
        //ArrayList<Number> al = new ArrayList<Number>();
        
        

        Number low = getMaximumOfData(alVolumes,x);       

        while (litr.hasNext()) {
            try {
                microVolume volume = (microVolume) litr.next();
                Number Corrected = processPosition(x, volume);
                if(Corrected.floatValue() < low.floatValue()){low = Corrected;}          
            } catch (NullPointerException e) {
            }
        }     
        return low.longValue();

    }

    private XYDataset createDataset(ArrayList alVolumes, int x, int y) {

        XYSeriesCollection result = new XYSeriesCollection();
        XYSeries series = new XYSeries("dataset");

        //IJ.log("Total microVolumes to parse: " + alVolumes.size());
        ListIterator litr = alVolumes.listIterator();
        while (litr.hasNext()) {
            try {
                microVolume volume = (microVolume) litr.next();
                Number xCorrected = processPosition(x, volume);
                Number yCorrected = processPosition(y, volume);

                //IJ.log("Plotted dataset: " + xCorrected + ", " + yCorrected);
                series.add(xCorrected, yCorrected);
            } catch (NullPointerException e) {
            }
        }
        result.addSeries(series);
        
        return result;
    }
    
    private XYZDataset createXYZDataset(ArrayList alVolumes, int x, int y, int l) {

        DefaultXYZDataset result = new DefaultXYZDataset();
        int counter = 0;
        
        double[] xCorrected = new double[alVolumes.size()-1];
        double[] yCorrected = new double[alVolumes.size()-1];
        double[] lCorrected = new double[alVolumes.size()-1];

        ListIterator litr = alVolumes.listIterator();
        while (litr.hasNext() && counter <= alVolumes.size()-1) {
            try {
                microVolume volume = (microVolume) litr.next();
                xCorrected[counter] = processPosition(x, volume).doubleValue();
                yCorrected[counter] = processPosition(y, volume).doubleValue();
                lCorrected[counter] = processPosition(l, volume).doubleValue();
                counter++;
            } catch (NullPointerException e) {
            }
        }
        double[][] series = new double[][] {xCorrected, yCorrected, lCorrected};
        result.addSeries("first", series);
        return result;
    }
    
    private XYPlot getPlotRenderers(XYPlot plot, ArrayList alVolumes, int l){      
        //ArrayList<XYItemRenderer> al = new ArrayList<XYItemRenderer>();
        //XYItemRenderer[] renderers = new XYItemRenderer[alVolumes.size()];
        ListIterator litr = alVolumes.listIterator();
        int counter = 0;
        
        
        double maximum = this.getMaximumOfData(alVolumes, l);
        double minimum = this.getMinimumOfData(alVolumes, l);
        double value = 0.0;
        double compare = 0.0; 
        while (litr.hasNext()) {
            //try {
                XYDotRenderer rend = new XYDotRenderer();  
                microVolume volume = (microVolume) litr.next();
                rend.setBasePaint(Color.BLACK);
                try{
                value = processPosition(l, volume).doubleValue();}
                catch(NullPointerException e){ value = 0;}
                
                compare = (value-minimum)/(value-maximum);
                IJ.log("Compare: " + compare);
                
                //rend.set

                if(compare >= 0.0 && compare < 0.05){rend.setBasePaint(this.ZEROPERCENT);}
                else if(compare >= 0.05 && compare < 0.1){rend.setBasePaint(this.TENPERCENT);}
                else if(compare >= 0.2 && compare < 0.3){rend.setBasePaint(this.TWENTYPERCENT);}
                else if(compare >= 0.3 && compare < 0.4){rend.setBasePaint(this.THIRTYPERCENT);}
                else if(compare >= 0.4 && compare < 0.5){rend.setBasePaint(this.FORTYPERCENT);}
                else if(compare >= 0.5 && compare < 0.6){rend.setBasePaint(this.FIFTYPERCENT);}
                else if(compare >= 0.6 && compare < 0.7){rend.setBasePaint(this.SIXTYPERCENT);}
                else if(compare >= 0.7 && compare < 0.8){rend.setBasePaint(this.SEVENTYPERCENT);}
                else if(compare >= 0.8 && compare < 0.9){rend.setBasePaint(this.EIGHTYPERCENT);}
                else if(compare >= 0.9 && compare < 0.95){rend.setBasePaint(this.NINETYPERCENT);}
                else if(compare >= 0.95 && compare < 1){rend.setBasePaint(this.ALLPERCENT);}
                else {rend.setBasePaint(Color.BLACK);}
                Shape shape2 = new Ellipse2D.Double(0, 0, 2, 2);
                rend.setBaseShape(shape2);
                plot.setRenderer(counter, rend);
                
                counter++;        
            //} catch (NullPointerException e) {
            //}
        }
        IJ.log("Renderer 1: " + plot.getRenderer(0) + " Renderer 2: " + plot.getRenderer(2));
        return plot;
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
            return (Number) volume.getAnalysisResultsVolume()[row][column];
        }
    }

    public ChartPanel getChartPanel() {
        return chartPanel;
    }
    public void setChartPanelRanges(int axis, double low, double high){
        
        XYPlot plot = (XYPlot)this.chartPanel.getChart().getPlot();
        ValueAxis newaxis = new NumberAxis();
        
        newaxis.setLowerBound(low);
        newaxis.setUpperBound(high);
        
        if(axis == this.XAXIS){
        plot.setDomainAxis(newaxis);
        } else if(axis == this.YAXIS){
         plot.setRangeAxis(newaxis);   
        }    
    }
}
