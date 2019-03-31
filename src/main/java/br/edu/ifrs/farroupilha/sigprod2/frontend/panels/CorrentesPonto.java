package br.edu.ifrs.farroupilha.sigprod2.frontend.panels;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class CorrentesPonto extends JPanel {

    private static final Logger LOGGER = LogManager.getLogger(CorrentesPonto.class.getName());
    private JToggleButton selecionarPonto;
    private JToggleButton mostrarTempos;

    public CorrentesPonto() {
        this.initComponents();
        this.addItens();
    }

    private void initComponents() {
        this.selecionarPonto = new JToggleButton("Selecionar Ponto");
        this.selecionarPonto.addActionListener(this::selecionarPontoActionPerformed);
        this.mostrarTempos = new JToggleButton("Mostrar Tempos");
        this.mostrarTempos.setEnabled(false);
        this.mostrarTempos.addActionListener(this::mostrarTemposActionPerformed);
    }

    private void addItens() {
        this.setLayout(new MigLayout());
        this.add(this.selecionarPonto, "wrap");
        this.add(this.mostrarTempos);
    }

    private void selecionarPontoActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.debug(evt.getActionCommand());
        if (this.selecionarPonto.getModel().isSelected()) {
            Main.setTipoSelecao(Main.SELECAO_CORRENTES);
        } else {
            Main.setTipoSelecao(Main.SELECAO_DEFAULT);
            Main.removeCorrentes();
        }
    }

    private void mostrarTemposActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.debug(evt.getActionCommand());
        if (this.mostrarTempos.getModel().isSelected()) {

        } else {

        }
    }

}
