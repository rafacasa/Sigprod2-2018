package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Religador;
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelAjusteReleReligador extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteReleReligador.class.getName());
    private Religador religador;
    private Rele relePai;
    private List<String> nomePontos;
    private Coordenograma coordenograma;
    private JTextField campoCorrente;
    private JButton botaoMostrar;
    private JButton botaoLimpar;
    private JLabel fabricante;
    private JLabel modelo;
    private JTabbedPane ajustes;
    private JPanel ajusteFase;
    private JPanel ajusteNeutro;

    public PanelAjusteReleReligador(Ponto p, Ponto pOrigem) {
        this.relePai = (Rele) pOrigem.getEquipamentoInstalado();
        this.religador = (Religador) p.getEquipamentoInstalado();
        this.dadosReligador();
        this.initComponents();
        this.placeComponents();
        Main.setCoordenograma(this.geraCoordenograma());
    }

    private void initComponents() {
        this.campoCorrente = new JTextField(5);
        this.botaoMostrar = new JButton("Exibir Tempo");
        this.botaoMostrar.addActionListener(this::botaoMostrarActionPerformed);
        this.botaoLimpar = new JButton("Limpar");
        this.botaoLimpar.addActionListener(this::botaoLimparActionPerformed);
    }

    private void dadosReligador() {
        this.fabricante = new JLabel("Fabricante: " + this.religador.getFabricante());
        this.modelo = new JLabel("Modelo: " + this.religador.getModelo());
        this.ajustes = new JTabbedPane(JTabbedPane.TOP);
        this.setPanelsAjuste();
        this.ajustes.addTab("Fase", null, this.ajusteFase, "Ajustes de Fase do Religador");
        this.ajustes.addTab("Neutro", null, this.ajusteNeutro, "Ajustes de Neutro do Religador");
    }

    private void setPanelsAjuste() {
        this.ajusteFase = this.getPanelDadosAjuste(true);
        this.ajusteNeutro = this.getPanelDadosAjuste(false);
    }

    private JPanel getPanelDadosAjuste(boolean fase) {
        AjusteRele ajuste = fase ? this.religador.getAjusteFase() : this.religador.getAjusteNeutro();
        AjusteRele ajusteRap = fase ? this.religador.getAjusteRapidaFase() : this.religador.getAjusteRapidaNeutro();
        JPanel panel = new JPanel(new MigLayout());
        JLabel tipo = new JLabel(this.religador.getNomeCurva(ajuste.getCurva(), fase));
        JLabel lenta = new JLabel("Curva Lenta");
        JLabel labelAT = new JLabel("AT: " + ajuste.getAt());
        JLabel labelAC = new JLabel("AC: " + ajuste.getAc());
        JLabel rapida = new JLabel("Curva Rápida");
        JLabel labelATRap = new JLabel("AT: " + ajusteRap.getAt());
        panel.add(lenta, "wrap");
        panel.add(tipo, "wrap");
        panel.add(labelAC, "wrap");
        panel.add(labelAT, "wrap");
        panel.add(rapida, "wrap");
        panel.add(labelATRap, "wrap");
        return panel;
    }

    private void placeComponents() {
        this.setLayout(new MigLayout());
        this.add(this.fabricante, "wrap");
        this.add(this.modelo, "wrap");
        this.add(this.ajustes, "wrap");
        this.add(this.campoCorrente, "wrap");
        this.add(this.botaoMostrar, "wrap");
        this.add(this.botaoLimpar, "wrap");
    }

    public void botaoMostrarActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        this.limparPontos();
        String numeroDigitado = this.campoCorrente.getText();
        try {
            BigDecimal corrente = new BigDecimal(numeroDigitado);
            BigDecimal tempoFaseLenta = religador.getAjusteFase().calculaTempo(corrente);
            BigDecimal tempoNeutroLenta = religador.getAjusteNeutro().calculaTempo(corrente);
            BigDecimal tempoFaseRapida = religador.getAjusteRapidaFase().calculaTempo(corrente);
            BigDecimal tempoNeutroRapida = religador.getAjusteRapidaNeutro().calculaTempo(corrente);
            BigDecimal tempoFaseRele = this.relePai.getAjusteFase().calculaTempo(corrente);
            BigDecimal tempoNeutroRele = this.relePai.getAjusteNeutro().calculaTempo(corrente);
            this.nomePontos = this.coordenograma.add(corrente, Arrays.asList(tempoFaseLenta, tempoNeutroLenta, tempoFaseRapida, tempoNeutroRapida, tempoFaseRele, tempoNeutroRele), "pontoDigitado", Arrays.asList(Color.BLUE, Color.RED, Color.BLACK, Color.ORANGE));
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
        this.coordenograma = new Coordenograma("Religador");
        this.coordenograma.add(this.religador, Color.BLUE, Color.RED, Color.DARK_GRAY, Color.ORANGE);
        this.coordenograma.add(this.relePai, Color.PINK, Color.BLACK);
        return this.coordenograma;
    }

}
