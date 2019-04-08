package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.CriteriosReligadorElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.MetricasReligadorElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Religador;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
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
public class PanelAjusteReligadorElo extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteReligadorElo.class.getName());
    private List<MetricasReligadorElo> metricas;
    private Religador religadorPai;
    private Elo selecionado;
    private Ponto ponto;
    private Coordenograma coordenograma;

    private JComboBox<MetricasReligadorElo> lista;
    private JButton botaoSelecionar;
    private JLabel nomeAlcance;
    private JLabel labelAlcance;
    private JLabel nomeSeletividade;
    private JLabel labelSeletividade;
    private JPanel panelAlcance;
    private JPanel panelSeletividade;

    public PanelAjusteReligadorElo(Ponto p, Rede rede, Ponto pOrigem) {
        LOGGER.trace("Cria Panel AjusteReleElo");
        this.religadorPai = (Religador) pOrigem.getEquipamentoInstalado();
        this.ponto = p;
        this.calculaAjustes(rede, pOrigem);
        this.initComponents();
        this.criaPanels();
        this.addItens();
        Main.setCoordenograma(this.geraCoordenograma());
    }

    private void calculaAjustes(Rede rede, Ponto pOrigem) {
        this.ponto.resetAtributos(true);
        CriteriosReligadorElo criterios = new CriteriosReligadorElo(this.religadorPai, this.ponto, rede, pOrigem);
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
        Main.selecionaEquipamento(this.ponto, this.selecionado);
    }

    private void listaActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        MetricasReligadorElo metrica = this.lista.getItemAt(this.lista.getSelectedIndex());
        this.labelAlcance.setText(metrica.getAlcance().toString());
        this.labelSeletividade.setText(metrica.isSeletividade() ? "TRUE" : "FALSE");
        Main.setCoordenograma(this.geraCoordenograma());
    }

    public Elo getSelecionado() {
        return selecionado;
    }

    @Override
    public final Coordenograma geraCoordenograma() {
        MetricasReligadorElo metricaselo = this.lista.getItemAt(this.lista.getSelectedIndex());
        if (metricaselo != null) {
            Elo elo = metricaselo.getElo();
            this.coordenograma = new Coordenograma("Coordenograma");
            this.coordenograma.add(this.religadorPai, Color.BLACK, Color.RED, Color.DARK_GRAY, Color.ORANGE);
            this.coordenograma.add(elo, "Elo Selecionado", Color.BLUE);
            return this.coordenograma;
        }
        return null;
    }
}
