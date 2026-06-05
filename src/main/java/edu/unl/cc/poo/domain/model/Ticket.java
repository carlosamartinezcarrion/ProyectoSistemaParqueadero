package edu.unl.cc.poo.domain.model;

import java.time.format.DateTimeFormatter;

public class Ticket {

    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    private Configuracion configuracion;
    private String nombreParqueadero;
    private String fechaGeneracion;
    private String rutaArchivoPdf;
    private Registro registro;

    public Ticket(Configuracion configuracion, Registro registro) {
        this.configuracion = configuracion;
        this.nombreParqueadero = configuracion.getNombreParqueadero();
        this.registro = registro;
        this.fechaGeneracion = java.time.LocalDateTime.now().format(FORMATO_FECHA);
        this.rutaArchivoPdf = "ticket_" + registro.getId() + ".pdf";
    }

    public String getNombreParqueadero()  { return nombreParqueadero; }
    public String getFechaGeneracion()    { return fechaGeneracion; }
    public String getRutaArchivoPdf()     { return rutaArchivoPdf; }
    public Registro getRegistro()         { return registro; }
}
