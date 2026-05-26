package edu.unl.cc.poo.domain.enums;

public enum TipoVehiculo {
    AUTOMOVIL,
    MOTO,
    CAMIONETA;

    public String getDescripcion() {
        return switch (this) {
            case AUTOMOVIL -> "Automóvil";
            case MOTO      -> "Moto";
            case CAMIONETA -> "Camioneta";
        };
    }
}
