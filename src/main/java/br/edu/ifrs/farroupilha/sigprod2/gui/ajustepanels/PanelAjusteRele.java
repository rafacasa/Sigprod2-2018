package br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels;

import br.edu.ifrs.farroupilha.sigprod2.auxiliar.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.auxiliar.Erro;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rele;
import java.awt.Color;
import java.math.BigDecimal;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JTextField;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelAjusteRele extends PanelAjuste {

    private Rele rele;
    private Coordenograma coordenograma;
    private JTextField campoCorrente;
    private JButton botaoMostrar;

    public PanelAjusteRele(Rele rele) {
        this.rele = rele;
    }

    public void initComponents() {
        this.campoCorrente = new JTextField(5);
        this.botaoMostrar = new JButton("Exibir Tempo");
        this.botaoMostrar.addActionListener(this::botaoMostrarActionPerformed);
    }

    public void placeComponents() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.add(this.campoCorrente);
        this.add(this.botaoMostrar);
    }

    public void botaoMostrarActionPerformed(java.awt.event.ActionEvent evt) {
        String numeroDigitado = this.campoCorrente.getText();
        try {
            BigDecimal corrente = new BigDecimal(numeroDigitado); //MARCAR ESTA CORRENTE COM TEMPO CALCULADO
        } catch (NumberFormatException e) {
            Erro.entradaInvalida(this);
        }
    }

    @Override
    public JPanel geraCoordenograma() {
        this.coordenograma = new Coordenograma("Relé");
        this.coordenograma.add(this.rele, Color.BLUE, Color.RED);
        return this.coordenograma.getChartPanel();
    }

}
