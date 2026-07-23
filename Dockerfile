FROM openliberty/open-liberty:full-java21-openj9-ubi-minimal

COPY --chown=1001:0 src/main/liberty/config/server.xml /config/server.xml
COPY --chown=1001:0 target/ProyectoSistemaParqueadero-1.0-SNAPSHOT.war /config/apps/Sistema-Parqueadero-App.war
COPY --chown=1001:0 target/liberty/wlp/usr/shared/resources/postgres/ /opt/ol/wlp/usr/shared/resources/postgres/

EXPOSE 9080 9443