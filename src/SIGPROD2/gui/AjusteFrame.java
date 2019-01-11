package SIGPROD2.gui;

import SIGPROD2.gui.ajustepanels.PanelAjusteEloElo;
import SIGPROD2.gui.ajustepanels.PanelNavegacao;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import sigprod2.auxiliar.AjusteImpossivelException;
import sigprod2.criterios.Criterios_Elo;
import sigprod2.criterios.Criterios_Elo_Elo;
import sigprod2.metricas.Metricas_Elo_Elo;
import sigprod2.modelo.Elo;
import sigprod2.modelo.Equipamento;
import sigprod2.modelo.Ponto;
import sigprod2.modelo.Rede;
import sigprod2.modelo.TipoEquipamento;

/**
 *
 * @author Rafael Casa
 */
public class AjusteFrame extends JDialog {

    private PanelNavegacao navegacao;
    private JPanel panelAjuste;
    private sigprod2.gui.MainFrame mainFrame;
    private Rede rede;

    public AjusteFrame(sigprod2.gui.MainFrame frame) {
        super(frame);
        this.mainFrame = frame;
        this.rede = this.mainFrame.getRede();
        initComponents();
        addComponents();
        this.ajustar(this.navegacao.getPontoAtual(), true);
    }

    private void initComponents() {
        this.panelAjuste = new JPanel();
        this.panelAjuste.add(Box.createRigidArea(new Dimension(400, 400)));

        this.navegacao = new PanelNavegacao(this.rede, this);
        this.navegacao.atualizarPontoAtual();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                limparPanel();
                dispose();
            }
        });
    }

    private void addComponents() {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.LINE_AXIS));
        this.add(this.navegacao);
        this.add(this.panelAjuste);
        this.pack();
    }

    private void limparPanel() {
        Ponto pontoAtual = this.navegacao.getPontoAtual();
        Ponto.removeAttribute(this.rede.getMapa().getNode(pontoAtual.getNome()), "ui.class", "equipamentoSendoAjustado");
        this.panelAjuste.removeAll();
        this.panelAjuste.add(Box.createRigidArea(new Dimension(400, 400)));

    }

    public boolean ajustar(Ponto ponto, boolean inicioRede) {
        this.limparPanel();

        Ponto pOrigem = this.rede.getParentRedeReduzida(ponto);
        TipoEquipamento equip = ponto.getTipoEquipamentoInstalado();

        if (inicioRede) {
            switch (equip) {
                case ELO:
                    //Ponto.addAttribute(this.rede.getMapa().getNode(ponto.getNome()), "ui.class", "equipamentoSendoAjustado");
                    Elo ajuste;
                    try {
                        ajuste = Criterios_Elo.criterio_elo(this.rede.getElosDisponiveis(), ponto, this.rede);
                        ponto.setEquipamentoInstalado(ajuste);
                        Ponto.addAttribute(this.rede.getMapa().getNode(ponto.getNome()), "ui.class", "equipamentoSelecionado");
                    } catch (AjusteImpossivelException ex) {
                        ex.printStackTrace();
                        return false;
                    }
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
                            Criterios_Elo_Elo criteriosEloElo = new Criterios_Elo_Elo(this.rede, pOrigem, ponto);
                            List<Metricas_Elo_Elo> metricas;
                            try {
                                System.out.println("PONTO SENDO AJUSTADO: " + ponto.getNome());
                                metricas = criteriosEloElo.ajuste();
                                Ponto.removeAttribute(this.rede.getMapa().getNode(ponto.getNome()), "ui.class", "equipamentoSelecionado");
                                Ponto.addAttribute(this.rede.getMapa().getNode(ponto.getNome()), "ui.class", "equipamentoSendoAjustado");
                                this.panelAjuste.removeAll();
                                this.panelAjuste.add(new PanelAjusteEloElo(metricas, this));
                                this.revalidate();
                            } catch (AjusteImpossivelException ex) {
                                ex.printStackTrace();
                                return false;
                            }
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

    public void selecionaEquipamento(Ponto p, Equipamento equipamento) {
        p.setEquipamentoInstalado(equipamento);
        Ponto.addAttribute(this.rede.getMapa().getNode(p.getNome()), "ui.class", "equipamentoSelecionado");
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
