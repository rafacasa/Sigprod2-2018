package br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels;

import br.edu.ifrs.farroupilha.sigprod2.gui.AjusteFrame;
import java.awt.Component;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelNavegacao extends JPanel {

    private List<List<Ponto>> camadasRedeReduzida;
    private Rede rede;
    private int camadaAtual, pontoAtual;
    private AjusteFrame ajusteFrame;
    private boolean coordenograma;
    private JButton up, down, left, right;
    private JLabel labelCamadas, labelPontos;
    private JToggleButton botaoCoordenograma;

    public PanelNavegacao(Rede rede, AjusteFrame ajusteFrame) {
        this.rede = rede;
        this.camadasRedeReduzida = rede.getCamadasRedeReduzida();
        this.ajusteFrame = ajusteFrame;
        this.camadaAtual = 1;
        this.pontoAtual = 0;
        this.coordenograma = false;
        this.initComponents();
        this.placeComponents();
    }

    private void initComponents() {
        this.up = new JButton("↑");
        this.down = new JButton("↓");
        this.left = new JButton("←");
        this.right = new JButton("→");

        this.up.addActionListener(this::subirCamada);
        this.down.addActionListener(this::descerCamada);
        this.left.addActionListener(this::pontoAnterior);
        this.right.addActionListener(this::proximoPonto);

        this.up.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.down.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.left.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.right.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.up.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.down.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.left.setAlignmentY(Component.CENTER_ALIGNMENT);
        this.right.setAlignmentY(Component.CENTER_ALIGNMENT);

        this.up.setEnabled(false);
        this.down.setEnabled(false);
        this.left.setEnabled(false);
        this.right.setEnabled(false);

        this.labelCamadas = new JLabel("Camadas: ");
        this.labelPontos = new JLabel("Pontos: ");

        this.botaoCoordenograma = new JToggleButton("Exibir Coordenograma");
        this.botaoCoordenograma.addActionListener(this::botaoCoordenogramaActionPerformed);
    }

    private void placeComponents() {
        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        panel1.setLayout(new BoxLayout(panel1, BoxLayout.LINE_AXIS));
        panel2.setLayout(new BoxLayout(panel2, BoxLayout.LINE_AXIS));
        panel3.setLayout(new BoxLayout(panel3, BoxLayout.LINE_AXIS));

        panel1.add(Box.createRigidArea(new Dimension(10, 0)));
        panel1.add(this.labelCamadas);
        panel1.add(Box.createRigidArea(new Dimension(10, 0)));
        panel1.add(this.up);
        panel1.add(Box.createRigidArea(new Dimension(10, 0)));
        panel1.add(this.down);
        panel1.add(Box.createHorizontalGlue());

        panel2.add(Box.createRigidArea(new Dimension(10, 0)));
        panel2.add(this.labelPontos);
        panel2.add(Box.createRigidArea(new Dimension(10, 0)));
        panel2.add(this.left);
        panel2.add(Box.createRigidArea(new Dimension(10, 0)));
        panel2.add(this.right);
        panel2.add(Box.createHorizontalGlue());

        panel3.add(Box.createRigidArea(new Dimension(10, 0)));
        panel3.add(this.botaoCoordenograma);
        panel3.add(Box.createHorizontalGlue());

        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.add(Box.createVerticalGlue());
        this.add(panel3);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(panel1);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(panel2);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
    }

    public Ponto getPontoAtual() {
        return this.camadasRedeReduzida.get(camadaAtual).get(pontoAtual);
    }

    public void atualizarPontoAtual() {
        int limiteInferior = this.camadasRedeReduzida.size() - 1;
        int limiteDireita = this.camadasRedeReduzida.get(this.camadaAtual).size() - 1;

        if (this.camadaAtual == 1) {
            this.up.setEnabled(false);
        } else {
            this.up.setEnabled(true);
        }

        if (this.camadaAtual == limiteInferior) {
            this.down.setEnabled(false);
        } else {
            this.down.setEnabled(true);
        }

        if (this.pontoAtual == 0) {
            this.left.setEnabled(false);
        } else {
            this.left.setEnabled(true);
        }

        if (this.pontoAtual == limiteDireita) {
            this.right.setEnabled(false);
        } else {
            this.right.setEnabled(true);
        }
    }

    private void desabilitarBotoes() {
        this.up.setEnabled(false);
        this.down.setEnabled(false);
        this.left.setEnabled(false);
        this.right.setEnabled(false);
    }

    private void subirCamada(java.awt.event.ActionEvent evt) {
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = rede.getParentRedeReduzida(atual);
        boolean inicioRede = this.camadaAtual == 2;
        if (this.ajusteFrame.ajustar(target, inicioRede)) {
            this.camadaAtual--;
            this.pontoAtual = this.camadasRedeReduzida.get(this.camadaAtual).indexOf(target);
            this.atualizarPontoAtual();
            atual.resetAtributos();
        }
    }

    private void descerCamada(java.awt.event.ActionEvent evt) {
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = rede.getDestinosRedeReduzida(atual).get(0);
        try {
            if (this.ajusteFrame.ajustar(target, false)) {
                this.camadaAtual++;
                this.pontoAtual = this.camadasRedeReduzida.get(this.camadaAtual).indexOf(target);
                this.atualizarPontoAtual();
                atual.resetAtributos();
            }
        } catch (java.lang.NullPointerException ex) {
            JOptionPane.showMessageDialog(this.ajusteFrame, "O equipamento superior não foi definido antecipadamente", "ERRO", JOptionPane.ERROR_MESSAGE);
            this.ajusteFrame.ajustar(atual, this.camadaAtual == 1);
            this.atualizarPontoAtual();
            atual.resetAtributos(true);
        }
    }

    private void proximoPonto(java.awt.event.ActionEvent evt) {
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = this.camadasRedeReduzida.get(this.camadaAtual).get(this.pontoAtual + 1);
        boolean inicioRede = this.camadaAtual == 1;
        try {
            if (this.ajusteFrame.ajustar(target, inicioRede)) {
                this.pontoAtual++;
                this.atualizarPontoAtual();
                atual.resetAtributos();
            }
        } catch (java.lang.NullPointerException ex) {
            JOptionPane.showMessageDialog(this.ajusteFrame, "O equipamento superior não foi definido antecipadamente", "ERRO", JOptionPane.ERROR_MESSAGE);
            this.ajusteFrame.ajustar(atual, inicioRede);
            this.atualizarPontoAtual();
            atual.resetAtributos(true);
        }
    }

    private void pontoAnterior(java.awt.event.ActionEvent evt) {
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = this.camadasRedeReduzida.get(this.camadaAtual).get(this.pontoAtual - 1);
        boolean inicioRede = this.camadaAtual == 1;
        try {
            if (this.ajusteFrame.ajustar(target, inicioRede)) {
                this.pontoAtual--;
                this.atualizarPontoAtual();
                atual.resetAtributos();
            }
        } catch (java.lang.NullPointerException ex) {
            JOptionPane.showMessageDialog(this.ajusteFrame, "O equipamento superior não foi definido antecipadamente", "ERRO", JOptionPane.ERROR_MESSAGE);
            this.ajusteFrame.ajustar(atual, inicioRede);
            this.atualizarPontoAtual();
            atual.resetAtributos(true);
        }
    }

    private void botaoCoordenogramaActionPerformed(java.awt.event.ActionEvent evt) {
        if (this.botaoCoordenograma.getModel().isSelected()) {
            this.ajusteFrame.ativarCoordenograma();
        } else {
            this.ajusteFrame.desativarCoordenograma();
        }
        evt.toString();
    }
}
