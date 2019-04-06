package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import ch.obermuhlner.math.big.BigDecimalMath;
import ch.obermuhlner.math.big.stream.BigDecimalStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class AjusteRele implements Comparable<AjusteRele> {

    private BigDecimal fm;
    private BigDecimal at;
    private BigDecimal ac;
    private CurvaRele curva;

    public AjusteRele(BigDecimal fm, BigDecimal at, BigDecimal ac, CurvaRele curva) {
        this.fm = fm;
        this.at = at;
        this.ac = ac;
        this.curva = curva;
    }

    public BigDecimal getFm() {
        return fm;
    }

    public BigDecimal getAt() {
        return at;
    }

    public BigDecimal getAc() {
        return ac;
    }

    public CurvaRele getCurva() {
        return curva;
    }

    @Override
    public int compareTo(AjusteRele o) {
        return this.fm.compareTo(o.fm);
    }

    public List<List<BigDecimal>> getPontosCurva() {
        List<BigDecimal> tempos = new ArrayList<>();
        List<BigDecimal> correntes = new ArrayList<>();
        BigDecimal minimaCorrente = this.ac.multiply(new BigDecimal("1.1"));
        BigDecimal maximaCorrente = this.ac.multiply(new BigDecimal("40"));
        BigDecimal qtdPassos = new BigDecimal("1000");
        BigDecimal passo = maximaCorrente.subtract(minimaCorrente).divide(qtdPassos, MathContext.DECIMAL128);
        BigDecimalStream.rangeClosed(minimaCorrente, maximaCorrente, passo, MathContext.DECIMAL128).forEach(i -> {
            correntes.add(i);
            tempos.add(calculaTempo(i));
        });
        return Arrays.asList(correntes, tempos);
    }

    public BigDecimal calculaTempo(BigDecimal corrente) {
        BigDecimal temp = corrente.divide(ac, MathContext.DECIMAL128);
        temp = BigDecimalMath.pow(temp, this.curva.getP(), MathContext.DECIMAL128);
        temp = temp.subtract(BigDecimal.ONE);
        temp = this.curva.getA().divide(temp, MathContext.DECIMAL128);
        temp = temp.add(this.curva.getB());
        return at.multiply(temp);
    }
}
