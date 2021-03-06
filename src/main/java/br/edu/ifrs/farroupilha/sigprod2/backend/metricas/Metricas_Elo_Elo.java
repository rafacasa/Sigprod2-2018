package br.edu.ifrs.farroupilha.sigprod2.backend.metricas;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;

/**
 *
 * @author Rafael Casa
 */
public class Metricas_Elo_Elo {
    private double tProtetorProtegido1, tProtetorProtegido2, iFTMinI300, iFTMinSelI300, porcentagemProtegida;
    private Elo elo;
    private Ponto ponto;

    public Metricas_Elo_Elo(double tProtetorProtegido1, double tProtetorProtegido2, double iFTMinI300, double iFTMinSelI300, double porcentagemProtegida, Elo elo, Ponto ponto) {
        this.tProtetorProtegido1 = tProtetorProtegido1;
        this.tProtetorProtegido2 = tProtetorProtegido2;
        this.iFTMinI300 = iFTMinI300;
        this.iFTMinSelI300 = iFTMinSelI300;
        this.porcentagemProtegida = porcentagemProtegida;
        this.elo = elo;
        this.ponto = ponto;
    }

    public Metricas_Elo_Elo(Elo elo, Ponto ponto) {
        this.elo = elo;
        this.ponto = ponto;
        this.tProtetorProtegido1 = 0;
        this.tProtetorProtegido2 = 0;
        this.iFTMinI300 = 0;
        this.iFTMinSelI300 = 0;
        this.porcentagemProtegida = 0;
    }
    
    public double gettProtetorProtegido1() {
        return tProtetorProtegido1;
    }

    public void settProtetorProtegido1(double tProtetorProtegido1) {
        this.tProtetorProtegido1 = tProtetorProtegido1;
    }

    public double gettProtetorProtegido2() {
        return tProtetorProtegido2;
    }

    public void settProtetorProtegido2(double tProtetorProtegido2) {
        this.tProtetorProtegido2 = tProtetorProtegido2;
    }

    public double getiFTMinI300() {
        return iFTMinI300;
    }

    public void setiFTMinI300(double iFTMinI300) {
        this.iFTMinI300 = iFTMinI300;
    }

    public double getiFTMinSelI300() {
        return iFTMinSelI300;
    }

    public void setiFTMinSelI300(double iFTMinSelI300) {
        this.iFTMinSelI300 = iFTMinSelI300;
    }

    public double getPorcentagemProtegida() {
        return porcentagemProtegida;
    }

    public void setPorcentagemProtegida(double porcentagemProtegida) {
        this.porcentagemProtegida = porcentagemProtegida;
    }

    public Elo getElo() {
        return elo;
    }

    public Ponto getPonto() {
        return ponto;
    }

    @Override
    public String toString() {
        return "ELO " + this.elo.getCorrenteNominal();
    }
}
