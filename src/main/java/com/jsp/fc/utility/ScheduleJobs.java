package com.jsp.fc.utility;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.jsp.fc.entity.User;
import com.jsp.fc.repository.UserRepository;

@Component
public class ScheduleJobs {
	@Autowired
    private UserRepository userRepo;

    @Scheduled(fixedDelay =  60 * 60 * 1000) 
    public void test() {
    	deleteUnverifiedUsers();
    }
    public void deleteUnverifiedUsers() {
        
        for (User user : userRepo.findAll()) {
        	
           if(Boolean.FALSE.equals(user.isEmailVerified())) {
        	   userRepo.delete(user);
           }
        }
    }

}
