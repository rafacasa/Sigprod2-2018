package br.edu.ifrs.farroupilha.sigprod2.frontend.frames;

import br.edu.ifrs.farroupilha.sigprod2.frontend.layout.RelativeLayout;
import java.awt.Component;
import java.awt.Dimension;
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
    private JPanel contentPanel;

    public RelativeMainFrame() {
        //this.rede = rede;
        this.initComponents();
        this.addItens();
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
        this.panelInferior = new JPanel();
        this.contentPanel = new JPanel();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
    }

    private void addItens() {
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

        this.add(this.contentPanel);
    }
//    private void addItens() {
//        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.LINE_AXIS));
//        this.add(this.panelAjuste);
//        this.add(this.panelCoordenograma);
//        this.add(this.panelMapa);
//        this.add(this.panelInfo);
//        this.add(this.panelNavegacao);
//    }

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
        
    }
}
