package edu.unl.cc.poo.business.core;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.io.Serializable;
import java.util.logging.Logger;

@Named("jpaInitializer")
@ApplicationScoped
public class JpaInitializer implements Serializable {
    
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(JpaInitializer.class.getName());
    
    @PersistenceContext(unitName = "ParqueaderoPU")
    private EntityManager entityManager;
    
    @PostConstruct
    public void init() {
        LOG.info("JpaInitializer: init() called, entityManager = " + entityManager);
        if (entityManager != null) {
            LOG.info("JpaInitializer: entityManager.isOpen() = " + entityManager.isOpen());
            try {
                LOG.info("JpaInitializer: executing native query to trigger schema generation...");
                Object result = entityManager.createNativeQuery("SELECT 1").getSingleResult();
                LOG.info("JpaInitializer: query result = " + result);
                
                // Also try to trigger DDL generation by accessing the metamodel
                entityManager.getMetamodel().getEntities().forEach(e -> 
                    LOG.info("JpaInitializer: Entity found: " + e.getName()));
                
            } catch (Exception e) {
                LOG.severe("JpaInitializer: Error during initialization: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            LOG.warning("JpaInitializer: entityManager is NULL!");
        }
    }
}