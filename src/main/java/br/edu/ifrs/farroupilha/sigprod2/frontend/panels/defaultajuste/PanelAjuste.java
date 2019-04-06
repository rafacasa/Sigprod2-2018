package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import javax.swing.JPanel;

/**
 *
 * @author Rafael Luiz Casa
 */
public abstract class PanelAjuste extends JPanel {

    public abstract Coordenograma geraCoordenograma();
}
