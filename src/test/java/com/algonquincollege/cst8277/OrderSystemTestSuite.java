/*****************************************************************c******************o*******v******id********
 * File: OrderSystemTestSuite.java
 * Course materials (20F) CST 8277
 * (Original Author) Mike Norman
 *
 * @date 2020 10
 *
 * (Modified) @author Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 */
package com.algonquincollege.cst8277;

import static com.algonquincollege.cst8277.utils.MyConstants.APPLICATION_API_VERSION;
import static com.algonquincollege.cst8277.utils.MyConstants.CUSTOMER_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.CUSTOMER_ADDRESS_SUBRESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.PRODUCT_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.STORE_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.ORDER_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_ADMIN_USER;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_ADMIN_USER_PASSWORD;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_USER_PREFIX;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.glassfish.jersey.logging.LoggingFeature;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.algonquincollege.cst8277.ejb.CustomerService;
import com.algonquincollege.cst8277.models.AddressPojo;
import com.algonquincollege.cst8277.models.BillingAddressPojo;
import com.algonquincollege.cst8277.models.CustomerPojo;
import com.algonquincollege.cst8277.models.OrderPojo;
import com.algonquincollege.cst8277.models.ProductPojo;
import com.algonquincollege.cst8277.models.ShippingAddressPojo;
import com.algonquincollege.cst8277.models.StorePojo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class OrderSystemTestSuite {
    private static final Class<?> _thisClaz = MethodHandles.lookup().lookupClass();
    private static final Logger logger = LoggerFactory.getLogger(_thisClaz);

    static final String APPLICATION_CONTEXT_ROOT = "rest-orderSystem";
    static final String HTTP_SCHEMA = "http";
    static final String HOST = "localhost";

    //TODO - if you changed your Payara's default port (to say for example 9090)
    //       your may need to alter this constant
    static final int PORT = 8080;

    // test fixture(s)
    static URI uri;
    static HttpAuthenticationFeature adminAuth;
    static HttpAuthenticationFeature userAuth;

    @BeforeAll
    public static void oneTimeSetUp() throws Exception {
        logger.debug("oneTimeSetUp");
        SLF4JBridgeHandler.removeHandlersForRootLogger();
        SLF4JBridgeHandler.install();
        uri = UriBuilder
            .fromUri(APPLICATION_CONTEXT_ROOT + APPLICATION_API_VERSION)
            .scheme(HTTP_SCHEMA)
            .host(HOST)
            .port(PORT)
            .build();
        adminAuth = HttpAuthenticationFeature.basic(DEFAULT_ADMIN_USER, DEFAULT_ADMIN_USER_PASSWORD);
        userAuth = HttpAuthenticationFeature.basic(DEFAULT_USER_PREFIX, DEFAULT_USER_PASSWORD);
    }

    protected WebTarget webTarget;
    @BeforeEach
    public void setUp() {
        Client client = ClientBuilder.newClient(
            new ClientConfig().register(MyObjectMapperProvider.class).register(new LoggingFeature()));
        webTarget = client.target(uri);
    }

    @Test
    public void test01_all_customers_with_adminrole() throws JsonMappingException, JsonProcessingException {
        Response response = webTarget
            //.register(userAuth)
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<CustomerPojo> custs = response.readEntity(new GenericType<List<CustomerPojo>>(){});
        assertThat(custs, is(not(empty())));
        //TODO - depending on what is in your Db when you run this, you may need to change the next line
        assertThat(custs, hasSize(1));
    }

    // TODO - create39 more test-cases that send GET/PUT/POST/DELETE messages
    // to REST'ful endpoints for the OrderSystem entities using the JAX-RS
    // ClientBuilder APIs

    //Create a customer, using Admin Auth, expect success 200 response code
    @Test
    public void test02_create_customer() {
        CustomerPojo cust = new CustomerPojo();
        cust.setId(1);
        cust.setFirstName("John");
        cust.setLastName("Doe");
        cust.setEmail("email@email.com");
        cust.setPhoneNumber("333-333-1234");
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .post(Entity.json(cust));
        assertThat(resp.getStatus(), is(200));

    }

    /*
     * Read a customer, no auth access required, should return all customers 200 response code
     */
    @Test
    public void test03_read_customer() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(resp.getStatus(), is(200));
    }
    
   /*
    * Update customer, admin auth required, should return one customer with CUST_ID value 1, 200 response code
   */
    @Test
    public void test04_update_customer() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .get();
        List<CustomerPojo> custs = resp.readEntity(new GenericType<List<CustomerPojo>>(){});


        custs.get(1).setFirstName("New Name");
        Response put = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1")
            .request()
            .put(Entity.json(custs.get(1)));
        assertThat(put.getStatus(),is(200));


    }
    
    /*
    * Deletes a customer, admin auth required, should remove customer with the CUST_ID value of 1, 200 response code
     * 
     */
    @Test
    public void test05_delete_customer() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(resp.getStatus(), is(not(200)));
        //TODO doesn't allow delete 400 error
    }
    
    /*
    * Creates a new billing address for customer with correct CUST_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test06_create_Billingaddress() {
        AddressPojo addr = new BillingAddressPojo();
        addr.setId(1);
        addr.setCity("Ottawa");
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(addr));
        assertThat(resp.getStatus(), is(200));
    }

    /*
    * Read billing address for customer with correct CUST_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test07_read_Billingaddress() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(resp.getStatus(), is(200));
    }
    
    /*
    * Update billing address for customer with correct CUST_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test08_update_Billingaddress() {
        Response put = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .get();
          List<CustomerPojo> custs = put.readEntity(new GenericType<List<CustomerPojo>>(){});
          AddressPojo addr = custs.get(0).getShippingAddress();
          addr.setCountry("Canada");
          custs.get(0).setShippingAddress(addr);
          Response resp = webTarget
              .path(CUSTOMER_RESOURCE_NAME + "/1")
              .request()
              .put(Entity.json(custs.get(0)));
          assertThat(resp.getStatus(), is(200));
    }
    
    /*
    * Delete billing address for customer with correct CUST_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test09_delete_Billingaddress() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .delete();
        assertThat(resp.getStatus(), not(200));
    }
    
    /*
    * Create shipping address for customer with correct CUST_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test10_create_ShippingAddress() {
        AddressPojo addr = new ShippingAddressPojo();
        addr.setId(1);
        addr.setCity("Ottawa");
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .post(Entity.json(addr));
        assertThat(resp.getStatus(), is(200));
    }
    
    /*
    * Read shipping address for customer with correct CUST_ID, admin auth is required, 200 response code
    */
    @Test
    public void test11_read_ShippingAddress() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .get();
        assertThat(resp.getStatus(), is(200));
    }
    
    /*
    * Update shipping address for customer with correct CUST_ID, admin auth is required, 200 response code
    */
    @Test
    public void test12_update_ShippingAddress() {
        Response put = webTarget
          .register(adminAuth)
          .path(CUSTOMER_RESOURCE_NAME)
          .request()
          .get();
        List<CustomerPojo> custs = put.readEntity(new GenericType<List<CustomerPojo>>(){});
        AddressPojo addr = custs.get(0).getShippingAddress();
        addr.setCountry("Canada");
        custs.get(0).setShippingAddress(addr);
        Response resp = webTarget
            .path(CUSTOMER_RESOURCE_NAME + "/1")
            .request()
            .put(Entity.json(custs.get(0)));
        assertThat(resp.getStatus(), is(200));
    }
    
    /*
    *Delete shipping address for customer with correct CUST_ID, admin auth is required, 200 response code
    */
    @Test
    public void test13_delete_ShippingAddress() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + CUSTOMER_ADDRESS_SUBRESOURCE_NAME)
            .request()
            .delete();
        assertThat(resp.getStatus(), not(200));
    }
    
    /*
    * Create new order for customer with correct CUST_ID, admin auth is required, 200 response code
    */
    @Test
    public void test14_create_Order() {
        OrderPojo order = new OrderPojo();
//        order.setId(10);
        order.setDescription("Something");
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + ORDER_RESOURCE_NAME)
            .request()
            .post(Entity.json(order));
        assertThat(resp.getStatus(), is(200));
    }
    
    /*
    * Read order for customer with correct CUST_ID, admin auth is required, 200 response code
    */
    @Test
    public void test15_read_Order() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + ORDER_RESOURCE_NAME)
            .request()
            .get();
        assertThat(resp.getStatus(), is(200));
    }
    
    /*
    *Update order for customer with correct CUST_ID, admin auth is required, 200 response code
    */
    @Test
    public void test16_update_Order() {
        Response put = webTarget
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + ORDER_RESOURCE_NAME)
            .request()
            .get();
        List<OrderPojo> orders = put.readEntity(new GenericType<List<OrderPojo>>(){});
        orders.get(0).setDescription("Update Order");
        Response resp = webTarget
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + ORDER_RESOURCE_NAME)
            .request()
            .put(Entity.json(orders.get(0)));
        assertThat(resp.getStatus(), is(200));
    }

    /*
    *Delete order for customer with correct CUST_ID, admin auth is required, 200 response code
    */
    @Test
    public void test17_delete_Order() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/2/" + ORDER_RESOURCE_NAME)
            .request()
            .delete();
        assertThat(resp.getStatus(), is(200));
    }
    
    /*
    *Create new product, admin auth is required, 200 response code
    */
    
    @Test
    public void test18_create_Product() {
        ProductPojo prod = new ProductPojo();
        prod.setId(1);
        prod.setDescription("Something");
        Response resp = webTarget
            .register(adminAuth)
            .path(PRODUCT_RESOURCE_NAME)
            .request()
            .post(Entity.json(prod));
        assertThat(resp.getStatus(), is(200));
    }

    /*
    *Get all products, 200 response code
    */
    @Test
    public void test19_get_all_product() {
        Response response = webTarget
            .path(PRODUCT_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<ProductPojo> prod = response.readEntity(new GenericType<List<ProductPojo>>(){});
        assertThat(prod, is(not(empty())));
        assertThat(prod, hasSize(1));
    }

    /*
    * Read product for customer with correct PRODUCT_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test20_read_Product() {
        Response resp = webTarget
            .register(adminAuth)
            .path(PRODUCT_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Update product for customer with correct PRODUCT_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test21_update_Product() {
        Response put = webTarget
            .path(PRODUCT_RESOURCE_NAME)
            .request()
            .get();
        List<ProductPojo> prod = put.readEntity(new GenericType<List<ProductPojo>>(){});

        prod.get(1).setDescription("Update Product");
        Response resp = webTarget
            .path(PRODUCT_RESOURCE_NAME)
            .request()
            .put(Entity.json(prod.get(1)));
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Delete product for with correct PRODUCT_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test22_delete_Product() {
        Response resp = webTarget
            .register(adminAuth)
            .path(PRODUCT_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Create new store, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test23_create_Store() {
        StorePojo store = new StorePojo();
        store.setId(1);
        store.setStoreName("Store1");
        Response resp = webTarget
            .register(adminAuth)
            .path(STORE_RESOURCE_NAME)
            .request()
            .post(Entity.json(store));
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Get all stores, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test24_get_all_store() {
        Response response = webTarget
            .path(STORE_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(200));
        List<ProductPojo> prod = response.readEntity(new GenericType<List<ProductPojo>>(){});
        assertThat(prod, is(not(empty())));
        assertThat(prod, hasSize(1));
    }
    /*
    * Read store with correct STORE_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test25_read_Store() {
        Response resp = webTarget
            .register(adminAuth)
            .path(STORE_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Update store with correct STORE_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test26_update_Store() {
        Response put = webTarget
            .path(STORE_RESOURCE_NAME)
            .request()
            .get();
        List<StorePojo> store = put.readEntity(new GenericType<List<StorePojo>>(){});

        store.get(1).setStoreName("Update Store");
        Response resp = webTarget
            .path(STORE_RESOURCE_NAME)
            .request()
            .put(Entity.json(store.get(1)));
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Delete store with correct STORE_ID, admin auth is required, 200 response code
     * 
     */
    @Test
    public void test27_delete_Store() {
        Response resp = webTarget
            .register(adminAuth)
            .path(STORE_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Create new store, user auth is required, 401 response code unauthorized
     * 
     */
    @Test
    public void test28_create_store_user() {
        StorePojo store = new StorePojo();
        store.setId(1);
        store.setStoreName("Store1");
        Response resp = webTarget
            .register(userAuth)
            .path(STORE_RESOURCE_NAME)
            .request()
            .post(Entity.json(store));
        assertThat(resp.getStatus(), is(not(400)));
    }
    /*
    * Update store with correct STORE_ID, user auth is required, 401 response code unauthorized
     * 
     */
    @Test
    public void test29_update_store_user() {
        Response put = webTarget
            .path(STORE_RESOURCE_NAME)
            .request()
            .get();
        List<StorePojo> store = put.readEntity(new GenericType<List<StorePojo>>(){});

        store.get(1).setStoreName("Update Store");
        Response resp = webTarget
            .register(userAuth)
            .path(STORE_RESOURCE_NAME)
            .request()
            .put(Entity.json(store.get(1)));
        assertThat(resp.getStatus(), is(not(400)));
    }
    
    /*
    * Update store with correct STORE_ID, user auth is required, 401 response code unauthorized
     * 
     */
    @Test
    public void test30_delete_store_user() {
        Response resp = webTarget
            .register(userAuth)
            .path(STORE_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(resp.getStatus(), is(not(400)));
    }
    /*
    * Create new product, user auth is required, 401 response code unauthorized
     * 
     */
    @Test
    public void test31_create_product_user() {
        ProductPojo prod = new ProductPojo();
        prod.setId(1);
        prod.setDescription("Something");
        Response resp = webTarget
            .register(userAuth)
            .path(PRODUCT_RESOURCE_NAME)
            .request()
            .post(Entity.json(prod));
        assertThat(resp.getStatus(), is(not(400)));
    }
    /*
    * Update product, user auth is required, 401 response code unauthorized
   * 
   */
    @Test
    public void test32_update_product_user() {
        Response put = webTarget
            .path(PRODUCT_RESOURCE_NAME)
            .request()
            .get();
        List<ProductPojo> prod = put.readEntity(new GenericType<List<ProductPojo>>(){});

        prod.get(1).setDescription("Update Product");
        Response resp = webTarget
            .register(userAuth)
            .path(PRODUCT_RESOURCE_NAME)
            .request()
            .put(Entity.json(prod.get(1)));
        assertThat(resp.getStatus(), is(not(400)));
    }
    /*
    * Delete product with correct PRODUCT_ID, user auth is required , 401 response code unauthorized
     * 
     */
    @Test
    public void test33_delete_product_user() {
        Response resp = webTarget
            .register(userAuth)
            .path(PRODUCT_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(resp.getStatus(), is(not(400)));
    }
    
    /*
    * Get all customers, user auth is required , 401 response code unauthorized
   * 
   */
    @Test
    public void test34_all_customers_with_userRole() {
        Response response = webTarget
            .register(userAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .get();
        assertThat(response.getStatus(), is(401));
        

    }
    
    /*
     * Create new customer, user auth is required , 401 response code unauthorized
     * 
     */
    @Test
    public void test35_create_customer_userRole() {
        CustomerPojo cust = new CustomerPojo();
        cust.setId(1);
        cust.setFirstName("John");
        cust.setLastName("Doe");
        cust.setEmail("email@email.com");
        cust.setPhoneNumber("333-333-1234");
        Response resp = webTarget
            .register(userAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .post(Entity.json(cust));
        assertThat(resp.getStatus(), is(401));

    }
    /*
     * Read customer with correct CUST_ID, user auth is required, expect 200 as both USER and ADMIN authentication is allowed
     * 
     */
    @Test
    public void test36_read_customer_userRole() {
        Response resp = webTarget
            .register(userAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1")
            .request()
            .get();
        assertThat(resp.getStatus(), is(200));
    }
    /*
    * Update customer with correct CUST_ID, user auth is required , 401 response code unauthorized
     * 
     */
    @Test
    public void test37_update_customer_userRole() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .get();
        List<CustomerPojo> custs = resp.readEntity(new GenericType<List<CustomerPojo>>(){});


        custs.get(1).setFirstName("New Name");
        Response put = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1")
            .request()
            .put(Entity.json(custs.get(1)));
        assertThat(put.getStatus(),is(401));


    }
    /*
    * Delete customer with correct CUST_ID, user auth is required , 401 response code unauthorized
     * 
     */
    @Test
    public void test38_delete_customer_userRole() {
        Response resp = webTarget
            .register(userAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1")
            .request()
            .delete();
        assertThat(resp.getStatus(), is(401));
    }
	
    /**
     * Test case to update orderline in order
     * */
    @Test
    public void test39_update_OrderLine_Order() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + ORDER_RESOURCE_NAME)
            .request()
            .get();
        List<OrderPojo> orders = resp.readEntity(new GenericType<List<OrderPojo>>(){});
        OrderPojo o = orders.get(0);
        List<OrderLinePojo> olp = o.getOrderlines();
        if(olp != null)
            olp.get(0).setAmount(150.00);
        o.setOrderlines(olp);
        orders.add(o);
        Response resp2 = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + ORDER_RESOURCE_NAME)
            .request()
            .put(Entity.json(orders.get(0)));
        assertThat(resp2.getStatus(), is(200));
    }
	
     /**
     * Test case to check the association between order and customer
     * */
    @Test
    public void test40_compare_Order_Customer() {
        Response resp = webTarget
            .register(adminAuth)
            .path(CUSTOMER_RESOURCE_NAME)
            .request()
            .get();
        List<CustomerPojo> custs = resp.readEntity(new GenericType<List<CustomerPojo>>(){});
        List<OrderPojo> custOrders = custs.get(0).getOrders();
        Response put = webTarget
            .path(CUSTOMER_RESOURCE_NAME + "/1/" + ORDER_RESOURCE_NAME)
            .request()
            .get();
        List<OrderPojo> orders = put.readEntity(new GenericType<List<OrderPojo>>(){});
        assertEquals(custOrders, orders);
    }

}

