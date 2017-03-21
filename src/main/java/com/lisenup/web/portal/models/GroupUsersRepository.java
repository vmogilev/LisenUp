package com.lisenup.web.portal.models;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface GroupUsersRepository extends CrudRepository<GroupUsers, Long> {
	
	public GroupUsers findByUgaIdAndUaId(long ugaId, long uaId);

	@Modifying
	@Query("update GroupUsers gua set gua.guaActive = ?1, modifiedBy = ?2 where gua.guaId = ?3")
	int setActiveForSubId(Boolean guaActive, String modifiedBy, Long guaId);

}
