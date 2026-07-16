package edu.unl.cc.poo.view.ConsoleViews;

import edu.unl.cc.poo.domain.model.*;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import edu.unl.cc.poo.domain.business.PdfService;

import java.util.regex.Pattern;
import java.util.Scanner;


/**
 * Controlador de la aplicación. Maneja toda la interacción con el usuario,
 * menús, validaciones y flujo de la aplicación.
 */
public class ConsoleView {

    private Parqueadero parqueadero;
    private Configuracion configuracion;
    private PdfService pdfService;
    private Scanner scanner;
    private static final Pattern PATRON_NOMBRE = Pattern.compile(
            "^[a-zA-ZáéíóúÁÉÍÓÚñÑüÜ\\s]+$"
    );

    private DisplayHelper display;
    private ConfiguracionView configuracionView;
    private HistorialView historialView;

    public ConsoleView(Parqueadero parqueadero, Configuracion configuracion, PdfService pdfService) {
        this.parqueadero = parqueadero;
        this.configuracion = configuracion;
        this.pdfService = pdfService;
        this.scanner = new Scanner(System.in);
        this.display = new DisplayHelper();
        this.configuracionView = new ConfiguracionView(parqueadero, configuracion, scanner, display);
        this.historialView = new HistorialView(parqueadero, scanner, display);
    }

    public void iniciarAplicacion() {
        mostrarBienvenida();
        menuPrincipal();
        scanner.close();
    }

