/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultmain;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.graphstream.graph.Node;

/**
 *
 * @author Rafael Casa
 */
public class Informacoes extends JPanel{
    private Node node;
    private Ponto ponto;
    
    private JLabel labelNomeNo, labelIcc3f, labelIcc2f, labelIccft, labelIccftmin, labelIcarga;

    public Informacoes(Node n) {
        super();
        this.node = n;
        this.ponto = n.getAttribute("classe", Ponto.class);
        initComponents();
    }
    
    private void initComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        initItens();
        initPanels();
    }
    
    private void initItens() {
        this.labelNomeNo = new JLabel("Nó: " + this.ponto.getNome());
        this.labelNomeNo.setFont(new Font("Tahoma", 0, 14));
        this.labelNomeNo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.labelIcc3f = new JLabel("Icc3F: " + this.ponto.getIcc3f());
        this.labelIcc3f.setFont(new Font("Tahoma", 0, 14));
        this.labelIcc3f.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.labelIcc2f = new JLabel("Icc2F: " + this.ponto.getIcc2f());
        this.labelIcc2f.setFont(new Font("Tahoma", 0, 14));
        this.labelIcc2f.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.labelIccft = new JLabel("IccFT: " + this.ponto.getIccft());
        this.labelIccft.setFont(new Font("Tahoma", 0, 14));
        this.labelIccft.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.labelIccftmin = new JLabel("IccFT Mínima: " + this.ponto.getIccftmin());
        this.labelIccftmin.setFont(new Font("Tahoma", 0, 14));
        this.labelIccftmin.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        this.labelIcarga = new JLabel("I Carga: " + this.ponto.getIcarga());
        this.labelIcarga.setFont(new Font("Tahoma", 0, 14));
        this.labelIcarga.setAlignmentX(Component.CENTER_ALIGNMENT);
    }
    
    private void initPanels() {
        this.add(Box.createHorizontalGlue());
        this.add(this.labelNomeNo);
        this.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(this.labelIcc3f);
        this.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(this.labelIcc2f);
        this.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(this.labelIccft);
        this.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(this.labelIccftmin);
        this.add(Box.createRigidArea(new Dimension(20, 0)));
        this.add(this.labelIcarga);
    }
    
    
}
