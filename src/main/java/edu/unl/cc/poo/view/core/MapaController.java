package edu.unl.cc.poo.view.core;

import edu.unl.cc.poo.business.core.ParqueaderoService;
import edu.unl.cc.poo.domain.EspacioParqueadero;
import edu.unl.cc.poo.domain.Registro;
import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Named("mapaController")
@ViewScoped
public class MapaController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ParqueaderoService parqueaderoService;

    private Integer filaSeleccionada;
    private Integer columnaSeleccionada;
    private String zonaSeleccionada = "Nivel 0";
    private String modoVista = "grid";

    public List<EspacioParqueadero> getEspacios() {
        return parqueaderoService.getParqueadero().getMapa().getEspacios();
    }

    public List<Integer> getFilas() {
        int filas = parqueaderoService.getParqueadero().getMapa().getFilas();
        List<Integer> result = new ArrayList<>(filas);
        for (int i = 0; i < filas; i++) {
            result.add(i);
        }
        return result;
    }

    public int getColumnas() {
        return parqueaderoService.getColumnasMapa();
    }

    public String getColumnasCssClass() {
        int cols = Math.min(Math.max(getColumnas(), 1), 15);
        return "columns-" + cols;
    }

    public String getGridTemplateColumns() {
        int cols = Math.min(Math.max(getColumnas(), 1), 15);
        StringBuilder sb = new StringBuilder("repeat(").append(cols).append(", minmax(0, 1fr))");
        return sb.toString();
    }

    public List<EspacioParqueadero> getEspaciosDeFila(Integer fila) {
        if (fila == null) {
            return List.of();
        }
        return getEspacios().stream()
                .filter(espacio -> espacio.getFila() == fila.intValue())
                .toList();
    }

    public Map<Integer, List<EspacioParqueadero>> getEspaciosPorFila() {
        Map<Integer, List<EspacioParqueadero>> mapa = new LinkedHashMap<>();
        for (Integer fila : getFilas()) {
            mapa.put(fila, getEspaciosDeFila(fila));
        }
        return mapa;
    }

    public void seleccionarEspacio(int fila, int columna) {
        this.filaSeleccionada = fila;
        this.columnaSeleccionada = columna;
        EspacioParqueadero espacio = parqueaderoService.getParqueadero().getMapa().getEspacio(fila, columna);
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Espacio seleccionado",
                        espacio.getEtiqueta() + " · " + espacio.getEstado()));
    }

    public void liberarSeleccion() {
        if (filaSeleccionada == null || columnaSeleccionada == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Sin selección", "Selecciona un espacio antes de intentar liberarlo."));
            return;
        }
        try {
            EspacioParqueadero espacio = parqueaderoService.getParqueadero().getMapa()
                    .getEspacio(filaSeleccionada, columnaSeleccionada);
            if (espacio.estaOcupado()) {
                Registro cerrado = parqueaderoService.forzarLiberacionEspacio(filaSeleccionada, columnaSeleccionada);
                String detalle = cerrado != null
                        ? "Salida de " + cerrado.getVehiculo().getPlaca()
                        + " · $" + String.format("%.2f", cerrado.getTotalCobrado())
                        : "Espacio liberado";
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO, "Espacio liberado", detalle));
            } else {
                parqueaderoService.setEstadoEspacio(filaSeleccionada, columnaSeleccionada, EstadoEspacio.LIBRE);
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Espacio liberado", getEtiquetaSeleccionada()));
            }
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se pudo liberar", ex.getMessage()));
        }
    }

    public void inhabilitarSeleccion() {
        if (filaSeleccionada == null || columnaSeleccionada == null) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Sin selección", "Selecciona un espacio libre para inhabilitarlo."));
            return;
        }
        try {
            parqueaderoService.setEstadoEspacio(filaSeleccionada, columnaSeleccionada, EstadoEspacio.INHABILITADO);
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Espacio marcado como inhabilitado", getEtiquetaSeleccionada()));
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se pudo cambiar el estado", ex.getMessage()));
        }
    }

    public String getEtiquetaSeleccionada() {
        if (filaSeleccionada == null || columnaSeleccionada == null) {
            return "Sin selección";
        }
        return String.valueOf((char) ('A' + filaSeleccionada)) + (columnaSeleccionada + 1);
    }

    public String getEstadoSeleccionado() {
        if (filaSeleccionada == null || columnaSeleccionada == null) {
            return "—";
        }
        return parqueaderoService.getParqueadero().getMapa()
                .getEspacio(filaSeleccionada, columnaSeleccionada)
                .getEstado()
                .name();
    }

    public long getDisponibles() { return parqueaderoService.getEspaciosDisponibles(); }
    public long getOcupados() { return parqueaderoService.getOcupados(); }
    public long getInhabilitados() { return parqueaderoService.getInhabilitados(); }
    public double getPorcentajeOcupacion() { return parqueaderoService.getPorcentajeOcupacion(); }
    public List<Registro> getHistorialReciente() { return parqueaderoService.getHistorialReciente(); }
    public Integer getFilaSeleccionada() { return filaSeleccionada; }
    public Integer getColumnaSeleccionada() { return columnaSeleccionada; }
    public String getZonaSeleccionada() { return zonaSeleccionada; }
    public void setZonaSeleccionada(String zonaSeleccionada) { this.zonaSeleccionada = zonaSeleccionada; }
    public String getModoVista() { return modoVista; }
    public void setModoVista(String modoVista) { this.modoVista = modoVista; }
}
