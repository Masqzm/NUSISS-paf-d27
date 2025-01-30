package paf.day27;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import paf.day27.repo.CommentsRepo;

@SpringBootApplication
public class Day27Application implements CommandLineRunner {
	@Autowired
	private CommentsRepo commentsRepo;

	public static void main(String[] args) {
		SpringApplication.run(Day27Application.class, args);
	}

	@Override
	public void run(String... args) {		
		// Day27 demo (index terms search)
		// -------------------------------
		// List<Document> results = commentsRepo.searchComments("love", "amazing", "excellent");

		// for(Document d : results)
		// System.out.println(">>> %s\n\n".formatted(d.toJson()));
	}
}
