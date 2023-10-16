package com.c88.member.common.config;

import com.c88.game.adapter.event.BetRecord;
import com.c88.member.event.RechargeAwardRecordModel;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;

import static com.c88.common.core.constant.TopicConstants.*;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

//    @Value("${spring.kafka.bootstrap-servers}")
//    private String bootstrapAddress;
//
//    public ConsumerFactory<String, BetRecord> consumerFactory(String groupId) {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        return new DefaultKafkaConsumerFactory<>(props,
//                new StringDeserializer(),
//                new JsonDeserializer<>(BetRecord.class));
//    }
//
//    public ConcurrentKafkaListenerContainerFactory<String, BetRecord> kafkaListenerContainerFactory(String groupId) {
//        ConcurrentKafkaListenerContainerFactory<String, BetRecord> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(consumerFactory(groupId));
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//        return factory;
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, BetRecord> validBetKafkaListenerContainerFactory() {
//        return kafkaListenerContainerFactory(VALID_BET);
//    }
//
//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, BetRecord> vipValidBetKafkaListenerContainerFactory() {
//        return kafkaListenerContainerFactory(VIP_VALID_BET);
//    }

    // @Bean
    // public KafkaAdmin kafkaAdmin() {
    //     Map<String, Object> configs = new HashMap<>();
    //     configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
    //     return new KafkaAdmin(configs);
    // }

    @Bean
    public NewTopic topic1() {
        return new NewTopic(RECHARGE_AWARD_RECORD, 3, (short) 3);
    }

//    @Bean
//    public ConcurrentKafkaListenerContainerFactory<String, RechargeAwardRecordModel> rechargeAwardRecordKafkaListenerContainerFactory() {
//        Map<String, Object> props = new HashMap<>();
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
//        props.put(ConsumerConfig.GROUP_ID_CONFIG, RECHARGE_AWARD_RECORD);
//        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, 600000);
//        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
//        DefaultKafkaConsumerFactory<String, RechargeAwardRecordModel> stringBetRecordDefaultKafkaConsumerFactory =
//                new DefaultKafkaConsumerFactory<>(props,
//                new StringDeserializer(),
//                new JsonDeserializer<>(RechargeAwardRecordModel.class));
//
//        ConcurrentKafkaListenerContainerFactory<String, RechargeAwardRecordModel> factory = new ConcurrentKafkaListenerContainerFactory<>();
//        factory.setConsumerFactory(stringBetRecordDefaultKafkaConsumerFactory);
//        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
//
//        return factory;
//    }

}
