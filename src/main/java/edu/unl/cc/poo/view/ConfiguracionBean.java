package edu.unl.cc.poo.view;

import edu.unl.cc.poo.domain.business.ParqueaderoService;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import edu.unl.cc.poo.domain.model.Configuracion;
import edu.unl.cc.poo.domain.model.Tarifa;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.Part;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Named("configuracionBean")
@ViewScoped
public class ConfiguracionBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ParqueaderoService parqueaderoService;

    private String nombreParqueadero;
    private String direccion;
    private int filasDefecto;
    private int columnasDefecto;

    private List<TarifaEditable> tarifasEditables;

    private Part logoFile;

    @jakarta.annotation.PostConstruct
    public void init() {
        Configuracion cfg = getConfiguracion();
        nombreParqueadero = cfg.getNombreParqueadero();
        direccion = cfg.getDireccion();
        filasDefecto = cfg.getFilasDefecto();
        columnasDefecto = cfg.getColumnasDefecto();
        cargarTarifas();
    }

    public Configuracion getConfiguracion() {
        return parqueaderoService.getParqueadero().getConfiguracion();
    }

    private void cargarTarifas() {
        tarifasEditables = new ArrayList<>();
        Map<TipoVehiculo, Tarifa> tarifas = getConfiguracion().getTarifas();
        for (TipoVehiculo tipo : TipoVehiculo.values()) {
            Tarifa t = tarifas.get(tipo);
            if (t != null) {
                tarifasEditables.add(new TarifaEditable(
                        tipo, tipo.getDescripcion(),
                        t.getPrecioPorHora(), t.getFraccionMinutos()));
            }
        }
    }

    public List<TarifaEditable> getTarifasEditables() {
        if (tarifasEditables == null) {
            cargarTarifas();
        }
        return tarifasEditables;
    }

    public String guardar() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            if (nombreParqueadero == null || nombreParqueadero.isBlank()) {
                ctx.addMessage("configuracionForm:nombre", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "El nombre del establecimiento es obligatorio.", null));
                return null;
            }
            if (nombreParqueadero.trim().length() < 3) {
                ctx.addMessage("configuracionForm:nombre", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "El nombre debe tener al menos 3 caracteres.", null));
                return null;
            }
            if (direccion == null || direccion.isBlank()) {
                ctx.addMessage("configuracionForm:direccion", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "La direccion es obligatoria.", null));
                return null;
            }
            if (filasDefecto < 1 || filasDefecto > 50) {
                ctx.addMessage("configuracionForm:filas", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Las filas deben estar entre 1 y 50.", null));
                return null;
            }
            if (columnasDefecto < 1 || columnasDefecto > 50) {
                ctx.addMessage("configuracionForm:columnas", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "Las columnas deben estar entre 1 y 50.", null));
                return null;
            }

            Configuracion cfg = getConfiguracion();
            cfg.setNombreParqueadero(nombreParqueadero);
            cfg.setDireccion(direccion);
            cfg.setFilasDefecto(filasDefecto);
            cfg.setColumnasDefecto(columnasDefecto);

            if (tarifasEditables != null) {
                for (TarifaEditable te : tarifasEditables) {
                    cfg.actualizarTarifa(te.getTipo(), te.getPrecioHora(), te.getFraccionMinutos());
                }
            }

            parqueaderoService.getParqueadero().setNombre(cfg.getNombreParqueadero());

            boolean guardado = parqueaderoService.guardarYAplicarConfiguracion();
            if (guardado) {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_INFO,
                                "Configuracion guardada", "Los cambios se guardaron correctamente."));
                FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
                init();
            } else {
                FacesContext.getCurrentInstance().addMessage(null,
                        new FacesMessage(FacesMessage.SEVERITY_WARN,
                                "Advertencia", "Los datos se actualizaron pero no se pudo persistir el archivo JSON."));
            }
            return "configuracion.xhtml?faces-redirect=true";
        } catch (IllegalStateException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se pudo guardar la configuracion", ex.getMessage()));
            return null;
        } catch (RuntimeException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se pudo guardar la configuracion", ex.getMessage()));
            return null;
        }
    }

    public void subirLogo() {
        if (logoFile == null || logoFile.getSize() <= 0) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Sin archivo", "Selecciona un archivo de imagen para subir."));
            return;
        }

        String fileName = logoFile.getSubmittedFileName();
        if (fileName == null || fileName.isBlank()) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "Nombre de archivo invalido."));
            return;
        }

        String ext = "";
        int dotIdx = fileName.lastIndexOf('.');
        if (dotIdx > 0) {
            ext = fileName.substring(dotIdx).toLowerCase();
        }

        if (!ext.matches("\\.(png|jpg|jpeg|gif|svg)")) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Formato no soportado", "Solo se permiten archivos PNG, JPG, JPEG, GIF o SVG."));
            return;
        }

        try {
            Path directorioLogos = Paths.get(System.getProperty("user.dir"), "logos");
            Files.createDirectories(directorioLogos);

            String nombreSeguro = "logo_parqueadero" + ext;
            Path destino = directorioLogos.resolve(nombreSeguro);

            try (InputStream input = logoFile.getInputStream()) {
                Files.copy(input, destino, StandardCopyOption.REPLACE_EXISTING);
            }

            String rutaRelativa = "logos/" + nombreSeguro;
            getConfiguracion().setLogoPath(rutaRelativa);
            getConfiguracion().guardarConfiguracion();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Logo subido", "El logo se guardo correctamente en: " + rutaRelativa));
        } catch (IOException ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error al subir logo", ex.getMessage()));
        }
    }

    public void restablecerPorDefecto() {
        try {
            Configuracion cfg = new Configuracion();
            cfg.setNombreParqueadero(cfg.getNombreParqueadero());
            this.nombreParqueadero = "AutoManager";
            this.direccion = "Av. Principal #123 - Sector Norte";
            this.filasDefecto = 5;
            this.columnasDefecto = 10;

            Configuracion original = getConfiguracion();
            original.setNombreParqueadero(this.nombreParqueadero);
            original.setDireccion(this.direccion);
            original.setFilasDefecto(this.filasDefecto);
            original.setColumnasDefecto(this.columnasDefecto);

            Configuracion defaults = new Configuracion();
            for (TipoVehiculo tipo : TipoVehiculo.values()) {
                Tarifa t = defaults.getTarifaPorTipo(tipo);
                original.actualizarTarifa(tipo, t.getPrecioPorHora(), t.getFraccionMinutos());
            }

            cargarTarifas();

            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Valores restablecidos", "Se restauraron los valores por defecto. Presiona Guardar para aplicar."));
        } catch (Exception ex) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Error", "No se pudieron restablecer los valores: " + ex.getMessage()));
        }
    }

    public String getNombreParqueadero() { return nombreParqueadero; }
    public void setNombreParqueadero(String nombreParqueadero) { this.nombreParqueadero = nombreParqueadero; }
    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }
    public int getFilasDefecto() { return filasDefecto; }
    public void setFilasDefecto(int filasDefecto) { this.filasDefecto = filasDefecto; }
    public int getColumnasDefecto() { return columnasDefecto; }
    public void setColumnasDefecto(int columnasDefecto) { this.columnasDefecto = columnasDefecto; }
    public Part getLogoFile() { return logoFile; }
    public void setLogoFile(Part logoFile) { this.logoFile = logoFile; }
    public String getLogoPath() {
        String path = getConfiguracion().getLogoPath();
        return path != null ? path : "";
    }
    public String getMoneda() { return getConfiguracion().getMoneda(); }

    public static class TarifaEditable implements Serializable {
        private static final long serialVersionUID = 1L;

        private final TipoVehiculo tipo;
        private final String tipoDescripcion;
        private double precioHora;
        private double fraccionMinutos;

        public TarifaEditable(TipoVehiculo tipo, String tipoDescripcion, double precioHora, double fraccionMinutos) {
            this.tipo = tipo;
            this.tipoDescripcion = tipoDescripcion;
            this.precioHora = precioHora;
            this.fraccionMinutos = fraccionMinutos;
        }

        public TipoVehiculo getTipo() { return tipo; }
        public String getTipoDescripcion() { return tipoDescripcion; }
        public double getPrecioHora() { return precioHora; }
        public void setPrecioHora(double precioHora) { this.precioHora = precioHora; }
        public double getFraccionMinutos() { return fraccionMinutos; }
        public void setFraccionMinutos(double fraccionMinutos) { this.fraccionMinutos = fraccionMinutos; }
    }
}
