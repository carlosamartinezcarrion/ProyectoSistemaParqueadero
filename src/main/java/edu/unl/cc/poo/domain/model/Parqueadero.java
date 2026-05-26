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
        this.nombre = nombre;
        this.configuracion = configuracion;
        this.mapa = new MapaParqueadero(
                configuracion.getFilasDefecto(),
                configuracion.getColumnasDefecto());
        this.capacidad = configuracion.getFilasDefecto() * configuracion.getColumnasDefecto();
        this.historial = new ArrayList<>();
    }


    public Registro registrarEntrada(Vehiculo vehiculo) {
        EspacioParqueadero espacio = mapa.getPrimerEspacioLibre();
        if (espacio == null) {
            throw new IllegalStateException("No hay espacios disponibles en el parqueadero.");
        }
        return registrarEntrada(vehiculo, espacio.getFila(), espacio.getColumna());
    }


    public Registro registrarEntrada(Vehiculo vehiculo, int fila, int columna) {
        mapa.validarPosicion(fila, columna);
        EspacioParqueadero espacio = mapa.getEspacio(fila, columna);

        if (espacio == null || !espacio.estaLibre()) {
            throw new IllegalStateException(
                    "El espacio (" + fila + "," + columna + ") no esta disponible.");
        }

        Registro registro = new Registro(vehiculo, espacio);
        espacio.ocupar(registro.getId());
        historial.add(registro);
        return registro;
    }


    public Registro registrarSalida(String registroId) {
        Registro registro = buscarRegistroActivo(registroId);
        if (registro == null) {
            throw new IllegalArgumentException("No se encontro un registro activo con ID: " + registroId);
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
        if (espacio != null) {
            espacio.setEstado(estado);
        }
    }


    public void imprimirResumen() {
        System.out.println("=================================================");
        System.out.println("  RESUMEN PARQUEADERO: " + nombre);
        System.out.println("=================================================");
        System.out.printf("  Capacidad total  : %d espacios%n", capacidad);
        System.out.printf("  Espacios libres  : %d%n", mapa.contarLibres());
        System.out.printf("  Espacios ocupados: %d%n", mapa.contarOcupados());
        System.out.printf("  Inhabilitados    : %d%n", mapa.contarInhabilitados());
        System.out.printf("  Moneda           : %s%n", configuracion.getMoneda());
        System.out.println("=================================================");
    }


    public Registro buscarRegistroActivo(String registroId) {
        return historial.stream()
                .filter(r -> r.getId().equalsIgnoreCase(registroId) && r.estaActivo())
                .findFirst()
                .orElse(null);
    }


    public String getNombre()                  { return nombre; }
    public void setNombre(String nombre)       { this.nombre = nombre; }
    public int getCapacidad()                  { return capacidad; }
    public MapaParqueadero getMapa()           { return mapa; }
    public Configuracion getConfiguracion()    { return configuracion; }
    public List<Registro> getHistorial()       { return historial; }
}
