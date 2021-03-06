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

public class PanelAjusteReleReligadorTemp extends PanelAjuste {

    private static final Logger LOGGER = LogManager.getLogger(PanelAjusteReleReligadorTemp.class.getName());
    private Religador religador;
    private Rele relePai;
    private List<String> nomePontos;
    private Coordenograma coordenograma;
    private JTextField campoCorrente;
    private JButton botaoMostrar;
    private JButton botaoLimpar;
    private JLabel fabricante;
    private JLabel modelo;
    private JTabbedPane ajustes;
    private JPanel ajusteFase;
    private JPanel ajusteNeutro;

    public PanelAjusteReleReligadorTemp(Ponto p, Ponto pOrigem) {
        this.relePai = (Rele) pOrigem.getEquipamentoInstalado();
        this.religador = (Religador) p.getEquipamentoInstalado();
        this.nomePontos = new ArrayList<>();
        this.logDadosRelePai();
        this.dadosReligador();
        this.initComponents();
        this.placeComponents();
        Main.setCoordenograma(this.geraCoordenograma());
    }

    private void logDadosRelePai() {
        LOGGER.info("DADOS RELE PAI - AJUSTANDO RELIGADOR");
        LOGGER.info("AJUSTES FASE");
        LOGGER.info("AC: " + this.relePai.getAjusteFase().getAc());
        LOGGER.info("AT: " + this.relePai.getAjusteFase().getAt());
        LOGGER.info("CURVA NI: " + this.relePai.getAjusteFase().getCurva().equals(CurvaRele.NI));
        LOGGER.info("CURVA MI: " + this.relePai.getAjusteFase().getCurva().equals(CurvaRele.MI));
        LOGGER.info("CURVA EI: " + this.relePai.getAjusteFase().getCurva().equals(CurvaRele.EI));
        LOGGER.info("AJUSTES NEUTRO");
        LOGGER.info("AC: " + this.relePai.getAjusteNeutro().getAc());
        LOGGER.info("AT: " + this.relePai.getAjusteNeutro().getAt());
        LOGGER.info("CURVA NI: " + this.relePai.getAjusteNeutro().getCurva().equals(CurvaRele.NI));
        LOGGER.info("CURVA MI: " + this.relePai.getAjusteNeutro().getCurva().equals(CurvaRele.MI));
        LOGGER.info("CURVA EI: " + this.relePai.getAjusteNeutro().getCurva().equals(CurvaRele.EI));
    }

    private void initComponents() {
        this.campoCorrente = new JTextField(5);
        this.botaoMostrar = new JButton("Exibir Tempo");
        this.botaoMostrar.addActionListener(this::botaoMostrarActionPerformed);
        this.botaoLimpar = new JButton("Limpar");
        this.botaoLimpar.addActionListener(this::botaoLimparActionPerformed);
    }

    private void dadosReligador() {
        this.fabricante = new JLabel("Fabricante: " + this.religador.getFabricante());
        this.modelo = new JLabel("Modelo: " + this.religador.getModelo());
        this.ajustes = new JTabbedPane(JTabbedPane.TOP);
        this.setPanelsAjuste();
        this.ajustes.addTab("Fase", null, this.ajusteFase, "Ajustes de Fase do Religador");
        this.ajustes.addTab("Neutro", null, this.ajusteNeutro, "Ajustes de Neutro do Religador");
    }

    private void setPanelsAjuste() {
        this.ajusteFase = new PanelDadosAjusteReleReligador(this.religador, true, this);
        this.ajusteNeutro = new PanelDadosAjusteReleReligador(this.religador, false, this);
    }

