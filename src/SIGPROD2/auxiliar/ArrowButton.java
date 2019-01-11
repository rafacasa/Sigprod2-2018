package SIGPROD2.auxiliar;

import java.awt.Dimension;
import javax.swing.plaf.basic.BasicArrowButton;

/**
 *
 * @author Rafael Luiz Casa
 */
public class ArrowButton extends BasicArrowButton {

    public ArrowButton(int direction) {
        super(direction);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(16, 16);
    }

    @Override
    public Dimension getMinimumSize() {
        return new Dimension(16, 16);
    }
}
