package br.edu.ifrs.farroupilha.sigprod2.backend.dadospreajuste;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.*;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.CorrenteForaDoAlcanceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.math.BigDecimal;
import java.math.MathContext;

public class DadosPreAjusteReligadorElo extends DadosPreAjusteReleElo {
    private static final Logger LOGGER = LogManager.getLogger(DadosPreAjusteReligadorElo.class.getName());
    private BigDecimal tempoMinimaEloFTPonto;
    private BigDecimal tempoMinimaEloFTMinAbaixo;
    private BigDecimal tempoMinimaEloFFFPonto;
    private BigDecimal tempoMinimaEloFFAbaixo;
    private Ponto ponto;

    public DadosPreAjusteReligadorElo(Elo maiorElo, Ponto p, Rede rede, BigDecimal fatorK, Elo menorElo) {
        super(menorElo, p, rede);
        this.ponto = p;
        BigDecimal ftPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICCFT));
        BigDecimal ftMinAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICCFTMIN));
        BigDecimal fffPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICC3F));
        BigDecimal ffAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICC2F));
        try {
            this.tempoMinimaEloFTPonto = BigDecimal.valueOf(maiorElo.tempoDaCorrente(ftPonto.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
            this.tempoMinimaEloFTMinAbaixo = BigDecimal.valueOf(maiorElo.tempoDaCorrente(ftMinAbaixo.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
            this.tempoMinimaEloFFFPonto = BigDecimal.valueOf(maiorElo.tempoDaCorrente(fffPonto.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
            this.tempoMinimaEloFFAbaixo = BigDecimal.valueOf(maiorElo.tempoDaCorrente(ffAbaixo.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
        } catch (CorrenteForaDoAlcanceException e) {
            LOGGER.error("CORRENTE FORA DO ALCANCE " + e.getMessage());
            this.tempoMinimaEloFTPonto = BigDecimal.ZERO;
            this.tempoMinimaEloFTMinAbaixo = BigDecimal.ZERO;
            this.tempoMinimaEloFFFPonto = BigDecimal.ZERO;
            this.tempoMinimaEloFFAbaixo = BigDecimal.ZERO;
        }
    }

    public void addPreAjuste(Elo maiorElo, Ponto p, Rede rede, BigDecimal fatorK, Elo menorElo) {
        super.addPreAjuste(menorElo, p, rede);
        BigDecimal ftPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICCFT));
        BigDecimal ftMinAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICCFTMIN));
        BigDecimal fffPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICC3F));
        BigDecimal ffAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICC2F));
        try {
            BigDecimal tempoMinimaEloFTPonto = BigDecimal.valueOf(maiorElo.tempoDaCorrente(ftPonto.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
            BigDecimal tempoMinimaEloFTMinAbaixo = BigDecimal.valueOf(maiorElo.tempoDaCorrente(ftMinAbaixo.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
            BigDecimal tempoMinimaEloFFFPonto = BigDecimal.valueOf(maiorElo.tempoDaCorrente(fffPonto.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
            BigDecimal tempoMinimaEloFFAbaixo = BigDecimal.valueOf(maiorElo.tempoDaCorrente(ffAbaixo.doubleValue(), CurvasElo.MINIMA)).divide(fatorK, MathContext.DECIMAL128);
            if (tempoMinimaEloFFAbaixo.compareTo(this.tempoMinimaEloFFAbaixo) > 0) {
                this.ponto = p;
                this.tempoMinimaEloFFAbaixo = tempoMinimaEloFFAbaixo;
            }
            if (tempoMinimaEloFFFPonto.compareTo(this.tempoMinimaEloFFFPonto) > 0) {
                this.ponto = p;
                this.tempoMinimaEloFFFPonto = tempoMinimaEloFFFPonto;
            }
            if (tempoMinimaEloFTMinAbaixo.compareTo(this.tempoMinimaEloFTMinAbaixo) > 0) {
                this.ponto = p;
                this.tempoMinimaEloFTMinAbaixo = tempoMinimaEloFTMinAbaixo;
            }
            if (tempoMinimaEloFTPonto.compareTo(this.tempoMinimaEloFTPonto) > 0) {
                this.ponto = p;
                this.tempoMinimaEloFTPonto = tempoMinimaEloFTPonto;
            }
        } catch (CorrenteForaDoAlcanceException e) {
            LOGGER.error("CORRENTE FORA DO ALCANCE " + e.getMessage());
        }
    }

    public BigDecimal getTempoMinimaEloFTPonto() {
        return tempoMinimaEloFTPonto;
    }

    public BigDecimal getTempoMinimaEloFTMinAbaixo() {
        return tempoMinimaEloFTMinAbaixo;
    }

    public BigDecimal getTempoMinimaEloFFFPonto() {
        return tempoMinimaEloFFFPonto;
    }

    public BigDecimal getTempoMinimaEloFFAbaixo() {
        return tempoMinimaEloFFAbaixo;
    }

    public Ponto getPonto() {
        return ponto;
    }
}
