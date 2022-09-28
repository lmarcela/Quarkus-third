package org.marce.resteasyjackson;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.smallrye.mutiny.Uni;
import io.vertx.core.json.JsonArray;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.mutiny.core.Vertx;
import io.vertx.mutiny.ext.web.client.WebClient;
import lombok.extern.slf4j.Slf4j;
import org.marce.entities.Customer;
import org.marce.entities.Product;
import org.marce.repositories.CustomerRepository;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Path("/customer")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CustomerApi {
    @Inject
    CustomerRepository pr;

    @Inject
    Vertx vertx;

    private WebClient webClient;

    @PostConstruct
    void initialize(){
        this.webClient=WebClient.create(vertx,
                new WebClientOptions().setDefaultHost("localhost")
                        .setDefaultPort(8081).setSsl(false).setTrustAll(true));
    }


    @GET
    public List<Customer> list() {
        return pr.listCustomer();
    }

    @GET
    @Path("/{Id}")
    public Customer getById(@QueryParam("Id") Long Id) {
        return pr.findCustomer(Id);
    }

    @GET
    @Path("/{Id}/product")
    public Customer getByIdProduct(@QueryParam("Id") Long Id) {
        return null;
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

    private Uni<Customer> getCustomerReactive(Long Id){
        Customer customer = pr.findCustomer(Id);
        Uni<Customer> item = Uni.createFrom().item(customer);
        return item;
    }
    private Uni<List<Product>> getAllProducts(){
        return webClient.get(8081, "localhost", "/product").send()
                .onFailure().invoke(res -> log.error("Error recuperando productos ", res))
                .onItem().transform(res -> {
                    List<Product> lista = new ArrayList<>();
                    JsonArray objects = res.bodyAsJsonArray();
                    objects.forEach(p -> {
                        log.info("See Objects: " + objects);
                        ObjectMapper objectMapper = new ObjectMapper();
                        // Pass JSON string and the POJO class
                        Product product = null;
                        try {
                            product = objectMapper.readValue(p.toString(), Product.class);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                        lista.add(product);
                    });
                    return lista;
                });
    }


}
