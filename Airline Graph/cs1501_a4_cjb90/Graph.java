import java.util.*;

public class Graph {
    private final int V;
    private int E;
    private LinkedList<Edge>[] adj;
    private ArrayList<String> Vertices;
    
    /**
     * Initializes an empty edge-weighted graph with <tt>V</tt> vertices and 0 edges.
     * param V the number of vertices
     * @throws java.lang.IllegalArgumentException if <tt>V</tt> < 0
     */
    public Graph(int V) {
        if (V < 0) throw new IllegalArgumentException("Number of vertices must be nonnegative");
        this.V = V;
        this.E = 0;
        adj = (LinkedList<Edge>[]) new LinkedList[V];
        for (int v = 0; v < V; v++) {
            adj[v] = new LinkedList<Edge>();
        }
    }

    /**
     * Returns the number of vertices in the edge-weighted graph.
     * @return the number of vertices in the edge-weighted graph
     */
    public int V() {
        return V;
    }

    /**
     * Returns the number of edges in the edge-weighted graph.
     * @return the number of edges in the edge-weighted graph
     */
    public int E() {
        return E;
    }

    // throw an IndexOutOfBoundsException unless 0 <= v < V
    private void validateVertex(int v) {
        if (v < 0 || v >= V)
            throw new IndexOutOfBoundsException("vertex " + v + " is not between 0 and " + (V-1));
    }

    /**
     * Adds the undirected edge <tt>e</tt> to the edge-weighted graph.
     * @param e the edge
     * @throws java.lang.IndexOutOfBoundsException unless both endpoints are between 0 and V-1
     */
    public boolean addEdge(Edge e) {
    	boolean result = false;
        int v = e.either();
        int w = e.other(v);
        validateVertex(v);
        validateVertex(w);
        result = adj[v].add(e);
        result = adj[w].add(e);
        if(result)
        	E++;
        return result;
    }
    
    public boolean removeEdge(Edge e) {
    	boolean result = false;
        int v = e.either();
        int w = e.other(v);
        validateVertex(v);
        validateVertex(w);
		result = adj[v].remove(e);
        result = adj[w].remove(e);
        if(result)
        	E--;
        return result;
    }

    /**
     * Returns the edges incident on vertex <tt>v</tt>.
     * @return the edges incident on vertex <tt>v</tt> as an Iterable
     * @param v the vertex
     * @throws java.lang.IndexOutOfBoundsException unless 0 <= v < V
     */
    public Iterable<Edge> adj(int v) {
        validateVertex(v);
        return adj[v];
    }

    /**
     * Returns all edges in the edge-weighted graph.
     * To iterate over the edges in the edge-weighted graph, use foreach notation:
     * <tt>for (Edge e : G.edges())</tt>.
     * @return all edges in the edge-weighted graph as an Iterable.
     */
    public Iterable<Edge> edges() {
        LinkedList<Edge> list = new LinkedList<Edge>();
        for (int v = 0; v < V; v++) {
            int selfLoops = 0;
            for (Edge e : adj(v)) {
                if (e.other(v) > v) {
                    list.add(e);
                }
                // only add one copy of each self loop (self loops will be consecutive)
                else if (e.other(v) == v) {
                    if (selfLoops % 2 == 0) list.add(e);
                    selfLoops++;
                }
            }
        }
        return list;
    }
    
    public void setVertices(ArrayList<String> v){
    	Vertices = v;
    }

    /**
     * Returns a string representation of the edge-weighted graph.
     * This method takes time proportional to <em>E</em> + <em>V</em>.
     * @return the number of vertices <em>V</em>, followed by the number of edges <em>E</em>,
     *   followed by the <em>V</em> adjacency lists of edges
     */
    public String toString() {
        String NEWLINE = System.getProperty("line.separator");
        StringBuilder s = new StringBuilder();
        s.append(V + " Vertices " + E +" Edges " + NEWLINE);
        for (int v = 0; v < V; v++) {
            s.append((v+1) + ": ");
            for (Edge e : adj[v]) {
                s.append(e.toString(Vertices,0) + "  ");
            }
            s.append(NEWLINE);
        }
        return s.toString();
    }
    
    private void changeEdgeWeightToDistance(){
    	for(int i = 0; i < V; i++){
    		for (Edge e : adj(i)) e.changeWeightToDistance();
    	}
    }
    
