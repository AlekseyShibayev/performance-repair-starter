package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.Second;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SecondRepository extends JpaRepository<Second, Long>, JpaSpecificationExecutor<Second> {
}