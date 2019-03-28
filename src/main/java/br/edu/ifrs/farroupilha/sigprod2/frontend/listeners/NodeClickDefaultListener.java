package br.edu.ifrs.farroupilha.sigprod2.frontend.listeners;

import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultmain.ExibeElo;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultmain.Informacoes;
import br.edu.ifrs.farroupilha.sigprod2.frontend.frames.DefaultMainFrame;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultmain.SelecaoElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.ViewerListener;

/**
 *
 * @author Rafael Luiz Casa
 */
public class NodeClickDefaultListener implements ViewerListener {

    public boolean loop = true;
    private Rede rede;
    private DefaultMainFrame frame;

    @Override
    public void viewClosed(String viewName) {
        loop = false;
    }

    public NodeClickDefaultListener(DefaultMainFrame frame) {
        this.frame = frame;
        this.rede = this.frame.getRede();
    }

    @Override
    public void buttonPushed(String id) {
        JPanel panel, panelInfo;
        if (id == null) {
            this.frame.limparDataPanel();
            this.frame.limparInfoPanel();
        } else {
            System.out.println("Button pushed on node " + id);
            Node n = this.rede.getMapa().getNode(id);
            Ponto p = n.getAttribute("classe", Ponto.class);
            System.out.println("icc3f - " + p.getIcc3f());

            panelInfo = new Informacoes(n);
            this.frame.setInfoPanel(panelInfo);

            switch (p.getTipoEquipamentoInstalado()) {
                case RELE:
                    panel = new JPanel();
                    panel.setMaximumSize(new Dimension(11000, 0));
                    this.frame.setDataPanel(panel);
                    break;
                case ELO:
                    if (p.getEquipamentoInstalado() == null) {
                        panel = new SelecaoElo(n, this);
                        this.frame.setDataPanel(panel);
                    } else {
                        panel = new ExibeElo(n, this);
                        this.frame.setDataPanel(panel);
                    }
                    break;
                case NENHUM:
                    panel = new JPanel();
                    panel.setMaximumSize(new Dimension(11000, 0));
                    this.frame.setDataPanel(panel);
                    break;
                case RELIGADOR:
                    panel = new JPanel();
                    panel.setMaximumSize(new Dimension(11000, 0));
                    this.frame.setDataPanel(panel);
                    break;
                case CHAVE:
                    panel = new JPanel();
                    panel.setMaximumSize(new Dimension(11000, 0));
                    this.frame.setDataPanel(panel);
                    break;
            }
        }
    }

    @Override
    public void buttonReleased(String id) {
        System.out.println("Button released on node " + id);
    }

    @Override
    public void mouseOver(String id) {

    }

    @Override
    public void mouseLeft(String id) {

    }

    public void setSalvo(boolean b) {
        this.frame.setSalvo(b);
    }

}
