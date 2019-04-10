package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvaRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Religador;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.ValorATImposivelException;
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
public class CriteriosReleReligador {

    private static final Logger LOGGER = LogManager.getLogger(CriteriosReleReligador.class.getName());
    private Rele relePai;
    private Religador religador;
    private Ponto pontoRede;
    private Rede rede;
    private BigDecimal CTIFase = new BigDecimal("0.2");
    private BigDecimal CTINeutro = new BigDecimal("0.2");
    private BigDecimal fatorDesbalanco = new BigDecimal("0.3");
    private BigDecimal tempoMinCurvaRapida = new BigDecimal("0.1");

    public CriteriosReleReligador(Rele relePai, Ponto pontoRede, Rede rede, Religador religador) {
        this.relePai = relePai;
        this.pontoRede = pontoRede;
        this.rede = rede;
        this.religador = religador;
    }

    public void ajuste() {
        List<AjusteRele> ajustesFase = ajusteSeletividadeFase();
        List<AjusteRele> ajustesNeutro = ajusteSeletividadeNeutro();

        this.religador.setAjusteFase(ajustesFase.get(0));
        this.religador.setAjusteNeutro(ajustesNeutro.get(0));

        this.ajustarCurvaRapida();
    }

    private List<AjusteRele> ajusteSeletividadeFase() {
        LOGGER.traceEntry();
        List<BigDecimal> dados = this.calculaTemposReligador(true);
        BigDecimal limiteMaximo = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICC2F));
        BigDecimal limiteMinimo = BigDecimal.valueOf(this.rede.buscaCorrentePonto(pontoRede, Corrente.ICARGA));
        CurvaRele ni = this.religador.getnIFase();
        CurvaRele mi = this.religador.getmIFase();
        CurvaRele ei = this.religador.geteIFase();
        List<AjusteRele> ajustesPossiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAjustesPossiveis(ni, dados.get(0), dados.get(2), dados.get(1), dados.get(3), limiteMaximo, limiteMinimo));
        ajustesPossiveis.addAll(calculaAjustesPossiveis(mi, dados.get(0), dados.get(2), dados.get(1), dados.get(3), limiteMaximo, limiteMinimo));
        ajustesPossiveis.addAll(calculaAjustesPossiveis(ei, dados.get(0), dados.get(2), dados.get(1), dados.get(3), limiteMaximo, limiteMinimo));
        ajustesPossiveis.sort(null);
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<AjusteRele> ajusteSeletividadeNeutro() {
        LOGGER.traceEntry();
        List<BigDecimal> dados = this.calculaTemposReligador(false);
        BigDecimal limiteMaximo = BigDecimal.valueOf(this.rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN));
        BigDecimal limiteMinimo = this.fatorDesbalanco.multiply(BigDecimal.valueOf(this.rede.buscaCorrentePonto(pontoRede, Corrente.ICARGA)));
        CurvaRele ni = this.religador.getnINeutro();
        CurvaRele mi = this.religador.getmINeutro();
        CurvaRele ei = this.religador.geteINeutro();
        List<AjusteRele> ajustesPossiveis = new ArrayList<>();
        ajustesPossiveis.addAll(calculaAjustesPossiveis(ni, dados.get(0), dados.get(2), dados.get(1), dados.get(3), limiteMaximo, limiteMinimo));
        ajustesPossiveis.addAll(calculaAjustesPossiveis(mi, dados.get(0), dados.get(2), dados.get(1), dados.get(3), limiteMaximo, limiteMinimo));
        ajustesPossiveis.addAll(calculaAjustesPossiveis(ei, dados.get(0), dados.get(2), dados.get(1), dados.get(3), limiteMaximo, limiteMinimo));
        ajustesPossiveis.sort(null);
        return LOGGER.traceExit(ajustesPossiveis);
    }

    private List<BigDecimal> calculaTemposReligador(boolean fase) {
        AjusteRele ajusteRele = this.getAjusteReleSeletividade(fase);
        BigDecimal i2 = this.getI2Seletividade(fase);
        BigDecimal i1 = this.getI1Seletividade(fase);
        BigDecimal tempoRele1 = ajusteRele.calculaTempo(i1);
        BigDecimal tempoRele2 = ajusteRele.calculaTempo(i2);
        BigDecimal cti = this.getCTI(fase);
        BigDecimal tempoReligador1 = tempoRele1.subtract(cti);
        BigDecimal tempoReligador2 = tempoRele2.subtract(cti);
        return Arrays.asList(i1, tempoReligador1, i2, tempoReligador2);
    }

    private List<AjusteRele> calculaAjustesPossiveis(CurvaRele curva, BigDecimal corrente1, BigDecimal corrente2, BigDecimal tempo1, BigDecimal tempo2, BigDecimal limiteMaximo, BigDecimal limiteMinimo) {
        List<AjusteRele> ajustesPossiveis = new ArrayList<>();
        List<BigDecimal> ac = restringeAC(curva, limiteMaximo, limiteMinimo);
        for (int i = 0; i < ac.size(); i++) {
            BigDecimal d = ac.get(i);
            BigDecimal at = this.calculaAT(curva, d, corrente1, corrente2, tempo1, tempo2);
            try {
                at = this.verificaAT(curva, at);
            } catch (ValorATImposivelException ex) {
                LOGGER.catching(Level.TRACE, ex);
                continue;
            }
            BigDecimal fm = this.calcularFM(curva, d, at, corrente1, corrente2, tempo1, tempo2);
            AjusteRele metrica = new AjusteRele(fm, at, d, curva);
            ajustesPossiveis.add(metrica);
        }
        return ajustesPossiveis;
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

    private BigDecimal calculaAT(CurvaRele curva, BigDecimal ac, BigDecimal corrente1, BigDecimal corrente2, BigDecimal tempo1, BigDecimal tempo2) {
        LOGGER.traceEntry();
        BigDecimal a = curva.getA();
        BigDecimal b = curva.getB();
        BigDecimal p = curva.getP();

        LOGGER.trace("ENTROU NO MÉTODO CALCULA AT");

        BigDecimal at1 = corrente1.divide(ac, MathContext.DECIMAL128);
        at1 = BigDecimalMath.pow(at1, p, MathContext.DECIMAL128);
        at1 = at1.subtract(BigDecimal.ONE);
        at1 = a.divide(at1, MathContext.DECIMAL128);
        at1 = at1.add(b);
        at1 = tempo1.divide(at1, MathContext.DECIMAL128);

        BigDecimal at2 = corrente2.divide(ac, MathContext.DECIMAL128);
        at2 = BigDecimalMath.pow(at2, p, MathContext.DECIMAL128);
        at2 = at2.subtract(BigDecimal.ONE);
        at2 = a.divide(at2, MathContext.DECIMAL128);
        at2 = at2.add(b);
        at2 = tempo2.divide(at2, MathContext.DECIMAL128);

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

    private BigDecimal calcularFM(CurvaRele curva, BigDecimal ac, BigDecimal at, BigDecimal corrente1, BigDecimal corrente2, BigDecimal tempo1, BigDecimal tempo2) {
        LOGGER.traceEntry();
        BigDecimal t0 = calculaTempo(curva, ac, at, corrente1);
        BigDecimal t1 = tempo1;
        BigDecimal t2 = calculaTempo(curva, ac, at, corrente2);
        BigDecimal t3 = tempo2;
        LOGGER.trace("t0 (tempo da curva 1) - " + t0);
        LOGGER.trace("t1 - (tolerância máxima aceitável)" + t1);
        LOGGER.trace("t2 (tempo da curva 2)- " + t2);
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

    private void ajustarCurvaRapida() {
        try {
            this.ajustarCurvaRapida(true);
            this.ajustarCurvaRapida(false);
        } catch (ValorATImposivelException ex) {
            LOGGER.error("AT IMPOSSIVEL - " + ex.getLocalizedMessage());
        }
    }

    private void ajustarCurvaRapida(boolean fase) throws ValorATImposivelException {
        AjusteRele ajusteLenta = fase ? this.religador.getAjusteFase() : this.religador.getAjusteNeutro();
        AjusteRele ajusteRapida = new AjusteRele(ajusteLenta);
        BigDecimal iInrush = BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.IINRUSH));
        BigDecimal at;
        if (ajusteLenta.getAc().compareTo(iInrush) < 0) {
            at = iInrush.divide(ajusteLenta.getAc(), MathContext.DECIMAL128);
            at = BigDecimalMath.pow(at, ajusteLenta.getCurva().getP(), MathContext.DECIMAL128);
            at = at.subtract(BigDecimal.ONE);
            at = ajusteLenta.getCurva().getA().divide(at, MathContext.DECIMAL128);
            at = at.add(ajusteLenta.getCurva().getB());
            at = this.tempoMinCurvaRapida.divide(at, MathContext.DECIMAL128);
            ajusteRapida.setAt(this.verificaAT(ajusteLenta.getCurva(), at));
        } else {
            ajusteRapida.setAt(ajusteLenta.getCurva().getMenorAT());
        }
        if (fase) {
            this.religador.setAjusteRapidaFase(ajusteRapida);
        } else {
            this.religador.setAjusteRapidaNeutro(ajusteRapida);
        }
    }

    private BigDecimal getI2Seletividade(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.ICC3F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.ICCFT));
        }
    }

    private BigDecimal getI1Seletividade(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pontoRede, Corrente.ICC2F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pontoRede, Corrente.ICCFTMIN));
        }
    }

    private AjusteRele getAjusteReleSeletividade(boolean fase) {
        if (fase) {
            return this.relePai.getAjusteFase();
        } else {
            return this.relePai.getAjusteNeutro();
        }
    }

    private BigDecimal getCTI(boolean fase) {
        if (fase) {
            return this.CTIFase;
        } else {
            return this.CTINeutro;
        }
    }

    public BigDecimal getCTIFase() {
        return CTIFase;
    }

    public void setCTIFase(BigDecimal CTIFase) {
        this.CTIFase = CTIFase;
    }

    public BigDecimal getCTINeutro() {
        return CTINeutro;
    }

    public void setCTINeutro(BigDecimal CTINeutro) {
        this.CTINeutro = CTINeutro;
    }

    public BigDecimal getFatorDesbalanco() {
        return fatorDesbalanco;
    }

    public void setFatorDesbalanco(BigDecimal fatorDesbalanco) {
        this.fatorDesbalanco = fatorDesbalanco;
    }

}
