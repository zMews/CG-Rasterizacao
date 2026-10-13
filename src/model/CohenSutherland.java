package model;

import java.util.ArrayList;
import java.util.List;

public class CohenSutherland {

    private static final int DENTRO = 0;
    private static final int ESQUERDA = 1;
    private static final int DIREITA = 2;
    private static final int ABAIXO = 4;
    private static final int ACIMA = 8;

    public List<Ponto> recortar(
        Ponto ponto1,
        Ponto ponto2,
        int xMin,
        int yMin,
        int xMax,
        int yMax
    ) {

        List<Ponto> resultado = new ArrayList<>();

        double x1 = ponto1.getX();
        double y1 = ponto1.getY();

        double x2 = ponto2.getX();
        double y2 = ponto2.getY();

        int codigo1 = calcularCodigo(
            x1, y1,
            xMin, yMin,
            xMax, yMax
        );

        int codigo2 = calcularCodigo(
            x2, y2,
            xMin, yMin,
            xMax, yMax
        );

        boolean aceita = false;

        while (true) {

            if ((codigo1 | codigo2) == 0) {

                aceita = true;
                break;

            } else if ((codigo1 & codigo2) != 0) {

                break;

            } else {

                int codigoFora;

                if (codigo1 != 0) {
                    codigoFora = codigo1;
                } else {
                    codigoFora = codigo2;
                }

                double x = 0;
                double y = 0;

                if ((codigoFora & ACIMA) != 0) {

                    x = x1 +
                        (x2 - x1) *
                        (yMin - y1) /
                        (y2 - y1);

                    y = yMin;

                } else if ((codigoFora & ABAIXO) != 0) {

                    x = x1 +
                        (x2 - x1) *
                        (yMax - y1) /
                        (y2 - y1);

                    y = yMax;

                } else if ((codigoFora & DIREITA) != 0) {

                    y = y1 +
                        (y2 - y1) *
                        (xMax - x1) /
                        (x2 - x1);

                    x = xMax;

                } else if ((codigoFora & ESQUERDA) != 0) {

                    y = y1 +
                        (y2 - y1) *
                        (xMin - x1) /
                        (x2 - x1);

                    x = xMin;
                }

                if (codigoFora == codigo1) {

                    x1 = x;
                    y1 = y;

                    codigo1 = calcularCodigo(
                        x1, y1,
                        xMin, yMin,
                        xMax, yMax
                    );

                } else {

                    x2 = x;
                    y2 = y;

                    codigo2 = calcularCodigo(
                        x2, y2,
                        xMin, yMin,
                        xMax, yMax
                    );
                }
            }
        }

        if (aceita) {

            resultado.add(
                new Ponto(
                    (int) Math.round(x1),
                    (int) Math.round(y1)
                )
            );

            resultado.add(
                new Ponto(
                    (int) Math.round(x2),
                    (int) Math.round(y2)
                )
            );
        }

        return resultado;
    }

    private int calcularCodigo(
        double x,
        double y,
        int xMin,
        int yMin,
        int xMax,
        int yMax
    ) {

        int codigo = DENTRO;

        if (x < xMin) {
            codigo |= ESQUERDA;

        } else if (x > xMax) {
            codigo |= DIREITA;
        }

        if (y < yMin) {
            codigo |= ACIMA;

        } else if (y > yMax) {
            codigo |= ABAIXO;
        }

        return codigo;
    }
}