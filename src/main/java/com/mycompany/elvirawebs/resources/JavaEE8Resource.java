package com.mycompany.elvirawebs.resources;

import elvira.Graph;

import javax.ws.rs.GET;
import javax.ws.rs.Path;




import elvira.Bnet;
import java.util.Random;
import java.util.Vector;
import elvira.Evidence;
import elvira.Link;
import elvira.LinkList;
import elvira.Node;
import elvira.NodeList;
import elvira.inference.clustering.HuginPropagation;

import elvira.inference.elimination.VariableElimination;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;


import com.google.gson.Gson;
import elvira.FiniteStates;
import elvira.Relation;
import elvira.potential.PotentialTable;


/*
Nodo

{"id":"dsdas", "estados":"computer/science","relevance":5.0,"datos":"0.25/0.75"}

Nodos

[{"id":"dsdas", "estados":"computer/science","relevance":5.0,"datos":"0.25/0.75"},{"id":"dsdas", "estados":"computer/ok",
"relevance":5.0,"datos":"0.25/0.122"}]

Link
{"tail":"aa", "head":"computer/sss"}

Links
[{"tail":"aa", "head":"computer/sss"},{"tail":"ab", "head":"computer/sss"},{"tail":"aa", "head":"computer/sss"}]

Graph
{"nodeList":[{"id":"dsdas", "estados":"computer/science","relevance":5.0,"datos":"0.25/0.75"},{"id":"dsdas", "estados":"computer/ok",
"relevance":5.0,"datos":"0.25/0.122"}],
"linkList":[{"tail":"aa", "head":"computer/sss"},{"tail":"ab", "head":"computer/sss"},{"tail":"aa", "head":"computer/sss"}]}

*/
 


/**
 *
 * @author 
 */
@Path("elvira")
public class JavaEE8Resource {
    
    public JavaEE8Resource(){
        System.out.println("Iniciando estructura----------------");
    }
    
    
    @GET
    @Produces("application/json")
    @Path("activo")
    public String activo(){
      
        return "El programa esta UP!! realiza las peticiones que quieras";
    }
    
    @POST
    @Path("graph1")    
    public String Graph(String nodo){
       Gson gson =new Gson();
        GraphAux aux= gson.fromJson(nodo, GraphAux.class);
        

        Graph grafo = aux.toGraph();
        Bnet bnet=new Bnet();
        bnet.setName(grafo.getName()); 
        bnet.setNodeList (grafo.getNodeList()); 
        bnet.setLinkList (grafo.getLinkList());
        bnet.setKindOfGraph(grafo.getKindOfGraph());

        
        //super(generator,numberOfNodes,nParents,con);
        PotentialTable potentialTable;
        Relation relation;
        NodeList pa, nodes;
        FiniteStates node;
        Vector states;
        int i;
        NodoAux[] nodosmal =aux.getNodes();

        for (i=0 ; i< bnet.getNodeList().size() ; i++) {
            node = (FiniteStates)bnet.getNodeList().elementAt(i);
            states = bnet.exactStates(nodosmal[i].getNumEstados());
            node.setStates(states);
            //Crear un Vector de String y realizar setStates
            node.setTitle("");
            node.setComment("");
            node.setPosX(0);
            node.setPosY(0);
            node.setRelevance(nodosmal[i].getRelevance());
            node.setTypeOfVariable("finite-states");
            node.setKindOfNode("chance");
        }
        for (i=0 ; i< bnet.getNodeList().size() ; i++) {
            nodes = new NodeList();
            node = (FiniteStates)bnet.getNodeList().elementAt(i);
            nodes.insertNode(node);
            pa = bnet.parents(node);
            nodes.join(pa);
            relation = new Relation();
            relation.setVariables(nodes);
            relation.setKind(Relation.CONDITIONAL_PROB);
                //potentialTable = new PotentialTable(generator,nodes,degreeOfExtreme);
            potentialTable= new PotentialTable(nodes);
            potentialTable.setValues(nodosmal[i].getDatosDouble());
                //potentialTable.print();
            relation.setValues(potentialTable);
            bnet.getRelationList().addElement(relation);
        }
        
        
        bnet.setName("");
        bnet.setTitle("");
        bnet.setComment("");
        bnet.setAuthor("");
        bnet.setWhoChanged("");
        bnet.setWhenChanged("");
        bnet.setLocked(false);
        bnet.setVersion((float)1.0);
        return  compile(bnet,2);        

    }
   
    
    
    
    
