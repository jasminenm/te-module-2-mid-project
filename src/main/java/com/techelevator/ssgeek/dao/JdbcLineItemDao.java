package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import com.techelevator.ssgeek.model.LineItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

public class JdbcLineItemDao implements LineItemDao {

    private final JdbcTemplate jdbcTemplate;
    public JdbcLineItemDao (DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<LineItem> getLineItemsBySale(int saleId) {
        List<LineItem> lineItems = new ArrayList<>();
        String sql = "SELECT line_item_id, sale_id, product_id, quantity " +
                "FROM line_item " +
                "ORDER BY line_item_id";
        SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
        while (results.next()) {
            LineItem lineItem = mapRowToLineItem(results);
            lineItems.add(lineItem);
        }
        return lineItems;
    }

    private LineItem mapRowToLineItem(SqlRowSet results) {
        LineItem lineItem = new LineItem();
        lineItem.setLineItemId(results.getInt("line_item_id"));
        lineItem.setSaleId(results.getInt("sale_id"));
        lineItem.setProductId(results.getInt("product_id"));
        lineItem.setQuantity(results.getInt("quantity"));
        return lineItem;
    }
}
