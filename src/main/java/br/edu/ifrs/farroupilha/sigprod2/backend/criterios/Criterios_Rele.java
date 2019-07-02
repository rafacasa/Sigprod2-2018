package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.dadospreajuste.DadosPreAjusteReleReligador;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.ValorATImposivelException;
import ch.obermuhlner.math.big.BigDecimalMath;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Criterios_Rele {

    private static final Logger LOGGER = LogManager.getLogger(Criterios_Rele.class.getName());
    private final Rede rede;
    private final Ponto ponto;
    private final Rele rele;
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

        //this.rele.setAjusteFase(ajustesFase.get(0));
        //this.rele.setAjusteNeutro(ajustesNeutro.get(0));

        this.rele.setAjusteFase(verificarPreAjusteFase(ajustesFase));
        this.rele.setAjusteNeutro(verificarPreAjusteNeutro(ajustesNeutro));

        this.preAjustesAcsDisponiveis();
    }

    private void preAjustesAcsDisponiveis() {
        List<BigDecimal> temp;
        List<ACDisponivel> tempAc = new ArrayList<>();

        for(ACDisponivel ac : this.rele.getAcsNIFase()) {
            temp = this.verificaPreAjustes(ac.getAjustesLenta(CurvaRele.NI), true);
            for (BigDecimal at : temp) {
                ac.removeAt(at);
            }
            if (ac.qtdAcertos() == 0) {
                tempAc.add(ac);
            }
        }

        for (ACDisponivel ac : tempAc) {
            this.rele.getAcsNIFase().remove(ac);
        }
        tempAc.clear();

        for(ACDisponivel ac : this.rele.getAcsMIFase()) {
            temp = this.verificaPreAjustes(ac.getAjustesLenta(CurvaRele.MI), true);
            for (BigDecimal at : temp) {
                ac.removeAt(at);
            }
            if (ac.qtdAcertos() == 0) {
                tempAc.add(ac);
            }
        }

        for (ACDisponivel ac : tempAc) {
            this.rele.getAcsMIFase().remove(ac);
        }
        tempAc.clear();

        for(ACDisponivel ac : this.rele.getAcsEIFase()) {
            temp = this.verificaPreAjustes(ac.getAjustesLenta(CurvaRele.EI), true);
            for (BigDecimal at : temp) {
                ac.removeAt(at);
            }
            if (ac.qtdAcertos() == 0) {
                tempAc.add(ac);
            }
        }

        for (ACDisponivel ac : tempAc) {
            this.rele.getAcsEIFase().remove(ac);
        }
        tempAc.clear();

        for(ACDisponivel ac : this.rele.getAcsNINeutro()) {
            temp = this.verificaPreAjustes(ac.getAjustesLenta(CurvaRele.NI), false);
            for (BigDecimal at : temp) {
                ac.removeAt(at);
            }
            if (ac.qtdAcertos() == 0) {
                tempAc.add(ac);
            }
        }

        for (ACDisponivel ac : tempAc) {
            this.rele.getAcsNINeutro().remove(ac);
        }
        tempAc.clear();

        for(ACDisponivel ac : this.rele.getAcsMINeutro()) {
            temp = this.verificaPreAjustes(ac.getAjustesLenta(CurvaRele.MI), false);
            for (BigDecimal at : temp) {
                ac.removeAt(at);
            }
            if (ac.qtdAcertos() == 0) {
                tempAc.add(ac);
            }
        }

        for (ACDisponivel ac : tempAc) {
            this.rele.getAcsMINeutro().remove(ac);
        }
        tempAc.clear();

        for(ACDisponivel ac : this.rele.getAcsEINeutro()) {
            temp = this.verificaPreAjustes(ac.getAjustesLenta(CurvaRele.EI), false);
            for (BigDecimal at : temp) {
                ac.removeAt(at);
            }
            if (ac.qtdAcertos() == 0) {
                tempAc.add(ac);
            }
        }

        for (ACDisponivel ac : tempAc) {
            this.rele.getAcsEINeutro().remove(ac);
        }
    }

    private List<BigDecimal> verificaPreAjustes(List<AjusteRele> ajustes, boolean fase) {
        Object oElo = this.ponto.getNode().getAttribute("preajusteELO");
        Object oRelig = this.ponto.getNode().getAttribute("preajusteRELIG");
        if (oElo != null && oRelig != null) {

        } else if (oElo != null) {
            return verificaPreAjustesElo(ajustes, oElo);
        } else if (oRelig != null) {
            return verificaPreAjustesReligador(ajustes, oRelig, fase);
        }
        return new ArrayList<>();
    }

    private List<BigDecimal> verificaPreAjustesElo(List<AjusteRele> ajustes, Object o) {
        return null;
    }

    private List<BigDecimal> verificaPreAjustesReligador(List<AjusteRele> ajustes, Object o, boolean fase) {
        List<DadosPreAjusteReleReligador> listaDados = (List<DadosPreAjusteReleReligador>) o;
        List<BigDecimal> retorno = new ArrayList<>();
        boolean ok = true;
        BigDecimal tempo1;
        BigDecimal tempo2;
        BigDecimal i1;
        BigDecimal i2;
        BigDecimal cti;
        int equipAbaixo = this.rede.qtdEquipamentosAbaixoMaiorCaminho(this.ponto);
        for (AjusteRele ajuste : ajustes) {
            for (DadosPreAjusteReleReligador dados : listaDados) {
                i1 = fase ? dados.getI1Fase() : dados.getI1Neutro();
                i2 = fase ? dados.getI2Fase() : dados.getI2Neutro();
                cti = fase ? dados.getCtiFase() : dados.getCtiNeutro();
                cti.multiply(new BigDecimal(equipAbaixo));
                tempo1 = ajuste.calculaTempo(i1);
                tempo2 = ajuste.calculaTempo(i2);
                LOGGER.debug("PRE AJUSTES ACS DISPONIVEIS RELE-RELIGADOR");
                LOGGER.debug("I1: " + i1);
                LOGGER.debug("tempo1: " + tempo1);
                LOGGER.debug("I2: " + i2);
                LOGGER.debug("tempo2: " + tempo2);
                ok = ok && (tempo1.compareTo(cti) > 0) && (tempo2.compareTo(cti) > 0);
            }
            if (!ok) {
                retorno.add(ajuste.getAt());
                ok = true;
            }
        }
        return retorno;
    }

    private AjusteRele verificarPreAjusteFase(List<AjusteRele> ajustesFase) {
        Object oElo = this.ponto.getNode().getAttribute("preajusteELO");
        Object oRelig = this.ponto.getNode().getAttribute("preajusteRELIG");

        if (oElo != null && oRelig != null) {

        } else if (oElo != null) {
            return this.verificaPreAjusteFaseElo(ajustesFase, oElo);
        } else if (oRelig != null) {
            return this.verificaPreAjusteFaseReligador(ajustesFase, oRelig);
        }
        return ajustesFase.get(0);
    }

    private AjusteRele verificaPreAjusteFaseElo(List<AjusteRele> ajustesFase, Object o) {
        throw new UnsupportedOperationException();
    }

    private AjusteRele verificaPreAjusteFaseReligador(List<AjusteRele> ajustesFase, Object o) {
        List<DadosPreAjusteReleReligador> listaDados = (List<DadosPreAjusteReleReligador>) o;
        boolean ok = true;
        BigDecimal tempo1;
        BigDecimal tempo2;
        int equipAbaixo = this.rede.qtdEquipamentosAbaixoMaiorCaminho(this.ponto);
        BigDecimal cti;
        LOGGER.info("EQUIPAMENTOS ABAIXO ELO = " + equipAbaixo);
        for (AjusteRele ajuste : ajustesFase) {
            for (DadosPreAjusteReleReligador dados : listaDados) {
                tempo1 = ajuste.calculaTempo(dados.getI1Fase());
                tempo2 = ajuste.calculaTempo(dados.getI2Fase());
                cti = dados.getCtiFase().multiply(new BigDecimal(equipAbaixo));
                LOGGER.info("PRE AJUSTES FASE RELE-RELIGADOR");
                LOGGER.info("I1: " + dados.getI1Fase());
                LOGGER.info("tempo1: " + tempo1);
                LOGGER.info("I2: " + dados.getI2Fase());
                LOGGER.info("tempo2: " + tempo2);
                ok = ok && (tempo1.compareTo(cti) > 0) && (tempo2.compareTo(cti) > 0);
            }
            if (ok) {
                return ajuste;
            } else {
                ok = true;
            }
        }
        LOGGER.error("NENHUM AJUSTE DE FASE PARA O RELE PASSA NOS PRE AJUSTES");
        return ajustesFase.get(0);
    }

    private AjusteRele verificarPreAjusteNeutro(List<AjusteRele> ajustesNeutro) {
        Object oElo = this.ponto.getNode().getAttribute("preajusteELO");
        Object oRelig = this.ponto.getNode().getAttribute("preajusteRELIG");

        if (oElo != null && oRelig != null) {

        } else if (oElo != null) {
            return this.verificaPreAjusteNeutroElo(ajustesNeutro, oElo);
        } else if (oRelig != null) {
            return this.verificaPreAjusteNeutroReligador(ajustesNeutro, oRelig);
        }
        return ajustesNeutro.get(0);
    }

    private AjusteRele verificaPreAjusteNeutroElo(List<AjusteRele> ajustesNeutro, Object o) {
        throw new UnsupportedOperationException();
    }

    private AjusteRele verificaPreAjusteNeutroReligador(List<AjusteRele> ajustesNeutro, Object o) {
        List<DadosPreAjusteReleReligador> listaDados = (List<DadosPreAjusteReleReligador>) o;
        boolean ok = true;
        BigDecimal tempo1;
        BigDecimal tempo2;
        int equipAbaixo = this.rede.qtdEquipamentosAbaixoMaiorCaminho(this.ponto);
        BigDecimal cti;
        LOGGER.info("EQUIPAMENTOS ABAIXO ELO = " + equipAbaixo);
        for (AjusteRele ajuste : ajustesNeutro) {
            for (DadosPreAjusteReleReligador dados : listaDados) {
                tempo1 = ajuste.calculaTempo(dados.getI1Neutro());
                tempo2 = ajuste.calculaTempo(dados.getI2Neutro());
                cti = dados.getCtiNeutro().multiply(new BigDecimal(equipAbaixo));
                LOGGER.info("PRE AJUSTES NEUTRO RELE-RELIGADOR");
                LOGGER.info("I1: " + dados.getI1Neutro());
                LOGGER.info("tempo1: " + tempo1);
                LOGGER.info("I2: " + dados.getI2Neutro());
                LOGGER.info("tempo2: " + tempo2);
                ok = ok && (tempo1.compareTo(cti) > 0) && (tempo2.compareTo(cti) > 0);
            }
            if (ok) {
                return ajuste;
            } else {
                ok = true;
            }
        }
        LOGGER.error("NENHUM AJUSTE DE NEUTRO PARA O RELE PASSA NOS PRE AJUSTES");
        return ajustesNeutro.get(0);
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
        List<ACDisponivel> acDisponiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAcerto(ni, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true, acDisponiveis));
        this.rele.setAcsNIFase(new ArrayList<>(acDisponiveis));
        acDisponiveis.clear();
        ajustesPossiveis.addAll(calculaAcerto(mi, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true, acDisponiveis));
        this.rele.setAcsMIFase(new ArrayList<>(acDisponiveis));
        acDisponiveis.clear();
        ajustesPossiveis.addAll(calculaAcerto(ei, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, true, acDisponiveis));
        this.rele.setAcsEIFase(new ArrayList<>(acDisponiveis));
        acDisponiveis.clear();
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
        List<ACDisponivel> acDisponiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAcerto(ni, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, false, acDisponiveis));
        this.rele.setAcsNINeutro(new ArrayList<>(acDisponiveis));
        acDisponiveis.clear();
        ajustesPossiveis.addAll(calculaAcerto(mi, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, false, acDisponiveis));
        this.rele.setAcsMINeutro(new ArrayList<>(acDisponiveis));
        acDisponiveis.clear();
        ajustesPossiveis.addAll(calculaAcerto(ei, iMinFFPP, iMinFFPR, limiteMaximo, limiteMinimo, false, acDisponiveis));
        this.rele.setAcsEINeutro(new ArrayList<>(acDisponiveis));
        acDisponiveis.clear();
        ajustesPossiveis.sort(null);
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<AjusteRele> calculaAcerto(CurvaRele curva, BigDecimal iMinFFPP, BigDecimal iMinFFPR, BigDecimal limiteMaximo, BigDecimal limiteMinimo, boolean fase, List<ACDisponivel> acDisponiveis) {
        LOGGER.traceEntry();
        List<AjusteRele> ajustesPossiveis = new ArrayList<>();
        List<BigDecimal> ac = restringeAC(curva, limiteMaximo, limiteMinimo);
        LOGGER.trace("RESTRINGIU OS ACS DISPONÍVEIS");
        LOGGER.debug("QTD AC - " + ac.size());
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
            acDisponiveis.add(aCDisponivel);
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

        if (primeiroAc.compareTo(limiteMinimo) < 0) {
            ac = restringeMinAC(ac, limiteMinimo);
        }

        if (limiteMaximo.compareTo(ultimoAc) < 0) {
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

        while (limiteMaximo.compareTo(atualAc) < 0) {
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
        CurvaRele ni = CurvaRele.NI;
        CurvaRele mi = CurvaRele.MI;
        CurvaRele ei = CurvaRele.EI;
        teste.setnIFase(ni);
        teste.setmIFase(mi);
        teste.seteIFase(ei);
        teste.setnINeutro(ni);
        teste.setmINeutro(mi);
        teste.seteINeutro(ei);
        return teste;
    }
}
