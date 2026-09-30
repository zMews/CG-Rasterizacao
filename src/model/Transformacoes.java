package model;

import java.util.ArrayList;
import java.util.List;

public class Transformacoes {

    public List<Ponto> transladar(List<Ponto> pontos, int deslocamentoX) {

        List<Ponto> novosPontos = new ArrayList<>();

        for (Ponto ponto : pontos) {

            novosPontos.add(
                new Ponto(
                    ponto.getX() + deslocamentoX,
                    ponto.getY()
                )
            );
        }

        return novosPontos;
    }

    public List<Ponto> escalar(List<Ponto> pontos, double fator) {

        List<Ponto> novosPontos = new ArrayList<>();

        double centroX = calcularCentroX(pontos);
        double centroY = calcularCentroY(pontos);

        for (Ponto ponto : pontos) {

            int novoX = (int) Math.round(
                centroX +
                (ponto.getX() - centroX) * fator
            );

            int novoY = (int) Math.round(
                centroY +
                (ponto.getY() - centroY) * fator
            );

            novosPontos.add(
                new Ponto(novoX, novoY)
            );
        }

        return novosPontos;
    }

    public List<Ponto> rotacionar(List<Ponto> pontos, double angulo) {

        List<Ponto> novosPontos = new ArrayList<>();

        double centroX = calcularCentroX(pontos);
        double centroY = calcularCentroY(pontos);

        double radianos = Math.toRadians(angulo);

        double cos = Math.cos(radianos);
        double sen = Math.sin(radianos);

        for (Ponto ponto : pontos) {

            double x = ponto.getX() - centroX;
            double y = ponto.getY() - centroY;

            int novoX = (int) Math.round(
                centroX +
                x * cos -
                y * sen
            );

            int novoY = (int) Math.round(
                centroY +
                x * sen +
                y * cos
            );

            novosPontos.add(
                new Ponto(novoX, novoY)
            );
        }

        return novosPontos;
    }

    private double calcularCentroX(List<Ponto> pontos) {

        double soma = 0;

        for (Ponto ponto : pontos) {
            soma += ponto.getX();
        }

        return soma / pontos.size();
    }

    private double calcularCentroY(List<Ponto> pontos) {

        double soma = 0;

        for (Ponto ponto : pontos) {
            soma += ponto.getY();
        }

        return soma / pontos.size();
    }
}