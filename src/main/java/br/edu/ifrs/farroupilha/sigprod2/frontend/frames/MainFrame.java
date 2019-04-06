package br.edu.ifrs.farroupilha.sigprod2.frontend.frames;

import java.awt.Component;

/**
 *
 * @author Rafael Luiz Casa
 */
public interface MainFrame {

    public void setMapa(Component panel);

    public void setCoordenograma(Component panel);

    public void setInfo(Component panel);

    public void setNavegacao(Component panel);

    public void setAjuste(Component panel);

    public void setCorrentes(Component panel);

    public void clearMapa();

    public void clearCoordenograma();

    public void clearInfo();

    public void clearNavegacao();

    public void clearAjuste();

    public void clearCorrentes();

    public void clearAll();

    public void setVisible(boolean b);
}
