import java.util.*; 
import java.io.*;

public class Airline{

	private static Graph G;
	private static ArrayList<String> Vertices;
	private static Scanner userInput;
	private static BufferedReader fileInput;
	private static PrintWriter fileOutput;
	private static FileReader fileToRead;
	private static File routesFile;
	private static String file;
	private static int V;

	public static void main(String[] args){
		userInput = new Scanner(System.in);
		try{
			do{
				System.out.println("Please enter the filename: ");
				file = userInput.nextLine();
				routesFile = new File(file);
			}while(!routesFile.exists());
	 		fileToRead = new FileReader(file);
    		fileInput = new BufferedReader(fileToRead);
    		V = Integer.parseInt(fileInput.readLine());
    		Vertices = new ArrayList<String>(V);
    		G = new Graph(V);
    		for(int i = 0; i < V; i++) Vertices.add(i,fileInput.readLine());
			G.setVertices(Vertices);
			String edges;
    		while((edges = fileInput.readLine()) != null){
    			 String[] splitStrings = edges.split(" ");
    			 int v = Integer.parseInt(splitStrings[0]);
    			 int w = Integer.parseInt(splitStrings[1]);
   		 		 double d = Double.parseDouble(splitStrings[2]);
    			 double p = Double.parseDouble(splitStrings[3]);
    			 Edge E = new Edge((v-1),(w-1),d,p);
  	  			 G.addEdge(E);
    		}
    		fileInput.close();
    	}
		catch(Exception E){}	
		
		int run = 0;
		StringBuilder menu = new StringBuilder("Airline Options:\n");
		menu.append("1) List ALL routes\n");
		menu.append("2) List Minimal Spanning Tree(s)\n");
		menu.append("3) Shortest DISTANCE path between source and destination\n");
		menu.append("4) Shortest PRICE path between source and destination\n");
		menu.append("5) Shortest HOPS path between source and destination\n");
		menu.append("6) ADD a route\n");
		menu.append("7) REMOVE a route\n");
		menu.append("8) All routes under a certain PRICE\n");
		menu.append("9) QUIT\n");
		menu.append("Enter your choice: ");
		
		do{
			System.out.println(menu.toString());
			try{run = Integer.parseInt(userInput.nextLine());}
			catch(NumberFormatException e){run = 0;}
			System.out.println();
			
			if (run == 1)
				System.out.print(G.toString());
			
			else if (run == 2)
				System.out.print(G.preprim());
			
			else if (run >= 3 && run <= 7 ){			
				String source,destination;
				int s,d;
				boolean result;
				Edge E;
				
				do{
				System.out.println("Enter source: ");
				source = toProperCase(userInput.nextLine());
				System.out.println("Enter destination: ");
				destination = toProperCase(userInput.nextLine().toLowerCase());
				s = Vertices.indexOf(source);
				d = Vertices.indexOf(destination);
				System.out.println();
				if(s < 0 || d < 0)
					System.out.println("Source or destination entered incorrectly. Please try again.");
				}while(s < 0 || d < 0);

				
				if(run == 3) 		System.out.print(G.dijkstra(s,d,1));
				else if(run == 4)	System.out.print(G.dijkstra(s,d,2));
				else if(run == 5)	System.out.print(G.bfs(s,d));
				
				else if (run == 6){
					double distance,price;
					System.out.println("Enter distance: ");
					distance = Double.parseDouble(userInput.nextLine());
					System.out.println("Enter price: ");
					price = Double.parseDouble(userInput.nextLine());
					System.out.println();
					E = new Edge(s,d,distance,price);
					result = G.addEdge(E);
					if(result)	System.out.println(source +" to " + destination + " was added to the graph");
					else		System.out.println("Error: Edge was not added in graph.");
				}
				
				else if (run == 7){
					E = new Edge(s,d);
					result = G.removeEdge(E);
					if(result)	System.out.println(source +" to " + destination + " was removed from the graph");
					else		System.out.println("Edge was not found in graph.");
				}
				
			}
			

			
			else if (run == 8){
					double price;
					System.out.println("Enter MAXIMUM price: ");
					price = Double.parseDouble(userInput.nextLine());
					System.out.println();
					G.premaxpath(price);
			}
			
			else if (run == 9){
				try{
					fileOutput= new PrintWriter(file, "UTF-8");
					fileOutput.println(V + "");
				
					for(int i = 0; i < V; i++){
						fileOutput.println(Vertices.get(i));
					}
				
					List<Edge> Edges = new ArrayList(V);
					for(int i = 0; i < V; i++){
						for(Edge e : G.adj(i)){
							if(!Edges.contains(e)){
								Edges.add(e);
								int v = e.either()+1;
								int w =	e.other((v-1))+1;
								StringBuilder s = new StringBuilder(v + " " + w  + " " + e.distance()  + " " + e.price());
								fileOutput.println(s.toString());
							}
						}
					}
					fileOutput.close();
					System.out.println("Thank you for using Airline!");
				}
				
				catch(Exception E){}
			}
			
			else{
				System.out.println("Please try again.");
			}
			
			System.out.println();
			
		}while(run != 9);
    	
	}
	
	public static String toProperCase(String s) {
   		return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	}
	
}