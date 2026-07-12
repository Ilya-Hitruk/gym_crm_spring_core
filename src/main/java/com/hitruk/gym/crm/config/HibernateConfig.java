package com.hitruk.gym.crm.config;

import lombok.RequiredArgsConstructor;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
@Import(DatasourceConfig.class)
@RequiredArgsConstructor
public class HibernateConfig {
    private static final String HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String HBM2DDL = "hibernate.hbm2ddl.auto";
    private static final String SHOW_SQL = "hibernate.show_sql";
    private static final String FORMAT_SQL = "hibernate.format_sql";

    @Bean
    public LocalSessionFactoryBean sessionFactory(
            DataSource dataSource,
            @Value("${hibernate.dialect}") String dialect,
            @Value("${hibernate.hbm2ddl.auto}") String ddlAuto,
            @Value("${hibernate.show_sql:false}") boolean showSql,
            @Value("${hibernate.format_sql:false}") boolean formatSql
    ) {
        LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();

        Properties properties = new Properties();
        properties.put(HIBERNATE_DIALECT, dialect);
        properties.put(HBM2DDL, ddlAuto);
        properties.put(SHOW_SQL, showSql);
        properties.put(FORMAT_SQL, formatSql);

        sessionFactory.setDataSource(dataSource);
        sessionFactory.setPackagesToScan(
                "com.hitruk.gym.crm.model.entity"
        );
        sessionFactory.setHibernateProperties(properties);

        return sessionFactory;
    }

    @Bean
    public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
        return new HibernateTransactionManager(sessionFactory);
    }
}
