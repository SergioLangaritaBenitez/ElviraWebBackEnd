/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.elvirawebs.resources;

import java.util.Vector;


/**
 *
 * @author sergio
 */
public class ResultadoLink{
    private Vector<Resultado> resultados;
       
    
    ResultadoLink(){
        resultados= new Vector<Resultado>();
    }
    public Vector getResultados(){
        return resultados;
    }
    public Resultado elementAt(int i){
        return resultados.elementAt(i);
    }
    public void add(Resultado r){
        resultados.add(r);
    }
    public int size(){
        return resultados.size();
    }
    public Resultado getbyId( String id){
        if(resultados.size() > 0){
            for(int i= 0; i <resultados.size();i++){
                if(resultados.elementAt(i).getId().equals(id)){
                    return resultados.elementAt(i);
                }
            }
        }
        return null;
     
    }
    public String toString(){
        String cadena="";
        for(int i = 0;i < resultados.size();i++){
            cadena+=resultados.elementAt(i);
        }
        return cadena;
    }
   
}
