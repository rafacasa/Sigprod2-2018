package br.edu.ifrs.farroupilha.sigprod2.frontend.panels;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Navegacao extends JPanel {

    private static final Logger LOGGER = LogManager.getLogger(Navegacao.class.getName());
    private JButton up;
    private JButton down;
    private JButton left;
    private JButton right;
    private int camadaAtual;
    private int pontoAtual;
    private List<List<Ponto>> camadasRedeReduzida;
    private Rede rede;

    public Navegacao(Rede rede) {
        this.rede = rede;
        this.camadasRedeReduzida = rede.getCamadasRedeReduzida();
        this.camadaAtual = 1;
        this.pontoAtual = 0;
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
    }

    private void placeComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));

        this.add(Box.createHorizontalGlue());
        this.add(this.left);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(this.up);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(this.down);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
        this.add(this.right);
        this.add(Box.createRigidArea(new Dimension(0, 20)));
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
        LOGGER.debug(evt.getActionCommand());
        boolean erro = false;
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = rede.getParentRedeReduzida(atual);
        boolean inicioRede = this.camadaAtual == 2;
        try {
            erro = Main.irPara(target, inicioRede, true);
        } catch (AjusteImpossivelException e) {
            Erro.mostraMensagemErro(this, "Houve problema ao se movimentar na rede");
            LOGGER.error("ERRO DO TRY CATCH DO IF ERRO - " + e.getMessage());
            this.atualizarPontoAtual();
            atual.resetAtributos(true);
        }
        if (erro) {
            this.camadaAtual--;
            this.pontoAtual = this.camadasRedeReduzida.get(this.camadaAtual).indexOf(target);
            this.atualizarPontoAtual();
            atual.resetAtributos();
        }
    }

    private void descerCamada(java.awt.event.ActionEvent evt) {
        LOGGER.debug(evt.getActionCommand());
        boolean erro = false;
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = rede.getDestinosRedeReduzida(atual).get(0);
        try {
            if (Main.irPara(target, false)) {
                this.camadaAtual++;
                this.pontoAtual = this.camadasRedeReduzida.get(this.camadaAtual).indexOf(target);
                this.atualizarPontoAtual();
                atual.resetAtributos();
            }
        } catch (java.lang.NullPointerException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "O equipamento superior não foi definido antecipadamente", "ERRO", JOptionPane.ERROR_MESSAGE);
            erro = true;
        } catch (AjusteImpossivelException e) {
            Erro.mostraMensagemErro(this, "Nao ha ajustes disponiveis para o equipamento abaixo");
            LOGGER.error("ERRO AO DESCER CAMADA - " + e.getMessage());
            erro = true;
        }
        if (erro) {
            try {
                Main.irPara(atual, this.camadaAtual == 1, true);
            } catch (AjusteImpossivelException e) {
                Erro.mostraMensagemErro(this, "Houve problema ao se movimentar na rede");
                LOGGER.error("ERRO DO TRY CATCH DO IF ERRO - " + e.getMessage());
            }
            this.atualizarPontoAtual();
            atual.resetAtributos(true);
        }
    }

    private void proximoPonto(java.awt.event.ActionEvent evt) {
        LOGGER.debug(evt.getActionCommand());
        boolean erro = false;
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = this.camadasRedeReduzida.get(this.camadaAtual).get(this.pontoAtual + 1);
        boolean inicioRede = this.camadaAtual == 1;
        try {
            if (Main.irPara(target, inicioRede)) {
                this.pontoAtual++;
                this.atualizarPontoAtual();
                atual.resetAtributos();
            }
        } catch (java.lang.NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "O equipamento superior não foi definido antecipadamente", "ERRO", JOptionPane.ERROR_MESSAGE);
            erro = true;
        } catch (AjusteImpossivelException e) {
            Erro.mostraMensagemErro(this, "Nao ha ajustes disponiveis para o equipamento abaixo");
            LOGGER.error("ERRO AO DESCER CAMADA - " + e.getMessage());
            erro = true;
        }
        if (erro) {
            try {
                Main.irPara(atual, inicioRede, true);
            } catch (AjusteImpossivelException e) {
                Erro.mostraMensagemErro(this, "Houve problema ao se movimentar na rede");
                LOGGER.error("ERRO DO TRY CATCH DO IF ERRO PROXIMO PONTO - " + e.getMessage());
            }
            this.atualizarPontoAtual();
            atual.resetAtributos(true);
        }
    }

    private void pontoAnterior(java.awt.event.ActionEvent evt) {
        LOGGER.debug(evt.getActionCommand());
        boolean erro = false;
        this.desabilitarBotoes();
        Ponto atual = this.getPontoAtual();
        Ponto target = this.camadasRedeReduzida.get(this.camadaAtual).get(this.pontoAtual - 1);
        boolean inicioRede = this.camadaAtual == 1;
        try {
            if (Main.irPara(target, inicioRede)) {
                this.pontoAtual--;
                this.atualizarPontoAtual();
                atual.resetAtributos();
            }
        } catch (java.lang.NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "O equipamento superior não foi definido antecipadamente", "ERRO", JOptionPane.ERROR_MESSAGE);
            erro = true;
            ex.printStackTrace();
        } catch (AjusteImpossivelException e) {
            Erro.mostraMensagemErro(this, "Nao ha ajustes disponiveis para o equipamento abaixo");
            LOGGER.error("ERRO AO DESCER CAMADA - " + e.getMessage());
            erro = true;
        }
        if (erro) {
            try {
                Main.irPara(atual, inicioRede, true);
            } catch (AjusteImpossivelException e) {
                Erro.mostraMensagemErro(this, "Houve problema ao se movimentar na rede");
                LOGGER.error("ERRO DO TRY CATCH DO IF ERRO PONTO PONTO ANTERIOR - " + e.getMessage());
            }
            this.atualizarPontoAtual();
            atual.resetAtributos(true);
        }
    }

    public Ponto getPontoAtual() {
        return this.camadasRedeReduzida.get(camadaAtual).get(pontoAtual);
    }
}
