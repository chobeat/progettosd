package game;

public class Position implements Cloneable{

	int x;
	int y;
	public static final int MAX_GRID_SIZE=3;
	
	public Position(int x,int y) {
		this.x=x;
		this.y=y;
	}
	public Position(){
		
	}
	 public Position clone() throws CloneNotSupportedException {
         return (Position) super.clone();
 }
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	public void addX(){x=(x+1)%MAX_GRID_SIZE;}
	public void addY(){y=(y+1)%MAX_GRID_SIZE;}
	public void lessX(){x=(x-1);
			x=x<0?MAX_GRID_SIZE-1:x;
	}
	public void lessY(){y=(y-1);
	y=y<0?MAX_GRID_SIZE-1:y;
	
	}
	
	@Override
	public boolean equals(Object o){
		Position that=(Position)o;
		return that.x==this.x&&that.y==this.y;
			
		
	}
	
	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
	public static void main (String args[]){
		
	}
	
}
