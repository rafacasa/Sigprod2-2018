/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SIGPROD2.criterios;

import SIGPROD2.auxiliar.AjusteImpossivelException;
import SIGPROD2.modelo.Corrente;
import SIGPROD2.modelo.CurvasElo;
import SIGPROD2.modelo.Elo;
import SIGPROD2.modelo.Ponto;
import SIGPROD2.modelo.Rede;
import java.util.List;

/**
 *
 * @author Rafael Casa
 */
public class Criterios_Elo {

    public static Elo criterio_elo(List<Elo> elos, Ponto pontoRede, Rede rede) throws AjusteImpossivelException {

        elos.sort((o1, o2) -> {
            return -Integer.compare(o1.getCorrenteNominal(), o2.getCorrenteNominal());
        });

        double I300, Ielo, Iinrush, IinrushMax, IFTmin, IcargaMax;
        String dadosAjuste;
        Elo elo;

        int numeroDeElosAbaixo = rede.contaElosAbaixo(pontoRede);
        int limite = elos.size() - numeroDeElosAbaixo;

        for (int contador = 0; contador < limite; contador++) {
            elo = elos.get(contador);
            I300 = elo.correntedoTempo(300, CurvasElo.MAXIMA);
            Ielo = elo.getCorrenteNominal();
            IinrushMax = elo.correntedoTempo(0.1, CurvasElo.MINIMA);
            IFTmin = rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN);
            IcargaMax = pontoRede.getIcarga();
            Iinrush = 0;

            if (IFTmin > I300 && Ielo > IcargaMax && IinrushMax > Iinrush) {
                dadosAjuste = "<html>IFTmin = " + IFTmin + "<br>I300 = " + I300 + "<br>IcargaMax = " + IcargaMax + "<br>IinrushMax = " + IinrushMax + "<br>Iinrush = " + Iinrush + "<br>Elos Abaixo = " + numeroDeElosAbaixo + "</html>";
                elo.setDadosAjuste(dadosAjuste);
                return elo;
            }
        }
        throw new AjusteImpossivelException();
    }
    
    public static Elo criterio_elo_elo (List<Elo> elos, Ponto pontoRede, Rede rede, Ponto pontoOrigem) {
        elos.sort((o1, o2) -> {
            return -Integer.compare(o1.getCorrenteNominal(), o2.getCorrenteNominal());
        });
        
        int numeroDeElosAbaixo = rede.contaElosAbaixo(pontoRede);
        int index = elos.indexOf(pontoOrigem.getEquipamentoInstalado());
        
        elos = elos.subList(index+1, elos.size());
        
        if(elos.size() < numeroDeElosAbaixo) {
            
        }
        
        System.out.println(elos.size());
        
        
        
        
        
        return null;
    }
}
