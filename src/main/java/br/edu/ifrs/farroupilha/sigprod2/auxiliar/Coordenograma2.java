package br.edu.ifrs.farroupilha.sigprod2.auxiliar;

import br.edu.ifrs.farroupilha.sigprod2.modelo.CurvasElo;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Elo;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
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
public class Coordenograma2 {

    private XYChart chart;
    private String titulo;

    public Coordenograma2(String titulo) {
        this.titulo = titulo;
        this.createChart();
    }

    private void createChart() {
        this.chart = new XYChartBuilder().title(this.titulo).xAxisTitle("Corrente").yAxisTitle("Tempo").build();
        this.chart.getStyler().setYAxisLogarithmic(true);
        this.chart.getStyler().setXAxisLogarithmic(true);
        this.chart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
    }

    public void add(Elo elo, String nomeCurva, Color cor) {
        this.addCurvaElo(elo.getDadosCurva(CurvasElo.MAXIMA), nomeCurva, cor, true);
        this.addCurvaElo(elo.getDadosCurva(CurvasElo.MINIMA), nomeCurva + "2", cor, false);
    }

    public void add(Elo elo, CurvasElo curva, double fator, String nomeCurva, Color cor) {
        List<List<Double>> dados = elo.getDadosCurva(curva);
        List<Double> tempo = dados.get(1);
        List<Double> novoTempos = new ArrayList<>();
        tempo.forEach(t -> {
            novoTempos.add(t * fator);
        });
        dados.set(1, novoTempos);
        this.addCurvaElo(dados, nomeCurva, cor, true);
    }

    private void addCurvaElo(List<List<Double>> pontos, String nomeCurva, Color cor, boolean showInLegend) {
        XYSeries serie = this.chart.addSeries(nomeCurva, pontos.get(0), pontos.get(1));
        serie.setLineColor(cor);
        serie.setMarker(SeriesMarkers.NONE);
        serie.setShowInLegend(showInLegend);
    }

    public JPanel getChartPanel() {
        this.chart.getStyler().setChartBackgroundColor(Color.WHITE);
        return new XChartPanel(this.chart);
    }
}
