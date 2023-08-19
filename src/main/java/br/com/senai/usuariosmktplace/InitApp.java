package br.com.senai.usuariosmktplace;

import br.com.senai.usuariosmktplace.core.service.UsuarioService;

public class InitApp {

	public static void main(String[] args) {
		UsuarioService service = new UsuarioService();
		
		System.out.println(service.removerAcentoDo("José da Sílva"));
		System.out.println(service.fracionar("Murilo de Dolores da silva"));
		System.out.println(service.gerarLoginPor("Dick Vigarista"));
	}
}
