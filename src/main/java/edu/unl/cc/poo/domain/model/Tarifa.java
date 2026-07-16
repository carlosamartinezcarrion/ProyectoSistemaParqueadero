package edu.unl.cc.poo.domain.model;

/**
 * Define el precio por hora y la fraccion de minutos aplicable
 * para el cobro en el parqueadero.
 */
public class Tarifa {

    private static final String MONEDA = "USD";

    private double precioPorHora;
    private double fraccionMinutos;

    public Tarifa(double precioPorHora, double fraccionMinutos) {
        if (precioPorHora <= 0) {
            throw new IllegalArgumentException("El precio por hora debe ser mayor a 0.");
        }
        if (fraccionMinutos <= 0) {
            throw new IllegalArgumentException("La fracción de minutos debe ser mayor a 0.");
        }
        this.precioPorHora = precioPorHora;
        this.fraccionMinutos = fraccionMinutos;
    }


    public double calcularCostos(long minutos) {
        if (minutos <= 0) return 0.0;
        double horas = minutos / 60.0;
        double costoBase = horas * precioPorHora;
        // Aplica fraccion de minutos como incremento minimo
        double fraccionHora = fraccionMinutos / 60.0;
        double fracciones = Math.ceil(horas / fraccionHora);
        return fracciones * fraccionHora * precioPorHora;
    }

    public double getPrecioPorHora() {
        return precioPorHora;
    }

    public void setPrecioPorHora(double precioPorHora) {
        this.precioPorHora = precioPorHora;
    }

    public double getFraccionMinutos() {
        return fraccionMinutos;
    }

    public void setFraccionMinutos(double fraccionMinutos) {
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
