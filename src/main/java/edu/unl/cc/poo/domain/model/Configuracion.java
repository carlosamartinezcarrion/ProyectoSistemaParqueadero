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

    private String nombreParqueadero;
    private String moneda;
    private String logoPath;
    private Map<TipoVehiculo, Tarifa> tarifas;
    private int filasDefecto;
    private int columnasDefecto;

    public Configuracion() {
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


    public void guardarConfiguracion() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try (FileWriter writer = new FileWriter("configuracion.json")) {
            gson.toJson(this, writer);
        } catch (IOException e) {
            System.err.println("Error al guardar configuracion: " + e.getMessage());
        }
    }


    public void cargarConfiguracion() {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader("configuracion.json")) {
            Configuracion cargada = gson.fromJson(reader, Configuracion.class);
            if (cargada != null) {
                this.nombreParqueadero = cargada.nombreParqueadero;
                this.logoPath = cargada.logoPath;
                this.tarifas = cargada.tarifas;
                this.filasDefecto = cargada.filasDefecto;
                this.columnasDefecto = cargada.columnasDefecto;
                this.moneda = MONEDA; // moneda siempre es USD
            }
        } catch (IOException e) {
            System.err.println("No se encontro archivo de configuracion, usando valores por defecto.");
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
