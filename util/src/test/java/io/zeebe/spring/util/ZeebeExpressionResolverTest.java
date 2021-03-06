package io.zeebe.spring.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@TestPropertySource(
    properties = {
      "zeebe.topic.name=foo",
      "zeebe.topic.partitions",
      "zeebe.isSomething=true",
    })
@ContextConfiguration(classes = ZeebeExpressionResolver.class)
public class ZeebeExpressionResolverTest {

  @Autowired private ZeebeExpressionResolver resolver;

  @Value("${zeebe.isSomething}")
  private boolean isSomeThing;

  @Value("${zeebe.isSomethingNotSet:true}")
  private boolean isSomethingNotSet;

  @Value("${zeebe.defaultEmpty:}")
  private String empty;

  @Test
  public void resolveTopic() throws Exception {
    final String topic = resolver.resolve("${zeebe.topic.name}");
    assertThat(topic).isEqualTo("foo");
  }

  @Test
  public void resolveIsSomething() throws Exception {
    assertThat(isSomeThing).isTrue();
  }

  @Test
  public void resolveDefaultValueForIsSomethingNotSet() throws Exception {
    assertThat(isSomethingNotSet).isTrue();
  }

  @Test
  public void useValueIfNoExpression() throws Exception {
    final String normalString = resolver.resolve("normalString");
    assertThat(normalString).isEqualTo("normalString");
  }

  @Test
  public void defaultsToEmptyString() throws Exception {
    assertThat(empty).isNotNull().isEmpty();
  }
}
