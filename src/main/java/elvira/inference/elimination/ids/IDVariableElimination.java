/* VariableElimination.java */
package elvira.inference.elimination.ids;

import java.util.Vector;
import java.io.*;
import elvira.*;
import elvira.inference.elimination.VariableElimination;
import elvira.parser.ParseException;
import elvira.potential.*;

/**
 * This class implements a generic variable elimination method of propagation.
 * The initial potentials can be of any kind, but
 * they must define the methods:
 * <ul>
 * <li> <code>Potential combine(Pontential combine)</code>
 * <li> <code>Potential addVariable(FiniteStates var)</code>
 * </ul>
 * This class can be extended for special requirements. The methods
 * that can be overloaded are:
 * <ul>
 * <li> <code><a href="#transformInitialRelation(Relation)">Relation transformInitialRelation(Relation r)</a></code>
 * <li> <code><a href="#transformAfterAdding(Potential)">Potential transformAfterAdding(Potential pot)</a></code>
 * <li> <code><a href="#transformAfterEliminating(Potential)">Potential transformAfterEliminating(Potential pot)</a></code>
 * <li> <code><a href="#transformAfterOperation(Potential)">Potential transformAfterOperation(Potential pot, boolean flag)</a></code>
 * <li> <code><a href="#combine(Potential, Potential)">Potential combine(Potential pot1,Potential pot2)</a></code>
 * <li> <code><a href="#addVariable(Potential, FiniteStates)">Potential addVariable(Potential pot,FiniteStates var)</a></code>
 * </ul>
 * @author Antonio Salmer�n (Antonio.Salmeron@ual.es)
 * @author Andr�s Cano (acu@decsai.ugr.es)
 * @see VEWithPotentialTree
 * @since 14/3/2001
 */
public class IDVariableElimination extends VariableElimination {
    /**
     * Maximum and minimum values reached during IDs evaluation
     */
    private double maximum;
    private double minimum;

    /**
     * To show if we want to use statistics about the evaluation
     * It is required to change this flag to use statistics
     */
    public boolean generateStatistics = true;

    /**
     * To show if we want to use debug information about the evaluation.
     * The information is related to the evaluation for Influence diagrams
     */
    public boolean generateDebugInformation = false;

    /**
     * Constructs a new propagation for a given Bayesian network and
     * some evidence.
     *
     * @param b a <code>Bnet</code>.
     * @param e the evidence.
     */
    public IDVariableElimination(Bnet b, Evidence e) {
        super(b,e);
    }

    /**
     * Constructs a new propagation for a given Bayesian network
     *
     * @param b a <code>Bnet</code>.
     */
    public IDVariableElimination(Bnet b) {
        super(b);
    }

    /**
     * Checks if an influence diagram has the properties required for being
     * evaluated
     * @return true or false
     */
    private boolean initialConditions() {
        boolean evaluable;
        IDiagram diag = (IDiagram) network;

        // Check if all the links are directed
        evaluable = diag.directedLinks();

        // Error if evaluable is false and return false
        if (evaluable == false) {
            System.out.print("Influence Diagram with no directed links\n\n");
            return (false);
        }

        // Check the presence of cycles
        evaluable = diag.hasCycles();
        if (evaluable == true) {
            System.out.print("Influence Diagram with cycles\n\n");
            return (false);
        }

        // Add non forgetting arcs
        diag.addNonForgettingArcs();

        // Check if there is a path linking all the decisions
        evaluable = diag.pathBetweenDecisions();
        if (evaluable == false) {
            System.out.print("Influence Diagram with non ordered decisions\n\n");
            return (false);
        }

        // Remove redundancy and barren nodes
        diag.eliminateRedundancy();

        // Transform the set of initial relations

        maximum = 0;
        minimum = 10000;
        currentRelations = getInitialRelations();

        // Return true
        return true;
    }

    /**
     * Method to return the value of maximum
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * Method to set the value for maximum
     * @param value to set
     */
    public void setMaximum(double value) {
        if (value > maximum) {
            maximum = value;
        }
    }

    /**
     * Method to return the value of minimum
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * Method to set the value for minimum
     * @param value to set
     */
    public void setMinimum(double value) {
        if (value < minimum) {
            minimum = value;
        }
    }

