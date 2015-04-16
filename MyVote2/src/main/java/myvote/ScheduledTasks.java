package myvote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kafka.producer.KeyedMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    
    @Autowired
	ModeratorRepo moderatorRepo;
    
    @Autowired
	PollRepo pollRepo;
    
    @Scheduled(fixedRate = 300000)
    public void checkExpiredPolls() {
		List<Moderator> moderators = new ArrayList<Moderator>();
		moderators = moderatorRepo.findAll();
		if(moderators!=null && !moderators.isEmpty()){
			for(Moderator moderator:moderators){
				for(String pollId : moderator.getPollslist()){
					try{
	    				Polls polls = pollRepo.findById(pollId);
	    				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    	        	Date expDate = format.parse(polls.expired_at);
	    	        	Calendar currentdate = Calendar.getInstance();
	    	        	Calendar expiredDate = Calendar.getInstance();
	    	        	currentdate.setTime(new Date());
	    	        	expiredDate.setTime(expDate);
	    					if(currentdate.after(expiredDate)){
	    						System.out.println("poll is expired");
	    						callProducer(polls,moderator.getEmail());
	    						System.out.println("producer call ended");
	    					}
	    				}catch(Exception e){
						e.printStackTrace();
					}	
				}
			}
		}
	}

	private void callProducer(Polls poll,String emailId) {
			int[] result = new int[2];
			result = poll.getResult();
			String[] choice = new String[2];
			choice = poll.getChoice();
		    new SimpleProducer();
	        String topic = "cmpe273-topic";
	        String msg = emailId+":010107266:Poll Result["+choice[0]+"="+result[0]+","+choice[1]+"="+result[1]+"]";
	        System.out.println(msg);
	        KeyedMessage<Integer, String> data = new KeyedMessage<>(topic, msg);
	        SimpleProducer.producer.send(data);
	        SimpleProducer.producer.close();
	} 
}
