package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.First;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface FirstRepository extends JpaRepository<First, Long>, JpaSpecificationExecutor<First> {
}