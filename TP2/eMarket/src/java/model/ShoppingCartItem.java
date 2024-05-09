/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

public class ShoppingCartItem implements java.io.Serializable {
    private Integer id;
    private Integer quantity;
    private Product product;
    
    public ShoppingCartItem() {
    }
    
    public ShoppingCartItem(Integer i, Integer q, Product p) {
        this.id = i;
        this.quantity = q;
        this.product = p;
    }

    public void setId(Integer i) {
        this.id = i;
    }
    
    public void setQuantity(Integer q) {
        this.quantity = q;
    }
    
    public void setProduct(Product p) {
        this.product = p;
    }
    
    public Integer getId() {
        return this.id;
    }
        
    public Integer getQuantity() {
        return this.quantity;
    }

    public Product getProduct() {
        return this.product;
    }
}