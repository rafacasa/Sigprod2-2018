package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import ch.obermuhlner.math.big.stream.BigDecimalStream;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class ACDisponivel {

    private BigDecimal ac;
    private List<BigDecimal> listaAt;
    private List<BigDecimal> listaAtRapida;

    public ACDisponivel(BigDecimal ac, BigDecimal maxAt, BigDecimal minAt, BigDecimal passoAt) {
        this.ac = ac;
        this.listaAt = new ArrayList<>();
        Iterator<BigDecimal> it = BigDecimalStream.rangeClosed(minAt, maxAt, passoAt, MathContext.DECIMAL128).iterator();
        while (it.hasNext()) {
            this.listaAt.add(it.next());
        }
    }

    public ACDisponivel(BigDecimal ac, BigDecimal maxAt, BigDecimal minAt, BigDecimal passoAt, boolean religador) {
        if(religador) {
            this.ac = ac;
            this.listaAt = new ArrayList<>();
            this.listaAtRapida = new ArrayList<>();
            Iterator<BigDecimal> it = BigDecimalStream.rangeClosed(minAt, maxAt, passoAt, MathContext.DECIMAL128).iterator();
            while (it.hasNext()) {
                BigDecimal temp = it.next();
                this.listaAt.add(temp);
                this.listaAtRapida.add(temp);
            }
        }
    }

    public ACDisponivel() {
    }

    public BigDecimal getAc() {
        return ac;
    }

    public List<BigDecimal> getListaAt() {
        return listaAt;
    }

    public List<BigDecimal> getListaAtRapida() {
        return this.listaAtRapida;
    }

    public void addAtRapida(List<BigDecimal> listaAtRapida) {
        this.listaAtRapida = Collections.unmodifiableList(listaAtRapida);
    }

    @Override
    public String toString() {
        return ac.toString();
    }
}
