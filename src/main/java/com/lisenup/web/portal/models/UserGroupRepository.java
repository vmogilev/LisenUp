package com.lisenup.web.portal.models;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface UserGroupRepository extends CrudRepository<UserGroup, Long> {

	public UserGroup findByUaIdAndUgaSlug(long uaId, String groupSlug);

}
