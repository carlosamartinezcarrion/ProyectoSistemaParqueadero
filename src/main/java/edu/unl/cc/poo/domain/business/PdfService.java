package edu.unl.cc.poo.domain.business;

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
import edu.unl.cc.poo.domain.model.Registro;
import edu.unl.cc.poo.domain.model.Ticket;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class PdfService {

    private static final String MONEDA = "USD";
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public void generarPDF(Ticket ticket) {
        try (PdfWriter writer = new PdfWriter(ticket.getRutaArchivoPdf());
             PdfDocument pdfDoc = new PdfDocument(writer);
             Document doc = new Document(pdfDoc)) {

            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont font     = PdfFontFactory.createFont(StandardFonts.HELVETICA);

            // Encabezado
            doc.add(new Paragraph(ticket.getNombreParqueadero())
                    .setFont(fontBold).setFontSize(18)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.DARK_GRAY));

            doc.add(new Paragraph("TICKET DE PARQUEO")
                    .setFont(fontBold).setFontSize(14)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph("Generado: " + ticket.getFechaGeneracion())
                    .setFont(font).setFontSize(10)
                    .setTextAlignment(TextAlignment.CENTER));

            doc.add(new Paragraph(" "));

            // Tabla de detalle
            Registro registro = ticket.getRegistro();
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
            doc.add(new Paragraph(generarContenido(ticket.getNombreParqueadero()))
                    .setFont(font).setFontSize(9)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontColor(ColorConstants.GRAY));

        } catch (IOException e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    public void abrirPDF(String rutaArchivoPdf) {
        try {
            File archivo = new File(rutaArchivoPdf);
            if (archivo.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(archivo);
            } else {
                System.out.println("PDF disponible en: " + rutaArchivoPdf);
            }
        } catch (IOException e) {
            throw new RuntimeException("No se pudo abrir el PDF: " + e.getMessage(), e);
        }
    }

    private String centrar(String texto, int ancho) {
        if (texto == null || texto.length() >= ancho) return texto;
        int padding = (ancho - texto.length()) / 2;
        return " ".repeat(padding) + texto;
    }

    private String generarContenido(String nombreParqueadero) {
        return centrar("Gracias por usar " + nombreParqueadero, 60);
    }

    private void agregarFila(Table tabla, PdfFont fontBold, PdfFont font, String etiqueta, String valor) {
        tabla.addCell(new Cell().add(new Paragraph(etiqueta).setFont(fontBold).setFontSize(11)));
        tabla.addCell(new Cell().add(new Paragraph(valor).setFont(font).setFontSize(11)));
    }
}
