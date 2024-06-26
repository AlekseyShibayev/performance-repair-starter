package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.FirstInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface FirstInfoRepository extends JpaRepository<FirstInfo, Long>, JpaSpecificationExecutor<FirstInfo> {
}