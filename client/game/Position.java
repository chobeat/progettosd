package game;

public class Position {

	int x;
	int y;
	int MAX_GRID_SIZE=99;
	
	public Position(int x,int y){
		this.x=x;
		this.y=y;
	}
	
	public void addX(){x=(x+1)%MAX_GRID_SIZE;}
	public void addY(){y=(y+1)%MAX_GRID_SIZE;}
	public void lessX(){x=(x-1);
			x=x<0?MAX_GRID_SIZE-x+1:x;
	}
	public void lessY(){y=(y-1);
	y=y<0?MAX_GRID_SIZE-y+1:y;
	
	}
	
	@Override
	public boolean equals(Object o){
		Position that=(Position)o;
		return that.x==this.x&&that.y==that.x;
			
		
	}
	public static void main (String args[]){
		
	}
	
}