package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.Second;
import com.company.app.test_domain.entity.Third;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ThirdRepository extends JpaRepository<Third, Long>, JpaSpecificationExecutor<Third> {
}