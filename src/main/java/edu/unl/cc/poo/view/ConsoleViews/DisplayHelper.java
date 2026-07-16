package edu.unl.cc.poo.view.ConsoleViews;

import edu.unl.cc.poo.domain.model.*;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;

/**
 * Responsable de toda la presentación de información en consola.
 */
public class DisplayHelper {

    public void mostrarResumenParqueadero(Parqueadero parqueadero) {
        System.out.println("=================================================");
        System.out.println("  RESUMEN PARQUEADERO: " + parqueadero.getNombre());
        System.out.println("=================================================");
        System.out.printf("  Capacidad total  : %d espacios%n", parqueadero.getCapacidad());
        System.out.printf("  Espacios libres  : %d%n", parqueadero.getMapa().contarLibres());
        System.out.printf("  Espacios ocupados: %d%n", parqueadero.getMapa().contarOcupados());
        System.out.printf("  Inhabilitados    : %d%n", parqueadero.getMapa().contarInhabilitados());
        System.out.printf("  Moneda           : %s%n", parqueadero.getConfiguracion().getMoneda());
        System.out.println("=================================================");
    }

    public void mostrarConfiguracion(Configuracion config) {
        System.out.println("\n╔══════════ CONFIGURACIÓN ACTUAL ═══════════════╗");
        System.out.println("║ Nombre: " + config.getNombreParqueadero());
        System.out.println("║ Filas: " + config.getFilasDefecto() + " | Columnas: " + config.getColumnasDefecto());
        System.out.println("║ Moneda: " + config.getMoneda());
        System.out.println("╠═══ TARIFAS ═══════════════════════════════════╣");

        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            Tarifa tarifa = config.getTarifaPorTipo(tipo);
            System.out.printf("║ %s: $%.2f/h (fracción: %.0f min)%n",
                    tipo.getDescripcion(), tarifa.getPrecioPorHora(), tarifa.getFraccionMinutos());
        }
        System.out.println("╚═══════════════════════════════════════════════╝\n");
    }

    public void mostrarInformacionVehiculo(Vehiculo vehiculo) {
        System.out.println("Vehiculo{placa='" + vehiculo.getPlaca() +
                "', conductor='" + vehiculo.getNombreConductor() + "'}");
    }

    public void mostrarRegistroEntrada(Registro registro) {
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("\n=== REGISTRO DE ENTRADA ===");
        System.out.println("ID Registro: " + registro.getId());
        System.out.println("Placa: " + registro.getVehiculo().getPlaca());
        System.out.println("Conductor: " + registro.getVehiculo().getNombreConductor());
        System.out.println("Tipo: " + registro.getVehiculo().getTipo());
        System.out.println("Entrada: " + registro.getFechaHoraEntrada().format(formato));
        System.out.println("Espacio: " + registro.getEspacio().getEtiqueta());
        System.out.println("===========================\n");
    }

    public void mostrarRegistroSalida(Registro registro) {
        java.time.format.DateTimeFormatter formato = java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
        System.out.println("\n=== REGISTRO DE SALIDA ===");
        System.out.println("ID Registro: " + registro.getId());
        System.out.println("Placa: " + registro.getVehiculo().getPlaca());
        System.out.println("Conductor: " + registro.getVehiculo().getNombreConductor());
        System.out.println("Tipo: " + registro.getVehiculo().getTipo());
        System.out.println("Entrada: " + registro.getFechaHoraEntrada().format(formato));
        System.out.println("Salida: " + (registro.getFechaHoraSalida() != null ? registro.getFechaHoraSalida().format(formato) : "N/A"));
        System.out.println("Duracion: " + registro.getDuracionMinutos() + " minutos");
        System.out.printf("Total a pagar: %s %.2f%n",
                registro.getMoneda(), registro.getTotalCobrado());
        System.out.println("==========================\n");
    }

    public void mostrarMapaParqueadero(Parqueadero parqueadero) {
        System.out.println("\n=== MAPA DEL PARQUEADERO ===");
        var mapa = parqueadero.getMapa();

        System.out.print("    ");
        for (int col = 0; col < mapa.getColumnas(); col++) {
            System.out.printf("%3d ", col + 1);
        }
        System.out.println();

        for (int fila = 0; fila < mapa.getFilas(); fila++) {
            char letra = (char) ('A' + fila);
            System.out.print(letra + " | ");

            for (int col = 0; col < mapa.getColumnas(); col++) {
                var espacio = mapa.getEspacio(fila, col);
                String estado = switch (espacio.getEstado()) {
                    case LIBRE -> " . ";
                    case OCUPADO -> " X ";
                    case INHABILITADO -> " # ";
                };
                System.out.print(estado + " ");
            }
            System.out.println();
        }
        System.out.println("Leyenda: . = Libre | X = Ocupado | # = Inhabilitado\n");
    }

    public void mostrarError(String mensaje) {
        System.err.println("ERROR: " + mensaje);
    }

    public void mostrarMensaje(String mensaje) {
        System.out.println(mensaje);
    }

    public void mostrarHistorialRegistros(Parqueadero parqueadero) {
        System.out.println("\n=== HISTORIAL DE REGISTROS ===");
        var historial = parqueadero.getHistorial();
        if (historial.isEmpty()) {
            System.out.println("No hay registros.");
        } else {
            for (Registro registro : historial) {
                System.out.println(registro.toString());
            }
        }
        System.out.println("=============================\n");
    }

    public void mostrarTicketGenerado(String rutaArchivo) {
        System.out.println("✓ Ticket generado en: " + rutaArchivo);
    }

    public void mostrarEspaciosDisponibles(Parqueadero parqueadero) {
        System.out.println("\n=== ESPACIOS DISPONIBLES ===");
        var mapa = parqueadero.getMapa();
        boolean hayEspacios = false;

        for (int fila = 0; fila < mapa.getFilas(); fila++) {
            for (int col = 0; col < mapa.getColumnas(); col++) {
                var espacio = mapa.getEspacio(fila, col);
                if (espacio.estaLibre()) {
                    System.out.printf("  %s (Fila: %d, Columna: %d)%n",
                            espacio.getEtiqueta(), fila, col);
                    hayEspacios = true;
                }
            }
        }

        if (!hayEspacios) {
            System.out.println("  No hay espacios disponibles");
        }
        System.out.println("============================\n");
    }
}