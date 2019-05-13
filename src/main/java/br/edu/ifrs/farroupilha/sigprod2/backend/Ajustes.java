package br.edu.ifrs.farroupilha.sigprod2.backend;

import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.Metricas_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.frontend.frames.MainFrame;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class Ajustes {
    private static final Logger LOGGER = LogManager.getLogger(Ajustes.class.getName());

    private Rede rede;
    private MainFrame frame;

    public Ajustes(Rede rede, MainFrame frame) { //Inicializa os dados da rede para os ajustes e realiza os pre-ajustes
        this.rede = rede;
        this.frame = frame;
    }

    public boolean irPara(Ponto ponto, boolean inicioRede) throws AjusteImpossivelException {
        Equipamento equipamento = this.calculaAjuste(ponto, inicioRede);
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
        throw new RuntimeException("calculaAjusteMeioRede() nao era Rele-Religador");
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
                Main.setPanelAjuste(new PanelAjusteReleTemp((Rele) e));
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
        TipoEquipamento tipoEquipamento = e.getTipoEquipamento();
        TipoEquipamento tipoOrigem = origem.getTipoEquipamento();
        switch (tipoOrigem) {
            case ELO:
                if (tipoEquipamento == TipoEquipamento.ELO) {
                    Criterios_Elo_Elo criteriosEloElo = new Criterios_Elo_Elo(rede, pOrigem, ponto);
                    List<Metricas_Elo_Elo> metricas;
                    metricas = criteriosEloElo.ajuste();
                    ponto.resetAtributos(true);
                    Main.setPanelAjuste(new PanelAjusteEloElo(metricas, (Elo) pOrigem.getEquipamentoInstalado()));
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
                    default:
                        LOGGER.error("DEFAULT CLAUSE - getAjustePanelMeioRede - tipoOrigem = RELE");
                        throw new UnsupportedOperationException();
                }
                break;
            case RELIGADOR:
                if (tipoEquipamento == TipoEquipamento.ELO) {
                    Main.setPanelAjuste(new PanelAjusteReligadorElo(ponto, rede, pOrigem));
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
