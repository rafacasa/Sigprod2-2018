package br.edu.ifrs.farroupilha.sigprod2.backend.bd;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.DadosConexaoException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Arquivo;
import com.google.gson.Gson;

/**
 * Classe responsável por organizar os dados necessários a uma conexao a Banco
 * de Dados
 *
 * @author Rafael Casa
 * @version 22/03/2016
 */
public class DadosConexao {

    public static final String ARQUIVO_CONFIGURACAO = "banco.ini";
    private String nomeBanco;
    private String ip;
    private String porta;
    private String usuario;
    private String senha;
    private static final Arquivo CONFIGURACOES = new Arquivo(DadosConexao.ARQUIVO_CONFIGURACAO);

    /**
     * Construtor que inicia os dados de uma conexçao a Banco de Dados.
     *
     * @param nomeBanco O nome do Banco.
     * @param ip o ip do servidor.
     * @param porta A porta do servidor.
     * @param usuario O usuário do servidor.
     * @param senha A senha do servidor;
     */
    public DadosConexao(String nomeBanco, String ip, String porta, String usuario, String senha) {
        this.nomeBanco = nomeBanco;
        this.ip = ip;
        this.porta = porta;
        this.usuario = usuario;
        this.senha = senha;
    }

    /**
     * Método responsável por dizer se o ip da conexão é um ip válido ou não.
     *
     * @return true se o ip é valido; false se o ip não é valido.
     */
    public boolean validaIp() {
        if (this.ip.isEmpty()) {
            return true;
        }
        if (this.ip.length() < 7 || this.ip.length() > 15) {
            return false;
        }
        String[] partes = this.ip.split("\\.");

        if (partes.length != 4) {
            return false;
        }
        for (int i = 0; i < 4; i++) {
            try {
                int parte = Integer.parseInt(partes[i]);

                if (parte > 255 || parte < 0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Método responsável por validar a porta da conexão.
     *
     * @return true se o ip é valido; false se o ip não é valido.
     */
    public boolean validaPorta() {
        if (this.porta.isEmpty()) {
            return true;
        }
        try {
            Integer.parseInt(this.porta);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Método responsável em transformar um objeto dessa classe em uma String
     * JSon para gravar em arquivo.
     *
     * @return A String para gravar em arquivo.
     * @see com.google.gson.Gson
     */
    public String getJson() {
        Gson json = new Gson();
        return json.toJson(this);
    }

    /**
     * Método responsável por fazer todos os procedimentos necessários para
     * salvar os dados de conexão ao Banco de Dados em um arquivo, de maneira
     * que possa ser recuperado para uso posterior por meio do método estático
     * {@link #getDadosConexaoSalvos()}
     *
     * @throws SIGPROD2.BD.DadosConexaoException Caso o ip e/ou porta forem
     * inválidos.
     * @see #getDadosConexaoSalvos()
     */
    public void salvar() throws DadosConexaoException {
        if (this.validaIp() && this.validaPorta()) {
            String salvar = this.getJson();
            CONFIGURACOES.escreverArquivo(salvar);
        } else {
            throw new DadosConexaoException("Ip e/ou porta inválidos");
        }
    }

    /**
     * Método responsável por recuperar do arquivo os dados de conexão que foram
     * salvos anteriormente.
     *
     * @return Instância da classe {@link DadosConexao} com os dados de conexão
     * salvos.
     * @throws SIGPROD2.BD.DadosConexaoException Caso não tenha sido configurado
     * as informações de acesso ao Banco de Dados.
     */
    public static DadosConexao getDadosConexaoSalvos() throws DadosConexaoException {
        if (CONFIGURACOES.existeArquivo()) {
            String fromArquivo = CONFIGURACOES.lerArquivo();

            Gson json = new Gson();
            DadosConexao dados = json.fromJson(fromArquivo, DadosConexao.class);
            return dados;
        }
        throw new DadosConexaoException("CONFIGURE AS INFORMAÇÕES DE ACESSO AO BANCO DE DADOS");
    }

    /**
     * Método responsável por entregar ao construtor de Connections a String de
     * conexão JBDC para mySql.
     *
     * @return A String de conexão JBDC para mySql.
     */
    public String getStringJBDC() {
        return "jdbc:mysql://" + this.ip + ":" + this.porta + "/" + this.nomeBanco;
    }

    /**
     * Informa o usuário a ser utilizado no Banco de Dados.
     *
     * @return O usuário a ser utilizado no Banco de Dados.
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * Informa a senha a ser utilizada no Banco de Dados.
     *
     * @return A senha a ser utilizada no Banco de Dados.
     */
    public String getSenha() {
        return senha;
    }

    /**
     * Informa o nome do Banco.
     *
     * @return O nome do Banco.
     */
    public String getNomeBanco() {
        return nomeBanco;
    }

    /**
     * Informa o ip do servidor de Banco de Dados.
     *
     * @return o ip do servidor de Banco de Dados.
     */
    public String getIp() {
        return ip;
    }

    /**
     * Informa a porta do servidor de Banco de Dados.
     *
     * @return a porta do servidor de Banco de Dados.
     */
    public String getPorta() {
        return porta;
    }
}
