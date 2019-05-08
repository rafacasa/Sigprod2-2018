package br.edu.ifrs.farroupilha.sigprod2.backend.criterios;

import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.backend.metricas.MetricasReleElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvasElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.CorrenteForaDoAlcanceException;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.exceptions.TempoForaDoAlcanceException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author Rafael Luiz Casa
 */
public class Criterios_Rele_Elo {

    private static final Logger LOGGER = LogManager.getLogger(Criterios_Rele_Elo.class.getName());
    private Rele relePai;
    private List<Elo> elosDisponiveis;
    private Ponto pontoRede;
    private Rede rede;

    public Criterios_Rele_Elo(Rele relePai, Ponto pontoRede, Rede rede) {
        this.relePai = relePai;
        this.pontoRede = pontoRede;
        this.rede = rede;
        this.elosDisponiveis = new ArrayList<>(this.rede.getElosDisponiveis());
        this.orderElos();
    }

    public List<MetricasReleElo> ajuste() throws AjusteImpossivelException {
        List<MetricasReleElo> metricas = new ArrayList<>();
        this.verificaElosDisponiveis();
        this.verificaCorrenteCarga();
        this.verificaIInrush();
        List<BigDecimal> alcances = this.calculaAlcances();
        //List<Boolean> seletividades = this.calculaSeletividades();
        List<Boolean> seletividadesNeutro = this.calculaSeletividadesNeutro();
        List<Boolean> seletividadesFasePonto = this.calculaSeletividadesFasePonto();
        List<Boolean> seletividadesFaseAbaixo = this.calculaSeletividadesFaseAbaixo();
        for (int i = 0; i < this.elosDisponiveis.size(); i++) {
            metricas.add(new MetricasReleElo(alcances.get(i), seletividadesNeutro.get(i), seletividadesFasePonto.get(i), seletividadesFaseAbaixo.get(i), this.elosDisponiveis.get(i)));
        }
        return metricas;
    }

    private void orderElos() {
        this.elosDisponiveis.sort((o1, o2) -> {
            return -Integer.compare(o1.getCorrenteNominal(), o2.getCorrenteNominal());
        });
    }

    private void verificaElosDisponiveis() throws AjusteImpossivelException {
        int numeroDeElosAbaixo = rede.contaElosAbaixo(pontoRede);
        int index = this.elosDisponiveis.size() - numeroDeElosAbaixo;
        try {
            elosDisponiveis = elosDisponiveis.subList(0, index);
        } catch (IndexOutOfBoundsException ex) {
            throw new AjusteImpossivelException("Não existe elos suficientes para ajustar os elos abaixo deste ponto quando se exclui os elos ajustados nos pontos superiores");
        }
        if (elosDisponiveis.size() < 1) {
            throw new AjusteImpossivelException("Não existe elos suficientes para ajustar os elos abaixo deste ponto");
        }
    }

    private void verificaCorrenteCarga() throws AjusteImpossivelException {
        int iElo;
        BigDecimal iCarga = BigDecimal.valueOf(this.pontoRede.getIcarga()); //PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
        Iterator<Elo> it = this.elosDisponiveis.iterator();

        while (it.hasNext()) {
            Elo elo = it.next();
            iElo = elo.getCorrenteNominal();
            if (iElo <= iCarga.doubleValue()) {
                it.remove();
            }
        }

        if (this.elosDisponiveis.isEmpty()) {
            throw new AjusteImpossivelException("Não exite elos que passam em todos os critérios indiscutíveis");
        }
    }

