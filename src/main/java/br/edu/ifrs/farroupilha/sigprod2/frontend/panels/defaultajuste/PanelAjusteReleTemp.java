package br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.Main;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.frontend.dialogs.Erro;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class PanelAjusteReleTemp extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteReleTemp.class.getName());
    private final Rede rede;
    private final Ponto ponto;
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

    public PanelAjusteReleTemp(Rele rele, Rede rede, Ponto ponto) {
        this.rele = rele;
        this.rede = rede;
        this.ponto = ponto;
        this.nomePontos = new ArrayList<>();
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
        this.ajusteFase = new PanelDadosAjusteRele(this.rele.getAcsNIFase(), this.rele.getAcsMIFase(), this.rele.getAcsEIFase(), this.rele.getAjusteFase(), index, this.rele, true, this, this.rede, this.ponto);

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
        this.ajusteNeutro = new PanelDadosAjusteRele(this.rele.getAcsNINeutro(), this.rele.getAcsMINeutro(), this.rele.getAcsEINeutro(), this.rele.getAjusteNeutro(), index, rele, false, this, this.rede, this.ponto);
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
            LOGGER.debug("tempoFase - " + tempoFase);
            LOGGER.debug("tempoNeutro - " + tempoNeutro);
            if (tempoFase.compareTo(BigDecimal.ZERO) > 0) {
                LOGGER.debug("ENTROU FASE");
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoFase), "pontoFase", Arrays.asList(Color.BLUE)));
            }
            if (tempoNeutro.compareTo(BigDecimal.ZERO) > 0) {
                LOGGER.debug("ENTROU NEUTRO");
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoNeutro), "tempoNeutro", Arrays.asList(Color.RED)));
            }
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
        if (this.nomePontos != null && !this.nomePontos.isEmpty()) {
            this.nomePontos.forEach(s -> {
                this.coordenograma.remove(s);
            });
            this.nomePontos.clear();
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
        private final Rede rede;
        private final Ponto ponto;
        private final boolean fase;
        private boolean impossiveis;
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

        public PanelDadosAjusteRele(List<ACDisponivel> acsNI, List<ACDisponivel> acsMI, List<ACDisponivel> acsEI, AjusteRele ajusteInical, int indexCurvaInicial, Rele rele, boolean fase, PanelAjuste panelAjuste, Rede rede, Ponto ponto) {
            this.indexCurvaInicial = indexCurvaInicial;
            this.rele = rele;
            this.rede = rede;
            this.ponto = ponto;
            this.fase = fase;
            this.panelAjuste = panelAjuste;
            this.acsNI = acsNI;
            this.acsMI = acsMI;
            this.acsEI = acsEI;
            this.ajusteInicial = ajusteInical;
            this.impossiveis = false;
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
            this.ajustesImpossiveis.addActionListener(this::botaoAjustesImpossiveisActionPerformed);
            this.listaCurvas.addItem("Normalmente Inversa");
            this.listaCurvas.addItem("Muito Inversa");
            this.listaCurvas.addItem("Extremamente Inversa");
            this.listaCurvas.addActionListener(this::listaCurvaActionPerformed);
            this.listaAC.addActionListener(this::listaACActionPerformed);
            this.listaAT.addActionListener(this::listaATActionPerformed);
        }

        private void addItens() {
            this.setLayout(new MigLayout("wrap 2"));
            this.add(new JLabel("Curva: "));
            this.add(this.listaCurvas);
            this.add(new JLabel("AC: "));
            this.add(this.listaAC);
            this.add(new JLabel("AT: "));
            this.add(this.listaAT);
            this.add(this.ajustesImpossiveis, "span");
            this.add(this.reset, "span");
        }

        private void botaoResetActionPerformed(java.awt.event.ActionEvent evt) {
            this.setValoresIniciais();
            LOGGER.debug(evt.getActionCommand());
        }

        private void botaoAjustesImpossiveisActionPerformed(java.awt.event.ActionEvent evt) {
            LOGGER.debug(evt.getActionCommand());
            this.listaAC.removeAllItems();
            this.listaAT.removeAllItems();
            if (this.ajustesImpossiveis.getModel().isSelected()) {
                preencheDadosImpossiveis();
                this.impossiveis = true;
            } else {
                this.impossiveis = false;
                this.listaCurvaActionPerformed(evt);
            }
        }

        private void preencheDadosImpossiveis() {
            int index = this.listaCurvas.getSelectedIndex();
            CurvaRele curva = null;
            switch (index) {
                case 0:
                    curva = this.fase ? this.rele.getnIFase() : this.rele.getnINeutro();
                    break;
                case 1:
                    curva = this.fase ? this.rele.getmIFase() : this.rele.getmINeutro();
                    break;
                case 2:
                    curva = this.fase ? this.rele.geteIFase() : this.rele.geteINeutro();
                    break;
                default:
                    LOGGER.error("Index inexistente - listaCurvaActionPerformed");
            }
            BigDecimal maiorAt = curva.getMaiorAT();
            BigDecimal menorAt = curva.getMenorAT();
            BigDecimal passoAt = curva.getPassoAT();

            curva.gerarAC().forEach(ac -> {
                this.listaAC.addItem(new ACDisponivel(ac, maiorAt, menorAt, passoAt));
            });
        }

        private void setValoresIniciais() {
            this.listaCurvas.setSelectedIndex(this.indexCurvaInicial);
            for (int i = 0; i < this.listaAC.getItemCount(); i++) {
                ACDisponivel acD = this.listaAC.getItemAt(i);
                if (this.ajusteInicial.getAc().equals(acD.getAc())) {
                    this.listaAC.setSelectedIndex(i);
                    break;
                }
            }
            this.listaAT.setSelectedItem(this.ajusteInicial.getAt());
        }

        private void listaCurvaActionPerformed(java.awt.event.ActionEvent evt) {
            if (!this.impossiveis) {
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
            } else {
                preencheDadosImpossiveis();
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
            AjusteRele ajuste = new AjusteRele(BigDecimal.ZERO, at, ac.getAc(), curva);
            if (this.fase) {
                this.rele.setAjusteFase(new AjusteRele(BigDecimal.ZERO, at, ac.getAc(), curva));
            } else {
                this.rele.setAjusteNeutro(new AjusteRele(BigDecimal.ZERO, at, ac.getAc(), curva));
            }
            Corrente corrente = fase ? Corrente.ICC2F : Corrente.ICCFTMIN;
            BigDecimal iMinFFPP = BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.ponto, corrente));
            BigDecimal iMinFFPR = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(this.ponto, corrente));
            BigDecimal tempo1 = ajuste.calculaTempo(iMinFFPP);
            BigDecimal tempo2 = ajuste.calculaTempo(iMinFFPR);
            LOGGER.info("TEMPOS AJUSTES RELE");
            LOGGER.info("CORRENTE 1: " + iMinFFPP);
            LOGGER.info("TEMPO 1: " + tempo1);
            LOGGER.info("CORRENTE 2: " + iMinFFPR);
            LOGGER.info("TEMPO 2: " + tempo2);
            Main.setCoordenograma(this.panelAjuste.geraCoordenograma());
        }

    }
}
