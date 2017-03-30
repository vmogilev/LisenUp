package com.lisenup.web.portal.models;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

@Transactional
public interface TopicFeedbackRepository extends CrudRepository<TopicFeedback, Long> {

	public List<TopicFeedback> findByGtaId(long gtaId);
	public List<TopicFeedback> findBySessIdAndGtaIdOrderByCreatedAtAsc(String sessId, long gtaId);
	public TopicFeedback findByTfaUuid(String tfaUuid);

	@Modifying
	@Query("update TopicFeedback tfa set tfa.uaId = ?1, tfa.modifiedBy = ?2, tfa.version = ?3 where tfa.tfaId = ?4")
	int setRealUserForFeedbackId(Long uaId, String modifiedBy, Integer version, Long tfaId);
}
