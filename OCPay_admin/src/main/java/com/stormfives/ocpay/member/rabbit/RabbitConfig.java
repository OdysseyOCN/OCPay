package com.stormfives.ocpay.member.rabbit;

import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * Created by fly on 2017/11/26.
 */
@Configuration
public class RabbitConfig {
    public static final String SEND_ADDRESS_MAIL = "send_address_mail";



    @Bean
    public Queue sendAddressMail() {
        return new Queue(RabbitConfig.SEND_ADDRESS_MAIL);
    }



    @Bean(name = "myConnectionFactory")
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setConcurrentConsumers(5);
        factory.setAcknowledgeMode(AcknowledgeMode.AUTO);
        factory.setPrefetchCount(2);
        return factory;
    }

}



