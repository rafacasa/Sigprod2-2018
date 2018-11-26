package SIGPROD2.modelo;

import java.util.ArrayList;

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

    public ArrayList<PontoCurva> getCurvaDeMinimaFusao() {
        return curvaDeMinimaFusao;
    }

    public void setCurvaDeMinimaFusao(ArrayList<PontoCurva> curvaDeMinimaFusao) {
        this.curvaDeMinimaFusao = curvaDeMinimaFusao;
    }

    public ArrayList<PontoCurva> getCurvaDeMaximaInterrupcao() {
        return curvaDeMaximaInterrupcao;
    }

    public void setCurvaDeMaximaInterrupcao(ArrayList<PontoCurva> curvadeMaximaInterrupcao) {
        this.curvaDeMaximaInterrupcao = curvadeMaximaInterrupcao;
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
    public ArrayList<PontoCurva> getCurva(int index) {
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
        ArrayList<PontoCurva> lista;
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
    
    public double[][] getCurvaMatrix(CurvasElo curva) { 
        if(curva == CurvasElo.MAXIMA) {
            return getCurvaMatrix(1);
        } else {
            return getCurvaMatrix(0);
        }
    }

    @Override
    public TipoEquipamento getTipoEquipamento() {
        return TipoEquipamento.ELO;
    }
    
    public double correntedoTempo(double tempoEntrada, CurvasElo curva) {
        return correnteDoTempo(getCurvaMatrix(curva), tempoEntrada);
    }
    
    private double correnteDoTempo(double curva[][], double tempoEntrada) {
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
        if (sai == true) {
            //E estipula um valor de corrente para o tempoEntrada
            double a1 = ((tempo2 - tempo1) / (corrente2 - corrente1));
            double b1 = (tempo1 - (((tempo2 - tempo1) / (corrente2 - corrente1)) * corrente1));
            return ((tempoEntrada - b1) / a1);
        } else {
            return -1;
        }
    }

    public double tempoDaCorrente(double correnteEntrada, CurvasElo curva) {
        return tempoDaCorrente(getCurvaMatrix(curva), correnteEntrada);
    }
    
    private double tempoDaCorrente(double curva[][], double correnteEntrada) {
        double corrente1 = 0, corrente2 = 0, tempo1 = 0, tempo2 = 0;
        boolean sai = false;
        for (int i = 0; i < curva.length; i++) {
            if ((correnteEntrada > curva[i][0]) && (correnteEntrada < curva[i + 1][0])) {
                corrente1 = curva[i][0]; //corrente Menor
                corrente2 = curva[i + 1][0]; //corrente Maior
                tempo1 = curva[i][1]; //tempo Maior
                tempo2 = curva[i + 1][1]; //tempo Menor
                sai = true;
                break;
            } else if (correnteEntrada == curva[i][0]) {
                return curva[i][0];
            }
        }
        if (sai == true) {
            //E estipula um valor de tempo para a correnteEntrada
            double a1 = ((tempo2 - tempo1) / (corrente2 - corrente1));
            double b1 = (tempo1 - (((tempo2 - tempo1) / (corrente2 - corrente1)) * corrente1));
            return (correnteEntrada * a1) + b1;
        } else {
            return -1;
        }
    }

    public double GetMenorCorrente(CurvasElo curva) {
        return GetMenorCorrente(getCurvaMatrix(curva));
    }
    
    private double GetMenorCorrente(double curva[][]) {
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
    
    private double GetMenorTempo(double curva[][]) {
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
    
    private double GetMaiorCorrente(double curva[][]) {
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
    
    private double GetMaiorTempo(double curva[][]) {
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
