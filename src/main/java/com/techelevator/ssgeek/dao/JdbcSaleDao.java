package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Sale;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class JdbcSaleDao implements SaleDao {

    private final JdbcTemplate jdbcTemplate;
    public JdbcSaleDao (DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Sale getSale(int saleId){
        Sale sale = null;
        String sql = "SELECT sale_id, customer_id, sale_date, ship_date  " +
                "FROM sale " +
                "WHERE sale_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, saleId);
        if (results.next()) {
            sale = mapRowToSale(results);
        }
        return sale;

    }

    @Override
    public List<Sale> getSalesUnshipped() {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, customer_id, sale_date, ship_date  " +
                "FROM sale " +
                "WHERE ship_date = null " +
                "ORDER BY sale_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Sale sale = mapRowToSale(results);
            sales.add(sale);
        }
        return sales;
    }

    @Override
    public List<Sale> getSalesByCustomerId(int customerId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, customer_id, sale_date, ship_date  " +
                "FROM sale " +
                "WHERE customer_id = ? " +
                "ORDER BY sale_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Sale sale = mapRowToSale(results);
            sales.add(sale);
        }
        return sales;
    }

    @Override
    public List<Sale> getSalesByProductId(int productId) {
        List<Sale> sales = new ArrayList<>();
        String sql = "SELECT sale_id, customer_id, sale_date, ship_date  " +
                "FROM sale " +
                "WHERE product_id = ? " +
                "ORDER BY sale_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Sale sale = mapRowToSale(results);
            sales.add(sale);
        }
        return sales;
    }

    @Override
    public Sale createSale(Sale newSale) {
        String sql = "INSERT INTO sale (customer_id, sale_date, ship_date) " +
                "VALUES (?, ?, ?) RETURNING sale_id;";
        int newId = jdbcTemplate.queryForObject(sql, int.class, newSale.getCustomerId(), newSale.getSaleDate(),
                newSale.getShipDate());
        return getSale(newId);
    }

    @Override
    public void updateSale(Sale updatedSale) {
        String sql = "UPDATE sale " +
                "SET customer_id = ?, sale_date = ?, ship_date = ? " +
                "WHERE sale_id = ?";
        jdbcTemplate.update(sql, updatedSale.getCustomerId(), updatedSale.getSaleDate(), updatedSale.getShipDate());
    }

    @Override
    public void deleteSale(int saleId) {
        String sql = "DELETE FROM sale WHERE sale_id = ?";
        jdbcTemplate.update(sql, saleId);
    }

    private Sale mapRowToSale(SqlRowSet results) {
        Sale sale = new Sale();
        sale.setSaleId(results.getInt("sale_id"));
        sale.setCustomerId(results.getInt("customer_id"));
        sale.setSaleDate(results.getDate("sale_date"));
        sale.setShipDate(results.getDate("ship_date"));
        return sale;
    }
}
