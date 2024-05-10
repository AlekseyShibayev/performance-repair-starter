package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.SecondInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SecondInfoRepository extends JpaRepository<SecondInfo, Long>, JpaSpecificationExecutor<SecondInfo> {
}