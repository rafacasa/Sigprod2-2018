package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.ValorATImposivelException;
import ch.obermuhlner.math.big.BigDecimalMath;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 *
 * @author Rafael Luiz Casa
 */
public class CriteriosReligador {

    private static final Logger LOGGER = LogManager.getLogger(Criterios_Rele.class.getName());
    private Rede rede;
    private Ponto ponto;
    private Religador religador;
    private Criterios_Rele criteriosRele;
    private BigDecimal tempoMaxPPFase = new BigDecimal("5");
    private BigDecimal tempoMaxPRFase = new BigDecimal("10");
    private BigDecimal tempoMaxPPNeutro = new BigDecimal("2.5");
    private BigDecimal tempoMaxPRNeutro = new BigDecimal("5");
    private BigDecimal fatorDesbalanco = new BigDecimal("0.3");
    private BigDecimal tempoMinCurvaRapida = new BigDecimal("0.1");

    public CriteriosReligador(Rede rede, Ponto ponto, Religador religador) {
        this.rede = rede;
        this.ponto = ponto;
        this.religador = religador;
        this.criteriosRele = new Criterios_Rele(rede, ponto, religador, tempoMaxPPFase, tempoMaxPRFase, tempoMaxPPNeutro, tempoMaxPRNeutro, fatorDesbalanco);
    }

    public void ajuste() {
        this.criteriosRele.ajuste();
        this.ajustarCurvaRapida();
        this.preencherAcsDisponiveis();
    }

    private void preencherAcsDisponiveis() {
        this.religador.getAcsNIFase().forEach(ac -> {
            ac.addAtRapida(CurvaRele.NI.gerarAT());
        });
        this.religador.getAcsNINeutro().forEach(ac -> {
            ac.addAtRapida(CurvaRele.NI.gerarAT());
        });
        this.religador.getAcsMIFase().forEach(ac -> {
            ac.addAtRapida(CurvaRele.MI.gerarAT());
        });
        this.religador.getAcsMINeutro().forEach(ac -> {
            ac.addAtRapida(CurvaRele.MI.gerarAT());
        });
        this.religador.getAcsEIFase().forEach(ac -> {
            ac.addAtRapida(CurvaRele.EI.gerarAT());
        });
        this.religador.getAcsEINeutro().forEach(ac -> {
            ac.addAtRapida(CurvaRele.EI.gerarAT());
        });
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
        BigDecimal iInrush = BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.ponto, Corrente.IINRUSH)).multiply(BigDecimal.valueOf(1.1), MathContext.DECIMAL128);
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
            return LOGGER.traceExit(curva.gerarAT().get(indice));
        }
    }

    public static Religador getReligadorTeste() {
        Religador teste = new Religador("modelo1", "fabricante1");
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
