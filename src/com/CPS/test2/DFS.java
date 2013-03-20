package com.CPS.test2;

//dfs.java
//demonstrates depth-first search
//to run this program: C>java DFSApp
////////////////////////////////////////////////////////////////
class StackX {
	private final int SIZE = 20;
	private int[] st;
	private int top;

	// ------------------------------------------------------------
	public StackX() // constructor
	{
		st = new int[SIZE]; // make array
		top = -1;
	}

	// ------------------------------------------------------------
	public void push(int j) // put item on stack
	{
		st[++top] = j;
	}

	// ------------------------------------------------------------
	public int pop() // take item off stack
	{
		return st[top--];
	}

	// ------------------------------------------------------------
	public int peek() // peek at top of stack
	{
		return st[top];
	}

	public int peekPrev()// peek the top - 1 element in the stack
	{
		if (top > 0)
			return st[top - 1];
		return st[top];
	}

	// ------------------------------------------------------------
	public boolean isEmpty() // true if nothing on stack
	{
		return (top == -1);
	}
	// ------------------------------------------------------------
} // end class StackX
// //////////////////////////////////////////////////////////////

class Vertex {
	public int label; // label (e.g. 'A')
	public boolean wasVisited;

	// ------------------------------------------------------------
	public Vertex(int lab) // constructor
	{
		label = lab;
		wasVisited = false;
	}
	// ------------------------------------------------------------
} // end class Vertex
// //////////////////////////////////////////////////////////////

class Graph {
	private final int MAX_VERTS = 11;
	private Vertex vertexList[]; // list of vertices
	private boolean adjMat[][]; // adjacency matrix
	private int nVerts; // current number of vertices
	private StackX theStack;
	String ltlString = "";

	// ------------------------------------------------------------
	public Graph() // constructor
	{
		vertexList = new Vertex[MAX_VERTS];
		// adjacency matrix
		ltlString = "";
		adjMat = new boolean[MAX_VERTS][MAX_VERTS];
		nVerts = 0;
		for (int y = 0; y < MAX_VERTS; y++)
			// set adjacency
			for (int x = 0; x < MAX_VERTS; x++)
				// matrix to 0
				adjMat[x][y] = false;
		theStack = new StackX();
	} // end constructor
		// ------------------------------------------------------------

	public void addVertex(int lab) {
		vertexList[nVerts++] = new Vertex(lab);
	}

	// ------------------------------------------------------------
	public void toggleEdge(int start, int end) {
		adjMat[start][end] = !adjMat[start][end];
		// adjMat[end][start] = 1;
	}

	// ------------------------------------------------------------
	public void displayVertex(int v) {
		System.out.print(vertexList[v].label);
	}

