package br.edu.ifrs.farroupilha.sigprod2.backend.dadospreajuste;

import java.math.BigDecimal;

public class DadosPreAjusteReleReligador {
    private BigDecimal ctiFase;
    private BigDecimal ctiNeutro;
    private BigDecimal i1Fase;
    private BigDecimal i2Fase;
    private BigDecimal i1Neutro;
    private BigDecimal i2Neutro;

    public DadosPreAjusteReleReligador(BigDecimal ctiFase, BigDecimal ctiNeutro, BigDecimal i1Fase, BigDecimal i2Fase, BigDecimal i1Neutro, BigDecimal i2Neutro) {
        this.ctiFase = ctiFase;
        this.ctiNeutro = ctiNeutro;
        this.i1Fase = i1Fase;
        this.i2Fase = i2Fase;
        this.i1Neutro = i1Neutro;
        this.i2Neutro = i2Neutro;
    }

    public BigDecimal getCtiFase() {
        return ctiFase;
    }

    public BigDecimal getCtiNeutro() {
        return ctiNeutro;
    }

    public BigDecimal getI1Fase() {
        return i1Fase;
    }

    public BigDecimal getI2Fase() {
        return i2Fase;
    }

    public BigDecimal getI1Neutro() {
        return i1Neutro;
    }

    public BigDecimal getI2Neutro() {
        return i2Neutro;
    }
}