package br.edu.ifrs.farroupilha.sigprod2.criterios;

import br.edu.ifrs.farroupilha.sigprod2.exceptions.ValorATImposivelException;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.modelo.CurvaRele;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rele;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Criterios_Rele {

    private static final Logger LOGGER = LogManager.getLogger(Criterios_Rele.class.getName());
    private Rede rede;
    private Ponto ponto;
    private Rele rele;
    private double tempoMaxPPFase = 5;
    private double tempoMaxPRFase = 10;
    private double tempoMaxPPNeutro = 5;
    private double tempoMaxPRNeutro = 10;

    public Criterios_Rele(Rede rede, Ponto ponto, Rele rele) {
        this.rede = rede;
        this.ponto = ponto;
        this.rele = rele;
    }

    private void calculaAcerto(CurvaRele curva, boolean fase) {
        LOGGER.trace("ENTROU NO MÉTODO CALCULA ACERTO");
        List<Double> ac = restringeAC(curva);
        LOGGER.trace("RESTRINGIU OS ACS DISPONÍVEIS");

    }

    private List<Double> restringeAC(CurvaRele curva) {
        LOGGER.trace("COMEÇA A EXECUÇÃO DO MÉTODO restringeAC");

        List<Double> ac = curva.gerarAC();
        double limiteMaximo = this.rede.buscaCorrenteMinima2Camadas(ponto, Corrente.ICC2F);
        double limiteMinimo = this.rede.buscaCorrentePonto(ponto, Corrente.ICARGA);
        double primeiroAc = ac.get(0);
        double ultimoAc = ac.get(ac.size() - 1);

        LOGGER.debug("QUANTIDADE DE ACS DA CURVA - " + ac.size());
        LOGGER.debug("LIMITE MAXIMO DO AC (CORRENTE MINIMA BIFASICA 2 CAMADAS) - " + limiteMaximo);
        LOGGER.debug("LIMITE MINIMO DO AC (CORRENTE DE CARGA NO PONTO) - " + limiteMinimo);
        LOGGER.debug("PRIMEIRO AC DA LISTA - " + primeiroAc);
        LOGGER.debug("ULTIMO AC DA LISTA - " + ultimoAc);

        if (primeiroAc <= limiteMinimo) {
            ac = restringeMinAC(ac, limiteMinimo);
        }

        if (limiteMaximo <= ultimoAc) {
            ac = restringeMaxAC(ac, limiteMaximo);
        }

        return ac;
    }

    private List<Double> restringeMinAC(List<Double> ac, double limiteMinimo) {
        LOGGER.trace("PRIMEIRO AC É MENOR QUE O LIMITE MÍNIMO (ENTROU NO IF)");
        int indice = 0;
        double atualAc = ac.get(0);

        while (atualAc <= limiteMinimo) {
            LOGGER.trace("REPETIÇÃO DO LAÇO WHILE");
            indice++;
            atualAc = ac.get(indice);
            LOGGER.debug("INDICE ATUAL - " + indice);
            LOGGER.debug("AC ATUAL - " + atualAc);
        }
        LOGGER.debug("INDICE MINIMO ESCOLHIDO - " + indice);
        ac = ac.subList(indice, ac.size());
        LOGGER.debug("NOVA QUANTIDADE DE ACS - " + ac.size());
        return ac;
    }

    private List<Double> restringeMaxAC(List<Double> ac, double limiteMaximo) {
        LOGGER.trace("LIMITE MAXIMO É MENOR QUE O ULTIMO AC (ENTROU NO IF)");
        int indice = ac.size() - 1;
        double atualAc = ac.get(indice);

        while (limiteMaximo <= atualAc) {
            LOGGER.trace("REPETIÇÃO DO LAÇO WHILE");
            indice--;
            atualAc = ac.get(indice);
            LOGGER.debug("INDICE ATUAL - " + indice);
            LOGGER.debug("AC ATUAL - " + atualAc);
        }
        LOGGER.debug("INDICE MÁXIMO ESCOLHIDO - " + indice);
        ac = ac.subList(0, indice + 1);
        LOGGER.debug("NOVA QUANTIDADE DE ACS - " + ac.size());
        return ac;
    }

    private double calculaAT(CurvaRele curva, double ac, double iMinFFPP, double iMinFFPR, boolean fase) {
        double a = curva.getA();
        double b = curva.getB();
        double p = curva.getP();
        double tempoMaxPP;
        double tempoMaxPR;

        if (fase) {
            tempoMaxPP = tempoMaxPPFase;
            tempoMaxPR = tempoMaxPRFase;
        } else {
            tempoMaxPP = tempoMaxPPNeutro;
            tempoMaxPR = tempoMaxPRNeutro;
        }

        LOGGER.trace("ENTROU NO MÉTODO CALCULA AT");

        double at1 = (tempoMaxPP / (a / ((Math.pow((iMinFFPP) / (ac), p) - 1) + b)));
        double at2 = (tempoMaxPR / (a / ((Math.pow((iMinFFPR) / (ac), p) - 1) + b)));

        LOGGER.debug("VALOR DE AT1 - " + at1);
        LOGGER.debug("VALOR DE AT2 - " + at2);

        double at = Collections.min(Arrays.asList(at1, at2));
        LOGGER.debug("MENOR AT - " + at);

        return at;
    }

    private void verificaAT(CurvaRele curva, double at) {
        LOGGER.trace("ENTROU NO MÉTODO verificaAT");
        double minAT = curva.getMenorAT();
        double maxAT = curva.getMaiorAT();
        if (at < minAT) {
            throw new ValorATImposivelException("Menor que mínimo");
            
        }

        if (at > maxAT) {

        }
    }

    public static Rele getReleTeste() {
        Rele teste = new Rele("modelo1", "fabricante1");
        CurvaRele ni = new CurvaRele(0.14, 0, 0.02, 1, 1000, 1, 0.01, 1, 0.01);
        CurvaRele mi = new CurvaRele(13.5, 0, 1, 1, 1000, 1, 0.01, 1, 0.01);
        CurvaRele ei = new CurvaRele(80, 0, 2, 1, 1000, 1, 0.01, 1, 0.01);
        teste.setnIFase(ni);
        teste.setmIFase(mi);
        teste.seteIFase(ei);
        return teste;
    }
}
