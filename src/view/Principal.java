package view;

import javax.swing.*;
import java.awt.*;

public class Principal extends JFrame {

    private static final long serialVersionUID = 2;

    private Painel painel;

    private JMenuItem itemAnalitico;
    private JMenuItem itemDDA;
    private JMenuItem itemBresenhamReta;
    private JMenuItem itemBresenhamCirculo;
    private JMenuItem itemVarredura;

    private JMenuItem itemTranslacao;
    private JMenuItem itemEscala;
    private JMenuItem itemRotacao;

    private JLabel mensagem;
    private JButton botaoLimpar;

    public Principal() {

        setTitle("Rasterização de Primitivas");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        painel = new Painel();

        mensagem = new JLabel(
            "Selecione um algoritmo."
        );

        mensagem.setBorder(
            BorderFactory.createEmptyBorder(
                5, 8, 5, 8
            )
        );

        criarMenu();

        setLayout(new BorderLayout());

        add(painel, BorderLayout.CENTER);
        add(mensagem, BorderLayout.SOUTH);

        pack();

        setLocationRelativeTo(null);

        setResizable(false);
    }

    private void criarMenu() {

        JMenuBar barraMenu = new JMenuBar();

        JMenu menuPrimitivas = new JMenu("Primitivas");

        JMenu menuLinhas = new JMenu("Linhas");

        itemAnalitico = new JMenuItem("Analítico");
        itemDDA = new JMenuItem("DDA");
        itemBresenhamReta = new JMenuItem("Bresenham");

        menuLinhas.add(itemAnalitico);
        menuLinhas.add(itemDDA);
        menuLinhas.add(itemBresenhamReta);

        JMenu menuCirculos = new JMenu("Círculos");

        itemBresenhamCirculo = new JMenuItem("Bresenham");

        menuCirculos.add(itemBresenhamCirculo);

        JMenu menuPoligonos = new JMenu("Polígonos");

        itemVarredura = new JMenuItem("Varredura");

        menuPoligonos.add(itemVarredura);

        menuPrimitivas.add(menuLinhas);
        menuPrimitivas.add(menuCirculos);
        menuPrimitivas.add(menuPoligonos);

        JMenu menuTransformacoes = new JMenu("Transformações");

        itemTranslacao = new JMenuItem("Translação");
        itemEscala = new JMenuItem("Escala");
        itemRotacao = new JMenuItem("Rotação");

        menuTransformacoes.add(itemTranslacao);
        menuTransformacoes.add(itemEscala);
        menuTransformacoes.add(itemRotacao);

        barraMenu.add(menuPrimitivas);
        barraMenu.add(menuTransformacoes);

        barraMenu.add(
            Box.createHorizontalStrut(20)
        );

        botaoLimpar = new JButton("Limpar");

        barraMenu.add(botaoLimpar);

        setJMenuBar(barraMenu);
    }

    public Painel getPainel() {
        return painel;
    }

    public JMenuItem getItemAnalitico() {
        return itemAnalitico;
    }

    public JMenuItem getItemDDA() {
        return itemDDA;
    }

    public JMenuItem getItemBresenhamReta() {
        return itemBresenhamReta;
    }

    public JMenuItem getItemBresenhamCirculo() {
        return itemBresenhamCirculo;
    }

    public JMenuItem getItemVarredura() {
        return itemVarredura;
    }

    public JMenuItem getItemTranslacao() {
        return itemTranslacao;
    }

    public JMenuItem getItemEscala() {
        return itemEscala;
    }

    public JMenuItem getItemRotacao() {
        return itemRotacao;
    }

    public JButton getBotaoLimpar() {
        return botaoLimpar;
    }

    public void setMensagem(String texto) {
        mensagem.setText(texto);
    }
}