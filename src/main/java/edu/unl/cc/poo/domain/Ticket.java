package edu.unl.cc.poo.domain;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.format.DateTimeFormatter;

public class Ticket {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private static final Path DIRECTORIO_TICKETS =
            Path.of(System.getProperty("java.io.tmpdir"), "automanager", "tickets");

    static {
        try {
            Files.createDirectories(DIRECTORIO_TICKETS);
        } catch (IOException e) {
            // Ignorar
        }
    }

    public static Path getDirectorioTickets() {
        return DIRECTORIO_TICKETS;
    }

    private Configuracion configuracion;
    private String nombreParqueadero;
    private String fechaGeneracion;
    private String nombreArchivoPdf;
    private Registro registro;

    public Ticket(Configuracion configuracion, Registro registro) {
        this.configuracion = configuracion;
        this.nombreParqueadero = configuracion.getNombreParqueadero();
        this.registro = registro;
        this.fechaGeneracion = java.time.LocalDateTime.now().format(FORMATO_FECHA);
        this.nombreArchivoPdf = "ticket_" + registro.getId() + ".pdf";
    }

    public String getRutaArchivoPdf() {
        return DIRECTORIO_TICKETS.resolve(nombreArchivoPdf).toAbsolutePath().toString();
    }

    public String getNombreArchivoPdf() {
        return nombreArchivoPdf;
    }

    public String getNombreParqueadero()  { return nombreParqueadero; }
    public String getFechaGeneracion()    { return fechaGeneracion; }
    public Registro getRegistro()         { return registro; }
}
