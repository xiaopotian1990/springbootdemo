package com.xiaopotian.demo.Filters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.util.Map;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(
        entityManagerFactoryRef = "entityManagerFactoryTest",//实体管理引用
        transactionManagerRef = "transactionManagerTest",//事务管理引用
        basePackages = {"com.xiaopotian.demo.service.test"}//设置test数据源所应用到的包
)
public class TestDataSourceConfig {
    /**
     * 注入test数据源
     */
    @Autowired
    @Qualifier("testDataSource")
    private DataSource testDataSource;
    /**
     * 注入jpa配置实体
     */
    @Autowired
    private JpaProperties jpaProperties;

    /**
     * 配置EntityManager实体
     *
     * @param builder
     * @return
     */
    @Primary
    @Bean(name = "entityManagerTest")
    public EntityManager entityManager(EntityManagerFactoryBuilder builder) {
        return entityManagerFactoryTest(builder).getObject().createEntityManager();
    }

    /**
     * 配置EntityManager工厂实体
     *
     * @param builder
     * @return
     */
    @Primary
    @Bean(name = "entityManagerFactoryTest")
    public LocalContainerEntityManagerFactoryBean entityManagerFactoryTest(EntityManagerFactoryBuilder builder) {
        return builder
                .dataSource(testDataSource)
                .properties(getProperties(testDataSource))
                .packages(new String[]{"com.xiaopotian.demo.domain"})
                .persistenceUnit("testPersistenceUnit")
                .build();
    }

    /**
     * 获取JPA配置信息
     *
     * @param dataSource
     * @return
     */
    private Map<String, String> getProperties(DataSource dataSource) {
        return jpaProperties.getHibernateProperties(dataSource);
    }

    /**
     * 配置事务
     *
     * @param builder
     * @return
     */
    @Primary
    @Bean(name = "transactionManagerTest")
    public PlatformTransactionManager transactionManagerTest(EntityManagerFactoryBuilder builder) {
        return new JpaTransactionManager(entityManagerFactoryTest(builder).getObject());
    }
}
