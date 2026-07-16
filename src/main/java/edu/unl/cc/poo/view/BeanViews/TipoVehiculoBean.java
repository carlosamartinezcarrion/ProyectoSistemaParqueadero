package edu.unl.cc.poo.view.BeanViews;

import edu.unl.cc.poo.domain.enums.TipoVehiculo;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.model.SelectItem;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.List;

@Named("tipoVehiculoBean")
@ApplicationScoped
public class TipoVehiculoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    public List<SelectItem> getItems() {
        return List.of(
                new SelectItem(TipoVehiculo.AUTOMOVIL, "Automóvil"),
                new SelectItem(TipoVehiculo.MOTO, "Moto"),
                new SelectItem(TipoVehiculo.CAMIONETA, "Camioneta")
        );
    }
}
