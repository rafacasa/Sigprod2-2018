package br.edu.ifrs.farroupilha.sigprod2.backend;

import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.dadospreajuste.DadosPreAjusteReleElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.dadospreajuste.DadosPreAjusteReleReligador;
import br.edu.ifrs.farroupilha.sigprod2.backend.dadospreajuste.DadosPreAjusteReligadorElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.Metricas_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.frontend.frames.MainFrame;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Ajustes {
    private static final Logger LOGGER = LogManager.getLogger(Ajustes.class.getName());

    private Rede rede;
    private MainFrame frame;

    public Ajustes(Rede rede, MainFrame frame) throws AjusteImpossivelException { //Inicializa os dados da rede para os ajustes e realiza os pre-ajustes
        this.rede = rede;
        this.frame = frame;
        this.preAjuste();
    }

    private void preAjuste() throws AjusteImpossivelException {
        List<List<Ponto>> camadas = this.rede.getCamadasRedeReduzida();
        List<Ponto> camada;
        for (int i = camadas.size() - 1; i > 0; i--) {
            camada = camadas.get(i);
            for (Ponto ponto : camada) {
                realizarPreAjuste(ponto);
            }
        }
    }

    private boolean realizarPreAjuste(Ponto p) throws AjusteImpossivelException {
        Object o;
        TipoEquipamento tipoFilho = p.getTipoEquipamentoInstalado();
        Ponto pai = rede.getParentRedeReduzida(p);
        TipoEquipamento tipoPai = pai.getTipoEquipamentoInstalado();

        if (tipoFilho == TipoEquipamento.NENHUM) return false;
        if (tipoPai == TipoEquipamento.NENHUM) return false;

        switch (tipoPai) {
            case ELO:
                if (tipoFilho == TipoEquipamento.ELO) {
                    //throw new UnsupportedOperationException();
                } else {
                    //throw new UnsupportedOperationException();
                }
                return false;
            case RELE:
                switch (tipoFilho) {
                    case ELO:
                        Criterios_Rele_Elo criterios = new Criterios_Rele_Elo(null, p, this.rede);
                        List<Elo> elosDisponiveis = criterios.getElosPossiveis();
                        o = pai.getNode().getAttribute("preajusteELO");
                        DadosPreAjusteReleElo dados;
                        if (o == null) {
                            dados = new DadosPreAjusteReleElo(elosDisponiveis.get(elosDisponiveis.size() - 1), p, this.rede);
                            pai.getNode().setAttribute("preajusteELO", dados);
                        } else {
                            dados = (DadosPreAjusteReleElo) o;
                            dados.addPreAjuste(elosDisponiveis.get(elosDisponiveis.size() - 1), p, this.rede);
                        }
                        break;
                    case RELIGADOR:
                        CriteriosReleReligador criterio = new CriteriosReleReligador(null, p, this.rede, null);
                        DadosPreAjusteReleReligador correntes = criterio.calculaPreAjuste(true);
                        o = pai.getNode().getAttribute("preajusteRELIG");
                        List<DadosPreAjusteReleReligador> listaDados;
                        if (o == null) {
                            listaDados = new ArrayList<>();
                            listaDados.add(correntes);
                            pai.getNode().setAttribute("preajusteRELIG",listaDados);
                        } else {
                            listaDados = (List<DadosPreAjusteReleReligador>) o;
                            listaDados.add(correntes);
                        }
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
                break;
            case RELIGADOR:
                if (tipoFilho == TipoEquipamento.ELO) {
                    CriteriosReligadorElo criterios = new CriteriosReligadorElo(null, p, this.rede);
                    List<Elo> elosDisponiveis = criterios.getElosPossiveis();
                    o = pai.getNode().getAttribute("preajuste");
                    DadosPreAjusteReligadorElo dados;
                    if (o == null) {
                        dados = new DadosPreAjusteReligadorElo(elosDisponiveis.get(0), p, this.rede, criterios.getFatorK(), elosDisponiveis.get(elosDisponiveis.size() - 1));
                        pai.getNode().setAttribute("preajuste", dados);
                    } else {
                        dados = (DadosPreAjusteReligadorElo) o;
                        dados.addPreAjuste(elosDisponiveis.get(0), p, this.rede, criterios.getFatorK(), elosDisponiveis.get(elosDisponiveis.size() - 1));
                    }
                } else {
                    throw new UnsupportedOperationException();
                }
                break;
            default:
                throw new UnsupportedOperationException();
        }
        return true;
    }

    public boolean irPara(Ponto ponto, boolean inicioRede) throws AjusteImpossivelException {
        Equipamento equipamento = null;
        //try {
            equipamento = this.calculaAjuste(ponto, inicioRede);
        //} catch (RuntimeException e) {
            //LOGGER.debug("Nao eh necessario passar pelo metodo calculaAjuste()");
            //LOGGER.error(e.getMessage());
        //}
        this.getAjustePanel(ponto, equipamento, inicioRede);
        return true;
    }

    public boolean irPara(Ponto ponto, boolean inicioRede, boolean useCache) throws AjusteImpossivelException {
        Equipamento equipamento = ponto.getEquipamentoInstalado();
        this.getAjustePanel(ponto, equipamento, inicioRede);
        return true;
    }

    private Equipamento calculaAjuste(Ponto ponto, boolean inicioRede) throws AjusteImpossivelException {
        if (inicioRede) {
            return calculaAjusteInicioRede(ponto);
        } else {
            return calculaAjusteMeioRede(ponto);
        }
    }

    private Equipamento calculaAjusteInicioRede(Ponto ponto) throws AjusteImpossivelException {
        TipoEquipamento tipoEquipamento = ponto.getTipoEquipamentoInstalado();
        switch (tipoEquipamento) {
            case ELO:
                return Criterios_Elo.criterio_elo(rede.getElosDisponiveis(), ponto, rede);
            case RELE:
                Rele rele = Criterios_Rele.getReleTeste(); //COMO DEFINIR QUAL EQUIPAMENTO ESTA INSTALADO NO PONTO
                Criterios_Rele criteriosRele = new Criterios_Rele(rede, ponto, rele);
                criteriosRele.ajuste();
                return rele;
            case RELIGADOR:
                Religador religador = CriteriosReligador.getReligadorTeste(); //DEFINIR QUAL RELIGADOR ESTA INSTALADO NO PONTO
                CriteriosReligador criteriosReligador = new CriteriosReligador(rede, ponto, religador);
                criteriosReligador.ajuste();
                return religador;
            default:
                LOGGER.error("DEFAULT CLAUSE");
                throw new AjusteImpossivelException("DEFAULT CLAUSE - calculaAjusteInicial()");
        }
    }

    private Equipamento calculaAjusteMeioRede(Ponto ponto) throws AjusteImpossivelException {
        Ponto pOrigem = rede.getParentRedeReduzida(ponto);
        TipoEquipamento tipoEquipamento = ponto.getTipoEquipamentoInstalado();
        TipoEquipamento tipoOrigem = pOrigem.getTipoEquipamentoInstalado();
        if (tipoOrigem == TipoEquipamento.RELE && tipoEquipamento == TipoEquipamento.RELIGADOR) {
            Rele relePai = (Rele) pOrigem.getEquipamentoInstalado();
            Religador religador = CriteriosReligador.getReligadorTeste(); //DEFINIR QUAL RELIGADOR ESTA INSTALADO NO PONTO
            CriteriosReleReligador criterios = new CriteriosReleReligador(relePai, ponto, rede, religador);
            criterios.ajuste();
            return religador;
        }
        //throw new RuntimeException("calculaAjusteMeioRede() nao era Rele-Religador");
        return null;
    }

    private void getAjustePanel(Ponto p, Equipamento e, boolean inicioRede) throws AjusteImpossivelException {
        if (inicioRede) {
            this.getAjustePanelInicioRede(p, e);
        } else {
            this.getAjustePanelMeioRede(p, e);
        }
    }

    private void getAjustePanelInicioRede(Ponto ponto, Equipamento e) {
        TipoEquipamento tipoEquipamento = e.getTipoEquipamento();
        switch (tipoEquipamento) {
            case ELO:
                Main.setPanelAjuste(null);
                break;
            case RELE:
                Main.setPanelAjuste(new PanelAjusteReleTemp((Rele) e, this.rede, ponto));
                break;
            case RELIGADOR:
                Main.setPanelAjuste(new PanelAjusteReligadorTemp((Religador) e));
                break;
            default:
                LOGGER.error("DEFAULT CLAUSE: getAjustePanelInicioRede");
        }
        Main.selecionaEquipamento(ponto, e);
    }

    private void getAjustePanelMeioRede(Ponto ponto, Equipamento e) throws AjusteImpossivelException {
        Ponto pOrigem = rede.getParentRedeReduzida(ponto);
        Equipamento origem = pOrigem.getEquipamentoInstalado();
        TipoEquipamento tipoEquipamento = ponto.getTipoEquipamentoInstalado();
        TipoEquipamento tipoOrigem = origem.getTipoEquipamento();
        switch (tipoOrigem) {
            case ELO:
                if (tipoEquipamento == TipoEquipamento.ELO) {
                    Criterios_Elo_Elo criteriosEloElo = new Criterios_Elo_Elo(rede, pOrigem, ponto);
                    List<Metricas_Elo_Elo> metricas;
                    metricas = criteriosEloElo.ajuste();
                    ponto.resetAtributos(true);
                    Main.setPanelAjuste(new PanelAjusteEloElo(metricas, (Elo) pOrigem.getEquipamentoInstalado()));
                } else if (tipoEquipamento == TipoEquipamento.NENHUM) {
                    LOGGER.error("fim de trecho selecionado");
                } else {
                    LOGGER.error("DEFAULT CLAUSE - getAjustePanelMeioRede - tipoOrigem = ELO");
                    throw new UnsupportedOperationException();
                }
                break;
            case RELE:
                switch (tipoEquipamento) {
                    case ELO:
                        Main.setPanelAjuste(new PanelAjusteReleElo(ponto, rede, pOrigem));
                        break;
                    case RELIGADOR:
                        Main.selecionaEquipamento(ponto, e);
                        Main.setPanelAjuste(new PanelAjusteReleReligadorTemp(ponto, pOrigem));
                        break;
                    case NENHUM:
                        LOGGER.error("fim de trecho selecionado");
                        break;
                    default:
                        LOGGER.error("DEFAULT CLAUSE - getAjustePanelMeioRede - tipoOrigem = RELE");
                        throw new UnsupportedOperationException();
                }
                break;
            case RELIGADOR:
                if (tipoEquipamento == TipoEquipamento.ELO) {
                    Main.setPanelAjuste(new PanelAjusteReligadorElo(ponto, rede, pOrigem));
                } else if (tipoEquipamento == TipoEquipamento.NENHUM) {
                    LOGGER.error("fim de trecho selecionado");
                } else {
                    LOGGER.error("DEFAULT CLAUSE - getAjustePanelMeioRede - tipoOrigem = RELIGADOR");
                    throw new UnsupportedOperationException();
                }
                break;
            default:
                LOGGER.error("DEFAULT CLAUSE - getAjustePanelMeioRede");
                throw new UnsupportedOperationException();
        }
    }
}
