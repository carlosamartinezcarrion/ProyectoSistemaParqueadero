package edu.unl.cc.poo.view.history;

import edu.unl.cc.poo.business.core.ParqueaderoService;
import edu.unl.cc.poo.domain.Registro;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Named("historialController")
@ViewScoped
public class HistorialController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ParqueaderoService parqueaderoService;

    private String placaFiltro;
    private LocalDate fechaFiltro;
    private String tipoFiltro;
    private String exportPath;

    public List<Registro> getRegistros() {
        return aplicarFiltros();
    }

    public List<TipoVehiculo> getTiposVehiculo() {
        return List.of(TipoVehiculo.values());
    }

    public int getTotalEncontrados() {
        return getRegistros().size();
    }

    public String exportarCsv() {
        List<Registro> registros = aplicarFiltros();
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Placa,Conductor,Tipo,Espacio,Entrada,Salida,DuracionMinutos,Total\n");
        for (Registro registro : registros) {
            csv.append(valor(registro.getUuid())).append(',')
               .append(valor(registro.getVehiculo().getPlaca())).append(',')
               .append(valor(registro.getVehiculo().getNombreConductor())).append(',')
               .append(valor(registro.getVehiculo().getTipo().getDescripcion())).append(',')
               .append(valor(registro.getEspacio().getEtiqueta())).append(',')
               .append(valor(String.valueOf(registro.getFechaHoraEntrada()))).append(',')
               .append(valor(String.valueOf(registro.getFechaHoraSalida()))).append(',')
               .append(registro.getDuracionMinutos()).append(',')
               .append(registro.getTotalCobrado()).append('\n');
        }

        try {
            Path destino = Path.of(System.getProperty("user.dir"), "historial_export.csv");
            Files.writeString(destino, csv.toString());
            exportPath = destino.toAbsolutePath().toString();
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "CSV generado", "Archivo exportado en " + exportPath));
            FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se pudo exportar CSV", ex.getMessage()));
        }
        return null;
    }

    private List<Registro> aplicarFiltros() {
        return parqueaderoService.getHistorial().stream()
                .filter(r -> placaFiltro == null || placaFiltro.isBlank()
                        || r.getVehiculo().getPlaca().toLowerCase().contains(placaFiltro.toLowerCase()))
                .filter(r -> tipoFiltro == null || tipoFiltro.isBlank()
                        || r.getVehiculo().getTipo().name().equals(tipoFiltro))
                .filter(r -> fechaFiltro == null || r.getFechaHoraEntrada().toLocalDate().equals(fechaFiltro))
                .sorted((a, b) -> b.getFechaHoraEntrada().compareTo(a.getFechaHoraEntrada()))
                .collect(Collectors.toList());
    }

    private String valor(String texto) {
        return "\"" + texto.replace("\"", "\"\"") + "\"";
    }

    public String getPlacaFiltro() { return placaFiltro; }
    public void setPlacaFiltro(String placaFiltro) { this.placaFiltro = placaFiltro; }
    public LocalDate getFechaFiltro() { return fechaFiltro; }
    public void setFechaFiltro(LocalDate fechaFiltro) { this.fechaFiltro = fechaFiltro; }
    public String getTipoFiltro() { return tipoFiltro; }
    public void setTipoFiltro(String tipoFiltro) { this.tipoFiltro = tipoFiltro; }
    public String getExportPath() { return exportPath; }
}
