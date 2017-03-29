package com.lisenup.web.portal.models;

import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface EmailLogRepository extends CrudRepository<EmailLog, Long> {

}
