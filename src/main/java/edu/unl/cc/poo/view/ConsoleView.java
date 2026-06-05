package edu.unl.cc.poo.view;

import edu.unl.cc.poo.domain.model.*;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import edu.unl.cc.poo.business.PdfService;

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

    public ConsoleView(Parqueadero parqueadero, Configuracion configuracion, PdfService pdfService) {
        this.parqueadero = parqueadero;
        this.configuracion = configuracion;
        this.pdfService = pdfService;
        this.scanner = new Scanner(System.in);
    }

    public void iniciarAplicacion() {
        mostrarBienvenida();
        menuPrincipal();
        scanner.close();
    }

    private void mostrarBienvenida() {
        System.out.println("\n╔════════════════════════════════════════════╗");
        System.out.println("║   SISTEMA DE PARQUEADERO - VERSIÓN 1.0    ║");
        System.out.println("╚════════════════════════════════════════════╝");
        mostrarConfiguracion(configuracion);
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
                    mostrarError("Entrada inválida. Debes ingresar un número (1-8). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> registrarEntrada();
                    case 2 -> registrarSalida();
                    case 3 -> verMapa();
                    case 4 -> verResumen();
                    case 5 -> verHistorial();
                    case 6 -> gestionarEspacio();
                    case 7 -> menuConfiguracion();
                    case 8 -> {
                        System.out.println("\n¡Gracias por usar el Sistema de Parqueadero!");
                        ejecutando = false;
                    }
                    default -> mostrarError("Opción inválida. Selecciona entre 1 y 8. Intenta de nuevo.");
                }
            } catch (Exception e) {
                mostrarError("Error inesperado: " + e.getMessage());
            }
        }
    }

    private void menuConfiguracion() {
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
                    mostrarError("Entrada inválida. Debes ingresar un número (1-7). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> mostrarConfiguracion(configuracion);
                    case 2 -> cambiarNombreParqueadero();
                    case 3 -> cambiarDimensiones();
                    case 4 -> cambiarTarifa(TipoVehiculo.AUTOMOVIL);
                    case 5 -> cambiarTarifa(TipoVehiculo.MOTO);
                    case 6 -> cambiarTarifa(TipoVehiculo.CAMIONETA);
                    case 7 -> {
                        mostrarMensaje("Volviendo al menú principal...");
                        enConfiguracion = false;
                    }
                    default -> mostrarError("Opción inválida. Selecciona entre 1 y 7. Intenta de nuevo.");
                }
            } catch (Exception e) {
                mostrarError("Error inesperado: " + e.getMessage());
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
                mostrarError("El nombre no puede estar vacío. Intenta de nuevo.");
                continue;
            }
            
            if (nombre.length() < 3) {
                mostrarError("El nombre debe tener al menos 3 caracteres. Intenta de nuevo.");
                continue;
            }
            
            if (nombre.length() > 100) {
                mostrarError("El nombre no puede exceder 100 caracteres. Intenta de nuevo.");
                continue;
            }

            try {
                configuracion.setNombreParqueadero(nombre);
                parqueadero.setNombre(nombre);
                if (guardarConfiguracionActual()) {
                    mostrarMensaje("✓ Nombre actualizado");
                    nombreValido = true;
                }
            } catch (Exception e) {
                mostrarError("Error al actualizar nombre: " + e.getMessage());
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
                    mostrarError("Las filas deben estar entre 1 y 50. Intenta de nuevo.");
                    continue;
                }
                filasValidas = true;
            } catch (NumberFormatException e) {
                mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
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
                    mostrarError("Las columnas deben estar entre 1 y 50. Intenta de nuevo.");
                    continue;
                }
                columnasValidas = true;
            } catch (NumberFormatException e) {
                mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            configuracion.setFilasDefecto(filas);
            configuracion.setColumnasDefecto(columnas);
            
            parqueadero = new Parqueadero(configuracion.getNombreParqueadero(), configuracion);
            if (guardarConfiguracionActual()) {
                mostrarMensaje("✓ Dimensiones actualizadas");
                mostrarMapaParqueadero(parqueadero);
            }
        } catch (Exception e) {
            mostrarError("Error al cambiar dimensiones: " + e.getMessage());
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
                    mostrarError("El precio debe ser mayor a 0. Intenta de nuevo.");
                    continue;
                }
                precioValido = true;
            } catch (NumberFormatException e) {
                mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
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
                    mostrarError("La fracción debe ser mayor a 0. Intenta de nuevo.");
                    continue;
                }
                fraccionValida = true;
            } catch (NumberFormatException e) {
                mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            configuracion.actualizarTarifa(tipo, precio, fraccion);
            if (guardarConfiguracionActual()) {
                mostrarMensaje("✓ Tarifa actualizada");
            }
        } catch (Exception e) {
            mostrarError("Error al actualizar tarifa: " + e.getMessage());
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
                mostrarError("La placa no puede estar vacía. Intenta de nuevo.");
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
                mostrarError("El nombre no puede estar vacío. Intenta de nuevo.");
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
                    mostrarError("Opción inválida. Selecciona entre 1 y 3. Intenta de nuevo.");
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
                mostrarError("Entrada inválida. Debes ingresar un número (1-3). Intenta de nuevo.");
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
                mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
            }
        }

        try {
            Vehiculo vehiculo = new Vehiculo(placa, conductor);
            vehiculo.setTipo(tipo);
            Registro registro;

            if (eligeEspacio) {
                mostrarEspaciosDisponibles(parqueadero);
                
                int fila = -1;
                boolean filaValida = false;
                while (!filaValida) {
                    System.out.print("Ingresa la fila (A, B, C...) o 'cancelar': ");
                    String filaStr = scanner.nextLine().trim().toUpperCase();
                    
                    if (filaStr.equalsIgnoreCase("cancelar")) {
                        return;
                    }
                    
                    if (filaStr.isEmpty() || filaStr.length() > 1) {
                        mostrarError("Fila inválida. Usa una sola letra. Intenta de nuevo.");
                        continue;
                    }
                    
                    fila = filaStr.charAt(0) - 'A';
                    
                    if (fila < 0 || fila >= parqueadero.getMapa().getFilas()) {
                        mostrarError("Fila fuera de rango. Intenta de nuevo.");
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
                            mostrarError("Columna fuera de rango. Intenta de nuevo.");
                            continue;
                        }
                        columnaValida = true;
                    } catch (NumberFormatException e) {
                        mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
                    }
                }

                registro = parqueadero.registrarEntrada(vehiculo, fila, columna - 1);
            } else {
                registro = parqueadero.registrarEntrada(vehiculo);
            }

            mostrarRegistroEntrada(registro);
            mostrarMensaje("✓ Entrada registrada exitosamente");

        } catch (IllegalStateException | IllegalArgumentException e) {
            mostrarError(e.getMessage());
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
                mostrarError("ID no puede estar vacío. Intenta de nuevo.");
                continue;
            }
            idValido = true;
        }

        try {
            Registro registro = parqueadero.registrarSalida(registroId);
            mostrarRegistroSalida(registro);

            generarTicket(registro);
            mostrarMensaje("✓ Salida registrada exitosamente");

        } catch (IllegalArgumentException e) {
            mostrarError("Registro no encontrado: " + e.getMessage());
        } catch (IllegalStateException e) {
            mostrarError("Error al procesar salida: " + e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
        }
    }

    private void gestionarEspacio() {
        System.out.println("\n=== GESTIONAR ESPACIO ===");
        mostrarMapaParqueadero(parqueadero);

        int fila = -1;
        boolean filaValida = false;
        while (!filaValida) {
            System.out.print("Ingresa la fila (A, B, C...) o 'cancelar': ");
            String filaStr = scanner.nextLine().trim().toUpperCase();

            if (filaStr.equalsIgnoreCase("cancelar")) {
                return;
            }

            if (filaStr.isEmpty() || filaStr.length() > 1) {
                mostrarError("Fila inválida. Usa una sola letra. Intenta de nuevo.");
                continue;
            }

            fila = filaStr.charAt(0) - 'A';

            if (fila < 0 || fila >= parqueadero.getMapa().getFilas()) {
                mostrarError("Fila fuera de rango. Intenta de nuevo.");
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
                    mostrarError("Columna fuera de rango. Intenta de nuevo.");
                    continue;
                }
                columnaValida = true;
            } catch (NumberFormatException e) {
                mostrarError("Entrada inválida. Debes ingresar un número. Intenta de nuevo.");
            }
        }

        try {
            var espacio = parqueadero.getMapa().getEspacio(fila, columna - 1);
            if (espacio == null) {
                mostrarError("No se encontró el espacio solicitado.");
                return;
            }

            System.out.println("Estado actual: " + espacio.getEstado().getDescripcion());
            if (espacio.estaOcupado()) {
                mostrarError("El espacio está ocupado. Debes registrar la salida antes de cambiar su estado.");
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
                    mostrarError("Opción inválida. Debes seleccionar 1 o 2.");
                    return;
                }
            }

            if (espacio.getEstado() == nuevoEstado) {
                mostrarMensaje("El espacio ya estaba en ese estado.");
                return;
            }

            parqueadero.setEstadoEspacio(fila, columna - 1, nuevoEstado);
            mostrarMensaje("✓ Estado del espacio actualizado correctamente");
            mostrarMapaParqueadero(parqueadero);
        } catch (IllegalArgumentException | IllegalStateException e) {
            mostrarError(e.getMessage());
        } catch (Exception e) {
            mostrarError("Error inesperado: " + e.getMessage());
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
                    mostrarTicketGenerado(ticket.getRutaArchivoPdf());
                    
                    // Preguntar si desea abrir el PDF
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
                            mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
                        }
                    }
                    respuestaValida = true;
                } catch (Exception e) {
                    mostrarError("Error al generar ticket: " + e.getMessage());
                    respuestaValida = true;
                }
            } else if (respuesta.equals("n")) {
                respuestaValida = true;
            } else {
                mostrarError("Respuesta inválida. Usa 's' o 'n'. Intenta de nuevo.");
            }
        }
    }

    private void verMapa() {
        mostrarMapaParqueadero(parqueadero);
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

    private void verHistorial() {
        mostrarHistorialRegistros(parqueadero);
        
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
                    mostrarError("Entrada inválida. Debes ingresar un número (1-4). Intenta de nuevo.");
                    continue;
                }

                switch (opcion) {
                    case 1 -> filtrarPorPlaca();
                    case 2 -> filtrarPorFecha();
                    case 3 -> mostrarHistorialRegistros(parqueadero);
                    case 4 -> filtroActivo = false;
                    default -> mostrarError("Opción inválida. Selecciona entre 1 y 4. Intenta de nuevo.");
                }
            } catch (Exception e) {
                mostrarError("Error inesperado: " + e.getMessage());
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
            mostrarError("La placa no puede estar vacía.");
            return;
        }

        var historial = parqueadero.getHistorial();
        var registrosFiltrados = historial.stream()
                .filter(r -> r.getVehiculo().getPlaca().equalsIgnoreCase(placa))
                .toList();

        if (registrosFiltrados.isEmpty()) {
            mostrarMensaje("No hay registros con la placa: " + placa);
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
            mostrarError("La fecha no puede estar vacía.");
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
                mostrarMensaje("No hay registros en la fecha: " + fechaStr);
            } else {
                System.out.println("\n=== REGISTROS DEL " + fechaStr + " ===");
                for (Registro registro : registrosFiltrados) {
                    System.out.println(registro.toString());
                }
                System.out.println("Total de registros: " + registrosFiltrados.size() + "\n");
            }
        } catch (java.time.format.DateTimeParseException e) {
            mostrarError("Formato de fecha inválido. Usa: dd/MM/yyyy");
        }
    }

    private boolean guardarConfiguracionActual() {
        if (!configuracion.guardarConfiguracion()) {
            mostrarError("No se pudo guardar la configuración en el archivo JSON.");
            return false;
        }
        return true;
    }

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
