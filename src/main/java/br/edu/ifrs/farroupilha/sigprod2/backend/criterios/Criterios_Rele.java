package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.ACDisponivel;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.ValorATImposivelException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvaRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import ch.obermuhlner.math.big.BigDecimalMath;
import java.math.BigDecimal;
import java.math.MathContext;
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
    private final Rede rede;
    private final Ponto ponto;
    private final Rele rele;
    private List<ACDisponivel> listaAcFase;
    private List<ACDisponivel> listaAcNeutro;
    private BigDecimal tempoMaxPPFase = new BigDecimal("5");
    private BigDecimal tempoMaxPRFase = new BigDecimal("10");
    private BigDecimal tempoMaxPPNeutro = new BigDecimal("2.5");
    private BigDecimal tempoMaxPRNeutro = new BigDecimal("5");
    private BigDecimal fatorDesbalanco = new BigDecimal("0.3");

    public Criterios_Rele(Rede rede, Ponto ponto, Rele rele) {
        this.rede = rede;
        this.ponto = ponto;
        this.rele = rele;
        this.listaAcFase = new ArrayList<>();
        this.listaAcNeutro = new ArrayList<>();
    }

    public Criterios_Rele(Rede rede, Ponto ponto, Rele rele, BigDecimal tempoMaxPPFase, BigDecimal tempoMaxPRFase, BigDecimal tempoMaxPPNeutro, BigDecimal tempoMaxPRNeutro, BigDecimal fatorDesbalanco) {
        this.rede = rede;
        this.ponto = ponto;
        this.rele = rele;
        this.tempoMaxPPFase = tempoMaxPPFase;
        this.tempoMaxPRFase = tempoMaxPRFase;
        this.tempoMaxPPNeutro = tempoMaxPPNeutro;
        this.tempoMaxPRNeutro = tempoMaxPRNeutro;
        this.fatorDesbalanco = fatorDesbalanco;
    }

    public void ajuste() {
        List<AjusteRele> ajustesFase = ajustaFase();
        List<AjusteRele> ajustesNeutro = ajustaNeutro();

        this.rele.setAcsFase(this.listaAcFase);
        this.rele.setAcsNeutro(this.listaAcNeutro);
        this.rele.setAjusteFase(ajustesFase.get(0));
        this.rele.setAjusteNeutro(ajustesNeutro.get(0));
    }

    private List<AjusteRele> ajustaFase() {
        LOGGER.traceEntry();
        BigDecimal iMinFFPP = BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.ponto, Corrente.ICC2F));
        BigDecimal iMinFFPR = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(this.ponto, Corrente.ICC2F));
        BigDecimal limiteMaximo = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(ponto, Corrente.ICC2F)).divide(BigDecimal.valueOf(1.1), MathContext.DECIMAL128);
        BigDecimal limiteMinimo = BigDecimal.valueOf(this.rede.buscaCorrentePonto(ponto, Corrente.ICARGA)).multiply(BigDecimal.valueOf(1.1), MathContext.DECIMAL128);
        CurvaRele ni = this.rele.getnIFase();
        CurvaRele mi = this.rele.getmIFase();
        CurvaRele ei = this.rele.geteIFase();
        List<AjusteRele> ajustesPossiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAcerto(ni, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.addAll(calculaAcerto(mi, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.addAll(calculaAcerto(ei, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true));
        ajustesPossiveis.sort(null);
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<AjusteRele> ajustaNeutro() {
        LOGGER.traceEntry();
        BigDecimal iMinFFPP = BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.ponto, Corrente.ICCFTMIN));
        BigDecimal iMinFFPR = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(this.ponto, Corrente.ICCFTMIN));
        BigDecimal limiteMaximo = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(ponto, Corrente.ICCFTMIN)).divide(BigDecimal.valueOf(1.1), MathContext.DECIMAL128);
        BigDecimal limiteMinimo = this.fatorDesbalanco.multiply(BigDecimal.valueOf(this.rede.buscaCorrentePonto(ponto, Corrente.ICARGA))).multiply(BigDecimal.valueOf(1.1), MathContext.DECIMAL128);
        CurvaRele ni = this.rele.getnINeutro();
        CurvaRele mi = this.rele.getmINeutro();
        CurvaRele ei = this.rele.geteINeutro();
        List<AjusteRele> ajustesPossiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAcerto(ni, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, false));
        ajustesPossiveis.addAll(calculaAcerto(mi, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, false));
        ajustesPossiveis.addAll(calculaAcerto(ei, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, false));
        ajustesPossiveis.sort(null);
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<AjusteRele> calculaAcerto(CurvaRele curva, BigDecimal iMinFFPP, BigDecimal iMinFFPR, BigDecimal limiteMaximo, BigDecimal limiteMinimo, boolean fase) {
        LOGGER.traceEntry();
        List<AjusteRele> ajustesPossiveis = new ArrayList<>();
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
            AjusteRele metrica = new AjusteRele(fm, at, d, curva);
            ajustesPossiveis.add(metrica);
            ACDisponivel aCDisponivel = new ACDisponivel(d, at, curva.getMenorAT(), curva.getPassoAT());
            if (fase) {
                this.listaAcFase.add(aCDisponivel);
            } else {
                this.listaAcNeutro.add(aCDisponivel);
            }
        }
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<BigDecimal> restringeAC(CurvaRele curva, BigDecimal limiteMaximo, BigDecimal limiteMinimo) {
        LOGGER.traceEntry();

        List<BigDecimal> ac = curva.gerarAC();
        BigDecimal primeiroAc = ac.get(0);
        BigDecimal ultimoAc = ac.get(ac.size() - 1);

        LOGGER.trace("QUANTIDADE DE ACS DA CURVA - " + ac.size());
        LOGGER.trace("LIMITE MAXIMO DO AC (CORRENTE MINIMA BIFASICA 2 CAMADAS) - " + limiteMaximo);
        LOGGER.trace("LIMITE MINIMO DO AC (CORRENTE DE CARGA NO PONTO) - " + limiteMinimo);
        LOGGER.trace("PRIMEIRO AC DA LISTA - " + primeiroAc);
        LOGGER.trace("ULTIMO AC DA LISTA - " + ultimoAc);

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
        BigDecimal atualAc = ac.get(0);

        while (atualAc.compareTo(limiteMinimo) <= 0) {
            LOGGER.trace("REPETIÇÃO DO LAÇO WHILE");
            indice++;
            atualAc = ac.get(indice);
            LOGGER.trace("INDICE ATUAL - " + indice);
            LOGGER.trace("AC ATUAL - " + atualAc);
        }
        LOGGER.trace("INDICE MINIMO ESCOLHIDO - " + indice);
        List<BigDecimal> novoAC = ac.subList(indice, ac.size());
        LOGGER.trace("NOVA QUANTIDADE DE ACS - " + novoAC.size());
        return LOGGER.traceExit(novoAC);
    }

    private List<BigDecimal> restringeMaxAC(List<BigDecimal> ac, BigDecimal limiteMaximo) {
        LOGGER.traceEntry();
        int indice = ac.size() - 1;
        BigDecimal atualAc = ac.get(indice);

        while (limiteMaximo.compareTo(atualAc) <= 0) {
            LOGGER.trace("REPETIÇÃO DO LAÇO WHILE");
            indice--;
            atualAc = ac.get(indice);
            LOGGER.trace("INDICE ATUAL - " + indice);
            LOGGER.trace("AC ATUAL - " + atualAc);
        }
        LOGGER.trace("INDICE MÁXIMO ESCOLHIDO - " + indice);
        List<BigDecimal> novoAC = ac.subList(0, indice + 1);
        LOGGER.trace("NOVA QUANTIDADE DE ACS - " + novoAC.size());
        return LOGGER.traceExit(novoAC);
    }

    private BigDecimal calculaAT(CurvaRele curva, BigDecimal ac, BigDecimal iMinFFPP, BigDecimal iMinFFPR, boolean fase) {
        LOGGER.traceEntry();
        BigDecimal a = curva.getA();
        BigDecimal b = curva.getB();
        BigDecimal p = curva.getP();
        BigDecimal tempoMaxPP;
        BigDecimal tempoMaxPR;

        if (fase) {
            tempoMaxPP = tempoMaxPPFase;
            tempoMaxPR = tempoMaxPRFase;
        } else {
            tempoMaxPP = tempoMaxPPNeutro;
            tempoMaxPR = tempoMaxPRNeutro;
        }

        LOGGER.trace("ENTROU NO MÉTODO CALCULA AT");

        BigDecimal at1 = iMinFFPP.divide(ac, MathContext.DECIMAL128);
        at1 = BigDecimalMath.pow(at1, p, MathContext.DECIMAL128);
        at1 = at1.subtract(BigDecimal.ONE);
        at1 = a.divide(at1, MathContext.DECIMAL128);
        at1 = at1.add(b);
        at1 = tempoMaxPP.divide(at1, MathContext.DECIMAL128);

        BigDecimal at2 = iMinFFPR.divide(ac, MathContext.DECIMAL128);
        at2 = BigDecimalMath.pow(at2, p, MathContext.DECIMAL128);
        at2 = at2.subtract(BigDecimal.ONE);
        at2 = a.divide(at2, MathContext.DECIMAL128);
        at2 = at2.add(b);
        at2 = tempoMaxPR.divide(at2, MathContext.DECIMAL128);

        LOGGER.trace("VALOR DE AT1 - " + at1);
        LOGGER.trace("VALOR DE AT2 - " + at2);

        BigDecimal at = Collections.min(Arrays.asList(at1, at2));
        LOGGER.trace("MENOR AT - " + at);

        return LOGGER.traceExit(at);
    }

    private BigDecimal verificaAT(CurvaRele curva, BigDecimal at) throws ValorATImposivelException {
        LOGGER.traceEntry();
        BigDecimal minAT = curva.getMenorAT();
        BigDecimal maxAT = curva.getMaiorAT();
        BigDecimal passoAT = curva.getPassoAT();
        if (at.compareTo(minAT) < 0) {
            throw LOGGER.throwing(Level.TRACE, new ValorATImposivelException("Menor que mínimo"));
        }

        if (at.compareTo(maxAT) > 0) {
            throw LOGGER.throwing(Level.TRACE, new ValorATImposivelException("Maior que máximo"));
        }

        if (at.remainder(passoAT).compareTo(BigDecimal.ZERO) == 0) {
            return LOGGER.traceExit(at);
        } else {
            BigDecimal razao = at.divide(passoAT, MathContext.DECIMAL128);
            int indice = razao.intValue();
            LOGGER.trace("RAZÃO - " + razao);
            LOGGER.trace("INDICE - " + indice);
            return LOGGER.traceExit(curva.gerarAT().get(indice - 1));
        }
    }

    private BigDecimal calcularFM(CurvaRele curva, BigDecimal ac, BigDecimal at, BigDecimal iMinFFPP, BigDecimal iMinFFPR, boolean fase) {
        LOGGER.traceEntry();
        BigDecimal t0 = calculaTempo(curva, ac, at, iMinFFPR);
        BigDecimal t1 = fase ? this.tempoMaxPRFase : this.tempoMaxPRNeutro;
        BigDecimal t2 = calculaTempo(curva, ac, at, iMinFFPP);
        BigDecimal t3 = fase ? this.tempoMaxPPFase : this.tempoMaxPPNeutro;
        LOGGER.trace("t0 (tempo da curva iMinFFPR) - " + t0);
        LOGGER.trace("t1 - (tolerância máxima aceitável)" + t1);
        LOGGER.trace("t2 (tempo da curva iMinFFPP)- " + t2);
        LOGGER.trace("t3 - (tolerância máxima aceitável)" + t3);
        BigDecimal diff1 = t1.subtract(t0);
        BigDecimal diff2 = t3.subtract(t2);
        diff1 = diff1.pow(2);
        diff2 = diff2.pow(2);
        BigDecimal fm = diff1.add(diff2);
        return LOGGER.traceExit(fm);
    }

    private BigDecimal calculaTempo(CurvaRele curva, BigDecimal ac, BigDecimal at, BigDecimal corrente) {
        LOGGER.traceEntry();
        BigDecimal a = curva.getA();
        BigDecimal b = curva.getB();
        BigDecimal p = curva.getP();

        BigDecimal temp = corrente.divide(ac, MathContext.DECIMAL128);
        temp = BigDecimalMath.pow(temp, p, MathContext.DECIMAL128);
        temp = temp.subtract(BigDecimal.ONE);
        temp = a.divide(temp, MathContext.DECIMAL128);
        temp = temp.add(b);
        BigDecimal tempo = at.multiply(temp);
        return LOGGER.traceExit(tempo);
    }

    public static Rele getReleTeste() {
        Rele teste = new Rele("modelo1", "fabricante1");
        CurvaRele ni = new CurvaRele("0.14", "0", "0.02", "1", "1000", "1", "0.01", "1", "0.01");
        CurvaRele mi = new CurvaRele("13.5", "0", "1", "1", "1000", "1", "0.01", "1", "0.01");
        CurvaRele ei = new CurvaRele("80", "0", "2", "1", "1000", "1", "0.01", "1", "0.01");
        teste.setnIFase(ni);
        teste.setmIFase(mi);
        teste.seteIFase(ei);
        teste.setnINeutro(ni);
        teste.setmINeutro(mi);
        teste.seteINeutro(ei);
        return teste;
    }
}
