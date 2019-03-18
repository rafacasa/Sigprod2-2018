package br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels;

import br.edu.ifrs.farroupilha.sigprod2.auxiliar.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rele;
import java.awt.Color;
import javax.swing.JPanel;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelAjusteRele extends PanelAjuste {

    private Rele rele;

    public PanelAjusteRele(Rele rele) {
        this.rele = rele;
    }

    @Override
    public JPanel geraCoordenograma() {
        Coordenograma temp = new Coordenograma("Relé");
        temp.add(this.rele, Color.BLUE, Color.RED);
        return temp.getChartPanel();
    }

}