	// ------------------------------------------------------------
	public String dfs(int balID) // depth-first search
	{ // begin at vertex 0
		vertexList[balID].wasVisited = true; // mark it
		boolean firstImplication = true;
		String ltlString = "";
		// String mainString = "";
		boolean popFlag = false;
		
		
		if(isLineToRootNodeWithArrows(balID) && !hasArrows(balID))
			return "";

		if (DrawView.colorballs[balID - 1].isPickObject()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( PickObj )"
					: "F( PickObj )";
		}
		if (DrawView.colorballs[balID - 1].isActivateSensor()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( ActSen )"
					: "F( ActSen )";
		}
		if (DrawView.colorballs[balID - 1].isDropObject()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( DropObj )"
					: "F( DropObj )";
		}
		if (DrawView.colorballs[balID - 1].isDeactivateSensor()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( DeactSen )"
					: "F( DeactSen )";
		}

		if (DrawView.colorballs[balID - 1].isPickObject()
				|| DrawView.colorballs[balID - 1].isDropObject()
				|| DrawView.colorballs[balID - 1].isActivateSensor()
				|| DrawView.colorballs[balID - 1].isDeactivateSensor()) {
			ltlString = " U( " + ltlString + ")";
		}

		ltlString = "q" + balID + ltlString;
		
		String subTempString = "";
		if (isCompletelyConnected(balID - 1) && isLineToRootNodeWithoutArrows(balID)) {

			for (int i = 0; i < 10; i++) {
				subTempString = "";
				if (DrawView.colorballs[balID - 1].isLineTo(i + 1)) {
					// vertexList[v].wasVisited = true; // mark it
					DrawView.visited[i] = true;//mark it so that duplication is avoided
					subTempString = "";

					if (DrawView.colorballs[i].isPickObject()) {
						subTempString = !subTempString.isEmpty() ? subTempString
								+ " && F( PickObj )"
								: "F( PickObj )";
					}
					if (DrawView.colorballs[i].isActivateSensor()) {
						subTempString = !subTempString.isEmpty() ? subTempString
								+ " && F( ActSen )"
								: "F( ActSen )";
					}
					if (DrawView.colorballs[i].isDropObject()) {
						subTempString = !subTempString.isEmpty() ? subTempString
								+ " && F( DropObj )"
								: "F( DropObj )";
					}
					if (DrawView.colorballs[i].isDeactivateSensor()) {
						subTempString = !subTempString.isEmpty() ? subTempString
								+ " && F( DeactSen )"
								: "F( DeactSen )";
					}

					if (DrawView.colorballs[i].isPickObject()
							|| DrawView.colorballs[i]
									.isDropObject()
							|| DrawView.colorballs[i]
									.isActivateSensor()
							|| DrawView.colorballs[i]
									.isDeactivateSensor()) {
						subTempString = " U( " + subTempString + ")";
					}

					subTempString = "q" + (i+1) + subTempString;
				}
				if (ltlString == "") {
					ltlString = subTempString;
				} else {
					if (subTempString != "")
						ltlString = ltlString + " || "
								+ subTempString;
				}

			}
			if (ltlString != "")
				ltlString = "(" + ltlString + ")";
			
		}else{
			if (DrawView.colorballs[balID - 1].isPickObject()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( PickObj )"
					: "F( PickObj )";
		}
		if (DrawView.colorballs[balID - 1].isActivateSensor()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( ActSen )"
					: "F( ActSen )";
		}
		if (DrawView.colorballs[balID - 1].isDropObject()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( DropObj )"
					: "F( DropObj )";
		}
		if (DrawView.colorballs[balID - 1].isDeactivateSensor()) {
			ltlString = !ltlString.isEmpty() ? ltlString + " && F( DeactSen )"
					: "F( DeactSen )";
		}

		if (DrawView.colorballs[balID - 1].isPickObject()
				|| DrawView.colorballs[balID - 1].isDropObject()
				|| DrawView.colorballs[balID - 1].isActivateSensor()
				|| DrawView.colorballs[balID - 1].isDeactivateSensor()) {
			ltlString = " U( " + ltlString + ")";
		}
		if(ltlString=="")
		ltlString = "q" + balID + ltlString;
		}
//////////////////////
		if (!DrawView.colorballs[balID - 1].isValid()) {
			if(DrawView.colorballs[balID - 1].isAlways()){
				ltlString = "G(NOT(" + ltlString + ".))";
			}else{
			ltlString = " (NOT(" + ltlString + ".))";
			}
		} else {
			if (!DrawView.colorballs[balID - 1].isAlways()) {
				if (DrawView.colorballs[balID - 1].isEventually()) {
					ltlString = "F(" + ltlString + ".)";
				} else {
					ltlString = "NOT(" + generateNotString(balID, balID)
							+ ")U(" + ltlString + ".)";
				}
			} else {
				if(DrawView.colorballs[balID - 1].isEventually() && DrawView.colorballs[balID -1].isAlwaysEventually()){
				ltlString = "GF(" + ltlString + ".)";// only for the root of
														// each tree
				}else if(DrawView.colorballs[balID - 1].isEventually() && !DrawView.colorballs[balID -1].isAlwaysEventually()){
					ltlString = "FG(" + ltlString + ".)";// only for the root of
					// each tree
				}
				else{
					ltlString = "G(" + ltlString + ".)";// only for the root of
					// each tree
				}
			}
		}
		theStack.push(balID); // push it
		int mostRecentPop = -1;

		while (!theStack.isEmpty()) // until stack empty,
		{

			// get an unvisited vertex adjacent to stack top
			int v = getAdjUnvisitedVertex(theStack.peek());
			if (v == -1) {// if no such vertex,
				// if(!(mainString=="")){
				// mainString = mainString + " && " + ltlString;
				// }
				// else
				// mainString = ltlString;
				// vertexList[theStack.peek()].wasVisited = true;
				popFlag = true;
				mostRecentPop = theStack.pop();
				String[] splitStrings = ltlString.split("\\.");
				ltlString = splitStrings[0] + ")."
						+ splitStrings[1].replaceFirst("\\)", "");

			} else // if it exists,
			{
				String[] splitStrings = ltlString.split("\\.");
				String tempString = "";

				vertexList[v].wasVisited = true; // mark it

				if (DrawView.colorballs[v - 1].isPickObject()) {
					tempString = !tempString.isEmpty() ? tempString
							+ " && F( PickObj )" : "F( PickObj )";
				}
				if (DrawView.colorballs[v - 1].isActivateSensor()) {
					tempString = !tempString.isEmpty() ? tempString
							+ " && F( ActSen )" : "F( ActSen )";
				}
				if (DrawView.colorballs[v - 1].isDropObject()) {
					tempString = !tempString.isEmpty() ? tempString
							+ " && F( DropObj )" : "F( DropObj )";
				}
				if (DrawView.colorballs[v - 1].isDeactivateSensor()) {
					tempString = !tempString.isEmpty() ? tempString
							+ " && F( DeactSen )" : "F( DeactSen )";
				}

				if (DrawView.colorballs[v - 1].isPickObject()
						|| DrawView.colorballs[v - 1].isDropObject()
						|| DrawView.colorballs[v - 1].isActivateSensor()
						|| DrawView.colorballs[v - 1].isDeactivateSensor()) {
					tempString = " U( " + tempString + ")";
				}
				if(DrawView.colorballs[v-1].isAlways()){
				tempString = "G q"+ v + tempString;
				}else{
					tempString = "q"+ v + tempString;
				}
				// vertexList[v].wasVisited = true; // mark it
				subTempString = "";
				if (isCompletelyConnected(v - 1)) {

					for (int i = 0; i < 10; i++) {
						subTempString = "";
						if (DrawView.colorballs[v - 1].isLineTo(i + 1)) {
							// vertexList[v].wasVisited = true; // mark it
							subTempString = "";

							if (DrawView.colorballs[i].isPickObject()) {
								subTempString = !tempString.isEmpty() ? tempString
										+ " && F( PickObj )"
										: "F( PickObj )";
							}
							if (DrawView.colorballs[i].isActivateSensor()) {
								subTempString = !tempString.isEmpty() ? tempString
										+ " && F( ActSen )"
										: "F( ActSen )";
							}
							if (DrawView.colorballs[i].isDropObject()) {
								subTempString = !tempString.isEmpty() ? tempString
										+ " && F( DropObj )"
										: "F( DropObj )";
							}
							if (DrawView.colorballs[i].isDeactivateSensor()) {
								subTempString = !tempString.isEmpty() ? tempString
										+ " && F( DeactSen )"
										: "F( DeactSen )";
							}

							if (DrawView.colorballs[i].isPickObject()
									|| DrawView.colorballs[i]
											.isDropObject()
									|| DrawView.colorballs[i]
											.isActivateSensor()
									|| DrawView.colorballs[i]
											.isDeactivateSensor()) {
								subTempString = " U( " + tempString + ")";
							}
							if(DrawView.colorballs[i].isAlways()){
							subTempString = "G q" + (i+1) + subTempString;
							}else{
								subTempString = "q" + (i+1) + subTempString;
							}
						}
						if (tempString == "") {
							tempString = subTempString;
						} else {
							if (subTempString != "")
								tempString = tempString + " || "
										+ subTempString;
						}

					}
					if (tempString != "")
						tempString = "(" + tempString + ")";
				}

				if (!DrawView.colorballs[v - 1].isValid()) {
					if (DrawView.colorballs[v - 1].isEventually()) {
						if (firstImplication) {
							// tempString = "=>(NOT(" + tempString + ")U(" +
							// generateNotString(theStack.peek(), v)+ ").)";
							if(!DrawView.colorballs[theStack.peek()-1].isValid()){
								tempString = " && (X(" + tempString + ").)";
							}else{
							tempString = "=> X(NOT(" + tempString + ").)";
							ltlString = ltlString.replace("F", "G");
							}
							splitStrings = ltlString.split("\\.");
							firstImplication = false;
						} else {
							if (DrawView.colorballs[theStack.peek() - 1]
									.isValid()) {
								/*tempString = " && (NOT(" + tempString + ")U("
										+ generateNotString(theStack.peek(), v)
										+ ").)";*/
								tempString = " && F(NOT(" + tempString
										+ ").)";
							} else {
								tempString = " && (X(" + tempString + ").)";
										
							}
						}
					} else {
						// tempString = "G (NOT(" + tempString + ".))";//need to
						// test it more
						// tempString = "=>X(NOT(" +
						// generateNotString(theStack.peek(), v) + "))U(" +
						// tempString + ".)";
						tempString = "=> X(NOT(" + tempString + ").)";
						ltlString = ltlString.replace("F", "G");
						splitStrings = ltlString.split("\\.");
					}
				} else {
					if (!DrawView.colorballs[v - 1].isEventually()) {
						if (firstImplication) {
							if (DrawView.colorballs[theStack.peek() - 1]
									.isValid()) {
								tempString = "(X(NOT("
										+ generateNotString(theStack.peek(), v)
										+ "))U(" + tempString + ".))";
							} else {
								tempString = "X(NOT("
										+ generateNotString(theStack.peek(), v)
										+ "))U(" + tempString + ".)";
							}
							// firstImplication=false;
						} else {
							tempString = "X(NOT("
									+ generateNotString(theStack.peek(), v)
									+ "))U(" + tempString + ".)";
						}
					} else {
						if (DrawView.colorballs[theStack.peek() - 1].isValid()) {
							tempString = "((F(" + tempString + ".)))";
						} else {
							tempString = "((" + tempString + ".))";// check
						}
					}
				}
				if (!popFlag) {
					// int x = theStack.peekPrev();
					// int y = theStack.peek();
					if (DrawView.colorballs[theStack.peek() - 1].isValid()) {
						if (DrawView.colorballs[v - 1].isValid()) {
							//if (!DrawView.colorballs[v - 1].isEventually()) {
								if (DrawView.colorballs[v - 1].isImplies()) {
									ltlString = splitStrings[0] + " => "
											+ tempString + splitStrings[1];
									firstImplication=false;
								} else {
									ltlString = splitStrings[0] + " && "
											+ tempString + splitStrings[1];
									firstImplication = false;
								}
							//} else {
							//	ltlString = splitStrings[0] + " && "
							//			+ tempString + splitStrings[1];
							//}
						} else {
							if (!DrawView.colorballs[v - 1].isEventually()) {
								ltlString = splitStrings[0] + tempString// test
																		// this
																		// case
										+ splitStrings[1];
							} else {
								ltlString = splitStrings[0] + tempString
										+ splitStrings[1];
							}

						}
					} else {
						if (!DrawView.colorballs[v - 1].isEventually()) {
							ltlString = splitStrings[0] + ")=>(" + tempString
									+ splitStrings[1];
						} else {
							if(DrawView.colorballs[v-1].isValid()){
							ltlString = splitStrings[0] + ") U (" + tempString
									+ splitStrings[1];
							}else{
								ltlString = splitStrings[0] + tempString + splitStrings[1];
							}
						}

					}
				} else {
					if (!DrawView.colorballs[theStack.peek() - 1].isORMode()) {
						ltlString = splitStrings[0] + ") && (" + tempString
								+ splitStrings[1];
					} else {
						if (mostRecentPop > 0
								&& !DrawView.colorballs[mostRecentPop - 1]
										.isEventually()) {
							ltlString = splitStrings[0] + " || " + tempString
									+ splitStrings[1];
						} else {
							ltlString = splitStrings[0] + ") || (" + tempString
									+ splitStrings[1];
						}
					}
					popFlag = false;
				}

				theStack.push(v); // push it
				mostRecentPop = -1;
			}
		} // end while
		firstImplication = true;
		// stack is empty, so we're done
		for (int j = 0; j < nVerts; j++)
			// reset flags
			vertexList[j].wasVisited = false;
		return ltlString.replace(".", "");
		
		// return mainString.replace(".","");
	} // end dfs

