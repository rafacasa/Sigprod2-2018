package sigprod2.modelo;

import sigprod2.criterios.Criterios_Elo_Elo;
import sigprod2.Auxiliar.Arquivo;
import sigprod2.auxiliar.AjusteImpossivelException;
import sigprod2.auxiliar.NodeClickDefaultListener;
import sigprod2.auxiliar.NodeClickMouseManager;
import sigprod2.auxiliar.RedeNaoRadialException;
import sigprod2.criterios.Criterios_Elo;
import sigprod2.dao.EloKDao;
import sigprod2.gui.MainFrame;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.DefaultGraph;
import org.graphstream.ui.view.View;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;
import sigprod2.gui.dialogs.EscolheEloElo;
import sigprod2.gui.mainframepanels.Informacoes;
import sigprod2.metricas.Metricas_Elo_Elo;

/**
 *
 * @author Rafael Casa
 */
public class Rede {

    private List<Elo> elosDisponiveis;
    private List<Trecho> redeReduzida;
    private List<Trecho> trechosRede;
    private List<Ponto> pontosRede;
    private List<List<Ponto>> L;
    private Graph mapa;
    private Viewer viewer;
    private View view;
    private ViewerPipe fromViewer;
    private ViewerListener listener;
    private boolean loop;

    public Rede(Arquivo arquivoRede) throws SQLException {
        loop = true;
        String dadosRede = arquivoRede.lerArquivo();
        String[] linhas = dadosRede.split("\r");
        this.pontosRede = new ArrayList();
        String nome;
        double coordX, coordY, icc3f, icc2f, iccft, iccftmin, icarga;
        int equip, indice;
        Ponto origem, destino;
        Trecho trecho;
        this.trechosRede = new ArrayList();
        this.redeReduzida = new ArrayList<>();
        elosDisponiveis = EloKDao.buscarElos();
        elosDisponiveis.sort((o1, o2) -> {
            return -Integer.compare(o1.getCorrenteNominal(), o2.getCorrenteNominal());
        });

        for (String linha : linhas) {
            String[] dados = linha.split("\t");
            //origem
            nome = dados[2];
            coordX = Double.parseDouble(dados[3].replace(",", "."));
            coordY = Double.parseDouble(dados[4].replace(",", "."));
            indice = pontosRede.indexOf(new Ponto(nome));
            if (indice == -1) {
                origem = new Ponto(nome, coordX, coordY, TipoEquipamento.NENHUM);
                pontosRede.add(origem);
            } else {
                origem = pontosRede.get(indice);
            }
            //destino
            nome = dados[5];
            coordX = Double.parseDouble(dados[6].replace(",", "."));
            coordY = Double.parseDouble(dados[7].replace(",", "."));
            equip = Integer.parseInt(dados[9]);
            icc3f = Double.parseDouble(dados[21].replace(",", "."));
            icc2f = Double.parseDouble(dados[22].replace(",", "."));
            iccft = Double.parseDouble(dados[23].replace(",", "."));
            iccftmin = Double.parseDouble(dados[24].replace(",", "."));
            icarga = Double.parseDouble(dados[25].replace(",", "."));
            indice = pontosRede.indexOf(new Ponto(nome));
            if (indice == -1) {
                destino = new Ponto(nome, coordX, coordY, TipoEquipamento.converte(equip), icc3f, icc2f, iccft, iccftmin, icarga);
                pontosRede.add(destino);
            } else {
                destino = pontosRede.get(indice);
            }
            //cria trecho
            trecho = new Trecho(origem, destino);
            trechosRede.add(trecho);
        }

        this.geraMapa();
        this.order();
        this.verificaFimdeTrechos();
        this.criaRedeReduzida();
        //Rede.verMapa(this.redeReduzida);
    }

