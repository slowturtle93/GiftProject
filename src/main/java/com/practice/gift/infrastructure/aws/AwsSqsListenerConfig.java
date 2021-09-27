package com.practice.gift.infrastructure.aws;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.aws.messaging.config.QueueMessageHandlerFactory;
import org.springframework.cloud.aws.messaging.config.SimpleMessageListenerContainerFactory;
import org.springframework.cloud.aws.messaging.listener.QueueMessageHandler;
import org.springframework.cloud.aws.messaging.listener.SimpleMessageListenerContainer;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import java.util.Collections;


/**
 * AWS SQS config 설정
 */
@Slf4j
@Component
public class AwsSqsListenerConfig {

    /**
     * 메세지 리스너 생성
     *
     * @param amazonSQSAsync
     * @return
     */
    @Bean
    public SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory(AmazonSQSAsync amazonSQSAsync){
        SimpleMessageListenerContainerFactory factory = new SimpleMessageListenerContainerFactory();
        factory.setAmazonSqs(amazonSQSAsync); // 메시징(SQS) API 와 상호 작용하기 위해 컨테이너에서 사용할 AmazonSQSAsync 설정
        factory.setAutoStartup(true);         // 해당 컨테이너를 자동으로 시작할지 여부 설정
        return factory;
    }

    /**
     * connection factory 에서 리스너 컨테이너 생성
     *
     * @param simpleMessageListenerContainerFactory
     * @param queueMessageHandler
     * @param messageThreadPoolTaskExecutor
     * @return
     */
    @Bean
    public SimpleMessageListenerContainer simpleMessageListenerContainer(
            SimpleMessageListenerContainerFactory simpleMessageListenerContainerFactory,
            QueueMessageHandler queueMessageHandler,
            ThreadPoolTaskExecutor messageThreadPoolTaskExecutor
    ){
        SimpleMessageListenerContainer container = simpleMessageListenerContainerFactory.createSimpleMessageListenerContainer();
        container.setMessageHandler(queueMessageHandler);         // 메세지 핸들러 설정
        container.setTaskExecutor(messageThreadPoolTaskExecutor); // 컨테이너에 대한 task executor 설정
        return container;
    }

    /**
     * QueueMessageHandlerFactory 및 Jasckson (JSON 메시지 직렬화에 사용)에게
     * 엄격한 콘텐츠 유형 일치 없이 메시지를 직렬화하도록 지시
     *
     * @ConditionalOnMissingBean
     * 스프링 부트 프로젝트 상에서 동명의 스프링 빈이 정의되었을 시에는 쓰지 않고 그 스프링 빈을 쓰며
     * 만약 없을 시에는 자동 등록한 빈을 쓰게 끔 유도하는 용도 사용
     *
     * @param amazonSQSAsync
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(QueueMessageHandlerFactory.class)
    public QueueMessageHandlerFactory queueMessageHandlerFactory(AmazonSQSAsync amazonSQSAsync){
        QueueMessageHandlerFactory factory = new QueueMessageHandlerFactory();

        // sendToMessagingTemplate 이  null 인 경우 새 QueueMessagingTemplate 을 생성하는데 사용할 AmazonSQS 클라이언트를 설정
        factory.setAmazonSqs(amazonSQSAsync);

        // Jackson 2.x를 사용하여 JSON 에서 메시지를 변환하는 메시지 변환기
        // 객체를 BytesMessage 에 매핑하거나 targetType 이 MessageType.TEXT 로 설정된 경우 TextMessage 에 매핑
        MappingJackson2MessageConverter messageConverter = new MappingJackson2MessageConverter();
        messageConverter.setStrictContentTypeMatch(false);
        factory.setArgumentResolvers(Collections.singletonList(new PayloadMethodArgumentResolver(messageConverter)));
        return factory;
    }

    /**
     * QueueMessageHandler 생성
     *
     * @param queueMessageHandlerFactory
     * @return
     */
    @Bean
    @ConditionalOnMissingBean(QueueMessageHandler.class)
    public QueueMessageHandler queueMessageHandler(QueueMessageHandlerFactory queueMessageHandlerFactory){
        return queueMessageHandlerFactory.createQueueMessageHandler();
    }

    /**
     * 스레드 풀을 사용하는 Executor
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskExecutor messageThreadPoolTaskExecutor(){
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setThreadNamePrefix("sqs_"); // 스레드 prefix 명 설정
        taskExecutor.setCorePoolSize(8);   // 기본 스레드 수
        taskExecutor.setMaxPoolSize(100);  // 최대 스레드 수
        taskExecutor.afterPropertiesSet(); // property 가 설정되고 난 뒤 실행
        return taskExecutor;
    }
}
