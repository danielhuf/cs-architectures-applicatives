/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package facade;

import jakarta.ejb.Stateless;
import model.Product ;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Stateless
public class ProductFacade extends AbstractFacade<Product> {
    @PersistenceContext( unitName = "eMarket_warPU")
    private EntityManager em ;
    
    @Override
    protected EntityManager getEntityManager(){
        return em;
    }
    
    public ProductFacade() {
        super(Product.class);
    }  
}
