package edu.unl.cc.poo.domain;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Registra la permanencia de un vehiculo en un espacio del parqueadero.
 * Calcula duracion y total a cobrar en USD.
 */
public class Registro {

    private static final DateTimeFormatter FORMATO_FECHA = 
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private String id;
    private Vehiculo vehiculo;
    private EspacioParqueadero espacio;
    private LocalDateTime fechaHoraEntrada;
    private LocalDateTime fechaHoraSalida;
    private long duracionMinutos;
    private double totalCobrado;
    private String moneda;
    private List<Registro> registros;

    public Registro(Vehiculo vehiculo, EspacioParqueadero espacio) {
        this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.fechaHoraEntrada = LocalDateTime.now();
        this.moneda = "USD";
        this.registros = new ArrayList<>();
    }


    public long calcularDuracion() {
        if (fechaHoraSalida == null) return 0L;
        return ChronoUnit.MINUTES.between(fechaHoraEntrada, fechaHoraSalida);
    }


    public double calcularTotal(Tarifa tarifa) {
        this.duracionMinutos = calcularDuracion();
        this.totalCobrado = tarifa.calcularCostos(duracionMinutos);
        return totalCobrado;
    }

    public boolean estaActivo() {
        return fechaHoraSalida == null;
    }

    public void agregarRegistro(Registro r) {
        this.registros.add(r);
    }

    public List<Registro> buscarPorPlaca(String placa) {
        return registros.stream()
                .filter(r -> r.getVehiculo().getPlaca().equalsIgnoreCase(placa))
                .toList();
    }

    public List<Registro> filtrarPorFecha(LocalDateTime fecha) {
        return registros.stream()
                .filter(r -> r.getFechaHoraEntrada().toLocalDate()
                              .equals(fecha.toLocalDate()))
                .toList();
    }

    public double getTotalRecaudado() {
        return registros.stream()
                .mapToDouble(Registro::getTotalCobrado)
                .sum();
    }

    public List<Registro> getRegistrosActivos() {
        return registros.stream()
                .filter(Registro::estaActivo)
                .toList();
    }

    public String getId()                         { return id; }
    public Vehiculo getVehiculo()                 { return vehiculo; }
    public EspacioParqueadero getEspacio()        { return espacio; }
    public LocalDateTime getFechaHoraEntrada()    { return fechaHoraEntrada; }
    public LocalDateTime getFechaHoraSalida()     { return fechaHoraSalida; }
    public long getDuracionMinutos()              { return duracionMinutos; }
    public double getTotalCobrado()               { return totalCobrado; }
    public String getMoneda()                     { return moneda; }
    public List<Registro> getRegistros()          { return registros; }

    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) {
        this.fechaHoraSalida = fechaHoraSalida;
    }

    @Override
    public String toString() {
        return String.format("Registro{id='%s', placa='%s', espacio='%s', entrada=%s, tipo vehiculo=%s}",
                id, vehiculo.getPlaca(), espacio.getEtiqueta(), 
                fechaHoraEntrada.format(FORMATO_FECHA), vehiculo.getTipo());
    }
}
