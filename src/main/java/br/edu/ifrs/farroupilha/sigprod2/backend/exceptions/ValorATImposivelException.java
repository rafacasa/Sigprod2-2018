package br.edu.ifrs.farroupilha.sigprod2.backend.exceptions;

/**
 *
 * @author Rafael Luiz Casa
 */
public class ValorATImposivelException extends Exception {

    /**
     * Creates a new instance of <code>ValorATImposivelException</code> without
     * detail message.
     */
    public ValorATImposivelException() {
    }

    /**
     * Constructs an instance of <code>ValorATImposivelException</code> with the
     * specified detail message.
     *
     * @param msg the detail message.
     */
    public ValorATImposivelException(String msg) {
        super(msg);
    }
}