    @POST
    @Path("graph")    
    public String prueba(String nodo){
        Gson gson =new Gson();
        GraphAux aux= gson.fromJson(nodo, GraphAux.class);
        

        Graph grafo = aux.toGraph();
        Bnet bnet=new Bnet();
        bnet.setName(grafo.getName()); 
        bnet.setNodeList (grafo.getNodeList()); 
        bnet.setLinkList (grafo.getLinkList());
        bnet.setKindOfGraph(grafo.getKindOfGraph());

        
        //super(generator,numberOfNodes,nParents,con);
        PotentialTable potentialTable;
        Relation relation;
        NodeList pa, nodes;
        FiniteStates node;
        Vector states;
        int i;
        NodoAux[] nodosmal =aux.getNodes();

        for (i=0 ; i< bnet.getNodeList().size() ; i++) {
            node = (FiniteStates)bnet.getNodeList().elementAt(i);
            states = bnet.exactStates(nodosmal[i].getNumEstados());
            node.setStates(states);
            //Crear un Vector de String y realizar setStates
            node.setTitle("");
            node.setComment("");
            node.setPosX(0);
            node.setPosY(0);
            node.setTypeOfVariable("finite-states");
            node.setKindOfNode("chance");
        }
        LinkList ll = bnet.getLinkList();
        for(i=0 ; i< ll.size() ; i++){
            Link l=ll.elementAt(i);
            Node head = l.getHead();
            head.addParent(l);
        }
        
        for (i=0 ; i< bnet.getNodeList().size() ; i++) {
            nodes = new NodeList();
            node = (FiniteStates)bnet.getNodeList().elementAt(i);
            nodes.insertNode(node);
            pa = bnet.parents(node);
            nodes.join(pa);
            relation = new Relation();
            relation.setVariables(nodes);
            relation.setKind(Relation.CONDITIONAL_PROB);
                //potentialTable = new PotentialTable(generator,nodes,degreeOfExtreme);
            potentialTable= new PotentialTable(nodes);
            potentialTable.setValues(nodosmal[i].getDatosDouble());
                //potentialTable.print();
            relation.setValues(potentialTable);
            bnet.getRelationList().addElement(relation);
        }
        
        bnet.setName("");
        bnet.setTitle("");
        bnet.setComment("");
        bnet.setAuthor("");
        bnet.setWhoChanged("");
        bnet.setWhenChanged("");
        bnet.setLocked(false);
        bnet.setVersion((float)1.0);
        
        //
        
        Vector rl = bnet.getRelationList();
        ResultadoLink resultadolink = new ResultadoLink();
        
        for(int m=0;m< rl.size();m++){
            Relation relacion =(Relation) rl.get(m);
            FiniteStates fs =(FiniteStates) relacion.getVariables().firstElement();
            int estados = fs.getNumStates();
            //if(m==1) return relacion.getVariables().size()+" ";
            if(relacion.getVariables().size()== 1){
                PotentialTable p=(PotentialTable)relacion.getValues();
                double[] valores =p.getValues();
                double[][] matrizfinal=new double[valores.length][1];
                for(int t=0;t< valores.length;t++){
                    matrizfinal[t][0]=valores[t];
                }
                resultadolink.add(sumarFilas(matrizfinal,fs.getName(),1,estados));
            }
        }
        
        for(int m=0;m< rl.size();m++){
            
            Relation relacion =(Relation) rl.get(m);
            FiniteStates fs =(FiniteStates) relacion.getVariables().firstElement();
            int estados = fs.getNumStates();
            //if(m==1) return relacion.getVariables().size()+" ";
            
            if(relacion.getVariables().size()== 1){
                
            }else{
              
                Vector<double[]> indices= new Vector();
                NodeList padres = relacion.getVariables();
                
                pa = bnet.parents(fs);
                
                //if(m==0) return info(bnet);
                NodeList padre= relacion.getVariables();
                
                for(int k=1; k< padre.size(); k++){
                    indices.add(resultadolink.getbyId(padre.elementAt(k).getName()).getDatos());  
                }

                //if(m==0) return info(bnet);

                int[] num= new int[indices.size()];
                for(int h=0; h< indices.size();h++){
                    num[h]=indices.elementAt(h).length;
                }
               
                int columnas=columnas(num);
                
                PotentialTable p=(PotentialTable)relacion.getValues();
                double[] valores = p.getValues();

                double[][] matriz = crearMatriz(columnas,estados,valores);          
                double[] vector = crearVector(indices,columnas);
                
                double[][] matrizfinal=calculo(matriz,vector, estados,columnas);
              
                
                resultadolink.add(sumarFilas(matrizfinal,fs.getName(),columnas,estados));
                }
        }
        
        
        
        return resultadolink.toString();
        
    }
    
