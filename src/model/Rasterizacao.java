package model;

import java.util.List;

public interface Rasterizacao {

    List<Ponto> rasterizar(Ponto inicio, Ponto fim);
}