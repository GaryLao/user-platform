package org.geektimes.projects.user.repository;

import org.geektimes.projects.user.domain.User;

import java.util.Collection;
import java.util.List;

/**
 * 用户存储仓库
 *
 * @since 1.0
 */
public interface UserRepository {

    boolean save(User user) throws Exception;

    boolean deleteById(Long userId);

    boolean update(User user);

    User getById(Long userId);

    //lzm add 2021-03-10 22:22:33
    User getByName(String userName);

    User getByNameAndPassword(String userName, String password);

    Collection<User> getAll();
}