    private void changeEdgeWeightToPrice(){
        for(int i = 0; i < V; i++){
    		for (Edge e : adj(i)) e.changeWeightToPrice();
    	}
    }
    
    public void premaxpath(double max){
    	boolean[] marked;
    	double[] weightOf;
    	double weight = 0;
    	ArrayList<Integer> path;
    	
    	System.out.print("ALL PATHS OF COST " + max + " OR LESS\nNote that paths are duplicated, once from each end city's point of view\n");
    	this.changeEdgeWeightToPrice();
    	
    	for(int i = 0; i < V; i++){
    		marked = new boolean[V];
    		weightOf = new double[V];
    		weight = 0;
    		path = new ArrayList<Integer>();
    		
    	    for (int v = 0; v < V; v++){
    			marked[v] = false;
    			weightOf[v] = 0.0;
    		}	
    	
    		path.add(i);
    		marked[i] = true;
    		maxpath(i,max,weight,path,marked,weightOf);
    	
    	}
    }
    
    public void maxpath(int v, double max, double weight, ArrayList<Integer> path,boolean[] marked,double[] weightArray){    
    	
    	Queue<Integer> q = new Queue<Integer>();
    	
    	double[] weightOf = Arrays.copyOf(weightArray, weightArray.length);

    	for(Edge e : adj(v)){
    		if(!marked[e.other(v)] && (weight + e.weight() <= max) ){
    			q.enqueue(e.other(v));
    			weightOf[e.other(v)] = e.weight();
    		}
    	}
    	
    	while(!q.isEmpty()){
    		int w = q.dequeue();
    		double tempweight = weight + weightOf[w];
    		
    		marked[w] = true;
    		path.add(w);

			StringBuilder maxPath = new StringBuilder("Cost " + tempweight + " Path: ");
        	for(int i = 0; i < path.size(); i++){
        		int x = path.get(i);
        		if(weightOf[x] != 0) maxPath.append(weightOf[x] + " ");
        		maxPath.append(Vertices.get(x) + " ");
       		}
       			
        	maxPath.append("\n");
        	System.out.println(maxPath.toString());

    		if(tempweight != max){
    			maxpath(w,max,tempweight,path,marked,weightOf);
    		}
    			
    		Integer removeVertex = new Integer(w);
    		path.remove(removeVertex);
    		marked[w] = false;		
    	}	
    }
    
    public String bfs(int s, int d){
        validateVertex(s);
        validateVertex(d);
    	if(s == d){
    		throw new IllegalArgumentException("Source and destination cannot be equal!");
    	}
   	 	boolean[] marked = new boolean[V];
        int[] parent = new int[V];
   	 	boolean found = false;
   	 	int hops = 0;
    	Stack<Integer> path = new Stack<Integer>();
        Queue<Integer> q = new Queue<Integer>();
        marked[s] = true;
        q.enqueue(s);

        while (!q.isEmpty() && !found) {
            int v = q.dequeue();
            for (Edge e : adj[v]) {
            	int w = e.other(v);
            	
            	if(w == d){
            		parent[w] = v;
            		path.push(w);
            		v = parent[w];
    
            		while(v != s){
            			path.push(v);
            			hops++;
            			v = parent[v];
            		}
            			
    				path.push(v);
    				hops++;
            		found = true;
            		break;
            	}
            	
                else if (!marked[w]){
                	parent[w] = v;
                    marked[w] = true;
                    q.enqueue(w);
                }
            }
            
            if(q.isEmpty() && !found){
            	return "No path exists between Source and destination.\n";
            }
        }
        
        StringBuilder bfsPath = new StringBuilder("FEWEST HOPS from " + Vertices.get(s)  + " to " + Vertices.get(d) + " is " + hops + "\n");
        while(!path.isEmpty()){
        	bfsPath.append(Vertices.get(path.pop())+ " ");
        }
        bfsPath.append("\n");
        return bfsPath.toString();
    }
    
    public String preprim(){
    	boolean[] marked = new boolean[V];
    	 StringBuilder mstPath = new StringBuilder("MINIMUM SPANNING TREE\n");
    	 this.prim(0,marked,mstPath);
    	 
    	for(int i=0; i < V; i++){
        	if(!marked[i] && !adj[i].isEmpty()){
        			mstPath.append("Unconnected Graph: MST of Sub-Tree:\n");
        			this.prim(i,marked,mstPath);
        	}
       	 }
    	  
    	 return mstPath.toString();
    }
    
