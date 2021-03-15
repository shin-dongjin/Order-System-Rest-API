/*****************************************************************c******************o*******v******id********
 * File: ProductResource.java
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
import static com.algonquincollege.cst8277.utils.MyConstants.PRODUCT_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utils.MyConstants.RESOURCE_PATH_ID_PATH;
import static javax.ws.rs.core.Response.Status.BAD_REQUEST;
import static javax.ws.rs.core.Response.Status.OK;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.algonquincollege.cst8277.ejb.CustomerService;
import com.algonquincollege.cst8277.models.ProductPojo;
import com.algonquincollege.cst8277.models.StorePojo;

@Path(PRODUCT_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProductResource {
    /** CustomerService customerServiceBean */
    @EJB
    protected CustomerService customerServiceBean;
    /** ServletContext servletContext */
    @Inject
    protected ServletContext servletContext;
    /**
     * @return Response
     */
    @GET
    @PermitAll // Task 4 - Any user can retrieve the list of Products and Stores
    public Response getProducts() {
        servletContext.log("retrieving all products ...");
        List<ProductPojo> custs = customerServiceBean.getAllProducts();
        Response response = Response.ok(custs).build();
        return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     */
    @GET
    @Path("{id}")
    public Response getProductById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to retrieve specific product " + id);
        ProductPojo theProduct = customerServiceBean.getProductById(id);
        Response response = Response.ok(theProduct).build();
        return response;
    }
    
    // TODO - Add new products
    /**
     * @return Response
     * @param ProductPojo newProduct
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    @Transactional
    public Response addProduct(ProductPojo newProduct) {
        Response resp = null;
        ProductPojo newProductWithIdTimestamps = customerServiceBean.persistProduct(newProduct);
        customerServiceBean.persistProduct(newProductWithIdTimestamps);
        resp = Response.ok(newProductWithIdTimestamps).build();
        return resp;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("{id}")
    public Response deleteProductById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to delete specific product " + id);
        Response resp = null;
        ProductPojo prod = null;
        try {
            prod = customerServiceBean.getProductById(id);
            customerServiceBean.removeProduct(prod);
            resp = Response.status(OK).entity(prod).build();
        }catch(Exception e) {
            resp = Response.status(BAD_REQUEST).entity(prod).build();
        }
        return resp;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id, ProductPojo productWithUpdates
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Transactional
    @Path("{id}")
    public Response updateProduct(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, ProductPojo productWithUpdates) {
        servletContext.log("try to update specific id " + id);
        Response resp = null;
        ProductPojo prod = null;
        try {
            prod = customerServiceBean.getProductById(id);
            customerServiceBean.updateProduct(prod, productWithUpdates);
            resp = Response.status(OK).entity(productWithUpdates).build();
        }catch(Exception e) {
            resp = Response.status(BAD_REQUEST).entity(prod).build();
        }
        return resp;
    }

}