package SIGPROD2.Auxiliar;

import java.io.*;

/**
 * Classe responsável por facilitar a manipulação de arquivos de texto.
 *
 * @author Sérgio Braunetta.
 * @version 13/04/2016
 */
public class Arquivo {

    private String nome;
    private PrintWriter pw;
    private String texto;
    private BufferedReader br;
    private File file;

    public Arquivo(String nomeArquivo) {
        setNome(nomeArquivo);
        this.file = new File(nome);
    }

    public Arquivo(String nomeArquivo, String texto) {
        setNome(nomeArquivo);
        criaArquivo();
        escreverArquivo(texto);
    }
    
    public Arquivo(File file) {
        this.file = file;
        this.nome = file.getName();
    }

    private void setNome(String nomeArquivo) {
        this.nome = nomeArquivo;
    }

    public String getNome() {
        return this.nome;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
    

    public boolean criaArquivo() {
        try {
            this.pw = new PrintWriter(new FileOutputStream(getFile()));
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public void fechaArquivoSaida() {
        getArquivoSaida().close();
    }

    public void fechaArquivoEntrada() {
        if (this.br != null) {
            try {
                getArquivoEntrada().close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void escreverArquivo(String textoArquivo) {
        this.texto = textoArquivo;
        getArquivoSaida().print(this.texto);
        this.pw.flush();
    }

    public PrintWriter getArquivoSaida() {
        if (this.pw == null) {
            criaArquivo();
        }
        return this.pw;
    }

    public BufferedReader getArquivoEntrada() {
        if (this.br == null) {
            abreArquivo();
        }
        return br;
    }

    public boolean abreArquivo() {
        try {
            br = new BufferedReader(new FileReader(getFile()));
            return true;
        } catch (IOException ex) {
            return false;
        }
    }

    public String lerArquivo() {
        abreArquivo();
        StringBuilder sb = new StringBuilder();
        try {
            while (this.br.ready()) {
                String line = this.br.readLine() + "\r";
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException ex) {
            System.out.println("Error");
        }
        return "";
    }

    public boolean existeArquivo() {
        return this.file.exists();
    }

    public boolean apagaArquivo() {
        return this.file.delete();
    }

    public boolean podeLerArquivo() {
        return this.file.canRead();
    }

    public boolean podeEscreverArquivo() {
        return this.file.canWrite();
    }

    public boolean ehDiretorio() {
        return this.file.isDirectory();
    }

    public void apagaArquivosDiretorio() {
        if (ehDiretorio()) {
            File arquivos[] = this.file.listFiles();

            for (int i = 0; i < arquivos.length; i++) {
                File arquivo = arquivos[i];

                arquivo.delete();
            }
        }
    }
}
