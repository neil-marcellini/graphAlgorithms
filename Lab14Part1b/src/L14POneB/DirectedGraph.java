package L14POneB;

import java.beans.VetoableChangeListener;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A class that implements the ADT directed graph.
 *
 * @author Frank M. Carrano
 * @version 12/22/2017
 * @modifiedBy Neil Marcellini
 */
public class DirectedGraph<T> implements GraphInterface<T> {
    private Map<T, VertexInterface<T>> vertices;
    private int edgeCount;

    public DirectedGraph() {
        this.vertices = new TreeMap<>();
        this.edgeCount = 0;
    } // end default constructor

    public boolean addVertex(T vertexLabel) {
        VertexInterface<T> isDuplicate = this.vertices.put(vertexLabel, new Vertex<T>(vertexLabel));
        return isDuplicate == null; // was add to dictionary successful?
    } // end addVertex

    public boolean addEdge(T begin, T end, double edgeWeight) {
        boolean result = false;

        VertexInterface<T> beginVertex = this.vertices.get(begin);
        VertexInterface<T> endVertex = this.vertices.get(end);

        if ((beginVertex != null) && (endVertex != null))
            result = beginVertex.connect(endVertex, edgeWeight);

        if (result)
            this.edgeCount++;

        return result;
    } // end addEdge

    public boolean addEdge(T begin, T end) {
        return addEdge(begin, end, 0);
    } // end addEdge

    public boolean hasEdge(T begin, T end) {
        boolean found = false;

        VertexInterface<T> beginVertex = this.vertices.get(begin);
        VertexInterface<T> endVertex = this.vertices.get(end);

        if ((beginVertex != null) && (endVertex != null)) {
            Iterator<VertexInterface<T>> neighbors =
                    beginVertex.getNeighborIterator();
            while (!found && neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();
                if (endVertex.equals(nextNeighbor))
                    found = true;
            }
        }

        return found;
    } // end hasEdge

    public boolean isEmpty() {
        return this.vertices.isEmpty();
    } // end isEmpty

    public void clear() {
        this.vertices.clear();
        this.edgeCount = 0;
    } // end clear

    public int getNumberOfVertices() {
        return this.vertices.size();
    } // end getNumberOfVertices

    public int getNumberOfEdges() {
        return this.edgeCount;
    } // end getNumberOfEdges

    protected void resetVertices() {
        Collection<VertexInterface<T>> values = this.vertices.values();
        Iterator<VertexInterface<T>> vertexIterator = values.iterator();
        while (vertexIterator.hasNext()) {
            VertexInterface<T> nextVertex = vertexIterator.next();
            nextVertex.unvisit();
            nextVertex.setCost(0);
            nextVertex.setPredecessor(null);
        }
    } // end resetVertices

    public Queue<T> getBreadthFirstTraversal(T origin) {
        resetVertices();
        Queue<T> traversalOrder = new LinkedBlockingQueue<>();
        Queue<VertexInterface<T>> vertexQueue =
                new LinkedBlockingQueue<>();
        VertexInterface<T> originVertex = this.vertices.get(origin);
        originVertex.visit();
        traversalOrder.offer(origin);    // enqueue vertex label
        vertexQueue.offer(originVertex); // enqueue vertex

        while (!vertexQueue.isEmpty()) {
            VertexInterface<T> frontVertex = vertexQueue.poll();

            Iterator<VertexInterface<T>> neighbors = frontVertex.getNeighborIterator();
            while (neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();
                if (!nextNeighbor.isVisited()) {
                    nextNeighbor.visit();
                    traversalOrder.offer(nextNeighbor.getLabel());
                    vertexQueue.offer(nextNeighbor);
                }
            }
        }

        return traversalOrder;
    } // end getBreadthFirstTraversal

