package edu.unl.cc.poo.domain.model;

import edu.unl.cc.poo.domain.enums.EstadoEspacio;

/**
 * Representa un espacio fisico dentro del mapa del parqueadero.
 * Conoce su posicion (fila, columna), su estado y el registro activo.
 */
public class EspacioParqueadero {

    private int numero;
    private int fila;
    private int columna;
    private EstadoEspacio estado;
    private String idRegistroActivo;

    public EspacioParqueadero(int numero, int fila, int columna) {
        this.numero = numero;
        this.fila = fila;
        this.columna = columna;
        this.estado = EstadoEspacio.LIBRE;
    }


    public String getEtiqueta() {
        char letra = (char) ('A' + fila);
        return String.valueOf(letra) + (columna + 1);
    }

    public boolean estaLibre() {
        return estado == EstadoEspacio.LIBRE;
    }

    public boolean estaOcupado() {
        return estado == EstadoEspacio.OCUPADO;
    }

    public boolean estaInhabilitado() {
        return estado == EstadoEspacio.INHABILITADO;
    }

    public void setEstado(EstadoEspacio estado) {
        this.estado = estado;
        if (estado != EstadoEspacio.OCUPADO) {
            this.idRegistroActivo = null;
        }
    }


    public void ocupar(String idRegistro) {
        this.estado = EstadoEspacio.OCUPADO;
        this.idRegistroActivo = idRegistro;
    }


    public void liberar() {
        this.estado = EstadoEspacio.LIBRE;
        this.idRegistroActivo = null;
    }


    public int getNumero()            { return numero; }
    public int getFila()              { return fila; }
    public int getColumna()           { return columna; }
    public EstadoEspacio getEstado()  { return estado; }
    public String getIdRegistroActivo() { return idRegistroActivo; }
}
