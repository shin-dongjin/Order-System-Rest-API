/*****************************************************************c******************o*******v******id********
 * File: CustomerResource.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 *
 */
package com.algonquincollege.cst8277.rest;

import static com.algonquincollege.cst8277.utils.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utils.MyConstants.CUSTOMER_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utils.MyConstants.RESOURCE_PATH_ID_PATH;
import static com.algonquincollege.cst8277.utils.MyConstants.USER_ROLE;
import static com.algonquincollege.cst8277.utils.MyConstants.CUSTOMER_ADDRESS_RESOURCE_PATH;
import static com.algonquincollege.cst8277.utils.MyConstants.CUSTOMER_ORDERS_RESOURCE_PATH;
import static com.algonquincollege.cst8277.utils.MyConstants.CUSTOMER_ADDRESS_SUBRESOURCE_NAME;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.security.enterprise.SecurityContext;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.ForbiddenException;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.glassfish.soteria.WrappingCallerPrincipal;

import com.algonquincollege.cst8277.ejb.CustomerService;
import com.algonquincollege.cst8277.models.AddressPojo;
import com.algonquincollege.cst8277.models.CustomerPojo;
import com.algonquincollege.cst8277.models.OrderLinePojo;
import com.algonquincollege.cst8277.models.OrderPojo;
import com.algonquincollege.cst8277.models.SecurityUser;
import com.algonquincollege.cst8277.models.ShippingAddressPojo;
import com.algonquincollege.cst8277.models.StorePojo;

