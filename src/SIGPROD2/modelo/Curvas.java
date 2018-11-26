package SIGPROD2.modelo;

import java.util.ArrayList;

/**
 * Esta interface deve ser implementada pelas classes que contenham curvas que
 * serão representadas em um Gráfico.
 *
 * @see SIGPROD2.Auxiliar.Grafico
 * @see org.jfree.chart.JFreeChart
 * @author Rafael Casa
 * @version 25/03/2016
 */
public interface Curvas {

    /**
     * Este método deve retornar o Título do Gráfico a ser criado.
     *
     * @return O título do Gráfico a ser criado.
     */
    String getTitulo();

    /**
     * Este método deve retornar a quantidade de curvas que a entidade contém.
     *
     * @return A quantidade de curvas que a entidade contém.
     */
    int getQtdCurvas();

    /**
     * Este método deve retornar o nome da curva informada pelo índice.
     *
     * @param index Um índice que identifica uma curva. Deve ser um número entre
     * 0 e qtdCurvas - 1.
     * @return O nome da curva informada pelo índice.
     */
    String getNomeCurva(int index);

    /**
     * Este método retorna os pontos da curva informada pelo índice.
     *
     * @param index Um índice que identifica uma curva. Deve ser um número entre
     * 0 e qtdCurvas - 1.
     * @return Um ArrayList com todos os pontos da curva selecionada.
     */
    ArrayList<PontoCurva> getCurva(int index);
    double[][] getCurvaMatrix(int index);
    

    /**
     * Este método retorna a cor desejada da curva informada pelo indice no
     * gráfico.
     *
     * @param index Um índice que identifica uma curva. Deve ser um número entre
     * 0 e qtdCurvas - 1.
     * @return a cor desejada da curva no gráfico.
     */
    java.awt.Paint getCurvaPaint(int index);
}
