package edu.unl.cc.poo.domain.enums;

/**
 * Representa los posibles estados de un espacio en el parqueadero.
 */
public enum EstadoEspacio {
    LIBRE,
    OCUPADO,
    INHABILITADO;

    public String getDescripcion() {
        return switch (this) {
            case LIBRE        -> "Libre";
            case OCUPADO      -> "Ocupado";
            case INHABILITADO -> "Inhabilitado";
        };
    }
}
