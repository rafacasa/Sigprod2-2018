package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.CorrenteForaDoAlcanceException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.TempoForaDoAlcanceException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Esta clsse representa um Elo de Tipo K
 *
 * @author Rafael Casa
 * @version 25/03/2016
 */
public class Elo implements Curvas, Equipamento {

    private int correnteNominal;
    private boolean preferencial;
    private ArrayList<PontoCurva> curvaDeMinimaFusao,
            curvaDeMaximaInterrupcao;
    private String dadosAjuste;

    public Elo(int correnteNominal, boolean preferencial) {
        this.correnteNominal = correnteNominal;
        this.preferencial = preferencial;
        this.curvaDeMinimaFusao = new ArrayList<>();
        this.curvaDeMaximaInterrupcao = new ArrayList<>();
    }

    public Elo() {
        this.curvaDeMinimaFusao = new ArrayList<>();
        this.curvaDeMaximaInterrupcao = new ArrayList<>();
    }

    public Elo(int correnteNominal, boolean preferencial, ArrayList<PontoCurva> curvaDeMinimaFusao, ArrayList<PontoCurva> curvadeMaximaInterrupcao) {
        this.correnteNominal = correnteNominal;
        this.preferencial = preferencial;
        this.curvaDeMinimaFusao = curvaDeMinimaFusao;
        this.curvaDeMaximaInterrupcao = curvadeMaximaInterrupcao;
    }

    public void addPontoDaCurvaDeMinimaFusao(PontoCurva ponto) {
        this.curvaDeMinimaFusao.add(ponto);
    }

    public void addPontoDaCurvaDeMaximaInterrupcao(PontoCurva ponto) {
        this.curvaDeMaximaInterrupcao.add(ponto);
    }

    public int getCorrenteNominal() {
        return correnteNominal;
    }

    public void setCorrenteNominal(int correnteNominal) {
        this.correnteNominal = correnteNominal;
    }

    public boolean isPreferencial() {
        return preferencial;
    }

    public void setPreferencial(boolean preferencial) {
        this.preferencial = preferencial;
    }

    public List<PontoCurva> getCurvaDeMinimaFusao() {
        return curvaDeMinimaFusao;
    }

    public void setCurvaDeMinimaFusao(ArrayList<PontoCurva> curvaDeMinimaFusao) {
        this.curvaDeMinimaFusao = curvaDeMinimaFusao;
    }

    public List<PontoCurva> getCurvaDeMaximaInterrupcao() {
        return curvaDeMaximaInterrupcao;
    }

    public void setCurvaDeMaximaInterrupcao(ArrayList<PontoCurva> curvadeMaximaInterrupcao) {
        this.curvaDeMaximaInterrupcao = curvadeMaximaInterrupcao;
    }

    public List<PontoCurva> getCurva(CurvasElo curva) {
        if (CurvasElo.MAXIMA.equals(curva)) {
            return this.getCurvaDeMaximaInterrupcao();
        } else {
            return this.getCurvaDeMinimaFusao();
        }
    }

    private List<List<Double>> getDadosCurvaMaxima() {
        List<PontoCurva> curva = this.getCurvaDeMaximaInterrupcao();
        List<Double> correntes = new ArrayList<>();
        List<Double> tempo = new ArrayList<>();
        curva.forEach(pc -> {
            correntes.add(pc.getCorrente());
            tempo.add(pc.getTempo());
        });
        return Arrays.asList(correntes, tempo);
    }

    private List<List<Double>> getDadosCurvaMinima() {
        List<PontoCurva> curva = this.getCurvaDeMinimaFusao();
        List<Double> correntes = new ArrayList<>();
        List<Double> tempo = new ArrayList<>();
        curva.forEach(pc -> {
            correntes.add(pc.getCorrente());
            tempo.add(pc.getTempo());
        });
        return Arrays.asList(correntes, tempo);
    }

    public List<List<Double>> getDadosCurva(CurvasElo curva) {
        if (CurvasElo.MAXIMA.equals(curva)) {
            return this.getDadosCurvaMaxima();
        } else {
            return this.getDadosCurvaMinima();
        }
    }

    @Override
    public String toString() {
        return String.valueOf(this.correnteNominal);
    }

    @Override
    public String getTitulo() {
        return "Grafico do Elo " + this.getCorrenteNominal();
    }

    @Override
    public int getQtdCurvas() {
        return 2;
    }

