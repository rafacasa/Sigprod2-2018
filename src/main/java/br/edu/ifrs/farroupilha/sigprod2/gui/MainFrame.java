package br.edu.ifrs.farroupilha.sigprod2.gui;

import br.edu.ifrs.farroupilha.sigprod2.gui.mainframepanels.Menu;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Arquivo;
import br.edu.ifrs.farroupilha.sigprod2.auxiliar.Erro;
import br.edu.ifrs.farroupilha.sigprod2.auxiliar.LoopPumpThread;
import br.edu.ifrs.farroupilha.sigprod2.auxiliar.NodeClickGraficoListener;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Trecho;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;

/**
 *
 * @author Rafael Luiz Casa
 */
public class MainFrame extends JFrame {

    private static final Logger LOGGER = LogManager.getLogger(MainFrame.class.getName());
    public static MainFrame frame;
    //private JPanel contentPane;
    private JPanel panelSuperior;
    private JPanel panelMiddle;
    private JPanel panelInferior;
    private JPanel panelLeft;
    private JPanel panelRight;
    private JPanel panelAll;
    private JButton botaoExecutarEstudo;
    private JButton botaoGrafico;
    private Rede rede;
    private List<Node> pontosGrafico;
    private boolean salvo, modoSelecionar;

    public MainFrame() {
        this.salvo = false;
        this.initComponents();
        this.initRede();

        //this.debugCorrentes("P23", Corrente.ICC3F);
    }

    private void debugCorrentes(String s, Corrente c) {
        Graph g = this.rede.getMapa();
        this.rede.verMapaReduzido();
        System.out.println("CORRENTE " + c + " NO " + s);
        System.out.println("NO PONTO: " + rede.buscaCorrentePonto(s, c));
        System.out.println("PROXIMA CAMADA: " + rede.buscaCorrenteProximoPonto(s, c));
        System.out.println("DUAS CAMADAS: " + rede.buscaCorrente2Camadas(s, c));
    }

    private void initComponents() {
        this.setTitle("SIGPROD2");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        initItens();
        initPanels();
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        //addBotaoExecutarAjustes();
    }

    private void initItens() {
        //config botao executar estudo
        this.botaoExecutarEstudo = new JButton();
        this.botaoExecutarEstudo.setText("Executar Estudo");
        botaoExecutarEstudo.addActionListener(this::botaoExecutarEstudoActionPerformed);

        this.botaoGrafico = new JButton();
        this.botaoGrafico.setText("Coordenograma");
        this.botaoGrafico.setEnabled(false);//NAO TA PRONTO O GRAFICO
        this.modoSelecionar = false;
        botaoGrafico.addActionListener(this::botaoGraficoActionPerformed);
    }

