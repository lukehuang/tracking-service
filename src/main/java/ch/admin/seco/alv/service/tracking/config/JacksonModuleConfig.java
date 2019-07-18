package ch.admin.seco.alv.service.tracking.config;

import org.zalando.problem.ProblemModule;

import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
class JacksonModuleConfig {

    @Bean
    public ProblemModule problemModule() {
        return new ProblemModule();
    }

}