	// ------------------------------------------------------------
	// returns an unvisited vertex adj to v
	public String getLtlString() {
		return ltlString;
	}

	public String generateNotString(int fromBalID, int toBalID) {// generates
																	// the
																	// string
																	// for part
																	// of
																	// ordered
																	// locomotion
		String notString = "";
		for (int i = 0; i < 10; i++) {
			int tempBalID = i + 1;
			if (i != (toBalID - 1) /* && i!=(fromBalID-1) */ && !DrawView.colorballs[toBalID-1].isLineTo(i+1)
					&& MainActivity.waypoint[i]) {
				if (notString == "") {

					notString = "" + "q" + tempBalID;
				} else {
					notString = notString + " || " + "q" + tempBalID;
				}
			}
		}
		if (notString.equals(""))
			return "False";
		return notString;
	}

	public int getAdjUnvisitedVertex(int v) {
		for (int j = 0; j < nVerts; j++)
			if (adjMat[v][j] == true && vertexList[j].wasVisited == false)
				return j;
		return -1;
	} // end getAdjUnvisitedVertex()

	public boolean isCompletelyConnected(int index) {
		boolean flags[] = new boolean[10];
		int edgeCount = 0;
		int nodeCount = 1;
		flags[index] = true;
		/*for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[index].isLineTo(i + 1)) {
				flags[i] = true;
				nodeCount++;
			}
		}*/
		for(int i=0;i<10;i++){
			for(int j=0;j<10;j++){
				if(DrawView.colorballs[i].isLineTo(j+1) && ((flags[i] &&!flags[j]) || (flags[j] &&!flags[i]))){
					flags[i] = flags[j] = true;
					nodeCount++;
				}
			}
		}
		
		for (int i = 0; i < 10; i++) {
			for (int j = 0; j < 10; j++) {
				if (flags[i] && flags[j]) {
					if (DrawView.colorballs[i].isLineTo(j + 1)) {
						edgeCount++;
					}
				}
			}
		}
		if ((nodeCount * (nodeCount - 1)) / 2 == edgeCount/2) {
			return true;
		}
		return false;

	}
	
