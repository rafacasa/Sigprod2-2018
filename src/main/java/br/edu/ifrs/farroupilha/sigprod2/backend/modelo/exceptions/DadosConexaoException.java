package br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions;

/**
 * Classe que representa uma excessão que acontece quando o usuário não
 * configurou anteriormente a conexão ao Banco de Dados, ou inseriu dados
 * inválidos.
 *
 * @author Rafael Casa
 * @version 23/03/2016
 */
public class DadosConexaoException extends Exception {

    public DadosConexaoException() {
        super();
    }

    public DadosConexaoException(String message) {
        super(message);
    }

}
