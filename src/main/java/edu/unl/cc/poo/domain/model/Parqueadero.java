package edu.unl.cc.poo.domain.model;

import edu.unl.cc.poo.domain.enums.EstadoEspacio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Orquesta el registro de entradas/salidas,
 * consulta de espacios y generacion de resumenes.
 */
public class Parqueadero {

    private String nombre;
    private int capacidad;
    private MapaParqueadero mapa;
    private Configuracion configuracion;

    private final List<Registro> historial;

    public Parqueadero(String nombre, Configuracion configuracion) {
        this.nombre = nombre != null && !nombre.isBlank()
                ? nombre
                : configuracion.getNombreParqueadero();
        this.configuracion = configuracion;
        this.mapa = new MapaParqueadero(
                configuracion.getFilasDefecto(),
                configuracion.getColumnasDefecto());
        this.capacidad = configuracion.getFilasDefecto() * configuracion.getColumnasDefecto();
        this.historial = new ArrayList<>();
    }

    public Registro registrarEntrada(Vehiculo vehiculo, Integer fila, Integer columna) {
        if (vehiculo == null) {
            throw new IllegalArgumentException("El vehículo es obligatorio.");
        }

        // Evitar doble entrada de la misma placa
        Registro activoMismaPlaca = buscarRegistroActivoPorPlaca(vehiculo.getPlaca());
        if (activoMismaPlaca != null) {
            throw new IllegalStateException(
                    "Ya existe un registro activo para la placa "
                            + vehiculo.getPlaca() + " (ID: " + activoMismaPlaca.getId() + ").");
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
        espacio.ocupar(registro.getId());
        historial.add(registro);
        return registro;
    }

    public Registro registrarSalida(String registroId) {
        Registro registro = buscarRegistroActivo(registroId);
        if (registro == null) {
            // Permitir salir también por placa
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

    /**
     * Libera un espacio ocupado forzando el cierre del registro activo asociado
     * (útil si se perdió la referencia del ticket).
     */
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
                .filter(r -> r.getId().equalsIgnoreCase(id) && r.estaActivo())
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
                        || r.getId().toLowerCase().equals(criterio)
                        || r.getId().toLowerCase().contains(criterio))
                .findFirst()
                .orElse(null);
    }

    /**
     * Aplica cambios de nombre y dimensiones desde la configuración.
     * No redimensiona si hay registros activos (vehículos dentro).
     */
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public MapaParqueadero getMapa() {
        return mapa;
    }

    public Configuracion getConfiguracion() {
        return configuracion;
    }

    public List<Registro> getHistorial() {
        return historial;
    }
}