	public boolean isRoot(int balID) {
		for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[i].isArrowTo(balID))
				return false;

		}
		return true;
	}
	
	public boolean isLineToRootNodeWithoutArrows(int balID){
		for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[balID - 1].isLineTo(i + 1)
					&& isRoot(i + 1)) {
				for(int j=0;j<10;j++){
					if(DrawView.colorballs[i].isArrowTo(j+1))
						return false;
				}
				
			}
		}return true;
	}
	
	public boolean isLineToRootNodeWithArrows(int balID){
		for (int i = 0; i < 10; i++) {
			if (DrawView.colorballs[balID - 1].isLineTo(i + 1)
					&& isRoot(i + 1)) {
				for(int j=0;j<10;j++){
					if(DrawView.colorballs[i].isArrowTo(j+1))
						return true;
				}
				
			}
		}return false;
	}
	
	public boolean hasArrows( int balID){
		for(int i=0;i<10;i++){
			if(DrawView.colorballs[balID-1].isArrowTo(i+1)){
				return true;
			}
		}
		return false;
	}
	
	

	// ------------------------------------------------------------
} // end class Graph
// //////////////////////////////////////////////////////////////

class DFSApp {
	public static void main(String[] args) {
		Graph theGraph = new Graph();
		theGraph.addVertex('A'); // 0 (start for dfs)
		theGraph.addVertex('B'); // 1
		theGraph.addVertex('C'); // 2
		theGraph.addVertex('D'); // 3
		theGraph.addVertex('E'); // 4

		theGraph.toggleEdge(0, 1); // AB
		theGraph.toggleEdge(1, 2); // BC
		theGraph.toggleEdge(0, 3); // AD
		theGraph.toggleEdge(3, 4); // DE

		System.out.print("Visits: ");
		// theGraph.dfs(); // depth-first search
		System.out.println();
	} // end main()
} // end class DFSApp
// //////////////////////////////////////////////////////////////


	

