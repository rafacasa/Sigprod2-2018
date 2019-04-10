package br.edu.ifrs.farroupilha.sigprod2.backend.metricas;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import java.math.BigDecimal;

/**
 *
 * @author Rafael Luiz Casa
 */
public class MetricasReligadorElo extends MetricasReleElo {

    private boolean seletividadeRapida;

    public MetricasReligadorElo(BigDecimal alcance, boolean seletividade, boolean seletividadeRapida, Elo elo) {
        super(alcance, seletividade, elo);
        this.seletividadeRapida = seletividadeRapida;
    }

    public MetricasReligadorElo(MetricasReleElo releElo) {
        super(releElo);
        this.seletividadeRapida = true;
    }

    @Override
    public boolean isSeletividade() {
        return ((super.isSeletividade()) && this.seletividadeRapida);
    }

    public boolean isSeletividadeRapida() {
        return seletividadeRapida;
    }

    public void setSeletividadeRapida(boolean seletividadeRapida) {
        this.seletividadeRapida = seletividadeRapida;
    }
}
