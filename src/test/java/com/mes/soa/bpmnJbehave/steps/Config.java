package com.mes.soa.bpmnJbehave.steps;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

@Configuration
public class Config 
{
	@Bean
    DataSource dataSource() 
	{
		DriverManagerDataSource driverManagerDataSource = new DriverManagerDataSource();
//		driverManagerDataSource.setUrl("jdbc:postgresql://mespostgresdb.ca3lm8sxl2ye.us-east-2.rds.amazonaws.com:5432/mes_pilot");
//      driverManagerDataSource.setUsername("mespostgres");
//      driverManagerDataSource.setPassword("MESPostgres091");
//      driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        
        
        driverManagerDataSource.setUrl("jdbc:postgresql://192.168.7.147:5432/mespilot");
        driverManagerDataSource.setUsername("postgres");
        driverManagerDataSource.setPassword("postgres");
        driverManagerDataSource.setDriverClassName("org.postgresql.Driver");
        return driverManagerDataSource;
    }
}
