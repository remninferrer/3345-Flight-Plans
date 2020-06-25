import java.io.BufferedReader;
import java.util.Arrays;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Stack;

public class FlightPlans {
	
	static class EdgeNode{    			
		String name;
		int cost;
	    int time;
	    
	    EdgeNode enext; 

	    EdgeNode(String n, int c, int t) { 
	    	name = n; cost = c; time = t;
	    } 
	}

	static class VertexNode {    			
		String name = "";
		EdgeNode enode;
	    VertexNode vnext; 
	    
	    VertexNode(String n){
	    	name = n;
	    }
	} 

	static class LinkedList { 		
	    VertexNode head;     
	    
	    LinkedList(){
	    	head = null;
	    }
	}
	
	static class Paths {
		String name;
		int cost;
	    int time;
	    
	    Paths(String n, int c, int t) { 
	    	name = n; cost = c; time = t;
	    } 
	}
	
	// Recursive function that returns VertexNode if it is the end path
	// Otherwise checks each of current VertexNode's EdgeNodes that are not visited
	public static void nodeSearch(VertexNode head, VertexNode vn, String end, Stack<String> stack, int cTotal, int tTotal,
			ArrayList<Paths> paths) {
		
		// BASE CASE!!! Save current order into paths list, then stack
	    if(vn.name.equals(end)) {
	    	// Add to completed ArrayList
	    	Paths completed = new Paths(Arrays.toString(stack.toArray()), cTotal, tTotal);
	    	paths.add(completed);    		    	
	    	stack.pop();	    		    	
		}
		else {
			// Traverse all of VertexNode's EdgeNodes
			EdgeNode en = vn.enode;			
			while(en != null) {
								
				// Returns position if found, -1 if not
			    int visited = stack.search(en.name);				
			    	
			    // If visited, skip
				if(visited > -1) { 				}
				
				// If EdgeNode has not been visited, turn it into VertexNode and check its new EdgeNode
				else {					
					// Find VertexNode version of EdgeNode
					VertexNode currentVN = head;					
					while(!en.name.equals(currentVN.name)) {									
						currentVN = currentVN.vnext;
					}
					// Add cost and time to total
					int costToAdd = en.cost + cTotal;
					int timeToAdd = en.time + tTotal;
					
					// Add to stack, then check current VertexNode's Edge Nodes
					stack.push(currentVN.name);
					nodeSearch(head,currentVN, end, stack, costToAdd, timeToAdd, paths);	
					
					// Remove from total when finished
					costToAdd = cTotal - en.cost;
					timeToAdd = tTotal - en.time;					
				}				
				
				en = en.enext;
			}
			stack.pop();
		}
			
	}
	
	// Finds three cheapest paths
	public static void shortestCost(ArrayList<Paths>paths) {
		
		for(int i = 0; i < 3; i++) {
			int shortest = 0;
			for(int j = 1; j < paths.size(); j++) {
				
				if (paths.get(shortest).cost > paths.get(j).cost) {
					shortest = j;
				}
			}
			System.out.println(paths.get(shortest).name + " with the cost of " + paths.get(shortest).cost);
		    paths.remove(paths.get(shortest));
		}
		
	}
	
	// Find three least time consuming paths
	public static void shortestTime(ArrayList<Paths>paths) {
		for(int i = 0; i < 3; i++) {
			int shortest = 0;			
			for(int j = 1; j < paths.size(); j++) {
				
				if (paths.get(shortest).time > paths.get(j).time) {
					shortest = j;
				}
			}
			System.out.println(paths.get(shortest).name + " with the time of " + paths.get(shortest).time);
		    paths.remove(paths.get(shortest));
		}
	}
	