    private void placeComponents() {
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
            BigDecimal corrente = new BigDecimal(numeroDigitado);
            BigDecimal tempoFaseLenta = religador.getAjusteFase().calculaTempo(corrente);
            BigDecimal tempoNeutroLenta = religador.getAjusteNeutro().calculaTempo(corrente);
            BigDecimal tempoFaseRapida = religador.getAjusteRapidaFase().calculaTempo(corrente);
            BigDecimal tempoNeutroRapida = religador.getAjusteRapidaNeutro().calculaTempo(corrente);
            BigDecimal tempoFaseRele = this.relePai.getAjusteFase().calculaTempo(corrente);
            BigDecimal tempoNeutroRele = this.relePai.getAjusteNeutro().calculaTempo(corrente);
            if (tempoFaseLenta.compareTo(BigDecimal.ZERO) > 0) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoFaseLenta), "Corrente Digitada 1", Arrays.asList(Color.RED)));
            }
            if (tempoNeutroLenta.compareTo(BigDecimal.ZERO) > 0) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoNeutroLenta), "Corrente Digitada 2", Arrays.asList(Color.RED)));
            }
            if (tempoFaseRapida.compareTo(BigDecimal.ZERO) > 0) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoFaseRapida), "Corrente Digitada 3", Arrays.asList(Color.RED)));
            }
            if (tempoNeutroRapida.compareTo(BigDecimal.ZERO) > 0) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoNeutroRapida), "Corrente Digitada 4", Arrays.asList(Color.RED)));
            }
            if (tempoFaseRele.compareTo(BigDecimal.ZERO) > 0) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoFaseRele), "Corrente Digitada 5", Arrays.asList(Color.BLACK)));
            }
            if (tempoNeutroRele.compareTo(BigDecimal.ZERO) > 0) {
                this.nomePontos.addAll(this.coordenograma.add(corrente, Arrays.asList(tempoNeutroRele), "Corrente Digitada 6", Arrays.asList(Color.BLACK)));
            }
        } catch (NumberFormatException e) {
            Erro.entradaInvalida(this);
            LOGGER.error("STRING INVÁLIDA" + e.getMessage());
        } catch (NullPointerException e) {
            LOGGER.error("COORDENOGRAMA NAO ABERTO" + e.getMessage());
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
        this.coordenograma = new Coordenograma("Religador");
        this.coordenograma.add(this.religador, Color.RED, Color.RED, Color.RED, Color.RED);
        this.coordenograma.add(this.relePai, Color.BLACK, Color.BLACK);
        return this.coordenograma;
    }

    class PanelDadosAjusteReleReligador extends JPanel {

        private final Religador religador;
        private final boolean fase;
        private final PanelAjuste panelAjuste;
        private final int indexCurvaInicial;
        private final List<ACDisponivel> acsNI;
        private final List<ACDisponivel> acsMI;
        private final List<ACDisponivel> acsEI;
        private final AjusteRele ajusteInicial;
        private final AjusteRele ajusteInicialRapida;
        private boolean impossiveis;
        private JComboBox<String> listaCurvas;
        private JComboBox<ACDisponivel> listaAC;
        private JComboBox<BigDecimal> listaAT;
        private JComboBox<BigDecimal> listaATRapida;
        private JButton reset;
        private JToggleButton ajustesImpossiveis;

        public PanelDadosAjusteReleReligador(Religador religador, boolean fase, PanelAjuste panelAjuste) {
            this.religador = religador;
            this.fase = fase;
            this.panelAjuste = panelAjuste;
            this.acsNI = fase ? this.religador.getAcsNIFase() : this.religador.getAcsNINeutro();
            this.acsMI = fase ? this.religador.getAcsMIFase() : this.religador.getAcsMINeutro();
            this.acsEI = fase ? this.religador.getAcsEIFase() : this.religador.getAcsEINeutro();
            this.impossiveis = false;
            this.ajusteInicial = fase ? this.religador.getAjusteFase() : this.religador.getAjusteNeutro();
            this.ajusteInicialRapida = fase ? this.religador.getAjusteRapidaFase() : this.religador.getAjusteRapidaNeutro();
            if (this.ajusteInicial == null || this.ajusteInicialRapida == null) {
                this.indexCurvaInicial = -1;
            } else {
                if (this.ajusteInicial.getCurva().equals(CurvaRele.NI)) {
                    this.indexCurvaInicial = 0;
                } else if (this.ajusteInicial.getCurva().equals(CurvaRele.MI)) {
                    this.indexCurvaInicial = 1;
                } else {
                    this.indexCurvaInicial = 2;
                }
            }
            this.initComponents();
            this.setValoresIniciais();
            this.addItens();
        }

        private void initComponents() {
            this.listaCurvas = new JComboBox<>();
            this.listaAC = new JComboBox<>();
            this.listaAT = new JComboBox<>();
            this.listaATRapida = new JComboBox<>();
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
            this.listaATRapida.addActionListener(this::listaAtRapidaActionPerformed);
        }

        private void addItens() {
            this.setLayout(new MigLayout("wrap 2"));
            this.add(new JLabel("Curva: "));
            this.add(this.listaCurvas);
            this.add(new JLabel("AC: "));
            this.add(this.listaAC);
            this.add(new JLabel("AT: "));
            this.add(this.listaAT);
            this.add(new JLabel("AT Curva Rapida: "));
            this.add(this.listaATRapida);
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
            this.listaATRapida.removeAllItems();
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
                    curva = this.fase ? this.religador.getnIFase() : this.religador.getnINeutro();
                    break;
                case 1:
                    curva = this.fase ? this.religador.getmIFase() : this.religador.getmINeutro();
                    break;
                case 2:
                    curva = this.fase ? this.religador.geteIFase() : this.religador.geteINeutro();
                    break;
                default:
                    LOGGER.error("Index inexistente - listaCurvaActionPerformed");
            }
            BigDecimal maiorAt = curva.getMaiorAT();
            BigDecimal menorAt = curva.getMenorAT();
            BigDecimal passoAt = curva.getPassoAT();

            curva.gerarAC().forEach(ac -> {
                this.listaAC.addItem(new ACDisponivel(ac, maiorAt, menorAt, passoAt, true));
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
            if (this.ajusteInicial != null) {
                this.listaAT.setSelectedItem(this.ajusteInicial.getAt());
            }
            if (this.ajusteInicialRapida != null) {
                this.listaATRapida.setSelectedItem(this.ajusteInicialRapida.getAt());
            }
        }

        private void listaCurvaActionPerformed(java.awt.event.ActionEvent evt) {
            if (!this.impossiveis) {
                int index = this.listaCurvas.getSelectedIndex();
                switch (index) {
                    case 0:
                        this.listaAC.removeAllItems();
                        this.acsNI.forEach(ac -> {
                            this.listaAC.addItem(ac);
                        });
                        break;
                    case 1:
                        this.listaAC.removeAllItems();
                        this.acsMI.forEach(ac -> {
                            this.listaAC.addItem(ac);
                        });
                        break;
                    case 2:
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
            this.listaATRapida.removeAllItems();
            ACDisponivel ac = this.listaAC.getItemAt(this.listaAC.getSelectedIndex());
            if (ac != null) {
                ac.getListaAt().forEach(at -> {
                    this.listaAT.addItem(at);
                });
                ac.getListaAtRapida().forEach(at -> {
                    this.listaATRapida.addItem(at);
                });
                LOGGER.trace(evt.getActionCommand());
            }
        }

        private void listaATActionPerformed(java.awt.event.ActionEvent evt) {
            ACDisponivel ac = this.listaAC.getItemAt(this.listaAC.getSelectedIndex());
            BigDecimal at = this.listaAT.getItemAt(this.listaAT.getSelectedIndex());
            BigDecimal atr = this.listaATRapida.getItemAt(this.listaATRapida.getSelectedIndex());
            if (ac != null && at != null && atr != null) {
                this.salvarAjusteSelecionadoNoReligador();
            }
        }

        private void listaAtRapidaActionPerformed(java.awt.event.ActionEvent evt) {
            ACDisponivel ac = this.listaAC.getItemAt(this.listaAC.getSelectedIndex());
            BigDecimal at = this.listaAT.getItemAt(this.listaAT.getSelectedIndex());
            BigDecimal atr = this.listaATRapida.getItemAt(this.listaATRapida.getSelectedIndex());
            if (ac != null && at != null && atr != null) {
                this.salvarAjusteSelecionadoNoReligador();
            }
        }

        private void salvarAjusteSelecionadoNoReligador() {
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
            BigDecimal atRapida = this.listaATRapida.getItemAt(this.listaATRapida.getSelectedIndex());
            if (this.fase) {
                this.religador.setAjusteFase(new AjusteRele(BigDecimal.ZERO, at, ac.getAc(), curva));
                this.religador.setAjusteRapidaFase(new AjusteRele(BigDecimal.ZERO, atRapida, ac.getAc(), curva));
            } else {
                this.religador.setAjusteNeutro(new AjusteRele(BigDecimal.ZERO, at, ac.getAc(), curva));
                this.religador.setAjusteRapidaNeutro(new AjusteRele(BigDecimal.ZERO, atRapida, ac.getAc(), curva));
            }
            Main.setCoordenograma(this.panelAjuste.geraCoordenograma());
        }

    }
}
