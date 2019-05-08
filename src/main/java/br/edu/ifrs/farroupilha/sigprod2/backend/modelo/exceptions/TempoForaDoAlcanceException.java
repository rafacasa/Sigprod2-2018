package br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions;

/**
 *
 * @author Rafael Luiz Casa
 */
public class TempoForaDoAlcanceException extends Exception {

    boolean tempoMaiorQueElo;

    public TempoForaDoAlcanceException() {
    }

    public TempoForaDoAlcanceException(boolean tempoMaiorQueElo) {
        this.tempoMaiorQueElo = tempoMaiorQueElo;
    }

    public TempoForaDoAlcanceException(boolean tempoMaiorQueElo, String message) {
        super(message);
        this.tempoMaiorQueElo = tempoMaiorQueElo;
    }

    public TempoForaDoAlcanceException(String message) {
        super(message);
    }

    public boolean isTempoMaiorQueElo() {
        return tempoMaiorQueElo;
    }
}
