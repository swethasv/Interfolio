package com.intf.DBConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import com.intf.model.SOQVO;

@Configuration
@PropertySource("classpath:application-${spring.profiles.active}.properties")
@Component
public class INTFDBConfig {

	@Autowired
	private Environment env;

	public Connection createConnection() throws Exception {
		String driverClassName = env.getProperty("spring.datasource.driver-class-name");
		String url = env.getProperty("spring.datasource.url");
		String userName = env.getProperty("spring.datasource.username");
		String password = env.getProperty("spring.datasource.password");
		try {

			Class.forName(driverClassName);
			return java.sql.DriverManager.getConnection(url, userName, password);
		} catch (SQLException sqlException) {
			System.out.println(sqlException.getMessage());

			throw sqlException;
		}
	}

	public static void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
		try {
			if (resultSet != null) {
				// resultSet.clearWarnings();
				resultSet.close();
			}
			if (statement != null) {
				// statement.clearWarnings();
				statement.close();
			}
			if (connection != null) {
				if (!connection.getAutoCommit()) {

					connection.setAutoCommit(true);
				}

				connection.close();
			}

		} catch (Exception ex) {
			System.out
					.println("Error in closing the JDBC Connection " + "or ResultSet or Statement:" + ex.getMessage());

		}
	}
}
