package smolka.smsapi.config;

import liquibase.integration.spring.SpringLiquibase;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.Properties;

@Configuration
@EnableJpaRepositories(basePackages = {"smolka.smsapi.repository"},
        entityManagerFactoryRef = "emF",
        transactionManagerRef = "tm")
@EntityScan(basePackages = {"smolka.smsapi.model"})
@EnableTransactionManagement
@EnableWebMvc
public class MainConfiguration {

    private static final String MODEL_PACKAGE = "smolka.smsapi.model";

    @Value(value = "${sms.api.datasource.driver-class-name}")
    private String driverClassName;

    @Value(value = "${sms.api.datasource.url}")
    private String databaseUrl;

    @Value(value = "${sms.api.datasource.username}")
    private String databaseUsername;

    @Value(value = "${sms.api.datasource.password}")
    private String databasePassword;

    @Value(value = "${sms.api.data-base-char-set}")
    private String databaseCharSet;

    @Value(value = "${sms.api.data-base-character-encoding}")
    private String databaseCharacterEncoding;

    @Value(value = "${sms.api.hibernate.dialect}")
    private String hibernateDialect;

    @Value(value = "${sms.api.liquibase.change-log}")
    private String liquibaseChangelogPath;

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(driverClassName);
        dataSource.setUrl(databaseUrl);
        dataSource.setUsername(databaseUsername);
        dataSource.setPassword(databasePassword);
        return dataSource;
    }

    @Bean(name = "emF")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
        LocalContainerEntityManagerFactoryBean localContainerEntityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        localContainerEntityManagerFactoryBean.setDataSource(dataSource());
        localContainerEntityManagerFactoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        localContainerEntityManagerFactoryBean.setPackagesToScan(MODEL_PACKAGE);
        localContainerEntityManagerFactoryBean.setJpaProperties(hibernateProperties());
        return localContainerEntityManagerFactoryBean;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter hibernateJpaVendorAdapter = new HibernateJpaVendorAdapter();
        hibernateJpaVendorAdapter.setShowSql(false);
        hibernateJpaVendorAdapter.setGenerateDdl(false);
        return hibernateJpaVendorAdapter;
    }

    @Bean
    public Properties hibernateProperties() {
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.charSet", databaseCharSet);
        hibernateProperties.setProperty("hibernate.connection.characterEncoding", databaseCharacterEncoding);
        hibernateProperties.setProperty("hibernate.connection.useUnicode", "true");
        hibernateProperties.setProperty("hibernate.dialect", hibernateDialect);
        return hibernateProperties;
    }

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setChangeLog(liquibaseChangelogPath);
        liquibase.setDataSource(dataSource());
        return liquibase;
    }

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();
        mapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setFieldMatchingEnabled(true)
                .setSkipNullEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);
        return mapper;
    }

    @Bean(name = "tm")
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager manager = new JpaTransactionManager();
        manager.setEntityManagerFactory(entityManagerFactory().getObject());
        return manager;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public Validator validator() {
        ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
        return validatorFactory.usingContext().getValidator();
    }
}
