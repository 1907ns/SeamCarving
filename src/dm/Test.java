package dm;

import java.io.IOException;

class Test
{
   static boolean visite[];
   public static void dfs(Graph g, int u)
	 {
		visite[u] = true;
		System.out.println("Je visite " + u);
		for (Edge e: g.next(u))
		  if (!visite[e.to])
			dfs(g,e.to);
	 }
   
   public static void testGraph()
	 {
		int n = 5;
		int i,j;
		GraphArrayList g = new GraphArrayList(n*n+2);
		
		for (i = 0; i < n-1; i++)
		  for (j = 0; j < n ; j++)
			g.addEdge(new Edge(n*i+j, n*(i+1)+j, 1664 - (i+j)));

		for (j = 0; j < n ; j++)		  
		  g.addEdge(new Edge(n*(n-1)+j, n*n, 666));
		
		for (j = 0; j < n ; j++)					
		  g.addEdge(new Edge(n*n+1, j, 0));
		
		g.addEdge(new Edge(13,17,1337));
		g.writeFile("test.dot");
		// dfs à partir du sommet 3
		visite = new boolean[n*n+2];
		dfs(g, 3);
	 }

	 public static void testToGraph() throws IOException {
		 SeamCarving s = new SeamCarving();
		 int[][] tab= new int[][]{{8, 2, 1, 15},{13,3,1,10},{140,52,5,25}};
		 GraphArrayList g = (GraphArrayList) s.tograph(tab);
		 g.writeFile("test.dot");
		 visite = new boolean[4*3+2];
		 dfs(g, 3);
		 System.out.println("---------------");
		 System.out.println("---------------");
		 s.Bellman_Ford(g, 1, 10);
	 }

   public static void main(String[] args) throws IOException {
		 testToGraph();
	 }
}
