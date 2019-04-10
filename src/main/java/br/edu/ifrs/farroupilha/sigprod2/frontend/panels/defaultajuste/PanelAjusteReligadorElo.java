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
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import java.awt.Color;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
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
    private JTextField campoCorrente;
    private JButton botaoMostrar;
    private JButton botaoLimpar;
    private List<String> nomePontos;

    public PanelAjusteReligadorElo(Ponto p, Rede rede, Ponto pOrigem) {
        LOGGER.trace("Cria Panel AjusteReleElo");
        this.religadorPai = (Religador) pOrigem.getEquipamentoInstalado();
        this.ponto = p;
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
        this.labelSeletividade = new JLabel();

        this.nomeAlcance = new JLabel("Porcentagem de Alcançe: ");
        this.nomeSeletividade = new JLabel("Seletividade: ");

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

        this.add(this.nomeSeletividade);
        this.add(this.labelSeletividade, "wrap");
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
        this.labelSeletividade.setText(metrica.isSeletividade() ? "TRUE" : "FALSE");
        Main.setCoordenograma(this.geraCoordenograma());
    }

    public Elo getSelecionado() {
        return selecionado;
    }

    public void botaoMostrarActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        this.limparPontos();
        String numeroDigitado = this.campoCorrente.getText();
        try {
            BigDecimal corrente = new BigDecimal(numeroDigitado);
            BigDecimal tempoFaseLenta = religadorPai.getAjusteFase().calculaTempo(corrente);
            BigDecimal tempoNeutroLenta = religadorPai.getAjusteNeutro().calculaTempo(corrente);
            BigDecimal tempoFaseRapida = religadorPai.getAjusteRapidaFase().calculaTempo(corrente);
            BigDecimal tempoNeutroRapida = religadorPai.getAjusteRapidaNeutro().calculaTempo(corrente);
            BigDecimal tempoMaximaElo = BigDecimal.valueOf(this.lista.getItemAt(this.lista.getSelectedIndex()).getElo().tempoDaCorrente(corrente.doubleValue(), CurvasElo.MAXIMA));
            BigDecimal tempoMinimaElo = BigDecimal.valueOf(this.lista.getItemAt(this.lista.getSelectedIndex()).getElo().tempoDaCorrente(corrente.doubleValue(), CurvasElo.MINIMA));
            this.nomePontos = this.coordenograma.add(corrente, Arrays.asList(tempoFaseLenta, tempoNeutroLenta, tempoFaseRapida, tempoNeutroRapida, tempoMaximaElo, tempoMinimaElo), "pontoDigitado", Arrays.asList(Color.BLACK, Color.RED, Color.DARK_GRAY, Color.ORANGE, Color.BLUE, Color.BLUE));
        } catch (NumberFormatException e) {
            Erro.entradaInvalida(this);
            LOGGER.error("STRING INVÁLIDA" + e.getMessage());
        } catch (NullPointerException e) {
            LOGGER.error("COORDENOGRAMA NAO ABERTO" + e.getMessage());
        }
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
