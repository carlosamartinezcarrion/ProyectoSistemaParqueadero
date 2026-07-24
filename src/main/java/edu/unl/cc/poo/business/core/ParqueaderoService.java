package edu.unl.cc.poo.business.core;

import edu.unl.cc.poo.business.service.CrudGenericService;
import edu.unl.cc.poo.domain.Configuracion;
import edu.unl.cc.poo.domain.EspacioParqueadero;
import edu.unl.cc.poo.domain.MapaParqueadero;
import edu.unl.cc.poo.domain.Parqueadero;
import edu.unl.cc.poo.domain.Registro;
import edu.unl.cc.poo.domain.Tarifa;
import edu.unl.cc.poo.domain.Vehiculo;
import edu.unl.cc.poo.domain.enums.EstadoEspacio;
import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Named("parqueaderoService")
@ApplicationScoped
public class ParqueaderoService implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(ParqueaderoService.class.getName());

    @Inject
    private CrudGenericService crud;

    @PersistenceContext(unitName = "ParqueaderoPU")
    private EntityManager em;

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
        MapaParqueadero mapa = new MapaParqueadero(configuracion.getFilasDefecto(), configuracion.getColumnasDefecto());
        mapa.setParqueadero(this.parqueadero);
        mapa.inicializarEspacios();
        this.parqueadero.setMapa(mapa);
        cargarHistorialDesdeDB();
    }

    private void cargarHistorialDesdeDB() {
        try {
            List<Object[]> rows = em.createNativeQuery(
                            "SELECT r.uuid, r.placa, r.conductor, r.tipo_vehiculo, r.espacio_etiqueta, " +
                                    "r.fecha_hora_entrada, r.fecha_hora_salida, r.duracion_minutos, r.total_cobrado, " +
                                    "v.tipo " +
                                    "FROM registro r LEFT JOIN vehiculo v ON r.vehiculo_id = v.id " +
                                    "ORDER BY r.fecha_hora_entrada ASC")
                    .getResultList();
            for (Object[] row : rows) {
                String uuid = (String) row[0];
                String placa = (String) row[1];
                String conductor = (String) row[2];
                String tipoStr = (String) row[3];
                String etiqueta = (String) row[4];
                LocalDateTime entrada = ((Timestamp) row[5]).toLocalDateTime();
                LocalDateTime salida = row[6] != null ? ((Timestamp) row[6]).toLocalDateTime() : null;
                long duracion = row[7] != null ? ((Number) row[7]).longValue() : 0L;
                double total = row[8] != null ? ((Number) row[8]).doubleValue() : 0.0;
                String tipoV = (String) row[9];
                TipoVehiculo tipoVehiculo;
                try {
                    tipoVehiculo = TipoVehiculo.valueOf(tipoV != null && !tipoV.isBlank() ? tipoV : tipoStr);
                } catch (Exception e) {
                    tipoVehiculo = TipoVehiculo.AUTOMOVIL;
                }
                Vehiculo v = new Vehiculo(placa, conductor);
                v.setTipo(tipoVehiculo);
                char letra = etiqueta.charAt(0);
                int fila = letra - 'A';
                int columna = Integer.parseInt(etiqueta.substring(1)) - 1;
                EspacioParqueadero ep = new EspacioParqueadero(0, fila, columna);
                Registro r = new Registro(v, ep);
                r.setUuid(uuid);
                r.setFechaHoraEntrada(entrada);
                r.setFechaHoraSalida(salida);
                r.setDuracionMinutos(duracion);
                r.setTotalCobrado(total);
                r.setParqueadero(this.parqueadero);
                parqueadero.getHistorial().add(r);
            }
            LOG.info("Cargados " + rows.size() + " registros desde la BD");
        } catch (Exception e) {
            LOG.warning("No se pudo cargar historial desde BD: " + e.getMessage());
        }
    }

    private void insertarRegistroDB(Registro r) {
        try {
            Number vehiculoId;
            List<?> existentes = em.createNativeQuery("SELECT id FROM vehiculo WHERE placa = ?")
                    .setParameter(1, r.getVehiculo().getPlaca())
                    .getResultList();
            if (!existentes.isEmpty()) {
                vehiculoId = (Number) existentes.get(0);
            } else {
                em.createNativeQuery("SELECT setval('vehiculo_id_seq', GREATEST((SELECT COALESCE(MAX(id), 0) FROM vehiculo), 1) + 1, false)")
                        .getSingleResult();
                em.createNativeQuery("INSERT INTO vehiculo (placa, nombre_conductor, tipo) VALUES (?, ?, ?)")
                        .setParameter(1, r.getVehiculo().getPlaca())
                        .setParameter(2, r.getVehiculo().getNombreConductor())
                        .setParameter(3, r.getVehiculo().getTipo().name())
                        .executeUpdate();
                vehiculoId = (Number) em.createNativeQuery("SELECT currval('vehiculo_id_seq')")
                        .getSingleResult();
            }

            em.createNativeQuery("INSERT INTO registro (uuid, placa, conductor, tipo_vehiculo, espacio_etiqueta, fecha_hora_entrada, moneda, vehiculo_id, espacio_id, parqueadero_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, (SELECT id FROM parqueadero LIMIT 1))")
                    .setParameter(1, r.getUuid())
                    .setParameter(2, r.getPlaca())
                    .setParameter(3, r.getNombreConductor())
                    .setParameter(4, r.getTipoVehiculo())
                    .setParameter(5, r.getEspacioEtiqueta())
                    .setParameter(6, java.sql.Timestamp.valueOf(r.getFechaHoraEntrada()))
                    .setParameter(7, "USD")
                    .setParameter(8, vehiculoId.longValue())
                    .setParameter(9, r.getEspacio().getNumero())
                    .executeUpdate();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo insertar registro en BD", e);
        }
    }

    private void actualizarRegistroDB(Registro r) {
        try {
            em.createNativeQuery("UPDATE registro SET fecha_hora_salida=?, duracion_minutos=?, total_cobrado=? WHERE uuid=?")
                    .setParameter(1, r.getFechaHoraSalida() != null ? java.sql.Timestamp.valueOf(r.getFechaHoraSalida()) : null)
                    .setParameter(2, r.getDuracionMinutos())
                    .setParameter(3, r.getTotalCobrado())
                    .setParameter(4, r.getUuid())
                    .executeUpdate();
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo actualizar registro en BD", e);
        }
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
                .filter(r -> r.getUuid().equalsIgnoreCase(registroId.trim()))
                .findFirst()
                .orElse(null);
    }

    public Registro buscarRegistroActivoPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        return parqueadero.buscarRegistroActivoPorPlaca(texto);
    }

    @Transactional
    public Registro registrarEntrada(Vehiculo v, Integer fila, Integer columna) {
        Registro registro = parqueadero.registrarEntrada(v, fila, columna);
        insertarRegistroDB(registro);
        return registro;
    }

    @Transactional
    public Registro registrarSalida(String registroIdOPlaca) {
        Registro registro = parqueadero.registrarSalida(registroIdOPlaca);
        actualizarRegistroDB(registro);
        return registro;
    }

    public void setEstadoEspacio(int fila, int columna, EstadoEspacio estado) {
        parqueadero.setEstadoEspacio(fila, columna, estado);
    }

    @Transactional
    public Registro forzarLiberacionEspacio(int fila, int columna) {
        Registro registro = parqueadero.forzarLiberacionEspacio(fila, columna);
        if (registro != null) {
            actualizarRegistroDB(registro);
        }
        return registro;
    }

    @Transactional
    public boolean guardarYAplicarConfiguracion() {
        parqueadero.aplicarConfiguracion();
        Configuracion cfg = parqueadero.getConfiguracion();

        try {
            int actualizadas = em.createNativeQuery(
                            "UPDATE configuracion SET nombre_parqueadero=?, direccion=?, filas_defecto=?, columnas_defecto=?, logo_path=?, moneda=? WHERE id=1")
                    .setParameter(1, cfg.getNombreParqueadero())
                    .setParameter(2, cfg.getDireccion())
                    .setParameter(3, cfg.getFilasDefecto())
                    .setParameter(4, cfg.getColumnasDefecto())
                    .setParameter(5, cfg.getLogoPath() != null ? cfg.getLogoPath() : "")
                    .setParameter(6, cfg.getMoneda())
                    .executeUpdate();

            if (actualizadas == 0) {
                em.createNativeQuery(
                                "INSERT INTO configuracion (id, nombre_parqueadero, direccion, filas_defecto, columnas_defecto, logo_path, moneda) VALUES (1, ?, ?, ?, ?, ?, ?)")
                        .setParameter(1, cfg.getNombreParqueadero())
                        .setParameter(2, cfg.getDireccion())
                        .setParameter(3, cfg.getFilasDefecto())
                        .setParameter(4, cfg.getColumnasDefecto())
                        .setParameter(5, cfg.getLogoPath() != null ? cfg.getLogoPath() : "")
                        .setParameter(6, cfg.getMoneda())
                        .executeUpdate();
            }

            for (Tarifa t : cfg.getTarifas()) {
                int tUpd = em.createNativeQuery(
                                "UPDATE tarifa SET precio_por_hora=?, fraccion_minutos=? WHERE tipo_vehiculo=? AND configuracion_id=1")
                        .setParameter(1, t.getPrecioPorHora())
                        .setParameter(2, t.getFraccionMinutos())
                        .setParameter(3, t.getTipoVehiculo().name())
                        .executeUpdate();
                if (tUpd == 0) {
                    em.createNativeQuery(
                                    "INSERT INTO tarifa (tipo_vehiculo, precio_por_hora, fraccion_minutos, configuracion_id) VALUES (?, ?, ?, 1) ")
                            .setParameter(1, t.getTipoVehiculo().name())
                            .setParameter(2, t.getPrecioPorHora())
                            .setParameter(3, t.getFraccionMinutos())
                            .executeUpdate();
                }
            }

            int pUpd = em.createNativeQuery("UPDATE parqueadero SET nombre=?, capacidad=? WHERE id=1")
                    .setParameter(1, parqueadero.getNombre())
                    .setParameter(2, parqueadero.getCapacidad())
                    .executeUpdate();
            if (pUpd == 0) {
                em.createNativeQuery("INSERT INTO parqueadero (id, nombre, capacidad, configuracion_id) VALUES (1, ?, ?, 1)")
                        .setParameter(1, parqueadero.getNombre())
                        .setParameter(2, parqueadero.getCapacidad())
                        .executeUpdate();
            }

            MapaParqueadero mapa = parqueadero.getMapa();
            int mUpd = em.createNativeQuery("UPDATE mapa_parqueadero SET filas=?, columnas=? WHERE parqueadero_id=1")
                    .setParameter(1, mapa.getFilas())
                    .setParameter(2, mapa.getColumnas())
                    .executeUpdate();
            if (mUpd == 0) {
                em.createNativeQuery("INSERT INTO mapa_parqueadero (id, filas, columnas, parqueadero_id) VALUES (1, ?, ?, 1)")
                        .setParameter(1, mapa.getFilas())
                        .setParameter(2, mapa.getColumnas())
                        .executeUpdate();
            }

            LOG.info("Configuracion guardada en BD correctamente");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "No se pudo guardar configuracion en BD", e);
        }

        return cfg.guardarConfiguracion();
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

    public List<Registro> buscarRegistrosActivosPorPlaca(String prefijo) {
        if (prefijo == null || prefijo.isBlank()) {
            return listarRegistrosActivos();
        }
        String criterio = prefijo.trim().toLowerCase();
        return parqueadero.getHistorial().stream()
                .filter(Registro::estaActivo)
                .filter(r -> r.getVehiculo().getPlaca().toLowerCase().startsWith(criterio)
                        || r.getUuid().toLowerCase().startsWith(criterio))
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