    /**
     * Program for performing experiments from the command line.
     * The command line arguments are as follows:
     * <ol>
     * <li> Input file: the network.
     * <li> Output file.
     * <li> Evidence file.
     * </ol>
     * If the evidence file is omitted, then no evidences are
     * considered.
     */
    public static void main(String args[]) throws ParseException, IOException {

        Network b;
        Evidence e;
        FileInputStream evidenceFile;
        IDVariableElimination ve;
        String base;
        int i;

        if (args.length < 2) {
            System.out.println("Too few arguments. Arguments are: ElviraFile OutputFile EvidenceFile");
        } else {
            // networkFile = new FileInputStream(args[0]);
            // b = new Bnet(networkFile);
            b = Network.read(args[0]);

            if (args.length == 3) {
                evidenceFile = new FileInputStream(args[2]);
                e = new Evidence(evidenceFile, b.getNodeList());
            } else {
                e = new Evidence();
            }

            ve = new IDVariableElimination((Bnet) b, e);
            ve.obtainInterest();

            // Compose the name for the file with the statistics

            if (ve.generateStatistics == true) {
                base = args[0].substring(0, args[0].lastIndexOf('.'));
                base = base.concat("_VariableElimination_data");
                ve.statistics.setFileName(base);
            }
            ve.propagate(args[1]);

        }
    }

    /**
     * Gets the expected utility tables for the decision nodes in
     * an influence diagram
     */
    private void getPosteriorDistributionsID() {
        NodeList notRemoved;
        Node x;
        RelationList rLtemp;
        Relation valueRel;
        PairTable pt = null;
        int i, j, s;
        Vector vars = null;
        String operation;
        String base;
        boolean combined;

        if (generateDebugInformation == true) {
            System.out.println("Entrando a metodo getPosteriorDistributionsID");
        }

        // Initializes the crono
        crono.start();

        // First at all, remove all results stored in results vector
        results.removeAllElements();

        // Insert in notRemoved all not observed nodes
        notRemoved = getNotObservedNodes();

        // Make a pair table
        pt = new PairTable((IDiagram) network, observations);

        // Consider the impact of observations on relations
        restrictCurrentRelationsToObservations();

        if (generateStatistics == true) {
            // Note down the data about the beginning of the evaluation
            statistics.addOperation("Start of evaluation: ");
            statistics.addSize(currentRelations.sumSizes());
            statistics.addTime(crono.getTime());
        }

        // Loop to eliminate the variables

        for (i = notRemoved.size(); i > 0; i--) {

            // Select next variable to remove

            x = pt.nextToRemoveID();

            if (generateStatistics == true) {
                // Store the operation

                statistics.addOperation("Variable elimination: " + x.getName());
            }

            // Delete this node of notRemoved and pairTable

            notRemoved.removeNode(x);
            pt.removeVariable((FiniteStates) x);

            // Combine the potentials of this node

            if (generateDebugInformation == true) {
                System.out.println("Variable a eliminar: " + x.getName());
            }

            if (x.getKindOfNode() == Node.CHANCE) {

                if (generateDebugInformation == true) {
                    System.out.println("A eliminar nodo de azar");
                }

                combinePotentialsToRemoveChanceNode((FiniteStates) x, pt);
            } else {
                if (x.getKindOfNode() == Node.DECISION) {

                    if (generateDebugInformation == true) {
                        System.out.println("A eliminar nodo de decision");
                    }

                    combinePotentialsToRemoveDecisionNode((FiniteStates) x, pt);
                }
            }

            if (generateStatistics == true) {
                // Store the size of the diagram at this moment

                statistics.addSize(currentRelations.sumSizes());
                statistics.addTime(crono.getTime());
            }
        } // end for

        // Finally prints the data about the evaluation

        if (generateStatistics == true) {

            // Sets the number of milliseconds needed to evaluate

            statistics.setTime(crono.getTime());

            // Generate the file with the statistics measures

            try {
                statistics.printOperationsAndSizes();
            } catch (IOException e) {
            }
            ;
        }

        //Althoug generateStatistics can be false, the final expected utility must be
        //stored in order to be showed in the inference panel.
        for (i = 0; i < currentRelations.size(); i++) {
            valueRel = (Relation) currentRelations.elementAt(i);
            if (valueRel.getKind() == Relation.UTILITY) {
                statistics.setFinalExpectedUtility(valueRel.getValues());
                break;
            }
        }

        // View the time needed to solve the IDiagram

        crono.viewTime();

        if (generateDebugInformation == true) {
            System.out.println("Saliendo de metodo getPosteriorDistributionsID");
        }

    } // end method

