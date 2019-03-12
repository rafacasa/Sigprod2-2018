package br.edu.ifrs.farroupilha.sigprod2.criterios;

import br.edu.ifrs.farroupilha.sigprod2.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.modelo.Rele;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Criterios_Rele {

    private Rede rede;
    private Ponto ponto;
    private Rele rele;

    public Criterios_Rele(Rede rede, Ponto ponto, Rele rele) {
        this.rede = rede;
        this.ponto = ponto;
        this.rele = rele;
    }
    
    
}
