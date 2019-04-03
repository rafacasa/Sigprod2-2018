package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por armazenar as informações da curva de um Relé
 *
 * @see br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele
 * @author Rafael Luiz Casa
 */
public class CurvaRele {

    private BigDecimal a;
    private BigDecimal b;
    private BigDecimal p;
    private BigDecimal menorAC;
    private BigDecimal maiorAC;
    private BigDecimal passoAC;
    private BigDecimal menorAT;
    private BigDecimal maiorAT;
    private BigDecimal passoAT;

    public CurvaRele(BigDecimal a, BigDecimal b, BigDecimal p, BigDecimal menorAC, BigDecimal maiorAC, BigDecimal passoAC, BigDecimal menorAT, BigDecimal maiorAT, BigDecimal passoAT) {
        this.a = a;
        this.b = b;
        this.p = p;
        this.menorAC = menorAC;
        this.maiorAC = maiorAC;
        this.passoAC = passoAC;
        this.menorAT = menorAT;
        this.maiorAT = maiorAT;
        this.passoAT = passoAT;
    }

    public CurvaRele(String a, String b, String p, String menorAC, String maiorAC, String passoAC, String menorAT, String maiorAT, String passoAT) {
        this.a = new BigDecimal(a);
        this.b = new BigDecimal(b);
        this.p = new BigDecimal(p);
        this.menorAC = new BigDecimal(menorAC);
        this.maiorAC = new BigDecimal(maiorAC);
        this.passoAC = new BigDecimal(passoAC);
        this.menorAT = new BigDecimal(menorAT);
        this.maiorAT = new BigDecimal(maiorAT);
        this.passoAT = new BigDecimal(passoAT);
    }

    public BigDecimal getA() {
        return a;
    }

    public void setA(BigDecimal a) {
        this.a = a;
    }

    public BigDecimal getB() {
        return b;
    }

    public void setB(BigDecimal b) {
        this.b = b;
    }

    public BigDecimal getP() {
        return p;
    }

    public void setP(BigDecimal p) {
        this.p = p;
    }

    public BigDecimal getMenorAC() {
        return menorAC;
    }

    public void setMenorAC(BigDecimal menorAC) {
        this.menorAC = menorAC;
    }

    public BigDecimal getMaiorAC() {
        return maiorAC;
    }

    public void setMaiorAC(BigDecimal maiorAC) {
        this.maiorAC = maiorAC;
    }

    public BigDecimal getPassoAC() {
        return passoAC;
    }

    public void setPassoAC(BigDecimal passoAC) {
        this.passoAC = passoAC;
    }

    public BigDecimal getMenorAT() {
        return menorAT;
    }

    public void setMenorAT(BigDecimal menorAT) {
        this.menorAT = menorAT;
    }

    public BigDecimal getMaiorAT() {
        return maiorAT;
    }

    public void setMaiorAT(BigDecimal maiorAT) {
        this.maiorAT = maiorAT;
    }

    public BigDecimal getPassoAT() {
        return passoAT;
    }

    public void setPassoAT(BigDecimal passoAT) {
        this.passoAT = passoAT;
    }

    public List<BigDecimal> gerarAT() {
        List<BigDecimal> tempos = new ArrayList<>();
        for (BigDecimal i = this.menorAT; i.compareTo(this.maiorAT) <= 0; i = i.add(this.passoAT)) {
            tempos.add(i);
        }
        return tempos;
    }

    public List<BigDecimal> gerarAC() {
        List<BigDecimal> correntes = new ArrayList<>();
        for (BigDecimal i = this.menorAC; i.compareTo(this.maiorAC) <= 0; i = i.add(this.passoAC)) {
            correntes.add(i);
        }
        return correntes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof CurvaRele) {
            CurvaRele c = (CurvaRele) obj;
            return this.a.equals(c.a) && this.b.equals(c.b) && this.p.equals(c.p) && this.menorAC.equals(c.menorAC) && this.maiorAC.equals(c.maiorAC) && this.passoAC.equals(c.passoAC) && this.menorAT.equals(c.menorAT) && this.maiorAT.equals(c.maiorAT) && this.passoAT.equals(c.passoAT);
        } else {
            return false;
        }
    }
}
