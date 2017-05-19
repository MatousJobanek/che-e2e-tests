package com.redhat.arquillian.che.config;

import java.util.Map;
import org.jboss.arquillian.config.descriptor.api.ArquillianDescriptor;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.ApplicationScoped;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;

public class CheExtensionConfigurator {

    @Inject
    @ApplicationScoped
    private InstanceProducer<CheExtensionConfiguration> reporterConfigurationInstanceProducer;

    public void configure(@Observes ArquillianDescriptor arquillianDescriptor){
        Map<String, String> reporterProps = arquillianDescriptor.extension("che").getExtensionProperties();
        CheExtensionConfiguration reporterConfiguration = CheExtensionConfiguration.fromMap(reporterProps);
        reporterConfigurationInstanceProducer.set(reporterConfiguration);
    }
}
