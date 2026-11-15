
package model;

import java.util.ArrayList;
import java.util.List;

public class CurvaBSpline {

    public List<Ponto> rasterizar(List<Ponto> pontosControle) {

        List<Ponto> pixels = new ArrayList<>();

        if (pontosControle.size() < 4) {
            return pixels;
        }

        int passos = 1000;

        for (int i = 0; i <= pontosControle.size() - 4; i++) {

            Ponto p0 = pontosControle.get(i);
            Ponto p1 = pontosControle.get(i + 1);
            Ponto p2 = pontosControle.get(i + 2);
            Ponto p3 = pontosControle.get(i + 3);

            for (int j = 0; j <= passos; j++) {

                double t = (double) j / passos;

                double t2 = t * t;
                double t3 = t2 * t;

                double b0 =
                    (-t3 + 3 * t2 - 3 * t + 1) / 6.0;

                double b1 =
                    (3 * t3 - 6 * t2 + 4) / 6.0;

                double b2 =
                    (-3 * t3 + 3 * t2 + 3 * t + 1) / 6.0;

                double b3 = t3 / 6.0;

                double x =
                    b0 * p0.getX() +
                    b1 * p1.getX() +
                    b2 * p2.getX() +
                    b3 * p3.getX();

                double y =
                    b0 * p0.getY() +
                    b1 * p1.getY() +
                    b2 * p2.getY() +
                    b3 * p3.getY();

                pixels.add(
                    new Ponto(
                        (int) Math.round(x),
                        (int) Math.round(y)
                    )
                );
            }
        }

        return pixels;
    }
}
