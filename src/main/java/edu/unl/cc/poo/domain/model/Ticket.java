package edu.unl.cc.poo.domain.model;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Genera y abre el ticket PDF de un registro de parqueadero.
 */
public class Ticket {

    private static final String MONEDA = "USD";
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


    public void generarPDF() {
        try (PdfWriter writer = new PdfWriter(rutaArchivoPdf);
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {

            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont font     = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Encabezado
            doc.add(new Paragraph(nombreParqueadero)
                    .setFont(fontBold).setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));

            doc.add(new Paragraph("TICKET DE PARQUEO")
                    .setFont(fontBold).setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Generado: " + fechaGeneracion)
                    .setFont(font).setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph(" "));

            // Tabla de detalle
            Table tabla = new Table(UnitValue.createPercentArray(new float[]{40, 60}))
                    .setWidth(UnitValue.createPercentValue(100));

            agregarFila(tabla, fontBold, font, "Registro ID:",   registro.getId());
            agregarFila(tabla, fontBold, font, "Placa:",         registro.getVehiculo().getPlaca());
            agregarFila(tabla, fontBold, font, "Conductor:",     registro.getVehiculo().getNombreConductor());
            agregarFila(tabla, fontBold, font, "Tipo vehiculo:", registro.getVehiculo().getTipo().getDescripcion());
            agregarFila(tabla, fontBold, font, "Espacio:",       registro.getEspacio().getEtiqueta());
            agregarFila(tabla, fontBold, font, "Entrada:",
                    registro.getFechaHoraEntrada().format(FORMATO_FECHA));

            if (registro.getFechaHoraSalida() != null) {
                agregarFila(tabla, fontBold, font, "Salida:",
                        registro.getFechaHoraSalida().format(FORMATO_FECHA));
                agregarFila(tabla, fontBold, font, "Duracion:",
                        registro.getDuracionMinutos() + " minutos");
                agregarFila(tabla, fontBold, font, "Total (" + MONEDA + "):",
                        String.format("$ %.2f", registro.getTotalCobrado()));
            }

            doc.add(tabla);
            doc.add(new Paragraph(" "));
            doc.add(new Paragraph(generarContenido())
                    .setFont(font).setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

        } catch (IOException e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
        }
    }


    public void abrirPDF() {
        try {
            File archivo = new File(rutaArchivoPdf);
            if (archivo.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            } else {
                System.out.println("PDF disponible en: " + rutaArchivoPdf);
            }
        } catch (IOException e) {
            System.err.println("No se pudo abrir el PDF: " + e.getMessage());
        }
    }


    public String centrar(String texto, int ancho) {
        if (texto == null || texto.length() >= ancho) return texto;
        int padding = (ancho - texto.length()) / 2;
        return " ".repeat(padding) + texto;
    }

    public String generarContenido() {
        return centrar("Gracias por usar " + nombreParqueadero, 60);
    }


    public String getNombreParqueadero()  { return nombreParqueadero; }
    public String getFechaGeneracion()    { return fechaGeneracion; }
    public String getRutaArchivoPdf()     { return rutaArchivoPdf; }
    public Registro getRegistro()         { return registro; }


    private void agregarFila(Table tabla, PdfFont fontBold, PdfFont font,
                              String etiqueta, String valor) {
        tabla.addCell(new Cell().add(new Paragraph(etiqueta).setFont(fontBold).setFontSize(11)));
        tabla.addCell(new Cell().add(new Paragraph(valor).setFont(font).setFontSize(11)));
    }
}
