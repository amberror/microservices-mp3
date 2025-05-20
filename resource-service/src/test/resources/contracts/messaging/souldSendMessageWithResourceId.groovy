package contracts.messaging

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "Should send a message with the resource ID and key to the Kafka topic"

    label "resource-created"
    input {
        triggeredBy("fileCreatedEvent(1)")
    }
    outputMessage {
        sentTo('resource-topic-test')
        headers {
            header("kafka_messageKey", 1L)
        }
        body(1L)
    }
}