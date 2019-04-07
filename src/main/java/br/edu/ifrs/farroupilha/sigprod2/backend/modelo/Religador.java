package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Religador extends Rele {

    private AjusteRele ajusteRapidaFase;
    private AjusteRele ajusteRapidaNeutro;

    public Religador(String modelo, String fabricante) {
        super(modelo, fabricante);
    }

    public AjusteRele getAjusteRapidaFase() {
        return ajusteRapidaFase;
    }

    public void setAjusteRapidaFase(AjusteRele ajusteRapidaFase) {
        this.ajusteRapidaFase = ajusteRapidaFase;
    }

    public AjusteRele getAjusteRapidaNeutro() {
        return ajusteRapidaNeutro;
    }

    public void setAjusteRapidaNeutro(AjusteRele ajusteRapidaNeutro) {
        this.ajusteRapidaNeutro = ajusteRapidaNeutro;
    }

    @Override
    public boolean ajustado() {
        return super.ajustado() && (this.ajusteRapidaFase != null) && (this.ajusteRapidaNeutro != null);
    }

    @Override
    public TipoEquipamento getTipoEquipamento() {
        return TipoEquipamento.RELIGADOR;
    }

    public List<List<BigDecimal>> getDadosAjusteRapida(boolean fase) {
        if (fase) {
            return this.ajusteRapidaFase.getPontosCurva();
        } else {
            return this.ajusteRapidaNeutro.getPontosCurva();
        }
    }

}