    private void mostrarBienvenida() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║           AutoManager - Parqueadero        ║");
        System.out.println("╚════════════════════════════════════════════╝");
        display.mostrarConfiguracion(configuracion);
    }

    private void menuPrincipal() {
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n┌─ MENÚ PRINCIPAL ─────────────────────────┐");
            System.out.println("│ 1. Registrar Entrada                      │");
            System.out.println("│ 2. Registrar Salida                       │");
            System.out.println("│ 3. Ver Mapa del Parqueadero               │");
            System.out.println("│ 4. Ver Resumen                            │");
            System.out.println("│ 5. Ver Historial de Registros             │");
            System.out.println("│ 6. Gestionar Espacio                      │");
            System.out.println("│ 7. Configuración                          │");
            System.out.println("│ 8. Salir                                  │");
            System.out.println("└───────────────────────────────────────────┘");
            System.out.print("Selecciona una opción (1-8): ");

            try {
                String entrada = scanner.nextLine().trim();
                int opcion;

                try {
                    opcion = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    display.mostrarError("Entrada inválida. Debes ingresar un número (1-8). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> registrarEntrada();
                    case 2 -> registrarSalida();
                    case 3 -> verMapa();
                    case 4 -> verResumen();
                    case 5 -> historialView.verHistorial();
                    case 6 -> gestionarEspacio();
                    case 7 -> {
                        configuracionView.menuConfiguracion();
                        parqueadero = configuracionView.getParqueadero();
                    }
                    case 8 -> {
                        System.out.println("\n¡Gracias por usar AutoManager!");
                        ejecutando = false;
                    }
                    default -> display.mostrarError("Opción inválida. Selecciona entre 1 y 8. Intenta de nuevo.");
                }
            } catch (Exception e) {
                display.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void registrarEntrada() {
        System.out.println("\n=== REGISTRAR ENTRADA ===");

        String placa = "";
        boolean placaValida = false;
        while (!placaValida) {
            System.out.print("Placa del vehículo (ej: ABC123, M123, C456) o 'salir': ");
            placa = scanner.nextLine().trim();

            if (placa.equalsIgnoreCase("salir")) {
                return;
            }

            if (placa.isEmpty()) {
                display.mostrarError("La placa no puede estar vacía. Intenta de nuevo.");
                continue;
            }
            placaValida = true;
        }

        String conductor = "";
        boolean conductorValido = false;
        while (!conductorValido) {
            System.out.print("Nombre del conductor o 'salir': ");
            conductor = scanner.nextLine().trim();

            if (conductor.equalsIgnoreCase("salir")) {
                return;
            }

            if (conductor.isEmpty()) {
                display.mostrarError("El nombre no puede estar vacío. Intenta de nuevo.");
                continue;
            }

            if (!PATRON_NOMBRE.matcher(conductor).matches()) {
                display.mostrarError("El nombre solo puede contener letras. "
                        + "No se permiten números ni símbolos. Intenta de nuevo.");
                continue;
            }


            conductorValido = true;
        }


        TipoVehiculo tipo = null;
        boolean tipoValido = false;
        while (!tipoValido) {
            System.out.println("\nTipo de vehículo:");
            System.out.println("1. Automóvil ($1.00/h)");
            System.out.println("2. Moto ($0.50/h)");
            System.out.println("3. Camioneta ($1.50/h)");
            System.out.print("Selecciona tipo (1-3) o 'salir': ");

            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("salir")) {
                return;
            }

            try {
                int tipoOpcion = Integer.parseInt(entrada);

                if (tipoOpcion < 1 || tipoOpcion > 3) {
                    display.mostrarError("Opción inválida. Selecciona entre 1 y 3. Intenta de nuevo.");
                    continue;
                }

                tipo = switch (tipoOpcion) {
                    case 1 -> TipoVehiculo.AUTOMOVIL;
                    case 2 -> TipoVehiculo.MOTO;
                    case 3 -> TipoVehiculo.CAMIONETA;
                    default -> TipoVehiculo.AUTOMOVIL;
                };
                tipoValido = true;
            } catch (NumberFormatException e) {
                display.mostrarError("Entrada inválida. Debes ingresar un número (1-3). Intenta de nuevo.");
            }
        }

        boolean eleccionValida = false;
        boolean eligeEspacio = false;
        while (!eleccionValida) {
            System.out.print("\n¿Deseas elegir un espacio específico? (s/n): ");
            String eleccion = scanner.nextLine().trim().toLowerCase();

            if (eleccion.equals("s")) {
                eligeEspacio = true;
                eleccionValida = true;
            } else if (eleccion.equals("n")) {
                eligeEspacio = false;
                eleccionValida = true;
            } else {
                display.mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
            }
        }

        try {
            Vehiculo vehiculo = new Vehiculo(placa, conductor);
            vehiculo.setTipo(tipo);
            Registro registro;

            if (eligeEspacio) {
                display.mostrarEspaciosDisponibles(parqueadero);

                int fila = -1;
                boolean filaValida = false;
                while (!filaValida) {
                    System.out.print("Ingresa la fila (A, B, C...) o 'cancelar': ");
                    String filaStr = scanner.nextLine().trim().toUpperCase();

                    if (filaStr.equalsIgnoreCase("cancelar")) {
                        return;
                    }

                    if (filaStr.isEmpty() || filaStr.length() > 1) {
                        display.mostrarError("Fila inválida. Usa una sola letra. Intenta de nuevo.");
                        continue;
                    }

                    fila = filaStr.charAt(0) - 'A';

                    if (fila < 0 || fila >= parqueadero.getMapa().getFilas()) {
                        display.mostrarError("Fila fuera de rango. Intenta de nuevo.");
                        continue;
                    }
                    filaValida = true;
                }

                int columna = -1;
                boolean columnaValida = false;
                while (!columnaValida) {
                    System.out.print("Ingresa la columna (número) o 'cancelar': ");
                    String entradaCol = scanner.nextLine().trim();

                    if (entradaCol.equalsIgnoreCase("cancelar")) {
                        return;
                    }

                    try {
                        columna = Integer.parseInt(entradaCol);

                        if (columna < 1 || columna > parqueadero.getMapa().getColumnas()) {
                            display.mostrarError("Columna fuera de rango. Intenta de nuevo.");
                            continue;
                        }
                        columnaValida = true;
                    } catch (NumberFormatException e) {
                        display.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
                    }
                }

                registro = parqueadero.registrarEntrada(vehiculo, fila, columna - 1);
            } else {
                registro = parqueadero.registrarEntrada(vehiculo, null, null);
            }

            display.mostrarRegistroEntrada(registro);
            display.mostrarMensaje("✓ Entrada registrada exitosamente");

        } catch (IllegalStateException | IllegalArgumentException e) {
            display.mostrarError(e.getMessage());
        }
    }

    private void registrarSalida() {
        System.out.println("\n=== REGISTRAR SALIDA ===");

        String registroId = "";
        boolean idValido = false;
        while (!idValido) {
            System.out.print("ID del Registro o 'cancelar': ");
            registroId = scanner.nextLine().trim();

            if (registroId.equalsIgnoreCase("cancelar")) {
                return;
            }

            if (registroId.isEmpty()) {
                display.mostrarError("ID no puede estar vacío. Intenta de nuevo.");
                continue;
            }
            idValido = true;
        }

        try {
            Registro registro = parqueadero.registrarSalida(registroId);
            display.mostrarRegistroSalida(registro);

            generarTicket(registro);
            display.mostrarMensaje("✓ Salida registrada exitosamente");

        } catch (IllegalArgumentException e) {
            display.mostrarError("Registro no encontrado: " + e.getMessage());
        } catch (IllegalStateException e) {
            display.mostrarError("Error al procesar salida: " + e.getMessage());
        } catch (Exception e) {
            display.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void gestionarEspacio() {
        System.out.println("\n=== GESTIONAR ESPACIO ===");
        display.mostrarMapaParqueadero(parqueadero);

        int fila = -1;
        boolean filaValida = false;
        while (!filaValida) {
            System.out.print("Ingresa la fila (A, B, C...) o 'cancelar': ");
            String filaStr = scanner.nextLine().trim().toUpperCase();

            if (filaStr.equalsIgnoreCase("cancelar")) {
                return;
            }

            if (filaStr.isEmpty() || filaStr.length() > 1) {
                display.mostrarError("Fila inválida. Usa una sola letra. Intenta de nuevo.");
                continue;
            }

            fila = filaStr.charAt(0) - 'A';

            if (fila < 0 || fila >= parqueadero.getMapa().getFilas()) {
                display.mostrarError("Fila fuera de rango. Intenta de nuevo.");
                continue;
            }
            filaValida = true;
        }

        int columna = -1;
        boolean columnaValida = false;
        while (!columnaValida) {
            System.out.print("Ingresa la columna (número) o 'cancelar': ");
            String entradaCol = scanner.nextLine().trim();

            if (entradaCol.equalsIgnoreCase("cancelar")) {
                return;
            }

            try {
                columna = Integer.parseInt(entradaCol);

                if (columna < 1 || columna > parqueadero.getMapa().getColumnas()) {
                    display.mostrarError("Columna fuera de rango. Intenta de nuevo.");
                    continue;
                }
                columnaValida = true;
            } catch (NumberFormatException e) {
                display.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            var espacio = parqueadero.getMapa().getEspacio(fila, columna - 1);

            System.out.println("Estado actual: " + espacio.getEstado().getDescripcion());
            if (espacio.estaOcupado()) {
                display.mostrarError("El espacio está ocupado. Debes registrar la salida antes de cambiar su estado.");
                return;
            }

            System.out.println("1. Inhabilitar");
            System.out.println("2. Habilitar (dejar libre)");
            System.out.print("Selecciona una opción (1-2) o 'cancelar': ");
            String opcion = scanner.nextLine().trim();

            if (opcion.equalsIgnoreCase("cancelar")) {
                return;
            }

            EstadoEspacio nuevoEstado;
            switch (opcion) {
                case "1" -> nuevoEstado = EstadoEspacio.INHABILITADO;
                case "2" -> nuevoEstado = EstadoEspacio.LIBRE;
                default -> {
                    display.mostrarError("Opción inválida. Debes seleccionar 1 o 2.");
                    return;
                }
            }

            if (espacio.getEstado() == nuevoEstado) {
                display.mostrarMensaje("El espacio ya estaba en ese estado.");
                return;
            }

            parqueadero.setEstadoEspacio(fila, columna - 1, nuevoEstado);
            display.mostrarMensaje("✓ Estado del espacio actualizado correctamente");
            display.mostrarMapaParqueadero(parqueadero);
        } catch (IllegalArgumentException | IllegalStateException e) {
            display.mostrarError(e.getMessage());
        } catch (Exception e) {
            display.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void generarTicket(Registro registro) {
        boolean respuestaValida = false;
        while (!respuestaValida) {
            System.out.print("\n¿Deseas generar un ticket PDF? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("s")) {
                try {
                    Ticket ticket = new Ticket(configuracion, registro);
                    pdfService.generarPDF(ticket);
                    display.mostrarTicketGenerado(ticket.getRutaArchivoPdf());

                    boolean abrirValido = false;
                    while (!abrirValido) {
                        System.out.print("\n¿Deseas abrir el PDF? (s/n): ");
                        String abrirRespuesta = scanner.nextLine().trim().toLowerCase();

                        if (abrirRespuesta.equals("s")) {
                            pdfService.abrirPDF(ticket.getRutaArchivoPdf());
                            abrirValido = true;
                        } else if (abrirRespuesta.equals("n")) {
                            abrirValido = true;
                        } else {
                            display.mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
                        }
                    }
                    respuestaValida = true;
                } catch (Exception e) {
                    display.mostrarError("Error al generar ticket: " + e.getMessage());
                    respuestaValida = true;
                }
            } else if (respuesta.equals("n")) {
                respuestaValida = true;
            } else {
                display.mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
            }
        }
    }

    private void verMapa() {
        display.mostrarMapaParqueadero(parqueadero);
    }

    private void verResumen() {
        mostrarResumen(parqueadero);
    }

    private void mostrarResumen(Parqueadero parqueadero) {
        double totalRecaudado = parqueadero.getHistorial().stream()
                .mapToDouble(Registro::getTotalCobrado)
                .sum();

        System.out.println("=================================================");
        System.out.println("  RESUMEN PARQUEADERO: " + parqueadero.getNombre());
        System.out.println("=================================================");
        System.out.printf("  Capacidad total  : %d espacios%n", parqueadero.getCapacidad());
        System.out.printf("  Espacios libres  : %d%n", parqueadero.getMapa().contarLibres());
        System.out.printf("  Espacios ocupados: %d%n", parqueadero.getMapa().contarOcupados());
        System.out.printf("  Inhabilitados    : %d%n", parqueadero.getMapa().contarInhabilitados());
        System.out.printf("  Moneda           : %s%n", parqueadero.getConfiguracion().getMoneda());
        System.out.printf("  Total recaudado  : %s %.2f%n", parqueadero.getConfiguracion().getMoneda(), totalRecaudado);
        System.out.println("=================================================\n");
    }
}
