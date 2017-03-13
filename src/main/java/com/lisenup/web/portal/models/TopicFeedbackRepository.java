package com.lisenup.web.portal.models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


public interface TopicFeedbackRepository extends CrudRepository<TopicFeedback, Long> {

	public List<TopicFeedback> findByGtaId(long gtaId);
}
