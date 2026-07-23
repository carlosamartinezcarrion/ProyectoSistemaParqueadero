package edu.unl.cc.poo.domain;

import edu.unl.cc.poo.domain.enums.TipoVehiculo;

/**
 * Representa un vehiculo que ingresa al parqueadero.
 * Encapsula placa, conductor y tipo de vehiculo.
 */

public class Vehiculo {

    private String placa;
    private String nombreConductor;
    private TipoVehiculo tipoEspecifico;

    public Vehiculo(String placa, String nombreConductor) {
        if (placa == null || placa.isBlank()) {
            throw new IllegalArgumentException("La placa del vehículo no puede estar vacía.");
        }
        if (nombreConductor == null || nombreConductor.isBlank()) {
            throw new IllegalArgumentException("El nombre del conductor no puede estar vacío.");
        }
        this.placa = placa;
        this.nombreConductor = nombreConductor;
        this.tipoEspecifico = null;
    }


    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public TipoVehiculo getTipo() {
        if (tipoEspecifico != null) {
            return tipoEspecifico;
        }
        return determinarTipoPorPlaca();
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipoEspecifico = tipo;
    }

    public String getNombreConductor() {
        return nombreConductor;
    }

    public void setNombreConductor(String nombreConductor) {
        this.nombreConductor = nombreConductor;
    }

    private TipoVehiculo determinarTipoPorPlaca() {
        if (placa == null || placa.isBlank()) {
            return TipoVehiculo.AUTOMOVIL;
        }
        String upper = placa.toUpperCase();
        if (upper.startsWith("M")) return TipoVehiculo.MOTO;
        if (upper.startsWith("C")) return TipoVehiculo.CAMIONETA;
        return TipoVehiculo.AUTOMOVIL;
    }

    @Override
    public String toString() {
        return "Vehiculo{placa='" + placa + "', conductor='" + nombreConductor + "'}";
    }
}
