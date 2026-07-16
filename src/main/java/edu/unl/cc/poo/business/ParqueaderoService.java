package edu.unl.cc.poo.business;

import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import edu.unl.cc.poo.domain.model.*;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Named("parqueaderoService")
@ApplicationScoped
public class ParqueaderoService implements Serializable {
    private static final long serialVersionUID = 1L;

    private Parqueadero parqueadero;

    @PostConstruct
    public void init() {
        Configuracion configuracion = new Configuracion();
        configuracion.cargarConfiguracion();
        String nombre = configuracion.getNombreParqueadero();
        if (nombre == null || nombre.isBlank()) {
            nombre = "AutoManager";
            configuracion.setNombreParqueadero(nombre);
        }
        this.parqueadero = new Parqueadero(nombre, configuracion);
    }

    public Parqueadero getParqueadero() {
        return parqueadero;
    }

    public int getCapacidad() {
        return parqueadero.getCapacidad();
    }

    public int getEspaciosDisponibles() {
        return parqueadero.getEspaciosDisponibles();
    }

    public long getOcupados() {
        return parqueadero.getMapa().contarOcupados();
    }

    public long getInhabilitados() {
        return parqueadero.getMapa().contarInhabilitados();
    }

    public double getTotalRecaudado() {
        return parqueadero.getHistorial().stream().mapToDouble(Registro::getTotalCobrado).sum();
    }

    public double getPorcentajeOcupacion() {
        if (getCapacidad() <= 0) {
            return 0.0;
        }
        return Math.round((getOcupados() * 1000.0) / getCapacidad()) / 10.0;
    }

    public String getNombreParqueadero() {
        return parqueadero.getConfiguracion().getNombreParqueadero();
    }

    public String getDireccion() {
        return parqueadero.getConfiguracion().getDireccion();
    }

    public List<Registro> getHistorial() {
        return parqueadero.getHistorial();
    }

    public List<Registro> getHistorialReciente(int limite) {
        return parqueadero.getHistorial().stream()
                .sorted(Comparator.comparing(Registro::getFechaHoraEntrada).reversed())
                .limit(Math.max(limite, 0))
                .collect(Collectors.toList());
    }

    public List<Registro> getHistorialReciente() {
        return getHistorialReciente(4);
    }

    public int getHistorialCount() {
        return parqueadero.getHistorial().size();
    }

    public List<Registro> buscarHistorial(String placa, LocalDate fecha) {
        return parqueadero.getHistorial().stream()
                .filter(registro -> placa == null || placa.isBlank()
                        || registro.getVehiculo().getPlaca().toLowerCase().contains(placa.toLowerCase()))
                .filter(registro -> fecha == null
                        || registro.getFechaHoraEntrada().toLocalDate().equals(fecha))
                .sorted(Comparator.comparing(Registro::getFechaHoraEntrada).reversed())
                .collect(Collectors.toList());
    }

    public Registro buscarRegistroPorId(String registroId) {
        if (registroId == null || registroId.isBlank()) {
            return null;
        }
        return parqueadero.getHistorial().stream()
                .filter(r -> r.getId().equalsIgnoreCase(registroId.trim()))
                .findFirst()
                .orElse(null);
    }

    public Registro buscarRegistroActivoPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return parqueadero.buscarRegistroActivoPorPlaca(texto);
    }

    public Registro registrarEntrada(Vehiculo v, Integer fila, Integer columna) {
        return parqueadero.registrarEntrada(v, fila, columna);
    }

    public Registro registrarSalida(String registroIdOPlaca) {
        return parqueadero.registrarSalida(registroIdOPlaca);
    }

    public void setEstadoEspacio(int fila, int columna, EstadoEspacio estado) {
        parqueadero.setEstadoEspacio(fila, columna, estado);
    }

    public Registro forzarLiberacionEspacio(int fila, int columna) {
        return parqueadero.forzarLiberacionEspacio(fila, columna);
    }

    /**
     * Persiste la configuración actual y aplica cambios de nombre/dimensiones al mapa.
     */
    public boolean guardarYAplicarConfiguracion() {
        parqueadero.aplicarConfiguracion();
        return parqueadero.getConfiguracion().guardarConfiguracion();
    }

    public List<Registro> getRegistrosDelDia(LocalDate fecha) {
        return parqueadero.getHistorial().stream()
                .filter(r -> r.getFechaHoraEntrada().toLocalDate().equals(fecha))
                .sorted(Comparator.comparing(Registro::getFechaHoraEntrada))
                .collect(Collectors.toList());
    }

    public List<Registro> listarRegistrosActivos() {
        return parqueadero.getHistorial().stream()
                .filter(Registro::estaActivo)
                .sorted(Comparator.comparing(Registro::getFechaHoraEntrada).reversed())
                .collect(Collectors.toList());
    }

    public long getRegistrosActivosCount() {
        return parqueadero.getHistorial().stream().filter(Registro::estaActivo).count();
    }

    public int getColumnasMapa() {
        return parqueadero.getMapa().getColumnas();
    }

    public int getFilasMapa() {
        return parqueadero.getMapa().getFilas();
    }
}
