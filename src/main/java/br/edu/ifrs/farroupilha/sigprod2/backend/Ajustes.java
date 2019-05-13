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
        if (inicioRede) {
            return irParaInicioRede(ponto);
        } else {
            return irParaMeioRede(ponto);
        }
    }

    private boolean irParaInicioRede(Ponto ponto) {
        TipoEquipamento tipoEquipamento = ponto.getTipoEquipamentoInstalado();
        switch (tipoEquipamento) {
            case ELO:
                Elo ajuste;
                try {
                    ajuste = Criterios_Elo.criterio_elo(rede.getElosDisponiveis(), ponto, rede);
                    Main.selecionaEquipamento(ponto, ajuste);
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
                Main.selecionaEquipamento(ponto, rele);
                break;
            case RELIGADOR:
                Religador religador = CriteriosReligador.getReligadorTeste(); //DEFINIR QUAL RELIGADOR ESTA INSTALADO NO PONTO
                CriteriosReligador criteriosReligador = new CriteriosReligador(rede, ponto, religador);
                criteriosReligador.ajuste();
                frame.setAjuste(new PanelAjusteReligadorTemp(religador));
                Main.selecionaEquipamento(ponto, religador);
                break;
            default:
                LOGGER.error("DEFAULT CLAUSE");
                break;
        }
        return true;
    }

    private boolean irParaMeioRede(Ponto ponto) throws AjusteImpossivelException {
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
                        Main.selecionaEquipamento(ponto, religador);
                        frame.setAjuste(new PanelAjusteReleReligadorTemp(ponto, pOrigem));
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
}
