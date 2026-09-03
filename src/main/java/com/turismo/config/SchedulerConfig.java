package com.turismo.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Habilita @Scheduled para los jobs de sincronizacion periodica de
 * PeruRail y SENAMHI (RNF-03), definidos en integration.scheduler.
 */
@Configuration
@EnableScheduling
public class SchedulerConfig {
}
