package edu.unl.cc.poo.view.Console;

import edu.unl.cc.poo.domain.Parqueadero;
import edu.unl.cc.poo.domain.Registro;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;

import java.util.Scanner;

/**
 * Maneja la visualización y filtrado del historial de registros.
 */
public class HistorialView {

    private Parqueadero parqueadero;
    private Scanner scanner;
    private DisplayHelper display;

    public HistorialView(Parqueadero parqueadero, Scanner scanner, DisplayHelper display) {
        this.parqueadero = parqueadero;
        this.scanner = scanner;
        this.display = display;
    }

    void verHistorial() {
        display.mostrarHistorialRegistros(parqueadero);

        boolean filtroActivo = true;
        while (filtroActivo) {
            System.out.println("\n┌─ OPCIONES DE FILTRO ─────────────┐");
            System.out.println("│ 1. Filtrar por Placa             │");
            System.out.println("│ 2. Filtrar por Fecha             │");
            System.out.println("│ 3. Ver Historial Completo        │");
            System.out.println("│ 4. Volver al Menú Principal      │");
            System.out.println("└──────────────────────────────────┘");
            System.out.print("Selecciona una opción (1-4): ");

            try {
                String entrada = scanner.nextLine().trim();
                int opcion;

                try {
                    opcion = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    display.mostrarError("Entrada inválida. Debes ingresar un número (1-4). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> filtrarPorPlaca();
                    case 2 -> filtrarPorFecha();
                    case 3 -> display.mostrarHistorialRegistros(parqueadero);
                    case 4 -> filtroActivo = false;
                    default -> display.mostrarError("Opción inválida. Selecciona entre 1 y 4. Intenta de nuevo.");
                }
            } catch (Exception e) {
                display.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void filtrarPorPlaca() {
        System.out.println("\n=== FILTRAR POR PLACA ===");
        System.out.print("Ingresa la placa o 'cancelar': ");
        String placa = scanner.nextLine().trim();

        if (placa.equalsIgnoreCase("cancelar")) {
            return;
        }

        if (placa.isEmpty()) {
            display.mostrarError("La placa no puede estar vacía.");
            return;
        }

        var historial = parqueadero.getHistorial();
        var registrosFiltrados = historial.stream()
                .filter(r -> r.getVehiculo().getPlaca().equalsIgnoreCase(placa))
                .toList();

        if (registrosFiltrados.isEmpty()) {
            display.mostrarMensaje("No hay registros con la placa: " + placa);
        } else {
            System.out.println("\n=== REGISTROS CON PLACA: " + placa.toUpperCase() + " ===");

            // Agrupar por tipo de vehículo
            var registrosPorTipo = registrosFiltrados.stream()
                    .collect(java.util.stream.Collectors.groupingBy(
                            r -> r.getVehiculo().getTipo()
                    ));

            // Mostrar registros agrupados por tipo
            for (TipoVehiculo tipo : TipoVehiculo.values()) {
                var registrosDelTipo = registrosPorTipo.get(tipo);
                if (registrosDelTipo != null && !registrosDelTipo.isEmpty()) {
                    System.out.println(tipo.getDescripcion() + ":");
                    for (Registro registro : registrosDelTipo) {
                        System.out.println(registro.toString());
                    }
                }
            }
            System.out.println("Total de registros: " + registrosFiltrados.size() + "\n");
        }
    }

    private void filtrarPorFecha() {
        System.out.println("\n=== FILTRAR POR FECHA ===");
        System.out.print("Ingresa la fecha (dd/MM/yyyy) o 'cancelar': ");
        String fechaStr = scanner.nextLine().trim();

        if (fechaStr.equalsIgnoreCase("cancelar")) {
            return;
        }

        if (fechaStr.isEmpty()) {
            display.mostrarError("La fecha no puede estar vacía.");
            return;
        }

        try {
            java.time.LocalDate fechaBuscada = java.time.LocalDate.parse(
                    fechaStr,
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
            );

            var historial = parqueadero.getHistorial();
            var registrosFiltrados = historial.stream()
                    .filter(r -> r.getFechaHoraEntrada().toLocalDate().equals(fechaBuscada))
                    .toList();

            if (registrosFiltrados.isEmpty()) {
                display.mostrarMensaje("No hay registros en la fecha: " + fechaStr);
            } else {
                System.out.println("\n=== REGISTROS DEL " + fechaStr + " ===");
                for (Registro registro : registrosFiltrados) {
                    System.out.println(registro.toString());
                }
                System.out.println("Total de registros: " + registrosFiltrados.size() + "\n");
            }
        } catch (java.time.format.DateTimeParseException e) {
            display.mostrarError("Formato de fecha inválido. Usa: dd/MM/yyyy");
        }
    }
}
