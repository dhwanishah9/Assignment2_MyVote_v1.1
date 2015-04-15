package myvote;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.stereotype.Repository;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@Repository
@EnableWebMvcSecurity
@RequestMapping(value = "/api/v1")
@RestController
public class VoteController extends WebSecurityConfigurerAdapter {

	@Autowired
	ModeratorRepo moderatorRepo;

	@Autowired
	PollRepo pollRepo;

	Moderator moderator;
	Polls poll;
	ArrayList<Moderator> moderatorList = new ArrayList<Moderator>();

	ArrayList<String> strlst = new ArrayList<String>();
	private static final AtomicLong randomId = new AtomicLong(432867);
	private SimpleDateFormat formater = new SimpleDateFormat(
			"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	int[] tempresult = new int[2];
	int[] result = new int[2];
	int[] finalresult = new int[2];
	String[] choice = new String[2];

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.httpBasic().and().csrf().disable().authorizeRequests()
				.antMatchers(HttpMethod.POST, "/api/v1/moderators").permitAll()
				.antMatchers("/api/v1/polls/**").permitAll()
				.antMatchers("/api/v1/moderators/**").fullyAuthenticated()
				.anyRequest().hasRole("USER");
	}

	@Autowired
	public void configureGlobal(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.inMemoryAuthentication().withUser("foo").password("bar")
				.roles("USER");
	}

	@RequestMapping(value = "/moderators", method = RequestMethod.POST)
	public ResponseEntity<Moderator> moderator(@Valid @RequestBody Moderator mod) {
		mod.setCreated_at(formater.format(new Date()));
		mod.setId((int) randomId.incrementAndGet());
		moderatorList.add(mod);
		moderatorRepo.save(mod);
		return new ResponseEntity<Moderator>(mod, HttpStatus.CREATED);

	}

	@RequestMapping(value = "/moderators/{id}", method = RequestMethod.GET)
	public ResponseEntity Viewmoderator(@PathVariable int id) {
		moderator = moderatorRepo.findById(id);
		if (moderator == null) {
			return new ResponseEntity("Moderator is not present for given id",HttpStatus.OK);
		}
		return new ResponseEntity<Moderator>(moderator, HttpStatus.OK);

	}

	@RequestMapping(value = "/moderators/{id}", method = RequestMethod.PUT)
	public ResponseEntity<Moderator> updatemoderator(
			@Valid @RequestBody Moderator mod, @PathVariable int id) {
		int identifier = 0;
		String email = mod.getEmail();
		String password = mod.getPassword();
		moderator = moderatorRepo.findById(id);
		moderator.setEmail(email);
		moderator.setPassword(password);
		moderatorRepo.save(moderator);
		return new ResponseEntity(moderator, HttpStatus.OK);
	}

	@RequestMapping(value = "/moderators/{moderator_id}/polls", method = RequestMethod.POST)
	public ResponseEntity createPoll(@Valid @RequestBody Polls poll,
			@PathVariable int moderator_id) {
		poll.setId(Integer.toString((int) randomId.incrementAndGet(), 36));
		pollRepo.save(poll);
		moderator = moderatorRepo.findById(moderator_id);
		strlst = moderator.getPollslist();
		strlst.add(poll.getId());
		moderator.setPollslist(strlst);
		moderatorRepo.save(moderator);
		Map<String, Object> pollMap = new LinkedHashMap<String, Object>();
		pollMap.put("id", poll.getId());
		pollMap.put("question", poll.getQuestion());
		pollMap.put("started_at", poll.getStarted_at());
		pollMap.put("expired_at", poll.getExpired_at());
		pollMap.put("choice", poll.getChoice());
		return new ResponseEntity(pollMap, HttpStatus.CREATED);
	}

	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.GET)
	public ResponseEntity viewPollsWithoughtResult(@PathVariable String poll_id) {
		Map<String, Object> pollMap = new LinkedHashMap<String, Object>();
		poll = pollRepo.findById(poll_id);
		if (poll == null) {
			return new ResponseEntity("Poll not present for the given id",HttpStatus.OK);
		}
		pollMap.put("id", poll.getId());
		pollMap.put("question", poll.getQuestion());
		pollMap.put("started_at", poll.getStarted_at());
		pollMap.put("expired_at", poll.getExpired_at());
		pollMap.put("choice", poll.getChoice());
		return new ResponseEntity(pollMap, HttpStatus.OK);
	}

	@RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.GET)
	public ResponseEntity viewPollWithResult(@PathVariable int moderator_id,
			@PathVariable String poll_id) {
		moderator = moderatorRepo.findById(moderator_id);
		strlst = moderator.getPollslist();
		for (int i = 0; i < strlst.size(); i++) {
			if (strlst.get(i).equals(poll_id)) {
				poll = pollRepo.findById(strlst.get(i));
				return new ResponseEntity(poll, HttpStatus.OK);
			}
		}
		return new ResponseEntity("View Poll Error", HttpStatus.OK);
	}

	@RequestMapping(value = "/moderators/{moderator_id}/polls", method = RequestMethod.GET)
	public ResponseEntity listAllPolls(@PathVariable int moderator_id) {
		ArrayList<Polls> stringlist1 = new ArrayList<Polls>();
		moderator = moderatorRepo.findById(moderator_id);
		strlst = moderator.getPollslist();
		for (int i = 0; i < strlst.size(); i++) {
			poll = pollRepo.findById(strlst.get(i));
			stringlist1.add(poll);
		}
		return new ResponseEntity(stringlist1, HttpStatus.OK);
	}

	@RequestMapping(value = "/moderators/{moderator_id}/polls/{poll_id}", method = RequestMethod.DELETE)
	public ResponseEntity deletePoll(@PathVariable int moderator_id,
			@PathVariable String poll_id) {
		poll = pollRepo.findById(poll_id);
		pollRepo.delete(poll);
		moderator = moderatorRepo.findById(moderator_id);
		strlst = moderator.getPollslist();
		for (int i = 0; i < strlst.size(); i++) {
			if (strlst.get(i).equals(poll_id)) {
				strlst.remove(i);
				moderator.setPollslist(strlst);
				moderatorRepo.save(moderator);
				return new ResponseEntity("Poll is deleted",
						HttpStatus.NO_CONTENT);
			}
		}
		return new ResponseEntity("Delete Poll Error",
				HttpStatus.OK);
	}

	@RequestMapping(value = "/polls/{poll_id}", method = RequestMethod.PUT)
	public ResponseEntity voteAPoll(@PathVariable String poll_id,
			@RequestParam(value = "choice") int choice_index) {
		poll = pollRepo.findById(poll_id);
		if (choice_index == 0) {
			tempresult = poll.getResult();
			tempresult[choice_index] = tempresult[choice_index] + 1;
			poll.setResult(tempresult);
			pollRepo.save(poll);
			return new ResponseEntity(poll, HttpStatus.NO_CONTENT);
		} else if (choice_index == 1) {
			tempresult = poll.getResult();
			tempresult[choice_index] = tempresult[choice_index] + 1;
			poll.setResult(tempresult);
			pollRepo.save(poll);
			return new ResponseEntity(HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity("Unable to vote", HttpStatus.NO_CONTENT);

	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	@ResponseBody
	public ResponseEntity handleBadInput(MethodArgumentNotValidException e) {
		String errors = "";
		for (FieldError obj : e.getBindingResult().getFieldErrors()) {
			errors += obj.getDefaultMessage();
		}
		return new ResponseEntity(errors, HttpStatus.BAD_REQUEST);
	}
}
