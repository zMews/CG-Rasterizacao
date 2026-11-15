
package model;

import java.util.ArrayList;
import java.util.List;

public class CurvaBezier {

    public List<Ponto> rasterizar(List<Ponto> pontosControle) {

        List<Ponto> pixels = new ArrayList<>();

        if (pontosControle.size() < 2) {
            return pixels;
        }

        int passos = 1000;

        for (int i = 0; i <= passos; i++) {

            double t = (double) i / passos;

            List<double[]> pontos = new ArrayList<>();

            for (Ponto ponto : pontosControle) {

                pontos.add(new double[] {
                    ponto.getX(),
                    ponto.getY()
                });
            }

            for (int nivel = pontos.size() - 1; nivel > 0; nivel--) {

                for (int j = 0; j < nivel; j++) {

                    double x =
                        (1 - t) * pontos.get(j)[0] +
                        t * pontos.get(j + 1)[0];

                    double y =
                        (1 - t) * pontos.get(j)[1] +
                        t * pontos.get(j + 1)[1];

                    pontos.get(j)[0] = x;
                    pontos.get(j)[1] = y;
                }
            }

            int x = (int) Math.round(pontos.get(0)[0]);
            int y = (int) Math.round(pontos.get(0)[1]);

            pixels.add(new Ponto(x, y));
        }

        return pixels;
    }
}
