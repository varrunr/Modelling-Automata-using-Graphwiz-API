/**
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
* * *Implementation of a Finite State Machine in memory* * * * * * * * * * * *
* * *and graphical representation using GraphViz* * * * * * * * * * * * * * * 
* * *Date: 04/11/09 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 *                                                                            *
 *              (c) Copyright 2009 Varrun Ramani                              *
 *                                                                            *
 * This program is free software: you can redistribute it and/or modify it    *
 * under the terms of the GNU General Public License as published by the Free *
 * Software Foundation, either version 3 of the License, or (at your option)  *
 * any later version. This program is distributed in the hope that it will    *
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of  *
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General   *
 * Public License for more details. You should have received a copy of the GNU*
 * General Public License along with this program. If not, see                *
 * http://www.gnu.org/licenses/                                               *                          *
  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * 
 */
//IMPORTS
import java.io.*;
import java.io.File;
import java.util.*;
//Program starts here
class FiniteStateMachine{

	//VARIABLES
	
	static int no_states; // Number of States
	static String start_state; 
	
	//Maps States onto integer values Eg. S0->0 , S1-> 1 For use in array
	static TreeMap < String , Integer > stateMap= new TreeMap < String , Integer >();
	//Maps input alphabets
	static TreeMap < String,Integer > alphaMap= new TreeMap < String , Integer >();
	//List of states, alphabets, end states
	
	static Vector<String> states,alpha,end_states;
	// Matrix representing Finite State Machine 
	static Vector< Vector<String> > automata = new Vector< Vector< String > >();
	
	//INITIALIZE AUTOMATA TO SIZE (size_states x size_alpha) and VALUE -1
	public static void initialize_automata(int size_states,int size_alpha){

		Vector<String> temp;
		for(int i=0;i<size_states;i++)
		{
			 temp = new Vector<String>();
			
				for(int j=0;j<size_alpha;j++)
				{temp.add("-1");}
		
			automata.add(temp);
		}
	}				
	//Parse each Transition of the form <begin>*<trans>=<target> Eg.S0*b=S1
	public static void parse_transition(String str)
	{
		//PARSING
		int i=0,prev=0,posn1=0,posn2=0;
		String trans,target;
		while(str.charAt(i)!='*')i++;  // Traverse till * is reached 
			String begin=str.substring(0,i); //  begin = string [0..(posn(*)-1) ]
		prev = i+1;
		
		while(str.charAt(i)!='=')i++; 		// Traverse till =
		trans = str.substring(prev,i); 		//  trans = string [ (posn(*)+1)....(posn(=)-1) ]
		target=str.substring(i+1,str.length()); //  target = string [ (posn(=)+1)....end ]
	
		//Maps begin,trans onto respective positions in array posn1,posn2 
		posn1 = stateMap.get(begin); 
		posn2 = alphaMap.get(trans);
		
		//Set element at (posn1,posn2) in Automata -> target 
		Vector< String > tempvec = automata.get(posn1);
		tempvec.setElementAt(target,posn2);
		automata.setElementAt(tempvec,posn1);
	
	}	
	
	//Print out the Automata as 2D array of Strings
	public static void print(int size_state,int size_alpha){
	
		for(int i=0;i<size_state;i++)
		{
			for(int j=0;j<size_alpha;j++)
			{
				System.out.printf("%s", (String)(automata.get(i)).get(j)+" ");
			}
			System.out.printf("\n");
		}
	}
	
	// Check if String s is present in the set of end States
	public static boolean chkinEndstates(String s){
	for(int i=0;i<(int)end_states.size();i++){
		
		if(s.equals(end_states.get(i)))
		return true;
	}
	return false;
	}
	
