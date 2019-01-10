package SIGPROD2.gui;

import SIGPROD2.gui.ajustepanels.PanelAjusteEloElo;
import SIGPROD2.gui.ajustepanels.PanelNavegacao;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import sigprod2.modelo.Ponto;
import sigprod2.modelo.Rede;
import sigprod2.modelo.TipoEquipamento;

/**
 *
 * @author Rafael Casa
 */
public class AjusteFrame extends JDialog{
    
    private PanelNavegacao navegacao;
    private PanelAjusteEloElo ajusteEloElo;
    private sigprod2.gui.MainFrame mainFrame;
    private Rede rede;

    public AjusteFrame(sigprod2.gui.MainFrame frame) {
        super(frame);
        this.mainFrame = frame;
        this.rede = this.mainFrame.getRede();
        initComponents();
    }
    
    private void initComponents() {
        this.navegacao = new PanelNavegacao(this.rede, this);
        this.add(this.navegacao);
        this.navegacao.atualizarPontoAtual();
        this.ajustar(this.navegacao.getPontoAtual(), true);
    }
    
    private void limparPanel() {
        
    }
    
    public boolean ajustar(Ponto ponto, boolean inicioRede) {
        this.limparPanel();
        
        Ponto pOrigem = this.rede.getParentRedeReduzida(ponto);
        TipoEquipamento equip = ponto.getTipoEquipamentoInstalado();
        
        
        if (inicioRede) {
                switch (equip) {
                    case ELO:
                        
                        break;
                    case RELE:

                        break;
                    case RELIGADOR:

                        break;
                }
            } else {
                switch (equip) {
                    case ELO:
                        switch (pOrigem.getTipoEquipamentoInstalado()) {
                            case ELO:
                                new PanelAjusteEloElo();
                                break;
                            case RELE:

                                break;
                            case RELIGADOR:

                                break;
                        }
                        break;
                    case RELE:
                        switch (pOrigem.getTipoEquipamentoInstalado()) {
                            case ELO:

                                break;
                            case RELE:

                                break;
                            case RELIGADOR:

                                break;
                        }
                        break;
                    case RELIGADOR:
                        switch (pOrigem.getTipoEquipamentoInstalado()) {
                            case ELO:

                                break;
                            case RELE:

                                break;
                            case RELIGADOR:

                                break;
                        }
                        break;
                }
            }
        
        return true;
    }
    
    public static void main(String[] args) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AjusteFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(() -> {
            new sigprod2.gui.MainFrame().setVisible(true);
        });
        SwingUtilities.invokeLater(() -> {
            new AjusteFrame(sigprod2.gui.MainFrame.frame).setVisible(true);
        });
    }
}
