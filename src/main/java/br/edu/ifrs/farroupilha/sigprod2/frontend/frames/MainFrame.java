package br.edu.ifrs.farroupilha.sigprod2.frontend.frames;

import javax.swing.JPanel;

/**
 *
 * @author Rafael Luiz Casa
 */
public interface MainFrame {

    public void setMapa(JPanel panel);

    public void setCoordenograma(JPanel panel);

    public void setInfo(JPanel panel);

    public void setNavegacao(JPanel panel);

    public void setAjuste(JPanel panel);

    public void clearMapa();

    public void clearCoordenograma();

    public void clearInfo();

    public void clearNavegacao();

    public void clearAjuste();

    public void clearAll();
}
