package org.yznal.matrixprofile;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.yznal.matrixprofile.service.AnomalyReactor;

@SpringBootTest
class MatrixProfileApplicationTest {

    @Configuration
    static class MatrixProfileApplicationTestConfiguration {
        @Bean
        public AnomalyReactor dummyAnomalyReactor() {
            return metric -> {
                // Do nothing
            };
        }
    }

    @Test
    void contextLoads() {
    }

}