    public Resultado sumarFilas(double[][] matriz, String id, int columnas, int estados){
        double[] datos=new double[estados];
 
       
        for(int i=0; i< estados;i++){
            for(int l=0; l< columnas;l++){
                datos[i]+=matriz[i][l];
            }
        }
        return new Resultado(id,datos);
    }
    
    public double[] crearVector(Vector<double[]> indices, int total){
        double[] vector = new double[total];
        for(int i=0; i< vector.length; i++){
            vector[i]=1;
        }

        int sumatorio=1;
        int lugar=0;
        for(int i=indices.size()-1;i>=0;i--){
            for(int vec=0; vec<total;vec++){
                for(int k=0; k< sumatorio;k++){          
                    vector[vec+k]*=indices.elementAt(i)[lugar];
                }
                lugar++;
                if(lugar==indices.elementAt(i).length)lugar=0;
                
                vec+=sumatorio-1;
            }
            lugar=0;
            
            sumatorio=sumatorio*indices.elementAt(i).length;
        }
        
        
        return vector;
    }
    public double[][] crearMatriz(int columnas, int estados,double[] valores){
        double[][] matriz= new double[estados][columnas];
        int a=0;
        for(int i=0; i< estados; i++){
            for(int j=0; j< columnas; j++){
                matriz[i][j]=valores[a];
                a++;
            }
        }
        return matriz;
    }
    
    
    public int[] actualizar(int[] num, int[] aux, int lugar){
        aux[lugar]++;
        if(aux[lugar] == num[lugar]){
            return actualizar(num,aux,lugar-1);
        }
        return aux;
    }
    
    public int columnas(int[] num){
        int total=1;
        for(int i=0; i< num.length; i++){
            total*=num[i];
        }
        return total;
    }
    
    public double[][] calculo(double[][] matriz,double[] vector, int estados,int columnas){
        for(int j = 0; j<estados ;j++){
            for(int i = 0; i<columnas ;i++){
                matriz[j][i]=matriz[j][i]*vector[i]; 
            }
        }
        return matriz;
    }
    
    
    
    
    
    
    
    
    
    
    public Resultado validacion(Relation r, ResultadoLink resulink){
            NodeList componentes =r.getVariables();
            if(componentes.size()==1){//No tiene padres, marcar como visitado
                return validacionHuerfano(r);
            }else{//Tiene padres y hay que hacer la validación de ellos
                if(componentesOK(componentes)){
                    return validacionRelacion(r,resulink);
                }else
                    return null;
                //Validación de otros nodos
            }
    }
     
    public Resultado validacionHuerfano(Relation relation){
        // guardar resultado
       
        relation.getVariables().firstElement().setMarked(true);
        PotentialTable p=(PotentialTable)relation.getValues();
        return new Resultado(relation.getVariables().firstElement().getName(),p.getValues());
    }
   
