/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.elvirawebs.resources;

import elvira.Link;
import elvira.Node;
import static elvira.Node.CHANCE;
import static elvira.Node.FINITE_STATES;

/**
 *
 * @author sergio
 */
public class LinkAux {
    private String tail;
    private String head;
    
    public LinkAux(String tail,String head){
        this.tail = tail;      
        this.head = head;   
    }
    
    public String getTail(){
        return tail;
    }
    public void setTail(String tail){
        this.tail=tail;
    }
    
    public String getHead(){
        return head;
    }
    public void setHead(String head){
        this.head=head;
    }
    
    public Link toLink(Node t, Node h){
        return new Link (t,h);
    }
}