    @Override
    public String getNomeCurva(int index) {
        switch (index) {
            case 0:
                return "Curva de Mínima Fusão";
            case 1:
                return "Curva de Máxima Interrupção";
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public List<PontoCurva> getCurva(int index) {
        switch (index) {
            case 0:
                return getCurvaDeMinimaFusao();
            case 1:
                return getCurvaDeMaximaInterrupcao();
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public java.awt.Paint getCurvaPaint(int index) {
        switch (index) {
            case 0:
                return java.awt.Color.GREEN;
            case 1:
                return java.awt.Color.RED;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    @Override
    public double[][] getCurvaMatrix(int index) {
        List<PontoCurva> lista;
        double[][] array;
        switch (index) {
            case 0:
                lista = getCurvaDeMinimaFusao();
                array = new double[lista.size()][2];
                for (int i = 0; i < lista.size(); i++) {
                    array[i] = lista.get(i).getPontoArray();
                }
                return array;
            case 1:
                lista = getCurvaDeMaximaInterrupcao();
                array = new double[lista.size()][2];
                for (int i = 0; i < lista.size(); i++) {
                    array[i] = lista.get(i).getPontoArray();
                }
                return array;
            default:
                throw new ArrayIndexOutOfBoundsException(index);
        }
    }

    private double[][] getCurvaMatrix(CurvasElo curva) {
        if (curva == CurvasElo.MAXIMA) {
            return getCurvaMatrix(1);
        } else {
            return getCurvaMatrix(0);
        }
    }

    @Override
    public TipoEquipamento getTipoEquipamento() {
        return TipoEquipamento.ELO;
    }

    public double correnteDoTempo(double tempoEntrada, CurvasElo curva) throws TempoForaDoAlcanceException {
        return correnteDoTempo(getCurvaMatrix(curva), tempoEntrada);
    }

    private double correnteDoTempo(double[][] curva, double tempoEntrada) throws TempoForaDoAlcanceException {
        double corrente1 = 0, corrente2 = 0, tempo1 = 0, tempo2 = 0;
        boolean sai = false;
        for (int i = 0; i < curva.length; i++) {
            if (curva[i][1] == tempoEntrada) {
                return curva[i][0];
            } else {
                if ((tempoEntrada < curva[i][1]) && (tempoEntrada > curva[i + 1][1])) {
                    corrente1 = curva[i][0]; //corrente Menor
                    corrente2 = curva[i + 1][0]; //corrente Maior
                    tempo1 = curva[i][1]; //tempo Maior
                    tempo2 = curva[i + 1][1]; //tempo Menor
                    sai = true;
                    break;
                }
            }
        }
        if (sai) {
            //E estipula um valor de corrente para o tempoEntrada
            double a1 = ((tempo2 - tempo1) / (corrente2 - corrente1));
            double b1 = (tempo1 - (((tempo2 - tempo1) / (corrente2 - corrente1)) * corrente1));
            return ((tempoEntrada - b1) / a1);
        }
        double ultimoValor = curva[curva.length - 1][1];
        if (tempoEntrada > ultimoValor) {
            return curva[curva.length - 1][0];
        } else {
            throw new TempoForaDoAlcanceException(tempoEntrada > curva[0][1], "TEMPO ENTRADO: " + tempoEntrada);
        }
    }

    public double tempoDaCorrente(double correnteEntrada, CurvasElo curva) throws CorrenteForaDoAlcanceException {
        return tempoDaCorrente(getCurvaMatrix(curva), correnteEntrada);
    }

    private double tempoDaCorrente(double[][] curva, double correnteEntrada) throws CorrenteForaDoAlcanceException {
        double corrente1 = 0, corrente2 = 0, tempo1 = 0, tempo2 = 0;
        boolean sai = false;
        for (int i = 0; i < curva.length - 1; i++) {
            if ((correnteEntrada > curva[i][0]) && (correnteEntrada < curva[i + 1][0])) {
                corrente1 = curva[i][0]; //corrente Menor
                corrente2 = curva[i + 1][0]; //corrente Maior
                tempo1 = curva[i][1]; //tempo Maior
                tempo2 = curva[i + 1][1]; //tempo Menor
                sai = true;
                break;
            } else if (correnteEntrada == curva[i][0]) {
                return curva[i][1];
            }
        }
        if (sai) {
            //E estipula um valor de tempo para a correnteEntrada
            double a1 = ((tempo2 - tempo1) / (corrente2 - corrente1));
            double b1 = (tempo1 - (((tempo2 - tempo1) / (corrente2 - corrente1)) * corrente1));
            return (correnteEntrada * a1) + b1;
        }
        double ultimoValor = curva[curva.length - 1][0];
        if (correnteEntrada > ultimoValor) {
            return curva[curva.length - 1][1];
        } else {
            throw new CorrenteForaDoAlcanceException(correnteEntrada > curva[0][0], "CORRENTE ENTRADA: " + correnteEntrada);
        }
    }

    public double GetMenorCorrente(CurvasElo curva) {
        return GetMenorCorrente(getCurvaMatrix(curva));
    }

    private double GetMenorCorrente(double[][] curva) {
        double menorValor = curva[0][0];
        for (int i = 1; i < curva.length; i++) {
            if (curva[i][0] < menorValor) {
                menorValor = curva[i][0];
            }
        }
        return menorValor;
    }

    public double GetMenorTempo(CurvasElo curva) {
        return GetMenorTempo(getCurvaMatrix(curva));
    }

    private double GetMenorTempo(double[][] curva) {
        double menorValor = curva[0][1];
        for (int i = 1; i < curva.length; i++) {
            if (curva[i][1] < menorValor) {
                menorValor = curva[i][1];
            }
        }
        return menorValor;
    }

    public double GetMaiorCorrente(CurvasElo curva) {
        return GetMaiorCorrente(getCurvaMatrix(curva));
    }

    private double GetMaiorCorrente(double[][] curva) {
        double maiorValor = curva[0][0];
        for (int i = 1; i < curva.length; i++) {
            if (curva[i][0] > maiorValor) {
                maiorValor = curva[i][0];
            }
        }
        return maiorValor;
    }

    public double GetMaiorTempo(CurvasElo curva) {
        return GetMaiorTempo(getCurvaMatrix(curva));
    }

    private double GetMaiorTempo(double[][] curva) {
        double maiorValor = curva[0][1];
        for (int i = 1; i < curva.length; i++) {
            if (curva[i][1] > maiorValor) {
                maiorValor = curva[i][1];
            }
        }
        return maiorValor;
    }

    public String getDadosAjuste() {
        return dadosAjuste;
    }

    public void setDadosAjuste(String dadosAjuste) {
        this.dadosAjuste = dadosAjuste;
    }

}
