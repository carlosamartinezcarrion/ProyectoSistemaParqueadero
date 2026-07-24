package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

/**
 * Almacena la configuracion del parqueadero: nombre, logo, tarifas por tipo
 * de vehiculo y valores por defecto de filas/columnas.
 */
@Entity
@Table(name = "configuracion")
public class Configuracion {

    private static final String MONEDA = "USD";
    private static final String NOMBRE_DEFECTO = "AutoManager";
    private static final String DIRECCION_DEFECTO = "Av. Principal #123 - Sector Norte";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Expose
    private Long id;

    @NotBlank(message = "El nombre del parqueadero es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    @Column(name = "nombre_parqueadero", nullable = false, length = 100)
    @Expose
    private String nombreParqueadero;

    @Size(max = 3, message = "La moneda debe tener máximo 3 caracteres")
    @Column(name = "moneda", length = 3)
    @Expose
    private String moneda = MONEDA;

    @Size(max = 255, message = "La ruta del logo es muy larga")
    @Column(name = "logo_path", length = 255)
    @Expose
    private String logoPath;

    @NotBlank(message = "La dirección es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    @Column(name = "direccion", length = 255)
    @Expose
    private String direccion;

    @Min(value = 1, message = "Las filas deben ser al menos 1")
    @Max(value = 50, message = "Las filas no pueden exceder 50")
    @Column(name = "filas_defecto")
    @Expose
    private int filasDefecto = 5;

    @Min(value = 1, message = "Las columnas deben ser al menos 1")
    @Max(value = 50, message = "Las columnas no pueden exceder 50")
    @Column(name = "columnas_defecto")
    @Expose
    private int columnasDefecto = 10;

    @OneToMany(mappedBy = "configuracion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Expose
    private List<Tarifa> tarifas = new ArrayList<>();

    @OneToOne(mappedBy = "configuracion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Expose(serialize = false)
    private Parqueadero parqueadero;

    public Configuracion() {
        inicializarTarifasPorDefecto();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombreParqueadero() { return nombreParqueadero; }
    public void setNombreParqueadero(String nombreParqueadero) { this.nombreParqueadero = nombreParqueadero; }

    public String getMoneda() { return moneda; }
    public void setMoneda(String moneda) { this.moneda = moneda; }

    public String getLogoPath() { return logoPath; }
    public void setLogoPath(String logoPath) { this.logoPath = logoPath; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public int getFilasDefecto() { return filasDefecto; }
    public void setFilasDefecto(int filasDefecto) { this.filasDefecto = filasDefecto; }

    public int getColumnasDefecto() { return columnasDefecto; }
    public void setColumnasDefecto(int columnasDefecto) { this.columnasDefecto = columnasDefecto; }

    public List<Tarifa> getTarifas() { return tarifas; }
    public void setTarifas(List<Tarifa> tarifas) { this.tarifas = tarifas; }

    public Parqueadero getParqueadero() { return parqueadero; }
    public void setParqueadero(Parqueadero parqueadero) { this.parqueadero = parqueadero; }

    public Tarifa getTarifaPorTipo(TipoVehiculo tipo) {
        return tarifas.stream()
                .filter(t -> t.getTipoVehiculo() == tipo)
                .findFirst()
                .orElseGet(() -> tarifaPorDefecto(tipo));
    }

    public void actualizarTarifa(TipoVehiculo tipo, double precioPorHora, double fraccionMinutos) {
        Tarifa existente = getTarifaPorTipo(tipo);
        if (existente != null && existente.getId() != null) {
            existente.setPrecioPorHora(precioPorHora);
            existente.setFraccionMinutos((int) fraccionMinutos);
        } else {
            tarifas.add(new Tarifa(tipo, precioPorHora, (int) fraccionMinutos));
        }
    }

    public boolean guardarConfiguracion() {
        Gson gson = new GsonBuilder().setPrettyPrinting().excludeFieldsWithoutExposeAnnotation().create();
        String rutaArchivo = System.getProperty("user.dir") + "/configuracion.json";
        try (FileWriter writer = new FileWriter(rutaArchivo)) {
            gson.toJson(this, writer);
            return true;
        } catch (IOException e) {
            System.err.println("Error al guardar configuracion: " + e.getMessage());
            return false;
        }
    }

    public void cargarConfiguracion() {
        Gson gson = new Gson();
        String rutaArchivo = System.getProperty("user.dir") + "/configuracion.json";
        try (FileReader reader = new FileReader(rutaArchivo)) {
            Configuracion cargada = gson.fromJson(reader, Configuracion.class);
            if (cargada != null) {
                if (cargada.nombreParqueadero != null && !cargada.nombreParqueadero.isBlank()) {
                    this.nombreParqueadero = cargada.nombreParqueadero;
                }
                this.logoPath = cargada.logoPath != null ? cargada.logoPath : this.logoPath;
                if (cargada.direccion != null && !cargada.direccion.isBlank()) {
                    this.direccion = cargada.direccion;
                }
                if (cargada.tarifas != null) {
                    this.tarifas = new ArrayList<>();
                    for (Tarifa t : cargada.tarifas) {
                        if (t != null) {
                            this.tarifas.add(new Tarifa(t.getTipoVehiculo(), t.getPrecioPorHora(), t.getFraccionMinutos()));
                        }
                    }
                    for (TipoVehiculo tipo : TipoVehiculo.values()) {
                        this.tarifas.removeIf(t -> t.getTipoVehiculo() == tipo);
                        this.tarifas.add(tarifaPorDefecto(tipo));
                    }
                }
                if (cargada.filasDefecto > 0) {
                    this.filasDefecto = cargada.filasDefecto;
                }
                if (cargada.columnasDefecto > 0) {
                    this.columnasDefecto = cargada.columnasDefecto;
                }
                this.moneda = MONEDA;
            }
        } catch (IOException e) {
            // Archivo no existe, usar valores por defecto
        }
    }

    private Tarifa tarifaPorDefecto(TipoVehiculo tipo) {
        return switch (tipo) {
            case AUTOMOVIL -> new Tarifa(TipoVehiculo.AUTOMOVIL, 1.00, 30);
            case MOTO -> new Tarifa(TipoVehiculo.MOTO, 0.50, 30);
            case CAMIONETA -> new Tarifa(TipoVehiculo.CAMIONETA, 1.50, 30);
        };
    }

    private void inicializarTarifasPorDefecto() {
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            tarifas.add(tarifaPorDefecto(tipo));
        }
    }
}