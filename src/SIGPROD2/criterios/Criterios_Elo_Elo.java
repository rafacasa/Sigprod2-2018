package sigprod2.criterios;

import sigprod2.metricas.Metricas_Elo_Elo;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import sigprod2.auxiliar.AjusteImpossivelException;
import sigprod2.modelo.Corrente;
import sigprod2.modelo.CurvasElo;
import sigprod2.modelo.Elo;
import sigprod2.modelo.Ponto;
import sigprod2.modelo.Rede;

public class Criterios_Elo_Elo {

    private List<Elo> elos;
    private Ponto pontoRede;
    private Rede rede;
    private Ponto pontoOrigem;
    private List<Metricas_Elo_Elo> metricas;

    public Criterios_Elo_Elo(Rede rede, Ponto origem, Ponto ajuste) {
        this.elos = new ArrayList<>(rede.getElosDisponiveis());
        this.pontoRede = ajuste;
        this.rede = rede;
        this.pontoOrigem = origem;

        this.orderElos();
    }

    public Criterios_Elo_Elo(List<Elo> elos, Ponto pontoRede, Rede rede, Ponto pontoOrigem) {
        this.elos = new ArrayList<>(elos);
        this.pontoRede = pontoRede;
        this.rede = rede;
        this.pontoOrigem = pontoOrigem;

        this.orderElos();
    }

    private void orderElos() {
        this.elos.sort((o1, o2) -> {
            return -Integer.compare(o1.getCorrenteNominal(), o2.getCorrenteNominal());
        });
    }

    private void verificaElosDisponiveis() throws AjusteImpossivelException {
        int numeroDeElosAbaixo = rede.contaElosAbaixo(pontoRede);
        int index2 = this.elos.size() - numeroDeElosAbaixo;
        int index = elos.indexOf(pontoOrigem.getEquipamentoInstalado());
        try {
            elos = elos.subList(index + 1, index2);
        } catch (IndexOutOfBoundsException ex) {
            throw new AjusteImpossivelException("Não existe elos suficientes para ajustar os elos abaixo deste ponto quando se exclui os elos ajustados nos pontos superiores");
        }
        if (elos.size() < 1) {
            throw new AjusteImpossivelException("Não existe elos suficientes para ajustar os elos abaixo deste ponto");
        }
    }

    private void verificaCriteriosIndiscutiveis() throws AjusteImpossivelException {
        int iElo;
        double iInrushMax;
        double iInrush = 0;
        double iCargaMax = this.pontoRede.getIcarga();

        Iterator<Elo> it = this.elos.iterator();

        while (it.hasNext()) {
            Elo elo = it.next();
            iElo = elo.getCorrenteNominal();
            iInrushMax = elo.correnteDoTempo(0.1, CurvasElo.MINIMA);
            if (iElo <= iCargaMax || iInrushMax <= iInrush) {
                it.remove();
            }
        }
        if (this.elos.isEmpty()) {
            throw new AjusteImpossivelException("Não exite elos que passam em todos os critérios indiscutíveis");
        }
    }

    private void iniciaMetricas() {
        this.metricas = new ArrayList<>(this.elos.size());
        this.elos.forEach((elo) -> {
            this.metricas.add(new Metricas_Elo_Elo(elo, pontoRede));
        });
    }

    private void metricas1() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double tProtetor, tProtetorProtegido1;
        double fatorMult = 0.75;
        double iCCMax = pontoRede.getMaxICC(1);
        double tProtegido = ((Elo) pontoOrigem.getEquipamentoInstalado()).tempoDaCorrente(iCCMax, CurvasElo.MINIMA);
        tProtegido *= fatorMult;

        double i300, iFTMinI300;
        double iFTMin = rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN);
        for (int i = 0; i < this.elos.size(); i++) {
            elo = this.elos.get(i);

            tProtetor = elo.tempoDaCorrente(iCCMax, CurvasElo.MAXIMA);
            tProtetorProtegido1 = (tProtegido - tProtetor) / tProtegido;

            i300 = elo.correnteDoTempo(300, CurvasElo.MAXIMA);
            iFTMinI300 = (iFTMin - i300) / iFTMin;

            metrica = this.metricas.get(i);
            metrica.settProtetorProtegido1(tProtetorProtegido1);
            metrica.setiFTMinI300(iFTMinI300);
        }
    }

    private void metricas2() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double i300, iFTMinSelI300;
        double iFTMinSel = rede.buscaCorrenteMinimaProximoPonto(pontoRede, Corrente.ICCFTMIN);

        for (int i = 0; i < this.elos.size(); i++) {
            elo = this.elos.get(i);

            i300 = elo.correnteDoTempo(300, CurvasElo.MAXIMA);
            iFTMinSelI300 = (iFTMinSel - i300) / iFTMinSel;

            metrica = this.metricas.get(i);
            metrica.setiFTMinSelI300(iFTMinSelI300);
        }
    }

    private void metricas3() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double tProtetor, tProtetorProtegido2;
        double fatorMult = 0.75;
        double iCCMax = pontoRede.getMaxICC(2);
        double tProtegido = ((Elo) pontoOrigem.getEquipamentoInstalado()).tempoDaCorrente(iCCMax, CurvasElo.MINIMA);
        tProtegido *= fatorMult;

        for (int i = 0; i < this.elos.size(); i++) {
            elo = this.elos.get(i);

            tProtetor = elo.tempoDaCorrente(iCCMax, CurvasElo.MAXIMA);
            tProtetorProtegido2 = (tProtegido - tProtetor) / tProtegido;

            metrica = this.metricas.get(i);
            metrica.settProtetorProtegido2(tProtetorProtegido2);
        }
    }

    private void calculaPorcentagens() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double i300, porcentagem;
        double iFTMinProximo = rede.buscaCorrenteMinimaProximoPonto(pontoRede, Corrente.ICCFTMIN);
        double iFTMin2Camadas = rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN);

        for (int i = 0; i < this.elos.size(); i++) {
            elo = this.elos.get(i);
            i300 = elo.correnteDoTempo(300, CurvasElo.MAXIMA);
            porcentagem = this.calculaPorcentagemCobertura(iFTMinProximo, iFTMin2Camadas, i300);

            metrica = this.metricas.get(i);
            metrica.setPorcentagemProtegida(porcentagem);
        }
    }

    private double calculaPorcentagemCobertura(double iFTMinProximo, double iFTMin2Camadas, double i300) {
        return ((iFTMinProximo * iFTMin2Camadas) - (iFTMin2Camadas * i300)) / (i300 * (iFTMinProximo - iFTMin2Camadas));
    }

    //Criar um metodo para cada metrica
    public List<Metricas_Elo_Elo> ajuste() throws AjusteImpossivelException {
        this.verificaElosDisponiveis();
        this.verificaCriteriosIndiscutiveis();
        this.iniciaMetricas();
        this.metricas1();
        this.metricas2();
        this.metricas3();
        this.calculaPorcentagens();
        return this.metricas;
    }
}
