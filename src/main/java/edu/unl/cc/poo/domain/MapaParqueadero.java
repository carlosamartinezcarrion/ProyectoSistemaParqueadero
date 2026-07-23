package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.EstadoEspacio;

import java.util.ArrayList;
import java.util.List;

/**
 * Gestiona la cuadricula bidimensional de espacios del parqueadero.
 */
public class MapaParqueadero {

    private int filas;
    private int columnas;
    private List<EspacioParqueadero> espacios;

    public MapaParqueadero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.espacios = new ArrayList<>();
        inicializarEspacios();
    }


    public EspacioParqueadero getEspacio(int fila, int columna) {
        return espacios.stream()
                .filter(e -> e.getFila() == fila && e.getColumna() == columna)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe un espacio en la posición (" + fila + ", " + columna + ")."));
    }


    public EspacioParqueadero getPrimerEspacioLibre() {
        return espacios.stream()
                .filter(EspacioParqueadero::estaLibre)
                .findFirst()
                .orElse(null);
    }


    public void redimensionar(int nuevasFilas, int nuevasColumnas) {
        this.filas = nuevasFilas;
        this.columnas = nuevasColumnas;
        this.espacios = new ArrayList<>();
        inicializarEspacios();
    }

    public void inicializarEspacios() {
        int numero = 1;
        for (int f = 0; f < filas; f++) {
            for (int c = 0; c < columnas; c++) {
                espacios.add(new EspacioParqueadero(numero++, f, c));
            }
        }
    }

    public long contarLibres() {
        return espacios.stream().filter(EspacioParqueadero::estaLibre).count();
    }

    public long contarOcupados() {
        return espacios.stream()
                .filter(e -> e.getEstado() == EstadoEspacio.OCUPADO)
                .count();
    }

    public long contarInhabilitados() {
        return espacios.stream()
                .filter(e -> e.getEstado() == EstadoEspacio.INHABILITADO)
                .count();
    }

    public void validarPosicion(int fila, int columna) {
        if (fila < 0 || fila >= filas || columna < 0 || columna >= columnas) {
            throw new IllegalArgumentException(
                    "Posicion (" + fila + "," + columna + ") fuera del rango del mapa.");
        }
    }

    public int getFilas()    { return filas; }
    public int getColumnas() { return columnas; }
    public List<EspacioParqueadero> getEspacios() { return espacios; }
}
