package br.com.senai.usuariosmktplace;

import br.com.senai.usuariosmktplace.core.domain.Usuario;
import br.com.senai.usuariosmktplace.core.service.UsuarioService;

public class InitApp {

	public static void main(String[] args) {
		
		UsuarioService service = new UsuarioService();
		Usuario usu = new Usuario("murilo.vieira", "Murilo Vieira", "murilo123");
		Usuario ususu = service.inserir(usu);
			
			System.out.println(ususu.getLogin());
			System.out.println(ususu.getNomeCompleto());
			System.out.println(ususu.getSenha());
			//service.alterar(usu, "lilo");
	}
}