    /**
     * Carries out a propagation storing the results in <code>results</code>.
     */
    public void propagate() {

        if (network.getClass() == IDiagram.class) {
            // First at all, check if the diagram is evaluable
            if (initialConditions() == false) {
                System.out.println("The influence diagram can not be solved with this method");
                return;
            }
            getPosteriorDistributionsID();
        } else {
            System.out.print("Error in VariableElimination.propagate(): ");
            System.out.println("this propagation method is not implemented for " + network.getClass());
            System.exit(1);
        }
    }

    /**
     * Carries out a propagation saving the results in <code>OutputFile</code>.
     *
     * @param outputFile the file where the exact results will be
     *                   stored.
     */
    public void propagate(String outputFile) throws ParseException, IOException {

        propagate();
        saveResults(outputFile);
    }

    /**
     * Makes a propagation on an ID, and gets a configuration as the
     * evidence for the propagation.
     * @param <code>Configuration</code> Evidence for the propagation
     *
     */
    public void propagate(Configuration configuration) {

        if (network.getClass() != IDiagram.class) {
            System.out.println("Error in VariableElimination.propagate(Configuration): This method is only for influence diagrams");
            System.exit(0);
        }

        // Make an evidence object from configuration, and store it
        // as observations

        observations = new Evidence(configuration);

        // If everything is OK

        obtainInterest();
        getPosteriorDistributionsID();
    }

    /**
     * Method to make the operations requiered to remove a chance node
     * @param node Node to remove
     * @param pt PairTable to store the relations of the nodes
     * @return <code>boolean</code> to show if the operation was made
     */
    private void combinePotentialsToRemoveChanceNode(FiniteStates node, PairTable pt) {
        Potential potC;
        Potential potU;

        if (generateDebugInformation == true) {
            System.out.println("Entrando a metodo combinePotentialsToRemoveChanceNode");
            System.out.println("Eliminando nodo de azar: " + node.getName());
        }

        // First at all, combine the probability potentials related to this
        // node

        potC = combineProbabilityPotentials(node, pt);

        // Combine the utility potentials related to node

        potU = combineUtilityPotentials(node, pt);

        // Combine both of them

        if (potC != null && potU != null) {
            potU = combine(potC, potU);

            if (generateDebugInformation == true) {
                System.out.println("Combinados ambos potenciales......");
                potU.print();
                System.out.println("----------------------------------------");
            }
        }

        // Remove from them the node itself

        if (potC != null) {

            // If the potential is marginal or conditional on the variable to
            // remove the result of the operation will be an unity potential
            // and is not needed to apply such operation

            if (((IDiagram) network).isConditionalOrMarginalPotential(node, potC) == false) {
                potC = addVariable(potC, node);
            } else {
                //Make potC equals to null to avoid unnecesary operations
                potC = null;
            }

            if (generateDebugInformation == true) {
                if (potC == null) {
                    System.out.println("Se trata de un potencial unidad y se descarta....");
                } else {
                    potC.print();
                    System.out.println("----------------------------------------");
                }
            }
        }

        if (potU != null) {
            potU = addVariable(potU, node);

            if (generateDebugInformation == true) {
                System.out.println("Se elimina en suma la variable a eliminar de potU " + node.getName());
                potU.print();
                System.out.println("----------------------------------------");
            }
        }

        // Finally, divide them to get the final utility

        if (potC != null && potU != null) {
            potU = divide(potU, potC);

            if (generateDebugInformation == true) {
                System.out.println("Se han dividido los potenciales: ");
                potU.print();
                System.out.println("----------------------------------------");
            }
        }

        // We have to make relations for the new potentials

        if (potC != null) {
            makeRelationFromPotential(potC, pt, Relation.POTENTIAL);
        }

        if (potU != null) {
            makeRelationFromPotential(potU, pt, Relation.UTILITY);
        }

        if (generateDebugInformation == true) {
            System.out.println("Saliendo de metodo combinePotentialsToRemoveChanceNode");
        }
    }

