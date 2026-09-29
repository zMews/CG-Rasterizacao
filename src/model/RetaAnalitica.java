package model;

import java.util.ArrayList;
import java.util.List;

public class RetaAnalitica implements Rasterizacao {

    @Override
    public List<Ponto> rasterizar(Ponto inicio, Ponto fim) {

        List<Ponto> pixels = new ArrayList<>();

        int x1 = inicio.getX();
        int y1 = inicio.getY();

        int x2 = fim.getX();
        int y2 = fim.getY();

        int dx = x2 - x1;
        int dy = y2 - y1;

        // Ponto
        if (dx == 0 && dy == 0) {
            pixels.add(new Ponto(x1, y1));
            return pixels;
        }

        // Reta vertical
        if (dx == 0) {

            int inicioY = Math.min(y1, y2);
            int fimY = Math.max(y1, y2);

            for (int y = inicioY; y <= fimY; y++) {
                pixels.add(new Ponto(x1, y));
            }

            return pixels;
        }

        double m = (double) dy / dx;

        double b = y1 - m * x1;

        if (Math.abs(dx) >= Math.abs(dy)) {

            int inicioX = Math.min(x1, x2);
            int fimX = Math.max(x1, x2);

            for (int x = inicioX; x <= fimX; x++) {

                double y = m * x + b;

                pixels.add(
                    new Ponto(
                        x,
                        (int) Math.round(y)
                    )
                );
            }

        } else {

            int inicioY = Math.min(y1, y2);
            int fimY = Math.max(y1, y2);

            for (int y = inicioY; y <= fimY; y++) {

                double x = (y - b) / m;

                pixels.add(
                    new Ponto(
                        (int) Math.round(x),
                        y
                    )
                );
            }
        }

        return pixels;
    }
}