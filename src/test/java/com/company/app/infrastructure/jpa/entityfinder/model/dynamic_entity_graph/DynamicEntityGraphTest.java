package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import com.company.app.configuration.SpringBootTest;
import com.company.app.infrastructure.jpa.entityfinder.model.CommonQuery;
import com.company.app.test_domain.entity.First;
import com.company.app.test_domain.entity.First_;
import com.company.app.test_domain.entity.Second;
import com.company.app.test_domain.repository.FirstRepository;
import com.company.app.test_domain.repository.SecondRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;


class DynamicEntityGraphTest extends SpringBootTest {

    @Autowired
    private FirstRepository firstRepository;
    @Autowired
    private SecondRepository secondRepository;

    @Test
    void test() {
        First first = prepareTestData();

        List<First> result = entityFinder.findAllAsList(new CommonQuery<>(First.class)
            .setSpecification(idEq(first.getId()))
            .with(First_.SECONDS));

        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(1, result.get(0).getSeconds().size());
    }

    private static Specification<First> idEq(Long id) {
        return (Root<First> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
            criteriaBuilder.equal(root.get(First_.ID), id);
    }

    private First prepareTestData() {
        return transactionTemplate.execute(status -> {
            First first = new First();
            firstRepository.save(first);

            Second second = new Second();
            secondRepository.save(second);

            second.setFirst(first);
            secondRepository.save(second);

            first.getSeconds().add(second);
            firstRepository.save(first);

            return first;
        });
    }

}