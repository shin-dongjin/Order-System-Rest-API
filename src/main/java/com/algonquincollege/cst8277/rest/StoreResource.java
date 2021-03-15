/*****************************************************************c******************o*******v******id********
 * File: StoreResource.java
 * Course materials (20F) CST 8277
 *
 * @author (original) Mike Norman
 * 
 * update by : Zhicheng Liu 040918057, Dongjin Shin 040923498, Cameron Harvey 040745918
 *
 */
package com.algonquincollege.cst8277.rest;


import static com.algonquincollege.cst8277.utils.MyConstants.ADMIN_ROLE;
import static com.algonquincollege.cst8277.utils.MyConstants.PRODUCT_RESOURCE_NAME;
import static com.algonquincollege.cst8277.utils.MyConstants.RESOURCE_PATH_ID_ELEMENT;
import static com.algonquincollege.cst8277.utils.MyConstants.RESOURCE_PATH_ID_PATH;
import static com.algonquincollege.cst8277.utils.MyConstants.STORE_RESOURCE_NAME;
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
import com.algonquincollege.cst8277.models.StorePojo;

@Path(STORE_RESOURCE_NAME)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class StoreResource {
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
    public Response getStores() {
        servletContext.log("retrieving all stores ...");
        List<StorePojo> stores = customerServiceBean.getAllStores();
        Response response = Response.ok(stores).build();
        return response;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     */
    @GET
    @Path("{id}")
    public Response getStoreById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to retrieve specific store " + id);
        StorePojo theStore = customerServiceBean.getStoreById(id);
        Response response = Response.ok(theStore).build();
        return response;
    }
    
    /**
     * @return Response
     * @param StorePojo newStore
     */
    @POST
    @RolesAllowed({ADMIN_ROLE})
    @Transactional
    public Response addStore(StorePojo newStore) {
        Response resp = null;
        StorePojo newStoreWithIdTimestamps = customerServiceBean.persistStore(newStore);
        customerServiceBean.persistStore(newStoreWithIdTimestamps);
        resp = Response.ok(newStoreWithIdTimestamps).build();
        return resp;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id
     */
    @DELETE
    @RolesAllowed({ADMIN_ROLE})
    @Path("{id}")
    public Response deleteStoreById(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id) {
        servletContext.log("try to delete specific store " + id);
        Response resp = null;
        StorePojo store = null;
        try {
            store = customerServiceBean.getStoreById(id);
            customerServiceBean.removeStore(store);
            resp = Response.status(OK).entity(store).build();
        }catch(Exception e) {
            resp = Response.status(BAD_REQUEST).entity(store).build();
        }
        return resp;
    }
    /**
     * @return Response
     * @param @PathParam(RESOURCE_PATH_ID_ELEMENT) int id, StorePojo updatedStore
     */
    @PUT
    @RolesAllowed({ADMIN_ROLE})
    @Transactional
    @Path("{id}")
    public Response updateStore(@PathParam(RESOURCE_PATH_ID_ELEMENT) int id, StorePojo updatedStore) {
        servletContext.log("try to update specific id " + id);
        Response resp = null;
        StorePojo store = null;
        try {
            store = customerServiceBean.getStoreById(id);
            customerServiceBean.updateStore(store, updatedStore);
            resp = Response.status(OK).entity(updatedStore).build();
        }catch(Exception e) {
            resp = Response.status(BAD_REQUEST).entity(store).build();
        }
        return resp;
    }


}