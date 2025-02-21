package com.afterschoolclub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SessionBeanConfiguration {
    private SessionBean sessionScopeBean;

    @Autowired
    public SessionBeanConfiguration(SessionBean sessionScopeBean) {
        this.sessionScopeBean = sessionScopeBean;
    }
	
}
