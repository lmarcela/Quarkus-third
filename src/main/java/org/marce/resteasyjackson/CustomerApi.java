package org.marce.resteasyjackson;


import org.marce.entities.Customer;
import org.marce.repositories.CustomerRepository;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;


@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerApi {
 @Inject
 CustomerRepository pr;

    @GET
    public List<Customer> list() {
        return pr.listCustomer();
    }

    @GET
    @Path("/{Id}")
    public Customer getById(@QueryParam("Id") Long Id) {
        return pr.findCustomer(Id);
    }

    @POST
    public Response add(Customer c) {
        c.getProducts().forEach(p-> p.setCustomer(c));
        pr.createdCustomer(c);
        return Response.ok().build();
    }

    @DELETE
    @Path("/{Id}")
    public Response delete(@QueryParam("Id") Long Id) {
        Customer customer = pr.findCustomer(Id);
        pr.deleteCustomer(customer);
        return Response.ok().build();
    }
    @PUT
    public Response update(Customer p) {
        Customer customer = pr.findCustomer(p.getId());
        customer.setCode(p.getCode());
        customer.setAccountNumber(p.getAccountNumber());
        customer.setSurname(p.getSurname());
        customer.setPhone(p.getPhone());
        customer.setAddress(p.getAddress());
        customer.setProducts(p.getProducts());
        pr.updateCustomer(customer);
        return Response.ok().build();
    }


}
