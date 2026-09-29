package model;

import java.util.ArrayList;
import java.util.List;

public class CirculoBresenham {

    public List<Ponto> rasterizar(Ponto centro, int raio) {

        List<Ponto> pixels = new ArrayList<>();

        int xc = centro.getX();
        int yc = centro.getY();

        int x = 0;
        int y = raio;

        int d = 3 - 2 * raio;

        while (x <= y) {

            adicionarPontosSimetricos(
                pixels,
                xc,
                yc,
                x,
                y
            );

            if (d < 0) {

                d = d + 4 * x + 6;

            } else {

                d = d + 4 * (x - y) + 10;
                y--;
            }

            x++;
        }

        return pixels;
    }

    private void adicionarPontosSimetricos(
        List<Ponto> pixels,
        int xc,
        int yc,
        int x,
        int y
    ) {

        pixels.add(new Ponto(xc + x, yc + y));
        pixels.add(new Ponto(xc - x, yc + y));
        pixels.add(new Ponto(xc + x, yc - y));
        pixels.add(new Ponto(xc - x, yc - y));

        pixels.add(new Ponto(xc + y, yc + x));
        pixels.add(new Ponto(xc - y, yc + x));
        pixels.add(new Ponto(xc + y, yc - x));
        pixels.add(new Ponto(xc - y, yc - x));
    }
}