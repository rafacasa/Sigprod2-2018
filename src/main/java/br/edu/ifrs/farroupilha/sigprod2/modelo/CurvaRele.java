package br.edu.ifrs.farroupilha.sigprod2.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por armazenar as informações da curva de um Relé
 *
 * @see br.edu.ifrs.farroupilha.sigprod2.modelo.Rele
 * @author Rafael Luiz Casa
 */
public class CurvaRele {

    private double a;
    private double b;
    private double p;
    private double menorAC;
    private double maiorAC;
    private double passoAC;
    private double menorAT;
    private double maiorAT;
    private double passoAT;

    public CurvaRele(double a, double b, double p, double menorAC, double maiorAC, double passoAC, double menorAT, double maiorAT, double passoAT) {
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

    public double getA() {
        return a;
    }

    public void setA(double a) {
        this.a = a;
    }

    public double getB() {
        return b;
    }

    public void setB(double b) {
        this.b = b;
    }

    public double getP() {
        return p;
    }

    public void setP(double p) {
        this.p = p;
    }

    public double getMenorAC() {
        return menorAC;
    }

    public void setMenorAC(double menorAC) {
        this.menorAC = menorAC;
    }

    public double getMaiorAC() {
        return maiorAC;
    }

    public void setMaiorAC(double maiorAC) {
        this.maiorAC = maiorAC;
    }

    public double getPassoAC() {
        return passoAC;
    }

    public void setPassoAC(double passoAC) {
        this.passoAC = passoAC;
    }

    public double getMenorAT() {
        return menorAT;
    }

    public void setMenorAT(double menorAT) {
        this.menorAT = menorAT;
    }

    public double getMaiorAT() {
        return maiorAT;
    }

    public void setMaiorAT(double maiorAT) {
        this.maiorAT = maiorAT;
    }

    public double getPassoAT() {
        return passoAT;
    }

    public void setPassoAT(double passoAT) {
        this.passoAT = passoAT;
    }

    public List<Double> gerarAT() {
        List<Double> tempos = new ArrayList<>();
        for (double i = this.menorAT; i <= this.maiorAT; i += this.passoAT) {
            tempos.add(i);
        }
        return tempos;
    }

    public List<Double> gerarAC() {
        List<Double> correntes = new ArrayList<>();
        for (double i = this.menorAC; i <= this.maiorAC; i += this.passoAC) {
            correntes.add(i);
        }
        return correntes;
    }
}
