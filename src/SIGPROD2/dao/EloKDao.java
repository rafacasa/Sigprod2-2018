package sigprod2.dao;

import sigprod2.bd.Conexao;
import sigprod2.bd.tables.elo.EloBD;
import sigprod2.modelo.Elo;
import sigprod2.modelo.PontoCurva;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe responsável por interagir com o Banco de Dados para inserir, alterar e
 * remover Elos Tipo K
 *
 * @author Rafael Casa
 * @version 25/03/2016
 */
public class EloKDao {

    public static final String INSERT = "INSERT INTO " + EloBD.TABELA + "(" + EloBD.CORRENTE_NOMINAL + ", " + EloBD.PREFERENCIAL + ") VALUES (?, ?);";
    public static final String DELETE = "DELETE FROM " + EloBD.TABELA + " WHERE " + EloBD.CORRENTE_NOMINAL + " = ?;";
    public static final String BUSCAR = "SELECT * FROM " + EloBD.TABELA;

    /**
     * Método para inserir um ELO Tipo K no Banco de Dados
     *
     * @param eloParaInserir As informações do EloBD a ser Inserido
     * @throws java.sql.SQLException Caso houver erro de acesso ao Banco de
     * Dados, ou os Dados forem inválidos
     */
    public static void insereEloK(Elo eloParaInserir) throws SQLException {
        inserirEloK(eloParaInserir);

        PontoCurvaEloDao.inserePontoCurva(eloParaInserir.getCurvaDeMinimaFusao(),
                PontoCurva.PONTO_DA_CURVA_MINIMA,
                eloParaInserir.getCorrenteNominal());

        PontoCurvaEloDao.inserePontoCurva(eloParaInserir.getCurvaDeMaximaInterrupcao(),
                PontoCurva.PONTO_DA_CURVA_MAXIMA,
                eloParaInserir.getCorrenteNominal());
    }

    /**
     * Método responsável por inserir as informações de um EloBD tipo K na
     * respectiva tabela do Banco de Dados
     *
     * @param eloParaInserir O elo a ser inserido
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados, ou os
     * Dados forem inválidos
     */
    private static void inserirEloK(Elo eloParaInserir) throws SQLException {
        Connection conexao = Conexao.getConexao();
        PreparedStatement comando = conexao.prepareStatement(INSERT);
        comando.setInt(1, eloParaInserir.getCorrenteNominal());
        comando.setBoolean(2, eloParaInserir.isPreferencial());
        comando.executeUpdate();
        Conexao.fechaConexao();
    }

    /**
     * Método para deletar um ELO Tipo K no Banco de Dados
     *
     * @param eloParaDeletar As informações do EloBD a ser deletado
     * @throws java.sql.SQLException Caso houver erro de acesso ao Banco de
     * Dados, ou os Dados forem inválidos
     */
    public static void deletaEloK(Elo eloParaDeletar) throws SQLException {
        PontoCurvaEloDao.deletaPontosCurvaDoElo(eloParaDeletar);
        Connection conexao = Conexao.getConexao();
        try (PreparedStatement comando = conexao.prepareStatement(DELETE)) {
            comando.setInt(1, eloParaDeletar.getCorrenteNominal());
            comando.executeUpdate();
        }
        Conexao.fechaConexao();
    }

    /**
     * Método que busca no banco de dados as informações básicas dos Elos
     * cadastrados(corrente nominal e preferencial)
     *
     * @return ArrayList com os elos encontrados
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados
     */
    public static List<Elo> buscarCorrentes() throws SQLException {
        ArrayList<Elo> lista = new ArrayList<>();
        Connection conexao = Conexao.getConexao();
        PreparedStatement comando = conexao.prepareStatement(BUSCAR);
        ResultSet resultado = comando.executeQuery();
        while (resultado.next()) {
            Elo elo = new Elo();
            elo.setCorrenteNominal(resultado.getInt("correnteNominal"));
            elo.setPreferencial(resultado.getBoolean("preferencial"));
            lista.add(elo);
        }
        Conexao.fechaConexao();
        return lista;
    }

    /**
     * Método que carrega no elo todos os pontos de curva salvos no Banco de
     * Dados
     *
     * @param elo o elo (contendo corrente nominal e preferencial) a ser
     * carregado com os pontos de curva
     * @return O EloBD já carregado com os pontos de curva
     * @throws SQLException Caso houver erro de acesso ao Banco de Dados
     */
    public static Elo buscarEloK(Elo elo) throws SQLException {
        elo.setCurvaDeMinimaFusao(PontoCurvaEloDao.buscaPontosCurva(elo.getCorrenteNominal(), PontoCurva.PONTO_DA_CURVA_MINIMA));
        elo.setCurvaDeMaximaInterrupcao(PontoCurvaEloDao.buscaPontosCurva(elo.getCorrenteNominal(), PontoCurva.PONTO_DA_CURVA_MAXIMA));
        return elo;
    }
    
    public static List<Elo> buscarElos() throws SQLException {
        List<Elo> elos = buscarCorrentes();
        List<Elo> retorno = new ArrayList<>();
        
        for (Elo elo : elos) {
            retorno.add(EloKDao.buscarEloK(elo));
        }
        
        return retorno;
    }
}