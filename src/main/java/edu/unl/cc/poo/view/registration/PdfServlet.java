package edu.unl.cc.poo.view.registration;

import edu.unl.cc.poo.domain.Ticket;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

public class PdfServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.length() <= 1) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo requerido");
            return;
        }

        String fileName = pathInfo.substring(1);
        if (fileName.contains("..") || fileName.contains("/") || fileName.contains("\\")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Nombre de archivo invalido");
            return;
        }

        File archivo = Ticket.getDirectorioTickets().resolve(fileName).toFile();

        if (!archivo.exists() || !archivo.getName().endsWith(".pdf")) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "PDF no encontrado: " + fileName);
            return;
        }

        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "inline; filename=\"" + fileName + "\"");
        response.setContentLengthLong(archivo.length());

        try (FileInputStream fis = new FileInputStream(archivo);
             OutputStream out = response.getOutputStream()) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        }
    }
}
