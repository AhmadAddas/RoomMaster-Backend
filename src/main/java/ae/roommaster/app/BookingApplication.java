package ae.roommaster.app;

import ae.roommaster.app.auth.AuthenticationService;
import ae.roommaster.app.auth.RegisterRequest;
import ae.roommaster.app.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import static ae.roommaster.app.user.Role.ADMIN;
import static ae.roommaster.app.user.Role.MANAGER;

@SpringBootApplication
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class BookingApplication {

    public BookingApplication(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public static void main(String[] args) {
		SpringApplication.run(BookingApplication.class, args);
	}

	private final UserRepository userRepository;
	@Bean
	public CommandLineRunner commandLineRunner(
			AuthenticationService service
	) {
		return args -> {
			if (!(userRepository.existsByEmail("admin@roommaster.ae"))) {
			var admin = RegisterRequest.builder()
					.firstname("Admin")
					.lastname("Admin")
					.email("admin@roommaster.ae")
					.password("password")
					.role(ADMIN)
					.build();
			System.out.println("Admin token: " + service.register(admin).getAccessToken());
			} else {
				System.out.println("Admin user already exists.");
			}
			if (!(userRepository.existsByEmail("manager@roommaster.ae"))) {
			var manager = RegisterRequest.builder()
					.firstname("Manager")
					.lastname("Manager")
					.email("manager@roommaster.ae")
					.password("password")
					.role(MANAGER)
					.build();
			System.out.println("Manager token: " + service.register(manager).getAccessToken());
			} else {
				System.out.println("Manager user already exists.");
			}
		};
	}

}
