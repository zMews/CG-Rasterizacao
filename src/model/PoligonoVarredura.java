package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PoligonoVarredura {

    public List<Ponto> preencher(List<Ponto> vertices) {

        List<Ponto> pixels = new ArrayList<>();

        if (vertices.size() < 3) {
            return pixels;
        }

        int menorY = vertices.get(0).getY();
        int maiorY = vertices.get(0).getY();

        for (Ponto ponto : vertices) {

            if (ponto.getY() < menorY) {
                menorY = ponto.getY();
            }

            if (ponto.getY() > maiorY) {
                maiorY = ponto.getY();
            }
        }

        for (int y = menorY; y <= maiorY; y++) {

            List<Integer> intersecoes = new ArrayList<>();

            for (int i = 0; i < vertices.size(); i++) {

                Ponto atual = vertices.get(i);

                Ponto proximo = vertices.get(
                    (i + 1) % vertices.size()
                );

                int x1 = atual.getX();
                int y1 = atual.getY();

                int x2 = proximo.getX();
                int y2 = proximo.getY();

                if ((y1 <= y && y2 > y) ||
                    (y2 <= y && y1 > y)) {

                    double x =
                        x1 +
                        (double) (y - y1) *
                        (x2 - x1) /
                        (y2 - y1);

                    intersecoes.add(
                        (int) Math.round(x)
                    );
                }
            }

            Collections.sort(intersecoes);

            for (int i = 0;
                 i + 1 < intersecoes.size();
                 i += 2) {

                int inicioX = intersecoes.get(i);
                int fimX = intersecoes.get(i + 1);

                for (int x = inicioX; x <= fimX; x++) {

                    pixels.add(
                        new Ponto(x, y)
                    );
                }
            }
        }

        return pixels;
    }
}