    public void prim(int s, boolean[] marked, StringBuilder mstPath) {	
    	double weight = 0;       							// weight of MST
        Stack<Edge> path = new Stack<Edge>();				// Edges in MST
        MinPQ<Edge> pq = new MinPQ<Edge>();					// MinPQ to hold edges					
        this.changeEdgeWeightToDistance();					// change weight on all edges to represent distance
        scan(s,marked,pq);
        while (!pq.isEmpty() && path.size() < (V-1)) {      // run prims until MST path is V-1  or PQ is Empty              	
            Edge e = pq.delMin();                      		// remove smallest edge in pq
            int v = e.either(), w = e.other(v);        		// get two endpoints
            if (marked[v] && marked[w]) continue;      		
            path.push(e);                            		// add edge to MST
            weight += e.weight();	
            if (!marked[v]) scan(v,marked,pq);               // if not marked, v becomes part of MST
            if (!marked[w]) scan(w,marked,pq);               // if not marked, w becomes part of MST
        }
        
        mstPath.append("The edges in the MST based on distance follow (total weight = " + weight + "):\n");
        while(!path.isEmpty()){
        	mstPath.append(path.pop().toString(Vertices,1)+ "\n");
        }
        mstPath.append("\n");
        return;
    }

    // add all edges of v onto pq if the other endpoint has not yet been marked
    private void scan(int v, boolean[] marked, MinPQ<Edge> pq) {
        marked[v] = true;
        for (Edge e : adj(v))
            if (!marked[e.other(v)]) pq.insert(e);
    }
    
    public String dijkstra(int s, int d, int flag) {
    	String pathType;
    	
    	if(flag == 1){
    		this.changeEdgeWeightToDistance();		// change weight on all edges to represent distance
    		pathType = new String("DISTANCE");
    	}
    	else{
    	        this.changeEdgeWeightToPrice();			// change weight on all edges to represent price
    	        pathType = new String("PRICE");
    	}

		double[] distTo = new double[V];          				// distTo[v] = distance  of shortest s->v path
     	Edge[] edgeTo = new Edge[V];    						// edgeTo[v] = last edge on shortest s->v path
     	double[] weightOf = new double[V];
     	int[] parentOf = new int[V];
     	IndexMinPQ<Double> pq = new IndexMinPQ<Double>(V);
     	Stack<Integer> path = new Stack<Integer>();
     	boolean found = false;

        for (int v = 0; v < V; v++){
            distTo[v] = Double.POSITIVE_INFINITY;
            weightOf[v] = 0.0;
            parentOf[v] = 0;
        }
        distTo[s] = 0.0;
        parentOf[s] = s;
        
		// relax vertices in order of distance from s
        pq.insert(s, distTo[s]);
        while (!pq.isEmpty() && !found) {
            int v = pq.delMin();
            for (Edge e : adj(v))
                relax(v,e,pq,distTo,edgeTo,weightOf,parentOf);
                
            if(v == d) found = true;
            
            if(pq.isEmpty() && !found) return "No path exists between Source and destination.\n";
        }
        
        path.push(d);
        int v = parentOf[d];
        while(v != s){
         	path.push(v);
         	v = parentOf[v];
         }
        path.push(s);
        
        StringBuilder swpPath = new StringBuilder("SHORTEST " + pathType + " PATH " + Vertices.get(s)  + " to " + Vertices.get(d) + " is " + distTo[d] +"\n");
        while(!path.isEmpty()){
        	v = path.pop();
        	if(weightOf[v] != 0) swpPath.append(weightOf[v] + " ");
        	swpPath.append(Vertices.get(v) + " ");
        }
        swpPath.append("\n");
        return swpPath.toString();
        
    }

    // relax edge e and update pq if changed
    private void relax(int v, Edge e, IndexMinPQ<Double> pq, double[] distTo, Edge[] edgeTo, double[] weightOf, int[] parentOf) {
        int w = e.other(v);
        if (distTo[w] > distTo[v] + e.weight()) {
            distTo[w] = distTo[v] + e.weight();
            edgeTo[w] = e;
            weightOf[w] = e.weight();
            parentOf[w] = v;
            if (pq.contains(w)) pq.decreaseKey(w, distTo[w]);
            else                pq.insert(w, distTo[w]);
        }
    }


}