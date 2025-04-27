package com.afterschoolclub;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class to ensure only have one instance of sessionBean per logged on user 
 */
@Configuration
public class SessionBeanConfiguration {
    private SessionBean sessionScopeBean;

    
//	static Logger logger = LoggerFactory.getLogger(SessionBeanConfiguration.class);

    /**
     * @param sessionScopeBean
     */
    @Autowired
    public SessionBeanConfiguration(SessionBean sessionScopeBean) {
        this.sessionScopeBean = sessionScopeBean;
        this.sessionScopeBean.getClass(); // Only inserted to avoid not used warning     
    }
	
}