    public Queue<T> getDepthFirstTraversal(T origin) {
        // TODO Part1a
        //traversalOrder = a new queue for the resulting traversal order
        //vertexStack = a new stack to hold vertices as they are visited

        //Mark originVertex as visited
        //traversalOrder.enqueue(originVertex)
        //vertexStack.push(originVertex)
        //while(!vertexStack.isEmpty()){
        //  topVertex = vertexStack.peek();
        //  if(topVertex has an unvisited neighbor){
        //      nextNeighbor = next unvisited of topVertex
        //      Mark nextNeighbor as visited
        //      traversalOrder.enqueue(nextNeighbor);
        //      vertexStack.push(nextNeighbor);
        //      }
        //  else{ all neighbors are visited
        //      vertexStack.pop();
        //  }
        //return traversalOrder


        Queue<T> traversalOrder = new LinkedBlockingQueue<>();
        Stack<VertexInterface<T>> vertexStack = new Stack<>();

        VertexInterface<T> originVertex = this.vertices.get(origin);
        originVertex.visit();
        traversalOrder.offer(origin);
        vertexStack.push(originVertex);

        while (!vertexStack.isEmpty()) {
            VertexInterface<T> topVertex = vertexStack.peek();
            VertexInterface<T> nextNeighbor = topVertex.getUnvisitedNeighbor();
            if (nextNeighbor != null) {
                nextNeighbor.visit();
                traversalOrder.offer(nextNeighbor.getLabel());
                vertexStack.push(nextNeighbor);
            } else { //all neighbors are visited
                vertexStack.pop();
            }
        }

        return traversalOrder;
    } // end getDepthFirstTraversal

    public Stack<T> getTopologicalOrder() {
        resetVertices();

        Stack<T> vertexStack = new Stack<>();
        int numberOfVertices = getNumberOfVertices();
        for (int counter = 1; counter <= numberOfVertices; counter++) {
            VertexInterface<T> nextVertex = findTerminal();
            nextVertex.visit();
            vertexStack.push(nextVertex.getLabel());
        }

        return vertexStack;
    } // end getTopologicalOrder

    /**
     * Precondition: path is an empty stack (NOT null)
     */
    public int getShortestPath(T begin, T end, Stack<T> path) {
        resetVertices();
        boolean done = false;
        Queue<VertexInterface<T>> vertexQueue =
                new LinkedBlockingQueue<>();
        VertexInterface<T> originVertex = this.vertices.get(begin);
        VertexInterface<T> endVertex = this.vertices.get(end);

        originVertex.visit();
        // Assertion: resetVertices() has executed setCost(0)
        // and setPredecessor(null) for originVertex

        vertexQueue.offer(originVertex);

        while (!done && !vertexQueue.isEmpty()) {
            VertexInterface<T> frontVertex = vertexQueue.poll();

            Iterator<VertexInterface<T>> neighbors =
                    frontVertex.getNeighborIterator();
            while (!done && neighbors.hasNext()) {
                VertexInterface<T> nextNeighbor = neighbors.next();

                if (!nextNeighbor.isVisited()) {
                    nextNeighbor.visit();
                    nextNeighbor.setCost(1 + frontVertex.getCost());
                    nextNeighbor.setPredecessor(frontVertex);
                    vertexQueue.offer(nextNeighbor);
                }

                if (nextNeighbor.equals(endVertex))
                    done = true;
            }
        }

        // traversal ends; construct shortest path
        int pathLength = (int) endVertex.getCost();
        path.push(endVertex.getLabel());

        VertexInterface<T> vertex = endVertex;
        while (vertex.hasPredecessor()) {
            vertex = vertex.getPredecessor();
            path.push(vertex.getLabel());
        }

        return pathLength;
    } // end getShortestPath

