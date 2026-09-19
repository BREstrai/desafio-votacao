package com.brunoestrai.desafio_votacao.configuracao;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class DatabasePgConfig {

    private static final String DRIVER_CLASS_NAME = "org.postgresql.Driver";
    private static final String POOL_NAME = "pg-votacao";

    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @Bean
    public DataSource dataSource() {
        HikariConfig config = new HikariConfig();

        config.setJdbcUrl(dbUrl);
        config.setUsername(dbUser);
        config.setPassword(dbPassword);
        config.setDriverClassName(DRIVER_CLASS_NAME);
        config.setPoolName(POOL_NAME);

        config.setAutoCommit(false);

        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setConnectionTimeout(20000);
        config.setLeakDetectionThreshold(30000);

        return new HikariDataSource(config);
    }
}
