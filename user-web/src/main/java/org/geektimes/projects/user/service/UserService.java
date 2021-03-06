package org.geektimes.projects.user.service;

import org.geektimes.projects.user.domain.User;
import org.geektimes.projects.user.sql.LocalTransactional;

/**
 * 用户服务
 */
public interface UserService {

    /**
     * 注册用户
     *
     * @param user 用户对象
     * @return 成功返回<code>true</code>
     */
    @LocalTransactional
    boolean register(User user) throws Exception;

    /**
     * 注销用户
     *
     * @param user 用户对象
     * @return 成功返回<code>true</code>
     */
    boolean deregister(User user);

    /**
     * 更新用户信息
     *
     * @param user 用户对象
     * @return
     */
    boolean update(User user);

    User queryUserById(Long id);

    //lzm add 2021-03-10 22:22:33
    User queryUserByName(String name);

    User queryUserByNameAndPassword(String name, String password);
}
