package com.lisenup.web.portal.models;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

	public User findByUaUsername(String username);
	public User findByUaEmail(String email);
}
