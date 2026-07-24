package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;

@Entity
@Table(name = "vehiculo")
public class Vehiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "La placa es obligatoria")
    @Size(min = 1, max = 12, message = "La placa debe tener entre 1 y 12 caracteres")
    @Pattern(regexp = "[A-Za-z0-9\\-]{1,12}", message = "La placa solo debe contener letras, números o guiones (máx 12 caracteres)")
    @Column(name = "placa", nullable = false, unique = true, length = 20)
    private String placa;

    @NotBlank(message = "El nombre del conductor es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre del conductor debe tener entre 3 y 100 caracteres")
    @Pattern(regexp = "^[^0-9]*$", message = "El nombre del conductor no debe contener números")
    @Column(name = "nombre_conductor", nullable = false, length = 100)
    private String nombreConductor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", length = 20)
    private TipoVehiculo tipo;

    public Vehiculo() {}

    public Vehiculo(String placa, String nombreConductor) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa del vehículo no puede estar vacía.");
        }
        if (nombreConductor == null || nombreConductor.isBlank()) {
            throw new IllegalArgumentException("El nombre del conductor no puede estar vacío.");
        }
        this.placa = placa;
        this.nombreConductor = nombreConductor;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public String getNombreConductor() { return nombreConductor; }
    public void setNombreConductor(String nombreConductor) { this.nombreConductor = nombreConductor; }

    public TipoVehiculo getTipo() { return tipo; }
    public void setTipo(TipoVehiculo tipo) { this.tipo = tipo; }

    public TipoVehiculo determinarTipoPorPlaca() {
        if (placa == null || placa.isBlank()) {
            return TipoVehiculo.AUTOMOVIL;
        }
        String upper = placa.toUpperCase();
        if (upper.startsWith("M")) return TipoVehiculo.MOTO;
        if (upper.startsWith("C")) return TipoVehiculo.CAMIONETA;
        return TipoVehiculo.AUTOMOVIL;
    }
}