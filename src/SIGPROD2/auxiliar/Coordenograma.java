package sigprod2.auxiliar;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import sigprod2.modelo.Curvas;
import sigprod2.modelo.CurvasElo;
import sigprod2.modelo.Elo;
import sigprod2.modelo.PontoCurva;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Coordenograma {

    private String titulo;
    private JFreeChart chart;
    private List<Color> cores;
    private XYSeriesCollection dataset;

    public Coordenograma(String titulo) {
        this.titulo = titulo;
        this.cores = new ArrayList<>();
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

    public void add(Elo elo, Color cor) {
        // Curva mínima
        this.add(elo, CurvasElo.MINIMA, cor);
        //Curva maxima
        this.add(elo, CurvasElo.MAXIMA, cor);
        //hachura
        this.addHachura(elo, cor);
    }

    public void add(Elo elo, CurvasElo curva, Color cor) {
        XYSeries data = new XYSeries("elo" + elo.getCorrenteNominal() + curva.toString());
        List<PontoCurva> pontos = elo.getCurva(curva);
        this.add(pontos, data, cor);
    }

    public void add(Elo elo, CurvasElo curva, double fator, Color cor) {
        XYSeries data = new XYSeries("elo" + elo.getCorrenteNominal() + curva.toString() + "fator" + fator);
        List<PontoCurva> pontos = elo.getCurva(curva);
        pontos.forEach(ponto -> {
            ponto.setTempo(ponto.getTempo() * fator);
        });
        this.add(pontos, data, cor);
    }

    private void add(List<PontoCurva> pontos, XYSeries data, Color cor) {
        pontos.forEach(ponto -> {
            data.add(ponto.getCorrente(), ponto.getTempo());
        });
        this.dataset.addSeries(data);
        this.cores.add(cor);
    }

    private void addHachura(Elo elo, Color cor) {
        List<XYSeries> hachura = new ArrayList<>();
        double tempoMM, tempoTC, tempoMenor, tempoMaior;
        tempoMM = elo.GetMaiorTempo(CurvasElo.MINIMA);
        tempoTC = elo.GetMaiorTempo(CurvasElo.MAXIMA);
        if (tempoMM <= tempoTC) {
            tempoMaior = tempoMM;
        } else {
            tempoMaior = tempoTC;
        }
        tempoMM = elo.GetMenorTempo(CurvasElo.MINIMA);
        tempoTC = elo.GetMenorTempo(CurvasElo.MAXIMA);
        if (tempoMM >= tempoTC) {
            tempoMenor = tempoMM;
        } else {
            tempoMenor = tempoTC;
        }
        int div = 80;
        double expTempoMenor = Math.log10(tempoMenor * 1.01);
        double expTempoMaior = Math.log10(tempoMaior / 1.01);
        double expPasso = (expTempoMaior - expTempoMenor) / (div - 1);
        double corrente1, corrente2;
        double expAux = expTempoMaior;
        double tempoAux;
        for (int k = 0; k < div; k++) {
            tempoAux = Math.pow(10, expAux);
            corrente1 = elo.correnteDoTempo(tempoAux, CurvasElo.MINIMA);
            corrente2 = elo.correnteDoTempo(tempoAux, CurvasElo.MINIMA);
            XYSeries hachuraAux = new XYSeries(k + "Elo" + elo.getCorrenteNominal());
            hachuraAux.add(corrente1, tempoAux);
            hachuraAux.add(corrente2, tempoAux);
            hachura.add(hachuraAux);
            expAux -= expPasso;
        }
        hachura.forEach((series) -> {
            this.dataset.addSeries(series);
            this.cores.add(cor);
        });
    }

    //inserir reta na corrente
    public void add(double corrente, double tempo1, double tempo2) {
        XYSeries retaCorrente = new XYSeries("retacorrente" + tempo1 + tempo2);
        retaCorrente.add(corrente, tempo1);
        retaCorrente.add(corrente, tempo2);
        this.dataset.addSeries(retaCorrente);
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
        XYPlot plot = chart.getXYPlot();
        NumberAxis domainAxis = new LogarithmicAxis("Corrente (A)");
        NumberAxis rangeAxis = new LogarithmicAxis("Tempo (s)");
        plot.setDomainAxis(domainAxis);
        plot.setRangeAxis(rangeAxis);
        chart.removeLegend();
        chart.setBackgroundPaint(Color.white);
        plot.setOutlinePaint(Color.black);
        XYItemRenderer renderer = plot.getRenderer();
        int nSeries = dataset.getSeriesCount();
        for (int k = 0; k < nSeries; k++) {
            renderer.setSeriesPaint(k, this.cores.get(k));
        }
    }

    public ChartPanel getChartPanel() {
        ChartPanel chartPanel = new ChartPanel(this.chart);
        chartPanel.setPreferredSize(new java.awt.Dimension(560, 560));
        return chartPanel;
    }
}
