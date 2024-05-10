package com.company.app.infrastructure.jpa.entityfinder.model.dynamic_entity_graph;

import com.company.app.configuration.SpringBootTest;
import com.company.app.test_domain.entity.First;
import com.company.app.test_domain.entity.FirstInfo;
import com.company.app.test_domain.entity.First_;
import com.company.app.test_domain.entity.Second;
import com.company.app.test_domain.entity.SecondInfo;
import com.company.app.test_domain.entity.Second_;
import com.company.app.test_domain.entity.Third;
import com.company.app.test_domain.entity.ThirdInfo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DynamicEntityGraphTest extends SpringBootTest {

    @Test
    void test() {
        First first = prepareTestData();

        DynamicEntityGraph graph = new DynamicEntityGraph()
            .with(First_.SECONDS)
            .with(First_.SECONDS, Second_.SECOND_INFO)
            .with(First_.SECONDS, Second_.THIRDS);

        First result = entityExtractor.load(First.class, first.getId(), graph);

        Assertions.assertNotNull(result.getFirstInfo());
        Assertions.assertEquals(1, result.getSeconds().size());

        Assertions.assertNotNull( result.getSeconds().get(0).getSecondInfo());
        Assertions.assertEquals(1, result.getSeconds().get(0).getThirds().size());

        Assertions.assertNotNull(result.getSeconds().get(0).getThirds().get(0).getThirdInfo());
    }

    private First prepareTestData() {
        return transactionTemplate.execute(status -> {
            First first = new First();
            firstRepository.save(first);

            FirstInfo firstInfo = new FirstInfo();
            firstInfoRepository.save(firstInfo);

            Second second = new Second();
            secondRepository.save(second);

            SecondInfo secondInfo = new SecondInfo();
            secondInfoRepository.save(secondInfo);

            Third third = new Third();
            thirdRepository.save(third);

            ThirdInfo thirdInfo = new ThirdInfo();
            thirdInfoRepository.save(thirdInfo);

            thirdInfo.setThird(third);

            third.setSecond(second);
            third.setThirdInfo(thirdInfo);

            secondInfo.setSecond(second);

            second.setFirst(first);
            second.setSecondInfo(secondInfo);
            second.getThirds().add(third);

            firstInfo.setFirst(first);

            first.setFirstInfo(firstInfo);
            first.getSeconds().add(second);

            return first;
        });
    }

}