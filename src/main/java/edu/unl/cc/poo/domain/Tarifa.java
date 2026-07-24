package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import com.google.gson.annotations.Expose;

@Entity
@Table(name = "tarifa")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Long id;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_vehiculo", nullable = false, length = 20, unique = true)
    @Expose
    private TipoVehiculo tipoVehiculo;

    @NotNull(message = "El precio por hora es obligatorio")
    @DecimalMin(value = "0.01", message = "El precio por hora debe ser mayor a 0")
    @Digits(integer = 8, fraction = 2, message = "El precio debe tener máximo 8 dígitos enteros y 2 decimales")
    @Column(name = "precio_por_hora", nullable = false, precision = 10, scale = 2)
    @Expose
    private Double precioPorHora;

    @NotNull(message = "La fracción de minutos es obligatoria")
    @Min(value = 1, message = "La fracción de minutos debe ser al menos 1")
    @Max(value = 1440, message = "La fracción de minutos no puede exceder 1440 (24 horas)")
    @Column(name = "fraccion_minutos", nullable = false)
    @Expose
    private Integer fraccionMinutos;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "configuracion_id", nullable = false)
    @Expose(serialize = false)
    private Configuracion configuracion;

    public Tarifa() {}

    public Tarifa(TipoVehiculo tipoVehiculo, Double precioPorHora, Integer fraccionMinutos) {
        validar(precioPorHora, fraccionMinutos);
        this.tipoVehiculo = tipoVehiculo;
        this.precioPorHora = precioPorHora;
        this.fraccionMinutos = fraccionMinutos;
    }

    private void validar(double precioPorHora, double fraccionMinutos) {
        if (precioPorHora <= 0) {
            throw new IllegalArgumentException("El precio por hora debe ser mayor a 0.");
        }
        if (fraccionMinutos <= 0) {
            throw new IllegalArgumentException("La fracción de minutos debe ser mayor a 0.");
        }
    }

    public double calcularCostos(long minutos) {
        if (minutos <= 0) {
            return 0.0;
        }
        double fracciones = Math.ceil(minutos / (double) fraccionMinutos);
        double costoPorFraccion = precioPorHora * (fraccionMinutos / 60.0);
        return Math.round(fracciones * costoPorFraccion * 100.0) / 100.0;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public TipoVehiculo getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public Double getPrecioPorHora() { return precioPorHora; }
    public void setPrecioPorHora(Double precioPorHora) {
        if (precioPorHora != null && precioPorHora <= 0) {
            throw new IllegalArgumentException("El precio por hora debe ser mayor a 0.");
        }
        this.precioPorHora = precioPorHora;
    }

    public Integer getFraccionMinutos() { return fraccionMinutos; }
    public void setFraccionMinutos(Integer fraccionMinutos) {
        if (fraccionMinutos != null && fraccionMinutos <= 0) {
            throw new IllegalArgumentException("La fracción de minutos debe ser mayor a 0.");
        }
        this.fraccionMinutos = fraccionMinutos;
    }

    public Configuracion getConfiguracion() { return configuracion; }
    public void setConfiguracion(Configuracion configuracion) { this.configuracion = configuracion; }
}