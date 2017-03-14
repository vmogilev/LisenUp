package com.lisenup.web.portal.models;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface GroupTopicRepository extends CrudRepository<GroupTopic, Long> {
	
	public List<GroupTopic> findByUgaId(long ugaId);
	public List<GroupTopic> findByUgaIdAndGtaActive(long ugaId, boolean active);
	public GroupTopic findByGtaIdAndGtaActive(long gqaId, boolean active);

}
