package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import jakarta.persistence.*;

@Entity
@Table(name = "espacio_parqueadero")
public class EspacioParqueadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero", nullable = false)
    private int numero;

    @Column(name = "fila", nullable = false)
    private int fila;

    @Column(name = "columna", nullable = false)
    private int columna;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoEspacio estado = EstadoEspacio.LIBRE;

    @Column(name = "id_registro_activo", length = 50)
    private String idRegistroActivo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mapa_parqueadero_id", nullable = false)
    private MapaParqueadero mapa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private Parqueadero parqueadero;

    public EspacioParqueadero() {}

    public EspacioParqueadero(int numero, int fila, int columna) {
        this.numero = numero;
        this.fila = fila;
        this.columna = columna;
    }

    public String getEtiqueta() {
        char letra = (char) ('A' + fila);
        return String.valueOf(letra) + (columna + 1);
    }

    public boolean estaLibre() { return estado == EstadoEspacio.LIBRE; }
    public boolean estaOcupado() { return estado == EstadoEspacio.OCUPADO; }
    public boolean estaInhabilitado() { return estado == EstadoEspacio.INHABILITADO; }

    public void setEstado(EstadoEspacio estado) {
        this.estado = estado;
        if (estado != EstadoEspacio.OCUPADO) {
            this.idRegistroActivo = null;
        }
    }

    public void ocupar(String idRegistro) {
        if (!estaLibre()) {
            throw new IllegalStateException(
                    "El espacio " + getEtiqueta() + " no está libre y no puede ser ocupado.");
        }
        this.estado = EstadoEspacio.OCUPADO;
        this.idRegistroActivo = idRegistro;
    }

    public void liberar() {
        if (!estaOcupado()) {
            throw new IllegalStateException(
                    "El espacio " + getEtiqueta() + " no está ocupado y no puede ser liberado.");
        }
        this.estado = EstadoEspacio.LIBRE;
        this.idRegistroActivo = null;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getNumero() { return numero; }
    public void setNumero(int numero) { this.numero = numero; }

    public int getFila() { return fila; }
    public void setFila(int fila) { this.fila = fila; }

    public int getColumna() { return columna; }
    public void setColumna(int columna) { this.columna = columna; }

    public EstadoEspacio getEstado() { return estado; }

    public String getIdRegistroActivo() { return idRegistroActivo; }
    public void setIdRegistroActivo(String idRegistroActivo) { this.idRegistroActivo = idRegistroActivo; }

    public MapaParqueadero getMapa() { return mapa; }
    public void setMapa(MapaParqueadero mapa) { this.mapa = mapa; }
}