    private void verificaIInrush() throws AjusteImpossivelException {
        BigDecimal iInrush = BigDecimal.ZERO;
        BigDecimal iInrushElo;
        Iterator<Elo> it = this.elosDisponiveis.iterator();

        while (it.hasNext()) {
            try {
                Elo elo = it.next();
                iInrushElo = BigDecimal.valueOf(elo.correnteDoTempo(0.1, CurvasElo.MINIMA)); //PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
                if (iInrushElo.compareTo(iInrush) <= 0) {
                    it.remove();
                }
            } catch (TempoForaDoAlcanceException ex) {
                it.remove();
                LOGGER.error("Elo nao tem alcance para o tempo 0.1 (IINRUSH)" + ex.getLocalizedMessage());
            }
        }

        if (this.elosDisponiveis.isEmpty()) {
            throw new AjusteImpossivelException("Não exite elos que passam em todos os critérios indiscutíveis");
        }
    }

    private List<BigDecimal> calculaAlcances() {
        Elo elo;
        List<BigDecimal> alcances = new ArrayList<>();
        BigDecimal i300;
        BigDecimal iFTMinProximo = BigDecimal.valueOf(rede.buscaCorrenteMinimaProximoPonto(pontoRede, Corrente.ICCFTMIN));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
        BigDecimal iFTMin2Camadas = BigDecimal.valueOf(rede.buscaCorrenteMinima2Camadas(pontoRede, Corrente.ICCFTMIN));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL

        for (int i = 0; i < this.elosDisponiveis.size(); i++) {
            try {
                elo = this.elosDisponiveis.get(i);
                i300 = BigDecimal.valueOf(elo.correnteDoTempo(300, CurvasElo.MAXIMA));
                alcances.add(this.calculaAlcance(iFTMinProximo, iFTMin2Camadas, i300));
            } catch (TempoForaDoAlcanceException ex) {
                LOGGER.error("ELO NAO TEM ALCANCE PARA O TEMPO 300 (ALCANCES)" + ex.getLocalizedMessage());
            }
        }
        return alcances;
    }

    private BigDecimal calculaAlcance(BigDecimal iFTMinProximo, BigDecimal iFTMin2Camadas, BigDecimal i300) {
        if (iFTMinProximo.compareTo(iFTMin2Camadas) == 0) {
            return BigDecimal.ZERO;
        }
        return iFTMinProximo.multiply(iFTMin2Camadas, MathContext.DECIMAL128).subtract(iFTMin2Camadas.multiply(i300, MathContext.DECIMAL128)).divide(i300.multiply(iFTMinProximo.subtract(iFTMin2Camadas), MathContext.DECIMAL128), MathContext.DECIMAL128);
    }

    private List<Boolean> calculaSeletividades() {
        List<Boolean> seletividade = new ArrayList<>();
        this.elosDisponiveis.forEach(elo -> {
            seletividade.add(this.calculaSeletividade(elo));
        });
        return seletividade;
    }

    private List<Boolean> calculaSeletividadesNeutro() {
        List<Boolean> seletividade = new ArrayList<>();
        this.elosDisponiveis.forEach(elo -> {
            seletividade.add(this.calculaSeletividade(elo, false));
        });
        return seletividade;
    }

    private List<Boolean> calculaSeletividadesFasePonto() {
        List<Boolean> seletividade = new ArrayList<>();
        this.elosDisponiveis.forEach(elo -> {
            seletividade.add(this.calculaSeletividadeFasePonto(elo, true));
        });
        return seletividade;
    }

    private List<Boolean> calculaSeletividadesFaseAbaixo() {
        List<Boolean> seletividade = new ArrayList<>();
        this.elosDisponiveis.forEach(elo -> {
            seletividade.add(this.calculaSeletividadeFaseAbaixo(elo, true));
        });
        return seletividade;
    }

    private boolean calculaSeletividade(Elo elo) {
        return this.calculaSeletividade(elo, true) && this.calculaSeletividade(elo, false);
    }

