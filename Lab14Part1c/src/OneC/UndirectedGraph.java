package OneC;

import java.util.Stack;

public class UndirectedGraph<T> extends DirectedGraph<T> implements ConnectedGraphInterface<T>, java.io.Serializable {
    /**
     * An undirected graph
     *
     * @author Neil Marcellini
     * @version 12/27/2017
     */
    public UndirectedGraph() {
        super();
    }

    @Override
    public boolean addEdge(T begin, T end, double edgeWeight) {
        super.addEdge(end, begin, edgeWeight);
        return super.addEdge(begin, end, edgeWeight);

    }

    public boolean addEdge(T begin, T end) {
        return addEdge(begin, end, 0.0);
    }

    public int getNumberOfEdges() {
        return super.getNumberOfEdges() / 2;
    }

    @Override
    public Stack<T> getTopologicalOrder() throws UnsupportedOperationException {
        throw new UnsupportedOperationException("Topological sort illegal in an undirected graph.");
    }

    public boolean isConnected(T origin) {
        return super.getNumberOfVertices() == super.getBreadthFirstTraversal(origin).size();
    }
}
