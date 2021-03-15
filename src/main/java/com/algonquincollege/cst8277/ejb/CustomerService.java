/*****************************************************************c******************o*******v******id********
 * File: CustomerService.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 *
 */
package com.algonquincollege.cst8277.ejb;

import static com.algonquincollege.cst8277.models.SecurityRole.ROLE_BY_NAME_QUERY;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_KEY_SIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_SALT_SIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_USER_PASSWORD;
import static com.algonquincollege.cst8277.utils.MyConstants.DEFAULT_USER_PREFIX;
import static com.algonquincollege.cst8277.utils.MyConstants.PARAM1;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_ALGORITHM;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_ITERATIONS;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_KEYSIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.PROPERTY_SALTSIZE;
import static com.algonquincollege.cst8277.utils.MyConstants.USER_ROLE;
import static com.algonquincollege.cst8277.models.CustomerPojo.ALL_CUSTOMERS_QUERY_NAME;
import static com.algonquincollege.cst8277.models.StorePojo.ALL_STORES_QUERY_NAME;
import static com.algonquincollege.cst8277.models.SecurityUser.USER_FOR_OWNING_CUST_QUERY;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Singleton;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
//import javax.transaction.Transactional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import com.algonquincollege.cst8277.models.AddressPojo;
import com.algonquincollege.cst8277.models.CustomerPojo;
import com.algonquincollege.cst8277.models.OrderLinePojo;
import com.algonquincollege.cst8277.models.OrderPojo;
import com.algonquincollege.cst8277.models.ProductPojo;
import com.algonquincollege.cst8277.models.SecurityRole;
import com.algonquincollege.cst8277.models.SecurityUser;
import com.algonquincollege.cst8277.models.ShippingAddressPojo;
import com.algonquincollege.cst8277.models.StorePojo;

/**
 * Stateless Singleton Session Bean - CustomerService
 */
