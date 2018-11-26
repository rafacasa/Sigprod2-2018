/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SIGPROD2.modelo;

/**
 *
 * @author Rafael Casa
 */
public class Trecho {
    private Ponto origem, destino;

    public Trecho(Ponto origem, Ponto destino) {
        this.origem = origem;
        this.destino = destino;
    }

    public Ponto getOrigem() {
        return origem;
    }

    public Ponto getDestino() {
        return destino;
    }

    @Override
    public String toString() {
        return origem.toString() + "-" + destino.toString();
    }
    
    

    
    
    
}
