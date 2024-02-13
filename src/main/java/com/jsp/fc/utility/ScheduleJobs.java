package com.jsp.fc.utility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.jsp.fc.serviceImpl.AuthServiceImpl;



@Component

public class ScheduleJobs {
	
	
	@Autowired
	private AuthServiceImpl authServiceImpl;

   

    @Scheduled(fixedDelay = 1000l*60*60*24)
    public void test() {
    authServiceImpl.deleteIfNotVerified();
    authServiceImpl.deleteExpiredTokens();
    }
    
}