    private void initPanels() {
        //Painel Superior (Botao executar estudo)
        panelSuperior = new JPanel();
        panelSuperior.setLayout(new BoxLayout(panelSuperior, BoxLayout.LINE_AXIS));
        panelSuperior.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        panelLeft = new JPanel();
        panelLeft.setLayout(new BoxLayout(panelLeft, BoxLayout.PAGE_AXIS));
        panelLeft.setPreferredSize(new Dimension(0, 0));

        panelRight = new JPanel();
        panelRight.setLayout(new BoxLayout(panelRight, BoxLayout.PAGE_AXIS));

        panelMiddle = new JPanel();
        panelMiddle.setLayout(new BoxLayout(panelMiddle, BoxLayout.LINE_AXIS));
        panelMiddle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panelMiddle.add(panelLeft);
        panelMiddle.add(panelRight);

        panelInferior = new JPanel();
        panelInferior.setLayout(new BoxLayout(panelInferior, BoxLayout.LINE_AXIS));
        panelInferior.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));

        panelAll = new JPanel();
        panelAll.setLayout(new BoxLayout(panelAll, BoxLayout.PAGE_AXIS));
        panelAll.add(panelSuperior);
        panelAll.add(panelMiddle);
        panelAll.add(panelInferior);

        Container contentPane = getContentPane();
        contentPane.add(panelAll, BorderLayout.CENTER);

        setJMenuBar(new Menu(this));

        limparDataPanel();
        limparInfoPanel();

    }

    private void addBotaoExecutarAjustes() {
        panelSuperior.add(Box.createHorizontalGlue());
        panelSuperior.add(botaoGrafico);
        panelSuperior.add(Box.createRigidArea(new Dimension(20, 0)));
        panelSuperior.add(botaoExecutarEstudo);
        this.revalidate();
        this.repaint();
    }

    public void limparDataPanel() {
        JPanel temp = new JPanel();
        temp.setLayout(new BoxLayout(temp, BoxLayout.PAGE_AXIS));
        temp.setPreferredSize(new Dimension(11000, 0));
        temp.setMaximumSize(new Dimension(11000, 0));
        temp.add(Box.createRigidArea(new Dimension(0, 5)));
        setDataPanel(temp);
    }

    public void limparInfoPanel() {
        JPanel temp2 = new JPanel();
        temp2.setLayout(new BoxLayout(temp2, BoxLayout.LINE_AXIS));
        temp2.setPreferredSize(new Dimension(0, 17));
        temp2.setMaximumSize(new Dimension(0, 17));
        temp2.add(Box.createRigidArea(new Dimension(5, 0)));
        setInfoPanel(temp2);
    }

    private void initRede() {
        MainFrame.frame = this;
        System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");
        Arquivo arquivoRede = new Arquivo("redeRele.ABCEEE");
        try {
            this.rede = new Rede(arquivoRede);
        } catch (SQLException ex) {
            Erro.mostraMensagemSQL(this);
        }
        Component c = (Component) this.rede.getMapaView(this);
        c.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        c.setPreferredSize(new Dimension(100, 100));
        this.panelRight.add((Component) this.rede.getMapaView(this));
        this.addBotaoExecutarAjustes();
        this.pack();
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        new LoopPumpThread(this.rede).start();
    }

    public void setDataPanel(Component component) {
        this.panelLeft.removeAll();
        this.panelLeft.add(component);
        this.revalidate();
        this.repaint();
    }

    public void setInfoPanel(Component component) {
        this.panelInferior.removeAll();
        this.panelInferior.add(component);
        LOGGER.info("PRE - " + component.getPreferredSize());
        LOGGER.debug("MAX - " + component.getMaximumSize());
        this.revalidate();
        this.repaint();
    }

    /*    
    private void botaoExecutarEstudoActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            this.rede.ajuste();
        } catch (AjusteImpossivelException ex) {
            Erro.ajusteInvalido(this);
        }
    }
     */
    private void botaoExecutarEstudoActionPerformed(java.awt.event.ActionEvent evt) {
        SwingUtilities.invokeLater(() -> {
            new br.edu.ifrs.farroupilha.sigprod2.gui.AjusteFrame(this).setVisible(true);
        });
    }

    private void botaoGraficoActionPerformed(java.awt.event.ActionEvent evt) {
        if (modoSelecionar) {

        } else {
            entraModoSelecionar();
        }
    }

    private void entraModoSelecionar() {
        limparDataPanel();
        limparInfoPanel();

        JPanel info = new JPanel();
        info.setLayout(new BoxLayout(info, BoxLayout.LINE_AXIS));
        JLabel selecione = new JLabel("Selecione os equipamentos que deseja visualizar");
        selecione.setFont(new Font("Tahoma", 0, 14));
        selecione.setAlignmentX(Component.CENTER_ALIGNMENT);
        info.add(Box.createHorizontalGlue());
        info.add(selecione);
        setInfoPanel(info);

        this.pontosGrafico = new ArrayList<>();

        this.rede.setListener(new NodeClickGraficoListener(this));
        this.modoSelecionar = true;
    }

    public void addPontoGrafico(Node n) {
        this.pontosGrafico.add(n);
    }

    public void removePontoGrafico(Node n) {
        this.pontosGrafico.remove(n);
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
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    public void salvarRede() {
        Gson gson = new Gson();
        java.lang.reflect.Type listType = new TypeToken<List<Trecho>>() {
        }.getType();
        String s = gson.toJson(this.rede.getTrechosRede(), listType);
        System.out.println(s);
    }

    public List<Trecho> getRede(String json) {
        Gson gson = new Gson();
        java.lang.reflect.Type listType = new TypeToken<List<Trecho>>() {
        }.getType();
        List<Trecho> lista = gson.fromJson(json, listType);
        return lista;
    }

    public Rede getRede() {
        return rede;
    }

    public void abrirRede(Arquivo arquivo) {
        System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");
        Arquivo arquivoRede = arquivo;
        try {
            this.rede = new Rede(arquivoRede);
        } catch (SQLException ex) {
            Erro.mostraMensagemSQL(this);
        }
        Component c = (Component) this.rede.getMapaView(this);
        c.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        c.setPreferredSize(new Dimension(100, 100));
        this.panelRight.add((Component) this.rede.getMapaView(this));
        this.addBotaoExecutarAjustes();
        this.pack();
        setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
        new LoopPumpThread(this.rede).start();
    }

    public void fecharRede() {
        this.rede.stopPumpLoop();
        this.rede = null;
        Container contentPane = getContentPane();
        contentPane.removeAll();
        this.initPanels();
    }

    public boolean isSalvo() {
        return salvo;
    }

    public void setSalvo(boolean salvo) {
        this.salvo = salvo;
    }

}
