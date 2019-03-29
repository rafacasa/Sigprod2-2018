package br.edu.ifrs.farroupilha.sigprod2.backend;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Arquivo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.BancoDeDadosException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Classe Principal do Sistema
 *
 * @author Rafael Luiz Casa
 */
public class Main {

    private static final Logger LOGGER = LogManager.getLogger(Main.class.getName());
    public static final int DEFAULT_GUI = 1;
    public static final int RELATIVE_GUI = 2;

    private static int getGuiType() { //ESTE METODO VERIFICARA QUAL TIPO DE GUI ESTA SALVA NAS CONFIGURACOES
        return RELATIVE_GUI;
    }

    private static Rede getRedeInicial() throws BancoDeDadosException {
        System.setProperty("org.graphstream.ui", "org.graphstream.ui.swing.util.Display");
        return new Rede(getArquivoRedeInicial());
    }

    private static Arquivo getArquivoRedeInicial() {
        return new Arquivo("redeRele.ABCEEE");
    }

//    private static Rede getRede(String caminhoArquivo) {
//        
//    }
    public static void main(String[] args) {
        int guiType = getGuiType();
        switch (guiType) {
            case DEFAULT_GUI:
                LOGGER.error("AINDA NÃO DESENVOLVIDO");
                break;
            case RELATIVE_GUI:

                break;
            default:
                LOGGER.error("OPÇÃO INVÁLIDA DE GUI TYPE");
                break;
        }
    }
}
