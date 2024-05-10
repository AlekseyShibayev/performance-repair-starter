package com.company.app.test_domain.repository;

import com.company.app.test_domain.entity.ThirdInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface ThirdInfoRepository extends JpaRepository<ThirdInfo, Long>, JpaSpecificationExecutor<ThirdInfo> {
}