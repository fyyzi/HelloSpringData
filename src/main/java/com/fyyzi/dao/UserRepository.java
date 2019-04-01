package com.fyyzi.dao;

import com.fyyzi.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author 息阳
 */
public interface UserRepository extends JpaRepository<User, Long> {

}
