package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.MetricasReleElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.MetricasReligadorElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvasElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Religador;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class CriteriosReligadorElo {

    private static final Logger LOGGER = LogManager.getLogger(CriteriosReligadorElo.class.getName());
    private Religador religadorPai;
    private Ponto pontoRede;
    private Rede rede;
    private Criterios_Rele_Elo criteriosReleElo;

    public CriteriosReligadorElo(Religador religadorPai, Ponto pontoRede, Rede rede) {
        this.religadorPai = religadorPai;
        this.pontoRede = pontoRede;
        this.rede = rede;
        this.criteriosReleElo = new Criterios_Rele_Elo(religadorPai, pontoRede, rede);
    }

    public List<MetricasReligadorElo> ajuste() throws AjusteImpossivelException {
        List<MetricasReleElo> ajustes = this.criteriosReleElo.ajuste();
        List<MetricasReligadorElo> ajustesReligador = this.convertMetricasReleElo(ajustes);
        this.verificaCurvaRapidaElo(ajustesReligador);
        return ajustesReligador;
    }

    private List<MetricasReligadorElo> convertMetricasReleElo(List<MetricasReleElo> ajustes) {
        List<MetricasReligadorElo> retorno = new ArrayList<>();
        ajustes.forEach(ajuste -> {
            retorno.add(new MetricasReligadorElo(ajuste));
        });
        return retorno;
    }

    private void verificaCurvaRapidaElo(List<MetricasReligadorElo> ajustes) {
        this.verificaCurvaRapidaElo(ajustes, true);
        this.verificaCurvaRapidaElo(ajustes, false);
    }

    private void verificaCurvaRapidaElo(List<MetricasReligadorElo> ajustes, boolean fase) {
        LOGGER.debug("FASE " + fase);
        AjusteRele curvaRapida = fase ? this.religadorPai.getAjusteRapidaFase() : this.religadorPai.getAjusteRapidaNeutro();
        BigDecimal correnteMin = this.getCorrenteMin(fase);
        BigDecimal correnteMax = this.getCorrenteMax(fase);
        BigDecimal tempoCorrenteMax = curvaRapida.calculaTempo(correnteMax);
        BigDecimal tempoCorrenteMin = curvaRapida.calculaTempo(correnteMin);

        LOGGER.debug("RELE ");
        LOGGER.debug("tempoCorrenteMax = " + tempoCorrenteMax);
        LOGGER.debug("tempoCorrenteMin = " + tempoCorrenteMin);

        ajustes.forEach(ajuste -> {
            double tempoEloMaxd = ajuste.getElo().tempoDaCorrente(correnteMax.doubleValue(), CurvasElo.MINIMA);
            double tempoEloMind = ajuste.getElo().tempoDaCorrente(correnteMin.doubleValue(), CurvasElo.MINIMA);
            LOGGER.debug("ELO " + ajuste.getElo().getCorrenteNominal());
            LOGGER.debug("tempoEloMin = " + tempoEloMind);
            LOGGER.debug("tempoEloMax = " + tempoEloMaxd);
            BigDecimal tempoEloMax = BigDecimal.valueOf(tempoEloMaxd);
            BigDecimal tempoEloMin = BigDecimal.valueOf(tempoEloMind);
            ajuste.setSeletividadeRapida((tempoCorrenteMax.compareTo(tempoEloMax) < 0) && (tempoCorrenteMin.compareTo(tempoEloMin) < 0));
        });
    }

    private BigDecimal getCorrenteMax(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.ICC3F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.ICCFT));
        }
    }

    private BigDecimal getCorrenteMin(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pontoRede, Corrente.ICC2F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pontoRede, Corrente.ICCFTMIN));
        }
    }
}
