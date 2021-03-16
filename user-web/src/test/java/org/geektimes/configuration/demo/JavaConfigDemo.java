package org.geektimes.configuration.demo;

import org.geektimes.configuration.microprofile.config.JavaConfig;
import org.geektimes.configuration.microprofile.config.JavaEEConfigProviderResolver;

import java.util.prefs.BackingStoreException;

public class JavaConfigDemo {
    public static void main(String[] args) throws BackingStoreException {
        JavaEEConfigProviderResolver javaEEConfigProviderResolver = new JavaEEConfigProviderResolver();
        JavaConfig javaConfig = (JavaConfig) javaEEConfigProviderResolver.getConfig();
        javaConfig.getPropertyNames().forEach(System.out::println);
    }
}
