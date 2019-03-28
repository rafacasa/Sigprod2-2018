package br.edu.ifrs.farroupilha.sigprod2.criterios;

import br.edu.ifrs.farroupilha.sigprod2.exceptions.AjusteImpossivelException;
import br.edu.ifrs.farroupilha.sigprod2.metricas.MetricasReleElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.AjusteRele;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Corrente;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.CurvasElo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Elo;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Ponto;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rede;
import br.edu.ifrs.farroupilha.sigprod2.backend.modelo.Rele;
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
    private Ponto pOrigem;
    private BigDecimal CTIFase;
    private BigDecimal CTINeutro;

    public Criterios_Rele_Elo(Rele relePai, Ponto pontoRede, Rede rede, Ponto pOrigem) {
        this.relePai = relePai;
        this.pontoRede = pontoRede;
        this.rede = rede;
        this.pOrigem = pOrigem;
        this.elosDisponiveis = new ArrayList<>(this.rede.getElosDisponiveis());
        this.CTIFase = new BigDecimal("0.2");
        this.CTINeutro = new BigDecimal("0.2");
        this.orderElos();
    }

    public List<MetricasReleElo> ajuste() throws AjusteImpossivelException {
        List<MetricasReleElo> metricas = new ArrayList<>();
        this.verificaElosDisponiveis();
        this.verificaCorrenteCarga();
        this.verificaIInrush();
        List<BigDecimal> alcances = this.calculaAlcances();
        List<Boolean> seletividades = this.calculaSeletividades();
        for (int i = 0; i < this.elosDisponiveis.size(); i++) {
            metricas.add(new MetricasReleElo(alcances.get(i), seletividades.get(i), this.elosDisponiveis.get(i)));
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
            Elo elo = it.next();
            iInrushElo = BigDecimal.valueOf(elo.correnteDoTempo(0.1, CurvasElo.MINIMA)); //PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
            if (iInrushElo.compareTo(iInrush) <= 0) {
                it.remove();
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
            elo = this.elosDisponiveis.get(i);
            i300 = BigDecimal.valueOf(elo.correnteDoTempo(300, CurvasElo.MAXIMA));
            alcances.add(this.calculaAlcance(iFTMinProximo, iFTMin2Camadas, i300));
        }
        return alcances;
    }

    private BigDecimal calculaAlcance(BigDecimal iFTMinProximo, BigDecimal iFTMin2Camadas, BigDecimal i300) {
        return iFTMinProximo.multiply(iFTMin2Camadas, MathContext.DECIMAL128).subtract(iFTMin2Camadas.multiply(i300, MathContext.DECIMAL128)).divide(i300.multiply(iFTMinProximo.subtract(iFTMin2Camadas), MathContext.DECIMAL128), MathContext.DECIMAL128);
    }

    private List<Boolean> calculaSeletividades() {
        List<Boolean> seletividade = new ArrayList<>();
        this.elosDisponiveis.forEach(elo -> {
            seletividade.add(this.calculaSeletividade(elo));
        });
        return seletividade;
    }

    private boolean calculaSeletividade(Elo elo) {
        return this.calculaSeletividade(elo, true) && this.calculaSeletividade(elo, false);
    }

    private boolean calculaSeletividade(Elo elo, boolean fase) {
        AjusteRele ajusteRele = this.getAjusteReleSeletividade(fase);
        BigDecimal i2 = this.getI2Seletividade(fase);
        BigDecimal i1 = this.getI1Seletividade(fase);
        BigDecimal tempoRele1 = ajusteRele.calculaTempo(i1);
        BigDecimal tempoRele2 = ajusteRele.calculaTempo(i2);
        BigDecimal tempoElo1 = BigDecimal.valueOf(elo.tempoDaCorrente(i1.doubleValue(), CurvasElo.MAXIMA));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
        BigDecimal tempoElo2 = BigDecimal.valueOf(elo.tempoDaCorrente(i2.doubleValue(), CurvasElo.MAXIMA));//PROBLEMA AO CONVERTER TODO O SISTEMA PARA BIGDECIMAL
        BigDecimal diff1 = tempoRele1.subtract(tempoElo1);
        BigDecimal diff2 = tempoRele2.subtract(tempoElo2);
        BigDecimal cti = this.getCTI(fase);
        LOGGER.debug("calculaSeletividade - diff1 = " + diff1.toString() + " diff2 = " + diff2);
        return diff1.compareTo(cti) >= 0 && diff2.compareTo(cti) >= 0;
    }

    private BigDecimal getCTI(boolean fase) {
        if (fase) {
            return this.CTIFase;
        } else {
            return this.CTINeutro;
        }
    }

    private BigDecimal getI2Seletividade(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pOrigem, Corrente.ICC3F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrentePonto(this.pOrigem, Corrente.ICCFT));
        }
    }

    private BigDecimal getI1Seletividade(boolean fase) {
        if (fase) {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pOrigem, Corrente.ICC2F));
        } else {
            return BigDecimal.valueOf(this.rede.buscaCorrenteMinimaProximoPonto(this.pOrigem, Corrente.ICCFTMIN));
        }
    }

    private AjusteRele getAjusteReleSeletividade(boolean fase) {
        if (fase) {
            return this.relePai.getAjusteFase();
        } else {
            return this.relePai.getAjusteNeutro();
        }
    }

    public BigDecimal getCTIFase() {
        return CTIFase;
    }

    public void setCTIFase(BigDecimal CTIFase) {
        this.CTIFase = CTIFase;
    }

    public BigDecimal getCTINeutro() {
        return CTINeutro;
    }

    public void setCTINeutro(BigDecimal CTINeutro) {
        this.CTINeutro = CTINeutro;
    }
}
