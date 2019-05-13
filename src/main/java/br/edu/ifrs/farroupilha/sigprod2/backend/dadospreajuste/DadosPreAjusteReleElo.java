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

    public DadosPreAjusteReleElo() {
    }

    public void calculaPreAjuste(Elo menorElo, Ponto p, Rede rede) {
        BigDecimal ftPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICCFT));
        BigDecimal ftMinAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICCFTMIN));
        BigDecimal fffPonto = BigDecimal.valueOf(rede.buscaCorrentePonto(p, Corrente.ICC3F));
        BigDecimal ffAbaixo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(p, Corrente.ICC2F));
        try {
            this.tempoEloFTPonto = BigDecimal.valueOf(menorElo.tempoDaCorrente(ftPonto.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            this.tempoEloFTMinAbaixo = BigDecimal.valueOf(menorElo.tempoDaCorrente(ftMinAbaixo.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            this.tempoEloFFFPonto = BigDecimal.valueOf(menorElo.tempoDaCorrente(fffPonto.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
            this.tempoEloFFAbaixo = BigDecimal.valueOf(menorElo.tempoDaCorrente(ffAbaixo.doubleValue(), CurvasElo.MAXIMA)).divide(new BigDecimal("0.9"), MathContext.DECIMAL128);
        } catch (CorrenteForaDoAlcanceException e) {
            LOGGER.error("CORRENTE FORA DO ALCANCE " + e.getMessage());
            this.tempoEloFTPonto = BigDecimal.ZERO;
            this.tempoEloFTMinAbaixo = BigDecimal.ZERO;
            this.tempoEloFFFPonto = BigDecimal.ZERO;
            this.tempoEloFFAbaixo = BigDecimal.ZERO;
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
}
