package myvote;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PollRepo extends MongoRepository<Polls, String> {
	public Polls findById(String id);
	public List<Polls> findAll();

}
