package br.edu.ifrs.farroupilha.sigprod2.gui;

import br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels.PanelAjuste;
import br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels.PanelAjusteEloElo;
import br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels.PanelNavegacao;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import br.edu.ifrs.farroupilha.sigprod2.auxiliar.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.auxiliar.Coordenograma;
import br.edu.ifrs.farroupilha.sigprod2.criterios.Criterios_Elo;
import br.edu.ifrs.farroupilha.sigprod2.criterios.Criterios_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.criterios.Criterios_Rele;
import br.edu.ifrs.farroupilha.sigprod2.gui.ajustepanels.PanelAjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.metricas.Metricas_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.metricas.Metricas_Rele;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Equipamento;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rele;
import br.edu.ifrs.farroupilha.sigprod2.modelo.TipoEquipamento;
import java.awt.Color;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class AjusteFrame extends JDialog {

    private static final Logger LOGGER = LogManager.getLogger(AjusteFrame.class.getName());
    private PanelNavegacao navegacao;
    private JPanel panelCoordenograma, panelAjuste;
    private PanelAjuste ajuste;
    private br.edu.ifrs.farroupilha.sigprod2.gui.MainFrame mainFrame;
    private Rede rede;
    private boolean coordenograma;

    public AjusteFrame(br.edu.ifrs.farroupilha.sigprod2.gui.MainFrame frame) {
        super(frame);
        this.coordenograma = false;
        this.mainFrame = frame;
        this.rede = this.mainFrame.getRede();
        initComponents();
        addComponents();
        this.ajustar(this.navegacao.getPontoAtual(), true);
    }

    private void initComponents() {
        this.navegacao = new PanelNavegacao(this.rede, this);
        this.navegacao.atualizarPontoAtual();

        this.panelAjuste = new JPanel();
        this.panelAjuste.add(Box.createRigidArea(this.navegacao.getPreferredSize()));

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
        this.add(this.navegacao);
        this.add(this.panelAjuste);
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
                    Metricas_Rele fase = rele.getAjusteFase();
                    Metricas_Rele neutro = rele.getAjusteNeutro();
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
                    this.ajuste = new PanelAjusteRele(rele);
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
                                ponto.resetAtributos(true);
                                this.panelAjuste.removeAll();
                                this.ajuste = new PanelAjusteEloElo(metricas, this, (Elo) pOrigem.getEquipamentoInstalado());
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
        p.resetAtributos(true);
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
            new br.edu.ifrs.farroupilha.sigprod2.gui.MainFrame().setVisible(true);
        });
        SwingUtilities.invokeLater(() -> {
            new AjusteFrame(br.edu.ifrs.farroupilha.sigprod2.gui.MainFrame.frame).setVisible(true);
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
    }

    public void atualizaCoordenograma(JPanel chart) {
        if (this.coordenograma) {
            this.panelCoordenograma.removeAll();
            this.panelCoordenograma.add(chart);
            this.pack();
        }
    }
}
