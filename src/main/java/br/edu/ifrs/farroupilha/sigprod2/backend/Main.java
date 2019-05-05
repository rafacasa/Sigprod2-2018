package br.edu.ifrs.farroupilha.sigprod2.backend;

import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.CriteriosReleReligador;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.CriteriosReligador;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.Criterios_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.Criterios_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.Criterios_Rele;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.Metricas_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Arquivo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Equipamento;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Religador;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.TipoEquipamento;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.BancoDeDadosException;
import br.edu.ifrs.farroupilha.sigprod2.frontend.frames.MainFrame;
import br.edu.ifrs.farroupilha.sigprod2.frontend.frames.RelativeMainFrame;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.CorrentesPonto;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.Navegacao;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteEloElo;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteReleElo;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteReleReligador;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteReleTemp;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteReligador;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteReligadorElo;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultmain.Informacoes;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Node;

/**
 * Classe Principal do Sistema
 *
 * @author Rafael Luiz Casa
 */
public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());
    private static Rede rede;
    private static MainFrame frame;
    private static Coordenograma coordenograma;
    private static int tipoSelecaoMapa = Main.SELECAO_DEFAULT;
    private static List<String> retasCorrentes;
    public static final int DEFAULT_GUI = 1;
    public static final int RELATIVE_GUI = 2;
    public static final int SELECAO_DEFAULT = 1;
    public static final int SELECAO_CORRENTES = 2;

    private static int getGuiType() { //ESTE METODO VERIFICARA QUAL TIPO DE GUI ESTA SALVA NAS CONFIGURACOES
        return RELATIVE_GUI;
    }

    private static Rede getRedeInicial() throws BancoDeDadosException {
        System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");
        return new Rede(getArquivoRedeInicial());
    }

    private static Arquivo getArquivoRedeInicial() {
        return new Arquivo("redeReleReligador1.ABCEEE");
    }

    private static void setupMainFrame(MainFrame frame) throws BancoDeDadosException {
        rede = getRedeInicial();
        Component c = (Component) rede.getMapaView();
        c.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        c.setPreferredSize(new Dimension(100, 100));
        frame.setMapa(c);
        frame.setCorrentes(new CorrentesPonto());
        Navegacao navegacao = new Navegacao(rede);
        navegacao.setMaximumSize(new Dimension(Short.MAX_VALUE, Short.MAX_VALUE));
        navegacao.setPreferredSize(new Dimension(100, 100));
        frame.setNavegacao(navegacao);
        navegacao.atualizarPontoAtual();
        irPara(navegacao.getPontoAtual(), true);
    }

    public static boolean irPara(Ponto ponto, boolean inicioRede) {
        if (inicioRede) {
            return irParaInicioRede(ponto);
        } else {
            return irParaMeioRede(ponto);
        }
    }

    private static boolean irParaInicioRede(Ponto ponto) {
        TipoEquipamento tipoEquipamento = ponto.getTipoEquipamentoInstalado();
        switch (tipoEquipamento) {
            case ELO:
                Elo ajuste;
                try {
                    ajuste = Criterios_Elo.criterio_elo(rede.getElosDisponiveis(), ponto, rede);
                    selecionaEquipamento(ponto, ajuste);
                } catch (AjusteImpossivelException ex) {
                    LOGGER.error("AJUSTE IMPOSSIVEL" + ex.getMessage());
                    return false;
                }
                break;
            case RELE:
                Rele rele = Criterios_Rele.getReleTeste(); //COMO DEFINIR QUAL EQUIPAMENTO ESTA INSTALADO NO PONTO
                Criterios_Rele criteriosRele = new Criterios_Rele(rede, ponto, rele);
                criteriosRele.ajuste();
                frame.setAjuste(new PanelAjusteReleTemp(rele));
                selecionaEquipamento(ponto, rele);
                break;
            case RELIGADOR:
                Religador religador = CriteriosReligador.getReligadorTeste(); //DEFINIR QUAL RELIGADOR ESTA INSTALADO NO PONTO
                CriteriosReligador criteriosReligador = new CriteriosReligador(Main.rede, ponto, religador);
                criteriosReligador.ajuste();
                frame.setAjuste(new PanelAjusteReligador(religador));
                selecionaEquipamento(ponto, religador);
                break;
            default:
                LOGGER.error("DEFAULT CLAUSE");
                break;
        }
        return true;
    }

    private static boolean irParaMeioRede(Ponto ponto) {
        Ponto pOrigem = rede.getParentRedeReduzida(ponto);
        TipoEquipamento tipoEquipamento = ponto.getTipoEquipamentoInstalado();
        TipoEquipamento tipoOrigem = pOrigem.getTipoEquipamentoInstalado();
        switch (tipoOrigem) {
            case ELO:
                switch (tipoEquipamento) {
                    case ELO:
                        Criterios_Elo_Elo criteriosEloElo = new Criterios_Elo_Elo(rede, pOrigem, ponto);
                        List<Metricas_Elo_Elo> metricas;
                        try {
                            metricas = criteriosEloElo.ajuste();
                            ponto.resetAtributos(true);
                            frame.setAjuste(new PanelAjusteEloElo(metricas, (Elo) pOrigem.getEquipamentoInstalado()));
                        } catch (AjusteImpossivelException ex) {
                            LOGGER.error("AJUSTE IMPOSSIVEL" + ex.getMessage());
                            return false;
                        }
                        break;
                    case RELE:

                        break;
                    case RELIGADOR:

                        break;
                    default:
                        LOGGER.error("DEFAULT CLAUSE");
                        break;
                }
                break;
            case RELE:
                switch (tipoEquipamento) {
                    case ELO:
                        frame.setAjuste(new PanelAjusteReleElo(ponto, rede, pOrigem));
                        break;
                    case RELE:

                        break;
                    case RELIGADOR:
                        Rele relePai = (Rele) pOrigem.getEquipamentoInstalado();
                        Religador religador = CriteriosReligador.getReligadorTeste(); //DEFINIR QUAL RELIGADOR ESTA INSTALADO NO PONTO
                        CriteriosReleReligador criterios = new CriteriosReleReligador(relePai, ponto, rede, religador);
                        criterios.ajuste();
                        selecionaEquipamento(ponto, religador);
                        frame.setAjuste(new PanelAjusteReleReligador(ponto, pOrigem));
                        break;
                    default:
                        LOGGER.error("DEFAULT CLAUSE");
                        break;
                }
                break;
            case RELIGADOR:
                switch (tipoEquipamento) {
                    case ELO:
                        frame.setAjuste(new PanelAjusteReligadorElo(ponto, rede, pOrigem));
                        break;
                    case RELE:

                        break;
                    case RELIGADOR:

                        break;
                    default:
                        LOGGER.error("DEFAULT CLAUSE");
                        break;
                }
                break;
            default:
                LOGGER.error("DEFAULT CLAUSE");
                break;
        }
        return true;
    }

    public static void selecionaEquipamento(Ponto p, Equipamento e) {
        p.setEquipamentoInstalado(e);
        p.resetAtributos(true);
    }

    public static void setCoordenograma(Coordenograma c) {
        if (c != null) {
            coordenograma = c;
            frame.setCoordenograma(c.getChartPanel());
        } else {
            frame.setCoordenograma(new JPanel());
        }
    }

    public static void setTipoSelecao(int tipoSelecao) {
        Main.tipoSelecaoMapa = tipoSelecao;
    }

    public static void mapaClickedActionPerformed(String id) {
        switch (Main.tipoSelecaoMapa) {
            case Main.SELECAO_DEFAULT:
                selecaoDefault(id);
                break;
            case Main.SELECAO_CORRENTES:
                selecaoCorrentes(id);
                break;
            default:
                LOGGER.error("DEFAULT CASE");
                break;
        }
    }

    private static void selecaoDefault(String id) {
        if (id == null) {
            frame.clearInfo();
        } else {
            LOGGER.debug("Button pushed on node " + id);
            Node n = rede.getMapa().getNode(id);
            Ponto p = n.getAttribute("classe", Ponto.class);
            LOGGER.debug("icc3f - " + p.getIcc3f());

            JPanel panelInfo = new Informacoes(n);
            frame.setInfo(panelInfo);
        }
    }

    private static void selecaoCorrentes(String id) {
        if (id != null) {
            LOGGER.debug("Button pushed on node " + id);
            Node n = rede.getMapa().getNode(id);
            Ponto p = n.getAttribute("classe", Ponto.class);
            LOGGER.debug("icc3f - " + p.getIcc3f());

            coordenograma.add(new BigDecimal(p.getIcc3f()), Color.red, "Icc3F" + id);
            coordenograma.add(new BigDecimal(p.getIcc2f()), Color.BLACK, "Icc2F" + id);
            coordenograma.add(new BigDecimal(p.getIccft()), Color.GREEN, "IccFT" + id);
            coordenograma.add(new BigDecimal(p.getIccftmin()), Color.MAGENTA, "IccFTMin" + id);
            retasCorrentes.add("Icc3F" + id);
            retasCorrentes.add("Icc2F" + id);
            retasCorrentes.add("IccFT" + id);
            retasCorrentes.add("IccFTMin" + id);
        }
    }

    public static void removeCorrentes() {
        retasCorrentes.forEach(s -> {
            coordenograma.remove(s);
        });
        retasCorrentes.clear();
    }

//    private static Rede getRede(String caminhoArquivo) {
//        
//    }
    public static void main(String[] args) {
        try {
            retasCorrentes = new ArrayList<>();
            int guiType = getGuiType();
            switch (guiType) {
                case DEFAULT_GUI:
                    LOGGER.error("AINDA NÃO DESENVOLVIDO");
                    break;
                case RELATIVE_GUI:
                    frame = new RelativeMainFrame();
                    break;
                default:
                    LOGGER.error("OPÇÃO INVÁLIDA DE GUI TYPE");
                    break;
            }
            try {
                for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                    if ("Windows".equals(info.getName())) {
                        javax.swing.UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
                LOGGER.error("look and feel nao encontgrado" + ex.getMessage());
            }
            SwingUtilities.invokeLater(() -> {
                frame.setVisible(true);
            });
            setupMainFrame(frame);
            LOGGER.info("over");
        } catch (BancoDeDadosException ex) {
            LOGGER.error("ERRO DE BANCO DE DADOS" + ex.getMessage());
        }
    }
}
