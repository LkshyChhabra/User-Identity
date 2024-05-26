package personal.userIdentity;

import java.util.Properties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserIdentityApplication {

	public static void main(String[] args) {
		Properties properties = System.getProperties();
		properties.setProperty("cluster.name", "gwplutus");
		System.setProperties(properties);
		SpringApplication.run(UserIdentityApplication.class, args);
	}

}
