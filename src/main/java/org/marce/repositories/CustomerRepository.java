package org.marce.repositories;

import com.blazebit.persistence.CriteriaBuilder;
import com.blazebit.persistence.CriteriaBuilderFactory;
import com.blazebit.persistence.view.EntityViewManager;
import com.blazebit.persistence.view.EntityViewSetting;
import org.marce.entities.Customer;
import org.marce.entities.CustomerView;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

@ApplicationScoped
public class CustomerRepository {
    @Inject
    EntityManager em;
    @Inject
    CriteriaBuilderFactory cbf;
    @Inject
    EntityViewManager evm;

    @Transactional
    public void createdCustomer(Customer p){
        em.persist(p);
    }

    @Transactional
    public void deleteCustomer(Customer p){
        em.remove(em.contains(p) ? p : em.merge(p));
    }

    @Transactional
    //public List<Customer> listCustomer(){
    public List<CustomerView> listCustomer(){
        //List<Customer> customers = em.createQuery("select p from Customer p").getResultList();
        CriteriaBuilder<Customer> cb=cbf.create(em,Customer.class);
        List<CustomerView> customers = evm.applySetting(EntityViewSetting.create(CustomerView.class),cb).getResultList();
        return customers;
    }
    @Transactional
    public Customer findCustomer(Long Id){
        return em.find(Customer.class, Id);
    }
    @Transactional
    public void updateCustomer(Customer p){
        em.merge(p);
    }
}
