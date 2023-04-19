package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcCustomerDao implements CustomerDao {

    private final JdbcTemplate jdbcTemplate;
    public JdbcCustomerDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Customer getCustomer(int customerId) {
        Customer customer = null;
        String sql = "SELECT customer_id, name, street_address1, street_address2, city, state, zip_code " +
                     "FROM customer " +
                     "WHERE customer_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, customerId);
        if (results.next()) {
            customer = mapRowToCustomer(results);
        }
        return customer;
    }

    @Override
    public List<Customer> getCustomers() {
        List<Customer> customers = new ArrayList<>();
        String sql = "SELECT customer_id, name, street_address1, street_address2, city, state, zip_code " +
                "FROM customer " +
                "ORDER BY customer_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Customer customer = mapRowToCustomer(results);
            customers.add(customer);
        }
        return customers;
    }

    @Override
    public Customer createCustomer(Customer newCustomer) {
        String sql = "INSERT INTO customer (name, street_address1, street_address2, city, state, zip_code) " +
                     "VALUES (?, ?, ?, ?, ?, ?) RETURNING customer_id;";
        int newId = jdbcTemplate.queryForObject(sql, int.class, newCustomer.getName(), newCustomer.getStreetAddress1(),
                newCustomer.getStreetAddress2(), newCustomer.getCity(), newCustomer.getState(), newCustomer.getZipCode());
        return getCustomer(newId);
    }

    @Override
    public void updateCustomer(Customer updatedCustomer) {
        String sql = "UPDATE customer " +
                     "SET name = ?, street_address1 = ?, street_address2 = ?, city = ?, state = ?, zip_code = ? " +
                     "WHERE customer_id = ?";
        jdbcTemplate.update(sql, updatedCustomer.getName(), updatedCustomer.getStreetAddress1(),
                updatedCustomer.getStreetAddress2(), updatedCustomer.getCity(), updatedCustomer.getState(), updatedCustomer.getZipCode(),
                updatedCustomer.getCustomerId());
    }

    @Override
    public void deleteCustomer(int customerId) {
        String sql = "DELETE FROM customer WHERE customer_id = ?";
        jdbcTemplate.update(sql, customerId);
    }

    private Customer mapRowToCustomer(SqlRowSet results) {
        // Create a model object and fill in its properties with values from the result set.
        Customer customer = new Customer();
        customer.setCustomerId(results.getInt("customer_id"));
        customer.setName(results.getString("name"));
        customer.setStreetAddress1(results.getString("street_address1"));
        customer.setStreetAddress2(results.getString("street_address2"));
        customer.setCity(results.getString("city"));
        customer.setState(results.getString("state"));
        customer.setZipCode(results.getString("zip_code"));
        return customer;
    }
}
