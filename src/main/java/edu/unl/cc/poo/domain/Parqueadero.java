package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta el registro de entradas/salidas,
 * consulta de espacios y generacion de resumenes.
 */
@Entity
@Table(name = "parqueadero")
public class Parqueadero {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Min(value = 1, message = "La capacidad debe ser al menos 1")
    @Column(name = "capacidad", nullable = false)
    private int capacidad;

    @NotNull(message = "La configuración es obligatoria")
    @Valid
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracion_id", nullable = false, unique = true)
    private Configuracion configuracion;

    @OneToOne(mappedBy = "parqueadero", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private MapaParqueadero mapa;

    @OneToMany(mappedBy = "parqueadero", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Registro> historial = new ArrayList<>();

    public Parqueadero() {}

    public Parqueadero(String nombre, Configuracion configuracion) {
        this.nombre = nombre;
        this.configuracion = configuracion;
        if (configuracion != null) {
            this.capacidad = configuracion.getFilasDefecto() * configuracion.getColumnasDefecto();
        }
    }

    public Registro registrarEntrada(Vehiculo vehiculo, Integer fila, Integer columna) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio.");
        }

        Registro activoMismaPlaca = buscarRegistroActivoPorPlaca(vehiculo.getPlaca());
        if (activoMismaPlaca != null) {
            throw new IllegalStateException(
                    "Ya existe un registro activo para la placa "
                            + vehiculo.getPlaca() + " (ID: " + activoMismaPlaca.getUuid() + ").");
        }

        EspacioParqueadero espacio;

        if (fila == null || columna == null) {
            espacio = mapa.getPrimerEspacioLibre();
            if (espacio == null) {
                throw new IllegalStateException("No hay espacios disponibles en el parqueadero.");
            }
        } else {
            mapa.validarPosicion(fila, columna);
            espacio = mapa.getEspacio(fila, columna);

            if (!espacio.estaLibre()) {
                throw new IllegalStateException(
                        "El espacio (" + espacio.getEtiqueta() + ") no está disponible.");
            }
        }

        Registro registro = new Registro(vehiculo, espacio);
        registro.setParqueadero(this);
        espacio.ocupar(registro.getUuid());
        historial.add(registro);
        return registro;
    }

    public Registro registrarSalida(String registroId) {
        Registro registro = buscarRegistroActivo(registroId);
        if (registro == null) {
            registro = buscarRegistroActivoPorPlaca(registroId);
        }
        if (registro == null) {
            throw new IllegalArgumentException(
                    "No se encontró un registro activo con ID o placa: " + registroId);
        }

        registro.setFechaHoraSalida(LocalDateTime.now());
        Tarifa tarifa = configuracion.getTarifaPorTipo(registro.getVehiculo().getTipo());
        registro.calcularTotal(tarifa);

        registro.getEspacio().liberar();
        return registro;
    }

    public int getEspaciosDisponibles() {
        return (int) mapa.contarLibres();
    }

    public void setEstadoEspacio(int fila, int columna, EstadoEspacio estado) {
        mapa.validarPosicion(fila, columna);
        EspacioParqueadero espacio = mapa.getEspacio(fila, columna);
        if (estado == null) {
            throw new IllegalArgumentException("El estado no puede ser nulo.");
        }
        if (estado == EstadoEspacio.OCUPADO) {
            throw new IllegalArgumentException("El estado OCUPADO se asigna al registrar una entrada.");
        }
        if (espacio.estaOcupado()) {
            throw new IllegalStateException(
                    "El espacio " + espacio.getEtiqueta()
                            + " está ocupado. Procesa la salida del vehículo antes de cambiar su estado.");
        }
        espacio.setEstado(estado);
    }

    public Registro forzarLiberacionEspacio(int fila, int columna) {
        mapa.validarPosicion(fila, columna);
        EspacioParqueadero espacio = mapa.getEspacio(fila, columna);
        if (!espacio.estaOcupado()) {
            espacio.setEstado(EstadoEspacio.LIBRE);
            return null;
        }
        String idRegistro = espacio.getIdRegistroActivo();
        if (idRegistro != null) {
            return registrarSalida(idRegistro);
        }
        espacio.liberar();
        return null;
    }

    public Registro buscarRegistroActivo(String registroId) {
        if (registroId == null || registroId.isBlank()) {
            return null;
        }
        String id = registroId.trim();
        return historial.stream()
                .filter(r -> r.getUuid().equalsIgnoreCase(id) && r.estaActivo())
                .findFirst()
                .orElse(null);
    }

    public Registro buscarRegistroActivoPorPlaca(String placa) {
        if (placa == null || placa.isBlank()) {
            return null;
        }
        String criterio = placa.trim().toLowerCase();
        return historial.stream()
                .filter(Registro::estaActivo)
                .filter(r -> r.getVehiculo().getPlaca().toLowerCase().equals(criterio)
                        || r.getVehiculo().getPlaca().toLowerCase().contains(criterio)
                        || r.getUuid().toLowerCase().equals(criterio)
                        || r.getUuid().toLowerCase().contains(criterio))
                .findFirst()
                .orElse(null);
    }

    public void aplicarConfiguracion() {
        this.nombre = configuracion.getNombreParqueadero();
        int nuevasFilas = configuracion.getFilasDefecto();
        int nuevasColumnas = configuracion.getColumnasDefecto();

        boolean cambiaMapa = mapa.getFilas() != nuevasFilas || mapa.getColumnas() != nuevasColumnas;
        if (cambiaMapa) {
            long activos = historial.stream().filter(Registro::estaActivo).count();
            if (activos > 0) {
                throw new IllegalStateException(
                        "No se pueden cambiar las dimensiones mientras haya "
                                + activos + " vehículo(s) estacionado(s). Procesa las salidas primero.");
            }
            mapa.redimensionar(nuevasFilas, nuevasColumnas);
            this.capacidad = nuevasFilas * nuevasColumnas;
        }
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }

    public Configuracion getConfiguracion() { return configuracion; }
    public void setConfiguracion(Configuracion configuracion) { this.configuracion = configuracion; }

    public MapaParqueadero getMapa() { return mapa; }
    public void setMapa(MapaParqueadero mapa) { this.mapa = mapa; }

    public List<Registro> getHistorial() { return historial; }
    public void setHistorial(List<Registro> historial) { this.historial = historial; }
}