/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.elvirawebs.resources;

import elvira.Graph;
import elvira.Link;
import elvira.LinkList;
import elvira.Node;
import elvira.NodeList;
import elvira.potential.Potential;
import elvira.potential.PotentialTree;
import java.util.Vector;

/**
 *
 * @author sergio
 */
public class GraphAux {
    protected NodoAux[] nodeList;
    protected LinkAux[] linkList;
    
    public GraphAux(NodoAux[] nodeList,LinkAux[] linkList){
        this.nodeList=nodeList;
        this.linkList=linkList;
    }
    
    public Graph toGraph(){
        Graph grafo= new Graph();
        for(NodoAux f:nodeList ){
            grafo.getNodeList().insertNode(f.toNode());
        }
        for(LinkAux f:linkList ){
            Node nodoTail= grafo.getNodeList().getNode(f.getTail());
            Node nodoHead=grafo.getNodeList().getNode(f.getHead());
            
            Link elLink=f.toLink(nodoTail,nodoHead);
            grafo.getLinkList().insertLink(elLink);

            //Añadir en el nodo tail
            nodoHead.getChildren().insertLink(elLink);
            //Añadir en en nodo head
            nodoTail.getParents().insertLink(elLink);

        }
 
        
        return grafo;
    }
      public NodoAux[] getNodes(){
        return nodeList;
      }
}