@Singleton
public class CustomerService implements Serializable {
    private static final long serialVersionUID = 1L;
    /** static final String CUSTOMER_PU */
    public static final String CUSTOMER_PU = "20f-groupProject-PU";
    /** EntityManager */
    @PersistenceContext(name = CUSTOMER_PU)
    protected EntityManager em;
    /** Pbkdf2PasswordHash */
    @Inject
    protected Pbkdf2PasswordHash pbAndjPasswordHash;
    /** List<OrderLinePojo> */
    protected List<OrderLinePojo> orderlines;
    /** List<AddressPojo> */
    protected List<AddressPojo> listAddr;
    //TODO
    /**
     * @return List<CustomerPojo>
     * */
    public List<CustomerPojo> getAllCustomers() {
        return em.createNamedQuery(ALL_CUSTOMERS_QUERY_NAME, CustomerPojo.class).getResultList();
    }
    /**
     * @return CustomerPojo
     * @param int custPK
     * */
    public CustomerPojo getCustomerById(int custPK) {
        try {
            CustomerPojo customer = em.find(CustomerPojo.class, custPK);
            return customer;
        }catch(Exception e) {
            return null;
        }
    }
    /**
     * @return CustomerPojo
     * @param CustomerPojo newCustomer
     * */
    @Transactional
    public CustomerPojo persistCustomer(CustomerPojo newCustomer) {
        try {
            em.persist(newCustomer);
            return newCustomer;
        }catch(Exception e) {
            return null;
        }

    }
    /**
     * @return CustomerPojo
     * @param CustomerPojo beforeCust, CustomerPojo customerWithUpdates
     * */
    @Transactional
    public CustomerPojo updateCustomer(CustomerPojo beforeCust, CustomerPojo customerWithUpdates) {
        CustomerPojo updatedCustomer = null;
        try {
            beforeCust.setFirstName(customerWithUpdates.getFirstName());
            beforeCust.setLastName(customerWithUpdates.getLastName());
            beforeCust.setEmail(customerWithUpdates.getEmail());
            beforeCust.setPhoneNumber(customerWithUpdates.getPhoneNumber());
            beforeCust.setShippingAddress(customerWithUpdates.getShippingAddress());
            beforeCust.setBillingAddress(customerWithUpdates.getBillingAddress());
            updatedCustomer = em.merge(em.contains(beforeCust) ? em.merge(beforeCust) : null);
            return updatedCustomer;
        }catch(Exception e) {
            return null;
        }
    }
    /**
     * @param CustomerPojo newCustomerWithIdTimestamps
     * */
    @Transactional
    public void buildUserForNewCustomer(CustomerPojo newCustomerWithIdTimestamps) {
        SecurityUser userForNewCustomer = new SecurityUser();
        userForNewCustomer.setUsername(DEFAULT_USER_PREFIX + "" + newCustomerWithIdTimestamps.getId());
        Map<String, String> pbAndjProperties = new HashMap<>();
        pbAndjProperties.put(PROPERTY_ALGORITHM, DEFAULT_PROPERTY_ALGORITHM);
        pbAndjProperties.put(PROPERTY_ITERATIONS, DEFAULT_PROPERTY_ITERATIONS);
        pbAndjProperties.put(PROPERTY_SALTSIZE, DEFAULT_SALT_SIZE);
        pbAndjProperties.put(PROPERTY_KEYSIZE, DEFAULT_KEY_SIZE);
        pbAndjPasswordHash.initialize(pbAndjProperties);
        String pwHash = pbAndjPasswordHash.generate(DEFAULT_USER_PASSWORD.toCharArray());
        userForNewCustomer.setPwHash(pwHash);
        userForNewCustomer.setCustomer(newCustomerWithIdTimestamps);
        SecurityRole userRole = em.createNamedQuery(ROLE_BY_NAME_QUERY,
            SecurityRole.class).setParameter(PARAM1, USER_ROLE).getSingleResult();
        userForNewCustomer.getRoles().add(userRole);
        userRole.getUsers().add(userForNewCustomer);
        em.persist(userForNewCustomer);
    }
    /**
     * @param CustomerPojo cust
     * */
    @Transactional
    public void removeCustomer(CustomerPojo cust) {
        SecurityUser user = null;
        try {
            em.remove(em.contains(cust) ? cust : em.merge(cust));
        }catch(Exception e) {
            System.out.println("Exception at cust: "+e);
        }
    }
    /**
     * @return CustomerPojo
     * @param int custId, AddressPojo newAddress
     * */
    @Transactional
    public CustomerPojo setAddressFor(int custId, AddressPojo newAddress) {
        CustomerPojo updatedCustomer = em.find(CustomerPojo.class, custId);
        if (newAddress instanceof ShippingAddressPojo) {
            updatedCustomer.setShippingAddress(newAddress);
        }
        else {
            updatedCustomer.setBillingAddress(newAddress);
        }
        em.merge(updatedCustomer);
        return updatedCustomer;
    }
    /**
     * @return AddressPojo
     * @param int custId, AddressPojo addressWithUpdates
     * */
    @Transactional
    public AddressPojo updateAddressFor(int custId, AddressPojo addressWithUpdates) {
        AddressPojo updatedAddress = null;
        CustomerPojo updatedCustomer = em.find(CustomerPojo.class, custId);
        AddressPojo beforedAddress = updatedCustomer.getShippingAddress();
        try {
            beforedAddress.setCity(addressWithUpdates.getCity());
            beforedAddress.setCountry(addressWithUpdates.getCountry());
            beforedAddress.setPostal(addressWithUpdates.getPostal());
            beforedAddress.setState(addressWithUpdates.getState());
            beforedAddress.setStreet(addressWithUpdates.getStreet());
            updatedAddress = em.merge(em.contains(beforedAddress) ? em.merge(beforedAddress) : null);
            return updatedAddress;
        }catch(Exception e) {
            return null;
        }
    }

