package view;

import model.Ponto;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Painel extends JPanel {
	private static final long serialVersionUID = 1;

    private final BufferedImage imagem;

    public Painel() {

        imagem = new BufferedImage(
                900,
                600,
                BufferedImage.TYPE_INT_RGB
        );

        setPreferredSize(new Dimension(900, 600));

        limpar();
    }

    public void desenharPixels(List<Ponto> pixels, Color cor) {

        for (Ponto ponto : pixels) {
            colocarPixel(
                ponto.getX(),
                ponto.getY(),
                cor
            );
        }

        repaint();
    }

    private void colocarPixel(int x, int y, Color cor) {

    if (x >= 0 &&
        x < imagem.getWidth() &&
        y >= 0 &&
        y < imagem.getHeight()) {

        imagem.setRGB(
            x,
            y,
            cor.getRGB()
        );
        }
    }

    public void limpar() {

        for (int x = 0; x < imagem.getWidth(); x++) {

            for (int y = 0; y < imagem.getHeight(); y++) {

                imagem.setRGB(
                        x,
                        y,
                        Color.WHITE.getRGB()
                );
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(
                imagem,
                0,
                0,
                null
        );
    }
}