/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.elvirawebs.resources;


/**
 *
 * @author sergio
 */
public class Resultado{
    private String id;
    private double[] datos;
    
    Resultado(String id,double[] datos){
        this.id=id;
        this.datos=datos;
    }
    public double[] getDatos(){
        return datos;
    }
    public String getId(){
        return id;
    }
    public void setDatos(double[] datos){
        this.datos =datos;
    }
    public void setId(String id){
        this.id=id;
    }
    public String toString(){
        String cadena="";
        for(int i = 0; i< datos.length;i++){
            if(i==datos.length-1)cadena += datos[i];
            else cadena += datos[i] +"/";
        }
        return id +":"+ cadena +"\n";
    }
    
}
