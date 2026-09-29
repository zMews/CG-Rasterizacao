package model;

import java.util.ArrayList;
import java.util.List;

public class RetaDDA implements Rasterizacao {

    @Override
    public List<Ponto> rasterizar(Ponto inicio, Ponto fim) {

        List<Ponto> pixels = new ArrayList<>();

        int x1 = inicio.getX();
        int y1 = inicio.getY();

        int x2 = fim.getX();
        int y2 = fim.getY();

        int dx = x2 - x1;
        int dy = y2 - y1;

        int passos = Math.max(
            Math.abs(dx),
            Math.abs(dy)
        );

        // Caso seja apenas um ponto
        if (passos == 0) {
            pixels.add(new Ponto(x1, y1));
            return pixels;
        }

        double incrementoX = (double) dx / passos;
        double incrementoY = (double) dy / passos;

        double x = x1;
        double y = y1;

        for (int i = 0; i <= passos; i++) {

            pixels.add(
                new Ponto(
                    (int) Math.round(x),
                    (int) Math.round(y)
                )
            );

            x += incrementoX;
            y += incrementoY;
        }

        return pixels;
    }
}