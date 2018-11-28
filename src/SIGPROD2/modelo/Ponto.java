/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sigprod2.modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.graphstream.graph.Node;

/**
 *
 * @author Rafael Casa
 */
public class Ponto {
    private String nome;
    private double coordenadaX, coordenadaY;
    private TipoEquipamento tipoEquipamentoInstalado;
    private Equipamento equipamentoInstalado;
    private boolean fimdeTrecho;
    private double icc3f, icc2f, iccft, iccftmin, icarga;

    public Ponto(String nome) {
        this.nome = nome;
    }

    public Ponto(String nome, double coordenadaX, double coordenadaY, TipoEquipamento tipoEquipamentoInstalado) {
        this.nome = nome;
        this.coordenadaX = coordenadaX;
        this.coordenadaY = coordenadaY;
        this.tipoEquipamentoInstalado = tipoEquipamentoInstalado;
        this.equipamentoInstalado = null;
        this.fimdeTrecho = false;
        this.icc3f = 0;
        this.icc2f = 0;
        this.iccft = 0;
        this.iccftmin = 0;
        this.icarga = 0;
    }

    
    
    public Ponto(String nome, double coordenadaX, double coordenadaY, TipoEquipamento tipoEquipamentoInstalado, double icc3f, double icc2f, double iccft, double iccftmin, double icarga) {
        this.nome = nome;
        this.coordenadaX = coordenadaX;
        this.coordenadaY = coordenadaY;
        this.tipoEquipamentoInstalado = tipoEquipamentoInstalado;
        this.icc3f = icc3f;
        this.icc2f = icc2f;
        this.iccft = iccft;
        this.iccftmin = iccftmin;
        this.icarga = icarga;
        this.equipamentoInstalado = null;
        this.fimdeTrecho = false;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Ponto other = (Ponto) obj;
        return Objects.equals(this.nome, other.nome);
    }

    @Override
    public String toString() {
        return nome;
    }

    public String getNome() {
        return nome;
    }

    public double getCoordenadaX() {
        return coordenadaX;
    }

    public double getCoordenadaY() {
        return coordenadaY;
    }

    public TipoEquipamento getTipoEquipamentoInstalado() {
        return tipoEquipamentoInstalado;
    }

    public Equipamento getEquipamentoInstalado() {
        return equipamentoInstalado;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCoordenadaX(double coordenadaX) {
        this.coordenadaX = coordenadaX;
    }

    public void setCoordenadaY(double coordenadaY) {
        this.coordenadaY = coordenadaY;
    }

    public void setTipoEquipamentoInstalado(TipoEquipamento tipoEquipamentoInstalado) {
        this.tipoEquipamentoInstalado = tipoEquipamentoInstalado;
    }

    public void setEquipamentoInstalado(Equipamento equipamentoInstalado) {
        this.equipamentoInstalado = equipamentoInstalado;
    }

    public boolean isFimdeTrecho() {
        return fimdeTrecho;
    }

    public void setFimdeTrecho(boolean ehFimdeTrecho) {
        this.fimdeTrecho = ehFimdeTrecho;
    }

    public double getIcc3f() {
        return icc3f;
    }

    public void setIcc3f(double icc3f) {
        this.icc3f = icc3f;
    }

    public double getIcc2f() {
        return icc2f;
    }

    public void setIcc2f(double icc2f) {
        this.icc2f = icc2f;
    }

    public double getIccft() {
        return iccft;
    }

    public void setIccft(double iccft) {
        this.iccft = iccft;
    }

    public double getIccftmin() {
        return iccftmin;
    }

    public void setIccftmin(double iccftmin) {
        this.iccftmin = iccftmin;
    }

    public double getIcarga() {
        return icarga;
    }

    public void setIcarga(double icarga) {
        this.icarga = icarga;
    }
        
    public static void addAttribute(Node n, String key, Object o) {
        Object[] array = n.getArray(key);
        if (array == null) {
            Object o2 = n.getAttribute(key);
            if(o2 == null) {
                n.setAttribute(key, o);
            } else {
                array = new Object[2];
                array[0] = o2;
                array[1] = o;
                n.setAttribute(key, array);
            }  
        } else {
            Object[] copyOf = Arrays.copyOf(array, array.length + 1);
            copyOf[array.length] = o;
            n.setAttribute(key, copyOf);
        }
    }
    
    public static void removeAttribute(Node n, String key, Object o) {
        Object[] array = n.getArray(key);
        if (array == null) {
            Object o2 = n.getAttribute(key);
            if(o2 != null) {
                if(o2.equals(o)) {
                    n.removeAttribute(key);
                }
            }
        } else {
            Object[] copyOf = new Object[array.length - 1];
            int i = 0;
            for (Object object : array) {
                if(!o.equals(object)) {
                    copyOf[i] = object;
                    i++;
                }
            }
            n.setAttribute(key, copyOf);
        }
    }
    
    public double getMaxICC(int i) {
        List<Double> corr = new ArrayList<>();
        corr.add(icc3f);
        corr.add(icc2f);
        corr.add(iccft);
        if(i == 1) {
            return Collections.max(corr);
        } else {
            corr.remove(Collections.max(corr));
            return Collections.max(corr);
        }
    }
       
}
