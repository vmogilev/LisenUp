package com.lisenup.web.portal.models;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

	public UserGroup findByUaIdAndUgaSlug(long uaId, String groupSlug);
	public List<UserGroup> findByUaId(long uaId);
	public List<UserGroup> findByUaIdAndUgaPublic(long uaId, boolean ugaPublic);

}
