package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import java.awt.Color;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.UIManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.markers.SeriesMarkers;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Coordenograma {

    private static final Logger LOGGER = LogManager.getLogger(Coordenograma.class.getName());
    private XYChart chart;
    private JPanel chartPanel;
    private String titulo;

    public Coordenograma(String titulo) {
        this.titulo = titulo;
        this.createChart();
    }

    private void createChart() {
        this.chart = new XYChartBuilder().title(this.titulo).xAxisTitle("Corrente").yAxisTitle("Tempo").build();
        this.chart.getStyler().setYAxisLogarithmic(true);
        this.chart.getStyler().setXAxisLogarithmic(true);
        this.chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        this.chart.getStyler().setToolTipsEnabled(true);
        this.chart.getStyler().setToolTipsAlwaysVisible(true);
        this.chart.getStyler().setChartBackgroundColor(UIManager.getColor("Panel.background"));
        this.chartPanel = new XChartPanel(this.chart);
    }

    public void add(Elo elo, String nomeCurva, Color cor) {
        this.addCurva(elo.getDadosCurva(CurvasElo.MAXIMA), nomeCurva, cor, true);
        this.addCurva(elo.getDadosCurva(CurvasElo.MINIMA), nomeCurva + "2", cor, false);
    }

    public List<String> add(BigDecimal corrente, List<BigDecimal> tempos, String nome, List<Color> cores) {
        double correnteD = corrente.doubleValue();
        List<String> nomes = new ArrayList<>();
        List<Double> temposD = this.convertToDouble(tempos);
        for (int i = 0; i < temposD.size(); i++) {
            this.addPonto(correnteD, temposD.get(i), nome + i, cores.get(i));
            nomes.add(nome + i);
        }
        return nomes;
    }

    public void add(Elo elo, CurvasElo curva, double fator, String nomeCurva, Color cor) {
        List<List<Double>> dados = elo.getDadosCurva(curva);
        List<Double> tempo = dados.get(1);
        List<Double> novoTempos = new ArrayList<>();
        tempo.forEach(t -> {
            novoTempos.add(t * fator);
        });
        dados.set(1, novoTempos);
        this.addCurva(dados, nomeCurva, cor, true);
    }

    private void addCurva(List<List<Double>> pontos, String nomeCurva, Color cor, boolean showInLegend) {
        XYSeries serie = this.chart.addSeries(nomeCurva, pontos.get(0), pontos.get(1));
        String[] tooltips = new String[pontos.get(0).size()];
        for (int i = 0; i < tooltips.length; i++) {
            tooltips[i] = null;
        }
        serie.setToolTips(tooltips);
        serie.setLineColor(cor);
        serie.setMarker(SeriesMarkers.NONE);
        serie.setShowInLegend(showInLegend);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    private void addPonto(double x, double y, String nomeCurva, Color cor) {
        List<Double> listaX = new ArrayList<>();
        List<Double> listaY = new ArrayList<>();
        listaX.add(x);
        listaY.add(y);
        XYSeries serie = this.chart.addSeries(nomeCurva, listaX, listaY);
        serie.setLineColor(cor);
        serie.setMarker(SeriesMarkers.CIRCLE);
        serie.setMarkerColor(cor);
        String[] tooltips = {"Tempo: " + y};
        serie.setToolTips(tooltips);
        serie.setShowInLegend(false);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    public void add(Rele rele, Color corFase, Color corNeutro) {
        if (rele.ajustado()) {
            List<List<BigDecimal>> dadosFase = rele.getDadosAjuste(true);
            List<List<Double>> dadosFaseDouble = new ArrayList<>();
            dadosFaseDouble.add(this.convertToDouble(dadosFase.get(0)));
            dadosFaseDouble.add(this.convertToDouble(dadosFase.get(1)));
            this.addCurva(dadosFaseDouble, "Curva de Fase", corFase, true);

            List<List<BigDecimal>> dadosNeutro = rele.getDadosAjuste(false);
            List<List<Double>> dadosNeutroDouble = new ArrayList<>();
            dadosNeutroDouble.add(this.convertToDouble(dadosNeutro.get(0)));
            dadosNeutroDouble.add(this.convertToDouble(dadosNeutro.get(1)));
            this.addCurva(dadosNeutroDouble, "Curva de Neutro", corNeutro, true);
        }
    }

    public void add(BigDecimal corrente, Color cor, String nome) {
        double tempoMin = 0.01;//this.chart.getStyler().getYAxisMin();
        double tempoMax = 100;//this.chart.getStyler().getYAxisMax();
        List<Double> tempos = Arrays.asList(tempoMin, tempoMax);
        List<Double> correntes = Arrays.asList(corrente.doubleValue(), corrente.doubleValue());
        String[] tooltips = {null, null};
        XYSeries serie = this.chart.addSeries(nome, correntes, tempos);
        serie.setLineColor(cor);
        serie.setMarker(SeriesMarkers.NONE);
        serie.setShowInLegend(true);
        serie.setToolTips(tooltips);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    public void remove(String s) {
        this.chart.removeSeries(s);
        this.chartPanel.revalidate();
        this.chartPanel.repaint();
    }

    public JPanel getChartPanel() {
        return this.chartPanel;
    }

    private List<Double> convertToDouble(List<BigDecimal> list) {
        List<Double> novaLista = new ArrayList<>();
        list.forEach(n -> {
            novaLista.add(n.doubleValue());
        });
        return novaLista;
    }
}
