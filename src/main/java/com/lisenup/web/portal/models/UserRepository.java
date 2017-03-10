package com.lisenup.web.portal.models;

import javax.transaction.Transactional;

import org.springframework.data.repository.Repository;

@Transactional
public interface UserRepository extends Repository<User, Long> {

	public User findByUaUsername(String username);
}