	// Reads in a request and fetches three shortest paths of either cost or time
	public static void readInRequest(LinkedList flights) throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("request.txt"));
	    String line = null;
	    
	    // Skip first line
	  	br.readLine();

	    while ((line = br.readLine()) != null) {
	    	String[] values = line.split("\\|");
	    	    	
	    	String start = values[0];
	    	String end = values [1];
	    	String type = values[2];
	    	
	    	System.out.println(start + "->" + end + " : "+ type);
	    	
	    	// Find starting node
	    	VertexNode vn = flights.head;
	    	while(!start.equals(vn.name)) {
	    		vn = vn.vnext;
	    	}
	    	
	    	// Used to save completed paths
	    	ArrayList<Paths> paths = new ArrayList<Paths>();
	    	
	    	// Used for holding order
	    	Stack<String> stack = new Stack<String>();	
	    	
	    	stack.push(start);	    	
	    	nodeSearch(flights.head, vn, end, stack, 0, 0, paths);    	    		    	
	    	 	    	
	    	if(paths.isEmpty()) {
	    		System.out.println("Error, no paths to file.");
	    	}
	    	else if(type.equals("C")) { 		
	    		shortestCost(paths);
	    	}
	    	else if(type.equals("T")) {
	    		shortestTime(paths);
	    	}    	    	
	    }
	    br.close();
	}
	
	// Reads in and assigns data as nodes or edges
	public static void readInAndPopulate(LinkedList flights) throws Exception {
		
		BufferedReader br = new BufferedReader(new FileReader("file.txt"));
	    String line = null;
	    
	    // Skip first line
	  	br.readLine();
	  	
	    while ((line = br.readLine()) != null) {
	      String[] values = line.split("\\|");
	      	    	      	     	      
	      // Values from file
	      String vnodeData = values[0];
	      String enodeData = values[1];
	      int costData = Integer.parseInt(values[2]);
	      int timeData = Integer.parseInt(values[3]);
	      	     	     
	      // Creates root, edge, and node after root
	      if (flights.head == null) {	    	  
	    	  flights.head = new VertexNode(vnodeData);
	    	  flights.head.enode = new EdgeNode(enodeData,costData,timeData);	
	    	  
	    	  flights.head.vnext =  new VertexNode(enodeData);
	    	  flights.head.vnext.enode = new EdgeNode(vnodeData,costData,timeData);
	      }
	      // Creates other nodes or assigns edge nodes to current existing ones
	      else {	
	    	  
	    	  VertexNode currentVNode = flights.head;
	    	  VertexNode previousVNode = flights.head;	    	  
	    	  boolean nodeExists = false;
	    	  
	    	  // Iterate through list and see if there is duplicate
	    	  while(currentVNode != null) {	 
	    		  
	    		  // If duplicate exists, add edge node as tail edge node
	    		  if(vnodeData.equals(currentVNode.name)) {	    
	    			  
		    		  EdgeNode currentENode = currentVNode.enode;
		    		  EdgeNode previousENode = currentVNode.enode;
		    		  
		    		  while(currentENode != null) {
		    			  previousENode = currentENode;
		    			  currentENode = currentENode.enext;
		    		  }
		    		  
		    		  currentENode = new EdgeNode(enodeData,costData,timeData);	 		    		    		  
		    		  previousENode.enext = currentENode;		    		  
		    		  nodeExists = true;
	    		  }	    	
	    		 
	    		  previousVNode = currentVNode;
    			  currentVNode = currentVNode.vnext;    			     			  
    		  }			      
	    	  // Otherwise, create new node with new edge
	    	  if (nodeExists == false) {	    	
	    		  currentVNode = new VertexNode(vnodeData);
	    		  currentVNode.enode = new EdgeNode(enodeData,costData,timeData);
	    		   	    		  
	    		  previousVNode.vnext = currentVNode;
	    	  }	
	    	  
	    	  // Since graph is bidirectional, the EdgeNode to VertexNode cost/time must be
	    	  // added to the EdgeNode's VertexNode form (if A->B, then B->A for bidirectional)
	    	  currentVNode = flights.head;
	    	  previousVNode = flights.head;	    	  
	    	  nodeExists = false;
	    	  
	    	  while(currentVNode != null) {	 	    		  
	    		  // If duplicate exists, add edge node as tail edge node
	    		  if(enodeData.equals(currentVNode.name)) {	    
	    			  
		    		  EdgeNode currentENode = currentVNode.enode;
		    		  EdgeNode previousENode = currentVNode.enode;
		    		  
		    		  while(currentENode != null) {
		    			  previousENode = currentENode;
		    			  currentENode = currentENode.enext;
		    		  }
		    		  
		    		  currentENode = new EdgeNode(vnodeData,costData,timeData);	 		    		    		  
		    		  previousENode.enext = currentENode;		    		  
		    		  nodeExists = true;
	    		  }	    	
	    		 
	    		  previousVNode = currentVNode;
    			  currentVNode = currentVNode.vnext;    			     			  
    		  }
	    	  // Otherwise, create new node with new edge
	    	  if (nodeExists == false) {	    	
	    		  currentVNode = new VertexNode(enodeData);
	    		  currentVNode.enode = new EdgeNode(vnodeData,costData,timeData);
	    		   	    		  
	    		  previousVNode.vnext = currentVNode;
	    	  }	
	      }      	        	      		     	      
	    }
	    br.close();
	}
	
	// Prints Linked List
	public static void printList(LinkedList flights) {
		
		VertexNode n = flights.head;
		
		while (n != null) {
			System.out.println("Current City: " + n.name);
			
			EdgeNode e = n.enode;
			while(e != null) {
				System.out.println(" to " + e.name + "| cost: " + e.cost + "| time: " + e.time);				
				e = e.enext;
			}			
			n = n.vnext;
		}
	}
	
	public static void main(String[] args) throws Exception{
		LinkedList flights = new LinkedList();
		
		readInAndPopulate(flights);
					
		printList(flights);
		
		readInRequest(flights);				
	}
}