    /**
     * Precondition: path is an empty stack (NOT null)
     */
    public double getCheapestPath(T begin, T end, Stack<T> path) {
        // TODO Part1a
        //done = false
        // priorityQueue
        //priorityQueue.add(new EntryPQ(originVertex, 0, null));
        //while(!done && !priorityQueue.isEmpty()){
        //  frontEntry = priorityQueue.remove();
        //  frontVertex = vertex in frontEntry
        //  if(frontVertex is not visited){
        //      mark frontVertex visited
        //      set the cost of the path to frontVertex as the cost recorded in frontEntry
        //      set the predecessor of frontVertex to the predecessor recorded in frontEntry
        //      if(frontVertex equals endvertex){
        //          done = true
        //      }
        //      else{
        //          while(frontVertex has a neighbor){
        //              nextNeighbor = next neighbor of frontVertex
        //              weightOfEdgeToNeighbor = weight of edge to next neighbor
        //              if(nextNeighbor is unvisited){
        //                  nextCost = weightOfEdgeToNeighbor + cost of path to frontVertex
        //                  priorityQueue.add(new EntryPQ(nextNeighbor, nextCost, frontVertex))
        //              }
        //          }
        //      }
        //  }
        //}
        //pathCost = cost of path to endVertex
        //path.push(endVertex)
        //vertex = endVertex
        //while(vertex has predecessor){
        //  vertex = predecessor of vertex
        //  path.push(vertex)
        //}
        //return pathCost

        resetVertices();
        boolean done = false;

        // use EntryPQ instead of Vertex because multiple entries contain
        // the same vertex but different costs - cost of path to vertex is EntryPQ's priority value
        PriorityQueue<EntryPQ> priorityQueue = new PriorityQueue<>();

        VertexInterface<T> originVertex = this.vertices.get(begin);
        VertexInterface<T> endVertex = this.vertices.get(end);

        EntryPQ frontEntry;
        VertexInterface<T> frontVertex;

        priorityQueue.add(new EntryPQ(originVertex, 0, null));
        while (!done && !priorityQueue.isEmpty()) {
            frontEntry = priorityQueue.remove();
            frontVertex = frontEntry.getVertex();
            if (!frontVertex.isVisited()) {
                frontVertex.visit();
                frontVertex.setCost(frontEntry.getCost());
                frontVertex.setPredecessor(frontEntry.getPredecessor());
                if (frontVertex.equals(endVertex)) {
                    done = true;
                } else {
                    Iterator<VertexInterface<T>> iter = frontVertex.getNeighborIterator();
                    VertexInterface<T> nextNeighbor;
                    Iterator<Double> weightIterator = frontVertex.getWeightIterator();
                    while (iter.hasNext()) {
                        nextNeighbor = iter.next();
                        double weightOfEdgeToNeighbor = weightIterator.next();
                        if (!nextNeighbor.isVisited()) {
                            double nextCost = weightOfEdgeToNeighbor + frontVertex.getCost();
                            priorityQueue.add(new EntryPQ(nextNeighbor, nextCost, frontVertex));
                        }
                    }
                }
            }
        }
        double pathCost = endVertex.getCost();
        path.push(endVertex.getLabel());
        VertexInterface<T> vertex = endVertex;
        while (vertex.hasPredecessor()) {
            vertex = vertex.getPredecessor();
            path.push(vertex.getLabel());
        }
        return pathCost;
    } // end getCheapestPath

    protected VertexInterface<T> findTerminal() {
        boolean found = false;
        VertexInterface<T> result = null;
        Collection<VertexInterface<T>> values = this.vertices.values();
        Iterator<VertexInterface<T>> vertexIterator = values.iterator();

        while (!found && vertexIterator.hasNext()) {
            VertexInterface<T> nextVertex = vertexIterator.next();

            // if nextVertex is unvisited AND has only visited neighbors)
            if (!nextVertex.isVisited()) {
                if (nextVertex.getUnvisitedNeighbor() == null) {
                    found = true;
                    result = nextVertex;
                }
            }
        }

        return result;
    } // end findTerminal

    // Used for testing
    public void display() {
        System.out.println("Graph has " + getNumberOfVertices() + " vertices and " +
                getNumberOfEdges() + " edges.");

        System.out.println("\nEdges exist from the first vertex in each line to the other vertices in the line.");
        System.out.println("(Edge weights are given; weights are zero for unweighted graphs):\n");
        Collection<VertexInterface<T>> values = this.vertices.values();
        Iterator<VertexInterface<T>> vertexIterator = values.iterator();
        while (vertexIterator.hasNext()) {
            ((Vertex<T>) (vertexIterator.next())).display();
        }
    } // end display

    private class EntryPQ implements Comparable<EntryPQ> {
        private VertexInterface<T> vertex;
        private VertexInterface<T> previousVertex;
        private double cost; // cost to nextVertex

        private EntryPQ(VertexInterface<T> vertex, double cost, VertexInterface<T> previousVertex) {
            this.vertex = vertex;
            this.previousVertex = previousVertex;
            this.cost = cost;
        } // end constructor

        public VertexInterface<T> getVertex() {
            return this.vertex;
        } // end getVertex

        public VertexInterface<T> getPredecessor() {
            return this.previousVertex;
        } // end getPredecessor

        public double getCost() {
            return this.cost;
        } // end getCost

        public int compareTo(EntryPQ otherEntry) {
            return (int) Math.signum(this.cost - otherEntry.cost);
        } // end compareTo

        public String toString() {
            return this.vertex.toString() + " " + this.cost;
        } // end toString
    } // end EntryPQ
}