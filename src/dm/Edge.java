package dm;
public class Edge
{
   int from;
   int to;
   int cost;
   Edge(int x, int y, int cost)
	 {
		this.from = x;
		this.to = y;
		this.cost = cost;
	 }

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}
	public int getCost(){
   		return  cost;
	}
	public void setCost(int cost){
   		this.cost=cost;
	 }
   
}
