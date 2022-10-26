package ru.javawebinar.topjava;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;

@Profile("!inmemory")
@Configuration
@EnableTransactionManagement
@ComponentScan("ru.javawebinar.topjava")
public class AppConfig {
    //    @Bean
    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://localhost:5432/topjava");
        dataSource.setUsername("user");
        dataSource.setPassword("password");
        return dataSource;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate(DataSource dataSource) {
        return new NamedParameterJdbcTemplate(dataSource);
    }

    //            <bean id="jdbcTemplate"class="org.springframework.jdbc.core.JdbcTemplate">
//        <constructor-
//    arg ref = "dataSource" / >
//    </bean>
//
//    <bean id="namedJdbcTemplate"class="org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate">
//        <constructor-
//    arg ref = "jdbcTemplate" / >
//    </bean>
//}
}
