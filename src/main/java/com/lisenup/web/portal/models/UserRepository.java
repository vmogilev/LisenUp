package com.lisenup.web.portal.models;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserRepository extends CrudRepository<User, Long> {

	public User findByUaUsername(String username);
	public User findByUaEmail(String email);
	
	@Modifying
	@Query("update User ua set ua.uaActive = ?1, ua.modifiedBy = ?2, ua.version = ?3 where ua.uaId = ?4")
	int setActiveForUserId(Boolean uaActive, String modifiedBy, Integer version, Long uaId);

	@Modifying
	@Query("update User ua set ua.uaName = ?1, ua.modifiedBy = ?2, ua.version = ?3 where ua.uaId = ?4")
	int setNameForUserId(String uaName, String modifiedBy, Integer version, Long uaId);

}
