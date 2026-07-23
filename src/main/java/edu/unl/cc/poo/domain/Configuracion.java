package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.TipoVehiculo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;

/**
 * Almacena la configuracion del parqueadero: nombre, logo, tarifas por tipo
 * de vehiculo y valores por defecto de filas/columnas.
 */
public class Configuracion {

    private static final String MONEDA = "USD";
    private static final String NOMBRE_DEFECTO = "AutoManager";
    private static final String DIRECCION_DEFECTO = "Av. Principal #123 - Sector Norte";

    private String nombreParqueadero;
    private String moneda;
    private String logoPath;
    private String direccion;
    private Map<TipoVehiculo, Tarifa> tarifas;
    private int filasDefecto;
    private int columnasDefecto;

    public Configuracion() {
        this.nombreParqueadero = NOMBRE_DEFECTO;
        this.moneda = MONEDA;
        this.direccion = DIRECCION_DEFECTO;
        this.tarifas = new EnumMap<>(TipoVehiculo.class);
        this.filasDefecto = 5;
        this.columnasDefecto = 10;
        inicializarTarifasPorDefecto();
    }

    public Tarifa getTarifaPorTipo(TipoVehiculo tipo) {
        return tarifas.getOrDefault(tipo, tarifas.get(TipoVehiculo.AUTOMOVIL));
    }

    public void actualizarTarifa(TipoVehiculo tipo, double precioPorHora, double fraccionMinutos) {
        tarifas.put(tipo, new Tarifa(precioPorHora, fraccionMinutos));
    }

    public boolean guardarConfiguracion() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
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
                    this.tarifas = new EnumMap<>(TipoVehiculo.class);
                    for (Map.Entry<TipoVehiculo, Tarifa> entry : cargada.tarifas.entrySet()) {
                        Tarifa t = entry.getValue();
                        if (t != null) {
                            this.tarifas.put(entry.getKey(),
                                    new Tarifa(t.getPrecioPorHora(), t.getFraccionMinutos()));
                        }
                    }
                    // Completar tarifas faltantes
                    for (TipoVehiculo tipo : TipoVehiculo.values()) {
                        this.tarifas.computeIfAbsent(tipo, this::tarifaPorDefecto);
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
            case AUTOMOVIL -> new Tarifa(1.00, 30);
            case MOTO -> new Tarifa(0.50, 30);
            case CAMIONETA -> new Tarifa(1.50, 30);
        };
    }

    private void inicializarTarifasPorDefecto() {
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            tarifas.put(tipo, tarifaPorDefecto(tipo));
        }
    }

    public String getNombreParqueadero() {
        return nombreParqueadero;
    }

    public void setNombreParqueadero(String nombreParqueadero) {
        if (nombreParqueadero == null || nombreParqueadero.isBlank()) {
            throw new IllegalArgumentException("El nombre del parqueadero no puede estar vacío.");
        }
        if (nombreParqueadero.trim().length() < 3) {
            throw new IllegalArgumentException("El nombre debe tener al menos 3 caracteres.");
        }
        this.nombreParqueadero = nombreParqueadero.trim();
    }

    public String getMoneda() {
        return MONEDA;
    }

    public String getLogoPath() {
        return logoPath;
    }

    public void setLogoPath(String logoPath) {
        this.logoPath = logoPath;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion == null || direccion.isBlank()
                ? DIRECCION_DEFECTO
                : direccion.trim();
    }

    public Map<TipoVehiculo, Tarifa> getTarifas() {
        return tarifas;
    }

    public int getFilasDefecto() {
        return filasDefecto;
    }

    public void setFilasDefecto(int filasDefecto) {
        if (filasDefecto < 1 || filasDefecto > 50) {
            throw new IllegalArgumentException("Las filas deben estar entre 1 y 50.");
        }
        this.filasDefecto = filasDefecto;
    }

    public int getColumnasDefecto() {
        return columnasDefecto;
    }

    public void setColumnasDefecto(int columnasDefecto) {
        if (columnasDefecto < 1 || columnasDefecto > 50) {
            throw new IllegalArgumentException("Las columnas deben estar entre 1 y 50.");
        }
        this.columnasDefecto = columnasDefecto;
    }
}
