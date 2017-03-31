package com.lisenup.web.portal.models;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AnonSessionRepository extends CrudRepository<AnonSession, String> {
	
	@Modifying
	@Query("update AnonSession x set x.fullName = ?1 where x.sessId = ?2")
	int setFullNameForSessId(String fullName, String sessId);


}
