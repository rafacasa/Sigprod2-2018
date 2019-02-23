package sigprod2.gui.ajustepanels;

import java.awt.Color;
import sigprod2.gui.AjusteFrame;
import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.jfree.chart.ChartPanel;
import sigprod2.auxiliar.Coordenograma;
import sigprod2.metricas.Metricas_Elo_Elo;
import sigprod2.modelo.CurvasElo;
import sigprod2.modelo.Elo;
import sigprod2.modelo.Equipamento;

/**
 *
 * @author Rafael Casa
 */
public class PanelAjusteEloElo extends SIGPROD2.gui.ajustepanels.PanelAjuste {

    private List<Metricas_Elo_Elo> metricas;
    private Elo selecionado, pai;
    private AjusteFrame ajusteFrame;
    private JPanel panelTProtetor1, panelTProtetor2, panelIFTMin, panelIFTMinSel, panelPorcentagem, panelBotoes;
    private JLabel nomeTProtetor1, nomeTProtetor2, nomeIFTMin, nomeIFTMinSel, nomePorcentagem, labelTProtetor1, labelTProtetor2, labelIFTMin, labelIFTMinSel, labelPorcentagem;
    private JComboBox<Metricas_Elo_Elo> lista;
    private JButton botaoSelecionar;

    public PanelAjusteEloElo(List<Metricas_Elo_Elo> metricas, AjusteFrame ajusteFrame, Elo pai) {
        this.metricas = metricas;
        this.ajusteFrame = ajusteFrame;
        this.criaPanels();
        this.initComponents();
        this.addItens();
        this.pai = pai;
    }

    private void criaPanels() {
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        this.panelTProtetor1 = new JPanel();
        this.panelTProtetor1.setLayout(new BoxLayout(this.panelTProtetor1, BoxLayout.LINE_AXIS));

        this.panelTProtetor2 = new JPanel();
        this.panelTProtetor2.setLayout(new BoxLayout(this.panelTProtetor2, BoxLayout.LINE_AXIS));

        this.panelIFTMin = new JPanel();
        this.panelIFTMin.setLayout(new BoxLayout(this.panelIFTMin, BoxLayout.LINE_AXIS));

        this.panelIFTMinSel = new JPanel();
        this.panelIFTMinSel.setLayout(new BoxLayout(this.panelIFTMinSel, BoxLayout.LINE_AXIS));

        this.panelPorcentagem = new JPanel();
        this.panelPorcentagem.setLayout(new BoxLayout(this.panelPorcentagem, BoxLayout.LINE_AXIS));

        this.panelBotoes = new JPanel();
        this.panelBotoes.setLayout(new BoxLayout(this.panelBotoes, BoxLayout.LINE_AXIS));
    }

    private void initComponents() {
        this.lista = new JComboBox<>() {
            @Override
            public Dimension getMaximumSize() {
                Dimension max = super.getMaximumSize();
                max.height = getPreferredSize().height;
                return max;
            }
        };

        this.metricas.forEach((metrica) -> {
            this.lista.addItem(metrica);
        });

        this.lista.setSelectedIndex(-1);
        this.lista.addActionListener(this::listaActionPerformed);

        this.nomeTProtetor1 = new JLabel("TProtetorProtegido1:  ");
        this.nomeTProtetor2 = new JLabel("TProtetorProtegido2:  ");
        this.nomeIFTMin = new JLabel("IFTMin:  ");
        this.nomeIFTMinSel = new JLabel("IFTMinSel:  ");
        this.nomePorcentagem = new JLabel("Porcentagem de Cobertura:  ");

        this.labelTProtetor1 = new JLabel();
        this.labelTProtetor2 = new JLabel();
        this.labelIFTMin = new JLabel();
        this.labelIFTMinSel = new JLabel();
        this.labelPorcentagem = new JLabel();

        this.botaoSelecionar = new JButton("Selecionar Elo");
        this.botaoSelecionar.addActionListener(this::botaoSelecionarActionPerformed);

    }

    private void addItens() {
        this.panelTProtetor1.add(this.nomeTProtetor1);
        this.panelTProtetor1.add(Box.createHorizontalGlue());
        this.panelTProtetor1.add(this.labelTProtetor1);

        this.panelTProtetor2.add(this.nomeTProtetor2);
        this.panelTProtetor2.add(Box.createHorizontalGlue());
        this.panelTProtetor2.add(this.labelTProtetor2);

        this.panelIFTMin.add(this.nomeIFTMin);
        this.panelIFTMin.add(Box.createHorizontalGlue());
        this.panelIFTMin.add(this.labelIFTMin);

        this.panelIFTMinSel.add(this.nomeIFTMinSel);
        this.panelIFTMinSel.add(Box.createHorizontalGlue());
        this.panelIFTMinSel.add(this.labelIFTMinSel);

        this.panelPorcentagem.add(this.nomePorcentagem);
        this.panelPorcentagem.add(Box.createHorizontalGlue());
        this.panelPorcentagem.add(this.labelPorcentagem);

        this.panelBotoes.add(Box.createHorizontalGlue());
        this.panelBotoes.add(this.botaoSelecionar);
        this.panelBotoes.add(Box.createRigidArea(new Dimension(20, 0)));

        this.add(this.lista);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelTProtetor1);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelIFTMin);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelIFTMinSel);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelTProtetor2);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelPorcentagem);
        this.add(Box.createRigidArea(new Dimension(0, 10)));
        this.add(this.panelBotoes);
    }

    private void listaActionPerformed(java.awt.event.ActionEvent evt) {
        Metricas_Elo_Elo metrica = this.lista.getItemAt(this.lista.getSelectedIndex());
        this.labelTProtetor1.setText(metrica.gettProtetorProtegido1() + "");
        this.labelTProtetor2.setText(metrica.gettProtetorProtegido2() + "");
        this.labelIFTMin.setText(metrica.getiFTMinI300() + "");
        this.labelIFTMinSel.setText(metrica.getiFTMinSelI300() + "");
        this.labelPorcentagem.setText((metrica.getPorcentagemProtegida() * 100) + "%");
        this.ajusteFrame.atualizaCoordenograma(this.geraCoordenograma());
        this.ajusteFrame.pack();
    }

    private void botaoSelecionarActionPerformed(java.awt.event.ActionEvent evt) {
        this.selecionado = this.lista.getItemAt(this.lista.getSelectedIndex()).getElo();
        this.ajusteFrame.selecionaEquipamento(this.metricas.get(0).getPonto(), this.selecionado);
    }

    @Override
    public JPanel geraCoordenograma() {
        int index = this.lista.getSelectedIndex();
        if (index >= 0) {
            Elo atual = this.lista.getItemAt(index).getElo();
            Coordenograma coordenograma = new Coordenograma("Coordenograma");
            coordenograma.add(this.pai, Color.BLUE);
            coordenograma.add(atual, Color.GREEN);
            coordenograma.add(this.pai, CurvasElo.MINIMA, 0.75, Color.CYAN);
            coordenograma.gerarGrafico();
            return coordenograma.getChartPanel();
        } else {
            return new JPanel();
        }
    }
}
