package io.ankburov.springsessionignite;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "to.do")
public class IgniteSessionConfigurationProperties {
}
