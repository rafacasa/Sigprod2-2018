package sigprod2.auxiliar;

import java.awt.Component;
import javax.swing.JOptionPane;

/**
 * Esta classe tem métodos estáticos para apresentar mensagens de erro ao
 * usuário.
 *
 * @author Rafael Coelho
 * @version 16/08/2015
 */
public class Erro {

    private static void mostraMensagem(Component janela, String texto) {
        JOptionPane.showMessageDialog(janela,
                texto,
                "ERRO",
                JOptionPane.ERROR_MESSAGE);
    }

    public static void arquivoInexistente(Component janela) {
        mostraMensagem(janela, "Arquivo Inexistente");
    }

    public static void aberturaDeArquivo(Component janela) {
        mostraMensagem(janela, "Não foi possível abrir o arquivo");
    }

    public static void naoPermitidaLeitura(Component janela) {
        mostraMensagem(janela, "Não é permitido ler este Arquivo");
    }

    public static void correnteVazia(Component janela) {
        mostraMensagem(janela, "Digite algum valor para a Corrente Nominal");
    }

    public static void linhaNaoSelecionada(Component janela) {
        mostraMensagem(janela, "Linha não selecionada");
    }

    public static void mostraMensagemSQL(Component janela) {
        mostraMensagem(janela, "Erro de SQL no banco de dados.");
    }

    public static void correnteExistente(Component janela) {
        mostraMensagem(janela, "Corrente Nominal existente");
    }

    public static void mostraMensagemClasse(Component janela, Exception ex) {
        mostraMensagem(janela, "Erro de Classe no banco de dados.");
    }

    public static void entradaSomenteNumeros(Component janela) {
        mostraMensagem(janela, "Entrada somente com Números");
    }

    public static void entradaInvalida(Component janela) {
        mostraMensagem(janela, "Entrada inválida");
    }

    public static void configureBancoDados(Component janela) {
        mostraMensagem(janela, "Você deve configurar as informações de acesso ao Banco de Dados");
    }

    public static void valoresVazios(Component janela){
        mostraMensagem(janela,"Valores vazios");
	}

    public static void selecioneTipoRele(Component janela) {
        mostraMensagem(janela, "Você deve selecionar o tipo de Relé que deseja inserir");
    }

    public static void selecioneUnidadeRele(Component janela) {
        mostraMensagem(janela, "Você deve selecionar pelo menos uma unidade para o Relé");
    }

    public static void camposVazios(Component janela) {
        mostraMensagem(janela, "Campos vazios");
    }

    public static void valorCadastrado(Component janela) {
        mostraMensagem(janela, "Dial já cadastrado");
    }
    
    public static void expressaoInvalida(Component janela) {
        mostraMensagem(janela, "Expressão Inválida");
    }
    
    public static void ajusteInvalido(Component janela) {
        mostraMensagem(janela, "Não há equipamentos para ajustar esta rede");
    }
}
