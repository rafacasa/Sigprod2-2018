package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.CriteriosReligadorElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.MetricasReligadorElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvasElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Religador;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.CorrenteForaDoAlcanceException;
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelAjusteReligadorElo extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteReligadorElo.class.getName());
    private List<MetricasReligadorElo> metricas;
    private final Religador religadorPai;
    private Elo selecionado;
    private final Ponto ponto;
    private Coordenograma coordenograma;

    private JComboBox<MetricasReligadorElo> lista;
    private JButton botaoSelecionar;
    private JLabel nomeAlcance;
    private JLabel labelAlcance;
    private JLabel nomeSeletividadeRapida;
    private JLabel labelSeletividadeRapida;
    private JLabel nomeSeletividadeNeutro;
    private JLabel labelSeletividadeNeutro;
    private JLabel nomeSeletividadeFasePonto;
    private JLabel labelSeletividadeFasePonto;
    private JLabel nomeSeletividadeFaseAbaixo;
    private JLabel labelSeletividadeFaseAbaixo;
    private JTextField campoCorrente;
    private JButton botaoMostrar;
    private JButton botaoLimpar;
    private final List<String> nomePontos;

    public PanelAjusteReligadorElo(Ponto p, Rede rede, Ponto pOrigem) {
        LOGGER.trace("Cria Panel AjusteReleElo");
        this.religadorPai = (Religador) pOrigem.getEquipamentoInstalado();
        this.ponto = p;
        this.nomePontos = new ArrayList<>();
        this.calculaAjustes(rede, pOrigem);
        this.initComponents();
        this.addItens();
        Main.setCoordenograma(this.geraCoordenograma());
    }

    private void calculaAjustes(Rede rede, Ponto pOrigem) {
        this.ponto.resetAtributos(true);
        CriteriosReligadorElo criterios = new CriteriosReligadorElo(this.religadorPai, this.ponto, rede);
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
        this.labelSeletividadeRapida = new JLabel();
        this.labelSeletividadeNeutro = new JLabel();
        this.labelSeletividadeFasePonto = new JLabel();
        this.labelSeletividadeFaseAbaixo = new JLabel();

        this.nomeAlcance = new JLabel("Porcentagem de Alcançe: ");
        this.nomeSeletividadeRapida = new JLabel("Seletividade Rapida: ");
        this.nomeSeletividadeNeutro = new JLabel("Seletividade Neutro: ");
        this.nomeSeletividadeFasePonto = new JLabel("Seletividade Fase Ponto: ");
        this.nomeSeletividadeFaseAbaixo = new JLabel("Seletividade Fase Abaixo: ");

        this.campoCorrente = new JTextField(5);
        this.botaoMostrar = new JButton("Exibir Tempo");
        this.botaoMostrar.addActionListener(this::botaoMostrarActionPerformed);
        this.botaoLimpar = new JButton("Limpar");
        this.botaoLimpar.addActionListener(this::botaoLimparActionPerformed);
    }

    private void addItens() {
        this.setLayout(new MigLayout());

        this.add(this.lista, "wrap");
        this.add(this.nomeAlcance);
        this.add(this.labelAlcance, "wrap");

        this.add(this.nomeSeletividadeRapida);
        this.add(this.labelSeletividadeRapida, "wrap");
        this.add(this.nomeSeletividadeNeutro);
        this.add(this.labelSeletividadeNeutro, "wrap");
        this.add(this.nomeSeletividadeFasePonto);
        this.add(this.labelSeletividadeFasePonto, "wrap");
        this.add(this.nomeSeletividadeFaseAbaixo);
        this.add(this.labelSeletividadeFaseAbaixo, "wrap");
        this.add(this.botaoSelecionar, "wrap");
        this.add(this.campoCorrente, "wrap");
        this.add(this.botaoMostrar, "wrap");
        this.add(this.botaoLimpar, "wrap");
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
        this.labelSeletividadeRapida.setText(metrica.isSeletividadeRapida() ? "TRUE" : "FALSE");
        this.labelSeletividadeNeutro.setText(metrica.isSeletividadeNeutro() ? "TRUE" : "FALSE");
        this.labelSeletividadeFasePonto.setText(metrica.isSeletividadeFasePonto() ? "TRUE" : "FALSE");
        this.labelSeletividadeFaseAbaixo.setText(metrica.isSeletividadeFaseAbaixo() ? "TRUE" : "FALSE");
        Main.setCoordenograma(this.geraCoordenograma());
    }

    public Elo getSelecionado() {
        return selecionado;
    }

    public int botaoMostrarActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        this.limparPontos();
        String numeroDigitado = this.campoCorrente.getText();
        BigDecimal corrente;
        try {
            corrente = new BigDecimal(numeroDigitado);
        } catch (NumberFormatException e) {
            Erro.entradaInvalida(this);
            LOGGER.error("STRING INVÁLIDA" + e.getMessage());
            return 1;
        }

        try {
            BigDecimal tempoFaseLenta = religadorPai.getAjusteFase().calculaTempo(corrente);
            if (tempoFaseLenta.compareTo(BigDecimal.ZERO) >= 1) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoFaseLenta), "tempoFaseLenta", Arrays.asList(Color.BLACK)));
            }

            BigDecimal tempoNeutroLenta = religadorPai.getAjusteNeutro().calculaTempo(corrente);
            if (tempoNeutroLenta.compareTo(BigDecimal.ZERO) >= 1) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoNeutroLenta), "tempoNeutroLenta", Arrays.asList(Color.RED)));
            }

            BigDecimal tempoFaseRapida = religadorPai.getAjusteRapidaFase().calculaTempo(corrente);
            if (tempoFaseRapida.compareTo(BigDecimal.ZERO) >= 1) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoFaseRapida), "tempoFaseRapida", Arrays.asList(Color.DARK_GRAY)));
            }

            BigDecimal tempoNeutroRapida = religadorPai.getAjusteRapidaNeutro().calculaTempo(corrente);
            if (tempoNeutroRapida.compareTo(BigDecimal.ZERO) >= 1) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoNeutroRapida), "tempoNeutroRapida", Arrays.asList(Color.ORANGE)));
            }

            try {
                BigDecimal tempoMaximaElo = BigDecimal.valueOf(this.lista.getItemAt(this.lista.getSelectedIndex()).getElo().tempoDaCorrente(corrente.doubleValue(), CurvasElo.MAXIMA));
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoMaximaElo), "tempoMaximaElo", Arrays.asList(Color.BLUE)));
            } catch (CorrenteForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE PARA CORRENTE DIGITADA " + ex.getLocalizedMessage());
            }

            try {
                BigDecimal tempoMinimaElo = BigDecimal.valueOf(this.lista.getItemAt(this.lista.getSelectedIndex()).getElo().tempoDaCorrente(corrente.doubleValue(), CurvasElo.MINIMA));
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoMinimaElo), "tempoMinimaElo", Arrays.asList(Color.BLUE)));
            } catch (CorrenteForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE PARA CORRENTE DIGITADA " + ex.getLocalizedMessage());
            }
        } catch (NullPointerException e) {
            LOGGER.error("COORDENOGRAMA NAO ABERTO" + e.getMessage());
            return 1;
        }
        return 0;
    }

    public void botaoLimparActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        this.limparPontos();
    }

    public void limparPontos() {
        if (this.nomePontos != null) {
            this.nomePontos.forEach(s -> {
                this.coordenograma.remove(s);
            });
            this.nomePontos.clear();
        }
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
