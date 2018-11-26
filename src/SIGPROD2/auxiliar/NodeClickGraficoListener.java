/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SIGPROD2.auxiliar;

import SIGPROD2.gui.MainFrame;
import SIGPROD2.modelo.Ponto;
import SIGPROD2.modelo.Rede;
import java.util.Objects;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.ViewerListener;

/**
 *
 * @author Rafael Casa
 */
public class NodeClickGraficoListener implements ViewerListener {

    private Rede rede;
    private MainFrame frame;

    public NodeClickGraficoListener(MainFrame frame) {
        this.frame = frame;
        this.rede = this.frame.getRede();
    }

    @Override
    public void viewClosed(String viewName) {
        this.rede.setLoop(false);
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

    @Override
    public void buttonPushed(String id) {

        System.out.println("Button pushed on node " + id);
        Node n = this.rede.getMapa().getNode(id);
        Ponto p = n.getAttribute("classe", Ponto.class);
        System.out.println("icc3f - " + p.getIcc3f());

        if (p.getEquipamentoInstalado() != null) {
            switch (p.getTipoEquipamentoInstalado()) {
                case RELE:
                    if(Objects.isNull(n.getAttribute("selecionado", Boolean.class))) {
                        n.removeAttribute("selecionado");
                        Ponto.removeAttribute(n, "ui.class", "selecionado");
                        this.frame.removePontoGrafico(n);
                    } else {
                        n.setAttribute("selecionado");
                        Ponto.addAttribute(n, "ui.class", "selecionado");
                        this.frame.addPontoGrafico(n);
                    }
                    break;
                case ELO:
                    if(Objects.isNull(n.getAttribute("selecionado", Boolean.class))) {
                        n.removeAttribute("selecionado");
                        Ponto.removeAttribute(n, "ui.class", "selecionado");
                        this.frame.removePontoGrafico(n);
                    } else {
                        n.setAttribute("selecionado");
                        Ponto.addAttribute(n, "ui.class", "selecionado");
                        this.frame.addPontoGrafico(n);
                    }
                    break;
                case NENHUM:

                    break;
                case RELIGADOR:
                    if(Objects.isNull(n.getAttribute("selecionado", Boolean.class))) {
                        n.removeAttribute("selecionado");
                        Ponto.removeAttribute(n, "ui.class", "selecionado");
                        this.frame.removePontoGrafico(n);
                    } else {
                        n.setAttribute("selecionado");
                        Ponto.addAttribute(n, "ui.class", "selecionado");
                        this.frame.addPontoGrafico(n);
                    }
                    break;
                case CHAVE:

                    break;
            }
        }
    }
}