    private void geraMapa() {
        this.mapa = new DefaultGraph("Mapa", true, false, this.pontosRede.size(), this.trechosRede.size());

        this.pontosRede.forEach(this::inserePontoMapa);

        this.trechosRede.forEach(this::insereTrechoMapa);

        File f = new File("estilo.css");
        String uri = "url(" + f.toURI().toString() + ")";
        this.mapa.setAttribute("ui.stylesheet", uri);

    }

    private void inserePontoMapa(Ponto p) {
        Node n = this.mapa.addNode(p.toString());
        n.setAttribute("xyz", p.getCoordenadaX(), p.getCoordenadaY(), 0);
        n.setAttribute("ui.class", p.getTipoEquipamentoInstalado().toString());
        n.setAttribute("classe", p);
    }

    private void insereTrechoMapa(Trecho t) {
        this.mapa.addEdge(t.toString(), t.getOrigem().toString(), t.getDestino().toString()).setAttribute("classe", t);
    }

    public void mostraMapa() {
        this.mapa.display(false);
    }

    public void displayGraph(MainFrame frame) {
        this.viewer = this.mapa.display(false);

        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        fromViewer = viewer.newViewerPipe();
        NodeClickDefaultListener listener = new NodeClickDefaultListener(frame);
        fromViewer.addViewerListener(listener);
        fromViewer.addSink(this.mapa);

        viewer.getDefaultView().setMouseManager(new NodeClickMouseManager(listener));

        while (listener.loop) {
            fromViewer.pump();

        }
    }

    public View getMapaView(MainFrame frame) {
        viewer = this.mapa.display(false);

        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);
        view = viewer.getDefaultView();
        view.openInAFrame(false);

        fromViewer = viewer.newViewerPipe();
        listener = new NodeClickDefaultListener(frame);

        view.setMouseManager(new NodeClickMouseManager(listener));

