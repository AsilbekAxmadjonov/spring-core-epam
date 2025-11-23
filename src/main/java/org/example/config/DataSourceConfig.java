package org.example.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

import javax.sql.DataSource;
import java.util.Properties;

import com.zaxxer.hikari.HikariDataSource;

@Configuration
@PropertySource(value = "classpath:application.yaml", factory = YamlPropertySourceFactory.class)
public class DataSourceConfig {

    private final DatabaseConnectionProperties databaseConnectionProperties;
    private final HibernateProperties hibernateProps;

    public DataSourceConfig(DatabaseConnectionProperties databaseConnectionProperties, HibernateProperties hibernateProps) {
        this.databaseConnectionProperties = databaseConnectionProperties;
        this.hibernateProps = hibernateProps;
    }

    @Bean
    public DataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(databaseConnectionProperties.getUrl());
        dataSource.setUsername(databaseConnectionProperties.getUsername());
        dataSource.setPassword(databaseConnectionProperties.getPassword());
        dataSource.setDriverClassName(databaseConnectionProperties.getDriverClassName());
        return dataSource;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource, HibernateProperties hibernateProperties) {
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPackagesToScan("org.example.entity");
        entityManagerFactoryBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());

        Properties properties = new Properties();
        properties.put("hibernate.show_sql", hibernateProps.isShowSql());
        properties.put("hibernate.hbm2ddl.auto", hibernateProps.getDdlAuto());
        properties.put("hibernate.jdbc.time_zone", hibernateProps.getJdbcTimeZone());
        properties.put("hibernate.physical_naming_strategy", hibernateProps.getPhysicalNamingStrategy());

        entityManagerFactoryBean.setJpaProperties(properties);
        return entityManagerFactoryBean;
    }

    @Bean
    public JpaTransactionManager transactionManager(LocalContainerEntityManagerFactoryBean emf) {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(emf.getObject());
        return transactionManager;
    }
}

