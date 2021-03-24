package org.geektimes.projects.user.repository;

import org.geektimes.context.ClassicComponentContext;
//import org.geektimes.web.mvc.context.ComponentContext;
import org.geektimes.web.mvc.function.ThrowableFunction;
import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.sql.DBConnectionManager;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.apache.commons.lang.ClassUtils.wrapperToPrimitive;

public class DatabaseUserRepository implements UserRepository {

    private static Logger logger = Logger.getLogger(DatabaseUserRepository.class.getName());

    @Resource(name = "bean/EntityManager")
    private EntityManager entityManager;

    /**
     * 通用处理方式
     */
    //private static Consumer<Throwable> COMMON_EXCEPTION_HANDLER = e -> logger.log(Level.SEVERE, e.getMessage());
    private static final Consumer<Throwable> COMMON_EXCEPTION_HANDLER = Throwable::printStackTrace;

    public static final String INSERT_USER_DML_SQL =
            "INSERT INTO users(name,password,email,phoneNumber) VALUES " +
                    "(?,?,?,?)";

    public static final String QUERY_ALL_USERS_DML_SQL = "SELECT id,name,password,email,phoneNumber FROM users";

    private DBConnectionManager dbConnectionManager;

    public DatabaseUserRepository() {
        this.dbConnectionManager = ClassicComponentContext.getInstance().getComponent("bean/DBConnectionManager");
    }

    @PostConstruct
    public void init() {
        this.dbConnectionManager = ClassicComponentContext.getInstance().getComponent("bean/DBConnectionManager");
    }

    private Connection getConnection() {
        //if (this.dbConnectionManager == null){
        //    this.dbConnectionManager = ClassicComponentContext.getInstance().getComponent("bean/DBConnectionManager");
        //}

        return dbConnectionManager.getConnection();
    }

    @Override
    public boolean save(User user) throws Exception {
        // before process
        EntityTransaction transaction = entityManager.getTransaction();
        transaction.begin();
        try {
            // 主调用
            entityManager.persist(user);

            // 调用其他方法方法
            update(user); // 涉及事务
            // register 方法和 update 方法存在于同一线程
            // register 方法属于 Outer 事务（逻辑）
            // update 方法属于 Inner 事务（逻辑）
            // Case 1 : 两个方法均涉及事务（并且传播行为和隔离级别相同）
            // 两者共享一个物理事务，但存在两个逻辑事务
            // 利用 ThreadLocal 管理一个物理事务（Connection）

            // rollback 情况 1 : update 方法（Inner 事务），它无法主动去调用 rollback 方法
            // 设置 rollback only 状态，Inner TX(rollback only)，说明 update 方法可能存在执行异常或者触发了数据库约束
            // 当 Outer TX 接收到 Inner TX 状态，它来执行 rollback
            // A -> B -> C -> D -> E 方法调用链条
            // A (B,C,D,E) 内联这些方法，合成大方法
            // 关于物理事务是哪个方法创建
            // 其他调用链路事务传播行为是一致时，都是逻辑事务

            // Case 2: register 方法是 PROPAGATION_REQUIRED（事务创建者），update 方法 PROPAGATION_REQUIRES_NEW
            // 这种情况 update 方法也是事务创建者
            // update 方法 rollback-only 状态不会影响 Outer TX，Outer TX 和 Inner TX 是两个物理事务

            // Case 3: register 方法是 PROPAGATION_REQUIRED（事务创建者），update 方法 PROPAGATION_NESTED
            // 这种情况 update 方法同样共享了 register 方法物理事务，并且通过 Savepoint 来实现局部提交和回滚

            // after process
            transaction.commit();
        }catch (Exception e) {
            transaction.rollback();

            throw new Exception(e);
            //e.printStackTrace();
        }

        return true;
    }

    @Override
    public boolean deleteById(Long userId) {
        return false;
    }

    @Override
    public boolean update(User user) {
        return false;
    }

