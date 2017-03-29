package com.lisenup.web.portal.models;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface AnonFeedbackRepository extends CrudRepository<AnonFeedback, Long> {
	
	public List<AnonFeedback> findByUgaIdAndSessId(long ugaId, String sessId);

}
