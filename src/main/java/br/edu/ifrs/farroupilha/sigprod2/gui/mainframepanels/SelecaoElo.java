package br.edu.ifrs.farroupilha.sigprod2.gui.mainframepanels;

import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import br.edu.ifrs.farroupilha.sigprod2.frontend.listeners.NodeClickDefaultListener;
import br.edu.ifrs.farroupilha.sigprod2.backend.bd.dao.EloKDao;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.graphstream.graph.Node;

/**
 *
 * @author Rafael Luiz Casa
 */
public class SelecaoElo extends JPanel {

    private Ponto ponto;
    private Node node;
    private NodeClickDefaultListener listener;

    private JPanel panelTipo, panelCorrente, panelNomeNo;

    private JLabel labelTitle, labelTipoElo, labelCorrente, labelNomeNoFixa, labelNomeNo;
    private JComboBox<String> listaTipoElo;
    private JComboBox<Elo> listaCorrentes;
    private JButton botaoGravar;

    public SelecaoElo(Node n, NodeClickDefaultListener listener) {
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

        this.labelTipoElo = new JLabel("Tipo do Elo:");
        this.labelTipoElo.setFont(new Font("Tahoma", 0, 16));

        this.labelCorrente = new JLabel("Corrente:");
        this.labelCorrente.setFont(new Font("Tahoma", 0, 16));

        this.labelNomeNoFixa = new JLabel("Nó:");
        this.labelNomeNoFixa.setFont(new Font("Tahoma", 0, 16));

        this.labelNomeNo = new JLabel(this.ponto.getNome());
        this.labelNomeNo.setFont(new Font("Tahoma", 0, 16));

        String[] tiposElo = {"K", "T"};
        this.listaTipoElo = new JComboBox<>(tiposElo) {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };
        this.listaTipoElo.setSelectedIndex(-1);
        this.listaTipoElo.addActionListener(this::listaTipoEloActionPerformed);

        this.listaCorrentes = new JComboBox<>() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };

        this.botaoGravar = new JButton("Gravar");
        this.botaoGravar.addActionListener(this::botaoGravarActionPerformed);
        this.botaoGravar.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    private void initPanels() {
        panelTipo = new JPanel();
        panelTipo.setLayout(new BoxLayout(panelTipo, BoxLayout.LINE_AXIS));
        panelTipo.add(labelTipoElo);
        panelTipo.add(Box.createRigidArea(new Dimension(5, 0)));
        panelTipo.add(listaTipoElo);
        panelTipo.add(Box.createRigidArea(new Dimension(20, 0)));

        panelCorrente = new JPanel();
        panelCorrente.setLayout(new BoxLayout(panelCorrente, BoxLayout.LINE_AXIS));
        panelCorrente.add(labelCorrente);
        panelCorrente.add(Box.createRigidArea(new Dimension(5, 0)));
        panelCorrente.add(listaCorrentes);
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
        add(botaoGravar);
        add(Box.createRigidArea(new Dimension(0, 10)));
        add(panelNomeNo);
        add(Box.createVerticalGlue());
    }

    private void listaTipoEloActionPerformed(java.awt.event.ActionEvent evt) {
        this.listaCorrentes.removeAllItems();
        if ("K".equals(this.listaTipoElo.getSelectedItem())) {
            this.carregarCorrentesEloK();
        } else {
            //this.carregarCorrentesEloT();
        }
    }

    private void botaoGravarActionPerformed(java.awt.event.ActionEvent evt) {
        Elo elo = this.listaCorrentes.getItemAt(this.listaCorrentes.getSelectedIndex());
        this.ponto.setEquipamentoInstalado(elo);
        this.ponto.resetAtributos();
        this.listener.buttonPushed(this.node.getId());
        this.listener.setSalvo(false);
    }

    private void carregarCorrentesEloK() {
        List<Elo> correntes;
        try {
            correntes = EloKDao.buscarCorrentes();

            correntes.forEach((elo) -> {
                this.listaCorrentes.addItem(elo);
            });
        } catch (SQLException ex) {
            Erro.mostraMensagemSQL(this);
        }
    }

    //private void carregarCorrentesEloT() {
    //}
}
