package SIGPROD2.auxiliar;

import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import sigprod2.modelo.Curvas;
import sigprod2.modelo.Elo;
import sigprod2.modelo.PontoCurva;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Coordenograma {

    private String titulo;
    private JFreeChart chart;
    private XYSeriesCollection dataset;

    public Coordenograma(String titulo) {
        this.titulo = titulo;
        this.dataset = new XYSeriesCollection();
    }

    public void add(Curvas curvas) {
        int numeroCurvas = curvas.getQtdCurvas();
        for (int i = 0; i < numeroCurvas; i++) {
            XYSeries data = new XYSeries(curvas.getNomeCurva(i));
            List<PontoCurva> pontos = curvas.getCurva(i);
            pontos.forEach(ponto -> {
                data.add(ponto.getCorrente(), ponto.getTempo());
            });
            this.dataset.addSeries(data);
        }
    }

    public void add(Elo elo) {
        // Curva mínima
        XYSeries dataMin = new XYSeries("elo" + elo.getCorrenteNominal() + "minima");
        List<PontoCurva> pontos = elo.getCurvaDeMinimaFusao();
        pontos.forEach(ponto -> {
            dataMin.add(ponto.getCorrente(), ponto.getTempo());
        });
        this.dataset.addSeries(dataMin);

        //Curva maxima
        XYSeries dataMax = new XYSeries("elo" + elo.getCorrenteNominal() + "maxima");
        pontos = elo.getCurvaDeMaximaInterrupcao();
        pontos.forEach(ponto -> {
            dataMax.add(ponto.getCorrente(), ponto.getTempo());
        });
        this.dataset.addSeries(dataMax);

        //hachura
    }

    //inserir reta na corrente
    public void add(double corrente) {

    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
        if (this.chart != null) {
            this.chart.setTitle(titulo);
        }
    }

    public void gerarGrafico() {
        this.chart = ChartFactory.createXYLineChart(
                titulo,
                "Category", "Value",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false);
    }
}
