package com.example.relationaldataaccess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SpringBootApplication
public class RelationalDataAccessApplication implements CommandLineRunner {

	private static final Logger log= LoggerFactory.getLogger(RelationalDataAccessApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(RelationalDataAccessApplication.class, args);
	}

	@Autowired
	JdbcTemplate jdbcTemplate;

	@Override
	public void run(String...strings) throws Exception{
	log.info("creating tables");

	jdbcTemplate.execute("DROP Table customers IF EXISTS");

	jdbcTemplate.execute("create TABLE customers(id SERIAL, first_name VARCHAR(255), last_name VARCHAR(255))");

		List<Object[]> splitNames = Stream.of("Peter park", "Harry potter", "Monika Geller", "Jim Halpert")
				.map(s -> s.split(" ")).collect(Collectors.toList());
		splitNames.forEach(name -> log.info("Inserting customer record for {} {}",name[0],name[1]));

		jdbcTemplate.batchUpdate("INSERT into customers(first_name , last_name) VALUES(?,?)",splitNames);

		log.info("Querying for customer records where first_name = 'Monika':");

		jdbcTemplate.query("SELECT id, first_name, last_name FROM customers WHERE first_name = ? ",new Object[] {"Monika"},
				(rs,rowNUm) -> new Customer(rs.getLong("id"),rs.getString("first_name")
						,rs.getString("last_name"))).forEach(customer -> log.info(customer.toString()));
	}

}