    /**
     * Method to make the operations requiered to remove a decision node
     * @param node Node to remove
     * @param pt PairTable to store the relations of the nodes
     * @return <code>boolean</code> to show if the operation was made
     */
    private void combinePotentialsToRemoveDecisionNode(FiniteStates node, PairTable pt) {
        Potential potC = null;
        Potential potU = null;
        Potential eU;
        Vector vars = null;

        if (generateDebugInformation == true) {
            System.out.println("Entrando a metodo combinePotentialsToRemoveDecisionNode");
        }

        // First at all, combine the probability potentials related to this
        // node

        potC = combineProbabilityPotentials(node, pt);

        // Combine the utility potentials related to node

        potU = combineUtilityPotentials(node, pt);

        // Combine both of them

        if (potC != null && potU != null) {
            potU = combine(potC, potU);
        }

        // Remove from them the node itself, maxMarginalizing

        if (potC != null) {
            vars = new Vector(potC.getVariables());
            vars.removeElement(node);
            potC = potC.maxMarginalizePotential(vars);
        }

        // Finally, divide them to get the final utility

        if (potC != null && potU != null) {
            potU = divide(potU, potC);

            if (generateDebugInformation == true) {
                System.out.println("Se han dividido los potenciales: ");
                potU.print();
                System.out.println("----------------------------------------");
            }
        }

        // Before removing the node itself, store the decision
        // table, making the requiered operations for that

        eU = getExpectedUtility(node, potU);
        results.addElement(eU);

        if (generateStatistics == true) {
            //For this table, display the statistics about the relative
            //importance of teh variables
            statistics.setExplanation(node.getName(), eU);
        }

        if (potU != null) {
            vars = new Vector(potU.getVariables());
            vars.removeElement(node);
            potU = potU.maxMarginalizePotential(vars);
        }

        // We have to make relations for the new potentials

        if (potC != null) {
            makeRelationFromPotential(potC, pt, Relation.POTENTIAL);
        }

        if (potU != null) {
            makeRelationFromPotential(potU, pt, Relation.UTILITY);
        }

        if (generateDebugInformation == true) {
            System.out.println("Saliendo de metodo combinePotentialsToRemoveDecisionNode");
        }
    }

    /**
     * Private method to combine the potentials of probability related
     * to a given node
     * @param node Node to consider
     * @param pt PairTable containing the relations for every node
     * @return the final potential after combination
     */
    private Potential combineProbabilityPotentials(FiniteStates node, PairTable pt) {
        RelationList relations = new RelationList();
        Potential pot = null;
        Relation relation;
        int i;

        if (generateDebugInformation == true) {
            System.out.println("Entrando a metodo combineProbabilityPotentials");
            System.out.println("Combinando potenciales de prob de: " + node.getName());
        }

        // Get the relations related to node

        if (currentRelations != null) {
            relations = currentRelations.getRelationsOfAndRemove(node, Relation.POTENTIAL);
        }

        // Consider all the probability potentials where node
        // takes part and combine them

        for (i = 0; i < relations.size(); i++) {
            // Get the relation

            relation = relations.elementAt(i);

            // Consider it if it is not an utility potential

            if (relation.getKind() != Relation.UTILITY) {

                if (generateDebugInformation == true) {
                    System.out.println("Potential de probabilidad: ");
                    relation.print();
                    System.out.println("---------------------------------------");
                }

                // Combine the potential

                pot = combinePotentials(pot, relation);

                if (generateDebugInformation == true) {
                    if (pot != null) {
                        System.out.println("Combinacion de potenciales hasta el momento:");
                        pot.print();
                        System.out.println("-------------------------------------");
                    } else {
                        System.out.println("Potencial nulo");
                    }
                }

                // Remove this relation from pt

                pt.removeRelation(relation);
            }
        }

        if (generateDebugInformation == true) {
            System.out.println("Saliendo de combineProbabilityPotentials");
            if (pot != null) {
                System.out.println("El potential final a devolver es:");
                pot.print();
                System.out.println("-------------------------------------");
            } else {
                System.out.println("Potencial de prob nulo");
            }
        }

        // Return the potential

        return pot;
    }

