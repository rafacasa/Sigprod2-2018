package br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;
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

    public PanelAjusteRele(Rele rele) {
        this.rele = rele;
        this.initComponents();
        this.placeComponents();
    }

    private void initComponents() {
        this.campoCorrente = new JTextField(5);
        this.botaoMostrar = new JButton("Exibir Tempo");
        this.botaoMostrar.addActionListener(this::botaoMostrarActionPerformed);
        this.botaoLimpar = new JButton("Limpar");
        this.botaoLimpar.addActionListener(this::botaoLimparActionPerformed);
    }

    private void placeComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(this.campoCorrente);
        this.add(this.botaoMostrar);
        this.add(this.botaoLimpar);
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
    public JPanel geraCoordenograma() {
        this.coordenograma = new Coordenograma("Relé");
        this.coordenograma.add(this.rele, Color.BLUE, Color.RED);
        return this.coordenograma.getChartPanel();
    }

}
