package se.callista.cadec.eda.shipping.conf;

import java.util.HashMap;
import java.util.Map;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import se.callista.cadec.eda.customer.domain.Customer;
import se.callista.cadec.eda.order.domain.Order;

@Configuration
@EnableKafka
public class KafkaConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Value("${spring.kafka.consumer.group-id}")
  private String groupId;

  @Bean
  public Map<String, Object> consumerConfigs() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "se.callista.cadec.eda.customer.domain,se.callista.cadec.eda.order.domain");
    props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);

    return props;
  }

  @Bean
  public ConsumerFactory<String, Order> orderConsumerFactory() {
    return new DefaultKafkaConsumerFactory<String, Order>(consumerConfigs());
  }

  @Bean()
  public ConcurrentKafkaListenerContainerFactory<String, Order> orderListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Order> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(orderConsumerFactory());

    return factory;
  }

  @Bean
  public ConsumerFactory<String, Customer> customerConsumerFactory() {
    return new DefaultKafkaConsumerFactory<String, Customer>(consumerConfigs());
  }

  @Bean()
  public ConcurrentKafkaListenerContainerFactory<String, Customer> customerListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, Customer> factory =
        new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(customerConsumerFactory());

    return factory;
  }

}
