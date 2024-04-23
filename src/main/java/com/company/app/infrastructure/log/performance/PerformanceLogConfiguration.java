package com.company.app.infrastructure.log.performance;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import com.company.app.infrastructure.log.performance.aop.PerformanceLogAspect;
import com.company.app.infrastructure.log.performance.aop.PerformanceLogService;
import com.company.app.infrastructure.log.performance.interceptor.HibernateQueryInfoInterceptor;

/**
 * PerformanceLog module.
 * <p>
 * Wraps the method by proxy, which writes the method execution time to log.
 * Additional do size log, if return result type is Collection, or Slice (result of Pageable query).
 * Additional do hibernate query log.
 * <p>
 * For use need:
 * 1. Enable log debug by PerformanceLogAspect.class.
 * example: logging.level.com.company.app.infrastructure.log.performance.aop.PerformanceLogAspect: DEBUG
 * 2. Enable spring bean configuration.
 * example: log.performance.enable: true
 * 3. Enable hibernate interceptor.
 * example: spring.jpa.properties.hibernate.ejb.interceptor: com.company.app.infrastructure.log.performance.interceptor.HibernateQueryInfoInterceptor
 * 4. Specify package/class/method proxy.
 * example: log.performance.pointcut-expression: aspectJ pointcut expression
 * default - class or method marked by PerformanceLog annotation
 * <p>
 * Additional parameters:
 * 1. For limit log by method execution time use:
 * log.performance.do-not-write-to-log-methods-whose-execution-time-is-less-than-X-ms
 * default - 0 ms
 */
@Slf4j
@Configuration
@ConditionalOnProperty(prefix = "log.performance", name = "enable", havingValue = "true")
public class PerformanceLogConfiguration {

    @Value("${log.performance.pointcut-expression:@annotation(com.company.app.infrastructure.log.performance.aop.PerformanceLog) || @within(com.company.app.infrastructure.log.performance.aop.PerformanceLog)}")
    private String expression;

    @Bean
    public DefaultPointcutAdvisor defaultPointcutAdvisor(HibernateQueryInfoInterceptor hibernateQueryInfoInterceptor, PerformanceLogService performanceLogService) {
        var aspectJExpressionPointcut = new AspectJExpressionPointcut();
        aspectJExpressionPointcut.setExpression(expression);

        var performanceLogAspect = new PerformanceLogAspect(hibernateQueryInfoInterceptor, performanceLogService);

        var defaultPointcutAdvisor = new DefaultPointcutAdvisor(aspectJExpressionPointcut, performanceLogAspect);
        log.info("**********     created [{}] with pointcut expression [{}]     **********", DefaultPointcutAdvisor.class.getName(), expression);

        return defaultPointcutAdvisor;
    }

}