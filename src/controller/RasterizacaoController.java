package controller;

import model.CirculoBresenham;
import model.PoligonoVarredura;
import model.Ponto;
import model.Rasterizacao;
import model.RetaAnalitica;
import model.RetaBresenham;
import model.RetaDDA;
import view.Principal;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JOptionPane;

public class RasterizacaoController {

    private Principal principal;

    private Random random = new Random();

    private String algoritmoSelecionado;

    private Ponto primeiroPonto;

    private List<Ponto> pontosPoligono = new ArrayList<>();

    private int quantidadePontosPoligono;

    public RasterizacaoController(Principal principal) {

        this.principal = principal;

        configurarEventos();
    }

    private void configurarEventos() {

        principal.getItemAnalitico().addActionListener(e -> {

            algoritmoSelecionado = "ANALITICO";
            primeiroPonto = null;
            pontosPoligono.clear();

            principal.setMensagem(
                "Algoritmo Analítico selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemDDA().addActionListener(e -> {

            algoritmoSelecionado = "DDA";
            primeiroPonto = null;
            pontosPoligono.clear();

            principal.setMensagem(
                "Algoritmo DDA selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemBresenhamReta().addActionListener(e -> {

            algoritmoSelecionado = "BRESENHAM_RETA";
            primeiroPonto = null;
            pontosPoligono.clear();

            principal.setMensagem(
                "Bresenham para reta selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemBresenhamCirculo().addActionListener(e -> {

            algoritmoSelecionado = "BRESENHAM_CIRCULO";
            primeiroPonto = null;
            pontosPoligono.clear();

            principal.setMensagem(
                "Bresenham para circunferência selecionado - clique no centro."
            );
        });

        principal.getItemVarredura().addActionListener(e -> {

            String entrada = JOptionPane.showInputDialog(
                principal,
                "Informe a quantidade de pontos do polígono:",
                "Polígono - Varredura",
                JOptionPane.QUESTION_MESSAGE
            );

            if (entrada == null) {
                return;
            }

            try {

                int quantidade = Integer.parseInt(entrada);

                if (quantidade < 3) {

                    JOptionPane.showMessageDialog(
                        principal,
                        "O polígono deve possuir pelo menos 3 pontos."
                    );

                    return;
                }

                algoritmoSelecionado = "POLIGONO_VARREDURA";
                quantidadePontosPoligono = quantidade;

                primeiroPonto = null;
                pontosPoligono.clear();

                principal.setMensagem(
                    "Varredura selecionada - clique no ponto 1 de "
                    + quantidadePontosPoligono + "."
                );

            } catch (NumberFormatException ex) {

                JOptionPane.showMessageDialog(
                    principal,
                    "Digite um número inteiro válido."
                );
            }
        });

        principal.getBotaoLimpar().addActionListener(e -> {

            principal.getPainel().limpar();

            primeiroPonto = null;
            pontosPoligono.clear();

            if (algoritmoSelecionado == null) {

                principal.setMensagem(
                    "Tela limpa - selecione um algoritmo."
                );

            } else if (algoritmoSelecionado.equals("BRESENHAM_CIRCULO")) {

                principal.setMensagem(
                    "Tela limpa - " + nomeAlgoritmo() +
                    " selecionado. Clique no centro."
                );

            } else if (algoritmoSelecionado.equals("POLIGONO_VARREDURA")) {

                principal.setMensagem(
                    "Tela limpa - Varredura selecionada. Clique no ponto 1 de "
                    + quantidadePontosPoligono + "."
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

        if (algoritmoSelecionado.equals("POLIGONO_VARREDURA")) {

            tratarCliquePoligono(x, y);

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

    private void tratarCliquePoligono(int x, int y) {

        pontosPoligono.add(
            new Ponto(x, y)
        );

        int quantidadeAtual = pontosPoligono.size();

        if (quantidadeAtual < quantidadePontosPoligono) {

            principal.setMensagem(
                "Varredura - ponto " +
                quantidadeAtual +
                " definido. Clique no ponto " +
                (quantidadeAtual + 1) +
                " de " +
                quantidadePontosPoligono +
                "."
            );

            return;
        }

        desenharPoligonoVarredura();

        pontosPoligono.clear();

        principal.setMensagem(
            "Polígono preenchido por Varredura - clique no ponto 1 de "
            + quantidadePontosPoligono +
            " para desenhar outro."
        );
    }

    private void desenharPoligonoVarredura() {

        Color cor = gerarCorAleatoria();

        PoligonoVarredura algoritmo =
            new PoligonoVarredura();

        List<Ponto> pixels =
            algoritmo.preencher(
                pontosPoligono
            );

        principal
            .getPainel()
            .desenharPixels(
                pixels,
                cor
            );

        RetaBresenham bresenham =
            new RetaBresenham();

        for (int i = 0; i < pontosPoligono.size(); i++) {

            Ponto inicio =
                pontosPoligono.get(i);

            Ponto fim =
                pontosPoligono.get(
                    (i + 1) % pontosPoligono.size()
                );

            List<Ponto> borda =
                bresenham.rasterizar(
                    inicio,
                    fim
                );

            principal
                .getPainel()
                .desenharPixels(
                    borda,
                    cor
                );
        }
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

            case "POLIGONO_VARREDURA":
                return "Varredura";

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