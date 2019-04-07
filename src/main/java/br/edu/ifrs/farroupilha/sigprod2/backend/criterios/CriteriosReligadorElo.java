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

/**
 *
 * @author Rafael Luiz Casa
 */
public class CriteriosReligadorElo {

    private Religador religadorPai;
    private Ponto pontoRede;
    private Rede rede;
    private Criterios_Rele_Elo criteriosReleElo;

    public CriteriosReligadorElo(Religador religadorPai, Ponto pontoRede, Rede rede, Ponto pOrigem) {
        this.religadorPai = religadorPai;
        this.pontoRede = pontoRede;
        this.rede = rede;
        this.criteriosReleElo = new Criterios_Rele_Elo(religadorPai, pontoRede, rede, pOrigem);
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
        AjusteRele curvaRapida = fase ? this.religadorPai.getAjusteRapidaFase() : this.religadorPai.getAjusteRapidaNeutro();
        BigDecimal correnteMin = this.getCorrenteMin(fase);
        BigDecimal correnteMax = this.getCorrenteMax(fase);
        BigDecimal tempoCorrenteMax = curvaRapida.calculaTempo(correnteMax);
        BigDecimal tempoCorrenteMin = curvaRapida.calculaTempo(correnteMin);

        ajustes.forEach(ajuste -> {
            BigDecimal tempoEloMax = BigDecimal.valueOf(ajuste.getElo().tempoDaCorrente(correnteMax.doubleValue(), CurvasElo.MINIMA));
            BigDecimal tempoEloMin = BigDecimal.valueOf(ajuste.getElo().tempoDaCorrente(correnteMin.doubleValue(), CurvasElo.MINIMA));
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
