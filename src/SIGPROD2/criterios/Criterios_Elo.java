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

    public static Elo criterio_elo_elo(List<Elo> elos, Ponto pontoRede, Rede rede, Ponto pontoOrigem) throws AjusteImpossivelException {
        elos.sort((o1, o2) -> {
            return -Integer.compare(o1.getCorrenteNominal(), o2.getCorrenteNominal());
        });
        
        System.out.println(elos);

        double fatorMult = 0.75;
        double I300, Ielo, Tprotetor, Tprotegido, Iinrush, IinrushMax, IFTmin, IcargaMax, iFTminSel;
        String dadosAjuste;
        Elo elo;

        int numeroDeElosAbaixo = rede.contaElosAbaixo(pontoRede);
        System.out.println("nuemroElosAbaixo - " + numeroDeElosAbaixo);
        int index = elos.indexOf(pontoOrigem.getEquipamentoInstalado());
        System.out.println("numeroIndex - " + index);

        elos = elos.subList(index + 1, elos.size());
        System.out.println(elos);

        if (elos.size() < numeroDeElosAbaixo) {
            throw new AjusteImpossivelException();
        }

        // ***** PRIMEIRO TESTE: CORRENTE FTmin de ALCANCE ***** //
        for (int contador = 0; contador < elos.size(); contador++) {
            elo = elos.get(contador);

            Ielo = elo.getCorrenteNominal();
            IcargaMax = pontoRede.getIcarga();
            Tprotetor = elo.tempoDaCorrente(IcargaMax, CurvasElo.MAXIMA);
            Tprotegido = ((Elo) pontoOrigem.getEquipamentoInstalado()).tempoDaCorrente(IcargaMax, CurvasElo.MINIMA); //CONFERIR SE EH CURVA MINIMA
            IinrushMax = elo.correntedoTempo(0.1, CurvasElo.MINIMA);
            I300 = elo.correntedoTempo(300, CurvasElo.MAXIMA);
            IFTmin = rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN);
            Iinrush = 0;

            if (Tprotetor < fatorMult * Tprotegido && IFTmin > I300 && Ielo > IcargaMax && IinrushMax > Iinrush) {
                dadosAjuste = "<html>Elo escolhido utilizando a corrente de FTmin de alcance<br>IFTmin = " + IFTmin + "<br>I300 = " + I300 + "<br>IcargaMax = " + IcargaMax + "<br>IinrushMax = " + IinrushMax + "<br>Iinrush = " + Iinrush + "<br>Tprotetor = " + Tprotetor + "<br>Tprotegido = " + Tprotegido + "<br>Elos Abaixo = " + numeroDeElosAbaixo + "</html>";
                elo.setDadosAjuste(dadosAjuste);
                return elo;
            }
        }

        // ***** SEGUNDO TESTE: CORRENTE FTmin de SELETIVIDADE ***** //
        for (int contador = 0; contador < elos.size(); contador++) {
            elo = elos.get(contador);
            Ielo = elo.getCorrenteNominal();
            IcargaMax = pontoRede.getIcarga();
            Tprotetor = elo.tempoDaCorrente(IcargaMax, CurvasElo.MAXIMA);
            Tprotegido = ((Elo) pontoOrigem.getEquipamentoInstalado()).tempoDaCorrente(IcargaMax, CurvasElo.MINIMA); //CONFERIR SE EH CURVA MINIMA
            IinrushMax = elo.correntedoTempo(0.1, CurvasElo.MINIMA);
            I300 = elo.correntedoTempo(300, CurvasElo.MAXIMA);
            iFTminSel = rede.buscaCorrenteMinimaProximoPonto(pontoRede, Corrente.ICCFTMIN);
            Iinrush = 0;

            if (Tprotetor < fatorMult * Tprotegido && iFTminSel > I300 && Ielo > IcargaMax && IinrushMax > Iinrush) {
                dadosAjuste = "<html>Elo escolhido utilizando a corrente FTmin de seletividade<br>IcargaMax = " + IcargaMax + "<br>Tprotetor = " + Tprotetor + "<br>Tprotegido = " + Tprotegido + "<br>IinrushMax = " + IinrushMax + "<br>I300 = " + I300 + "<br>iFTminSel = " + iFTminSel + "<br>Iinrush = " + Iinrush + "<br>Elos Abaixo = " + numeroDeElosAbaixo + "</html>";
                elo.setDadosAjuste(dadosAjuste);
                return elo;
            }
        }

        // ***** TERCEIRO TESTE: CORRENTE trifásica de ALCANCE E FTMAX PARA SELETIVIDADE ***** //
        for (int contador = 0; contador < elos.size(); contador++) {
            elo = elos.get(contador);
            Ielo = elo.getCorrenteNominal();
            IcargaMax = pontoRede.getIcarga();
            Tprotetor = elo.tempoDaCorrente(pontoRede.getIccft(), CurvasElo.MAXIMA);
            Tprotegido = ((Elo) pontoOrigem.getEquipamentoInstalado()).tempoDaCorrente(pontoRede.getIccft(), CurvasElo.MINIMA); //CONFERIR SE EH CURVA MINIMA
            IinrushMax = elo.correntedoTempo(0.1, CurvasElo.MINIMA);
            I300 = elo.correntedoTempo(300, CurvasElo.MAXIMA);
            IFTmin = rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN);
            Iinrush = 0;

            if (Tprotetor < fatorMult * Tprotegido && IFTmin > I300 && Ielo > IcargaMax && IinrushMax > Iinrush) {
                dadosAjuste = "<html>Elo escolhido utilizando FTmax para a seletividade<br>IcargaMax = " + IcargaMax + "<br>Tprotetor = " + Tprotetor + "<br>Tprotegido = " + Tprotegido + "<br>IinrushMax = " + IinrushMax + "<br>I300 = " + I300 + "<br>iFTmin = " + IFTmin + "<br>Iinrush = " + Iinrush + "<br>Elos Abaixo = " + numeroDeElosAbaixo + "</html>";
                elo.setDadosAjuste(dadosAjuste);
                return elo;
            }
        }
        throw new AjusteImpossivelException();
    }
}
