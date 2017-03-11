package com.lisenup.web.portal.models;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.repository.Repository;

@Transactional
public interface GroupQuestionRepository extends Repository<GroupQuestion, Long> {
	
	public List<GroupQuestion> findByUgaId(long ugaId);
	public List<GroupQuestion> findByUgaIdAndGqaActive(long ugaId, boolean active);
	public GroupQuestion findByGqaIdAndGqaActive(long gqaId, boolean active);

}
