package com.lisenup.web.portal.models;

import javax.transaction.Transactional;

import org.springframework.data.repository.Repository;

@Transactional
public interface UserGroupRepository extends Repository<UserGroup, Long> {

	public UserGroup findByUaIdAndUgaSlug(long uaId, String groupSlug);

}
