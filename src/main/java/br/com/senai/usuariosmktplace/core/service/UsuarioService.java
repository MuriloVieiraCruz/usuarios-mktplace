package br.com.senai.usuariosmktplace.core.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import com.google.common.base.CharMatcher;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import br.com.senai.usuariosmktplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmktplace.core.dao.FactoryDao;
import br.com.senai.usuariosmktplace.core.domain.Usuario;

public class UsuarioService {
	
	private DaoUsuario dao;
	
	public UsuarioService() {
		this.dao = FactoryDao.getInstance().getDaoUsuario();
	}
	
	public Usuario inserir(String nomeCompleto, String senha) {
		this.validar(nomeCompleto ,senha);
		String login = gerarLoginPor(nomeCompleto);
		String senhaCriptografada = gerarHashDa(senha);
		Usuario usuario = new Usuario(login, nomeCompleto, senhaCriptografada);
		this.dao.inserir(usuario);
		Usuario usuarioSalvo = dao.buscarPor(login);
		return usuarioSalvo;
	}
	
	public void alterar(Usuario usuario, String novaSenha) throws IllegalAccessException {
		Usuario usuarioDoBanco = dao.buscarPor(usuario.getLogin()); 
		
		if (usuarioDoBanco != null) {
			
			String senhaAntigaHash = gerarHashDa(usuario.getSenha());
			String novaSenhaHash = gerarHashDa(novaSenha);
			if (senhaAntigaHash.equals(usuarioDoBanco.getSenha())) {
				usuario.setSenha(novaSenhaHash);
				dao.alterar(usuario);
			} else {
				throw new IllegalAccessException("A senha está errada!");
			}
			
		} else {
			throw new IllegalArgumentException("O login informado não existe no banco de dados!");
		}
	}
	
	private String removerAcentoDo(String nomeCompleto) {
		return Normalizer.normalize(nomeCompleto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
	
	private String gerarHashDa(String senha) {
		return new DigestUtils(MessageDigestAlgorithms.SHA3_256).digestAsHex(senha);
	}
	
	private List<String> fracionar(String nomeCompleto) {
		List<String> nomeFracionado = new ArrayList<String>();
		if (!Strings.isNullOrEmpty(nomeCompleto)) {
			
			nomeCompleto = nomeCompleto.trim();
			String[] partesDoNome = nomeCompleto.split(" ");
			
			for (String parte : partesDoNome) {
				boolean isNaoContemArtigo = !parte.equalsIgnoreCase("de") 
						&& !parte.equalsIgnoreCase("da")
						&& !parte.equalsIgnoreCase("do")
						&& !parte.equalsIgnoreCase("e")
						&& !parte.equalsIgnoreCase("dos")
						&& !parte.equalsIgnoreCase("das");	
				if (isNaoContemArtigo) {
					nomeFracionado.add(parte.toLowerCase());
				}
			}
				return nomeFracionado;
		} else {
			throw new IllegalArgumentException("O nome não pode ser nulo!");
		}
	}
	
	private String gerarLoginPor(String nomeCompleto) {
		nomeCompleto = removerAcentoDo(nomeCompleto);
		List<String> partesDoNome = fracionar(nomeCompleto);
		String loginGerado = null;
		Usuario usuarioEncontrado = null;
		if (!partesDoNome.isEmpty()) {
			for (int i = 1; i < partesDoNome.size(); i++) {
					loginGerado = partesDoNome.get(0) + "." + partesDoNome.get(i);
					usuarioEncontrado = dao.buscarPor(loginGerado);
					if (usuarioEncontrado == null) {
						return loginGerado;
					}
			}
			
			int proximoSequencial = 0;
			String loginDisponivel = null;
				while(usuarioEncontrado != null) {
					loginDisponivel = loginGerado + ++proximoSequencial;
					usuarioEncontrado = dao.buscarPor(loginDisponivel);
				}
				loginGerado = loginDisponivel;
				
				if (loginGerado.length() > 5 && loginGerado.length() < 50) {
					return loginGerado;
				} else {
					throw new IllegalArgumentException("Forneça um nome e sobrenome que fique entre 5 e 50 caracteres!");
				}
		}
		
		throw new IllegalArgumentException("erro");
			
	}
	
	private void validar(String senha) {
			boolean isSenhaInvalida = Strings.isNullOrEmpty(senha)
					|| senha.length() < 6
					|| senha.length() > 15
					|| senha.isBlank()
					|| senha == null;
					
			Preconditions.checkArgument(!isSenhaInvalida, 
					"A senha é obrigatória deve conter entre 6 e 15 caracteres!");
		
			boolean isContemLetra = CharMatcher.inRange('a', 'z').countIn(senha.toLowerCase()) > 0;
			boolean isContemNumero = CharMatcher.inRange('0', '9').countIn(senha) > 0;;
			boolean isCaractereInvalido = senha.contains(" ");
			
			Preconditions.checkArgument(isContemLetra && isContemNumero && !isCaractereInvalido, 
					"A senha deve conter letras e números");

		
	}
	
	private void validar(String nomeCompleto, String senha) {
		List<String> partesDoNome = fracionar(nomeCompleto);
		
		boolean isNomeCompleto = partesDoNome.size() > 1;
		
		boolean isNomeValido = !Strings.isNullOrEmpty(nomeCompleto)
				&& isNomeCompleto
				&& nomeCompleto.length() >= 5
				&& nomeCompleto.length() <= 120;
				
		Preconditions.checkArgument(isNomeValido, 
				"O nome é obrigatório e deve conter sobrenome e deve estar entre 5 e 50 caracteres!");
		
		this.validar(senha);
	}
	
	private String resetSenhaDo(String login) {
		if (login != null) {
			Usuario usuarioDoBanco = dao.buscarPor(login);
			if (usuarioDoBanco != null) {
				
				final String chars = "abcdefghijklmnopqrstuvwxyz123456789";
				
				Random random = new Random();
				StringBuilder builder = new StringBuilder();
				
				for (int i = 0; i < 6; i++) {
					int randomIndex = random.nextInt(chars.length());
					builder.append(chars.charAt(randomIndex));
				}
				String senhaResetada = builder.toString();
				
				usuarioDoBanco.setSenha(senhaResetada);
				dao.alterar(usuarioDoBanco);
				
				return senhaResetada;
			} else {
				throw new IllegalArgumentException("Usuario inexistente!");
			}	
		} else {
			throw new IllegalArgumentException("O login é obrigatório!");
		}
	}
}
