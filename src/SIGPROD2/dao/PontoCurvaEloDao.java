package sigprod2.dao;

import sigprod2.bd.Conexao;
import sigprod2.bd.tables.elo.PontoDeCurvaBD;
import sigprod2.modelo.Elo;
import sigprod2.modelo.PontoCurva;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Classe responsável por interagir com o Banco de Dados para inserir, alterar e
 * remover pontos de curva de um Elo.
 *
 * @author Rafael Casa
 * @version 06/04/2016
 */
public class PontoCurvaEloDao {

    private static final String INSERT = "INSERT INTO "
            + PontoDeCurvaBD.TABELA + " ("
            + PontoDeCurvaBD.CORRENTE + ", "
            + PontoDeCurvaBD.TEMPO + ", "
            + PontoDeCurvaBD.EH_CURVA_DE_MAXIMA + ", "
            + PontoDeCurvaBD.CORRENTE_DO_ELO + ") VALUES (?, ?, ?, ?)";
    private static final String VARIAVEIS_INSERT = ", (?, ?, ?, ?)";
    private static final String DELETE = "DELETE FROM " + PontoDeCurvaBD.TABELA + " WHERE " + PontoDeCurvaBD.CORRENTE_DO_ELO + " = ?;";
    private static final String BUSCAR = "SELECT " + PontoDeCurvaBD.ID + ", "
            + PontoDeCurvaBD.CORRENTE + ", "
            + PontoDeCurvaBD.TEMPO + " FROM "
            + PontoDeCurvaBD.TABELA + " WHERE ("
            + PontoDeCurvaBD.CORRENTE_DO_ELO + " = ? AND "
            + PontoDeCurvaBD.EH_CURVA_DE_MAXIMA + " = ?);";

    /**
     * Método responsável por inserir um Ponto de Curva no Banco de Dados
     *
     * @param ponto O Ponto a ser inserido
     * @param ehCurvaMaxima Informa em qual curva está este ponto. Utilize as
     * constantes SIGPRO2.Modelo.PontoCurva.PONTODACURVAMAXIMA e
     * SIGPRO2.Modelo.PontoCurva.PONTODACURVAMINIMA.
     * @param elo O Elo a que pertence este Ponto de Curva
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados, ou os
     * Dados forem inválidos
     */
    public static void inserePontoCurva(PontoCurva ponto, boolean ehCurvaMaxima, Elo elo) throws SQLException {
        Connection conexao = Conexao.getConexao();
        PreparedStatement comando = conexao.prepareStatement(INSERT);
        comando.setDouble(1, ponto.getCorrente());
        comando.setDouble(2, ponto.getTempo());
        comando.setBoolean(3, ehCurvaMaxima);
        comando.setInt(4, elo.getCorrenteNominal());
        comando.executeUpdate();
        Conexao.fechaConexao();
    }

    /**
     * Método responsável por gerenciar a inclusão de uma grande quantidade de
     * Pontos de Curva no Banco de Dados
     *
     * @param lista ArrayList com os pontos de curva a serem adicionados
     * @param ehCurvaMaxima Informa em qual curva está este ponto. Utilize as
     * constantes SIGPRO2.Modelo.PontoCurva.PONTODACURVAMAXIMA e
     * SIGPRO2.Modelo.PontoCurva.PONTODACURVAMINIMA.
     * @param correnteElo A Corrente Nominal do Elo em que será adicionado os
     * Pontos
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados, ou os
     * Dados forem inválidos
     */
    public static void inserePontoCurva(ArrayList<PontoCurva> lista, boolean ehCurvaMaxima, int correnteElo) throws SQLException {
        int qtd = lista.size();
        String comandoSql = INSERT;
        for (int i = 1; i < qtd; i++) {
            comandoSql += VARIAVEIS_INSERT;
        }
        Connection conexao = Conexao.getConexao();
        PreparedStatement comando = conexao.prepareStatement(comandoSql);

        for (int i = 0; i < qtd * 4; i += 4) {
            comando.setDouble(i + 1, lista.get(i / 4).getCorrente());
            comando.setDouble(i + 2, lista.get(i / 4).getTempo());
            comando.setBoolean(i + 3, ehCurvaMaxima);
            comando.setInt(i + 4, correnteElo);
        }

        comando.executeUpdate();
        Conexao.fechaConexao();
    }

    /**
     * Método responsável por deletar todos os pontos de curva do Elo informado.
     *
     * @param eloParaDeletar O Elo que será deletado
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados, ou os
     * Dados forem inválidos
     */
    public static void deletaPontosCurvaDoElo(Elo eloParaDeletar) throws SQLException {
        Connection conexao = Conexao.getConexao();
        PreparedStatement comando = conexao.prepareStatement(DELETE);
        comando.setInt(1, eloParaDeletar.getCorrenteNominal());
        comando.executeUpdate();
        Conexao.fechaConexao();
    }

    /**
     * Método responsável por recuperar todos os Pontos de Curva salvos no Banco
     * de Dados do Elo informado
     *
     * @param correnteNominal A Corrente Nominal do Elo
     * @param curva Informa de qual curva deverá ser buscado os Pontos. Utilize
     * as constantes SIGPRO2.Modelo.PontoCurva.PONTODACURVAMAXIMA e
     * SIGPRO2.Modelo.PontoCurva.PONTODACURVAMINIMA.
     * @return Um ArrayList com os Pontos de Curva localizados no Banco de Dados
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados, ou os
     * Dados forem inválidos
     */
    public static ArrayList<PontoCurva> buscaPontosCurva(int correnteNominal, boolean curva) throws SQLException {
        ArrayList<PontoCurva> lista = new ArrayList<>();
        Connection conexao = Conexao.getConexao();
        PreparedStatement comando = conexao.prepareStatement(BUSCAR);
        comando.setInt(1, correnteNominal);
        comando.setBoolean(2, curva);
        ResultSet resultado = comando.executeQuery();
        while (resultado.next()) {
            PontoCurva pontoCurva = new PontoCurva();
            pontoCurva.setId(resultado.getInt("Id"));
            pontoCurva.setCorrente(resultado.getDouble("corrente"));
            pontoCurva.setTempo(resultado.getDouble("tempo"));
            lista.add(pontoCurva);
        }
        Conexao.fechaConexao();
        return lista;
    }
}
