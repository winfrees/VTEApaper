/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Exploration.plottools.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import javax.swing.JPanel;

/**
 *
 * @author vinfrais
 */
public class DefaultPlotPanels implements PlotAxesPanels {

    protected JPanel HeaderPanel = new JPanel(true);
    protected JPanel FooterPanel = new JPanel(true);
    protected JPanel LeftPanel = new JPanel(true);
    protected JPanel RightPanel = new JPanel(true);

    public DefaultPlotPanels() {
        HeaderPanel.setBackground(VTC._VTC.BACKGROUND);
        FooterPanel.setBackground(VTC._VTC.BACKGROUND);
        LeftPanel.setBackground(VTC._VTC.BACKGROUND);
        RightPanel.setBackground(VTC._VTC.BACKGROUND);
    }
    

    @Override
    public JPanel getBorderPanelHeader() {
        return HeaderPanel;
    }

    @Override
    public JPanel getBorderPanelFooter() {
        return FooterPanel;
    }

    @Override
    public JPanel getBorderPanelLeft() {
        return LeftPanel;
    }

    @Override
    public JPanel getBorderPanelRight() {
        return RightPanel;
    }

}
