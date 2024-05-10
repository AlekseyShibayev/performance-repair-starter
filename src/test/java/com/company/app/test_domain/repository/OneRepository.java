package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.One;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface OneRepository extends JpaRepository<One, Long>, JpaSpecificationExecutor<One> {
}