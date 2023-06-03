package sd2223.trab2.servers.rest;

import jakarta.inject.Singleton;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import sd2223.trab2.api.Message;
import sd2223.trab2.kafka.KafkaPublisher;
import sd2223.trab2.kafka.KafkaSubscriber;
import sd2223.trab2.kafka.RecordProcessor;
import sd2223.trab2.kafka.sync.SyncPoint;
import utils.JSON;

import java.util.List;

@Singleton
public class RestFeedsRepResource extends RestFeedsPullResource implements RecordProcessor {
    static final String FROM_BEGINNING = "earliest";
    static final String TOPIC = "replicated_feeds";
    static final String KAFKA_BROKERS = "localhost:9092, kafka:9092";

    final KafkaPublisher sender;
    final KafkaSubscriber receiver;
    final SyncPoint<Long> sync;

    public RestFeedsRepResource() {
        this.sender = KafkaPublisher.createPublisher(KAFKA_BROKERS);
        this.receiver = KafkaSubscriber.createSubscriber(KAFKA_BROKERS, List.of(TOPIC), FROM_BEGINNING);
        this.receiver.start(false, this);
        this.sync = new SyncPoint<>();
    }

    @Override
    public void onReceive(ConsumerRecord<String, String> r) {
        String[] values = r.value().split("#");
        String[] keys = r.key().split("#");
        var operation = values[0];
        var value = values[1];
        var user = keys[0];
        var pwd = keys[1];
        var version = r.offset();

        System.out.printf("%s %s : processing: (%d, %s)\n",user, operation, version, r.value());

        if (operation.startsWith("postMessage")) {
            Message msg = JSON.decode(value, Message.class);
            long result = super.postMessage(user, pwd, msg);
            sync.setResult(version, result);
        } else if (operation.startsWith("subUser")) {
            super.subUser(user, value, pwd);
            sync.setResult(version, null);
        } else if (operation.startsWith("unsubscribeUser")) {
            super.unsubscribeUser(user, value, pwd);
            sync.setResult(version, null);
        } else if (operation.startsWith("removeFromPersonalFeed")) {
            super.removeFromPersonalFeed(user, Long.parseLong(value), pwd);
            sync.setResult(version, null);
        }
    }

    @Override
    public long postMessage(String user, String pwd, Message msg) {
        var id = super.postMessage(user, pwd, msg);
        msg.setId(id);
        var value = JSON.encode(msg);
        var operation = "postMessage#" + value;
        var key = user+"#"+pwd;
        var version = sender.publish(TOPIC, key, operation);
        var result = sync.waitForResult(version);
        //System.out.printf("Op: %s, version: %s, result: %s\n", operation, version, result);

        return result.intValue();
    }

    @Override
    public void subUser(String user, String userSub, String pwd) {
        var operation = "subUser#" + userSub;
        var key = user+"#"+pwd;
        var version = sender.publish(TOPIC, key, operation);
        sync.waitForResult(version);
    }

    @Override
    public void unsubscribeUser(String user, String userSub, String pwd) {
        var operation = "unsubscribeUser#" + userSub;
        var key = user+"#"+pwd;
        var version = sender.publish(TOPIC, key, operation);
        sync.waitForResult(version);
    }

    @Override
    public void removeFromPersonalFeed(String user, long mid, String pwd) {
        var operation = "removeFromPersonalFeed#" + mid;
        var key = user+"#"+pwd;
        var version = sender.publish(TOPIC, key, operation);
        sync.waitForResult(version);
    }


}
