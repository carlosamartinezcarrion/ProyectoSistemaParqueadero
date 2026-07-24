package edu.unl.cc.poo.domain;

import jakarta.persistence.*;
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
@Entity
@Table(name = "registro")
public class Registro {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "uuid", nullable = false, unique = true, length = 50)
    private String uuid;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehiculo_id", nullable = false)
    private Vehiculo vehiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "espacio_id", nullable = false)
    private EspacioParqueadero espacio;

    @Column(name = "fecha_hora_entrada", nullable = false)
    private LocalDateTime fechaHoraEntrada;

    @Column(name = "fecha_hora_salida")
    private LocalDateTime fechaHoraSalida;

    @Column(name = "duracion_minutos")
    private long duracionMinutos;

    @Column(name = "total_cobrado", precision = 10, scale = 2)
    private double totalCobrado;

    @Column(name = "placa", length = 20)
    private String placa;

    @Column(name = "conductor", length = 100)
    private String nombreConductor;

    @Column(name = "tipo_vehiculo", length = 20)
    private String tipoVehiculo;

    @Column(name = "espacio_etiqueta", length = 20)
    private String espacioEtiqueta;

    @Column(name = "moneda", length = 10)
    private String moneda = "USD";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parqueadero_id", nullable = false)
    private Parqueadero parqueadero;

    @Transient
    private List<Registro> registros = new ArrayList<>();

    public Registro() {}

    public Registro(Vehiculo vehiculo, EspacioParqueadero espacio) {
        this.vehiculo = vehiculo;
        this.espacio = espacio;
        this.fechaHoraEntrada = LocalDateTime.now();
        this.uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.registros = new ArrayList<>();
        this.placa = vehiculo.getPlaca();
        this.nombreConductor = vehiculo.getNombreConductor();
        this.tipoVehiculo = vehiculo.getTipo().name();
        this.espacioEtiqueta = espacio.getEtiqueta();
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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUuid() { return uuid; }
    public void setUuid(String uuid) { this.uuid = uuid; }

    public String getPlaca() { return placa; }
    public String getNombreConductor() { return nombreConductor; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public String getEspacioEtiqueta() { return espacioEtiqueta; }

    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }

    public EspacioParqueadero getEspacio() { return espacio; }
    public void setEspacio(EspacioParqueadero espacio) { this.espacio = espacio; }

    public LocalDateTime getFechaHoraEntrada() { return fechaHoraEntrada; }
    public void setFechaHoraEntrada(LocalDateTime fechaHoraEntrada) { this.fechaHoraEntrada = fechaHoraEntrada; }

    public LocalDateTime getFechaHoraSalida() { return fechaHoraSalida; }
    public void setFechaHoraSalida(LocalDateTime fechaHoraSalida) { this.fechaHoraSalida = fechaHoraSalida; }

    public long getDuracionMinutos() { return duracionMinutos; }
    public void setDuracionMinutos(long duracionMinutos) { this.duracionMinutos = duracionMinutos; }

    public double getTotalCobrado() { return totalCobrado; }
    public void setTotalCobrado(double totalCobrado) { this.totalCobrado = totalCobrado; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public Parqueadero getParqueadero() { return parqueadero; }
    public void setParqueadero(Parqueadero parqueadero) { this.parqueadero = parqueadero; }

    public List<Registro> getRegistros() { return registros; }
    public void setRegistros(List<Registro> registros) { this.registros = registros; }

    @Override
    public String toString() {
        return String.format("Registro{id='%s', placa='%s', espacio='%s', entrada=%s, tipo vehiculo=%s}",
                uuid, vehiculo.getPlaca(), espacio.getEtiqueta(),
                fechaHoraEntrada.format(FORMATO_FECHA), vehiculo.getTipo());
    }
}