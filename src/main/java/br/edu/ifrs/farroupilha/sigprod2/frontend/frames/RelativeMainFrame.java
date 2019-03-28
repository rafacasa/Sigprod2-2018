package br.edu.ifrs.farroupilha.sigprod2.frontend.frames;

import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultmain.Menu;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class RelativeMainFrame extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger(RelativeMainFrame.class.getName());
    private Rede rede;

    public RelativeMainFrame(Rede rede) {
        this.rede = rede;
        this.initComponents();
        this.addItens();
    }
    
    private void initComponents() {
        
    }
    
    private void addItens() {
        
        //this.setJMenuBar(new Menu);
    }
    
    
    

    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            LOGGER.error("ERRO NA SELEÇÃO DE LOOKANDFELL - " + ex.getMessage());
        }
        SwingUtilities.invokeLater(() -> {
            //new MainFrameRelative().setVisible(true);
        });
    }
}
