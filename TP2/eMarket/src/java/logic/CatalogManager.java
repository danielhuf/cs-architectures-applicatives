/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import jakarta.inject.Named;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.ArrayList;
import model.Product;
import facade.ProductFacade;
import jakarta.annotation.PostConstruct;
import jakarta.ejb.EJB;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;

@Named
@ApplicationScoped
public class CatalogManager implements java.io.Serializable{
    private ArrayList<Product> products;
    private Integer newProductId;
    private String newProductName;
    private Double newProductPrice;
    private String newProductImage;
    @EJB
    private ProductFacade productFacade;  
    
    public CatalogManager() {
        this.products = new ArrayList<Product>();
    }

    public ArrayList<Product> getProducts() {
        return products;
    }
    
    public void setProducts(ArrayList<Product> products) {
        this.products = products;
    }
    
    public Integer getNewProductId(){
        return newProductId;
    }
    
    public void setNewProductId(Integer i){
       this.newProductId = i;
    }
    
    public String getNewProductName(){
        return newProductName;
    }
    
    public void setNewProductName(String n){
       this.newProductName = n;
    }
    
    public Double getNewProductPrice(){
        return newProductPrice;
    }
    
    public void setNewProductPrice(Double p){
       this.newProductPrice = p;
    }
    
    public String getNewProductImage(){
        return newProductImage;
    }
    
    public void setNewProductImage(String im){
       this.newProductImage = im;
    }
    
    @PostConstruct
    public void initCatalog() {
//        Product p1 = new Product(1, "Potato", 3.0, "https://pngfre.com/wp-content/uploads/Potato-10-2.png");
//        Product p2 = new Product(2, "Cookie", 2.50, "https://ogourmets-snacking.fr/wp-content/uploads/2023/09/cookie.png");
//        this.products.add(p1);
//        this.products.add(p2);
        products.addAll(productFacade.findAll());
    }
    
    public String createProduct(){
        Product p;
        if (newProductImage != null && !newProductImage.isEmpty()){
            p = new Product(newProductId, newProductName, newProductPrice, newProductImage);
        } else {
            p = new Product(newProductId, newProductName, newProductPrice, "https://www.mon-site-bug.fr/uploads/products/default-product.png");
        }
        
        try {
            this.productFacade.create(p);
            this.products.add(p);
            return "goToCatalog";
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "Error: This product ID already exists.", "Cannot create product."));
            return null; 
        }
    }
}