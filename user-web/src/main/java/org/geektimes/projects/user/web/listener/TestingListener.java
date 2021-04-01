package org.geektimes.projects.user.web.listener;

import org.geektimes.context.ClassicComponentContext;
//import org.geektimes.web.mvc.context.ComponentContext;
import org.geektimes.function.ThrowableAction;
import org.geektimes.projects.user.management.UserManager;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.jms.*;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.lang.management.ManagementFactory;
import java.util.Set;
import java.util.logging.Logger;

/**
 * 测试用途
 */
@Deprecated
public class TestingListener implements ServletContextListener {

    private Logger logger = Logger.getLogger(this.getClass().getName());
    private User user;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        //ComponentContext context = ComponentContext.getInstance();
        ClassicComponentContext context = ClassicComponentContext.getInstance();
        DBConnectionManager dbConnectionManager = context.getComponent("bean/DBConnectionManager");
        dbConnectionManager.getConnection();
        testPropertyFromServletContext(sce.getServletContext());
        testPropertyFromJNDI(context);
        testUser(dbConnectionManager.getEntityManager());
        logger.info("所有的 JNDI 组件名称：[");
        context.getComponentNames().forEach(logger::info);
        logger.info("]");

        //lzm add 2021-03-16 05:58:20
        if (testUserMXBean(this.user)) {
            logger.info("开启UserMXBean 成功");
        }

        ConnectionFactory connectionFactory = context.getComponent("jms/activemq-factory");
        testJms(connectionFactory);
    }

    //从web.xml获取配置信息
    private void testPropertyFromServletContext(ServletContext servletContext) {
        String propertyName = "application.name";
        logger.info("ServletContext Property[" + propertyName + "] : "
                + servletContext.getInitParameter(propertyName));
    }

    //从context.xml获取配置信息
    private void testPropertyFromJNDI(ClassicComponentContext context) {
        String propertyName = "maxValue";
        logger.info("JNDI Property[" + propertyName + "] : "
                + context.lookupComponent(propertyName));
    }

    //测试 EntityManager
    private void testUser(EntityManager entityManager) {
        this.user = new User();
        this.user.setName("小马哥");
        this.user.setPassword("*******");
        this.user.setEmail("mercyblitz@gmail.com");
        this.user.setPhoneNumber("abcdefg");

        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        Set<ConstraintViolation<User>> violations = validator.validate(this.user);
        if (violations != null) {
            violations.forEach(c -> {
                System.out.println(c.getMessage());
            });
        }else {
            EntityTransaction transaction = entityManager.getTransaction();
            transaction.begin();
            entityManager.persist(this.user);
            transaction.commit();
            System.out.println(entityManager.find(User.class, user.getId()));
        }
    }

    private boolean testUserMXBean(User user) {
        try {
            this.user.initMBeanServer();
            //// 获取平台 MBean Server
            //MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
            //// 为 UserMXBean 定义 ObjectName
            //ObjectName objectName = new ObjectName("org.geektimes.projects.user.management:type=User");
            //// 创建 UserMBean 实例
            //mBeanServer.registerMBean(createUserMBean(user), objectName);

            return true;
        }catch (Exception e){
            logger.warning("UserMXBean 初始化出错" + e.getMessage());
            return false;
        }
    }

    private static Object createUserMBean(User user) throws Exception {
        return new UserManager(user);
    }

    private void testJms(ConnectionFactory connectionFactory) {
        ThrowableAction.execute(() -> {
//            testMessageProducer(connectionFactory);
            testMessageConsumer(connectionFactory);
        });
    }

    private void testMessageProducer(ConnectionFactory connectionFactory) throws JMSException {
        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageProducer from the Session to the Topic or Queue
        MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);

        // Create a messages
        String text = "Hello world! From: " + Thread.currentThread().getName() + " : " + this.hashCode();
        TextMessage message = session.createTextMessage(text);

        // Tell the producer to send the message
        producer.send(message);
        System.out.printf("[Thread : %s] Sent message : %s\n", Thread.currentThread().getName(), message.getText());

        // Clean up
        session.close();
        connection.close();

    }

    private void testMessageConsumer(ConnectionFactory connectionFactory) throws JMSException {

        // Create a Connection
        Connection connection = connectionFactory.createConnection();
        connection.start();

        // Create a Session
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Create the destination (Topic or Queue)
        Destination destination = session.createQueue("TEST.FOO");

        // Create a MessageConsumer from the Session to the Topic or Queue
        MessageConsumer consumer = session.createConsumer(destination);

        consumer.setMessageListener(m -> {
            TextMessage tm = (TextMessage) m;
            try {
                System.out.printf("[Thread : %s] Received : %s\n", Thread.currentThread().getName(), tm.getText());
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });

        // Clean up
        // session.close();
        // connection.close();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }

}
