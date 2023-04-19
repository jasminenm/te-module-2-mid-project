package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Product;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcProductDao implements ProductDao {

    private final JdbcTemplate jdbcTemplate;
    public JdbcProductDao(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Product getProduct(int productId) {
        Product product = null;
        String sql = "SELECT product_id, name, description, price, image_name " +
                "FROM product " +
                "WHERE product_id = ?";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql, productId);
        if (results.next()) {
            product = mapRowToProduct(results);
        }
        return product;
    }

    @Override
    public List<Product> getProducts() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT product_id, name, description, price, image_name " +
                "FROM product " +
                "ORDER BY product_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Product product = mapRowToProduct(results);
            products.add(product);
        }
        return products;
    }

    @Override
    public List<Product> getProductsWithNoSales() {
        List<Product> products = new ArrayList<>();
        String sql = "SELECT * FROM product " +
                "LEFT OUTER JOIN line_item ON line_item.product_id = product.product_id " +
                "WHERE sale_id ISNULL ";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            Product product = mapRowToProduct(results);
            products.add(product);
        }
        return products;
    }

    @Override
    public Product createProduct(Product newProduct) {
        String sql = "INSERT INTO product (name, description, price, image_name) " +
                "VALUES (?, ?, ?, ?) RETURNING product_id;";
        int newId = jdbcTemplate.queryForObject(sql, int.class, newProduct.getName(), newProduct.getDescription(),
                newProduct.getPrice(), newProduct.getImageName());
        return getProduct(newId);

    }

    @Override
    public void updateProduct(Product updatedProduct) {
        String sql = "UPDATE product " +
                "SET name = ?, description = ?, price = ?, image_name = ? " +
                "WHERE product_id = ?";
        jdbcTemplate.update(sql, updatedProduct.getName(), updatedProduct.getDescription(), updatedProduct.getPrice(),
                updatedProduct.getImageName());
    }

    @Override
    public void deleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";
        jdbcTemplate.update(sql, productId);
    }

    private Product mapRowToProduct(SqlRowSet results) {
        Product product = new Product();
        product.setProductId(results.getInt("product_id"));
        product.setName(results.getString("name"));
        product.setDescription(results.getString("description"));
        product.setPrice(results.getBigDecimal("price"));
        product.setImageName(results.getString("image_name"));
        return product;
    }
}
