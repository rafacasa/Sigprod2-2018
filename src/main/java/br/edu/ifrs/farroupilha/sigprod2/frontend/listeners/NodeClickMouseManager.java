/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifrs.farroupilha.sigprod2.frontend.listeners;

import java.awt.event.MouseEvent;
import java.util.EnumSet;
import org.graphstream.graph.Node;
import org.graphstream.ui.swing_viewer.util.DefaultMouseManager;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.util.InteractiveElement;

/**
 *
 * @author Rafael Casa
 */
public class NodeClickMouseManager extends DefaultMouseManager {

    ViewerListener listener;

    public NodeClickMouseManager(ViewerListener listener) {
        this.listener = listener;
    }

    public ViewerListener getListener() {
        return listener;
    }

    public void setListener(ViewerListener listener) {
        this.listener = listener;
    }

    @Override
    public void mouseDragged(MouseEvent event) {

    }

    @Override
    protected void mouseButtonPress(MouseEvent event) {
        //super.mouseButtonPress(event);

        //System.out.println("Press");
    }

    @Override
    public void mouseClicked(MouseEvent event) {
        //super.mouseClicked(event);
        //System.out.println("Clicked");
        curElement = view.findGraphicElementAt(EnumSet.allOf(InteractiveElement.class), event.getX(), event.getY());

        if (curElement != null) {
            Node node = graph.getNode(curElement.getId());
            if (node != null) {
                System.out.println("Mouse pressed at node: " + node.getId());
                listener.buttonPushed(node.getId());
            } else {
                listener.buttonPushed(null);
            }
        } else {
            listener.buttonPushed(null);
        }
    }

    @Override
    public void mousePressed(MouseEvent event) {
        //super.mousePressed(event);

        // if you need object of Node pressed, following code will help you, curElement is already defined at DefaultMouseManager.
    }
}
