package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.Two;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface TwoRepository extends JpaRepository<Two, Long>, JpaSpecificationExecutor<Two> {
}