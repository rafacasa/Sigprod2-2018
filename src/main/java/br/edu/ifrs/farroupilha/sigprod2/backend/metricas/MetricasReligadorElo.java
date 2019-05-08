package br.edu.ifrs.farroupilha.sigprod2.backend.metricas;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import java.math.BigDecimal;

/**
 *
 * @author Rafael Luiz Casa
 */
public class MetricasReligadorElo extends MetricasReleElo {

    private boolean seletividadeRapidaFase;
    private boolean seletividadeRapidaNeutro;

    public MetricasReligadorElo(BigDecimal alcance, boolean seletividade, boolean seletividadeRapida, Elo elo) {
        super(alcance, seletividade, elo);
        this.seletividadeRapidaFase = seletividadeRapida;
    }

    public MetricasReligadorElo(MetricasReleElo releElo) {
        super(releElo);
        this.seletividadeRapidaFase = true;
        this.seletividadeRapidaNeutro = true;
    }

    @Override
    public boolean isSeletividade() {
        return ((super.isSeletividade()) && this.isSeletividadeRapida());
    }

    public boolean isSeletividadeRapida() {
        return seletividadeRapidaFase && this.seletividadeRapidaNeutro;
    }

    public void setSeletividadeRapida(boolean seletividadeRapida, boolean fase) {
        if (fase) {
            this.seletividadeRapidaFase = seletividadeRapida;
        } else {
            this.seletividadeRapidaNeutro = seletividadeRapida;
        }
    }

    public boolean isSeletividadeRapidaFase() {
        return seletividadeRapidaFase;
    }

    public void setSeletividadeRapidaFase(boolean seletividadeRapidaFase) {
        this.seletividadeRapidaFase = seletividadeRapidaFase;
    }

    public boolean isSeletividadeRapidaNeutro() {
        return seletividadeRapidaNeutro;
    }

    public void setSeletividadeRapidaNeutro(boolean seletividadeRapidaNeutro) {
        this.seletividadeRapidaNeutro = seletividadeRapidaNeutro;
    }
}
