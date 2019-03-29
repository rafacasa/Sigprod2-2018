package br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions;

/**
 *
 * @author Rafael Luiz Casa
 */
public class BancoDeDadosException extends Exception {

    public BancoDeDadosException() {
    }

    public BancoDeDadosException(String message) {
        super(message);
    }

    public BancoDeDadosException(String message, Throwable cause) {
        super(message, cause);
    }

    public BancoDeDadosException(Throwable cause) {
        super(cause);
    }
}
