package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.ACDisponivel;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvaRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import java.awt.Color;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelAjusteReleTemp extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteReleTemp.class.getName());
    private final Rele rele;
    private List<String> nomePontos;
    private Coordenograma coordenograma;
    private JTextField campoCorrente;
    private JButton botaoMostrar;
    private JButton botaoLimpar;
    private JLabel fabricante;
    private JLabel modelo;
    private JTabbedPane ajustes;
    private PanelDadosAjusteRele ajusteFase;
    private PanelDadosAjusteRele ajusteNeutro;

    public PanelAjusteReleTemp(Rele rele) {
        this.rele = rele;
        this.initComponents();
        this.preencheDados();
        this.addItens();
        Main.setCoordenograma(this.geraCoordenograma());
    }

    private void initComponents() {
        this.campoCorrente = new JTextField(5);
        this.botaoMostrar = new JButton("Exibir Tempo");
        this.botaoMostrar.addActionListener(this::botaoMostrarActionPerformed);
        this.botaoLimpar = new JButton("Limpar");
        this.botaoLimpar.addActionListener(this::botaoLimparActionPerformed);
    }

    private void preencheDados() {
        this.fabricante = new JLabel("Fabricante: " + this.rele.getFabricante());
        this.modelo = new JLabel("Modelo: " + this.rele.getModelo());
        this.ajustes = new JTabbedPane(JTabbedPane.TOP);
        this.setPanelsAjuste();
        this.ajustes.addTab("Fase", null, this.ajusteFase, "Ajustes de Fase do Rele");
        this.ajustes.addTab("Neutro", null, this.ajusteNeutro, "Ajustes de Neutro do Rele");
    }

    private void setPanelsAjuste() {
        int index = 0;
        switch (this.rele.getNomeCurva(this.rele.getAjusteFase().getCurva(), true)) {
            case "Curva Normalmente Inversa":
                index = 0;
                break;
            case "Curva Muito Inversa":
                index = 1;
                break;
            case "Curva Extremamente Inversa":
                index = 2;
                break;
            default:
                LOGGER.error("setPanelsAjuste - erro curva fase");
                throw new RuntimeException("setPanelsAjuste - erro curva fase");
        }
        this.ajusteFase = new PanelDadosAjusteRele(this.rele.getAcsNIFase(), this.rele.getAcsMIFase(), this.rele.getAcsEIFase(), this.rele.getAjusteFase(), index, this.rele, true, this);

        switch (this.rele.getNomeCurva(this.rele.getAjusteNeutro().getCurva(), false)) {
            case "Curva Normalmente Inversa":
                index = 0;
                break;
            case "Curva Muito Inversa":
                index = 1;
                break;
            case "Curva Extremamente Inversa":
                index = 2;
                break;
            default:
                LOGGER.error("setPanelsAjuste - erro curva neutro");
                throw new RuntimeException("setPanelsAjuste - erro curva neutro");
        }
        this.ajusteNeutro = new PanelDadosAjusteRele(this.rele.getAcsNINeutro(), this.rele.getAcsMINeutro(), this.rele.getAcsEINeutro(), this.rele.getAjusteNeutro(), index, rele, false, this);
    }

    private void addItens() {
        this.setLayout(new MigLayout());
        this.add(this.fabricante, "wrap");
        this.add(this.modelo, "wrap");
        this.add(this.ajustes, "wrap");
        this.add(this.campoCorrente, "wrap");
        this.add(this.botaoMostrar, "wrap");
        this.add(this.botaoLimpar, "wrap");
    }

    public void botaoMostrarActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        this.limparPontos();
        String numeroDigitado = this.campoCorrente.getText();
        try {
            BigDecimal corrente = new BigDecimal(numeroDigitado); //MARCAR ESTA CORRENTE COM TEMPO CALCULADO
            BigDecimal tempoFase = rele.getAjusteFase().calculaTempo(corrente);
            BigDecimal tempoNeutro = rele.getAjusteNeutro().calculaTempo(corrente);
            this.nomePontos = this.coordenograma.add(corrente, Arrays.asList(tempoFase, tempoNeutro), "pontoDigitado", Arrays.asList(Color.BLUE, Color.RED));
        } catch (NumberFormatException e) {
            Erro.entradaInvalida(this);
            LOGGER.trace("STRING INVÁLIDA" + e.getMessage());
        } catch (NullPointerException e) {
            LOGGER.trace("COORDENOGRAMA NAO ABERTO" + e.getMessage());
        }
    }

    public void botaoLimparActionPerformed(java.awt.event.ActionEvent evt) {
        LOGGER.trace(evt.getActionCommand());
        this.limparPontos();
    }

    public void limparPontos() {
        if (this.nomePontos != null) {
            this.nomePontos.forEach(s -> {
                this.coordenograma.remove(s);
            });
        }
    }

    @Override
    public final Coordenograma geraCoordenograma() {
        this.coordenograma = new Coordenograma("Relé");
        this.coordenograma.add(this.rele, Color.BLUE, Color.RED);
        return this.coordenograma;
    }

    class PanelDadosAjusteRele extends JPanel {

        private final Rele rele;
        private final boolean fase;
        private final PanelAjuste panelAjuste;
        private final int indexCurvaInicial;
        private final List<ACDisponivel> acsNI;
        private final List<ACDisponivel> acsMI;
        private final List<ACDisponivel> acsEI;
        private JComboBox<String> listaCurvas;
        private JComboBox<ACDisponivel> listaAC;
        private JComboBox<BigDecimal> listaAT;
        private final AjusteRele ajusteInicial;
        private JButton reset;
        private JToggleButton ajustesImpossiveis;

        public PanelDadosAjusteRele(List<ACDisponivel> acsNI, List<ACDisponivel> acsMI, List<ACDisponivel> acsEI, AjusteRele ajusteInical, int indexCurvaInicial, Rele rele, boolean fase, PanelAjuste panelAjuste) {
            this.indexCurvaInicial = indexCurvaInicial;
            this.rele = rele;
            this.fase = fase;
            this.panelAjuste = panelAjuste;
            this.acsNI = acsNI;
            this.acsMI = acsMI;
            this.acsEI = acsEI;
            this.ajusteInicial = ajusteInical;
            this.initComponents();
            this.setValoresIniciais();
            this.addItens();
        }

        private void initComponents() {
            this.listaCurvas = new JComboBox<>();
            this.listaAC = new JComboBox<>();
            this.listaAT = new JComboBox<>();
            this.reset = new JButton("Resetar Ajustes");
            this.reset.addActionListener(this::botaoResetActionPerformed);
            this.ajustesImpossiveis = new JToggleButton("Permitir ajustes impossiveis");
            this.listaCurvas.addItem("Normalmente Inversa");
            this.listaCurvas.addItem("Muito Inversa");
            this.listaCurvas.addItem("Extremamente Inversa");
            this.listaCurvas.addActionListener(this::listaCurvaActionPerformed);
            this.listaAC.addActionListener(this::listaACActionPerformed);
            this.listaAT.addActionListener(this::listaATActionPerformed);
        }

        private void addItens() {
            this.setLayout(new MigLayout());
            this.add(new JLabel("Curva: "));
            this.add(this.listaCurvas, "wrap");
            this.add(new JLabel("AC: "));
            this.add(this.listaAC, "wrap");
            this.add(new JLabel("AT: "));
            this.add(this.listaAT, "wrap");
            this.add(this.reset, "wrap");
        }

        private void botaoResetActionPerformed(java.awt.event.ActionEvent evt) {
            this.setValoresIniciais();
            LOGGER.debug(evt.getActionCommand());
        }

        private void botaoAjustesImpossiveisActionPerformed(java.awt.event.ActionEvent evt) {
            LOGGER.debug(evt.getActionCommand());
        }

        private void setValoresIniciais() {
            this.listaCurvas.setSelectedIndex(this.indexCurvaInicial);
            this.listaAC.setSelectedItem(this.ajusteInicial.getAc());
            this.listaAT.setSelectedItem(this.ajusteInicial.getAt());
        }

        private void listaCurvaActionPerformed(java.awt.event.ActionEvent evt) {
            int index = this.listaCurvas.getSelectedIndex();
            switch (index) {
                case 0:
                    this.listaAT.removeAllItems();
                    this.listaAC.removeAllItems();
                    this.acsNI.forEach(ac -> {
                        this.listaAC.addItem(ac);
                    });
                    break;
                case 1:
                    this.listaAT.removeAllItems();
                    this.listaAC.removeAllItems();
                    this.acsMI.forEach(ac -> {
                        this.listaAC.addItem(ac);
                    });
                    break;
                case 2:
                    this.listaAT.removeAllItems();
                    this.listaAC.removeAllItems();
                    this.acsEI.forEach(ac -> {
                        this.listaAC.addItem(ac);
                    });
                    break;
                default:
                    LOGGER.error("Index inexistente - listaCurvaActionPerformed");
                    LOGGER.error(evt.getActionCommand());
            }
        }

        private void listaACActionPerformed(java.awt.event.ActionEvent evt) {
            this.listaAT.removeAllItems();
            ACDisponivel ac = this.listaAC.getItemAt(this.listaAC.getSelectedIndex());
            if (ac != null) {
                ac.getListaAt().forEach(at -> {
                    this.listaAT.addItem(at);
                });
                LOGGER.trace(evt.getActionCommand());
            }
        }

        private void listaATActionPerformed(java.awt.event.ActionEvent evt) {
            ACDisponivel ac = this.listaAC.getItemAt(this.listaAC.getSelectedIndex());
            BigDecimal at = this.listaAT.getItemAt(this.listaAT.getSelectedIndex());
            if (ac != null && at != null) {
                this.salvarAjusteSelecionadoNoRele();
            }
        }

        private void salvarAjusteSelecionadoNoRele() {
            int indexCurva = this.listaCurvas.getSelectedIndex();
            CurvaRele curva = null;
            switch (indexCurva) {
                case 0:
                    curva = CurvaRele.NI;
                    break;
                case 1:
                    curva = CurvaRele.MI;
                    break;
                case 2:
                    curva = CurvaRele.EI;
                    break;
                default:
                    LOGGER.error("INDEX NAO EXISTENTE SALVAR AJUSTE RELE");
            }
            ACDisponivel ac = this.listaAC.getItemAt(this.listaAC.getSelectedIndex());
            BigDecimal at = this.listaAT.getItemAt(this.listaAT.getSelectedIndex());
            if (this.fase) {
                this.rele.setAjusteFase(new AjusteRele(BigDecimal.ZERO, at, ac.getAc(), curva));
            } else {
                this.rele.setAjusteNeutro(new AjusteRele(BigDecimal.ZERO, at, ac.getAc(), curva));
            }
            Main.setCoordenograma(this.panelAjuste.geraCoordenograma());
        }

    }
}
