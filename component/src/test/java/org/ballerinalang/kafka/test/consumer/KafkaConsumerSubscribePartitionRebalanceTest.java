/*
 * Copyright (c) 2018, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  WSO2 Inc. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
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

package org.ballerinalang.kafka.test.consumer;

import io.debezium.kafka.KafkaCluster;
import io.debezium.util.Testing;
import org.ballerinalang.launcher.util.BCompileUtil;
import org.ballerinalang.launcher.util.BRunUtil;
import org.ballerinalang.launcher.util.CompileResult;
import org.ballerinalang.model.values.BInteger;
import org.ballerinalang.model.values.BMap;
import org.ballerinalang.model.values.BValue;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.ballerinalang.kafka.test.utils.Constants.KAFKA_BROKER_PORT;
import static org.ballerinalang.kafka.test.utils.Constants.ZOOKEEPER_PORT_1;

@Test(singleThreaded = true)
public class KafkaConsumerSubscribePartitionRebalanceTest {
    private CompileResult result;
    private static File dataDir;
    private static KafkaCluster kafkaCluster;

    @BeforeClass
    public void setup() throws IOException {
        Properties prop = new Properties();
        kafkaCluster = kafkaCluster().deleteDataPriorToStartup(true)
                .deleteDataUponShutdown(true).withKafkaConfiguration(prop).addBrokers(1).startup();
    }

    @Test(description = "Test functionality of getAvailableTopics() function")
    public void testKafkaConsumerSubscribeToPattern () {
        result = BCompileUtil.compileAndSetup("consumer/kafka_consumer_subscribe_with_partition_rebalance.bal");
        BValue[] kafkaConsumerValue = BRunUtil.invoke(result, "funcKafkaGetKafkaConsumer");
        Assert.assertEquals(kafkaConsumerValue.length, 1);
        Assert.assertTrue(kafkaConsumerValue[0] instanceof BMap);
        // adding kafka endpoint as the input parameter
        BValue[] kafkaConsumer = new BValue[]{kafkaConsumerValue[0]};
        try {
            await().atMost(5000, TimeUnit.MILLISECONDS).until(() -> {
                BValue[] returnBValuesRevoked = BRunUtil
                        .invokeStateful(result, "funcKafkaGetRebalanceInvokedPartitionsCount", kafkaConsumer);
                Assert.assertEquals(returnBValuesRevoked.length, 1);
                Assert.assertTrue(returnBValuesRevoked[0] instanceof BInteger);
                long revokedPartitionCount = ((BInteger) returnBValuesRevoked[0]).intValue();

                BValue[] returnBValuesAssigned = BRunUtil
                        .invokeStateful(result, "funcKafkaGetRebalanceAssignedPartitionsCount", kafkaConsumer);
                Assert.assertEquals(returnBValuesAssigned.length, 1);
                Assert.assertTrue(returnBValuesAssigned[0] instanceof BInteger);
                long assignedPartitionCount = ((BInteger) returnBValuesAssigned[0]).intValue();
                return (revokedPartitionCount == -1 && assignedPartitionCount == -1);
            });
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }

        kafkaCluster.createTopic("rebalance-topic-1", 3, 1);
        kafkaCluster.createTopic("rebalance-topic-2", 2, 1);

        try {
            await().atMost(10000, TimeUnit.MILLISECONDS).until(() -> {
                BRunUtil.invokeStateful(result, "funcKafkaTestSubscribeWithPartitionRebalance", kafkaConsumer);
                BValue[] returnBValuesRevoked = BRunUtil
                        .invokeStateful(result, "funcKafkaGetRebalanceInvokedPartitionsCount", kafkaConsumer);
                Assert.assertEquals(returnBValuesRevoked.length, 1);
                Assert.assertTrue(returnBValuesRevoked[0] instanceof BInteger);
                long revokedPartitionCount = ((BInteger) returnBValuesRevoked[0]).intValue();

                BValue[] returnBValuesAssigned = BRunUtil
                        .invokeStateful(result, "funcKafkaGetRebalanceAssignedPartitionsCount", kafkaConsumer);
                Assert.assertEquals(returnBValuesAssigned.length, 1);
                Assert.assertTrue(returnBValuesAssigned[0] instanceof BInteger);
                long assignedPartitionCount = ((BInteger) returnBValuesAssigned[0]).intValue();

                return (revokedPartitionCount == 0 && assignedPartitionCount == 5);
            });
        } catch (Throwable e) {
            Assert.fail(e.getMessage());
        }
    }

    @AfterClass
    public void tearDown() {
        if (kafkaCluster != null) {
            kafkaCluster.shutdown();
            kafkaCluster = null;
            boolean delete = dataDir.delete();
            // If files are still locked and a test fails: delete on exit to allow subsequent test execution
            if (!delete) {
                dataDir.deleteOnExit();
            }
        }
    }

    private static KafkaCluster kafkaCluster() {
        if (kafkaCluster != null) {
            throw new IllegalStateException();
        }
        dataDir = Testing.Files.createTestingDirectory("cluster-kafka-consumer-subscribe-to-pattern-test");
        kafkaCluster = new KafkaCluster().usingDirectory(dataDir).withPorts(ZOOKEEPER_PORT_1, KAFKA_BROKER_PORT);
        return kafkaCluster;
    }
}
