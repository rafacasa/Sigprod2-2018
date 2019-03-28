/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifrs.farroupilha.sigprod2.gui.mainframepanels;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Arquivo;
import java.awt.event.ActionEvent;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import br.edu.ifrs.farroupilha.sigprod2.gui.MainFrame;

/**
 *
 * @author Rafael Casa
 */
public class Menu extends JMenuBar{

    MainFrame frame;
    Arquivo localSalvo = null;
    
    JMenu arquivo, sobre, config, dados;
    JMenuItem sair, abrir, salvar, salvarComo, importar, exportar, novo, fechar;
    JMenuItem sobreSigprod2;
    JMenuItem bdConfig, limparAjustes;
    JMenuItem elo;
    JMenu rele, religador;
    JMenuItem releSelect, releAdd, religadorSelect, religadorAdd;
    
    public Menu(MainFrame f) {
        super();
        this.frame = f;
        initComponents();
    }
    
    private void initComponents() {
        this.sair = new JMenuItem("Sair");
        this.sair.addActionListener(this::sairActionPerformed);        
        this.abrir = new JMenuItem("Abrir Rede");
        this.abrir.addActionListener(this::abrirActionPerformed);       
        this.salvar = new JMenuItem("Salvar");
        this.salvar.addActionListener(this::salvarActionPerformed);        
        this.salvarComo = new JMenuItem("Salvar Como");
        this.salvarComo.addActionListener(this::salvarComoActionPerformed); 
        this.fechar = new JMenuItem("Fechar Rede");
        this.fechar.addActionListener(this::fecharActionPerformed);
        this.importar = new JMenuItem("Importar .ABCEEE");
        this.importar.addActionListener(this::importarActionPerformed);       
        this.exportar = new JMenuItem("Exportar");
        this.exportar.addActionListener(this::exportarActionPerformed);       
        this.novo = new JMenuItem("Nova Rede");
        this.novo.addActionListener(this::novoActionPerformed);
        this.arquivo = new JMenu("Arquivo");
        this.arquivo.add(this.novo);
        this.arquivo.add(this.abrir);
        this.arquivo.add(this.salvar);
        this.arquivo.add(this.salvarComo);
        this.arquivo.add(this.fechar);
        this.arquivo.add(this.importar);
        this.arquivo.add(this.exportar);
        this.arquivo.add(this.sair);
        
        this.sobreSigprod2 = new JMenuItem("Sobre o SIGPROD2..");
        this.sobreSigprod2.addActionListener(this::sobreSigprod2ActionPerformed);
        this.sobre = new JMenu("Sobre");
        this.sobre.add(this.sobreSigprod2);
        
        this.bdConfig = new JMenuItem("Banco de Dados");
        this.bdConfig.addActionListener(this::bdConfigActionPerformed);
        this.limparAjustes = new JMenuItem("Limpar Ajustes");
        this.limparAjustes.addActionListener(this::limparAjustesActionPerformed);
        this.config = new JMenu("Opções");
        this.config.add(this.limparAjustes);
        this.config.add(this.bdConfig);
        
        this.releAdd = new JMenuItem("Adicionar Relé");
        this.releAdd.addActionListener(this::releAddActionPerformed);
        this.releSelect = new JMenuItem("Ver Relés");
        this.releSelect.addActionListener(this::releSelectActionPerformed);
        this.rele = new JMenu("Relé");
        this.rele.add(this.releAdd);
        this.rele.add(this.releSelect);
        this.religadorAdd = new JMenuItem("Adicionar Religador");
        this.religadorAdd.addActionListener(this::religadorAddActionPerformed);
        this.religadorSelect = new JMenuItem("Ver Religadores");
        this.religadorSelect.addActionListener(this::religadorSelectActionPerformed);
        this.religador = new JMenu("Religador");
        this.religador.add(this.religadorAdd);
        this.religador.add(this.religadorSelect);
        this.elo = new JMenuItem("Elo");
        this.elo.addActionListener(this::eloActionPerformed);
        this.dados = new JMenu("Dados");
        this.dados.add(this.elo);
        this.dados.add(this.rele);
        this.dados.add(this.religador);
        
        this.add(this.arquivo);
        this.add(this.dados);
        this.add(this.config);
        this.add(this.sobre);
    }
    
    private void sairActionPerformed(ActionEvent e) {
        System.exit(0);
    }
    private void abrirActionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i = chooser.showOpenDialog(this.frame);
        switch(i) {
            case JFileChooser.APPROVE_OPTION:
                Arquivo arquivo = new Arquivo(chooser.getSelectedFile());
                this.frame.abrirRede(arquivo);
                break;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
                break;
        }
    }
    private void salvarActionPerformed(ActionEvent e) {
        if(this.localSalvo == null) {
            this.salvarComoActionPerformed(e);
        } else {
            this.frame.getRede().salvarRede(this.localSalvo);
            this.frame.setSalvo(true);
        }
    }
    private void salvarComoActionPerformed(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int i = chooser.showSaveDialog(this.frame);
        switch(i) {
            case JFileChooser.APPROVE_OPTION:
                Arquivo arquivo = new Arquivo(chooser.getSelectedFile());
                this.frame.getRede().salvarRede(arquivo);
                this.localSalvo = arquivo;
                this.frame.setTitle("SIGPROD2 - " + arquivo.getFile().getAbsolutePath());
                this.frame.setSalvo(true);
                break;
            case JFileChooser.CANCEL_OPTION:
            case JFileChooser.ERROR_OPTION:
                break;
        }
    }
    private void fecharActionPerformed(ActionEvent e) {
        this.frame.fecharRede();
    }
    private void importarActionPerformed(ActionEvent e) {
        
    }
    private void exportarActionPerformed(ActionEvent e) {
        
    }
    private void novoActionPerformed(ActionEvent e) {
        this.frame.fecharRede();
        this.importarActionPerformed(e);
    }
    private void sobreSigprod2ActionPerformed(ActionEvent e) {
        
    }
    private void limparAjustesActionPerformed(ActionEvent e) {
        this.frame.getRede().limparAjustes();
        this.frame.limparDataPanel();
        this.frame.limparInfoPanel();
        this.frame.setSalvo(false);
    }
    private void bdConfigActionPerformed(ActionEvent e) {
        
    }
    private void releAddActionPerformed(ActionEvent e) {
        
    }
    private void releSelectActionPerformed(ActionEvent e) {
        
    }
    private void religadorAddActionPerformed(ActionEvent e) {
        
    }
    private void religadorSelectActionPerformed(ActionEvent e) {
        
    }
    private void eloActionPerformed(ActionEvent e) {
        
    }
}
