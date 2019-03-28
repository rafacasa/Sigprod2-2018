package br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.criterios.Criterios_Rele_Elo;
import br.edu.ifrs.farroupilha.sigprod2.gui.AjusteFrame;
import br.edu.ifrs.farroupilha.sigprod2.metricas.MetricasReleElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import java.awt.Color;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelAjusteReleElo extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteReleElo.class.getName());
    private List<MetricasReleElo> metricas;
    private AjusteFrame ajusteFrame;
    private Rele relePai;
    private Elo selecionado;
    private Ponto ponto;
    private Coordenograma coordenograma;

    private JComboBox<MetricasReleElo> lista;
    private JButton botaoSelecionar;
    private JLabel nomeAlcance;
    private JLabel labelAlcance;
    private JLabel nomeSeletividade;
    private JLabel labelSeletividade;
    private JPanel panelAlcance;
    private JPanel panelSeletividade;

    public PanelAjusteReleElo(AjusteFrame ajusteFrame, Ponto p, Rede rede, Ponto pOrigem) {
        LOGGER.trace("Cria Panel AjusteReleElo");
        this.ajusteFrame = ajusteFrame;
        this.relePai = (Rele) pOrigem.getEquipamentoInstalado();
        this.ponto = p;
        this.calculaAjustes(rede, pOrigem);
        this.initComponents();
        this.criaPanels();
        this.addItens();
    }

    private void calculaAjustes(Rede rede, Ponto pOrigem) {
        this.ponto.resetAtributos(true);
        Criterios_Rele_Elo criterios = new Criterios_Rele_Elo(this.relePai, this.ponto, rede, pOrigem);
        try {
            this.metricas = criterios.ajuste();
        } catch (AjusteImpossivelException ex) {
            LOGGER.error("AJUSTE IMPOSSIVEL" + ex.getMessage());
        }
    }

    private void initComponents() {
        this.botaoSelecionar = new JButton("Selecionar Elo");
        this.botaoSelecionar.addActionListener(this::botaoSelecionarActionPerformed);

        this.lista = new JComboBox<>() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };

        this.metricas.forEach(m -> {
            this.lista.addItem(m);
        });

        this.lista.setSelectedIndex(-1);
        this.selecionado = null;
        this.lista.addActionListener(this::listaActionPerformed);

        this.labelAlcance = new JLabel();
        this.labelSeletividade = new JLabel();

        this.nomeAlcance = new JLabel("Porcentagem de Alcançe: ");
        this.nomeSeletividade = new JLabel("Seletividade: ");
    }

    private void criaPanels() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.panelAlcance = new JPanel();
        this.panelAlcance.setLayout(new BoxLayout(this.panelAlcance, BoxLayout.LINE_AXIS));

        this.panelSeletividade = new JPanel();
        this.panelSeletividade.setLayout(new BoxLayout(this.panelSeletividade, BoxLayout.LINE_AXIS));
    }

    private void addItens() {
        this.panelAlcance.add(this.nomeAlcance);
        this.panelAlcance.add(Box.createHorizontalGlue());
        this.panelAlcance.add(this.labelAlcance);

        this.panelSeletividade.add(this.nomeSeletividade);
        this.panelSeletividade.add(Box.createHorizontalGlue());
        this.panelSeletividade.add(this.labelSeletividade);

        this.add(this.lista);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelAlcance);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelSeletividade);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.botaoSelecionar);
    }

    private void botaoSelecionarActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        this.selecionado = this.lista.getItemAt(this.lista.getSelectedIndex()).getElo();
        this.ajusteFrame.selecionaEquipamento(this.ponto, this.selecionado);
    }

    private void listaActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        MetricasReleElo metrica = this.lista.getItemAt(this.lista.getSelectedIndex());
        this.labelAlcance.setText(metrica.getAlcance().toString());
        this.labelSeletividade.setText(metrica.isSeletividade() ? "TRUE" : "FALSE");
        this.ajusteFrame.atualizaCoordenograma(this.geraCoordenograma());
        this.ajusteFrame.pack();
    }

    public Elo getSelecionado() {
        return selecionado;
    }

    @Override
    public JPanel geraCoordenograma() {
        MetricasReleElo metricaselo = this.lista.getItemAt(this.lista.getSelectedIndex());
        if (metricaselo != null) {
            Elo elo  = metricaselo.getElo();
            this.coordenograma = new Coordenograma("Coordenograma");
            this.coordenograma.add(this.relePai, Color.RED, Color.RED);
            this.coordenograma.add(elo, "Elo Selecionado", Color.BLUE);
            return this.coordenograma.getChartPanel();
        }
        return new JPanel();
    }

}
