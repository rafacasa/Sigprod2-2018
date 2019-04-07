package br.edu.ifrs.farroupilha.sigprod2.backend.metricas;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import java.math.BigDecimal;

/**
 *
 * @author Rafael Luiz Casa
 */
public class MetricasReleElo {

    private BigDecimal alcance;
    private boolean seletividade;
    private Elo elo;

    public MetricasReleElo(BigDecimal alcance, boolean seletividade, Elo elo) {
        this.alcance = alcance;
        this.seletividade = seletividade;
        this.elo = elo;
    }

    public MetricasReleElo(MetricasReleElo metricas) {
        this.alcance = metricas.alcance;
        this.seletividade = metricas.seletividade;
        this.elo = metricas.elo;
    }

    public BigDecimal getAlcance() {
        return alcance;
    }

    public void setAlcance(BigDecimal alcance) {
        this.alcance = alcance;
    }

    public boolean isSeletividade() {
        return seletividade;
    }

    public void setSeletividade(boolean seletividade) {
        this.seletividade = seletividade;
    }

    public Elo getElo() {
        return elo;
    }

    public void setElo(Elo elo) {
        this.elo = elo;
    }

    @Override
    public String toString() {
        return "ELO " + this.elo.getCorrenteNominal();
    }
}
