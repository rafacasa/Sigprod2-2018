package br.edu.ifrs.farroupilha.sigprod2.frontend.frames;

import br.edu.ifrs.farroupilha.sigprod2.frontend.layout.RelativeLayout;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class RelativeMainFrame extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger(RelativeMainFrame.class.getName());
    private JPanel panelMapa;
    private JPanel panelCoordenograma;
    private JPanel panelAjuste;
    private JPanel panelInfo;
    private JPanel panelNavegacao;
    private JPanel panelConteudo;
    private JPanel panelInferior;
    private JPanel contentPanel;
    //private Rede rede;

    public RelativeMainFrame() {
        //this.rede = rede;
        this.initComponents();
        this.addItens();
    }

    private void initComponents() {
        LOGGER.error("AINDA NAO DESENVOLVIDO");
        this.panelMapa = new JPanel();
        this.panelCoordenograma = new JPanel();
        this.panelAjuste = new JPanel();
        this.panelInfo = new JPanel();
        this.panelNavegacao = new JPanel();
        this.panelConteudo = new JPanel();
        this.panelInferior = new JPanel();
        this.contentPanel = new JPanel();
    }

    private void addItens() {
        LOGGER.error("AINDA NAO DESENVOLVIDO");
        //this.setJMenuBar(new Menu);
        this.panelConteudo.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));
        this.panelConteudo.add(this.panelAjuste, Float.valueOf("0.2"));
        this.panelConteudo.add(this.panelCoordenograma, Float.valueOf("0.35"));
        this.panelConteudo.add(this.panelMapa, Float.valueOf("0.45"));

        this.panelInferior.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));
        this.panelInferior.add(this.panelInfo, Float.valueOf("0.7"));
        this.panelInferior.add(this.panelNavegacao, Float.valueOf("0.3"));
        this.panelInferior.setPreferredSize(new Dimension(0, 30));

        this.contentPanel.setLayout(new RelativeLayout(RelativeLayout.Y_AXIS));
        this.contentPanel.add(this.panelConteudo, Float.valueOf("1"));
        this.contentPanel.add(this.panelInferior);
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
            new RelativeMainFrame().setVisible(true);
        });
    }
}
