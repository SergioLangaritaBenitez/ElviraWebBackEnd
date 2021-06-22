/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.elvirawebs.resources;

import java.io.Serializable;
/**
 *
 * @author sergio
 */
public class Prueba implements Serializable{
    public String id;
    public String title;

    public Prueba(String id,String title){
        this.id=id;
        this.title=title;
    }
    public String getId(){
        return id;
    }
    public String getTitle(){
        return title;
    }
}
