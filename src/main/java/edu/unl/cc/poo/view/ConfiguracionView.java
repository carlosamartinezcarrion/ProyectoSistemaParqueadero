package edu.unl.cc.poo.view;

import edu.unl.cc.poo.domain.model.*;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import java.util.Scanner;

/**
 * Maneja el menú de configuración: nombre, dimensiones y tarifas.
 */
public class ConfiguracionView {

    private Parqueadero parqueadero;
    private Configuracion configuracion;
    private Scanner scanner;
    private DisplayHelper display;

    public ConfiguracionView(Parqueadero parqueadero, Configuracion configuracion,
                             Scanner scanner, DisplayHelper display) {
        this.parqueadero = parqueadero;
        this.configuracion = configuracion;
        this.scanner = scanner;
        this.display = display;
    }

    public Parqueadero getParqueadero() {
        return parqueadero;
    }

    void menuConfiguracion() {
        boolean enConfiguracion = true;

        while (enConfiguracion) {
            System.out.println("\n┌─ CONFIGURACIÓN ──────────────────────────┐");
            System.out.println("│ 1. Ver Configuración Actual               │");
            System.out.println("│ 2. Cambiar Nombre del Parqueadero         │");
            System.out.println("│ 3. Cambiar Dimensiones (Filas/Columnas)   │");
            System.out.println("│ 4. Cambiar Tarifa de Automóvil            │");
            System.out.println("│ 5. Cambiar Tarifa de Moto                 │");
            System.out.println("│ 6. Cambiar Tarifa de Camioneta            │");
            System.out.println("│ 7. Volver al Menú Principal               │");
            System.out.println("└───────────────────────────────────────────┘");
            System.out.print("Selecciona una opción (1-7): ");

            try {
                String entrada = scanner.nextLine().trim();
                int opcion;

                try {
                    opcion = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    display.mostrarError("Entrada inválida. Debes ingresar un número (1-7). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> display.mostrarConfiguracion(configuracion);
                    case 2 -> cambiarNombreParqueadero();
                    case 3 -> cambiarDimensiones();
                    case 4 -> cambiarTarifa(TipoVehiculo.AUTOMOVIL);
                    case 5 -> cambiarTarifa(TipoVehiculo.MOTO);
                    case 6 -> cambiarTarifa(TipoVehiculo.CAMIONETA);
                    case 7 -> {
                        display.mostrarMensaje("Volviendo al menú principal...");
                        enConfiguracion = false;
                    }
                    default -> display.mostrarError("Opción inválida. Selecciona entre 1 y 7. Intenta de nuevo.");
                }
            } catch (Exception e) {
                display.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void cambiarNombreParqueadero() {
        System.out.println("\n=== CAMBIAR NOMBRE DEL PARQUEADERO ===");

        boolean nombreValido = false;
        while (!nombreValido) {
            System.out.print("Nuevo nombre o 'cancelar': ");
            String nombre = scanner.nextLine().trim();

            if (nombre.equalsIgnoreCase("cancelar")) {
                return;
            }

            if (nombre.isEmpty()) {
                display.mostrarError("El nombre no puede estar vacío. Intenta de nuevo.");
                continue;
            }

            if (nombre.length() < 3) {
                display.mostrarError("El nombre debe tener al menos 3 caracteres. Intenta de nuevo.");
                continue;
            }

            if (nombre.length() > 100) {
                display.mostrarError("El nombre no puede exceder 100 caracteres. Intenta de nuevo.");
                continue;
            }

            try {
                configuracion.setNombreParqueadero(nombre);
                parqueadero.setNombre(nombre);
                if (guardarConfiguracionActual()) {
                    display.mostrarMensaje("✓ Nombre actualizado");
                    nombreValido = true;
                }
            } catch (Exception e) {
                display.mostrarError("Error al actualizar nombre: " + e.getMessage());
            }
        }
    }

    private void cambiarDimensiones() {
        System.out.println("\n=== CAMBIAR DIMENSIONES ===");

        int filas = -1;
        boolean filasValidas = false;
        while (!filasValidas) {
            System.out.print("Número de filas (1-50) o 'cancelar': ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("cancelar")) {
                return;
            }

            try {
                filas = Integer.parseInt(entrada);

                if (filas <= 0 || filas > 50) {
                    display.mostrarError("Las filas deben estar entre 1 y 50. Intenta de nuevo.");
                    continue;
                }
                filasValidas = true;
            } catch (NumberFormatException e) {
                display.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        int columnas = -1;
        boolean columnasValidas = false;
        while (!columnasValidas) {
            System.out.print("Número de columnas (1-50) o 'cancelar': ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("cancelar")) {
                return;
            }

            try {
                columnas = Integer.parseInt(entrada);

                if (columnas <= 0 || columnas > 50) {
                    display.mostrarError("Las columnas deben estar entre 1 y 50. Intenta de nuevo.");
                    continue;
                }
                columnasValidas = true;
            } catch (NumberFormatException e) {
                display.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            configuracion.setFilasDefecto(filas);
            configuracion.setColumnasDefecto(columnas);
            parqueadero.setNombre(configuracion.getNombreParqueadero());
            parqueadero.aplicarConfiguracion();
            if (guardarConfiguracionActual()) {
                display.mostrarMensaje("✓ Dimensiones actualizadas");
                display.mostrarMapaParqueadero(parqueadero);
            }
        } catch (Exception e) {
            display.mostrarError("Error al cambiar dimensiones: " + e.getMessage());
        }
    }

    private void cambiarTarifa(TipoVehiculo tipo) {
        System.out.printf("\n=== CAMBIAR TARIFA DE %s ===%n", tipo.getDescripcion().toUpperCase());

        double precio = -1;
        boolean precioValido = false;
        while (!precioValido) {
            System.out.print("Precio por hora (USD) o 'cancelar': ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("cancelar")) {
                return;
            }

            try {
                precio = Double.parseDouble(entrada);

                if (precio <= 0) {
                    display.mostrarError("El precio debe ser mayor a 0. Intenta de nuevo.");
                    continue;
                }
                precioValido = true;
            } catch (NumberFormatException e) {
                display.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        double fraccion = -1;
        boolean fraccionValida = false;
        while (!fraccionValida) {
            System.out.print("Fracción de minutos o 'cancelar': ");
            String entrada = scanner.nextLine().trim();

            if (entrada.equalsIgnoreCase("cancelar")) {
                return;
            }

            try {
                fraccion = Double.parseDouble(entrada);

                if (fraccion <= 0) {
                    display.mostrarError("La fracción debe ser mayor a 0. Intenta de nuevo.");
                    continue;
                }
                fraccionValida = true;
            } catch (NumberFormatException e) {
                display.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            configuracion.actualizarTarifa(tipo, precio, fraccion);
            if (guardarConfiguracionActual()) {
                display.mostrarMensaje("✓ Tarifa actualizada");
            }
        } catch (Exception e) {
            display.mostrarError("Error al actualizar tarifa: " + e.getMessage());
        }
    }

    private boolean guardarConfiguracionActual() {
        if (!configuracion.guardarConfiguracion()) {
            display.mostrarError("No se pudo guardar la configuración en el archivo JSON.");
            return false;
        }
        return true;
    }
}