package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

import com.company.app.configuration.SpringBootTest;
import com.company.app.infrastructure.jpa.entityfinder.model.CommonQuery;
import com.company.app.test_domain.entity.One;
import com.company.app.test_domain.entity.Two;
import com.company.app.test_domain.repository.OneRepository;
import com.company.app.test_domain.repository.TwoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;


class DynamicEntityGraphTest extends SpringBootTest {

    @Autowired
    private OneRepository oneRepository;
    @Autowired
    private TwoRepository twoRepository;

    @Test
    void test() {
        One one = prepareTestData();

        List<One> ones = entityFinder.findAllAsList(new CommonQuery<>(One.class)
                .setSpecification(idEq(one.getId()) )
            .with("twos"));

        Assertions.assertEquals(1, ones.size());
        Assertions.assertEquals(1, ones.get(0).getTwos().size());
    }

    public static Specification<One> idEq(Long id) {
        return (Root<One> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) ->
            criteriaBuilder.equal(root.get("id"), id);
    }

    private One prepareTestData() {
        return transactionTemplate.execute(status -> {
            One one = new One();
            oneRepository.save(one);

            Two two = new Two();
            twoRepository.save(two);

            two.setOne(one);
            twoRepository.save(two);

            one.getTwos().add(two);
            oneRepository.save(one);

            return one;
        });
    }

}