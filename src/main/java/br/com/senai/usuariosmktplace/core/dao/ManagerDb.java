package br.com.senai.usuariosmktplace.core.dao;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class ManagerDb {
	
	private static ManagerDb manager;
	
	private Connection conexao;
	
	private ManagerDb() {		
		try {
			InputStream is = new FileInputStream("src/main/resources/application.properties");
			Properties config = new Properties();
			config.load(is);
			
			Class.forName(config.getProperty("database-jdbc-driver")).getDeclaredConstructor().newInstance();
			this.conexao = DriverManager.getConnection(
					config.getProperty("database-url"), 
					config.getProperty("database-user"), config.getProperty("database-password"));
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro de conex�o "
					+ "com o banco de dados. Motivo: " + e.getMessage());
		}
	}
	
	public Connection getConexao() {
		return conexao;
	}
	
	public void configurarAutocommitDa(Connection conexao, boolean isHabilitado) {
		try {
			if (conexao != null) {
				conexao.setAutoCommit(isHabilitado);
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro na ativa��o do "
					+ "autocommit. Motivo: " + e.getMessage());
		}
	}
	
	public void fechar(PreparedStatement ps) {		
		try {
			if (ps != null) {
				ps.close();
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro no fechamento do "
					+ "PreparedStatement. Motivo: " + e.getMessage());
		}
	}
	
	public void fechar(ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		}catch (Exception e) {
			throw new RuntimeException("Ocorreu um erro no fechamento do "
					+ "ResultSet. Motivo: " + e.getMessage());
		}
	}	
	
	public static ManagerDb getInstance() {
		
		if (manager == null) {
			manager = new ManagerDb();
		}
		
		return manager;
		
	}
	
	
	
}
