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

    public ACDisponivel(BigDecimal ac, List<BigDecimal> listaAt) {
        this.ac = ac;
        this.listaAt = Collections.unmodifiableList(listaAt);
    }

    public ACDisponivel(BigDecimal ac, BigDecimal maxAt, BigDecimal minAt, BigDecimal passoAt) {
        this.ac = ac;
        this.listaAt = new ArrayList<>();
        Iterator<BigDecimal> it = BigDecimalStream.rangeClosed(minAt, maxAt, passoAt, MathContext.DECIMAL128).iterator();
        while (it.hasNext()) {
            this.listaAt.add(it.next());
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

}
