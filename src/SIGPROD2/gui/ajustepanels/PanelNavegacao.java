package SIGPROD2.gui.ajustepanels;

import SIGPROD2.gui.AjusteFrame;
import java.awt.Component;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.plaf.basic.BasicArrowButton;
import sigprod2.modelo.Ponto;
import sigprod2.modelo.Rede;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelNavegacao extends JPanel {

    private List<List<Ponto>> camadasRedeReduzida;
    private Rede rede;
    private int camadaAtual, pontoAtual;
    private AjusteFrame ajusteFrame;

    private BasicArrowButton up, down, left, right;

    public PanelNavegacao(Rede rede, AjusteFrame ajusteFrame) {
        this.rede = rede;
        this.camadasRedeReduzida = rede.getCamadasRedeReduzida();
        this.ajusteFrame = ajusteFrame;
        this.camadaAtual = 1;
        this.pontoAtual = 0;
        this.initComponents();
        this.placeComponents();
    }

    private void initComponents() {
        this.up = new BasicArrowButton(BasicArrowButton.NORTH);
        this.down = new BasicArrowButton(BasicArrowButton.SOUTH);
        this.left = new BasicArrowButton(BasicArrowButton.WEST);
        this.right = new BasicArrowButton(BasicArrowButton.EAST);

        this.up.addActionListener(this::subirCamada);
        this.down.addActionListener(this::descerCamada);
        this.left.addActionListener(this::pontoAnterior);
        this.right.addActionListener(this::proximoPonto);

        this.up.setEnabled(false);
        this.down.setEnabled(false);
        this.left.setEnabled(false);
        this.right.setEnabled(false);
    }

    private void placeComponents() {
        this.up.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.down.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.left.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.right.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel panel1 = new JPanel();
        JPanel panel2 = new JPanel();
        JPanel panel3 = new JPanel();

        panel2.setLayout(new BoxLayout(panel2, BoxLayout.PAGE_AXIS));

        panel1.add(this.left);
        panel2.add(this.up);
        panel2.add(Box.createVerticalGlue());
        panel2.add(this.down);
        panel3.add(this.right);

        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        this.add(panel1);
        this.add(panel2);
        this.add(panel3);
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
        }
    }

    private void descerCamada(java.awt.event.ActionEvent evt) {
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = rede.getDestinosRedeReduzida(atual).get(0);
        if (this.ajusteFrame.ajustar(target, false)) {
            this.camadaAtual++;
            this.pontoAtual = this.camadasRedeReduzida.get(this.camadaAtual).indexOf(target);
            this.atualizarPontoAtual();
        }
    }

    private void proximoPonto(java.awt.event.ActionEvent evt) {
        this.desabilitarBotoes();
        Ponto target = this.camadasRedeReduzida.get(this.camadaAtual).get(this.pontoAtual + 1);
        boolean inicioRede = this.camadaAtual == 1;
        if (this.ajusteFrame.ajustar(target, inicioRede)) {
            this.pontoAtual++;
            this.atualizarPontoAtual();
        }
    }

    private void pontoAnterior(java.awt.event.ActionEvent evt) {
        this.desabilitarBotoes();
        Ponto target = this.camadasRedeReduzida.get(this.camadaAtual).get(this.pontoAtual - 1);
        boolean inicioRede = this.camadaAtual == 1;
        if (this.ajusteFrame.ajustar(target, inicioRede)) {
            this.pontoAtual--;
            this.atualizarPontoAtual();
        }
    }

}
