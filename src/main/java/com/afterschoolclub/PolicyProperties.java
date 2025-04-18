package com.afterschoolclub;

import org.springframework.boot.context.properties.ConfigurationProperties;
import com.afterschoolclub.data.Policy;
import java.util.List;



@ConfigurationProperties("asc")

public record PolicyProperties(List<Policy> policies) {
}
	
	
