package edu.unl.cc.poo;

import edu.unl.cc.poo.domain.model.*;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import edu.unl.cc.poo.view.ConsoleView;

import java.util.Scanner;

public class Main {

    private static Parqueadero parqueadero;
    private static Configuracion configuracion;
    private static ConsoleView view;
    private static Scanner scanner;

    public static void main(String[] args) {
        inicializar();
        menuPrincipal();
    }



    private static void inicializar() {
        configuracion = new Configuracion();
        configuracion.cargarConfiguracion();

        parqueadero = new Parqueadero(configuracion.getNombreParqueadero(), configuracion);
        view = new ConsoleView();
        scanner = new Scanner(System.in);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            configuracion.guardarConfiguracion();
        }));

        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE PARQUEADERO - VERSIÓN 1.0    ║");
        System.out.println("╚════════════════════════════════════════════╝");
        view.mostrarConfiguracion(configuracion);
    }

    private static void menuPrincipal() {
        boolean ejecutando = true;

        while (ejecutando) {
            System.out.println("\n┌─ MENÚ PRINCIPAL ─────────────────────────┐");
            System.out.println("│ 1. Registrar Entrada                      │");
            System.out.println("│ 2. Registrar Salida                       │");
            System.out.println("│ 3. Ver Mapa del Parqueadero               │");
            System.out.println("│ 4. Ver Resumen                            │");
            System.out.println("│ 5. Ver Historial de Registros             │");
            System.out.println("│ 6. Configuración                          │");
            System.out.println("│ 7. Salir                                  │");
            System.out.println("└───────────────────────────────────────────┘");
            System.out.print("Selecciona una opción (1-7): ");

            try {
                String entrada = scanner.nextLine().trim();
                int opcion;
                
                try {
                    opcion = Integer.parseInt(entrada);
                } catch (NumberFormatException e) {
                    view.mostrarError("Entrada inválida. Debes ingresar un número (1-7). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> registrarEntrada();
                    case 2 -> registrarSalida();
                    case 3 -> verMapa();
                    case 4 -> verResumen();
                    case 5 -> verHistorial();
                    case 6 -> menuConfiguracion();
                    case 7 -> {
                        System.out.println("\n¡Gracias por usar el Sistema de Parqueadero!");
                        ejecutando = false;
                    }
                    default -> view.mostrarError("Opción inválida. Selecciona entre 1 y 7. Intenta de nuevo.");
                }
            } catch (Exception e) {
                view.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
        scanner.close();
    }

    private static void menuConfiguracion() {
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
                    view.mostrarError("Entrada inválida. Debes ingresar un número (1-7). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> view.mostrarConfiguracion(configuracion);
                    case 2 -> cambiarNombreParqueadero();
                    case 3 -> cambiarDimensiones();
                    case 4 -> cambiarTarifa(TipoVehiculo.AUTOMOVIL);
                    case 5 -> cambiarTarifa(TipoVehiculo.MOTO);
                    case 6 -> cambiarTarifa(TipoVehiculo.CAMIONETA);
                    case 7 -> {
                        view.mostrarMensaje("Volviendo al menú principal...");
                        enConfiguracion = false;
                    }
                    default -> view.mostrarError("Opción inválida. Selecciona entre 1 y 7. Intenta de nuevo.");
                }
            } catch (Exception e) {
                view.mostrarError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private static void cambiarNombreParqueadero() {
        System.out.println("\n=== CAMBIAR NOMBRE DEL PARQUEADERO ===");
        
        boolean nombreValido = false;
        while (!nombreValido) {
            System.out.print("Nuevo nombre o 'cancelar': ");
            String nombre = scanner.nextLine().trim();
            
            if (nombre.equalsIgnoreCase("cancelar")) {
                return;
            }
            
            if (nombre.isEmpty()) {
                view.mostrarError("El nombre no puede estar vacío. Intenta de nuevo.");
                continue;
            }
            
            if (nombre.length() < 3) {
                view.mostrarError("El nombre debe tener al menos 3 caracteres. Intenta de nuevo.");
                continue;
            }
            
            if (nombre.length() > 100) {
                view.mostrarError("El nombre no puede exceder 100 caracteres. Intenta de nuevo.");
                continue;
            }

            try {
                configuracion.setNombreParqueadero(nombre);
                parqueadero.setNombre(nombre);
                if (guardarConfiguracionActual()) {
                    view.mostrarMensaje("✓ Nombre actualizado");
                    nombreValido = true;
                }
            } catch (Exception e) {
                view.mostrarError("Error al actualizar nombre: " + e.getMessage());
            }
        }
    }

    private static void cambiarDimensiones() {
        System.out.println("\n=== CAMBIAR DIMENSIONES ===");
        
        int filas = -1;
        boolean filasValidas = false;
        while (!filasValidas) {
            System.out.print("Número de filas (1-26) o 'cancelar': ");
            String entrada = scanner.nextLine().trim();
            
            if (entrada.equalsIgnoreCase("cancelar")) {
                return;
            }
            
            try {
                filas = Integer.parseInt(entrada);
                
                if (filas <= 0 || filas > 50) {
                    view.mostrarError("Las filas deben estar entre 1 y 50. Intenta de nuevo.");
                    continue;
                }
                filasValidas = true;
            } catch (NumberFormatException e) {
                view.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }
        
        int columnas = -1;
        boolean columnasValidas = false;
        while (!columnasValidas) {
            System.out.print("Número de columnas (1-99) o 'cancelar': ");
            String entrada = scanner.nextLine().trim();
            
            if (entrada.equalsIgnoreCase("cancelar")) {
                return;
            }
            
            try {
                columnas = Integer.parseInt(entrada);
                
                if (columnas <= 0 || columnas > 50) {
                    view.mostrarError("Las columnas deben estar entre 1 y 50. Intenta de nuevo.");
                    continue;
                }
                columnasValidas = true;
            } catch (NumberFormatException e) {
                view.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            configuracion.setFilasDefecto(filas);
            configuracion.setColumnasDefecto(columnas);
            
            parqueadero = new Parqueadero(configuracion.getNombreParqueadero(), configuracion);
            if (guardarConfiguracionActual()) {
                view.mostrarMensaje("✓ Dimensiones actualizadas");
                view.mostrarMapaParqueadero(parqueadero);
            }
        } catch (Exception e) {
            view.mostrarError("Error al cambiar dimensiones: " + e.getMessage());
        }
    }

    private static void cambiarTarifa(TipoVehiculo tipo) {
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
                    view.mostrarError("El precio debe ser mayor a 0. Intenta de nuevo.");
                    continue;
                }
                precioValido = true;
            } catch (NumberFormatException e) {
                view.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
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
                    view.mostrarError("La fracción debe ser mayor a 0. Intenta de nuevo.");
                    continue;
                }
                fraccionValida = true;
            } catch (NumberFormatException e) {
                view.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            configuracion.actualizarTarifa(tipo, precio, fraccion);
            if (guardarConfiguracionActual()) {
                view.mostrarMensaje("✓ Tarifa actualizada");
            }
        } catch (Exception e) {
            view.mostrarError("Error al actualizar tarifa: " + e.getMessage());
        }
    }

    private static void registrarEntrada() {
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
                view.mostrarError("La placa no puede estar vacía. Intenta de nuevo.");
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
                view.mostrarError("El nombre no puede estar vacío. Intenta de nuevo.");
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
                    view.mostrarError("Opción inválida. Selecciona entre 1 y 3. Intenta de nuevo.");
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
                view.mostrarError("Entrada inválida. Debes ingresar un número (1-3). Intenta de nuevo.");
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
                view.mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
            }
        }

        try {
            Vehiculo vehiculo = new Vehiculo(placa, conductor);
            vehiculo.setTipo(tipo);
            Registro registro;

            if (eligeEspacio) {
                view.mostrarEspaciosDisponibles(parqueadero);
                
                int fila = -1;
                boolean filaValida = false;
                while (!filaValida) {
                    System.out.print("Ingresa la fila (A, B, C...) o 'cancelar': ");
                    String filaStr = scanner.nextLine().trim().toUpperCase();
                    
                    if (filaStr.equalsIgnoreCase("cancelar")) {
                        return;
                    }
                    
                    if (filaStr.isEmpty() || filaStr.length() > 1) {
                        view.mostrarError("Fila inválida. Usa una sola letra. Intenta de nuevo.");
                        continue;
                    }
                    
                    fila = filaStr.charAt(0) - 'A';
                    
                    if (fila < 0 || fila >= parqueadero.getMapa().getFilas()) {
                        view.mostrarError("Fila fuera de rango. Intenta de nuevo.");
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
                            view.mostrarError("Columna fuera de rango. Intenta de nuevo.");
                            continue;
                        }
                        columnaValida = true;
                    } catch (NumberFormatException e) {
                        view.mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
                    }
                }

                registro = parqueadero.registrarEntrada(vehiculo, fila, columna - 1);
            } else {
                registro = parqueadero.registrarEntrada(vehiculo);
            }

            view.mostrarRegistroEntrada(registro);
            view.mostrarMensaje("✓ Entrada registrada exitosamente");

        } catch (IllegalStateException | IllegalArgumentException e) {
            view.mostrarError(e.getMessage());
        }
    }

    private static void registrarSalida() {
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
                view.mostrarError("ID no puede estar vacío. Intenta de nuevo.");
                continue;
            }
            idValido = true;
        }

        try {
            Registro registro = parqueadero.registrarSalida(registroId);
            view.mostrarRegistroSalida(registro);

            generarTicket(registro);
            view.mostrarMensaje("✓ Salida registrada exitosamente");

        } catch (IllegalArgumentException e) {
            view.mostrarError("Registro no encontrado: " + e.getMessage());
        } catch (IllegalStateException e) {
            view.mostrarError("Error al procesar salida: " + e.getMessage());
        } catch (Exception e) {
            view.mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private static void generarTicket(Registro registro) {
        boolean respuestaValida = false;
        while (!respuestaValida) {
            System.out.print("\n¿Deseas generar un ticket PDF? (s/n): ");
            String respuesta = scanner.nextLine().trim().toLowerCase();

            if (respuesta.equals("s")) {
                try {
                    Ticket ticket = new Ticket(parqueadero.getNombre(), registro);
                    ticket.generarPDF();
                    view.mostrarTicketGenerado(ticket.getRutaArchivoPdf());
                    respuestaValida = true;
                } catch (Exception e) {
                    view.mostrarError("Error al generar ticket: " + e.getMessage());
                    respuestaValida = true;
                }
            } else if (respuesta.equals("n")) {
                respuestaValida = true;
            } else {
                view.mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
            }
        }
    }

    private static void verMapa() {
        view.mostrarMapaParqueadero(parqueadero);
    }

    private static void verResumen() {
        parqueadero.imprimirResumen();
    }

    private static void verHistorial() {
        view.mostrarHistorialRegistros(parqueadero);
    }

    private static boolean guardarConfiguracionActual() {
        if (!configuracion.guardarConfiguracion()) {
            view.mostrarError("No se pudo guardar la configuración en el archivo JSON.");
            return false;
        }
        return true;
    }
}