    /**
     * Private method to combine the potentials of utility related
     * to a given node
     * @param node Node to consider
     * @param pt PairTable containing the relations for every node
     * @return the final potential after combination
     */
    private Potential combineUtilityPotentials(Node node, PairTable pt) {
        RelationList relations = new RelationList();
        Potential pot = null;
        Relation relation;
        int i;

        if (generateDebugInformation == true) {
            System.out.println("Entrando a m�todo combineUtilityPotentials");
            System.out.println("Combinando potenciales de utilidad de: " + node.getName());
        }

        // Get the relations related to node

        if (currentRelations != null) {
            relations = currentRelations.getRelationsOfAndRemove(node, Relation.UTILITY);
        }

        // Consider all the probability potentials where node
        // takes part and combine them

        for (i = 0; i < relations.size(); i++) {
            // Get the relation

            relation = relations.elementAt(i);

            // Consider it if it is not an utility potential

            if (relation.getKind() == Relation.UTILITY) {

                if (generateDebugInformation == true) {
                    System.out.println("Potential de utilidad: ");
                    relation.print();
                    System.out.println("---------------------------------------");
                }

                // Combine the potential

                pot = addPotentials(pot, relation);

                if (generateDebugInformation == true) {
                    if (pot != null) {
                        System.out.println("Combinacion de potenciales hasta el momento:");
                        pot.print();
                        System.out.println("-------------------------------------");
                    } else {
                        System.out.println("Potencial nulo");
                    }
                }

                // Remove this relation from pt

                pt.removeRelation(relation);
            }
        }

        if (generateDebugInformation == true) {
            System.out.println("Saliendo de combineUtilityPotentials");
            if (pot != null) {
                System.out.println("El potential final a devolver es:");
                pot.print();
                System.out.println("-------------------------------------");
            } else {
                System.out.println("Potencial nulo");
            }
        }

        // Return the potential

        return pot;
    }

    /**
     * Method to add two potentials: once passed as argument and other
     * from a relation. If any of them is null will be returned the
     * other
     * @param <code>Potential</code> potential
     * @param <code>Relation</code> relation, which potential wish to
     *        add to potential
     * @return <code>Potential</code> final potential
     */
    private Potential addPotentials(Potential pot, Relation rel) {
        Potential finalPotential;

        if (pot == null) {
            finalPotential = rel.getValues();
        } else {
            finalPotential = addition(pot, rel.getValues());
        }

        return (finalPotential);
    }

    /**
     * Method to preproccess two potential, looking if both of them
     * share some variable. If not build a new unity potential over
     * the new variables and combine both potentials. When there
     * are variables in common then add them
     * @param <code>Potential</code> potential
     * @param <code>Relation</code> relation, which potential wish to
     *        add to potential
     * @return <code>Potential</code> final potential
     */
    private Potential preproccess(Potential pot, Relation rel) {
        NodeList varsInRel = rel.getVariables();
        NodeList varsInPot = new NodeList(pot.getVariables());
        Node node;
        boolean shared = false;
        Potential potNew;
        Potential res;
        Configuration conf;
        int i;

        // Get if there is at least one variable in common

        for (i = 0; i < varsInRel.size(); i++) {
            node = varsInRel.elementAt(i);

            if (varsInPot.getId(node.getName()) != -1) {
                shared = true;
                break;
            }
        }

        // If shared == false, build a new PotentialTree

        potNew = rel.getValues().copy();
        conf = new Configuration(potNew.getVariables());
        for (i = 0; i < conf.possibleValues(); i++) {
            potNew.setValue(conf, 1.0);
            conf.nextConfiguration();
        }

        // Combine both potentials

        res = pot.combine(potNew);

        // Return res

        return res;
    }

    /**
     * Method to combine two potentials: once passed as argument and other
     * from a relation. If any of them is null will be returned the
     * other
     * @param <code>Potential</code> potential
     * @param <code>Relation</code> relation, which potential wish to
     *        add to potential
     * @return <code>Potential</code> final potential
     */
    private Potential combinePotentials(Potential pot, Relation rel) {
        Potential finalPotential;

        if (pot == null) {
            finalPotential = rel.getValues();
        } else {
            finalPotential = combine(pot, rel.getValues());
        }

        return (finalPotential);
    }

