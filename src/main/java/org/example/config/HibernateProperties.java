package org.example.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class HibernateProperties {

    @Value("${spring.jpa.hibernate.show_sql}")
    private boolean showSql;

    @Value("${spring.jpa.hibernate.ddl-auto}")
    private String ddlAuto;

    @Value("${spring.jpa.hibernate.jdbc.time_zone}")
    private String jdbcTimeZone;

    @Value("${spring.jpa.hibernate.naming.physical-strategy}")
    private String physicalNamingStrategy;
}
