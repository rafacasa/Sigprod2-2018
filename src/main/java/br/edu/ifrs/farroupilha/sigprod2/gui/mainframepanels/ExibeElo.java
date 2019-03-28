package br.edu.ifrs.farroupilha.sigprod2.gui.mainframepanels;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.graphstream.graph.Node;
import br.edu.ifrs.farroupilha.sigprod2.frontend.listeners.NodeClickDefaultListener;

/**
 *
 * @author Rafael Luiz Casa
 */
public class ExibeElo extends JPanel {

    private Ponto ponto;
    private Node node;
    private NodeClickDefaultListener listener;
    private JPanel panelTipo, panelCorrente, panelNomeNo;
    private JLabel labelTitle, labelTipoEloFixa, labelCorrenteFixa, labelNomeNoFixa, labelNomeNo, labelTipoElo, labelCorrente, dadosAjuste;
    private JButton botaoRemover;

    public ExibeElo(Node n, NodeClickDefaultListener listener) {
        this.listener = listener;
        this.node = n;
        this.ponto = n.getAttribute("classe", Ponto.class);
        initComponents();
    }

    private void initComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        initItens();
        initPanels();
        int height = getPreferredSize().height;
        setPreferredSize(new Dimension(11000, height));
        height = getMaximumSize().height;
        setMaximumSize(new Dimension(11000, height));

    }

    private void initItens() {
        this.labelTitle = new JLabel("Elo Fusível");
        this.labelTitle.setFont(new Font("Tahoma", 0, 40));
        this.labelTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.labelTipoEloFixa = new JLabel("Tipo do Elo:");
        this.labelTipoEloFixa.setFont(new Font("Tahoma", 0, 16));

        this.labelTipoElo = new JLabel("K");
        this.labelTipoElo.setFont(new Font("Tahoma", 0, 16));

        this.labelCorrenteFixa = new JLabel("Corrente:");
        this.labelCorrenteFixa.setFont(new Font("Tahoma", 0, 16));

        this.labelCorrente = new JLabel(((Elo) this.ponto.getEquipamentoInstalado()).getCorrenteNominal() + "");
        this.labelCorrente.setFont(new Font("Tahoma", 0, 16));

        this.labelNomeNoFixa = new JLabel("Nó:");
        this.labelNomeNoFixa.setFont(new Font("Tahoma", 0, 16));

        this.labelNomeNo = new JLabel(this.ponto.getNome());
        this.labelNomeNo.setFont(new Font("Tahoma", 0, 16));

        this.dadosAjuste = new JLabel(((Elo) this.ponto.getEquipamentoInstalado()).getDadosAjuste());
        this.dadosAjuste.setFont(new Font("Tahoma", 0, 16));
        this.dadosAjuste.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.botaoRemover = new JButton("Remover Equipamento Selecionado");
        this.botaoRemover.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.botaoRemover.addActionListener(this::botaoRemoverActionPerformed);
    }

    private void initPanels() {
        panelTipo = new JPanel();
        panelTipo.setLayout(new BoxLayout(panelTipo, BoxLayout.LINE_AXIS));
        panelTipo.add(labelTipoEloFixa);
        panelTipo.add(Box.createRigidArea(new Dimension(5, 0)));
        panelTipo.add(labelTipoElo);
        panelTipo.add(Box.createRigidArea(new Dimension(20, 0)));

        panelCorrente = new JPanel();
        panelCorrente.setLayout(new BoxLayout(panelCorrente, BoxLayout.LINE_AXIS));
        panelCorrente.add(labelCorrenteFixa);
        panelCorrente.add(Box.createRigidArea(new Dimension(5, 0)));
        panelCorrente.add(labelCorrente);
        panelCorrente.add(Box.createRigidArea(new Dimension(20, 0)));

        panelNomeNo = new JPanel();
        panelNomeNo.setLayout(new BoxLayout(panelNomeNo, BoxLayout.LINE_AXIS));
        panelNomeNo.add(labelNomeNoFixa);
        panelNomeNo.add(Box.createRigidArea(new Dimension(5, 0)));
        panelNomeNo.add(labelNomeNo);
        panelNomeNo.add(Box.createRigidArea(new Dimension(20, 0)));

        add(labelTitle);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(panelTipo);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(panelCorrente);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(panelNomeNo);
        add(Box.createVerticalGlue());
        add(botaoRemover);
        add(Box.createRigidArea(new Dimension(0, 30)));
        add(dadosAjuste);
    }

    private void botaoRemoverActionPerformed(java.awt.event.ActionEvent evt) {
        this.ponto.setEquipamentoInstalado(null);
        this.ponto.resetAtributos();
        this.listener.buttonPushed(this.node.getId());
        this.listener.setSalvo(false);
        System.out.println(evt.getWhen() + " - " + evt.getActionCommand() + " - " + evt.paramString() + " - REMOVER ACTION PERFORMED");
    }
}