    /**
     * @return CustomerPojo
     * @param int custId, OrderLinePojo newOrderLine
     * */
    @Transactional
    public CustomerPojo setOrderFor(int custId, OrderLinePojo newOrderLine) {
        CustomerPojo updatedCustomer = em.find(CustomerPojo.class, custId);
        List<OrderPojo> myOrdersList = updatedCustomer.getOrders();
        OrderPojo myOrder = newOrderLine.getOwningOrder();
        myOrdersList.add(myOrder);
        updatedCustomer.setOrders(myOrdersList);
        return em.merge(updatedCustomer);
    }
    /**
     * @return List<OrderPojo>
     * @param int id
     * */
    public List<OrderPojo> getOrderFor(int id) {
        try {
            CustomerPojo customer = em.find(CustomerPojo.class, id);
            return customer.getOrders();
        }catch(Exception e) {
            return null;
        }
    }
    /**
     * @return OrderPojo
     * @param int custId, OrderPojo orderWithUpdates
     * */
    @Transactional
    public OrderPojo updateOrderFor(int custId, OrderPojo orderWithUpdates) {
        OrderPojo beforedOrder = null;
        OrderPojo updatedOrder = null;
        CustomerPojo updatedCustomer = em.find(CustomerPojo.class, custId);
        List<OrderPojo> orderList = updatedCustomer.getOrders();
        for(OrderPojo order : orderList) {
            if(order.getId() == orderWithUpdates.getId()) {
                beforedOrder = order;
            }
        }
        try {
            beforedOrder.setDescription(orderWithUpdates.getDescription());
            beforedOrder.setOrderlines(orderWithUpdates.getOrderlines());
            updatedOrder = em.merge(em.contains(beforedOrder) ? em.merge(beforedOrder) : null);
            return updatedOrder;
        }catch(Exception e) {
            return null;
        }
    }
    /**
     * @param List<OrderPojo> orders
     * */
    @Transactional
    public void removeOrder(List<OrderPojo> orders) {
        try {
            em.remove(em.contains(orders) ? orders : em.merge(orders));
        }catch(Exception e) {
            System.out.println("Exception: "+e);
        }
    }
    /**
     * @return List<ProductPojo>
     * */
    public List<ProductPojo> getAllProducts() {
        //example of using JPA Criteria query instead of JPQL
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ProductPojo> q = cb.createQuery(ProductPojo.class);
            Root<ProductPojo> c = q.from(ProductPojo.class);
            q.select(c);
            TypedQuery<ProductPojo> q2 = em.createQuery(q);
            List<ProductPojo> allProducts = q2.getResultList();
            return allProducts;
        }
        catch (Exception e) {
            return null;
        }
    }
    /**
     * @return ProductPojo
     * @param int prodId
     * */
    public ProductPojo getProductById(int prodId) {
        try {
            ProductPojo product = em.find(ProductPojo.class, prodId);
            return product;
        }catch(Exception e) {
            return null;
        }

    }
    /**
     * @return ProductPojo
     * @param ProductPojo newProduct
     * */
    @Transactional
    public ProductPojo persistProduct(ProductPojo newProduct) {
        try {
            em.persist(newProduct);
            return newProduct;
        }catch(Exception e) {
            return null;
        }

    }
    /**
     * @return ProductPojo
     * @param ProductPojo beforeProject, ProductPojo productWithUpdates
     * */
    @Transactional
    public ProductPojo updateProduct(ProductPojo beforeProject, ProductPojo productWithUpdates) {
        ProductPojo updatedProduct = null;
        try {
            beforeProject.setDescription(productWithUpdates.getDescription());
            beforeProject.setSerialNo(productWithUpdates.getSerialNo());
            updatedProduct = em.merge(em.contains(beforeProject) ? em.merge(beforeProject) : null);
            return updatedProduct;
        }catch(Exception e) {
            return null;
        }
    }
    /**
     * @param ProductPojo prod
     * */
    @Transactional
    public void removeProduct(ProductPojo prod) {
        try {
            em.remove(em.contains(prod) ? prod : em.merge(prod));
        }catch(Exception e) {
            System.out.println("Exception: "+e);
        }
    }
    /**
     * @return List<StorePojo>
     * */
    public List<StorePojo> getAllStores() {
        TypedQuery<StorePojo> allStoresQuery = em.createNamedQuery(ALL_STORES_QUERY_NAME, StorePojo.class);
        return allStoresQuery.getResultList();
    }
    /**
     * @param int id
     * @return StorePojo
     * */
    public StorePojo getStoreById(int id) {
        try {
            StorePojo store = em.find(StorePojo.class, id);
            return store;
        }catch(Exception e) {
            return null;
        }

    }
    /**
     * @param StorePojo newStore
     * @return StorePojo
     * */
    @Transactional
    public StorePojo persistStore(StorePojo newStore) {
        try {
            em.persist(newStore);
            return newStore;
        }catch(Exception e) {
            System.out.println("Error: "+e);
            return null;
        }

    }
    /**
     * @param StorePojo newStore
     * @return StorePojo beforeStore, StorePojo storeWithUpdates
     * */
    @Transactional
    public StorePojo updateStore(StorePojo beforeStore, StorePojo storeWithUpdates) {
        StorePojo updatedStore = null;
        try {
            beforeStore.setStoreName(storeWithUpdates.getStoreName());
            updatedStore = em.merge(em.contains(beforeStore) ? em.merge(beforeStore) : null);
            return updatedStore;
        }catch(Exception e) {
            System.out.println("Exception: "+e);
            return null;
        }
    }
    /**
     * @param StorePojo store
     * */
    @Transactional
    public void removeStore(StorePojo store) {
        try {
            em.remove(em.contains(store) ? store : em.merge(store));
        }catch(Exception e) {
            System.out.println("Exception: "+e);
        }
    }
    /**
     * @param AddressPojo addr
     * */
    @Transactional
    public void removeAddressFor(AddressPojo addr) {
        try {
            em.remove(em.contains(addr) ? addr : em.merge(addr));
        }catch(Exception e) {
            System.out.println("Exception: "+e);
        }
    }
    /**
     * @param int id
     * @return AddressPojo
     * */
    public AddressPojo getAddressByCustId(int id) {
        try {
            CustomerPojo cust = em.find(CustomerPojo.class, id);
            AddressPojo addr = cust.getShippingAddress();
            return addr;
        }catch(Exception e) {
            return null;
        }

    }
    /**
     * @return List<ShippingAddressPojo>
     * */
    public List<ShippingAddressPojo> getAllAddresss() {
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<ShippingAddressPojo> q = cb.createQuery(ShippingAddressPojo.class);
            Root<ShippingAddressPojo> c = q.from(ShippingAddressPojo.class);
            q.select(c);
            TypedQuery<ShippingAddressPojo> q2 = em.createQuery(q);
            List<ShippingAddressPojo> allAddresses = q2.getResultList();
            return allAddresses;
        }
        catch (Exception e) {
            return null;
        }
    }
    /*

    public OrderPojo getAllOrders ... getOrderbyId ... build Orders with OrderLines ...

     */
//    public List<OrderLinePojo> getOrderlines() {
//        return this.orderlines;
//    }
//    public void setOrderlines(List<OrderLinePojo> orderlines) {
//        this.orderlines = orderlines;
//    }
//    public OrderLinePojo addOrderline(OrderLinePojo orderline) {
//        OrderPojo ord = new OrderPojo();
//        getOrderlines().add(orderline);
//        orderline.setOwningOrder(ord);
//        return orderline;
//    }
//    public OrderLinePojo removeOrderline(OrderLinePojo orderline) {
//        getOrderlines().remove(orderline);
//        orderline.setOwningOrder(null);
//        return orderline;
//    }



}