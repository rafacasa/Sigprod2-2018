package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvaRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Religador;

/**
 *
 * @author Rafael Luiz Casa
 */
public class CriteriosReligador {

    public static Religador getReligadorTeste() {
        Religador teste = new Religador("modelo1", "fabricante1");
        CurvaRele ni = new CurvaRele("0.14", "0", "0.02", "1", "1000", "1", "0.01", "1", "0.01");
        CurvaRele mi = new CurvaRele("13.5", "0", "1", "1", "1000", "1", "0.01", "1", "0.01");
        CurvaRele ei = new CurvaRele("80", "0", "2", "1", "1000", "1", "0.01", "1", "0.01");
        teste.setnIFase(ni);
        teste.setmIFase(mi);
        teste.seteIFase(ei);
        teste.setnINeutro(ni);
        teste.setmINeutro(mi);
        teste.seteINeutro(ei);
        return teste;
    }
}
