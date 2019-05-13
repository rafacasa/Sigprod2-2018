package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.Metricas_Elo_Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.CorrenteForaDoAlcanceException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.TempoForaDoAlcanceException;
import org.apache.logging.log4j.LogManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Criterios_Elo_Elo {

    private static final org.apache.logging.log4j.Logger LOGGER = LogManager.getLogger(Criterios_Elo_Elo.class.getName());
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

    public List<Elo> getElosPossiveis() throws AjusteImpossivelException {
        this.verificaElosDisponiveis();
        this.verificaCriteriosIndiscutiveis();
        return this.elos;
    }

    private void orderElos() {
        this.elos.sort((o1, o2) -> -Integer.compare(o1.getCorrenteNominal(), o2.getCorrenteNominal()));
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
        double iInrush = this.pontoRede.getiInRush();
        double iCargaMax = this.pontoRede.getIcarga();

        Iterator<Elo> it = this.elos.iterator();

        while (it.hasNext()) {
            try {
                Elo elo = it.next();
                iElo = elo.getCorrenteNominal();
                iInrushMax = elo.correnteDoTempo(0.1, CurvasElo.MINIMA);
                if (iElo <= iCargaMax || iInrushMax <= iInrush) {
                    it.remove();
                }
            } catch (TempoForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE PARA O TEMPO DE 0.1");
                it.remove();
            }
        }
        if (this.elos.isEmpty()) {
            throw new AjusteImpossivelException("Não exite elos que passam em todos os critérios indiscutíveis");
        }
    }

    private void iniciaMetricas() {
        this.metricas = new ArrayList<>(this.elos.size());
        this.elos.forEach((elo) -> this.metricas.add(new Metricas_Elo_Elo(elo, pontoRede)));
    }

    private void metricas1() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double tProtetor, tProtetorProtegido1;
        double fatorMult = 0.75;
        double iCCMax = pontoRede.getMaxICC(1);
        double tProtegido;
        try {
            tProtegido = ((Elo) pontoOrigem.getEquipamentoInstalado()).tempoDaCorrente(iCCMax, CurvasElo.MINIMA);
            tProtegido *= fatorMult;
        } catch (CorrenteForaDoAlcanceException ex) {
            LOGGER.error("ELO NAO TEM ALCANCE " + ex.getLocalizedMessage());
            throw new RuntimeException("ELO NAO TEM ALCANCE " + ex.getLocalizedMessage());
        }

        double i300, iFTMinI300;
        double iFTMin = rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN);
        for (int i = 0; i < this.elos.size(); i++) {
            try {
                elo = this.elos.get(i);

                tProtetor = elo.tempoDaCorrente(iCCMax, CurvasElo.MAXIMA);
                tProtetorProtegido1 = (tProtegido - tProtetor) / tProtegido;

                i300 = elo.correnteDoTempo(300, CurvasElo.MAXIMA);
                iFTMinI300 = (iFTMin - i300) / iFTMin;

                metrica = this.metricas.get(i);
                metrica.settProtetorProtegido1(tProtetorProtegido1);
                metrica.setiFTMinI300(iFTMinI300);
            } catch (CorrenteForaDoAlcanceException | TempoForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE " + ex.getLocalizedMessage());
            }
        }
    }

    private void metricas2() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double i300, iFTMinSelI300;
        double iFTMinSel = rede.buscaCorrenteMinimaProximoPonto(pontoRede, Corrente.ICCFTMIN);

        for (int i = 0; i < this.elos.size(); i++) {
            try {
                elo = this.elos.get(i);

                i300 = elo.correnteDoTempo(300, CurvasElo.MAXIMA);
                iFTMinSelI300 = (iFTMinSel - i300) / iFTMinSel;

                metrica = this.metricas.get(i);
                metrica.setiFTMinSelI300(iFTMinSelI300);
            } catch (TempoForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE" + ex.getLocalizedMessage());
            }
        }
    }

    private void metricas3() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double tProtetor, tProtetorProtegido2;
        double fatorMult = 0.75;
        double iCCMax = pontoRede.getMaxICC(2);
        double tProtegido;
        try {
            tProtegido = ((Elo) pontoOrigem.getEquipamentoInstalado()).tempoDaCorrente(iCCMax, CurvasElo.MINIMA);
            tProtegido *= fatorMult;
        } catch (CorrenteForaDoAlcanceException ex) {
            LOGGER.error("Elo nao tem alcance " + ex.getLocalizedMessage());
            throw new RuntimeException("Elo nao tem alcance " + ex.getLocalizedMessage());
        }

        for (int i = 0; i < this.elos.size(); i++) {
            try {
                elo = this.elos.get(i);

                tProtetor = elo.tempoDaCorrente(iCCMax, CurvasElo.MAXIMA);
                tProtetorProtegido2 = (tProtegido - tProtetor) / tProtegido;

                metrica = this.metricas.get(i);
                metrica.settProtetorProtegido2(tProtetorProtegido2);
            } catch (CorrenteForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE " + ex.getLocalizedMessage());
            }
        }
    }

    private void calculaPorcentagens() {
        Elo elo;
        Metricas_Elo_Elo metrica;

        double i300, porcentagem;
        double iFTMinProximo = rede.buscaCorrenteMinimaProximoPonto(pontoRede, Corrente.ICCFTMIN);
        double iFTMin2Camadas = rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN);

        for (int i = 0; i < this.elos.size(); i++) {
            try {
                elo = this.elos.get(i);
                i300 = elo.correnteDoTempo(300, CurvasElo.MAXIMA);
                porcentagem = this.calculaPorcentagemCobertura(iFTMinProximo, iFTMin2Camadas, i300);

                metrica = this.metricas.get(i);
                metrica.setPorcentagemProtegida(porcentagem);
            } catch (TempoForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE " + ex.getLocalizedMessage());
            }
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