    /**
     * Method to create a new relation with the variables of
     * the chance potential. The potential is transformed after
     * the addition
     * @param <code>Potential</code> chance potential
     * @param <code>PairTable</code> pairTable where the relation is added
     * @param kind of relation to create (POTENTIAL or UTILITY)
     * @return <code>Relation</code> relation made from variables in potential
     */
    private void makeRelationFromPotential(Potential pot, PairTable pair,
            int kind) {
        Relation r = null;
        boolean utility = false;

        // Check the kind of the relation

        if (kind != Relation.POTENTIAL && kind != Relation.UTILITY) {
            System.out.println("Error in VariableElimination.makeRelationFromPotential(Potential, PairTable, int):");
            System.out.println("Invalid kind of relation to be created");
            System.exit(1);
        }

        // Works only for potentials not null

        if (pot != null) {
            if (kind == Relation.UTILITY) {
                utility = true;
            }

            // Transform it
            pot = transformAfterOperation(pot, utility);

            // Creates the new relation

            r = new Relation();
            r.setKind(kind);
            r.getVariables().setNodes((Vector) pot.getVariables().clone());
            r.setValues(pot);

            currentRelations.insertRelation(r);

            // Add the relation to the pairtable

            pair.addRelation(r);
        }
    }

    /**
     * Method to compare the policies obtained as a consequence
     * of two evalautions on the same IDiagram
     * @param <code>resultsToCompare</code> evaluation to compare with
     * @result <code>double</code> the distance between policies
     */
    public double comparePolicies(Vector resultsToCompare) {
        Potential result;
        Potential resultToCompare;
        Vector vars;
        Vector varsForConf = new Vector();
        FiniteStates decision;
        Configuration partial, total, totalToCompare;
        double utility, utilityToCompare, max, diff = 0, diffLocal;
        long k;
        long cases;
        int i, j, indMax;

        // To compare both policies we must see the expected
        // utility of the proposed policy for the evaluation
        // respect to the evaluation passed as an argument
        // We will only consider the last table with the global
        // policy, ANYWAY, ITS IN A LOOP........

        for (i = 0; i < results.size(); i++) {

            // Select the potential related with this own object

            result = (Potential) results.elementAt(i);

            // Select the potential related with the results to be compared
            // with this

            resultToCompare = (Potential) resultsToCompare.elementAt(i);

            // Make a configuration over the whole set of
            // variables except the last one. The last one is related
            // with the decision variable and it must be kept appart
            // to loop over it

            vars = result.getVariables();
            for (j = 0; j < vars.size() - 1; j++) {
                varsForConf.addElement(vars.elementAt(j));
            }

            // Build the configuration

            partial = new Configuration(varsForConf);

            // Build a new configuration with the whole set of variables
            // This configuration will be used to access to the table with
            // the optimal policy, given a certain configuration

            total = new Configuration(vars);

            // Retrieve the decision to consider

            decision = (FiniteStates) vars.elementAt(j);

            // The decision is used to determine the number od states
            // of the related variable. So is computed the final size
            // of the potential with the optimal policy, without taking
            // into account the number of states of the decision

            cases = (long) FiniteStates.getSize(vars) / decision.getNumStates();

            // Built a configuration with the variables of the potential
            // passed as argument and that will be compared

            totalToCompare = new Configuration(resultToCompare.getVariables());

            // Once the configuration is done, we must go over
            // all of its values, to retrieve the optimal policy
            // for it

            diff = 0;
            for (k = 0; k < cases; k++) {

                // Copy the values from partial configuration

                total.resetConfiguration(partial);

                // Get the optimal policy for each case, in the table
                // related to this object

                max = 0;
                utility = 0;
                for (j = 0, indMax = 0; j < decision.getNumStates(); j++) {
                    total.putValue(decision, j);
                    utility = result.getValue(total);

                    if (j == 0) {
                        max = utility;
                    } else {
                        if (max < utility) {
                            max = utility;
                            indMax = j;
                        }
                    }
                }

                // Set the value for the maximum value in the total
                // configuration

                total.putValue(decision, indMax);

                // Once obtained the maximum for the base table,
                // get this value for the second one

                for (j = 0; j < totalToCompare.size(); j++) {
                    totalToCompare.putValue(total.getVariable(j).getName(), total.getValue(j));
                }

                // Get the expected utility related to this policy, but in the potential
                // passed as argument

                utilityToCompare = resultToCompare.getValue(totalToCompare);

                // Compute the difference

                diffLocal = max - utilityToCompare;
                ;
                diff += Math.pow(diffLocal, 2);

                // Go to the next configuration

                partial.nextConfiguration();
            }

            // Once finished, divide by the number of cases

            diff = diff / cases;

            // Get square root, as the global diff

            diff = Math.sqrt(diff);

            // As by now we are only interested inf the first
            // table, break the loop

            break;
        }

        return (diff);
    }

