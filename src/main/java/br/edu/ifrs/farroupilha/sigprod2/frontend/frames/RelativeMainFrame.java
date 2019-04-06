package br.edu.ifrs.farroupilha.sigprod2.frontend.frames;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class RelativeMainFrame extends JFrame implements MainFrame {

    private static final Logger LOGGER = LogManager.getLogger(RelativeMainFrame.class.getName());
    //private Rede rede;
    private JPanel panelMapa;
    private JPanel panelCoordenograma;
    private JPanel panelAjuste;
    private JPanel panelInfo;
    private JPanel panelNavegacao;
    private JPanel panelConteudo;
    private JPanel panelInferior;
    private JPanel panelEsquerdo;
    private JPanel panelCorrentes;
    private JPanel contentPanel;

    public RelativeMainFrame() {
        //this.rede = rede;
        this.initComponents();
        this.addItens();
        this.calculaTamanhos();
    }

    private void initComponents() {
        this.panelMapa = new JPanel();
        this.panelMapa.setLayout(new BoxLayout(this.panelMapa, BoxLayout.PAGE_AXIS));
        this.panelCoordenograma = new JPanel();
        this.panelCoordenograma.setLayout(new BoxLayout(this.panelCoordenograma, BoxLayout.PAGE_AXIS));
        this.panelAjuste = new JPanel();
        this.panelAjuste.setLayout(new BoxLayout(this.panelAjuste, BoxLayout.PAGE_AXIS));
        this.panelInfo = new JPanel();
        this.panelInfo.setLayout(new BoxLayout(this.panelInfo, BoxLayout.PAGE_AXIS));
        this.panelNavegacao = new JPanel();
        this.panelNavegacao.setLayout(new BoxLayout(this.panelNavegacao, BoxLayout.PAGE_AXIS));
        this.panelConteudo = new JPanel();
        this.panelConteudo.setLayout(new BoxLayout(this.panelConteudo, BoxLayout.LINE_AXIS));
        this.panelInferior = new JPanel();
        this.panelInferior.setLayout(new BoxLayout(this.panelInferior, BoxLayout.LINE_AXIS));
        this.contentPanel = new JPanel();
        this.contentPanel.setLayout(new BoxLayout(this.contentPanel, BoxLayout.PAGE_AXIS));
        this.panelEsquerdo = new JPanel();
        this.panelEsquerdo.setLayout(new BoxLayout(this.panelEsquerdo, BoxLayout.PAGE_AXIS));
        this.panelCorrentes = new JPanel();
        this.panelCorrentes.setLayout(new BoxLayout(this.panelCorrentes, BoxLayout.PAGE_AXIS));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    }

//    private void addItens() {
//        //this.setJMenuBar(new Menu);
//        this.panelConteudo.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));
//        this.panelConteudo.add(this.panelAjuste, Float.valueOf("0.2"));
//        this.panelConteudo.add(this.panelCoordenograma, Float.valueOf("0.35"));
//        this.panelConteudo.add(this.panelMapa, Float.valueOf("0.45"));
//
//        this.panelInferior.setLayout(new RelativeLayout(RelativeLayout.X_AXIS));
//        this.panelInferior.add(this.panelInfo, Float.valueOf("0.7"));
//        this.panelInferior.add(this.panelNavegacao, Float.valueOf("0.3"));
//        this.panelInferior.setPreferredSize(new Dimension(0, 30));
//
//        this.contentPanel.setLayout(new RelativeLayout(RelativeLayout.Y_AXIS));
//        this.contentPanel.add(this.panelConteudo, Float.valueOf("1"));
//        this.contentPanel.add(this.panelInferior);
//
//        this.add(this.contentPanel);
//    }
    private void addItens() {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.LINE_AXIS));

        this.panelEsquerdo.add(this.panelAjuste);
        this.panelEsquerdo.add(this.panelCorrentes);
        this.panelConteudo.add(this.panelEsquerdo);
        this.panelConteudo.add(this.panelCoordenograma);
        this.panelConteudo.add(this.panelMapa);
        this.panelInferior.add(this.panelInfo);
        this.panelInferior.add(this.panelNavegacao);
        this.contentPanel.add(this.panelConteudo);
        this.contentPanel.add(Box.createVerticalGlue());
        this.contentPanel.add(this.panelInferior);
        this.add(this.contentPanel);
    }

