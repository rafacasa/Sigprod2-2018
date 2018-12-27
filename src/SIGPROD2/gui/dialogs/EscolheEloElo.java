package sigprod2.gui.dialogs;

import java.awt.Dimension;
import java.util.List;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import sigprod2.gui.MainFrame;
import sigprod2.metricas.Metricas_Elo_Elo;
import sigprod2.modelo.Elo;

public class EscolheEloElo{

    private List<Metricas_Elo_Elo> metricas;
    private Elo selecionado;
    private boolean loop;
    private JPanel panel, panelTProtetor1, panelTProtetor2, panelIFTMin, panelIFTMinSel;
    private JComboBox<Metricas_Elo_Elo> lista;
    private JButton botaoSelecionar;
    private JLabel nomeTProtetor1, nomeTProtetor2, nomeIFTMin, nomeIFTMinSel, labelTProtetor1, labelTProtetor2, labelIFTMin, labelIFTMinSel;
    private JDialog dialog;

    public EscolheEloElo(List<Metricas_Elo_Elo> metricas) {
        this.metricas = metricas;
        this.loop = true;
        this.criaPanels();
        this.initComponents();
        this.addItens();
        this.dialog.pack();
        this.dialog.setVisible(true);
    }

    private void criaPanels() {
        this.panel = new JPanel();
        this.panel.setLayout(new BoxLayout(this.panel, BoxLayout.PAGE_AXIS));

        this.panelTProtetor1 = new JPanel();
        this.panelTProtetor1.setLayout(new BoxLayout(this.panelTProtetor1, BoxLayout.LINE_AXIS));

        this.panelTProtetor2 = new JPanel();
        this.panelTProtetor2.setLayout(new BoxLayout(this.panelTProtetor2, BoxLayout.LINE_AXIS));

        this.panelIFTMin = new JPanel();
        this.panelIFTMin.setLayout(new BoxLayout(this.panelIFTMin, BoxLayout.LINE_AXIS));

        this.panelIFTMinSel = new JPanel();
        this.panelIFTMinSel.setLayout(new BoxLayout(this.panelIFTMinSel, BoxLayout.LINE_AXIS));
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
        
        this.labelTProtetor1 = new JLabel();
        this.labelTProtetor2 = new JLabel();
        this.labelIFTMin = new JLabel();
        this.labelIFTMinSel = new JLabel();
        
        this.botaoSelecionar = new JButton("Selecionar Elo");
        this.botaoSelecionar.addActionListener(this::botaoSelecionarActionPerformed);
        
        this.dialog = new JDialog(MainFrame.frame, "Escolher Elo", true);
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
        
        this.panel.add(this.lista);
        this.panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.panel.add(this.panelTProtetor1);
        this.panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.panel.add(this.panelIFTMin);
        this.panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.panel.add(this.panelIFTMinSel);
        this.panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.panel.add(this.panelTProtetor2);
        this.panel.add(Box.createRigidArea(new Dimension(0, 10)));
        this.panel.add(this.botaoSelecionar);
        
        this.dialog.add(this.panel);
        
    }
    
    private void listaActionPerformed(java.awt.event.ActionEvent evt) {
        Metricas_Elo_Elo metrica = this.lista.getItemAt(this.lista.getSelectedIndex());
        this.labelTProtetor1.setText(metrica.gettProtetorProtegido1() + "");
        this.labelTProtetor2.setText(metrica.gettProtetorProtegido2() + "");
        this.labelIFTMin.setText(metrica.getiFTMinI300()+ "");
        this.labelIFTMinSel.setText(metrica.getiFTMinSelI300()+ "");
    }
    
    private void botaoSelecionarActionPerformed(java.awt.event.ActionEvent evt) {
        this.selecionado = this.lista.getItemAt(this.lista.getSelectedIndex()).getElo();
        this.dialog.dispose();
        this.loop = false;
    }

    public Elo getSelecionado() {
        return selecionado;
    }

    public boolean isLoop() {
        return loop;
    }

//    @Override
//    public void run() {
//        this.dialog.pack();
//        this.dialog.setVisible(true);
//    }
    
    
}
