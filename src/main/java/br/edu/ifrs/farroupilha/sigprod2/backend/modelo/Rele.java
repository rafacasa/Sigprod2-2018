package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import java.math.BigDecimal;
import java.util.List;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Rele implements Equipamento {

    private CurvaRele nIFase;
    private CurvaRele mIFase;
    private CurvaRele eIFase;
    private CurvaRele nINeutro;
    private CurvaRele mINeutro;
    private CurvaRele eINeutro;
    private AjusteRele ajusteNeutro;
    private AjusteRele ajusteFase;
    private String modelo;
    private String fabricante;

    public Rele(String modelo, String fabricante) {
        this.modelo = modelo;
        this.fabricante = fabricante;
    }

    public CurvaRele getnIFase() {
        return nIFase;
    }

    public void setnIFase(CurvaRele nIFase) {
        this.nIFase = nIFase;
    }

    public CurvaRele getmIFase() {
        return mIFase;
    }

    public void setmIFase(CurvaRele mIFase) {
        this.mIFase = mIFase;
    }

    public CurvaRele geteIFase() {
        return eIFase;
    }

    public void seteIFase(CurvaRele eIFase) {
        this.eIFase = eIFase;
    }

    public CurvaRele getnINeutro() {
        return nINeutro;
    }

    public void setnINeutro(CurvaRele nINeutro) {
        this.nINeutro = nINeutro;
    }

    public CurvaRele getmINeutro() {
        return mINeutro;
    }

    public void setmINeutro(CurvaRele mINeutro) {
        this.mINeutro = mINeutro;
    }

    public CurvaRele geteINeutro() {
        return eINeutro;
    }

    public void seteINeutro(CurvaRele eINeutro) {
        this.eINeutro = eINeutro;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public String getFabricante() {
        return fabricante;
    }

    public void setFabricante(String fabricante) {
        this.fabricante = fabricante;
    }

    public AjusteRele getAjusteNeutro() {
        return ajusteNeutro;
    }

    public void setAjusteNeutro(AjusteRele ajusteNeutro) {
        this.ajusteNeutro = ajusteNeutro;
    }

    public AjusteRele getAjusteFase() {
        return ajusteFase;
    }

    public void setAjusteFase(AjusteRele ajusteFase) {
        this.ajusteFase = ajusteFase;
    }

    public boolean ajustado() {
        return (this.ajusteFase != null) && (this.ajusteNeutro != null);
    }

    public List<List<BigDecimal>> getDadosAjuste(boolean fase) {
        if (fase) {
            return this.ajusteFase.getPontosCurva();
        } else {
            return this.ajusteNeutro.getPontosCurva();
        }
    }

    @Override
    public TipoEquipamento getTipoEquipamento() {
        return TipoEquipamento.RELE;
    }

    public String getNomeCurva(CurvaRele curva, boolean fase) {
        if (fase) {
            if (this.nIFase.equals(curva)) {
                return "Curva Normalmente Inversa";
            } else if (this.mIFase.equals(curva)) {
                return "Curva Muito Inversa";
            } else if (this.eIFase.equals(curva)) {
                return "Curva Extremamente Inversa";
            } else {
                return null;
            }
        } else {
            if (this.nINeutro.equals(curva)) {
                return "Curva Normalmente Inversa";
            } else if (this.mINeutro.equals(curva)) {
                return "Curva Muito Inversa";
            } else if (this.eINeutro.equals(curva)) {
                return "Curva Extremamente Inversa";
            } else {
                return null;
            }
        }
    }
}
