package br.com.senai.usuariosmktplace.core.service;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.codec.digest.MessageDigestAlgorithms;

import br.com.senai.usuariosmktplace.core.dao.DaoUsuario;
import br.com.senai.usuariosmktplace.core.dao.FactoryDao;
import br.com.senai.usuariosmktplace.core.domain.Usuario;

public class UsuarioService {
	
	private DaoUsuario dao;
	
	public UsuarioService() {
		this.dao = FactoryDao.getInstance().getDaoUsuario();
	}
	
	public String removerAcentoDo(String nomeCompleto) {
		return Normalizer.normalize(nomeCompleto, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
	}
	
	public String gerarHashDa(String senha) {
		return new DigestUtils(MessageDigestAlgorithms.MD5).digestAsHex(senha);
	}
	
	public Usuario inserir(Usuario usuario) {
		this.validar(usuario);
		String login = gerarLoginPor(usuario.getNomeCompleto());
		String senhaCriptografada = gerarHashDa(usuario.getSenha());
		usuario.setLogin(login);
		usuario.setSenha(senhaCriptografada);
		this.dao.inserir(usuario);
		return usuario;
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
	
	public List<String> fracionar(String nomeCompleto) {
		List<String> nomeFracionado = new ArrayList<String>();
		if (nomeCompleto != null && !nomeCompleto.isBlank()) {
			
			String[] partesDoNome = nomeCompleto.split(" ");
			
			String regex = "^[A-Z][a-z]+(\\s[A-Z][a-z]+)+$";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(nomeCompleto);
			
			if (matcher.matches()) {
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
				throw new IllegalArgumentException("O campo deve ser composto por nome e sobrenome!");
			}
		} else {
			throw new IllegalArgumentException("O nome não pode ser nulo!");
		}
	}
	
	public String gerarLoginPor(String nomeCompleto) {
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
	
	private void validar(Usuario usuario) {
		if (usuario != null) {
			boolean isNomeInvalido = usuario.getNomeCompleto().isBlank()
					|| usuario.getNomeCompleto().length() < 5
					|| usuario.getNomeCompleto().length() > 50;
					
			if (isNomeInvalido) {
				throw new IllegalArgumentException("O nome é obrigatório e deve conter entre 5 e 120 caracteres!");
			}
			
			boolean isSenhaInvalida = usuario.getSenha().length() < 6
					|| usuario.getSenha().length() > 15
					|| !usuario.getSenha().matches("^(?=.*[a-zA-Z])(?=.*\\d).+$");
					
			if (isSenhaInvalida) {
				throw new IllegalArgumentException("A senha deve conter entre 6 e 15 caracteres com letras e numeros!");
			}
		} else {
			throw new IllegalArgumentException("O usuario não pode ser nulo");
		}
	}
	
	public String resetSenhaDo(String login) {
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
