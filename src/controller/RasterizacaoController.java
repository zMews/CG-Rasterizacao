package controller;

import model.CirculoBresenham;
import model.Ponto;
import model.Rasterizacao;
import model.RetaAnalitica;
import model.RetaBresenham;
import model.RetaDDA;
import view.Principal;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.awt.Color;
import java.util.Random;

public class RasterizacaoController {

    private Principal principal;

    private String algoritmoSelecionado;

    private Ponto primeiroPonto;

    public RasterizacaoController(Principal principal) {

        this.principal = principal;

        configurarEventos();
    }

    private void configurarEventos() {

        principal.getItemAnalitico().addActionListener(e -> {

            algoritmoSelecionado = "ANALITICO";
            primeiroPonto = null;

            principal.setMensagem(
                "Algoritmo Analítico selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemDDA().addActionListener(e -> {

            algoritmoSelecionado = "DDA";
            primeiroPonto = null;

            principal.setMensagem(
                "Algoritmo DDA selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemBresenhamReta().addActionListener(e -> {

            algoritmoSelecionado = "BRESENHAM_RETA";
            primeiroPonto = null;

            principal.setMensagem(
                "Bresenham para reta selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemBresenhamCirculo().addActionListener(e -> {

            algoritmoSelecionado = "BRESENHAM_CIRCULO";
            primeiroPonto = null;

            principal.setMensagem(
                "Bresenham para circunferência selecionado - clique no centro."
            );
        });

        principal.getBotaoLimpar().addActionListener(e -> {

            principal.getPainel().limpar();
            primeiroPonto = null;

            if (algoritmoSelecionado == null) {
                principal.setMensagem(
                    "Tela limpa - selecione um algoritmo."
                );

            } else if (algoritmoSelecionado.equals("BRESENHAM_CIRCULO")) {
                principal.setMensagem(
                    "Tela limpa - " + nomeAlgoritmo() +
                    " selecionado. Clique no centro."
                );

            } else {
                principal.setMensagem(
                    "Tela limpa - " + nomeAlgoritmo() +
                    " selecionado. Clique no primeiro ponto."
                );
            }
        });

        principal.getPainel().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {

                tratarClique(
                    e.getX(),
                    e.getY()
                );
            }
        });
    }


    private void tratarClique(int x, int y) {

        if (algoritmoSelecionado == null) {

            principal.setMensagem(
                "Selecione um algoritmo antes de desenhar."
            );

            return;
        }

        Ponto pontoAtual = new Ponto(x, y);


        if (primeiroPonto == null) {

            primeiroPonto = pontoAtual;

            if (algoritmoSelecionado.equals("BRESENHAM_CIRCULO")) {

                principal.setMensagem(
                    "Bresenham para circunferência - centro definido em (" +
                    x + ", " + y +
                    "). Clique no segundo ponto para definir o raio."
                );

            } else {

                principal.setMensagem(
                    nomeAlgoritmo() +
                    " - primeiro ponto definido em (" +
                    x + ", " + y +
                    "). Clique no segundo ponto."
                );
            }

            return;
        }


        if (algoritmoSelecionado.equals("BRESENHAM_CIRCULO")) {

            desenharCirculo(
                primeiroPonto,
                pontoAtual
            );

        } else {

            desenharReta(
                primeiroPonto,
                pontoAtual
            );
        }

        primeiroPonto = null;

        principal.setMensagem(
            nomeAlgoritmo() +
            " - desenho concluído. Clique no primeiro ponto ou troque de algoritmo para desenhar novamente."
        );
    }


    private void desenharReta(Ponto inicio, Ponto fim) {

        Rasterizacao algoritmo;

        switch (algoritmoSelecionado) {

            case "ANALITICO":
                algoritmo = new RetaAnalitica();
                break;

            case "DDA":
                algoritmo = new RetaDDA();
                break;

            case "BRESENHAM_RETA":
                algoritmo = new RetaBresenham();
                break;

            default:
                return;
        }

        List<Ponto> pixels =
            algoritmo.rasterizar(
                inicio,
                fim
            );

        principal
            .getPainel()
            .desenharPixels(
                pixels,
                gerarCorAleatoria()
            );
    }


    private void desenharCirculo(
        Ponto centro,
        Ponto pontoRaio
    ) {

        int dx =
            pontoRaio.getX() - centro.getX();

        int dy =
            pontoRaio.getY() - centro.getY();

        int raio =
            (int) Math.round(
                Math.sqrt(
                    dx * dx +
                    dy * dy
                )
            );

        CirculoBresenham algoritmo =
            new CirculoBresenham();

        List<Ponto> pixels =
            algoritmo.rasterizar(
                centro,
                raio
            );

        principal
            .getPainel()
            .desenharPixels(
                pixels,
                gerarCorAleatoria()
            );
    }


    private String nomeAlgoritmo() {

        switch (algoritmoSelecionado) {

            case "ANALITICO":
                return "Algoritmo Analítico";

            case "DDA":
                return "Algoritmo DDA";

            case "BRESENHAM_RETA":
                return "Bresenham para reta";

            case "BRESENHAM_CIRCULO":
                return "Bresenham para circunferência";

            default:
                return "";
        }
    }

    private Color gerarCorAleatoria() {

    int vermelho = random.nextInt(200);
    int verde = random.nextInt(200);
    int azul = random.nextInt(200);

    return new Color(
        vermelho,
        verde,
        azul
    );
}
}