    /**
     * Method to get the expected utility for a decision table
     * @param Node decision node related to the decision table
     * @param Potential utility directly related to the decision
     */
    private Potential getExpectedUtility(Node node, Potential util) {
        Potential aux = null;
        Potential potC = null;
        Relation rel;
        Vector vars;
        Vector newVars;
        Node decision;
        String name;
        int kindOfRel;
        boolean added = false;
        int i;

        if (generateDebugInformation == true) {
            System.out.println("Entrando en getExpectecUtility");
            System.out.println("Argumentos: ");
            System.out.println("Node: " + node.getName());
            System.out.println("Util: ");
            util.print();
        }

        // Assign aux to util

        aux = util;

        // Consider the utility relations

        for (i = 0; i < currentRelations.size(); i++) {
            rel = currentRelations.elementAt(i);
            if (rel.isInRelation(node) == false && rel.getKind() == Relation.UTILITY) {

                // First at all check if both potentials have shared variables
                // If not, combine first with an unity potential to add these
                // new variables

                aux = preproccess(aux, rel);
                aux = addPotentials(aux, rel);
                added = true;

                if (generateDebugInformation == true) {
                    System.out.println("Integrando utilidades :");
                    System.out.println("Potencial auxiliar: ");
                    aux.print();
                }
            }
        }

        //Get the set of variables present in aux

        vars = aux.getVariables();

        // Once the utility relations are integrated, we must consider the
        // probability relations to avoid utilty related to non possible
        // events. This is required if really were added new relations

        if (added == true) {

            RelationList relations = currentRelations.getRelationsOf(aux.getVariables());

            for (i = 0; i < relations.size(); i++) {
                // Get every relation

                rel = (Relation) relations.elementAt(i);

                // Consider if it is not an utility

                kindOfRel = rel.getKind();
                if (kindOfRel != Relation.UTILITY && kindOfRel != Relation.CONSTRAINT) {

                    if (generateDebugInformation == true) {
                        System.out.println("Combinando con distr. de probabilidad");
                        System.out.println("Relacion correspondiente");
                        rel.print();
                    }

                    // Combine on potC

                    potC = combinePotentials(potC, rel);

                    if (generateDebugInformation == true) {
                        System.out.println("Tras combinacion: resultado parcial");
                        potC.print();
                    }
                }
            }

            // If potC is non null, multiply the utility and potC to avoid
            // utility related to impossible events. The divide to get the
            // same values

            if (potC != null) {
                aux = combine(aux, potC);
                aux = divide(aux, potC);
            }

            //Finally, remove adding all the variables that were not present
            //in aux: that variables are stored in aux

            newVars = aux.getVariables();

            for (i = 0; i < newVars.size(); i++) {
                Node var = (Node) newVars.elementAt(i);

                // Look if this variable is in vars

                if (vars.contains(var) == false) {
                    aux = addVariable(aux, var);
                    if (generateDebugInformation == true) {
                        System.out.println("Eliminada variable: " + var.getName());
                        aux.print();
                    }
                }
            }
        }

        // Apply constraints on the final decision table
        aux = transformAfterOperation(aux, true);

        // Get sure node is the last variable in the potential

        aux = (Potential) aux.sendVarToEnd(node);
        if (generateDebugInformation == true) {
            System.out.println("Saliendo de getExpecteUtility");
            System.out.println("A devolver: ");
            aux.print();
        }

        // Return aux

        return aux;
    }
} // End of class

