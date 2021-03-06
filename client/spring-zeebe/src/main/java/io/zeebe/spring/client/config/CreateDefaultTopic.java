package io.zeebe.spring.client.config;

import io.zeebe.client.ZeebeClient;
import io.zeebe.client.api.commands.PartitionInfo;
import java.util.function.Consumer;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;

/**
 * @deprecated zeebe now has its own way of defining a default property
 */
@Getter
@ToString
@Slf4j
@Deprecated
public class CreateDefaultTopic implements Consumer<ZeebeClient> {

  @Value("${zeebe.defaultTopic.name:0}")
  private String name;

  @Value("${zeebe.defaultTopic.partitions:-1}")
  private Integer partitions;

  @Value("${zeebe.defaultTopic.create:false}")
  private Boolean create;

  @Override
  public void accept(final ZeebeClient client) {
    if (create && !StringUtils.isEmpty(name) && partitions > 0 && !topicExists(client, name)) {
      client.newCreateTopicCommand().name(name).partitions(partitions).replicationFactor(1).send()
          .join();
      log.info("create topic: {}", this);
    }
  }

  private boolean topicExists(final ZeebeClient client, final String topicName) {
    return client
        .newTopologyRequest()
        .send()
        .join()
        .getBrokers()
        .stream()
        .flatMap(broker -> broker.getPartitions().stream().map(PartitionInfo::getTopicName))
        .anyMatch(topicName::equals);
  }
}
