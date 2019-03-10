package br.edu.ifrs.farroupilha.sigprod2.bd;

import br.edu.ifrs.farroupilha.sigprod2.auxiliar.Erro;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Esta clase gerencia a criação, o armazenamento e o fechamento de conexões com
 * o banco de dados.
 *
 * @author Adrian Borba
 * @author Rafael Casa
 * @version 22/03/2016
 */
public class Conexao {

    private static Connection conexao;

    /**
     * Método que fecha a conexao armazenada, caso haja uma. Caso não tenha
     * nenhuma conexão armazenada, nada será executado
     *
     * @return true caso o processo tenha sucesso
     */
    public static boolean fechaConexao() {
        try {
            if (conexao != null) {
                conexao.close();
                conexao = null;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Falha ao fechar a conexão com o banco de dados");
        }
        return false;
    }

    /**
     * Método que cria uma conexão nova
     *
     * @return A conexão criada
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados
     */
    private static Connection abreConexao() throws SQLException {
        try {
            DadosConexao dados = DadosConexao.getDadosConexaoSalvos();
            String jbdc = dados.getStringJBDC();
            String usuario = dados.getUsuario();
            String senha = dados.getSenha();
            conexao = DriverManager.getConnection(jbdc, usuario, senha);
            return conexao;
        } catch (DadosConexaoException ex) {
            Erro.configureBancoDados(null);
        }
        return null;
    }

    /**
     * Método que retorna uma conexão com o banco de dados para ser usada. Esta
     * conexão deve ser fechada pelo método {@link #fechaConexao() }
     *
     * @return A conexão com o banco de dados
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados
     */
    public static Connection getConexao() throws SQLException {
        if (conexao == null) {
            conexao = abreConexao();
        }
        return conexao;
    }
}