//    public static void main(String[] args) {
//        try {
//            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
//                if ("Windows".equals(info.getName())) {
//                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
//                    break;
//                }
//            }
//        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
//            LOGGER.error("ERRO NA SELEÇÃO DE LOOKANDFELL - " + ex.getMessage());
//        }
//        SwingUtilities.invokeLater(() -> {
//            new RelativeMainFrame().setVisible(true);
//        });
//    }
    @Override
    public void setMapa(Component panel) {
        this.clearMapa();
        this.panelMapa.add(panel);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void setCoordenograma(Component panel) {
        this.clearCoordenograma();
        this.panelCoordenograma.add(panel);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void setInfo(Component panel) {
        this.clearInfo();
        this.panelInfo.add(panel);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void setNavegacao(Component panel) {
        this.clearNavegacao();
        this.panelNavegacao.add(panel);
        this.revalidate();
        this.repaint();
    }

    @Override
    public void setAjuste(Component panel) {
        this.clearAjuste();
        this.panelAjuste.add(panel);
        this.panelAjuste.add(Box.createVerticalGlue());
        this.revalidate();
        this.repaint();
    }

    @Override
    public void setCorrentes(Component panel) {
        this.clearCorrentes();
        this.panelCorrentes.add(panel);
        this.panelCorrentes.add(Box.createVerticalGlue());
        this.revalidate();
        this.repaint();
    }

    @Override
    public void clearAll() {
        this.panelAjuste.removeAll();
        this.panelNavegacao.removeAll();
        this.panelCoordenograma.removeAll();
        this.panelInfo.removeAll();
        this.panelMapa.removeAll();
    }

    @Override
    public void clearMapa() {
        this.panelMapa.removeAll();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void clearCoordenograma() {
        this.panelCoordenograma.removeAll();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void clearCorrentes() {
        this.panelCorrentes.removeAll();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void clearInfo() {
        this.panelInfo.removeAll();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void clearNavegacao() {
        this.panelNavegacao.removeAll();
        this.revalidate();
        this.repaint();
    }

    @Override
    public void clearAjuste() {
        this.panelAjuste.removeAll();
        this.revalidate();
        this.repaint();
    }

    private void calculaTamanhos() { // calcula preferedSize dos panels e seta neles e em cada coisa q sera adicionada
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        LOGGER.debug("screensize = " + screenSize.toString());
        pack(); // Need this, otherwise insets() show as 0.  
        int scrW = (int) screenSize.getWidth();
        int scrH = (int) screenSize.getHeight();
        int innerW = scrW - getInsets().left - getInsets().right;
        int innerH = scrH - getInsets().top - getInsets().bottom;

        // Need to setSize(), otherwise pack() will collapse the empty JFrame
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);

        this.panelEsquerdo.setPreferredSize(new Dimension((int) (innerW * 0.2), 0));
        this.panelCoordenograma.setPreferredSize(new Dimension((int) (innerW * 0.4), 0));
        this.panelMapa.setPreferredSize(new Dimension((int) (innerW * 0.4), 0));
        this.panelInfo.setPreferredSize(new Dimension((int) (innerW * 0.7), 20));
        this.panelNavegacao.setPreferredSize(new Dimension((int) (innerW * 0.3), 20));

        this.panelConteudo.setPreferredSize(new Dimension(innerW, innerH - 20));
        this.panelInferior.setPreferredSize(new Dimension(innerW, 20));
        this.contentPanel.setPreferredSize(new Dimension(innerW, innerH));
    }
}
