package edu.unl.cc.poo.view.BeanViews;

import edu.unl.cc.poo.business.ParqueaderoService;
import edu.unl.cc.poo.business.PdfService;
import edu.unl.cc.poo.domain.model.Registro;
import edu.unl.cc.poo.domain.model.Ticket;
import edu.unl.cc.poo.domain.model.Vehiculo;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Named("registroBean")
@ViewScoped
public class RegistroBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ParqueaderoService parqueaderoService;

    @Inject
    private PdfService pdfService;

    private String placa;
    private String conductor;
    private TipoVehiculo tipo = TipoVehiculo.AUTOMOVIL;
    private Integer fila;
    private Integer columna;
    private String registroId;
    private Registro registroConsultado;
    private String resumenOperacion;
    private String pdfGenerado;
    private Double totalEstimado;
    private java.util.List<Registro> registrosActivosBusqueda = new java.util.ArrayList<>();
    private int indiceRegistroSeleccionado = -1;
    private boolean salidaProcesada = false;

    public void seleccionarEspacio(int fila, int columna) {
        this.fila = fila;
        this.columna = columna;
        this.resumenOperacion = "Espacio seleccionado: " + getEspacioSeleccionado();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO,
                        "Espacio asignado", getEspacioSeleccionado()));
    }

    public void limpiarSeleccionEspacio() {
        this.fila = null;
        this.columna = null;
        this.resumenOperacion = "Selección de espacio limpiada. Se asignará el primer libre disponible.";
    }

    public void limpiarFormularioEntrada() {
        this.placa = null;
        this.conductor = null;
        this.tipo = TipoVehiculo.AUTOMOVIL;
        this.fila = null;
        this.columna = null;
        this.resumenOperacion = null;
        this.pdfGenerado = null;
        this.registroConsultado = null;
        this.registroId = null;
        this.totalEstimado = null;
    }

    public String registrarEntrada() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            if (placa == null || placa.isBlank()) {
                ctx.addMessage("entradaForm:placa", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "La placa es obligatoria.", null));
                return null;
            }
            if (!placa.trim().matches("[A-Za-z0-9\\-]{1,12}")) {
                ctx.addMessage("entradaForm:placa", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "La placa solo debe contener letras, numeros o guiones (max 12 caracteres).", null));
                return null;
            }
            if (conductor == null || conductor.isBlank()) {
                ctx.addMessage("entradaForm:conductor", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "El nombre del conductor es obligatorio.", null));
                return null;
            }
            if (conductor.trim().matches(".*\\d.*")) {
                ctx.addMessage("entradaForm:conductor", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "El nombre del conductor no debe contener numeros.", null));
                return null;
            }
            if (conductor.trim().length() < 3) {
                ctx.addMessage("entradaForm:conductor", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                        "El nombre del conductor debe tener al menos 3 caracteres.", null));
                return null;
            }

            Vehiculo v = new Vehiculo(placa.trim().toUpperCase(), conductor.trim());
            v.setTipo(tipo);
            Registro r = parqueaderoService.registrarEntrada(v, fila, columna);
            this.registroId = r.getId();
            this.registroConsultado = r;
            this.resumenOperacion = "Entrada registrada para " + r.getVehiculo().getPlaca()
                    + " en el espacio " + r.getEspacio().getEtiqueta()
                    + " (ID: " + r.getId() + ")";
            generarTicket(r);
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO,
                    "Entrada registrada", resumenOperacion));
            ctx.getExternalContext().getFlash().setKeepMessages(true);
            limpiarFormularioEntrada();
            return "mapaParqueadero.xhtml?faces-redirect=true";
        } catch (Exception e) {
            ctx.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR,
                    "No se pudo registrar la entrada", e.getMessage()));
            return null;
        }
    }

    public String buscarRegistroActivo() {
        registroConsultado = parqueaderoService.buscarRegistroActivoPorTexto(registroId);
        totalEstimado = null;
        if (registroConsultado == null) {
            resumenOperacion = "No se encontró un registro activo para: " + registroId;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Registro no encontrado", resumenOperacion));
            return null;
        }

        // Normalizar al ID real para la salida
        this.registroId = registroConsultado.getId();
        totalEstimado = calcularTotalEstimado(registroConsultado);
        resumenOperacion = "Registro encontrado: " + registroConsultado.getVehiculo().getPlaca()
                + " en " + registroConsultado.getEspacio().getEtiqueta()
                + " · ID " + registroConsultado.getId();
        FacesContext.getCurrentInstance().addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_INFO, "Registro localizado", resumenOperacion));
        return null;
    }

    public void buscarRegistrosActivos() {
        registrosActivosBusqueda.clear();
        registroConsultado = null;
        totalEstimado = null;
        resumenOperacion = null;
        salidaProcesada = false;
        if (registroId != null && !registroId.isBlank()) {
            Registro r = parqueaderoService.buscarRegistroActivoPorTexto(registroId.trim());
            if (r != null) {
                registrosActivosBusqueda.add(r);
            }
        } else {
            registrosActivosBusqueda.addAll(parqueaderoService.listarRegistrosActivos());
        }
        if (registrosActivosBusqueda.isEmpty()) {
            resumenOperacion = "No se encontraron registros activos.";
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Sin resultados", "No se encontraron registros activos para la busqueda."));
        }
    }

    public void seleccionarRegistro() {
        if (indiceRegistroSeleccionado >= 0 && indiceRegistroSeleccionado < registrosActivosBusqueda.size()) {
            registroConsultado = registrosActivosBusqueda.get(indiceRegistroSeleccionado);
            registroId = registroConsultado.getId();
            totalEstimado = calcularTotalEstimado(registroConsultado);
            resumenOperacion = "Registro seleccionado: " + registroConsultado.getVehiculo().getPlaca()
                    + " en " + registroConsultado.getEspacio().getEtiqueta()
                    + " · ID " + registroConsultado.getId();
        }
    }

    public String registrarSalida() {
        FacesContext ctx = FacesContext.getCurrentInstance();
        try {
            if (registroConsultado == null || !registroConsultado.estaActivo()) {
                if (registroId == null || registroId.isBlank()) {
                    ctx.addMessage("busquedaForm:registroId", new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "Debe buscar y seleccionar un registro activo primero.", null));
                    return null;
                }
            }
            String criterio = resolverCriterioSalida();
            Registro r = parqueaderoService.registrarSalida(criterio);
            this.registroConsultado = r;
            this.registroId = r.getId();
            this.totalEstimado = r.getTotalCobrado();
            this.resumenOperacion = "Salida procesada para " + r.getVehiculo().getPlaca()
                    + " por $" + String.format("%.2f", r.getTotalCobrado())
                    + " (" + r.getDuracionMinutos() + " min)";
            this.salidaProcesada = true;
            this.pdfGenerado = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO,
                            "Salida procesada", resumenOperacion));
            return null;
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR,
                            "No se pudo procesar la salida", e.getMessage()));
            return null;
        }
    }

    public void generarTicketSalida() {
        if (registroConsultado == null) {
            return;
        }
        generarTicket(registroConsultado);
    }

    private String resolverCriterioSalida() {
        if (registroConsultado != null && registroConsultado.estaActivo()) {
            return registroConsultado.getId();
        }
        if (registroId != null && !registroId.isBlank()) {
            return registroId.trim();
        }
        throw new IllegalArgumentException("Indica el ID de registro o la placa del vehículo.");
    }

    private Double calcularTotalEstimado(Registro registro) {
        if (registro == null || !registro.estaActivo()) {
            return registro != null ? registro.getTotalCobrado() : null;
        }
        long minutos = ChronoUnit.MINUTES.between(registro.getFechaHoraEntrada(), LocalDateTime.now());
        if (minutos < 1) {
            minutos = 1;
        }
        return parqueaderoService.getParqueadero().getConfiguracion()
                .getTarifaPorTipo(registro.getVehiculo().getTipo())
                .calcularCostos(minutos);
    }

    private void generarTicket(Registro registro) {
        try {
            Ticket ticket = new Ticket(parqueaderoService.getParqueadero().getConfiguracion(), registro);
            pdfService.generarPDF(ticket);
            this.pdfGenerado = ticket.getNombreArchivoPdf();
        } catch (RuntimeException ex) {
            this.pdfGenerado = null;
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_WARN,
                            "Ticket PDF no generado", ex.getMessage()));
        }
    }

    public String getPdfDownloadUrl() {
        if (pdfGenerado == null || pdfGenerado.isBlank()) {
            return null;
        }
        return "pdf/" + pdfGenerado;
    }

    public String getEspacioSeleccionado() {
        if (fila == null || columna == null) {
            return "Automático (primer libre)";
        }
        return String.valueOf((char) ('A' + fila)) + (columna + 1);
    }

    /** Evita error de binding en h:inputText readonly sin setter. */
    public void setEspacioSeleccionado(String ignored) {
        // no-op: el espacio se asigna con seleccionarEspacio
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getConductor() {
        return conductor;
    }

    public void setConductor(String conductor) {
        this.conductor = conductor;
    }

    public TipoVehiculo getTipo() {
        return tipo;
    }

    public void setTipo(TipoVehiculo tipo) {
        this.tipo = tipo;
    }

    public Integer getFila() {
        return fila;
    }

    public void setFila(Integer fila) {
        this.fila = fila;
    }

    public Integer getColumna() {
        return columna;
    }

    public void setColumna(Integer columna) {
        this.columna = columna;
    }

    public String getRegistroId() {
        return registroId;
    }

    public void setRegistroId(String registroId) {
        this.registroId = registroId;
    }

    public Registro getRegistroConsultado() {
        return registroConsultado;
    }

    public String getResumenOperacion() {
        return resumenOperacion;
    }

    public String getPdfGenerado() {
        return pdfGenerado;
    }

    public Double getTotalEstimado() {
        return totalEstimado;
    }

    public java.util.List<Registro> getRegistrosActivosBusqueda() {
        return registrosActivosBusqueda;
    }

    public int getIndiceRegistroSeleccionado() {
        return indiceRegistroSeleccionado;
    }

    public void setIndiceRegistroSeleccionado(int indiceRegistroSeleccionado) {
        this.indiceRegistroSeleccionado = indiceRegistroSeleccionado;
    }

    public boolean isSalidaProcesada() {
        return salidaProcesada;
    }
}
