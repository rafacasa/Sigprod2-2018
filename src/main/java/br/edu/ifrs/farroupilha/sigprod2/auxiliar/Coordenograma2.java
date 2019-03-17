package br.edu.ifrs.farroupilha.sigprod2.auxiliar;

import br.edu.ifrs.farroupilha.sigprod2.modelo.Elo;
import java.util.List;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.XYSeries;
import org.knowm.xchart.style.Styler;

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

    private void add(Elo elo) {

    }

    private void addElo(List<List<Double>> pontos, String nomeCurva) {
        XYSeries serie = this.chart.addSeries(nomeCurva, pontos.get(0), pontos.get(1));
    }
}