	//Read from file
	public static void input_file(Scanner readInput) 
	{
		Scanner parser; // Scanner to parse a line
		String temp1,temp2; 	
		int i=0;
		//READ THE LIST OF STATES
		temp1=readInput.nextLine(); // Reading line by line from FILE
	
		parser = new Scanner(temp1);
		states = new Vector<String>(); // Vector of States
		//PARSING line for each state delimited by spaces
		while(parser.hasNext()){
			temp2 = parser.next();
			states.add(temp2);
			stateMap.put(temp2,i);
			i++;
		}
	
		//READ THE LIST OF ALPHABETS
		temp1= readInput.nextLine();
		alpha = new Vector<String>();
		parser = new Scanner(temp1);
		i=0;
		
		//PARSING
		while(parser.hasNext()){
			temp2 = parser.next();
			alpha.add(temp2);
			alphaMap.put(temp2,i);
			i++;
		}
		
		//INITIALIZE AUTOMATA
		initialize_automata((int)states.size(),(int)alpha.size());
		//print(states.size(),alpha.size());
		
		//READ START AND END STATES
		start_state = readInput.nextLine();
		
		//READ LIST OF END STATES
		temp1= readInput.nextLine();
		end_states = new Vector<String>();
		parser = new Scanner(temp1);
		while(parser.hasNext()){
			temp2 = parser.next();
			end_states.add(temp2);
		}
		
		//READ TRANSITIONS
		while(readInput.hasNext()){
			parse_transition(readInput.nextLine()); // PARSE transitions and map onto AUTOMATA
		}
		
	}
	
	//Function to generate a graphical representation of the Automata using GraphViz
	public static void genGraph(){
		
		GraphViz gv = new GraphViz();
		gv.addln(gv.start_graph()); // Initializing the Graph
      		
      		gv.addln( "o [shape=circle, width=.2, height=.2,fontsize=\"1\",fillcolor=\"black\",style=\"filled\"];");
      		gv.addln( "o -> " + start_state +";");      		
      		gv.addln( "node [shape=circle];");
      		// Traverse through the Automata data structure
      		for(int i=0;i<states.size();i++)
      		{
      			String st = states.get(i);
      			int posn = stateMap.get(st);
      			Vector< String > mappings = new Vector< String >();
      			mappings = automata.get(posn);
      			
      				for(int j=0;j<mappings.size();j++)
      				{
      					if(mappings.get(j) != "-1")
      					{
      						//Add nodes to the graph
      						if( chkinEndstates( mappings.get(j) )) {
      						//If node is and end state colour it
      						gv.addln( mappings.get(j)+ " [shape=doublecircle,fillcolor=\"grey\",style=\"filled\"];");}
      						String transition = st+"->" + mappings.get(j)+ "[label=\"" + alpha.get(j) + "\" ];";
      						gv.addln(transition);
      					}
      				}
      		}
      						
		//End graph construction
      		gv.addln(gv.end_graph());
      		//Print out Digraph
      		System.out.println(gv.getDotSource());
		
		//Store digraph in image file      
      		File out = new File("Automata.gif");
      		gv.writeGraphToFile(gv.getGraph(gv.getDotSource()), out);
      		
      	}
      	public static void readHelpfile()
      	{
	      		try{
		      		Scanner readHelp= new Scanner(new File("Docs"));
				System.out.println("\n-------------------------------------------");
				while(readHelp.hasNext()){
					System.out.println(readHelp.nextLine());
				}
				System.out.println("\n-------------------------------------------\n");	
				
			}
			catch(Exception e){
			
				System.out.println("Doc File not found");
			}
			
	}      		
      	//Main Function
	public static void main(String args[])  throws FileNotFoundException{

		try{

			if("--filename".equals(args[0]))
			{
				Scanner readInput= new Scanner(new File(args[1]));
				input_file(readInput); // Read Automata from file	
				genGraph(); // Generate Graph from Automata
				System.out.println("GRAPH successfully generated in Automata.gif");
				Runtime rr = Runtime.getRuntime();
				rr.exec("evince Automata.gif");
			}
			else if("--help".equals(args[0])) {
							
				readHelpfile();	
			}
			else System.out.println("Unknown Parameters:run as $ java FiniteStateMachine -filename <filename>");
		
		}
		catch(Exception e){
	
			System.out.println("Unknown Parameters:run as $ java FiniteStateMachine --filename <filename>");
	
		}
	
		return;
	}
};