@Path(CUSTOMER_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class CustomerResource {
    /** CustomerService customerServiceBean */
    @EJB
    protected CustomerService customerServiceBean;
    /** ServletContext servletContext */
    @Inject
    protected ServletContext servletContext;
    /** SecurityContext sc */
    @Inject
    protected SecurityContext sc;
    /**
     * @return Response
     * */
    @GET
    //    @RolesAllowed({ADMIN_ROLE}) // Task 4 - Only a user with the SecurityRole ‘ADMIN_ROLE’ can get the list of all Customers
    public Response getCustomers() {
        servletContext.log("retrieving all customers ...");
        List<CustomerPojo> custs = customerServiceBean.getAllCustomers();
        Response response = Response.ok(custs).build();
        return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     * */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE}) // Task 4 - A user with either the role ‘ADMIN_ROLE’ or ‘USER_ROLE’ can get a specific Customer.
    @Path(RESOURCE_PATH_ID_PATH)
    public Response getCustomerById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to retrieve specific customer " + id);
        Response response = null;
        CustomerPojo cust = null;
        servletContext.log("SECURITY: "+sc.isCallerInRole(USER_ROLE +" "+ADMIN_ROLE ));
        if (sc.isCallerInRole(ADMIN_ROLE)) {
            cust = customerServiceBean.getCustomerById(id);
            response = Response.status( cust == null ? NOT_FOUND : OK).entity(cust).build();
        }
        else if (sc.isCallerInRole(USER_ROLE)) {
            WrappingCallerPrincipal wCallerPrincipal = (WrappingCallerPrincipal)sc.getCallerPrincipal();
            SecurityUser sUser = (SecurityUser)wCallerPrincipal.getWrapped();
            cust = sUser.getCustomer();
            if (cust != null && cust.getId() == id) {
                response = Response.status(OK).entity(cust).build();
            }
            else {
                throw new ForbiddenException();
            }
        }
        else {
            response = Response.status(BAD_REQUEST).build();
        }
        return response;
    }
    /**
     * @return Response
     * @param CustomerPojo newCustomer
     * */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    @Transactional
    public Response addCustomer(CustomerPojo newCustomer) {
        Response response = null;
        CustomerPojo newCustomerWithIdTimestamps = customerServiceBean.persistCustomer(newCustomer);
        //build a SecurityUser linked to the new customer
        customerServiceBean.buildUserForNewCustomer(newCustomerWithIdTimestamps);
        response = Response.ok(newCustomerWithIdTimestamps).build();
        return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     * */
    @DELETE
    @RolesAllowed({ADMIN_ROLE}) // Task 4 - Only an ‘ADMIN_ROLE’ user can delete any entities
    @Path(RESOURCE_PATH_ID_PATH)
    public Response deleteCustomerById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to delete specific customer " + id);
        Response response = null;
        CustomerPojo cust = null;
        try {
            cust = customerServiceBean.getCustomerById(id);
            customerServiceBean.removeCustomer(cust);
            response = Response.status(OK).entity(cust).build();
        }catch(Exception e) {
            response = Response.status(BAD_REQUEST).entity(cust).build();
        }

        return response;
    }
    
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id, CustomerPojo customerWithUpdates
     * */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Transactional
    @Path(RESOURCE_PATH_ID_PATH)
    public Response updateCustomer(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, CustomerPojo customerWithUpdates) {
        servletContext.log("try to update specific customer " + id);
        Response resp = null;
        CustomerPojo cust = null;
        try {
            cust = customerServiceBean.getCustomerById(id);
            customerServiceBean.updateCustomer(cust, customerWithUpdates);
            resp = Response.status(OK).entity(customerWithUpdates).build();
        }catch(Exception e) {
            resp = Response.status(BAD_REQUEST).entity(resp).build();
        }
        return resp;
    }
    /**
     * @return Response
     * */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(CUSTOMER_ADDRESS_RESOURCE_PATH)
    public Response getAddresses() {
        servletContext.log("retrieving all addresses ...");
        List<ShippingAddressPojo> addr = customerServiceBean.getAllAddresss();
        Response response = Response.ok(addr).build();
        return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id, AddressPojo newAddress
     * */
    @POST
    @RolesAllowed({ADMIN_ROLE}) // Task 4 - Only an ‘ADMIN_ROLE’ user can associate an Address (Billing or Shipping) to a Customer
    @Path(CUSTOMER_ADDRESS_RESOURCE_PATH)
    public Response addAddressForCustomer(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, AddressPojo newAddress) {
        Response response = null;
        CustomerPojo updatedCustomer = customerServiceBean.setAddressFor(id, newAddress);
        response = Response.ok(updatedCustomer).build();
        return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id, AddressPojo updatedAddress
     * */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Path(CUSTOMER_ADDRESS_RESOURCE_PATH)
    public Response updateAddressForCustomer(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, AddressPojo updatedAddress) {
        Response response = null;
        AddressPojo update = customerServiceBean.updateAddressFor(id, updatedAddress);
        response = Response.ok(update).build();
        return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     * */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(CUSTOMER_ADDRESS_RESOURCE_PATH)
    public Response deleteCustomerAddressById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to delete specific customer address" + id);
        Response response = null;
        AddressPojo addr = null;
        try {
            addr = customerServiceBean.getAddressByCustId(id);
            customerServiceBean.removeAddressFor(addr);
            response = Response.status(OK).entity(addr).build();
        }catch(Exception e) {
            response = Response.status(BAD_REQUEST).entity(addr).build();
        }

        return response;
    }

    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     * */
    @GET
    @RolesAllowed({ADMIN_ROLE, USER_ROLE})
    @Path(CUSTOMER_ADDRESS_RESOURCE_PATH + RESOURCE_PATH_ID_PATH)
    public Response getCustomerAddressById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to retrieve specific customer address" + id);
        Response response = null;
        try {
            AddressPojo addr = customerServiceBean.getAddressByCustId(id);
            response = Response.ok(addr).build();

        }catch(Exception e) {
            response = Response.status(BAD_REQUEST).build();
        }
        return response;
    }

    //TODO - endpoints for setting up Orders/OrderLines
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id, OrderLinePojo newOrderLine
     * */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    @Path(CUSTOMER_ORDERS_RESOURCE_PATH)
    public Response addOrderForCustomer(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, OrderLinePojo newOrderLine) {
      servletContext.log("try to add specific order/orderline " + id);
      Response response = null;
      CustomerPojo updatedCustomer = customerServiceBean.setOrderFor(id, newOrderLine);
      response = Response.ok(updatedCustomer).build();
      return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     * */
    @GET
    @RolesAllowed({ADMIN_ROLE})
    @Path(CUSTOMER_ORDERS_RESOURCE_PATH)
    public Response getOrderForCustomer(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
      servletContext.log("try to get specific order/orderline " + id);
      Response response = null;
      List<OrderPojo> orders = customerServiceBean.getOrderFor(id);
      response = Response.ok(orders).build();
      return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id, OrderPojo orderWithUpdates
     * */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Transactional
    @Path(CUSTOMER_ORDERS_RESOURCE_PATH)
    public Response updateOrderForCustomer(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, OrderPojo orderWithUpdates) {
        servletContext.log("try to update specific order " + id);
        Response resp = null;
        try {
            customerServiceBean.updateOrderFor(id, orderWithUpdates);
            resp = Response.status(OK).entity(orderWithUpdates).build();
        }catch(Exception e) {
            resp = Response.status(BAD_REQUEST).entity(resp).build();
        }
        return resp;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     * */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path(CUSTOMER_ORDERS_RESOURCE_PATH)
    public Response deleteOrdersById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to delete specific order " + id);
        Response resp = null;
        List<OrderPojo> orders = null;
        try {
            orders = customerServiceBean.getOrderFor(id);
            customerServiceBean.removeOrder(orders);
            resp = Response.status(OK).entity(orders).build();
        }catch(Exception e) {
            resp = Response.status(BAD_REQUEST).entity(orders).build();
        }
        return resp;
    }

    // Task Two - Relationship between SecurityUser and Customer
    /*
    @RolesAllowed({USER_ROLE})
    @GET
    @Path("{userId}")
    public Response getByUserId(@PathParam("userId")
        int userId) {
        Response response = null;
        WrappingCallerPrincipal wCallerPrincipal =
            (WrappingCallerPrincipal)sc.getCallerPrincipal();
        SecurityUser sUser =
            (SecurityUser)wCallerPrincipal.getWrapped();
        CustomerPojo c = sUser.getCustomer();
        if (c.getId() != userId) {
            throw new ForbiddenException(
            "User trying to access resource it does not own" +
            "(wrong userid)");
        }
     // ...
     return response;
     }
     */

}