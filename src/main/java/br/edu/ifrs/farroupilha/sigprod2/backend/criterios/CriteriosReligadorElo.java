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
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.CorrenteForaDoAlcanceException;
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
    private final Religador religadorPai;
    private final Ponto pontoRede;
    private final Rede rede;
    private final Criterios_Rele_Elo criteriosReleElo;
    private final List<BigDecimal> fatorK;

    public CriteriosReligadorElo(Religador religadorPai, Ponto pontoRede, Rede rede) {
        this.fatorK = new ArrayList<>();
        this.fatorK.add(BigDecimal.valueOf(1.25));
        this.fatorK.add(BigDecimal.valueOf(1.25));
        this.fatorK.add(BigDecimal.valueOf(1.35));
        this.fatorK.add(BigDecimal.valueOf(1.8));
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
        BigDecimal fatorKAtual = this.getFatorK();
        BigDecimal tempoCorrenteMax = curvaRapida.calculaTempo(correnteMax).multiply(fatorKAtual);
        BigDecimal tempoCorrenteMin = curvaRapida.calculaTempo(correnteMin).multiply(fatorKAtual);

        LOGGER.debug("RELE ");
        LOGGER.debug("tempoCorrenteMax = " + tempoCorrenteMax);
        LOGGER.debug("tempoCorrenteMin = " + tempoCorrenteMin);

        ajustes.forEach(ajuste -> {
            try {
                //if (ajuste.isSeletividade()) {
                double tempoEloMaxd = ajuste.getElo().tempoDaCorrente(correnteMax.doubleValue(), CurvasElo.MINIMA);
                double tempoEloMind = ajuste.getElo().tempoDaCorrente(correnteMin.doubleValue(), CurvasElo.MINIMA);
                LOGGER.debug("ELO " + ajuste.getElo().getCorrenteNominal());
                LOGGER.debug("tempoEloMin = " + tempoEloMind);
                LOGGER.debug("tempoEloMax = " + tempoEloMaxd);
                BigDecimal tempoEloMax = BigDecimal.valueOf(tempoEloMaxd);
                BigDecimal tempoEloMin = BigDecimal.valueOf(tempoEloMind);
                ajuste.setSeletividadeRapida((tempoCorrenteMax.compareTo(tempoEloMax) < 0) && (tempoCorrenteMin.compareTo(tempoEloMin) < 0), fase);
                //}
            } catch (CorrenteForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE PARA AS CORRENTES NECESSARIAS (VERIFICACURVARAPIDAELO)" + ex.getLocalizedMessage());
                ajuste.setSeletividadeRapida(false, fase);
            }
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

    private BigDecimal getFatorK() {
        //PEGAR INFORMACOES SOBRE TEMPO DE RELIGAMENTO E QTD OPERACOES RAPIDAS
        //1 OP RAPIDA e tempo > 0,5s e < 5,0s .get(0)
        //1 OP RAPIDA e tempo < 0,5s .get(1)
        //2 OP RAPIDA e tempo > 0,5s e < 5,0s .get(2)
        //2 OP RAPIDA e tempo < 0,5s .get(3)
        return this.fatorK.get(2);
    }
}
