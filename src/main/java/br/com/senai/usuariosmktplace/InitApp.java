package br.com.senai.usuariosmktplace;

import br.com.senai.usuariosmktplace.core.domain.Usuario;
import br.com.senai.usuariosmktplace.core.service.UsuarioService;

public class InitApp {

	public static void main(String[] args) {
		
		UsuarioService service = new UsuarioService();
		Usuario us = service.inserir("Murilo Vieira", "murilo123");
		System.out.println(us);
	}
}
