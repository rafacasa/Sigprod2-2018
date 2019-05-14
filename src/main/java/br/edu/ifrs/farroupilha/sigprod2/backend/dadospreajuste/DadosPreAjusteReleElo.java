package br.edu.ifrs.farroupilha.sigprod2.backend.dadospreajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.CorrenteForaDoAlcanceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;

public class DadosPreAjusteReleElo {
    private static final Logger LOGGER = LogManager.getLogger(DadosPreAjusteReleElo.class.getName());
    private BigDecimal tempoEloFTPonto;
    private BigDecimal tempoEloFTMinAbaixo;
    private BigDecimal tempoEloFFFPonto;
    private BigDecimal tempoEloFFAbaixo;
    private BigDecimal correnteEloFTPonto;
    private BigDecimal correnteEloFTMinAbaixo;
    private BigDecimal correnteEloFFFPonto;
    private BigDecimal correnteEloFFAbaixo;

    public DadosPreAjusteReleElo(Elo menorElo, Ponto p, Rede rede) {
        correnteEloFTPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICCFT));
        correnteEloFTMinAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICCFTMIN));
        correnteEloFFFPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICC3F));
        correnteEloFFAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICC2F));
        try {
            this.tempoEloFTPonto = BigDecimal.valueOf(menorElo.tempoDaCorrente(correnteEloFTPonto.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            this.tempoEloFTMinAbaixo = BigDecimal.valueOf(menorElo.tempoDaCorrente(correnteEloFTMinAbaixo.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            this.tempoEloFFFPonto = BigDecimal.valueOf(menorElo.tempoDaCorrente(correnteEloFFFPonto.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            this.tempoEloFFAbaixo = BigDecimal.valueOf(menorElo.tempoDaCorrente(correnteEloFFAbaixo.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
        } catch (CorrenteForaDoAlcanceException e) {
            LOGGER.error("CORRENTE FORA DO ALCANCE " + e.getMessage());
            this.tempoEloFTPonto = BigDecimal.ZERO;
            this.tempoEloFTMinAbaixo = BigDecimal.ZERO;
            this.tempoEloFFFPonto = BigDecimal.ZERO;
            this.tempoEloFFAbaixo = BigDecimal.ZERO;
        }
    }

    public void addPreAjuste(Elo menorElo, Ponto p, Rede rede) {
        BigDecimal ftPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICCFT));
        BigDecimal ftMinAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICCFTMIN));
        BigDecimal fffPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICC3F));
        BigDecimal ffAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICC2F));
        try {
            BigDecimal tempoEloFTPonto = BigDecimal.valueOf(menorElo.tempoDaCorrente(ftPonto.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            BigDecimal tempoEloFTMinAbaixo = BigDecimal.valueOf(menorElo.tempoDaCorrente(ftMinAbaixo.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            BigDecimal tempoEloFFFPonto = BigDecimal.valueOf(menorElo.tempoDaCorrente(fffPonto.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            BigDecimal tempoEloFFAbaixo = BigDecimal.valueOf(menorElo.tempoDaCorrente(ffAbaixo.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);

            if (tempoEloFFAbaixo.compareTo(this.tempoEloFFAbaixo) > 0) {
                this.tempoEloFFAbaixo = tempoEloFFAbaixo;
                this.correnteEloFFAbaixo = ffAbaixo;
            }
            if (tempoEloFFFPonto.compareTo(this.tempoEloFFFPonto) > 0) {
                this.tempoEloFFFPonto = tempoEloFFFPonto;
                this.correnteEloFFFPonto = fffPonto;
            }
            if (tempoEloFTMinAbaixo.compareTo(this.tempoEloFTMinAbaixo) > 0) {
                this.tempoEloFTMinAbaixo = tempoEloFTMinAbaixo;
                this.correnteEloFTMinAbaixo = ftMinAbaixo;
            }
            if (tempoEloFTPonto.compareTo(this.tempoEloFTPonto) > 0) {
                this.tempoEloFTPonto = tempoEloFTPonto;
                this.correnteEloFTPonto = ftPonto;
            }
        } catch (CorrenteForaDoAlcanceException e) {
            LOGGER.error("CORRENTE FORA DO ALCANCE " + e.getMessage());
        }
    }

    public BigDecimal getTempoEloFTPonto() {
        return tempoEloFTPonto;
    }

    public BigDecimal getTempoEloFTMinAbaixo() {
        return tempoEloFTMinAbaixo;
    }

    public BigDecimal getTempoEloFFFPonto() {
        return tempoEloFFFPonto;
    }

    public BigDecimal getTempoEloFFAbaixo() {
        return tempoEloFFAbaixo;
    }

    public BigDecimal getCorrenteEloFTPonto() {
        return correnteEloFTPonto;
    }

    public BigDecimal getCorrenteEloFTMinAbaixo() {
        return correnteEloFTMinAbaixo;
    }

    public BigDecimal getCorrenteEloFFFPonto() {
        return correnteEloFFFPonto;
    }

    public BigDecimal getCorrenteEloFFAbaixo() {
        return correnteEloFFAbaixo;
    }
}
