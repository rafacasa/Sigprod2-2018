package br.edu.ifrs.farroupilha.sigprod2.frontend.frames;

import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjuste;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteEloElo;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelNavegacao;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.Criterios_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.Criterios_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.criterios.Criterios_Rele;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.frontend.panels.defaultajuste.PanelAjusteReleElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.Metricas_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Equipamento;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.TipoEquipamento;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class DefaultAjusteFrame extends JDialog {

    private static final Logger LOGGER = LogManager.getLogger(DefaultAjusteFrame.class.getName());
    private PanelNavegacao navegacao;
    private JPanel panelCoordenograma, panelAjuste, panelEsquerda;
    private PanelAjuste ajuste;
    //private DefaultMainFrame mainFrame;
    private Rede rede;
    private boolean coordenograma;

    public DefaultAjusteFrame(br.edu.ifrs.farroupilha.sigprod2.frontend.frames.DefaultMainFrame frame) {
        super(frame);
        this.coordenograma = false;
        //this.mainFrame = frame;
        this.rede = frame.getRede();
        initComponents();
        addComponents();
        this.ajustar(this.navegacao.getPontoAtual(), true);
    }

    private void initComponents() {
        this.navegacao = new PanelNavegacao(this.rede, this);
        this.navegacao.atualizarPontoAtual();

        this.panelAjuste = new JPanel();
        this.panelAjuste.add(Box.createRigidArea(this.navegacao.getPreferredSize()));

        this.panelEsquerda = new JPanel();
        this.panelEsquerda.setLayout(new BoxLayout(this.panelEsquerda, BoxLayout.PAGE_AXIS));

        this.panelCoordenograma = new JPanel();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent evt) {
                limparPanel();
                dispose();
            }
        });
    }

    private void addComponents() {
        this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.LINE_AXIS));
        this.panelEsquerda.add(this.panelAjuste);
        this.panelAjuste.add(Box.createVerticalGlue());
        this.panelEsquerda.add(this.navegacao);
        this.add(this.panelEsquerda);
        this.add(this.panelCoordenograma);
        this.pack();
    }

    private void limparPanel() {
        Ponto pontoAtual = this.navegacao.getPontoAtual();
        pontoAtual.resetAtributos();
        this.panelAjuste.removeAll();
        this.panelAjuste.add(Box.createRigidArea(new Dimension(400, 400)));
        this.panelCoordenograma.removeAll();
        this.pack();
    }

    public boolean ajustar(Ponto ponto, boolean inicioRede) {
        this.limparPanel();

        Ponto pOrigem = this.rede.getParentRedeReduzida(ponto);
        TipoEquipamento equip = ponto.getTipoEquipamentoInstalado();
        LOGGER.debug("AJUSTE PONTO - " + ponto.getNome());
        LOGGER.debug("EQUIPAMENTO NO PONTO " + ponto.getTipoEquipamentoInstalado());

        if (inicioRede) {
            switch (equip) {
                case ELO:
                    Elo ajuste;
                    try {
                        ajuste = Criterios_Elo.criterio_elo(this.rede.getElosDisponiveis(), ponto, this.rede);
                        ponto.setEquipamentoInstalado(ajuste);
                        ponto.resetAtributos(true);
                    } catch (AjusteImpossivelException ex) {
                        ex.printStackTrace();
                        return false;
                    }
                    break;
                case RELE:
                    LOGGER.traceEntry();
                    Rele rele = Criterios_Rele.getReleTeste(); //COMO DEFINIR QUAL EQUIPAMENTO ESTA INSTALADO NO PONTO
                    Criterios_Rele criteriosRele = new Criterios_Rele(this.rede, ponto, rele);
                    criteriosRele.ajuste();
                    AjusteRele fase = rele.getAjusteFase();
                    AjusteRele neutro = rele.getAjusteNeutro();
                    LOGGER.debug("AJUSTE DE FASE");
                    LOGGER.debug("MENOR FM - " + fase.getFm());
                    LOGGER.debug("AC - " + fase.getAc());
                    LOGGER.debug("AT - " + fase.getAt());
                    LOGGER.debug("CURVA a/b/p - " + fase.getCurva().getA() + " / " + fase.getCurva().getB() + " / " + fase.getCurva().getP());
                    LOGGER.debug("AJUSTE DE NEUTRO");
                    LOGGER.debug("MENOR FM - " + neutro.getFm());
                    LOGGER.debug("AC - " + neutro.getAc());
                    LOGGER.debug("AT - " + neutro.getAt());
                    LOGGER.debug("CURVA a/b/p - " + neutro.getCurva().getA() + " / " + neutro.getCurva().getB() + " / " + neutro.getCurva().getP());
                    this.panelAjuste.removeAll();
                    this.ajuste = new PanelAjusteRele(rele);
                    this.panelAjuste.add(this.ajuste);
                    this.selecionaEquipamento(ponto, rele);
                    break;
                case RELIGADOR:

                    break;
                default:
                    LOGGER.debug("DEFAULT NO PRIMEIRO SWITCH");
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
                                metricas = criteriosEloElo.ajuste();
                                ponto.resetAtributos(true);
                                this.panelAjuste.removeAll();
                                this.ajuste = new PanelAjusteEloElo(metricas, (Elo) pOrigem.getEquipamentoInstalado());
                                this.panelAjuste.add(this.ajuste);
                                if (this.coordenograma) {
                                    this.panelCoordenograma.removeAll();
                                    this.panelCoordenograma.add(this.ajuste.geraCoordenograma());
                                }
                                this.pack();
                            } catch (AjusteImpossivelException ex) {
                                ex.printStackTrace();
                                return false;
                            }
                            break;
                        case RELE:
                            LOGGER.trace("AJUSTE RELE ELO");
                            this.setPanelAjuste(new PanelAjusteReleElo(ponto, rede, pOrigem));
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
                default:
                    LOGGER.debug("DEFAULT NO SEGUNDO SWITCH");
                    break;
            }
        }
        return true;
    }

    public void selecionaEquipamento(Ponto p, Equipamento equipamento) {
        p.setEquipamentoInstalado(equipamento);
        p.resetAtributos(true);
    }

    private void setPanelAjuste(PanelAjuste panel) {
        this.ajuste = panel;
        this.panelAjuste.removeAll();
        this.panelAjuste.add(this.ajuste);
        if (this.coordenograma) {
            this.panelCoordenograma.removeAll();
            this.panelCoordenograma.add(this.ajuste.geraCoordenograma());
        }
        this.revalidate();
        this.repaint();
        this.pack();
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
            java.util.logging.Logger.getLogger(DefaultAjusteFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        SwingUtilities.invokeLater(() -> {
            new br.edu.ifrs.farroupilha.sigprod2.frontend.frames.DefaultMainFrame().setVisible(true);
        });
        SwingUtilities.invokeLater(() -> {
            new DefaultAjusteFrame(br.edu.ifrs.farroupilha.sigprod2.frontend.frames.DefaultMainFrame.frame).setVisible(true);
        });
    }

    public void ativarCoordenograma() {
        this.coordenograma = true;
        this.panelCoordenograma.add(this.ajuste.geraCoordenograma());
        this.pack();
    }

    public void desativarCoordenograma() {
        this.coordenograma = false;
        this.panelCoordenograma.removeAll();
        this.revalidate();
        this.repaint();
        this.pack();
        LOGGER.debug("REMOVE COORDENOGRAMA");
    }

    public void atualizaCoordenograma(JPanel chart) {
        if (this.coordenograma) {
            this.panelCoordenograma.removeAll();
            this.panelCoordenograma.add(chart);
            this.pack();
        }
    }
}
