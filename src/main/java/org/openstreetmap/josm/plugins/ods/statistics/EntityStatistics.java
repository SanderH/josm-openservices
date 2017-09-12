package org.openstreetmap.josm.plugins.ods.statistics;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashSet;
import java.util.Set;

import org.openstreetmap.josm.Main;
import org.openstreetmap.josm.plugins.ods.OdsDataSource;
import org.openstreetmap.josm.plugins.ods.OdsModule;
import org.openstreetmap.josm.plugins.ods.OpenDataServicesPlugin;
import org.openstreetmap.josm.plugins.ods.entities.EntityType;
import org.openstreetmap.josm.plugins.ods.io.Host;
import org.openstreetmap.josm.tools.I18n;

public class EntityStatistics {
    private OdsModule module;

    public void run() {
        module = OpenDataServicesPlugin.INSTANCE.getActiveModule();
        try (
                Writer writer = new StringWriter();
                ) {
            if (module == null) {
                logLn(writer, "At the moment there is no active ODS module");
            }
            else {
                logModuleStatistics(writer);
            }
            Main.info(writer.toString());
        } catch (IOException e) {
            // Deliberately ignored
        }
    }

    private void logModuleStatistics(Writer writer) throws IOException {
        listEntityTypes(writer);
        listHosts(writer);
        listDataSources(writer);
    }

    private void listEntityTypes(Writer writer) throws IOException {
        logLn(writer, "Entity types:");
        for (EntityType entityType : module.getEntityTypes()) {
            logLn(writer, "\t {0}", entityType);
        }
        logLn(writer);
    }

    private void listHosts(Writer writer) throws IOException {
        Set<Host> hosts = new HashSet<>();
        for (OdsDataSource dataSource : module.getDataSources().values()) {
            hosts.add(dataSource.getOdsFeatureSource().getHost());
        }
        logLn(writer, "Hosts:");
        for (Host host : hosts) {
            logLn(writer, "\t {0}", host);
        }
        logLn(writer);
    }

    private void listDataSources(Writer writer) throws IOException {
        logLn(writer, "Data sources:");
        for (OdsDataSource dataSource : module.getDataSources().values()) {
            logLn(writer, "\t {0}", dataSource);
        }
        logLn(writer);
    }

    private static void log(Writer writer, String text, Object ...objects ) throws IOException {
        writer.write(I18n.tr(text, objects));
    }

    private static void logLn(Writer writer) throws IOException {
        writer.write("\n");
    }

    private static void logLn(Writer writer, String text, Object ...objects ) throws IOException {
        log(writer, text, objects);
        logLn(writer);
    }

}
