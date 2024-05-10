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
import com.company.app.test_domain.entity.Third_;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class DynamicEntityGraphTest extends SpringBootTest {

    @Test
    void test() {
        First first = prepareTestData();

        DynamicEntityGraph graph = new DynamicEntityGraph()
            .with(First_.FIRST_INFO)
            .with(First_.SECONDS, Second_.SECOND_INFO)
            .with(First_.SECONDS, Second_.THIRDS, Third_.THIRD_INFO);

        First result = entityExtractor.load(First.class, first.getId(), graph);

        Assertions.assertNotNull(result.getFirstInfo());
        Assertions.assertEquals("first", result.getFirstInfo().getDescription());
        Assertions.assertEquals(1, result.getSeconds().size());

        Assertions.assertNotNull(result.getSeconds().get(0).getSecondInfo());
        Assertions.assertEquals("second", result.getSeconds().get(0).getSecondInfo().getDescription());
        Assertions.assertEquals(1, result.getSeconds().get(0).getThirds().size());

        Assertions.assertNotNull(result.getSeconds().get(0).getThirds().get(0).getThirdInfo());
        Assertions.assertEquals("third", result.getSeconds().get(0).getThirds().get(0).getThirdInfo().getDescription());
    }

    private First prepareTestData() {
        return transactionTemplate.execute(status -> {
            First first = new First();
            firstRepository.save(first);

            FirstInfo firstInfo = new FirstInfo().setDescription("first");
            firstInfoRepository.save(firstInfo);

            Second second = new Second();
            secondRepository.save(second);

            SecondInfo secondInfo = new SecondInfo().setDescription("second");
            secondInfoRepository.save(secondInfo);

            Third third = new Third();
            thirdRepository.save(third);

            ThirdInfo thirdInfo = new ThirdInfo().setDescription("third");
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