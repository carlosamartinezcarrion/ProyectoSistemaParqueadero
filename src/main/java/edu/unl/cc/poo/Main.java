package edu.unl.cc.poo;

import edu.unl.cc.poo.business.PdfService;
import edu.unl.cc.poo.domain.model.Configuracion;
import edu.unl.cc.poo.domain.model.Parqueadero;
import edu.unl.cc.poo.view.ConsoleViews.ConsoleView;

public class Main {

    public static void main(String[] args) {

        Configuracion configuracion = new Configuracion();
        configuracion.cargarConfiguracion();

        Parqueadero parqueadero = new Parqueadero(configuracion.getNombreParqueadero(), configuracion);
        PdfService pdfService = new PdfService();

        Runtime.getRuntime().addShutdownHook(new Thread(configuracion::guardarConfiguracion));

        ConsoleView view = new ConsoleView(parqueadero, configuracion, pdfService);
        view.iniciarAplicacion();

    }
}
