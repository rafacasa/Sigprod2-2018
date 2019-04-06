package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
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
public class PanelAjusteRele extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteRele.class.getName());
    private Rele rele;
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

    public PanelAjusteRele(Rele rele) {
        this.rele = rele;
        this.dadosRele();
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

    private void dadosRele() {
        this.fabricante = new JLabel("Fabricante: " + this.rele.getFabricante());
        this.modelo = new JLabel("Modelo: " + this.rele.getModelo());
        this.ajustes = new JTabbedPane(JTabbedPane.TOP);
        this.setPanelsAjuste();
        this.ajustes.addTab("Fase", null, this.ajusteFase, "Ajustes de Fase do Rele");
        this.ajustes.addTab("Neutro", null, this.ajusteNeutro, "Ajustes de Neutro do Rele");
    }

    private void setPanelsAjuste() {
        this.ajusteFase = this.getPanelDadosAjuste(true);
        this.ajusteNeutro = this.getPanelDadosAjuste(false);
    }

    private JPanel getPanelDadosAjuste(boolean fase) {
        AjusteRele ajuste = fase ? this.rele.getAjusteFase() : this.rele.getAjusteNeutro();
        JPanel panel = new JPanel(new MigLayout());
        JLabel tipo = new JLabel(this.rele.getNomeCurva(ajuste.getCurva(), fase));
        JLabel labelAT = new JLabel("AT: " + ajuste.getAt());
        JLabel labelAC = new JLabel("AC: " + ajuste.getAc());
        panel.add(tipo, "wrap");
        panel.add(labelAT, "wrap");
        panel.add(labelAC, "wrap");
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
            BigDecimal corrente = new BigDecimal(numeroDigitado); //MARCAR ESTA CORRENTE COM TEMPO CALCULADO
            BigDecimal tempoFase = rele.getAjusteFase().calculaTempo(corrente);
            BigDecimal tempoNeutro = rele.getAjusteNeutro().calculaTempo(corrente);
            this.nomePontos = this.coordenograma.add(corrente, Arrays.asList(tempoFase, tempoNeutro), "pontoDigitado", Arrays.asList(Color.BLUE, Color.RED));
        } catch (NumberFormatException e) {
            Erro.entradaInvalida(this);
            LOGGER.trace("STRING INVÁLIDA" + e.getMessage());
        } catch (NullPointerException e) {
            LOGGER.trace("COORDENOGRAMA NAO ABERTO" + e.getMessage());
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
    public Coordenograma geraCoordenograma() {
        this.coordenograma = new Coordenograma("Relé");
        this.coordenograma.add(this.rele, Color.BLUE, Color.RED);
        return this.coordenograma;
    }

}
