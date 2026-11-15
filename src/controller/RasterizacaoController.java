
package controller;

import model.CirculoBresenham;
import model.CohenSutherland;
import model.CurvaBezier;
import model.CurvaBSpline;
import model.PoligonoVarredura;
import model.Ponto;
import model.Rasterizacao;
import model.RetaAnalitica;
import model.RetaBresenham;
import model.RetaDDA;
import model.Transformacoes;
import view.Principal;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

public class RasterizacaoController {

    private Principal principal;

    private Random random = new Random();

    private String algoritmoSelecionado;

    private String transformacaoSelecionada;

    private Ponto primeiroPonto;

    private List<Ponto> pontosPoligono = new ArrayList<>();

    private List<Ponto> ultimoPoligono = new ArrayList<>();

    private int quantidadePontosPoligono;

    private Color corUltimoPoligono;

    private Transformacoes transformacoes = new Transformacoes();

    private Ponto primeiroPontoRecorte;

    private List<Ponto[]> segmentosRecorte = new ArrayList<>();

    private List<Ponto> pontosCurva = new ArrayList<>();

    private int quantidadePontosCurva;

    public RasterizacaoController(Principal principal) {

        this.principal = principal;

        configurarEventos();
        configurarTeclado();
    }

    private void configurarEventos() {

        principal.getItemAnalitico().addActionListener(e -> {

            selecionarAlgoritmo("ANALITICO");

            principal.setMensagem(
                "Algoritmo Analítico selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemDDA().addActionListener(e -> {

            selecionarAlgoritmo("DDA");

            principal.setMensagem(
                "Algoritmo DDA selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemBresenhamReta().addActionListener(e -> {

            selecionarAlgoritmo("BRESENHAM_RETA");

            principal.setMensagem(
                "Bresenham para reta selecionado - clique no primeiro ponto."
            );
        });

        principal.getItemBresenhamCirculo().addActionListener(e -> {

            selecionarAlgoritmo("BRESENHAM_CIRCULO");

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

                int quantidade = Integer.parseInt(entrada.trim());

                if (quantidade < 3) {

                    JOptionPane.showMessageDialog(
                        principal,
                        "O polígono deve possuir pelo menos 3 pontos."
                    );

                    return;
                }

                selecionarAlgoritmo("POLIGONO_VARREDURA");

                quantidadePontosPoligono = quantidade;

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

        principal.getItemBezier().addActionListener(e -> {

            configurarCurva("BEZIER", 2);
        });

        principal.getItemBSpline().addActionListener(e -> {

            configurarCurva("BSPLINE", 4);
        });

        principal.getItemTranslacao().addActionListener(e -> {

            transformacaoSelecionada = "TRANSLACAO";

            principal.setMensagem(
                "Translação selecionada - use ← para retornar e → para avançar."
            );
        });

        principal.getItemEscala().addActionListener(e -> {

            transformacaoSelecionada = "ESCALA";

            principal.setMensagem(
                "Escala selecionada - use ← para diminuir e → para aumentar."
            );
        });

        principal.getItemRotacao().addActionListener(e -> {

            transformacaoSelecionada = "ROTACAO";

            principal.setMensagem(
                "Rotação selecionada - use ← para rotacionar -10° e → para +10°."
            );
        });

        principal.getItemCohenSutherland().addActionListener(e -> {

            selecionarAlgoritmo("COHEN_SUTHERLAND");

            gerarElementosRecorte();

            principal.setMensagem(
                "Cohen-Sutherland selecionado - clique no primeiro canto do retângulo de recorte."
            );
        });

        principal.getBotaoLimpar().addActionListener(e -> {

            principal.getPainel().limpar();

            primeiroPonto = null;
            primeiroPontoRecorte = null;

            pontosPoligono.clear();
            ultimoPoligono.clear();
            segmentosRecorte.clear();
            pontosCurva.clear();

            transformacaoSelecionada = null;

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

            } else if (algoritmoSelecionado.equals("COHEN_SUTHERLAND")) {

                algoritmoSelecionado = null;

                principal.setMensagem(
                    "Tela limpa - selecione Cohen-Sutherland novamente para iniciar o recorte."
                );

            } else if (algoritmoSelecionado.equals("BEZIER") ||
                       algoritmoSelecionado.equals("BSPLINE")) {

                principal.setMensagem(
                    "Tela limpa - " + nomeAlgoritmo() +
                    " selecionada. Clique no ponto 1 de " +
                    quantidadePontosCurva + "."
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

    private void selecionarAlgoritmo(String algoritmo) {

        algoritmoSelecionado = algoritmo;

        primeiroPonto = null;
        primeiroPontoRecorte = null;

        pontosPoligono.clear();
        pontosCurva.clear();

        transformacaoSelecionada = null;
    }

    private void configurarCurva(String algoritmo, int minimo) {

        String entrada = JOptionPane.showInputDialog(
            principal,
            "Informe a quantidade de pontos de controle:",
            "Curvas",
            JOptionPane.QUESTION_MESSAGE
        );

        if (entrada == null) {
            return;
        }

        try {

            int quantidade = Integer.parseInt(entrada.trim());

            if (quantidade < minimo) {

                JOptionPane.showMessageDialog(
                    principal,
                    "A curva deve possuir pelo menos " +
                    minimo + " pontos de controle."
                );

                return;
            }

            selecionarAlgoritmo(algoritmo);

            quantidadePontosCurva = quantidade;

            principal.setMensagem(
                nomeAlgoritmo() +
                " selecionada - clique no ponto 1 de " +
                quantidadePontosCurva + "."
            );

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                principal,
                "Digite um número inteiro válido."
            );
        }
    }

    private void configurarTeclado() {

        principal.getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(
                KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0),
                "esquerda"
            );

        principal.getRootPane()
            .getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW)
            .put(
                KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0),
                "direita"
            );

        principal.getRootPane()
            .getActionMap()
            .put(
                "esquerda",
                new AbstractAction() {

                    private static final long serialVersionUID = 1;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        executarTransformacao(-1);
                    }
                }
            );

        principal.getRootPane()
            .getActionMap()
            .put(
                "direita",
                new AbstractAction() {

                    private static final long serialVersionUID = 1;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        executarTransformacao(1);
                    }
                }
            );
    }

    private void executarTransformacao(int direcao) {

        if (transformacaoSelecionada == null) {
            return;
        }

        if (ultimoPoligono.isEmpty()) {

            principal.setMensagem(
                "Desenhe um polígono antes de aplicar uma transformação."
            );

            return;
        }

        switch (transformacaoSelecionada) {

            case "TRANSLACAO":

                ultimoPoligono =
                    transformacoes.transladar(
                        ultimoPoligono,
                        direcao * 10
                    );

                break;

            case "ESCALA":

                double fator;

                if (direcao > 0) {
                    fator = 1.1;
                } else {
                    fator = 1.0 / 1.1;
                }

                ultimoPoligono =
                    transformacoes.escalar(
                        ultimoPoligono,
                        fator
                    );

                break;

            case "ROTACAO":

                ultimoPoligono =
                    transformacoes.rotacionar(
                        ultimoPoligono,
                        direcao * 10
                    );

                break;
        }

        redesenharPoligonoTransformado();
    }

    private void redesenharPoligonoTransformado() {

        principal.getPainel().limpar();

        PoligonoVarredura varredura =
            new PoligonoVarredura();

        List<Ponto> preenchimento =
            varredura.preencher(
                ultimoPoligono
            );

        principal
            .getPainel()
            .desenharPixels(
                preenchimento,
                corUltimoPoligono
            );

        RetaBresenham bresenham =
            new RetaBresenham();

        for (int i = 0; i < ultimoPoligono.size(); i++) {

            Ponto inicio =
                ultimoPoligono.get(i);

            Ponto fim =
                ultimoPoligono.get(
                    (i + 1) % ultimoPoligono.size()
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
                    corUltimoPoligono
                );
        }

        switch (transformacaoSelecionada) {

            case "TRANSLACAO":

                principal.setMensagem(
                    "Translação ativa - use ← ou →."
                );

                break;

            case "ESCALA":

                principal.setMensagem(
                    "Escala ativa - use ← para diminuir ou → para aumentar."
                );

                break;

            case "ROTACAO":

                principal.setMensagem(
                    "Rotação ativa - use ← para -10° ou → para +10°."
                );

                break;
        }
    }

    private void tratarClique(int x, int y) {

        if (algoritmoSelecionado == null) {

            principal.setMensagem(
                "Selecione um algoritmo antes de desenhar."
            );

            return;
        }

        if (algoritmoSelecionado.equals("COHEN_SUTHERLAND")) {

            tratarCliqueRecorte(x, y);

            return;
        }

        if (algoritmoSelecionado.equals("POLIGONO_VARREDURA")) {

            tratarCliquePoligono(x, y);

            return;
        }

        if (algoritmoSelecionado.equals("BEZIER") ||
            algoritmoSelecionado.equals("BSPLINE")) {

            tratarCliqueCurva(x, y);

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

    private void tratarCliqueCurva(int x, int y) {

        pontosCurva.add(
            new Ponto(x, y)
        );

        int quantidadeAtual = pontosCurva.size();

        if (quantidadeAtual < quantidadePontosCurva) {

            principal.setMensagem(
                nomeAlgoritmo() +
                " - ponto " +
                quantidadeAtual +
                " definido. Clique no ponto " +
                (quantidadeAtual + 1) +
                " de " +
                quantidadePontosCurva + "."
            );

            return;
        }

        desenharCurva();

        pontosCurva.clear();

        principal.setMensagem(
            nomeAlgoritmo() +
            " - curva concluída. Clique no ponto 1 de " +
            quantidadePontosCurva +
            " para desenhar novamente."
        );
    }

    private void desenharCurva() {

        List<Ponto> pontos;

        if (algoritmoSelecionado.equals("BEZIER")) {

            CurvaBezier algoritmo =
                new CurvaBezier();

            pontos = algoritmo.rasterizar(
                pontosCurva
            );

        } else {

            CurvaBSpline algoritmo =
                new CurvaBSpline();

            pontos = algoritmo.rasterizar(
                pontosCurva
            );
        }

        if (pontos.isEmpty()) {
            return;
        }

        List<Ponto> pixels = new ArrayList<>();

        RetaBresenham bresenham =
            new RetaBresenham();

        pixels.add(pontos.get(0));

        for (int i = 1; i < pontos.size(); i++) {

            List<Ponto> segmento =
                bresenham.rasterizar(
                    pontos.get(i - 1),
                    pontos.get(i)
                );

            pixels.addAll(segmento);
        }

        principal
            .getPainel()
            .desenharPixels(
                pixels,
                gerarCorAleatoria()
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
                quantidadePontosPoligono + "."
            );

            return;
        }

        desenharPoligonoVarredura();

        pontosPoligono.clear();

        principal.setMensagem(
            "Polígono preenchido por Varredura - selecione uma transformação ou desenhe outro."
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

        ultimoPoligono =
            new ArrayList<>(pontosPoligono);

        corUltimoPoligono = cor;
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

    private void gerarElementosRecorte() {

        principal.getPainel().limpar();

        segmentosRecorte.clear();

        segmentosRecorte.add(
            new Ponto[] {
                new Ponto(100, 100),
                new Ponto(800, 500)
            }
        );

        segmentosRecorte.add(
            new Ponto[] {
                new Ponto(50, 300),
                new Ponto(850, 300)
            }
        );

        segmentosRecorte.add(
            new Ponto[] {
                new Ponto(250, 50),
                new Ponto(250, 550)
            }
        );

        segmentosRecorte.add(
            new Ponto[] {
                new Ponto(600, 80),
                new Ponto(820, 180)
            }
        );

        segmentosRecorte.add(
            new Ponto[] {
                new Ponto(100, 500),
                new Ponto(400, 400)
            }
        );

        Ponto p1 = new Ponto(480, 130);
        Ponto p2 = new Ponto(720, 160);
        Ponto p3 = new Ponto(760, 430);
        Ponto p4 = new Ponto(500, 460);

        segmentosRecorte.add(
            new Ponto[] { p1, p2 }
        );

        segmentosRecorte.add(
            new Ponto[] { p2, p3 }
        );

        segmentosRecorte.add(
            new Ponto[] { p3, p4 }
        );

        segmentosRecorte.add(
            new Ponto[] { p4, p1 }
        );

        RetaBresenham bresenham =
            new RetaBresenham();

        for (Ponto[] segmento : segmentosRecorte) {

            List<Ponto> pixels =
                bresenham.rasterizar(
                    segmento[0],
                    segmento[1]
                );

            principal
                .getPainel()
                .desenharPixels(
                    pixels,
                    Color.GRAY
                );
        }
    }

    private void tratarCliqueRecorte(int x, int y) {

        Ponto pontoAtual =
            new Ponto(x, y);

        if (primeiroPontoRecorte == null) {

            primeiroPontoRecorte =
                pontoAtual;

            principal.setMensagem(
                "Primeiro canto definido em (" +
                x + ", " + y +
                "). Clique no canto oposto."
            );

            return;
        }

        int xMin =
            Math.min(
                primeiroPontoRecorte.getX(),
                pontoAtual.getX()
            );

        int xMax =
            Math.max(
                primeiroPontoRecorte.getX(),
                pontoAtual.getX()
            );

        int yMin =
            Math.min(
                primeiroPontoRecorte.getY(),
                pontoAtual.getY()
            );

        int yMax =
            Math.max(
                primeiroPontoRecorte.getY(),
                pontoAtual.getY()
            );

        executarRecorte(
            xMin,
            yMin,
            xMax,
            yMax
        );

        primeiroPontoRecorte = null;

        principal.setMensagem(
            "Recorte concluído - clique em dois novos pontos para definir outra janela de recorte."
        );
    }

    private void executarRecorte(
        int xMin,
        int yMin,
        int xMax,
        int yMax
    ) {

        principal.getPainel().limpar();

        CohenSutherland cohen =
            new CohenSutherland();

        RetaBresenham bresenham =
            new RetaBresenham();

        Color corRecorte =
            gerarCorAleatoria();

        for (Ponto[] segmento : segmentosRecorte) {

            List<Ponto> resultado =
                cohen.recortar(
                    segmento[0],
                    segmento[1],
                    xMin,
                    yMin,
                    xMax,
                    yMax
                );

            if (resultado.size() == 2) {

                List<Ponto> pixels =
                    bresenham.rasterizar(
                        resultado.get(0),
                        resultado.get(1)
                    );

                principal
                    .getPainel()
                    .desenharPixels(
                        pixels,
                        corRecorte
                    );
            }
        }

        desenharRetanguloRecorte(
            xMin,
            yMin,
            xMax,
            yMax
        );
    }

    private void desenharRetanguloRecorte(
        int xMin,
        int yMin,
        int xMax,
        int yMax
    ) {

        RetaBresenham bresenham =
            new RetaBresenham();

        Ponto superiorEsquerdo =
            new Ponto(xMin, yMin);

        Ponto superiorDireito =
            new Ponto(xMax, yMin);

        Ponto inferiorDireito =
            new Ponto(xMax, yMax);

        Ponto inferiorEsquerdo =
            new Ponto(xMin, yMax);

        Ponto[][] lados = {

            {
                superiorEsquerdo,
                superiorDireito
            },

            {
                superiorDireito,
                inferiorDireito
            },

            {
                inferiorDireito,
                inferiorEsquerdo
            },

            {
                inferiorEsquerdo,
                superiorEsquerdo
            }
        };

        for (Ponto[] lado : lados) {

            List<Ponto> pixels =
                bresenham.rasterizar(
                    lado[0],
                    lado[1]
                );

            principal
                .getPainel()
                .desenharPixels(
                    pixels,
                    Color.BLACK
                );
        }
    }

    private String nomeAlgoritmo() {

        if (algoritmoSelecionado == null) {
            return "";
        }

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

            case "COHEN_SUTHERLAND":
                return "Cohen-Sutherland";

            case "BEZIER":
                return "Bézier";

            case "BSPLINE":
                return "B-Spline";

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
