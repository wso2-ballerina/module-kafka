/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ballerinalang.net.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Constants for kafka.
 *
 * @since 0.8.0
 */
public class Constants {

    private Constants() {
    }

    public static final String PRODUCER_CONNECTOR_NAME = "KafkaProducerConnector";
    public static final String CONSUMER_CONNECTOR_NAME = "KafkaConsumerConnector";
    public static final String NATIVE_CONSUMER = "KafkaConsumer";
    public static final String NATIVE_PRODUCER = "KafkaProducer";
    public static final String KAFKA_NATIVE_PACKAGE = "ballerina.net.kafka";
    public static final String TOPIC_PARTITION_STRUCT_NAME = "TopicPartition";
    public static final String OFFSET_STRUCT_NAME = "Offset";

    public static final String CONSUMER_RECORD_STRUCT_NAME = "ConsumerRecord";
    public static final String CONSUMER_PARTITION_INFO_STRUCT_NAME = "PartitionInfo";
    public static final String CONSUMER_STRUCT_NAME = "KafkaConsumer";
    public static final String PRODUCER_STRUCT_NAME = "KafkaProducer";
    public static final String ANNOTATION_KAFKA_CONFIGURATION = "configuration";
    public static final String PROPERTIES_ARRAY = "properties";

    public static final String ALIAS_BOOTSTRAP_SERVERS = "bootstrapServers";
    public static final String ALIAS_GROUP_ID = "groupId";
    public static final String ALIAS_CONCURRENT_CONSUMERS = "concurrentConsumers";
    public static final String ALIAS_TOPICS = "topics";
    public static final String ALIAS_POLLING_TIMEOUT = "pollingTimeout";
    public static final String ALIAS_ENABLE_AUTO_COMMIT = "autoCommit";
    public static final String ALIAS_DECOUPLE_PROCESSING = "decoupleProcessing";

    public static final String DEFAULT_KEY_DESERIALIZER
            = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
    public static final String DEFAULT_VALUE_DESERIALIZER
            = "org.apache.kafka.common.serialization.ByteArrayDeserializer";
    public static final String PARAM_TRANSACTION_ID = "transactional.id";

    public static final String DEFAULT_KEY_SERIALIZER
            = "org.apache.kafka.common.serialization.ByteArraySerializer";
    public static final String DEFAULT_VALUE_SERIALIZER
            = "org.apache.kafka.common.serialization.ByteArraySerializer";

    private static Map<String, String> mappingParameters;

    static {
        mappingParameters = new HashMap<>();
        mappingParameters.put(ALIAS_BOOTSTRAP_SERVERS, ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG);
        mappingParameters.put(ALIAS_GROUP_ID, ConsumerConfig.GROUP_ID_CONFIG);
        mappingParameters.put(ALIAS_ENABLE_AUTO_COMMIT, ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG);
    }

    public static final Map<String, String> MAPPING_PARAMETERS = Collections.unmodifiableMap(mappingParameters);

}

