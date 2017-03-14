package com.lisenup.web.portal.models;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.CrudRepository;

@Transactional
public interface GroupTopicRepository extends CrudRepository<GroupTopic, Long> {
	
	public List<GroupTopic> findByUgaIdOrderByGtaOrderAsc(long ugaId);
	public List<GroupTopic> findByUgaIdAndGtaActiveOrderByGtaOrderAsc(long ugaId, boolean active);
	public GroupTopic findByGtaIdAndGtaActive(long gqaId, boolean active);

}
