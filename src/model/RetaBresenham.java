package model;

import java.util.ArrayList;
import java.util.List;

public class RetaBresenham implements Rasterizacao {

    @Override
    public List<Ponto> rasterizar(Ponto inicio, Ponto fim) {

        List<Ponto> pixels = new ArrayList<>();

        int x1 = inicio.getX();
        int y1 = inicio.getY();

        int x2 = fim.getX();
        int y2 = fim.getY();

        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);

        int passoX = x1 < x2 ? 1 : -1;
        int passoY = y1 < y2 ? 1 : -1;

        int erro = dx - dy;

        while (true) {

            pixels.add(new Ponto(x1, y1));

            if (x1 == x2 && y1 == y2) {
                break;
            }

            int erro2 = 2 * erro;

            if (erro2 > -dy) {
                erro -= dy;
                x1 += passoX;
            }

            if (erro2 < dx) {
                erro += dx;
                y1 += passoY;
            }
        }

        return pixels;
    }
}