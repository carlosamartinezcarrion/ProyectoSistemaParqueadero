package edu.unl.cc.poo.domain;

/**
 * Define el precio por hora y la fraccion de minutos aplicable
 * para el cobro en el parqueadero.
 */
public class Tarifa {

    private static final String MONEDA = "USD";

    private double precioPorHora;
    private double fraccionMinutos;

    /** Constructor vacío requerido por Gson. */
    public Tarifa() {
        this.precioPorHora = 1.0;
        this.fraccionMinutos = 30;
    }

    public Tarifa(double precioPorHora, double fraccionMinutos) {
        validar(precioPorHora, fraccionMinutos);
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

    /**
     * Calcula el costo redondeando hacia arriba a la fracción configurada.
     * Ejemplo: 1h a $2/h con fracción 30 min → 2 fracciones de 0.5 h = $2.
     */
    public double calcularCostos(long minutos) {
        if (minutos <= 0) {
            return 0.0;
        }
        double fracciones = Math.ceil(minutos / fraccionMinutos);
        double costoPorFraccion = precioPorHora * (fraccionMinutos / 60.0);
        return Math.round(fracciones * costoPorFraccion * 100.0) / 100.0;
    }

    public double getPrecioPorHora() {
        return precioPorHora;
    }

    public void setPrecioPorHora(double precioPorHora) {
        if (precioPorHora <= 0) {
            throw new IllegalArgumentException("El precio por hora debe ser mayor a 0.");
        }
        this.precioPorHora = precioPorHora;
    }

    public double getFraccionMinutos() {
        return fraccionMinutos;
    }

    public void setFraccionMinutos(double fraccionMinutos) {
        if (fraccionMinutos <= 0) {
            throw new IllegalArgumentException("La fracción de minutos debe ser mayor a 0.");
        }
        this.fraccionMinutos = fraccionMinutos;
    }

    public static String getMoneda() {
        return MONEDA;
    }

    @Override
    public String toString() {
        return String.format("Tarifa{precioPorHora=%.2f %s, fraccionMinutos=%.0f min}",
                precioPorHora, MONEDA, fraccionMinutos);
    }
}
