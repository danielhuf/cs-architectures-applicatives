/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package logic;

import jakarta.inject.Named;
import jakarta.enterprise.context.SessionScoped;
import java.util.ArrayList;
import model.ShoppingCartItem;
import model.Product;
import jakarta.annotation.PostConstruct;
import java.util.Objects;

@Named
@SessionScoped
public class ShoppingCartManager implements java.io.Serializable{
    private ArrayList<ShoppingCartItem> shoppingCartItems;
    private Product prodToAdd;
    
    public ShoppingCartManager() {
        this.shoppingCartItems = new ArrayList<ShoppingCartItem>();
    }

    public ArrayList<ShoppingCartItem> getShoppingCartItems() {
        return shoppingCartItems;
    }
    
    public void setShoppingCartItems(ArrayList<ShoppingCartItem> items) {
        this.shoppingCartItems = items;
    }
    
    public Product getProdToAdd(){
        return prodToAdd;
    }
    
    public void setProdToAdd(Product p) {
        this.prodToAdd = p;
    }
    
    @PostConstruct
    public void initCart() {
        Product p1 = new Product(1, "PC configuration de base", 400.0, "https://static.fnac-static.com/multimedia/Images/FR/MDM/bd/a1/ae/11444669/1540-1.jpg");
        Product p2 = new Product(4, "MacBook configuration performance", 1000.0, "https://www.apple.com/v/mac/home/by/images/overview/select/product_tile_imac_24__inq0od011wuq_large_2x.png");
        ShoppingCartItem i1 = new ShoppingCartItem(1, 2, p1);
        ShoppingCartItem i2 = new ShoppingCartItem(2, 1, p2);
        this.shoppingCartItems.add(i1);
        this.shoppingCartItems.add(i2);
    }
    
    public String addToCart(){
        boolean itemFound = false;
        Integer newId = 1;
        for (ShoppingCartItem item : this.shoppingCartItems) {
            newId++;
            if (Objects.equals(item.getProduct().getId(), this.prodToAdd.getId())) {
                item.setQuantity(item.getQuantity() + 1);
                itemFound = true;
                break;
            }
        }
        // If the product was not found in the list, add a new item.
        if (!itemFound) {
            ShoppingCartItem i = new ShoppingCartItem(newId, 1, this.prodToAdd);
            this.shoppingCartItems.add(i);
        }
        return "goToShoppingCart";
    }
    
    public double getTotalPrice() {
        double totalPrice = 0.0;
        for (ShoppingCartItem item : shoppingCartItems) {
            totalPrice += item.getProduct().getPrice() * item.getQuantity();
        }
        return totalPrice;
    }
    
    public void removeFromCart(ShoppingCartItem item) {
        shoppingCartItems.removeIf(i -> i.getId().equals(item.getId()));
    }   
    
    public void increaseQuantity(ShoppingCartItem item) {
        item.setQuantity(item.getQuantity() + 1);
    }

    public void decreaseQuantity(ShoppingCartItem item) {
        if (item.getQuantity() > 1) {
            item.setQuantity(item.getQuantity() - 1);
        } else {
            removeFromCart(item);
        }
    }
}