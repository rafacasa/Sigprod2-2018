/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.edu.ifrs.farroupilha.sigprod2.modelo;

/**
 *
 * @author Rafael Casa
 */
public enum TipoEquipamento {
    NENHUM(1), RELE(6), ELO(3), RELIGADOR(5), CHAVE(4);

    private final int codigo;

    TipoEquipamento(int codigo) {
        this.codigo = codigo;
    }

    int codigo() {
        return codigo;
    }

    public static TipoEquipamento converte(int codigo) {
        for (TipoEquipamento equip : TipoEquipamento.values()) {
            if (codigo == equip.codigo()) {
                return equip;
            }
        }
        throw new IllegalArgumentException("codigo invalido");
    }

    @Override
    public String toString() {
        switch (this) {
            case NENHUM:
                return "nenhum";
            case RELE:
                return "rele";
            case ELO:
                return "elo";
            case RELIGADOR:
                return "religador";
            case CHAVE:
                return "chave";
        }
        return null;
    }

}