    @Override
    public User getById(Long userId) {
        return null;
    }

    //lzm add 2021-03-10 22:22:33
    @Override
    public User getByName(String userName) {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users WHERE name=?",
                resultSet -> {
                    // TODO
                    User user = new User();
                    if (resultSet.next()) {
                        user.setName(resultSet.getString("name"));
                    }
                    return user;
                }, COMMON_EXCEPTION_HANDLER, userName);
    }

    @Override
    public User getByNameAndPassword(String userName, String password) {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users WHERE name=? and password=?",
                resultSet -> {
                    // TODO
                    return new User();
                }, COMMON_EXCEPTION_HANDLER, userName, password);
    }

    @Override
    public Collection<User> getAll() {
        return executeQuery("SELECT id,name,password,email,phoneNumber FROM users", resultSet -> {
            // BeanInfo -> IntrospectionException
            BeanInfo userBeanInfo = Introspector.getBeanInfo(User.class, Object.class);
            List<User> users = new ArrayList<>();
            while (resultSet.next()) { // 如果存在并且游标滚动 // SQLException
                User user = new User();
                for (PropertyDescriptor propertyDescriptor : userBeanInfo.getPropertyDescriptors()) {
                    String fieldName = propertyDescriptor.getName();
                    Class fieldType = propertyDescriptor.getPropertyType();
                    String methodName = resultSetMethodMappings.get(fieldType);
                    // 可能存在映射关系（不过此处是相等的）
                    String columnLabel = mapColumnLabel(fieldName);
                    Method resultSetMethod = ResultSet.class.getMethod(methodName, String.class);
                    // 通过放射调用 getXXX(String) 方法
                    Object resultValue = resultSetMethod.invoke(resultSet, columnLabel);
                    // 获取 User 类 Setter方法
                    // PropertyDescriptor ReadMethod 等于 Getter 方法
                    // PropertyDescriptor WriteMethod 等于 Setter 方法
                    Method setterMethodFromUser = propertyDescriptor.getWriteMethod();
                    // 以 id 为例，  user.setId(resultSet.getLong("id"));
                    setterMethodFromUser.invoke(user, resultValue);
                }
            }
            return users;
        }, e -> {
            // 异常处理
        });
    }

    /**
     * @param sql
     * @param function
     * @param <T>
     * @return
     */
    protected <T> T executeQuery(String sql, ThrowableFunction<ResultSet, T> function,
                                 Consumer<Throwable> exceptionHandler, Object... args) {
        Connection connection = getConnection();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            for (int i = 0; i < args.length; i++) {
                Object arg = args[i];
                Class argType = arg.getClass();

                Class wrapperType = wrapperToPrimitive(argType);

                if (wrapperType == null) {
                    wrapperType = argType;
                }

                // Boolean -> boolean
                String methodName = preparedStatementMethodMappings.get(argType);
                Method method = PreparedStatement.class.getMethod(methodName, int.class, wrapperType);  //lzm modify 2021-03-11 05:39:53
                method.invoke(preparedStatement, i + 1, args[i]);
            }
            ResultSet resultSet = preparedStatement.executeQuery();
            // 返回一个 POJO List -> ResultSet -> POJO List
            // ResultSet -> T
            return function.apply(resultSet);
        } catch (Throwable e) {
            exceptionHandler.accept(e);
        }
        return null;
    }


    private static String mapColumnLabel(String fieldName) {
        return fieldName;
    }

    /**
     * 数据类型与 ResultSet 方法名映射
     */
    static Map<Class, String> resultSetMethodMappings = new HashMap<>();

    static Map<Class, String> preparedStatementMethodMappings = new HashMap<>();

    static {
        resultSetMethodMappings.put(Long.class, "getLong");
        resultSetMethodMappings.put(String.class, "getString");

        preparedStatementMethodMappings.put(Long.class, "setLong"); // long
        preparedStatementMethodMappings.put(String.class, "setString"); //


    }
}
