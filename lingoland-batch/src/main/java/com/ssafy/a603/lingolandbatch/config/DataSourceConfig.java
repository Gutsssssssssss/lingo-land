package com.ssafy.a603.lingolandbatch.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.r2dbc.ConnectionFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

import javax.sql.DataSource;

@Configuration
public class DataSourceConfig {

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder.create()
                .driverClassName("org.postgresql.Driver")
                .url("jdbc:postgresql://localhost:5432/lingoland")
                .username("postgres")
                .password("ssafy")
                .build();
    }

    @Bean
    public ConnectionFactory connectionFactory(){
        return ConnectionFactoryBuilder.withUrl("r2dbc:postgresql://localhost:5432/lingoland")
                .username("postgres")
                .password("ssafy")
                .build();
    }

    @Bean
    public R2dbcEntityTemplate r2dbcEntityTemplate(ConnectionFactory connectionFactory){
        return new R2dbcEntityTemplate(connectionFactory);
    }
}