    public Resultado validacionRelacion(Relation relation, ResultadoLink resulink){
        FiniteStates fs =(FiniteStates) relation.getVariables().firstElement();
        int estados=fs.getNumStates();
        //Las cuentas que se hacen y guardar resultado
        double[] finale=new double[estados];
        PotentialTable p=(PotentialTable)relation.getValues();
        double[] tusDatos = p.getValues();
        Vector aux = new Vector();
        NodeList nl = relation.getVariables();
        for(Node n: nl.getNodes()){
           double[] separar = resulink.getbyId(n.getName()).getDatos();
           for(int i= 0; i< separar.length;i++){
                aux.add(separar[i]);
           }
        }
        int limite=fs.getNumStates()* aux.size();
        for(int i= 0; i< limite;i++){
            tusDatos[i]=tusDatos[i] * (double) aux.elementAt(i%fs.getNumStates());
        }
        
        for(int j= 0; j< aux.size();j++){
          for(int i= 0; i< fs.getNumStates();i++){
            finale[i]=tusDatos[i+j];
          }  
        }
        
       relation.getVariables().firstElement().setMarked(true);
       return new Resultado(relation.getVariables().firstElement().getName(),finale);
    }
  
    public boolean componentesOK(NodeList componentes){
        for(int j=1;j<componentes.size();j++){
            if(componentes.elementAt(j).getMarked()){
                return false;
            }
        }
        return true;
    }
    
    
    
    @GET
    @Path("random")
    public String random(){
        Random rnd = new Random();
        /*Random generator, int numberOfNodes, double nParents,
    double nStates, boolean con, int degreeOfExtreme,
    double approximation*/
        Bnet bnet =new Bnet(rnd, 3, 1.0,2.0, true, 2);
        //Vector parameters = null;
        //Vector filesNames= null;
        //Vector abductiveValues=null;
        //bnet.compile(id,parameters,filesNames,abductiveValues);
        //return "ha pasado";
        //return info(bnet);
        //return  compile(bnet,2);
        return  compile(bnet,2) + "\n"+ info(bnet);

    }
    
    
    public String info(Bnet bnet){
        String fin="";
        for(Object r: bnet.getRelationList()){
            fin+=r.toString()+"\n";
        }
        
        fin+="Nodos: \n";
        for(Node v: bnet.getNodeList().getNodes()){
            fin+=v.getName()+"/"+v.getKind()+"/"+ v.getType()+" "+v.toShowIndependent()+ "\n";
        }

        /*for(Object n : bnet.getNodeList().getNodes()){
            fin+=n.toString()+"\n";
        }*/
        for(Object l : bnet.getLinkList().getLinks()){
            fin+=l.toString()+"\n";
        }
        Link ll=(Link) bnet.getLinkList().getLinks().elementAt(0);
        ll.getHead();
        fin+="Head :" + ll.getHead().getName();
        return fin;
    }
    
    
    
      public String compile(Bnet bnet, int index) {
        
        Evidence e = new Evidence();

        //return veWithPT.getResults();
        //Parece que funciona 0,1,3,
        
       switch (index) {
            case 0: HuginPropagation hp = new HuginPropagation(bnet,e,"tables");
            hp.obtainInterest();
            hp.propagate(hp.getJoinTree().elementAt(0),"no");
            //return "OK";
            return hp.getResults().toString();
            case 1: HuginPropagation hp2 = new HuginPropagation(bnet,e,"trees");
            hp2.obtainInterest();
            hp2.propagate(hp2.getJoinTree().elementAt(0),"no");
            
            return hp2.getResults().toString();


            case 2: VariableElimination ve = new VariableElimination(bnet,e);
            //ve.setInterest(bnet.getNodeList());
            ve.obtainInterest();
            //void setCurrentRelations(Vector relations)
            ve.propagate();
            //return "OK";
            return ve.getResults().toString();
        }
        bnet.setCompiled(true);
        return null;
    }
    
}
