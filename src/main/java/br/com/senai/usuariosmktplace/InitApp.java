package br.com.senai.usuariosmktplace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import br.com.senai.usuariosmktplace.core.service.UsuarioService;

@SpringBootApplication
public class InitApp {
	
	@Autowired
	private UsuarioService service;

	public static void main(String[] args) {
		
		SpringApplication.run(InitApp.class, args);
	}
	
	@Bean
	public CommandLineRunner commandLineRunner() {
		return args -> {
			System.out.println(service.buscarPor("alan.duarte").getNomeCompleto());
		};
	}
	
}
