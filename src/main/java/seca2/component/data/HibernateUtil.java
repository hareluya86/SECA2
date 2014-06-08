/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package seca2.component.data;

import java.io.Serializable;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 *
 * @author vincent.a.lee
 */
public class HibernateUtil implements Serializable {
    
    private SessionFactory sessionFactory;
    
    /**
     * Singleton factory method
     * @return 
     */
    public SessionFactory getSessionFactory(){
        if(sessionFactory == null){
            Configuration cfg = this.createJNDIConfig();
            cfg.configure();
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder ().applySettings(cfg.getProperties()).build();
            sessionFactory = cfg.buildSessionFactory(serviceRegistry);
        }
        return sessionFactory;
    }
    
    public Session getSession() throws org.hibernate.exception.JDBCConnectionException{
        
        Session session = getSessionFactory().openSession();
        
        return session;
    }
    
    private Configuration createFullConfig(){
        Configuration cfg = createPartialConfig();
        cfg.setProperty("hibernate.current_session_context_class", "thread");
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        cfg.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        cfg.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/seca2?rewriteBatchedStatements=true");
        cfg.setProperty("hibernate.connection.username","seca2");
        cfg.setProperty("hibernate.connection.password","seca2");
        cfg.setProperty("hibernate.c3p0.min_size","5");
        cfg.setProperty("hibernate.c3p0.max_size","20");
        cfg.setProperty("hibernate.c3p0.timeout","30");
        cfg.setProperty("hibernate.c3p0.max_statements","100");
        cfg.setProperty("hibernate.hbm2ddl.auto","update");
        cfg.setProperty("hibernate.archive.autodetection","class, hbm");
        cfg.setProperty("hibernate.show_sql","true");
        cfg.setProperty("hibernate.connection.autocommit","false");
        
        //cfg.setProperty("hibernate.session_factory_name","hi");
        
        return cfg;
    }
    
    private Configuration createPartialConfig(){
        Configuration cfg = new Configuration();
        return cfg;
    }
    
    private Configuration createJNDIConfig(){
        Configuration cfg = createPartialConfig();
        cfg.setProperty("hibernate.current_session_context_class", "thread");
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        cfg.setProperty("hibernate.connection.driver_class", "com.mysql.jdbc.Driver");
        cfg.setProperty("hibernate.connection.datasource", "jdbc/__default");
        //cfg.setProperty("hibernate.connection.username","seca2");
        //cfg.setProperty("hibernate.connection.password","seca2");
        cfg.setProperty("hibernate.c3p0.min_size","5");
        cfg.setProperty("hibernate.c3p0.max_size","20");
        cfg.setProperty("hibernate.c3p0.timeout","30");
        cfg.setProperty("hibernate.c3p0.max_statements","100");
        cfg.setProperty("hibernate.hbm2ddl.auto","update");
        cfg.setProperty("hibernate.archive.autodetection","class, hbm");
        cfg.setProperty("hibernate.show_sql","false");
        cfg.setProperty("hibernate.connection.autocommit","false");
        
        //cfg.setProperty("hibernate.session_factory_name","hi");
        
        return cfg;
    }
}
