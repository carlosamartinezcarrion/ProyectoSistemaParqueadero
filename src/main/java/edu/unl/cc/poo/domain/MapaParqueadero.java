package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "mapa_parqueadero")
public class MapaParqueadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "filas", nullable = false)
    private int filas;

    @Column(name = "columnas", nullable = false)
    private int columnas;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parqueadero_id", nullable = false, unique = true)
    private Parqueadero parqueadero;

    @OneToMany(mappedBy = "mapa", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<EspacioParqueadero> espacios = new ArrayList<>();

    public MapaParqueadero() {}

    public MapaParqueadero(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
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
                EspacioParqueadero espacio = new EspacioParqueadero(numero++, f, c);
                espacio.setMapa(this);
                espacios.add(espacio);
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getFilas() { return filas; }
    public void setFilas(int filas) { this.filas = filas; }

    public int getColumnas() { return columnas; }
    public void setColumnas(int columnas) { this.columnas = columnas; }

    public Parqueadero getParqueadero() { return parqueadero; }
    public void setParqueadero(Parqueadero parqueadero) { this.parqueadero = parqueadero; }

    public List<EspacioParqueadero> getEspacios() { return espacios; }
    public void setEspacios(List<EspacioParqueadero> espacios) { this.espacios = espacios; }
}