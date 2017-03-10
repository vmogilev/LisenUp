package com.lisenup.web.portal;

import javax.transaction.Transactional;

import org.springframework.data.repository.Repository;

@Transactional
public interface UserGroupRepository extends Repository<UserGroup, Long> {

	public UserGroup findByUgaSlug(String groupSlug);

}
