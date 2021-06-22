/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.elvirawebs.resources;

import elvira.FiniteStates;
import elvira.LinkList;
import elvira.Node;
import static elvira.Node.CHANCE;
import static elvira.Node.FINITE_STATES;
import java.io.Serializable;
import java.util.Hashtable;

/**
 *
 * @author sergio
 */
public class NodoAux{
    private String id;
    private double relevance = 7.0;
    private String estados;
    private String datos;
    
    /*protected LinkList children;
    protected LinkList parents;
    protected LinkList siblings;*/
    /*
    public static final String typeNames[]={"continuous","finite-states","infinite-discrete","mixed"};
    public static final String kindNames[]= {"chance","decision","utility","super-value"};*/
    
    public NodoAux(String id){
        this.id=id;
    }
    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id=id;
    }
    
    public String getDatos(){
        return datos;
    }
  public void setDatos(String datos){
        this.datos=datos;
    }
    
    public Double getRelevance(){
        return relevance;
    }
    public void setRelevance(Double relevance){
        this.relevance=relevance;
    }
    
  
    public Node toNode(){
        FiniteStates nodo = new FiniteStates();
        nodo.setName(this.id);
        nodo.setRelevance(this.relevance);
        nodo.setNumStates(this.getNumEstados());
        /*String[] estado=estados.split("/");
        String[] dato=datos.split("/");
        for(int i=0; i < estado.length;i++){
            nodo.putProperty(estado[i],dato[i]);
        }*/
        return nodo;
    }
     public int getNumEstados(){
        String[] estado=estados.split("/");
        return estado.length;
    }
     public double[] getDatosDouble(){
        String[] dato=datos.split("/");
        double[] resultado=new double[dato.length];
        for(int i=0; i < dato.length;i++){
            resultado[i]=Double.parseDouble(dato[i]);
        }
           
        
         //Vector vars
                
          //      addRelation()
        return resultado;
    }
}
