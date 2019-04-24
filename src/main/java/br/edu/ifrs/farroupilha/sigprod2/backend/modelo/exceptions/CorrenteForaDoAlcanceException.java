package br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions;

/**
 *
 * @author Rafael Luiz Casa
 */
public class CorrenteForaDoAlcanceException extends Exception {

    boolean correnteMaiorQueElo;

    public CorrenteForaDoAlcanceException() {
    }

    public CorrenteForaDoAlcanceException(boolean correnteMaiorQueElo) {
        this.correnteMaiorQueElo = correnteMaiorQueElo;
    }

    public CorrenteForaDoAlcanceException(boolean correnteMaiorQueElo, String message) {
        super(message);
        this.correnteMaiorQueElo = correnteMaiorQueElo;
    }

    public CorrenteForaDoAlcanceException(String message) {
        super(message);
    }

    public boolean isCorrenteMaiorQueElo() {
        return correnteMaiorQueElo;
    }
}
