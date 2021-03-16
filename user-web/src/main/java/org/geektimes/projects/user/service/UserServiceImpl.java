package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.repository.UserRepository;
import org.geektimes.projects.user.sql.DBConnectionManager;
import org.geektimes.projects.user.sql.LocalTransactional;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

public class UserServiceImpl implements UserService {
    //private final UserRepository userRepository;
    //public UserServiceImpl(UserRepository userRepository){
    //    this.userRepository = userRepository;
    //}

    @Resource(name = "bean/DatabaseUserRepository")
    private UserRepository userRepository;

    //@Resource(name = "bean/DBConnectionManager")
    //private DBConnectionManager dbConnectionManager;
    //
    //@Resource(name = "bean/EntityManager")
    //private EntityManager entityManager;
    //
    //@Resource(name = "bean/Validator")
    //private Validator validator;

    @Override
    // 默认需要事务
    @LocalTransactional
    public boolean register(User user) throws Exception {
        //移到org.geektimes.web.mvc.FrontControllerServlet.service内 //lzm modify 2021-03-16 18:40:55
        //Set<ConstraintViolation<User>> violations = validator.validate(user);
        //if (!violations.isEmpty()) {
        //    throw new ConstraintViolationException(violations);
        //}

        //移到DatabaseUserRepository内 //lzm modify 2021-03-16 16:43:07
        //// before process
        //EntityTransaction transaction = entityManager.getTransaction();
        //transaction.begin();
        //try {
        //    // 主调用
        //    //entityManager.persist(user);
        //
        //    // 调用其他方法方法
        //    update(user); // 涉及事务
        //    // register 方法和 update 方法存在于同一线程
        //    // register 方法属于 Outer 事务（逻辑）
        //    // update 方法属于 Inner 事务（逻辑）
        //    // Case 1 : 两个方法均涉及事务（并且传播行为和隔离级别相同）
        //    // 两者共享一个物理事务，但存在两个逻辑事务
        //    // 利用 ThreadLocal 管理一个物理事务（Connection）
        //
        //    // rollback 情况 1 : update 方法（Inner 事务），它无法主动去调用 rollback 方法
        //    // 设置 rollback only 状态，Inner TX(rollback only)，说明 update 方法可能存在执行异常或者触发了数据库约束
        //    // 当 Outer TX 接收到 Inner TX 状态，它来执行 rollback
        //    // A -> B -> C -> D -> E 方法调用链条
        //    // A (B,C,D,E) 内联这些方法，合成大方法
        //    // 关于物理事务是哪个方法创建
        //    // 其他调用链路事务传播行为是一致时，都是逻辑事务
        //
        //    // Case 2: register 方法是 PROPAGATION_REQUIRED（事务创建者），update 方法 PROPAGATION_REQUIRES_NEW
        //    // 这种情况 update 方法也是事务创建者
        //    // update 方法 rollback-only 状态不会影响 Outer TX，Outer TX 和 Inner TX 是两个物理事务
        //
        //    // Case 3: register 方法是 PROPAGATION_REQUIRED（事务创建者），update 方法 PROPAGATION_NESTED
        //    // 这种情况 update 方法同样共享了 register 方法物理事务，并且通过 Savepoint 来实现局部提交和回滚
        //
        //    // after process
        //    transaction.commit();
        //}catch (Exception e) {
        //    transaction.rollback();
        //
        //    throw new Exception(e);
        //    //e.printStackTrace();
        //}

        return userRepository.save(user);
    }

    @Override
    public boolean deregister(User user) {
        return false;
    }

    @Override
    @LocalTransactional
    public boolean update(User user) {
        return false;
    }

    @Override
    public User queryUserById(Long id) {
        return null;
    }

    //lzm add 2021-03-10 22:22:33
    @Override
    public User queryUserByName(String name) {
        return userRepository.getByName(name);
    }

    @Override
    public User queryUserByNameAndPassword(String name, String password) {
        return userRepository.getByNameAndPassword(name, password);
    }
}
