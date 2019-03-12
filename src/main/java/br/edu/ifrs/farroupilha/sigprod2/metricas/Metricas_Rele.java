package br.edu.ifrs.farroupilha.sigprod2.metricas;

import br.edu.ifrs.farroupilha.sigprod2.modelo.CurvaRele;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Metricas_Rele implements Comparable<Metricas_Rele> {

    double fm;
    double at;
    double ac;
    CurvaRele curva;

    public Metricas_Rele(double fm, double at, double ac, CurvaRele curva) {
        this.fm = fm;
        this.at = at;
        this.ac = ac;
        this.curva = curva;
    }

    public double getFm() {
        return fm;
    }

    public double getAt() {
        return at;
    }

    public double getAc() {
        return ac;
    }

    public CurvaRele getCurva() {
        return curva;
    }

    @Override
    public int compareTo(Metricas_Rele o) {
        return Double.compare(this.fm, o.fm);
    }

}
