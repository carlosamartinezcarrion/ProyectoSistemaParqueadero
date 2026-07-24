package edu.unl.cc.poo.view.registration;

import edu.unl.cc.poo.business.core.ParqueaderoService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import java.io.Serializable;

@Named("layoutController")
@RequestScoped
public class LayoutController implements Serializable {
    private static final long serialVersionUID = 1L;

    @Inject
    private ParqueaderoService parqueaderoService;

    public String getNombreParqueadero() {
        return parqueaderoService.getNombreParqueadero();
    }
}