        return view;
    }

    public void setListener(ViewerListener list) {
        listener = list;
        view.setMouseManager(new NodeClickMouseManager(listener));
    }

    public boolean isLoop() {
        return loop;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void loopPump() {
        while (loop) {
            fromViewer.pump();
        }
    }

    public List<Trecho> getTrechosRede() {
        return trechosRede;
    }

    public List<Ponto> getPontosRede() {
        return pontosRede;
    }

    public Graph getMapa() {
        return mapa;
    }

    public Ponto getPonto(String id) {
        for (Ponto p : this.pontosRede) {
            if (p.getNome().equals(id)) {
                return p;
            }
        }
        return null;
    }

    private void order() {
        List<Trecho> novaLista = new ArrayList<>();

        int n_bus = this.trechosRede.size();

        boolean ok = false;
        int n = 0;
        this.L = new ArrayList();
        Ponto origem, destino, inicioRede = null;

        for (int i = 0; i < n_bus; i++) {
            origem = this.trechosRede.get(i).getOrigem();
            for (int j = 0; j < n_bus; j++) {
                destino = this.trechosRede.get(j).getDestino();
                if (origem == destino) {
                    ok = true;
                    break;
                }
            }
            if (ok) {
                ok = false;
            } else {
                inicioRede = origem;
                n++;
            }
        }

        if (n != 1) {
            throw new RedeNaoRadialException();
        }

        List<Ponto> dadosLinha = new ArrayList<>();
        dadosLinha.add(inicioRede);
        L.add(dadosLinha);

        int cont = 1;
        int linha = 0;
        int coluna, nElem;
        Ponto bus;

        while (cont < n_bus) {
            linha++;
            dadosLinha = new ArrayList<>();
            L.add(dadosLinha);
            coluna = 0;
            nElem = L.get(linha - 1).size();

            for (int i = 0; i < nElem; i++) {
                bus = L.get(linha - 1).get(i);
                for (int j = 0; j < n_bus; j++) {
                    if (bus == this.trechosRede.get(j).getOrigem()) {
                        L.get(linha).add(this.trechosRede.get(j).getDestino());
                        cont++;
                        coluna++;
                    }
                }
            }
        }

        L.forEach((list) -> {
            list.forEach((ponto) -> {
                for (int k = 0; k < n_bus; k++) {
                    if (this.trechosRede.get(k).getOrigem() == ponto) {
                        novaLista.add(this.trechosRede.get(k));
                    }
                }
            });
        });
        this.trechosRede = novaLista;
    }

    private List<Ponto> procuraEquipamento(Ponto origemTrecho) {
        List<Ponto> equipamentos = new ArrayList<>();
        List<Ponto> destinosTrecho = new ArrayList<>();
        this.trechosRede.forEach(trecho -> {
            if (trecho.getOrigem() == origemTrecho) {
                destinosTrecho.add(trecho.getDestino());
            }
        });

        for (Ponto destino : destinosTrecho) {
            if ((destino.getTipoEquipamentoInstalado() != TipoEquipamento.NENHUM && destino.getTipoEquipamentoInstalado() != TipoEquipamento.CHAVE) || destino.isFimdeTrecho()) {
                equipamentos.add(destino);
            } else {
                for (Ponto ponto : procuraEquipamento(destino)) {
                    equipamentos.add(ponto);
                }
            }
        }
        return equipamentos;
    }

    private void addTrechosReduzidos(Ponto origemTrecho) {
        List<Ponto> destinos = procuraEquipamento(origemTrecho);
        for (Ponto destino : destinos) {
            this.redeReduzida.add(new Trecho(origemTrecho, destino));
            if (!destino.isFimdeTrecho()) {
                addTrechosReduzidos(destino);
            }
        }
    }

    private void criaRedeReduzida() {
        addTrechosReduzidos(this.trechosRede.get(0).getOrigem());
    }

    public int contaElosAbaixo(Ponto pontoElo) {
        List<Integer> contagem = new ArrayList<>();
        for (Trecho trecho : redeReduzida) {
            if (trecho.getOrigem().equals(pontoElo)) {
                if (trecho.getDestino().getTipoEquipamentoInstalado() == TipoEquipamento.ELO) {
                    contagem.add(contaElosAbaixo(trecho.getDestino()) + 1);
                } else {
                    contagem.add(contaElosAbaixo(trecho.getDestino()));
                }
            }
        }
        try {
            return Collections.max(contagem);
        } catch (java.util.NoSuchElementException e) {
            return 0;
        }
    }

    private void verificaFimdeTrechos() {
        boolean ok = false;
        for (Trecho t : this.trechosRede) {
            ok = false;
            Ponto p = t.getDestino();
            for (Trecho t2 : this.trechosRede) {
                Ponto p2 = t2.getOrigem();
                if (p == p2) {
                    ok = true;
                }
            }
            if (!ok) {
                p.setFimdeTrecho(true);
            }
        }
    }

    public double buscaCorrentePonto(Ponto p, Corrente c) {
        switch (c) {
            case ICARGA:
                return p.getIcarga();
            case ICC2F:
                return p.getIcc2f();
            case ICC3F:
                return p.getIcc3f();
            case ICCFT:
                return p.getIccft();
            case ICCFTMIN:
                return p.getIccftmin();
        }
        return 0;
    }

    public double buscaCorrentePonto(String s, Corrente c) {
        return buscaCorrentePonto(this.getPonto(s), c);
    }

    private List<Ponto> buscaProximoPonto(Ponto p) {
        List<Ponto> proximos = new ArrayList<>();

        for (Trecho trecho : this.redeReduzida) {
            if (trecho.getOrigem().equals(p)) {
                proximos.add(trecho.getDestino());
            }
        }
        return proximos;
    }

    public List<Double> buscaCorrenteProximoPonto(Ponto p, Corrente c) {
        List<Double> correntes = new ArrayList<>();
        if (p.isFimdeTrecho()) {
            correntes.add(this.buscaCorrentePonto(p, c));
            return correntes;
        }
        List<Ponto> proximos = this.buscaProximoPonto(p);

        for (Ponto proximo : proximos) {
            correntes.add(this.buscaCorrentePonto(proximo, c));
        }
        return correntes;
    }

    public List<Double> buscaCorrenteProximoPonto(String s, Corrente c) {
        return buscaCorrenteProximoPonto(this.getPonto(s), c);
    }

    public double buscaCorrenteMinimaProximoPonto(Ponto p, Corrente c) {
        List<Double> correntes = this.buscaCorrenteProximoPonto(p, c);
        return Collections.min(correntes);
    }

    public double buscaCorrenteMinimaProximoPonto(String s, Corrente c) {
        return buscaCorrenteMinimaProximoPonto(this.getPonto(s), c);
    }

    public List<Double> buscaCorrente2Camadas(Ponto p, Corrente c) {
        List<Double> correntes = new ArrayList<>();
        if (p.isFimdeTrecho()) {
            correntes.add(this.buscaCorrentePonto(p, c));
            return correntes;
        }
        List<Ponto> proximos = this.buscaProximoPonto(p);
        for (Ponto proximo : proximos) {
            List<Double> correntesProximo = this.buscaCorrenteProximoPonto(proximo, c);
            correntes.addAll(correntesProximo);
        }
        return correntes;
    }

    public List<Double> buscaCorrente2Camadas(String s, Corrente c) {
        return buscaCorrente2Camadas(this.getPonto(s), c);
    }

    public double buscaCorrenteMinima2Camadas(Ponto p, Corrente c) {
        return Collections.min(buscaCorrente2Camadas(p, c));
    }

    public double buscaCorrenteMinima2Camadas(String s, Corrente c) {
        return buscaCorrenteMinima2Camadas(this.getPonto(s), c);
    }

    public void verMapaReduzido() {
        Rede.verMapa(this.redeReduzida);
    }

    public static void verMapa(List<Trecho> trechos) {
        Graph graph = new DefaultGraph("MAPA");
        List<Ponto> pontos = new ArrayList<>();
        Ponto origem, destino;
        for (Trecho trecho : trechos) {
            //origem
            origem = trecho.getOrigem();
            if (pontos.indexOf(origem) == -1) {
                pontos.add(origem);
                Node n = graph.addNode(origem.getNome());
                n.setAttribute("xyz", origem.getCoordenadaX(), origem.getCoordenadaY(), 0);
            }
            //destino
            destino = trecho.getDestino();
            if (pontos.indexOf(destino) == -1) {
                pontos.add(destino);
                Node n = graph.addNode(destino.getNome());
                n.setAttribute("xyz", destino.getCoordenadaX(), destino.getCoordenadaY(), 0);
            }

            graph.addEdge(trecho.toString(), origem.getNome(), destino.getNome());
        }
        graph.display(false);
    }

    public void limparAjustes() {
        this.redeReduzida.forEach((trecho) -> {
            trecho.getDestino().setEquipamentoInstalado(null);
            Ponto.removeAttribute(this.mapa.getNode(trecho.getDestino().getNome()), "ui.class", "equipamentoSelecionado");
        });
    }

    public void stopPumpLoop() {
        this.loop = false;
    }

    public void salvarRede(Arquivo arquivo) {
        Gson gson = new Gson();
        java.lang.reflect.Type listType = new TypeToken<List<Trecho>>() {
        }.getType();
        String s = gson.toJson(this.getTrechosRede(), listType);
        arquivo.escreverArquivo(s);
    }

    public void ajuste() throws AjusteImpossivelException {
        for (int i = 0; i < redeReduzida.size(); i++) {
            Trecho t = redeReduzida.get(i);
            Ponto pAjuste = t.getDestino();
            Ponto pOrigem = t.getOrigem();
            TipoEquipamento equip = pAjuste.getTipoEquipamentoInstalado();
            if (i == 0) {
                switch (equip) {
                    case ELO:
                        ajusteElo(pAjuste);
                        break;
                    case RELE:

                        break;
                    case RELIGADOR:

                        break;
                }
            } else {
                switch (equip) {
                    case ELO:
                        switch (pOrigem.getTipoEquipamentoInstalado()) {
                            case ELO:
                                ajusteEloElo(pAjuste, pOrigem);
                                break;
                            case RELE:

                                break;
                            case RELIGADOR:

                                break;
                        }
                        break;
                    case RELE:
                        switch (pOrigem.getTipoEquipamentoInstalado()) {
                            case ELO:

                                break;
                            case RELE:

                                break;
                            case RELIGADOR:

                                break;
                        }
                        break;
                    case RELIGADOR:
                        switch (pOrigem.getTipoEquipamentoInstalado()) {
                            case ELO:

                                break;
                            case RELE:

                                break;
                            case RELIGADOR:

                                break;
                        }
                        break;
                }
            }

        }
    }

    public void ajusteElo(Ponto p) throws AjusteImpossivelException {
        Elo ajuste = Criterios_Elo.criterio_elo(elosDisponiveis, p, this);
        p.setEquipamentoInstalado(ajuste);
        Ponto.addAttribute(this.getMapa().getNode(p.getNome()), "ui.class", "equipamentoSelecionado");
    }

    public void ajusteEloElo(Ponto pontoRede, Ponto pontoOrigem) throws AjusteImpossivelException {
//        int numeroDeElosAbaixo = this.contaElosAbaixo(pontoRede);
//        int index = elosDisponiveis.indexOf(pontoOrigem.getEquipamentoInstalado());
//        List<Elo> elos = elosDisponiveis.subList(index + 1, elosDisponiveis.size());
//
//        if (elos.size() < numeroDeElosAbaixo) {
//            throw new AjusteImpossivelException();
//        }
//        
//        try {
//            Criterios_Elo.criterio_elo_elo_1(elos, pontoRede, this, pontoOrigem);
//        } catch (AjusteImpossivelException ex) {
//            try {
//                Criterios_Elo.criterio_elo_elo_2(elos, pontoRede, this, pontoOrigem);
//            } catch (AjusteImpossivelException ex1) {
//                try {
//                    Criterios_Elo.criterio_elo_elo_3(elos, pontoRede, this, pontoOrigem);
//                } catch (AjusteImpossivelException ex2) {
//                    reajusteEloElo();
//                }
//            }
//        }

        //Elo ajuste = Criterios_Elo.criterio_elo_elo(elosDisponiveis, pontoRede, this, pontoOrigem);
        //pontoRede.setEquipamentoInstalado(ajuste);
        //Ponto.addAttribute(this.getMapa().getNode(pontoRede.getNome()), "ui.class", "equipamentoSelecionado");
        Criterios_Elo_Elo criteriosEloElo = new Criterios_Elo_Elo(elosDisponiveis, pontoRede, this, pontoOrigem);
        List<Metricas_Elo_Elo> metricas = criteriosEloElo.ajuste();
        Ponto.addAttribute(this.getMapa().getNode(pontoRede.getNome()), "ui.class", "equipamentoSendoAjustado");
        Informacoes info = new Informacoes(this.getMapa().getNode(pontoRede.getNome()));
        MainFrame.frame.setInfoPanel(info);
        EscolheEloElo escolhe = new EscolheEloElo(metricas);
        Elo ajuste = escolhe.getSelecionado();
        pontoRede.setEquipamentoInstalado(ajuste);
        Ponto.removeAttribute(this.getMapa().getNode(pontoRede.getNome()), "ui.class", "equipamentoSendoAjustado");
        Ponto.addAttribute(this.getMapa().getNode(pontoRede.getNome()), "ui.class", "equipamentoSelecionado");
    }

    public void reajusteEloElo() {

    }
}
