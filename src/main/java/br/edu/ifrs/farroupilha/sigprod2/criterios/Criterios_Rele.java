package br.edu.ifrs.farroupilha.sigprod2.criterios;

import br.edu.ifrs.farroupilha.sigprod2.exceptions.ValorATImposivelException;
import br.edu.ifrs.farroupilha.sigprod2.metricas.Metricas_Rele;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.modelo.CurvaRele;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rele;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.Level;
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
    private BigDecimal tempoMaxPPFase = new BigDecimal("5");
    private BigDecimal tempoMaxPRFase = new BigDecimal("10");
    private BigDecimal tempoMaxPPNeutro = new BigDecimal("2.5");
    private BigDecimal tempoMaxPRNeutro = new BigDecimal("5");
    private BigDecimal fatorDesbalanco = new BigDecimal("0.3");

    public Criterios_Rele(Rede rede, Ponto ponto, Rele rele) {
        this.rede = rede;
        this.ponto = ponto;
        this.rele = rele;
    }

    public void ajuste() {
        List<Metricas_Rele> ajustesFase = ajustaFase();
        List<Metricas_Rele> ajustesNeutro = ajustaNeutro();

        this.rele.setAjusteFase(ajustesFase.get(0));
        this.rele.setAjusteNeutro(ajustesNeutro.get(0));
    }

    public List<Metricas_Rele> ajustaFase() {
        LOGGER.traceEntry();
        BigDecimal iMinFFPP = BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.ponto, Corrente.ICC2F));
        BigDecimal iMinFFPR = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(this.ponto, Corrente.ICC2F));
        BigDecimal limiteMaximo = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(ponto, Corrente.ICC2F));
        BigDecimal limiteMinimo = BigDecimal.valueOf(this.rede.buscaCorrentePonto(ponto, Corrente.ICARGA));
        CurvaRele ni = this.rele.getnIFase();
        CurvaRele mi = this.rele.getmIFase();
        CurvaRele ei = this.rele.geteIFase();
        List<Metricas_Rele> ajustesPossiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAcerto(ni, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.addAll(calculaAcerto(mi, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.addAll(calculaAcerto(ei, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.sort(null);
        return LOGGER.traceExit(ajustesPossiveis);
    }

    public List<Metricas_Rele> ajustaNeutro() {
        LOGGER.traceEntry();
        BigDecimal iMinFFPP = BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.ponto, Corrente.ICCFTMIN));
        BigDecimal iMinFFPR = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(this.ponto, Corrente.ICCFTMIN));
        BigDecimal limiteMaximo = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(ponto, Corrente.ICCFTMIN));
        BigDecimal limiteMinimo = this.fatorDesbalanco.multiply(BigDecimal.valueOf(this.rede.buscaCorrentePonto(ponto, Corrente.ICARGA)));
        CurvaRele ni = this.rele.getnINeutro();
        CurvaRele mi = this.rele.getmINeutro();
        CurvaRele ei = this.rele.geteINeutro();
        List<Metricas_Rele> ajustesPossiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAcerto(ni, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.addAll(calculaAcerto(mi, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.addAll(calculaAcerto(ei, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.sort(null);
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<Metricas_Rele> calculaAcerto(CurvaRele curva, BigDecimal iMinFFPP, BigDecimal iMinFFPR, BigDecimal limiteMaximo, BigDecimal limiteMinimo, boolean fase) {
        LOGGER.traceEntry();
        List<Metricas_Rele> ajustesPossiveis = new ArrayList<>();
        List<BigDecimal> ac = restringeAC(curva, limiteMaximo, limiteMinimo);
        LOGGER.trace("RESTRINGIU OS ACS DISPONÍVEIS");
        LOGGER.info("QTD AC - " + ac.size());
        for (int i = 0; i < ac.size(); i++) {
            BigDecimal d = ac.get(i);
            BigDecimal at = this.calculaAT(curva, d, iMinFFPP, iMinFFPR, fase);
            try {
                at = this.verificaAT(curva, at);
            } catch (ValorATImposivelException ex) {
                LOGGER.catching(Level.TRACE, ex);
                continue;
            }
            BigDecimal fm = this.calcularFM(curva, d, at, iMinFFPP, iMinFFPR, fase);
            Metricas_Rele metrica = new Metricas_Rele(fm, at, d, curva);
            ajustesPossiveis.add(metrica);
        }
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<BigDecimal> restringeAC(CurvaRele curva, BigDecimal limiteMaximo, BigDecimal limiteMinimo) {
        LOGGER.traceEntry();

        List<BigDecimal> ac = curva.gerarAC();
        BigDecimal primeiroAc = ac.get(0);
        BigDecimal ultimoAc = ac.get(ac.size() - 1);

        LOGGER.debug("QUANTIDADE DE ACS DA CURVA - " + ac.size());
        LOGGER.debug("LIMITE MAXIMO DO AC (CORRENTE MINIMA BIFASICA 2 CAMADAS) - " + limiteMaximo);
        LOGGER.debug("LIMITE MINIMO DO AC (CORRENTE DE CARGA NO PONTO) - " + limiteMinimo);
        LOGGER.debug("PRIMEIRO AC DA LISTA - " + primeiroAc);
        LOGGER.debug("ULTIMO AC DA LISTA - " + ultimoAc);

        if (primeiroAc.compareTo(limiteMinimo) <= 0) {
            ac = restringeMinAC(ac, limiteMinimo);
        }

        if (limiteMaximo.compareTo(ultimoAc) <= 0) {
            ac = restringeMaxAC(ac, limiteMaximo);
        }

        return LOGGER.traceExit(ac);
    }

    private List<BigDecimal> restringeMinAC(List<BigDecimal> ac, BigDecimal limiteMinimo) {
        LOGGER.traceEntry();
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
        return LOGGER.traceExit(ac);
    }

    private List<BigDecimal> restringeMaxAC(List<BigDecimal> ac, BigDecimal limiteMaximo) {
        LOGGER.traceEntry();
        int indice = ac.size() - 1;
        double atualAc = ac.get(indice);

        while (limiteMaximo <= atualAc) {
            LOGGER.trace("REPETIÇÃO DO LAÇO WHILE");
            indice--;
            atualAc = ac.get(indice);
            LOGGER.trace("INDICE ATUAL - " + indice);
            LOGGER.trace("AC ATUAL - " + atualAc);
        }
        LOGGER.debug("INDICE MÁXIMO ESCOLHIDO - " + indice);
        ac = ac.subList(0, indice + 1);
        LOGGER.debug("NOVA QUANTIDADE DE ACS - " + ac.size());
        return LOGGER.traceExit(ac);
    }

    private double calculaAT(CurvaRele curva, double ac, double iMinFFPP, double iMinFFPR, boolean fase) {
        LOGGER.traceEntry();
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

        return LOGGER.traceExit(at);
    }

    private double verificaAT(CurvaRele curva, double at) throws ValorATImposivelException {
        LOGGER.traceEntry();
        double minAT = curva.getMenorAT();
        double maxAT = curva.getMaiorAT();
        double passoAT = curva.getPassoAT();
        if (at < minAT) {
            throw LOGGER.throwing(Level.TRACE, new ValorATImposivelException("Menor que mínimo"));
        }

        if (at > maxAT) {
            throw LOGGER.throwing(Level.TRACE, new ValorATImposivelException("Maior que máximo"));
        }

        if (at % passoAT == 0) {
            return LOGGER.traceExit(at);
        } else {
            double razao = at / passoAT;
            int indice = (int) razao;
            LOGGER.debug("RAZÃO - " + razao);
            LOGGER.debug("INDICE - " + indice);
            return LOGGER.traceExit(curva.gerarAT().get(indice - 1));
        }
    }

    private double calcularFM(CurvaRele curva, double ac, double at, double iMinFFPP, double iMinFFPR, boolean fase) {
        LOGGER.traceEntry();
        double t0 = calculaTempo(curva, ac, at, iMinFFPR);
        double t1 = fase ? this.tempoMaxPRFase : this.tempoMaxPRNeutro;
        double t2 = calculaTempo(curva, ac, at, iMinFFPP);
        double t3 = fase ? this.tempoMaxPPFase : this.tempoMaxPPNeutro;
        LOGGER.debug("t0 (tempo da curva iMinFFPR) - " + t0);
        LOGGER.debug("t1 - (tolerância máxima aceitável)" + t1);
        LOGGER.debug("t2 (tempo da curva iMinFFPP)- " + t2);
        LOGGER.debug("t3 - (tolerância máxima aceitável)" + t3);
        double fm = Math.pow(t1 - t0, 2) + Math.pow(t3 - t2, 2);
        return LOGGER.traceExit(fm);
    }

    private double calculaTempo(CurvaRele curva, double ac, double at, double corrente) {
        LOGGER.traceEntry();
        double a = curva.getA();
        double b = curva.getB();
        double p = curva.getP();

        double tempo = at * ((a / ((Math.pow(corrente / ac, p)) - 1)) + b);
        return LOGGER.traceExit(tempo);
    }

    public static Rele getReleTeste() {
        Rele teste = new Rele("modelo1", "fabricante1");
        CurvaRele ni = new CurvaRele(0.14, 0, 0.02, 1, 1000, 1, 0.01, 1, 0.01);
        CurvaRele mi = new CurvaRele(13.5, 0, 1, 1, 1000, 1, 0.01, 1, 0.01);
        CurvaRele ei = new CurvaRele(80, 0, 2, 1, 1000, 1, 0.01, 1, 0.01);
        teste.setnIFase(ni);
        teste.setmIFase(mi);
        teste.seteIFase(ei);
        teste.setnINeutro(ni);
        teste.setmINeutro(mi);
        teste.seteINeutro(ei);
        return teste;
    }
}
