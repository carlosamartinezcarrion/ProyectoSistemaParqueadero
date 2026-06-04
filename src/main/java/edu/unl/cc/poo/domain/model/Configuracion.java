package edu.unl.cc.poo.domain.model;

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
 * */
public class Configuracion {

    private static final String MONEDA = "USD";
    private static final String NOMBRE_DEFECTO = "PARQUEADERO CENTRAL";

    private String nombreParqueadero;
    private String moneda;
    private String logoPath;
    private Map<TipoVehiculo, Tarifa> tarifas;
    private int filasDefecto;
    private int columnasDefecto;

    public Configuracion() {
        this.nombreParqueadero = NOMBRE_DEFECTO;
        this.moneda = MONEDA;
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
                if (cargada.tarifas != null) {
                    this.tarifas = new EnumMap<>(TipoVehiculo.class);
                    this.tarifas.putAll(cargada.tarifas);
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


    private void inicializarTarifasPorDefecto() {
        tarifas.put(TipoVehiculo.AUTOMOVIL, new Tarifa(1.00, 30));
        tarifas.put(TipoVehiculo.MOTO,      new Tarifa(0.50, 30));
        tarifas.put(TipoVehiculo.CAMIONETA, new Tarifa(1.50, 30));
    }


    public String getNombreParqueadero() {
        return nombreParqueadero;
    }

    public void setNombreParqueadero(String nombreParqueadero) {
        this.nombreParqueadero = nombreParqueadero;
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

    public Map<TipoVehiculo, Tarifa> getTarifas() {
        return tarifas;
    }

    public int getFilasDefecto() {
        return filasDefecto;
    }

    public void setFilasDefecto(int filasDefecto) {
        this.filasDefecto = filasDefecto;
    }

    public int getColumnasDefecto() {
        return columnasDefecto;
    }

    public void setColumnasDefecto(int columnasDefecto) {
        this.columnasDefecto = columnasDefecto;
    }
}
