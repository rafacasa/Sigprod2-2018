package br.edu.ifrs.farroupilha.sigprod2.backend.modelo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import org.graphstream.graph.Node;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Ponto {

    private String nome;
    private double coordenadaX, coordenadaY;
    private TipoEquipamento tipoEquipamentoInstalado;
    private Equipamento equipamentoInstalado;
    private boolean fimdeTrecho;
    private double icc3f, icc2f, iccft, iccftmin, icarga;
    private Node node;

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

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    public static void addAttribute(Node n, String key, Object o) {
        Object[] array = n.getArray(key);
        if (array == null) {
            Object o2 = n.getAttribute(key);
            if (o2 == null) {
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
            if (o2 != null) {
                if (o2.equals(o)) {
                    n.removeAttribute(key);
                }
            }
        } else {
            List<Object> lista = new ArrayList<>(Arrays.asList(array));
            if (lista.contains(o)) {
                lista.remove(o);
            }
            Object[] copyOf = lista.toArray();
            n.setAttribute(key, copyOf);
        }
    }

    public double getMaxICC(int i) {
        List<Double> corr = new ArrayList<>();
        corr.add(icc3f);
        corr.add(icc2f);
        corr.add(iccft);
        if (i == 1) {
            return Collections.max(corr);
        } else {
            corr.remove(Collections.max(corr));
            return Collections.max(corr);
        }
    }

    public void resetAtributos() {
        this.node.removeAttribute("ui.class");
        if (this.equipamentoInstalado != null) {
            this.node.setAttribute("ui.class", this.tipoEquipamentoInstalado.toString(), "equipamentoSelecionado");
        } else {
            this.node.setAttribute("ui.class", this.tipoEquipamentoInstalado.toString());
        }
    }

    public void resetAtributos(boolean b) {
        this.node.removeAttribute("ui.class");
        if (this.equipamentoInstalado != null) {
            this.node.setAttribute("ui.class", this.tipoEquipamentoInstalado.toString(), "equipamentoSelecionado", "equipamentoSendoAjustado");
        } else {
            this.node.setAttribute("ui.class", this.tipoEquipamentoInstalado.toString(), "equipamentoSendoAjustado");
        }
    }
}
