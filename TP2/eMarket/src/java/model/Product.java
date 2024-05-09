/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.Id;

@Entity
@Table( name="PRODUCT" )
public class Product implements java.io.Serializable{
    @Id 
    @Column( name = "ID" )
    private Integer id;
    @Column( name = "NAME" )
    private String name;
    @Column( name = "SELLING_PRICE" )
    private Double price;
    @Column( name = "IMAGE" )
    private String image;
    
    public Product() {
    }
    
    public Product(Integer i, String n, Double p, String im) {
        id = i;
        name = n;
        price = p;
        image = im;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public Integer getId() {
        return id;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getName() {
        return name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }
    public Double getPrice() {
        return price;
    }
    
    public void setImage(String im) {
        this.image = im;
    }
    public String getImage() {
        return image;
    }
}