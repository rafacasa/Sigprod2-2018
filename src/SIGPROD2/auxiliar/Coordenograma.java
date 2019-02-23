package sigprod2.auxiliar;

import java.util.ArrayList;
import java.util.List;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
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
        this.add(elo, CurvasElo.MINIMA);
        //Curva maxima
        this.add(elo, CurvasElo.MAXIMA);
        //hachura
        this.addHachura(elo);
    }

    public void add(Elo elo, CurvasElo curva) {
        XYSeries data = new XYSeries("elo" + elo.getCorrenteNominal() + curva.toString());
        List<PontoCurva> pontos = elo.getCurva(curva);
        pontos.forEach(ponto -> {
            data.add(ponto.getCorrente(), ponto.getTempo());
        });
        this.dataset.addSeries(data);
    }

    private void addHachura(Elo elo) {
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
            XYSeries hachuraAux = new XYSeries(k);
            hachuraAux.add(corrente1, tempoAux);
            hachuraAux.add(corrente2, tempoAux);
            hachura.add(hachuraAux);
            expAux -= expPasso;
        }
        hachura.forEach((series) -> {
            dataset.addSeries(series);
        });
    }

    //inserir reta na corrente
//    public void add(double corrente) {
//
//    }
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
