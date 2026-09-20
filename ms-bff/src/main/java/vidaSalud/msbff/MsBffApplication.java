package vidaSalud.msbff;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients 
public class MsBffApplication {

	public static void main(String[] args) {
		SpringApplication.run(MsBffApplication.class, args);
	}

}
