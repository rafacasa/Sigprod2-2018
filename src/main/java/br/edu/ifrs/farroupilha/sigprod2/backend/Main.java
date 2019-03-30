package br.edu.ifrs.farroupilha.sigprod2.backend;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Arquivo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.BancoDeDadosException;
import br.edu.ifrs.farroupilha.sigprod2.frontend.frames.MainFrame;
import br.edu.ifrs.farroupilha.sigprod2.frontend.frames.RelativeMainFrame;
import java.awt.Component;
import java.util.logging.Level;
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

    private static void setupMainFrame(MainFrame frame) throws BancoDeDadosException {
        Rede rede = getRedeInicial();
        Component c = (Component) rede.getMapaView();
        frame.setMapa(c);
    }

//    private static Rede getRede(String caminhoArquivo) {
//        
//    }
    public static void main(String[] args) {
        try {
            int guiType = getGuiType();
            switch (guiType) {
                case DEFAULT_GUI:
                    LOGGER.error("AINDA NÃO DESENVOLVIDO");
                    break;
                case RELATIVE_GUI:
                    MainFrame frame = new RelativeMainFrame();
                    setupMainFrame(frame);
                    break;
                default:
                    LOGGER.error("OPÇÃO INVÁLIDA DE GUI TYPE");
                    break;
            }
        } catch (BancoDeDadosException ex) {
            LOGGER.error("ERRO DE BANCO DE DADOS" + ex.getMessage());
        }
    }
}
