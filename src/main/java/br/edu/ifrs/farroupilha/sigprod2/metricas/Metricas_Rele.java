package br.edu.ifrs.farroupilha.sigprod2.metricas;

import br.edu.ifrs.farroupilha.sigprod2.modelo.CurvaRele;
import java.math.BigDecimal;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Metricas_Rele implements Comparable<Metricas_Rele> {

    private BigDecimal fm;
    private BigDecimal at;
    private BigDecimal ac;
    private CurvaRele curva;

    public Metricas_Rele(BigDecimal fm, BigDecimal at, BigDecimal ac, CurvaRele curva) {
        this.fm = fm;
        this.at = at;
        this.ac = ac;
        this.curva = curva;
    }

    public BigDecimal getFm() {
        return fm;
    }

    public BigDecimal getAt() {
        return at;
    }

    public BigDecimal getAc() {
        return ac;
    }

    public CurvaRele getCurva() {
        return curva;
    }

    @Override
    public int compareTo(Metricas_Rele o) {
        return this.fm.compareTo(o.fm);
    }

}