    private boolean calculaSeletividade(Elo elo, boolean fase) {
        try {
            AjusteRele ajusteRele = this.getAjusteReleSeletividade(fase);
            BigDecimal i2 = this.getI2Seletividade(fase);
            BigDecimal i1 = this.getI1Seletividade(fase);
            BigDecimal tempoRele1 = ajusteRele.calculaTempo(i1).multiply(BigDecimal.valueOf(0.9));
            BigDecimal tempoRele2 = ajusteRele.calculaTempo(i2).multiply(BigDecimal.valueOf(0.9));
            BigDecimal tempoElo1 = BigDecimal.valueOf(elo.tempoDaCorrente(i1.doubleValue(), CurvasElo.MAXIMA));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
            BigDecimal tempoElo2 = BigDecimal.valueOf(elo.tempoDaCorrente(i2.doubleValue(), CurvasElo.MAXIMA));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
            BigDecimal diff1 = tempoRele1.subtract(tempoElo1);
            BigDecimal diff2 = tempoRele2.subtract(tempoElo2);
            LOGGER.debug("calculaSeletividade - diff1 = " + diff1.toString() + " diff2 = " + diff2);
            return diff1.compareTo(BigDecimal.ZERO) >= 0 && diff2.compareTo(BigDecimal.ZERO) >= 0;
        } catch (CorrenteForaDoAlcanceException ex) {
            LOGGER.error("ELO NAO TEM ALCANCE PARA AS CORRENTES NECESSARIAS (SELETIVIDADE)" + ex.getLocalizedMessage());
            return false;
        }
    }

    private boolean calculaSeletividadeFasePonto(Elo elo, boolean original) {
        try {
            AjusteRele ajusteRele = this.getAjusteReleSeletividade(true);
            BigDecimal i2 = original ? this.getI2Seletividade(true) : BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.ICC2F));
            BigDecimal tempoRele2 = ajusteRele.calculaTempo(i2).multiply(BigDecimal.valueOf(0.9));
            BigDecimal tempoElo2 = BigDecimal.valueOf(elo.tempoDaCorrente(i2.doubleValue(), CurvasElo.MAXIMA));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
            BigDecimal diff2 = tempoRele2.subtract(tempoElo2);
            LOGGER.debug("calculaSeletividade - diff2 = " + diff2);
            return diff2.compareTo(BigDecimal.ZERO) >= 0;
        } catch (CorrenteForaDoAlcanceException ex) {
            LOGGER.error("ELO NAO TEM ALCANCE PARA A CORRENTE NECESSARIA (SELETIVIDADEFASEPONTO)" + ex.getLocalizedMessage());
            return false;
        }
    }

    private boolean calculaSeletividadeFaseAbaixo(Elo elo, boolean original) {
        try {
            AjusteRele ajusteRele = this.getAjusteReleSeletividade(true);
            BigDecimal i1 = original ? this.getI1Seletividade(true) : BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pontoRede, Corrente.ICC2F));
            BigDecimal tempoRele1 = ajusteRele.calculaTempo(i1).multiply(BigDecimal.valueOf(0.9));
            BigDecimal tempoElo1 = BigDecimal.valueOf(elo.tempoDaCorrente(i1.doubleValue(), CurvasElo.MAXIMA));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
            BigDecimal diff1 = tempoRele1.subtract(tempoElo1);
            LOGGER.debug("calculaSeletividade - diff1 = " + diff1.toString());
            return diff1.compareTo(BigDecimal.ZERO) >= 0;
        } catch (CorrenteForaDoAlcanceException ex) {
            LOGGER.error("ELO NAO TEM ALCANCE PARA A CORRENTE NECESSARIA (SELETIVIDADEFASEABAIXO)" + ex.getLocalizedMessage());
            return false;
        }
    }

    private BigDecimal getI2Seletividade(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.ICC3F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pontoRede, Corrente.ICCFT));
        }
    }

    private BigDecimal getI1Seletividade(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pontoRede, Corrente.ICC2F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pontoRede, Corrente.ICCFTMIN));
        }
    }

    private AjusteRele getAjusteReleSeletividade(boolean fase) {
        if (fase) {
            return this.relePai.getAjusteFase();
        } else {
            return this.relePai.getAjusteNeutro();
        }
    }
}
