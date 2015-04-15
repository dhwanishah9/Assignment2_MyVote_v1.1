package myvote;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModeratorRepo extends MongoRepository<Moderator, String> {

	public Moderator findById(int id);
	public List<Moderator> findAll();

}