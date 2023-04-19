package com.techelevator.ssgeek.dao;

import com.techelevator.ssgeek.model.Customer;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;

public class JdbcCustomerDaoTests extends BaseDaoTests {

    private static final Customer CUSTOMER_1 = new Customer(1, "Customer 1", "Addr 1-1", "Addr 1-2", "City 1", "S1", "11111");
    private static final Customer CUSTOMER_2 = new Customer(2, "Customer 2", "Addr 2-1", "Addr 2-2", "City 2", "S2", "22222");
    private static final Customer CUSTOMER_3 = new Customer(3, "Customer 3", "Addr 3-1",       null, "City 3", "S3", "33333");
    private static final Customer CUSTOMER_4 = new Customer(4, "Customer 4", "Addr 4-1",       null, "City 4", "S4", "44444");

    private JdbcCustomerDao customerDao;
    private Customer testCustomer;

    @Before
    public void setup() {
        customerDao = new JdbcCustomerDao(dataSource);
        testCustomer = new Customer(0, "Test Customer", "Test Addr 1", "Test Addr 2", "Test City", "TS", "98765");
    }

    @Test
    public void getCustomer_returns_correct_customer_for_id() {
        Customer customer = customerDao.getCustomer(1);
        Assert.assertNotNull("getCustomer returned null", customer);
        assertCustomersMatch("getCustomer returned wrong or partial data", CUSTOMER_1, customer);

        customer = customerDao.getCustomer(4);
        Assert.assertNotNull("getCustomer returned null", customer);
        assertCustomersMatch("getCustomer returned wrong or partial data", CUSTOMER_4, customer);
    }

    @Test
    public void getCustomer_returns_null_when_id_not_found() {
        Customer customer = customerDao.getCustomer(99);
        Assert.assertNull("getCustomer failed to return null for id not in database", customer);
    }

    @Test
    public void getCustomers_returns_list_of_all_customers() {
        List<Customer> customers = customerDao.getCustomers();
        Assert.assertEquals("getCustomers returned wrong number of customers", 4, customers.size());
        Assert.assertEquals("getCustomers: first customer id does not match expected, check sort order",
                CUSTOMER_1.getCustomerId(), customers.get(0).getCustomerId());
        assertCustomersMatch("getCustomers", CUSTOMER_1, customers.get(0));
        assertCustomersMatch("getCustomers", CUSTOMER_2, customers.get(1));
        assertCustomersMatch("getCustomers", CUSTOMER_3, customers.get(2));
        assertCustomersMatch("getCustomers", CUSTOMER_4, customers.get(3));
    }

    @Test
    public void createCustomer_returns_customer_with_id_and_expected_values() {
        Customer createdCustomer = customerDao.createCustomer(testCustomer);

        Assert.assertNotNull("createCustomer returned null", createdCustomer);

        int newId = createdCustomer.getCustomerId();
        Assert.assertTrue("createCustomer failed to return a customer with an id", newId > 0);

        testCustomer.setCustomerId(newId);
        assertCustomersMatch("createCustomer returned wrong or partial data", testCustomer, createdCustomer);
    }

    @Test
    public void created_customer_has_expected_values_when_retrieved() {
        Customer createdCustomer = customerDao.createCustomer(testCustomer);

        int newId = createdCustomer.getCustomerId();
        Customer retrievedCustomer = customerDao.getCustomer(newId);

        assertCustomersMatch("createCustomer did not save correct data in database", createdCustomer, retrievedCustomer);
    }

    @Test
    public void updated_customer_has_expected_values_when_retrieved() {
        Customer customer = customerDao.getCustomer(1);
        customer.setName("A new name");
        customer.setStreetAddress1("A new address line 1");
        customer.setStreetAddress2(null);
        customer.setCity("A new city");
        customer.setState("XX");
        customer.setZipCode("ABCDE");

        customerDao.updateCustomer(customer);

        Customer updatedCustomer = customerDao.getCustomer(1);
        assertCustomersMatch("updateCustomer", customer, updatedCustomer);
    }

    @Test
    public void deleted_customer_cant_be_retrieved() {
        // NOTE: We must delete one that isn't on any order
        customerDao.deleteCustomer(3);

        Customer customer = customerDao.getCustomer(3);
        Assert.assertNull("deleteCustomer failed to remove customer from database", customer);

        List<Customer> customers = customerDao.getCustomers();
        Assert.assertEquals("deleteCustomer removed the wrong number of customers", 3, customers.size());
    }

    @Test
    public void customer_with_sales_cant_be_deleted() {
        // An exception is expected if the customer has sales orders, and we try to delete the customer.
        try {
            customerDao.deleteCustomer(1);
        } catch (DataIntegrityViolationException exception) {
            // The DAO threw an exception because the row couldn't be deleted. Test passes. Return from the method.
            return;
        }
        // If control gets here, then the row was deleted. It shouldn't have been deleted, because
        // sales orders are attached to this customer. Therefore, FAIL the test.
        Assert.fail("A customer with sales orders should not be deleted.");
    }
    
    private void assertCustomersMatch(String messagePrefix, Customer expected, Customer actual) {
        String message = messagePrefix + ": unexpected data in field '%s'.";
        Assert.assertEquals(String.format(message, "customerId"), expected.getCustomerId(), actual.getCustomerId());
        Assert.assertEquals(String.format(message, "name"), expected.getName(), actual.getName());
        Assert.assertEquals(String.format(message, "streetAddress1"), expected.getStreetAddress1(), actual.getStreetAddress1());
        Assert.assertEquals(String.format(message, "streetAddress2"), expected.getStreetAddress2(), actual.getStreetAddress2());
        Assert.assertEquals(String.format(message, "city"), expected.getCity(), actual.getCity());
        Assert.assertEquals(String.format(message, "state"), expected.getState(), actual.getState());
        Assert.assertEquals(String.format(message, "zipCode"), expected.getZipCode(), actual.getZipCode());
    }
}

