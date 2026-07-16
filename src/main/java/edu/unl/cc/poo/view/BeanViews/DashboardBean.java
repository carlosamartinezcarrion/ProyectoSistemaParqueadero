package edu.unl.cc.poo.view.BeanViews;

import edu.unl.cc.poo.business.ParqueaderoService;
import edu.unl.cc.poo.domain.model.Registro;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Named("dashboardBean")
@RequestScoped
public class DashboardBean implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ParqueaderoService parqueaderoService;

    public int getCapacidad() { return parqueaderoService.getCapacidad(); }
    public int getEspaciosDisponibles() { return parqueaderoService.getEspaciosDisponibles(); }
    public long getOcupados() { return parqueaderoService.getOcupados(); }
    public long getInhabilitados() { return parqueaderoService.getInhabilitados(); }
    public double getTotalRecaudado() { return parqueaderoService.getTotalRecaudado(); }
    public double getPorcentajeOcupacion() { return parqueaderoService.getPorcentajeOcupacion(); }

    public List<Registro> getUltimosRegistros() {
        return parqueaderoService.getHistorialReciente(5);
    }

    public List<BarraResumen> getOcupacionSemanal() {
        LocalDate hoy = LocalDate.now();
        List<BarraResumen> resultado = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate fecha = hoy.minusDays(i);
            long conteo = parqueaderoService.getRegistrosDelDia(fecha).size();
            double altura = Math.min(100, Math.max(18, conteo * 14.0));
            resultado.add(new BarraResumen(
                    fecha.getDayOfWeek().getDisplayName(TextStyle.SHORT, new Locale("es")),
                    conteo,
                    altura));
        }
        return resultado;
    }

    public List<EstadoResumen> getResumenEstados() {
        return List.of(
                new EstadoResumen("Libres", String.valueOf(getEspaciosDisponibles()), "check_circle"),
                new EstadoResumen("Ocupados", String.valueOf(getOcupados()), "directions_car"),
                new EstadoResumen("Inhabilitados", String.valueOf(getInhabilitados()), "block"),
                new EstadoResumen("Capacidad", String.valueOf(getCapacidad()), "view_quilt"),
                new EstadoResumen("Total recaudado", "$" + String.format("%.2f", getTotalRecaudado()), "payments")
        );
    }

    public long getRegistrosActivos() {
        return parqueaderoService.getHistorial().stream().filter(Registro::estaActivo).count();
    }

    public List<String> getUltimasPlacas() {
        return parqueaderoService.getHistorialReciente(5).stream()
                .map(r -> r.getVehiculo().getPlaca())
                .collect(Collectors.toList());
    }

    public static class BarraResumen {
        private final String dia;
        private final long cantidad;
        private final double altura;

        public BarraResumen(String dia, long cantidad, double altura) {
            this.dia = dia;
            this.cantidad = cantidad;
            this.altura = altura;
        }

        public String getDia() { return dia; }
        public long getCantidad() { return cantidad; }
        public double getAltura() { return altura; }
    }

    public static class EstadoResumen {
        private final String etiqueta;
        private final String valor;
        private final String icono;

        public EstadoResumen(String etiqueta, String valor, String icono) {
            this.etiqueta = etiqueta;
            this.valor = valor;
            this.icono = icono;
        }

        public String getEtiqueta() { return etiqueta; }
        public String getValor() { return valor; }
        public String getIcono() { return icono; }
    }
}
