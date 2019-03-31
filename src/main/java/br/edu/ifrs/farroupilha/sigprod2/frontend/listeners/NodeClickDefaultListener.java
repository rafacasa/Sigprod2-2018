package br.edu.ifrs.farroupilha.sigprod2.frontend.listeners;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.ui.view.ViewerListener;

/**
 *
 * @author Rafael Luiz Casa
 */
public class NodeClickDefaultListener implements ViewerListener {

    public boolean loop = true;
    private static final Logger LOGGER = LogManager.getLogger(NodeClickDefaultListener.class.getName());

    @Override
    public void viewClosed(String viewName) {
        loop = false;
        LOGGER.debug(viewName);
    }

    @Override
    public void buttonPushed(String id) {
        Main.mapaClickedActionPerformed(id);
    }

    @Override
    public void buttonReleased(String id) {
        LOGGER.debug("buttonReleased " + id);
    }

    @Override
    public void mouseOver(String id) {
        LOGGER.debug("mouseOver " + id);
    }

    @Override
    public void mouseLeft(String id) {
        LOGGER.debug("mouseLeft " + id);
    }
}
