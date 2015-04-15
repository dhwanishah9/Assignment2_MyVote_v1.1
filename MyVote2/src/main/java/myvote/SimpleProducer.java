package myvote;

import java.util.Properties;

import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;

public class SimpleProducer {
    public static Producer<Integer, String> producer;
    private final Properties properties = new Properties();

    public SimpleProducer() {
        properties.put("metadata.broker.list", "54.149.84.25:9092");
        properties.put("serializer.class", "kafka.serializer.StringEncoder");
        properties.put("request.required.acks", "1");
        producer = new Producer<>(new ProducerConfig(properties));
    }
}
