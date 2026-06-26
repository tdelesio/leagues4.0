package com.makeurpicks;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.makeurpicks.repository.DoublePickRepository;
import com.makeurpicks.repository.PickRepository;
import com.makeurpicks.repository.PicksByWeekRepository;

@SpringBootApplication
@EntityScan(basePackages = "com.makeurpicks.domain")
@EnableJpaRepositories(
    basePackages = "com.makeurpicks.repository",
    excludeFilters = @ComponentScan.Filter(
        type = FilterType.ASSIGNABLE_TYPE,
        classes = { PickRepository.class, DoublePickRepository.class, PicksByWeekRepository.class }
    )
)
public class MonolithApplication {

    public static void main(String[] args) {
        SpringApplication.run(MonolithApplication.class, args);
    }
}
