package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

/**
 * Esta classe representa um Ponto de Curva de um ELO
 *
 * @author Coelho
 * @version 16/08/2015
 */
public class PontoCurva {

    public static final boolean PONTO_DA_CURVA_MAXIMA = true;
    public static final boolean PONTO_DA_CURVA_MINIMA = false;
    private double corrente;
    private double tempo;
    private int id;

    public PontoCurva(double corrente, double tempo, int id) {
        this.corrente = corrente;
        this.tempo = tempo;
        this.id = id;
    }

    public PontoCurva() {
    }

    public PontoCurva(PontoCurva p) {
        this.corrente = p.corrente;
        this.tempo = p.tempo;
        this.id = p.id;
    }

    public PontoCurva(double corrente, double tempo) {
        this.corrente = corrente;
        this.tempo = tempo;
    }

    public double getCorrente() {
        return this.corrente;
    }

    public void setCorrente(double corrente) {
        this.corrente = corrente;
    }

    public double getTempo() {
        return this.tempo;
    }

    public void setTempo(double tempo) {
        this.tempo = tempo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double[] getPontoArray() {
        double[] array = new double[2];
        array[0] = this.corrente;
        array[1] = this.tempo;
        return array;
    }
}
