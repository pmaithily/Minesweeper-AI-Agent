package src;
import src.Action.ACTION;
import java.util.*;


class TileInfo {
	int number, voteNumber;
	boolean uncover;
        public TileInfo(int number, boolean uncover){
                this.number = number;
                this.uncover = uncover;
                this.voteNumber = 0;
        }
}

public class MyAI extends AI {
	TileInfo[][] Tiles = new TileInfo[35][35];
	int[][] pat=new int[3][3];
    boolean[][] pat2=new boolean[3][3];
	int rows, cols, mines, prev_x, prev_y, uncoverCount, minesLeft, x, y;
	Action obj = null;
	Action next = null;
	Action act = null;
	Queue<Action> q = new LinkedList<>();
	Queue<Action> voteq = new LinkedList<>();
	ArrayList<Integer> list;
	ArrayList<Integer> list_one;
	HashSet<ArrayList<Integer>> set = new HashSet<>();
	public MyAI(int rowDimension, int colDimension, int totalMines, int startX, int startY) {
		this.rows = rowDimension;
		this.cols = colDimension;
		this.mines = totalMines;
		this.x = startX;
		this.y = startY;
		this.uncoverCount = 0;
		this.minesLeft = totalMines;
		for(int i = 0; i < this.rows; i++){
			for(int j = 0; j < this.cols; j++){
				Tiles[i][j] = new TileInfo(-10, false);
			}
		}
	}
	
	public Action getAction(int number) {
//		System.out.println(q);
//		System.out.println("Tile numbers");
//		for(int i = 0; i < this.rows; i++){
//           for(int j = 0; j < this.cols; j++){
//              System.out.print(Tiles[i][j].voteNumber + "  ");
//           }
//        System.out.print("\n");
//        }
		
		uncoverCount+=1;
		if(number == 0){
			int x1=ourx(x,y);
			int y1=oury(x,y);
			//System.out.println("-1 set");
			for(int i = x1-1; i <= x1+1; i++){
				for(int j = y1-1; j <= y1+1; j++){
					if(isValid(i, j)){
						Tiles[i][j].voteNumber = -1;
						//System.out.println("valid i"+i+"valid j"+j);
					}	
				}
			}
			
			if(isValid(x1, y1) == true){
			this.Tiles[x1][y1].number = number;
			this.Tiles[x1][y1].uncover = true;
			}
			
			list = new ArrayList<>();
			list.add(x);
			list.add(y);
			set.add(list);
			for(int i = x1 - 1; i <= x1 + 1; i++){
				for(int j = y1 - 1; j <= y1 + 1; j++){
					list = new ArrayList<>();
					int x2=theirx(i,j);
					int y2=theiry(i,j);
					list.add(x2);
					list.add(y2);
					if(isValid(i, j) == true && !set.contains(list)){
						q.add(new Action(ACTION.UNCOVER, x2, y2));
						set.add(list);
					}
				}
			}
			if(q.size() > 0){
				next = popq(q);
				if(next != null)
					x = next.x;
					y = next.y;
					return next;
			}
			else{
				boolean filled = scanBoard();
				//System.out.println("q filled in scanning "+filled);
					if(q.size() > 0){
						next = popq(q);
						x = next.x;
						y = next.y;
						if(next != null)
							return next;
					}
				
					else{
						boolean patfound = checkPatterns();
						//System.out.println("pat found"+patfound);
						if(patfound){
							if(q.size() > 0){
								next = popq(q);
								x = next.x;
								y = next.y;
								if(next != null)
								return next;
							}
						}
						
						else{
							 //if(uncoverCount > 0.67 * this.rows * this.cols){
								boolean corners=cornerStart();
								//System.out.println("corner found"+corners);
								if(corners){
									if(q.size() > 0){
										next = popq(q);
										x = next.x;
										y = next.y;
										if(next != null)
										return next;
									}
								}
							//}
							boolean revalDone = reval();
							//System.out.println("reval found"+revalDone);
							if(revalDone){
								next = popq(q);
								if(next!=null){
									
									x = next.x;
									y = next.y;
									//if(!set.contains(getList(x, y)) && next != null){
										set.add(getList(x, y));
									return next;
									//}
								}
							}
							
						}
				}
			}
			
		}
		
		if(number != 0){
			int x1 = ourx(x,y);
			int y1 = oury(x,y);
			if(isValid(x1, y1) == true){
				this.Tiles[x1][y1].number = number;
				this.Tiles[x1][y1].uncover = true;
			}
			
			if(number==-1){
				this.minesLeft--;
				for(int i = x1-1; i <= x1+1; i++){
					for(int j = y1-1; j <= y1+1; j++){
						if(isValid(i, j) && Tiles[i][j].number >0){
						//	System.out.println("subtracting x "+theirx(i,j)+"y"+theiry(i,j));
							Tiles[i][j].number =Tiles[i][j].number- 1;
						//	System.out.println("new pecept"+Tiles[i][j].number);
						}
						
					}
				}
			}
			
			if(number > 0){
				
				for(int i = x1-1; i <= x1+1; i++){
					for(int j = y1-1; j <= y1+1; j++){
						if(isValid(i, j) && Tiles[i][j].voteNumber != -1){
							Tiles[i][j].voteNumber += number;
							
						}
						if(isValid(i,j) && Tiles[i][j].number==-1){
						//	System.out.println("subtracting x "+theirx(i,j)+"y"+theiry(i,j));
							Tiles[ourx(x,y)][oury(x,y)].number=Tiles[ourx(x,y)][oury(x,y)].number-1;
						//	System.out.println("new pecept"+Tiles[ourx(x,y)][oury(x,y)].number);
						}
						
					}
				}
	
			}
			
			
			
			if(q.size() > 0){
				next = popq(q);
				if(next != null)
					x = next.x;
					y = next.y;
					return next;
			}
			else{
				boolean filled = scanBoard();
				//System.out.println("scan found"+filled);
					if(q.size() > 0){
						next = popq(q);
						x = next.x;
						y = next.y;
						if(next != null)
							return next;
					}
				
					else{
						boolean patfound = checkPatterns();
						//System.out.println("patfound"+patfound);
						if(patfound){
								if(q.size() > 0){
								next = popq(q);
								if(next != null)
									x = next.x;
									y = next.y;
									return next;
							}
						}
						else{
							// if(uncoverCount > 0.67 * this.rows * this.cols){
								boolean corners=cornerStart();
								//System.out.println("corner start"+corners);
								if(corners){
									if(q.size() > 0){
										next = popq(q);
										if(next != null){
											x = next.x;
											y = next.y;
											return next;
										}
									}
								}
							//}
							boolean revalDone = reval();
							//System.out.println("reval :" + revalDone);
							if(revalDone){
								next = popq(q);
								if(next!=null){
									
									x = next.x;
									y = next.y;
									//if(!set.contains(getList(x, y)) && next != null){
										set.add(getList(x, y));
									return next;
									//}
								}
								
							}
						}
					}
				}
			
		}
		
		return new Action(ACTION.LEAVE);
	}
	
	public boolean cornerStart(){
		//if(!this.Tiles[this.rows-1][this.cols-1].uncover){
			if(isValid(this.rows-1, this.cols-1) && !set.contains(getList(this.rows-1, this.cols-1))){
			q.add(new Action(ACTION.UNCOVER, theirx(this.rows-1, this.cols-1), theiry(this.rows-1, this.cols-1)));
			set.add(getList(this.rows-1, this.cols-1));
			}
			else if(isValid(this.rows-1, 0) && !set.contains(getList(this.rows-1, 0))){
			q.add(new Action(ACTION.UNCOVER, theirx(this.rows-1, 0), theiry(this.rows-1, 0)));
			set.add(getList(this.rows-1, 0));
			}
			else if(isValid(0, this.cols-1) && !set.contains(getList(0, this.cols-1))){
			q.add(new Action(ACTION.UNCOVER, theirx(0, this.cols-1), theiry(0, this.cols-1)));
			set.add(getList(0, this.cols-1));
			}
			else if(isValid(0, 0) && !set.contains(getList(0, 0))){
			q.add(new Action(ACTION.UNCOVER, theirx(0, 0), theiry(0, 0)));
			set.add(getList(0, 0));
			}

			
			if(q.size() > 0)
			return true;
			else
			return false;
	}
	 public int ourx(int x,int y){
	    	//x is rows-y y is x-1 theirs to ours
	    	return this.rows-y;
	    }
	    
	    public int oury(int x,int y){
	    	//x is rows-y y is x-1 theirs to ours
	    	return x-1;
	    }
	    
	    public int theirx(int x,int y){
			//ours to theirs x y+1 and y rows-x
	    	return y+1;
	    }
	    
	    public int theiry(int x,int y){
			//ours to theirs x y+1 and y rows-x
	    	return this.rows-x;
	    }
	
	public boolean checkNeighbors(int x, int y){
	//	System.out.println("check Nei called x "+theirx(x,y)+"y"+theiry(x,y));
		int count_uncovered = 0, count_flagged = 0;
		int percept = this.Tiles[x][y].number;
	//	System.out.println("percept"+percept);
		ArrayList<ArrayList<Integer>> uncovered = new ArrayList<>();
		ArrayList<ArrayList<Integer>> flagged = new ArrayList<>();
		ArrayList<Integer> list = new ArrayList<>();
		for(int i = x - 1; i <= x + 1; i++){
			for(int j = y - 1; j <= y + 1; j++){
						int x2=theirx(i,j);
						int y2=theiry(i,j);
						if(isValid(i,j)){
						if(this.Tiles[i][j].uncover == false){
							list = new ArrayList<>();
							list.add(x2);
							list.add(y2);
							uncovered.add(list);
							count_uncovered++;
						}
					if(this.Tiles[i][j].number == -1){
							list = new ArrayList<>();
							list.add(x2);
							list.add(y2);
							flagged.add(list);
							count_flagged++;
					}
				}
			}
		}
		//System.out.println("uncovered"+count_uncovered);
		//System.out.println("percept"+percept);
		if(percept==0){
			for(ArrayList<Integer> l : uncovered){
				int i = (Integer)l.get(0);
				int j = (Integer)l.get(1);
				if(!set.contains(l)){
					set.add(l);
				//	System.out.println("adding uncover"+ l);
					q.add(new Action(ACTION.UNCOVER, i, j));
				}
			}
		}
		
		if(percept==count_uncovered){
			for(ArrayList<Integer> l : uncovered){
				int i = (Integer)l.get(0);
				int j = (Integer)l.get(1);
				if(!set.contains(l)){
					set.add(l);
				//	System.out.println("adding flag" +l);
					q.add(new Action(ACTION.FLAG, i, j));
				}
			}
		}
		
		if(q.size() > 0)
			return true;
		else
			return false;
		
	}
	
	public boolean checkPatterns(){
		
		for(int x=1;x<this.rows-1;x++){
			for(int y=1;y<this.cols-1;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				checkbasic12Pattern(x,y);
			}
		}
		
		
		for(int x=1;x<this.rows-1;x++){
			for(int y=1;y<this.cols-2;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				check12Pattern(x,y);
			}
		}
		
		for(int x=1;x<this.rows-2;x++){
			for(int y=1;y<this.cols-1;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				checkinverted12Pattern(x,y);
			}
		}
		
		
		
		for(int x=1;x<this.rows-1;x++){
			for(int y=1;y<this.cols-1;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				checkdouble12Pattern(x,y);
			}
		}
		
		
			
		
		for(int x=1;x<this.rows-2;x++){
			for(int y=1;y<this.cols-1;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				checkinverted1221Pattern(x,y);
			}
		}
		
		for(int x=1;x<this.rows-2;x++){
			for(int y=1;y<this.cols-2;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				checkCubePattern(x,y);
			}
		}
		
		for(int x=1;x<this.rows-1;x++){
			for(int y=1;y<this.cols-3;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				check121Pattern(x,y);
			}
		}
		
		for(int x=1;x<this.rows-1;x++){
			for(int y=1;y<this.cols-3;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				checkinverted121Pattern(x,y);
			}
		}
		
		
		
		for(int x=1;x<this.rows-1;x++){
			for(int y=1;y<this.cols-3;y++){
				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
				{
					continue;
				}
				check323Pattern(x,y);
			}
		}
//		
//		for(int x=1;x<this.rows-1;x++){
//			for(int y=1;y<this.cols-3;y++){
//				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
//				{
//					continue;
//				}
//				check212Pattern(x,y);
//			}
//		}
		
		
		
		for(int y = 1; y < this.cols-1; y++){
			if(this.Tiles[this.rows-2][y].number == -10 || this.Tiles[this.rows-2][y].number == -1 || this.Tiles[this.rows-2][y].number == 0)
			{
				continue;
			}	
			checkBottomPatterns(this.rows - 2, y);
		}
		
		for(int y = 1; y < this.cols-1; y++){
			if(this.Tiles[1][y].number == -10 || this.Tiles[1][y].number == -1 || this.Tiles[1][y].number == 0)
			{
				continue;
			}	
			checkTopPatterns(1, y);
		}
		
		for(int x = 1; x < this.rows-1; x++){
			if(this.Tiles[x][1].number == -10 || this.Tiles[x][1].number == -1 || this.Tiles[x][1].number == 0)
			{
				continue;
			}	
			checkLeftPatterns(x, 1);
		}
		
		for(int x = 1; x < this.rows-1; x++){
			if(this.Tiles[x][this.cols-2].number == -10 || this.Tiles[x][this.cols-2].number == -1 || this.Tiles[x][this.rows-2].number == 0)
			{
				continue;
			}	
			checkRightPatterns(x,this.cols - 2);
		}
		
		
		
//		for(int x=1; x<this.rows-1; x++){
//			for(int y = 1; y < this.cols-1; y++){
//				if(this.Tiles[x][y].number == -10 || this.Tiles[x][y].number == -1 || this.Tiles[x][y].number == 0)
//				{
//					continue;
//				}	
//				checkMiddlePatterns(x, y);
//			}
//		}
		
		HashMap<String, ArrayList<Integer>> map = new HashMap<>();
		ArrayList<Integer> l1 = new ArrayList<>();
		l1.add(1);l1.add(1);
		ArrayList<Integer> l2 = new ArrayList<>();
                    l2.add(1);l2.add(this.cols-2);
		ArrayList<Integer> l3 = new ArrayList<>();
                    l3.add(this.rows-2);l3.add(1);
		ArrayList<Integer> l4 = new ArrayList<>();
                    l4.add(this.rows-2);l4.add(this.cols-2);
		map.put("tl", l1);
		map.put("tr", l2);
		map.put("bl", l3);
		map.put("br", l4);
		for(String s: map.keySet()){
			checkCornerPatterns(s, map.get(s).get(0), map.get(s).get(1));
		}
				
		if(q.size() > 0)
			return true;
		else
			return false;
	}
	
public void checkbasic12Pattern(int x, int y){
	
	int[][] patty=new int[3][3];
	for(int i=-1;i<2;i++){
		for(int j=-1;j<2;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	 if (patty[0][0]==-10 && patty[0][1]==-10 && patty[0][2]==-10 && patty[1][0]==1 &&  patty[1][1]==2 && patty[1][2]!=-10 && patty[2][0]!=-10 && patty[2][1]!=-10 && patty[2][2]!=-10  ){
		 if(isValid(x-1,y+1)){
    		 if(!set.contains(getList(x-1,y+1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
            	 set.add(getList(x-1,y+1));
             }
		 }
	 }
	 
	 else if (patty[0][0]==-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[1][0]==-10 &&  patty[1][1]==2 && patty[1][2]!=-10 && patty[2][0]==-10 && patty[2][1]==1 && patty[2][2]!=-10  ){
		 if(isValid(x-1,y-1)){
    		 if(!set.contains(getList(x-1,y-1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
            	 set.add(getList(x-1,y-1));
             }
		 }
	 }
	 
	 else if (patty[0][0]!=-10 && patty[0][1]==1 && patty[0][2]==-10 && patty[1][0]!=-10 &&  patty[1][1]==2 && patty[1][2]==-10 && patty[2][0]!=-10 && patty[2][1]!=-10 && patty[2][2]==-10  ){
		 if(isValid(x+1,y+1)){
    		 if(!set.contains(getList(x+1,y+1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x+1,y+1),theiry(x+1,y+1)));
            	 set.add(getList(x+1,y+1));
             }
		 }
	 }
	 
	 else if (patty[0][0]==-10 && patty[0][1]==1 && patty[0][2]!=-10 && patty[1][0]==-10 &&  patty[1][1]==2 && patty[1][2]!=-10 && patty[2][0]==-10 && patty[2][1]!=-10 && patty[2][2]!=-10  ){
		 if(isValid(x+1,y-1)){
    		 if(!set.contains(getList(x+1,y-1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x+1,y-1),theiry(x+1,y-1)));
            	 set.add(getList(x+1,y-1));
             }
		 }
	 }
	 
	 else if (patty[0][0]!=-10 && patty[0][1]!=-10 && patty[0][2]==-10 && patty[1][0]!=-10 &&  patty[1][1]==2 && patty[1][2]==-10 && patty[2][0]!=-10 && patty[2][1]==1 && patty[2][2]==-10  ){
		 if(isValid(x-1,y+1)){
    		 if(!set.contains(getList(x-1,y+1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
            	 set.add(getList(x-1,y+1));
             }
		 }
	 }
	 
	 else if (patty[0][0]==-10 && patty[0][1]==-10 && patty[0][2]==-10 && patty[1][0]!=-10 &&  patty[1][1]==2 && patty[1][2]==1 && patty[2][0]!=-10 && patty[2][1]!=-10 && patty[2][2]!=-10  ){
		 if(isValid(x-1,y-1)){
    		 if(!set.contains(getList(x-1,y-1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
            	 set.add(getList(x-1,y-1));
             }
		 }
	 }
	 
	 else if (patty[0][0]!=-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[1][0]==1 &&  patty[1][1]==2 && patty[1][2]!=-10 && patty[2][0]==-10 && patty[2][1]==-10 && patty[2][2]==-10  ){
		 if(isValid(x+1,y+1)){
    		 if(!set.contains(getList(x+1,y+1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x+1,y+1),theiry(x+1,y+1)));
            	 set.add(getList(x+1,y+1));
             }
		 }
	 }
	 
	 else if (patty[0][0]!=-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[1][0]!=-10 &&  patty[1][1]==2 && patty[1][2]==1 && patty[2][0]==-10 && patty[2][1]==-10 && patty[2][2]==-10  ){
		 if(isValid(x+1,y-1)){
    		 if(!set.contains(getList(x+1,y-1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x+1,y-1),theiry(x+1,y-1)));
            	 set.add(getList(x+1,y-1));
             }
		 }
	 }
	 
	 
	
}
	
public void check22Pattern(int x, int y){
	
	int[][] patty=new int[3][3];
	for(int i=-1;i<2;i++){
		for(int j=-1;j<2;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	 if (patty[0][0]!=-10 && patty[1][0]!=-10 && patty[2][0]!=-10 && patty[0][1]==2 &&  patty[1][1]==2 && patty[0][2]==-10 && patty[1][2]==-10 && patty[2][1]!=-1  ){
		 if(isValid(x-1,y+1)){
    		 if(!set.contains(getList(x-1,y+1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
            	 set.add(getList(x-1,y+1));
             }
		 }
		 if(isValid(x,y+1)){
    		 if(!set.contains(getList(x,y+1))){
    			 q.add(new Action(ACTION.FLAG,theirx(x,y+1),theiry(x,y+1)));
    			 set.add(getList(x,y+1));
    		 }
		 }
	 }
	
	
}
	
public void	checkdouble12Pattern(int x,int y){
	
	int[][] patty=new int[3][3];
	for(int i=-1;i<2;i++){
		for(int j=-1;j<2;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	  if (patty[1][0]==1 && patty[1][1]==2 && patty[1][2]==2 && patty[0][0]==1 &&  patty[0][1]==-10 && patty[0][2]==-10  ){
		 if(isValid(x-1,y+1)){
    		 if(!set.contains(getList(x-1,y+1))){
            	 q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
            	 set.add(getList(x-1,y+1));
             }
		 }
		 if(isValid(x-1,y)){
    		 if(!set.contains(getList(x-1,y))){
    			 q.add(new Action(ACTION.FLAG,theirx(x-1,y),theiry(x-1,y)));
    			 set.add(getList(x-1,y));
    		 }
		 }
	 }
	 
	 else if (patty[1][0]==2 && patty[1][1]==2 && patty[1][2]==1 && patty[0][2]==1 &&  patty[0][1]==-10 && patty[0][2]==-10 ){
		 if(isValid(x-1,y)){
    		 if(!set.contains(getList(x-1,y))){
            	 q.add(new Action(ACTION.FLAG,theirx(x-1,y),theiry(x-1,y)));
            	 set.add(getList(x-1,y));
             }
		 }
		 if(isValid(x-1,y-1)){
    		 if(!set.contains(getList(x-1,y-1))){
    			 q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
    			 set.add(getList(x-1,y-1));
    		 }
		 }
	 }
	
	
}
	
public void checkinverted121Pattern(int x, int y){
	int[][] patty=new int[3][3];
	for(int i=-1;i<2;i++){
		for(int j=-1;j<2;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	if(patty[0][0]!=-10 && patty[1][0]!=-10 && patty[2][0]!=-10 && patty[0][2]==-10 && patty[1][2]==-10 && patty[2][2]==-10 && patty[0][1]==1 && patty[1][1]==2 &&patty[2][1]==1 ){
		if(isValid(x,y+1)){
			if(!set.contains(getList(x,y+1))){
				set.add(getList(x,y+1));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y+1),theiry(x,y+1)));
				
			}
		}
		
		if(isValid(x-1,y+1)){
			if(!set.contains(getList(x-1,y+1))){
				set.add(getList(x-1,y+1));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
				
			}
		}
		
		if(isValid(x+1,y+1)){
			if(!set.contains(getList(x+1,y+1))){
				set.add(getList(x+1,y+1));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y+1),theiry(x+1,y+1)));
				
			}
		}
	}
	
}
	
	

public void checkinverted1221Pattern(int x, int y){
	int[][] patty=new int[4][3];
	for(int i=-1;i<3;i++){
		for(int j=-1;j<2;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	if(patty[0][0]!=-10 && patty[0][1]==1 && patty[0][2]==-10 && patty[1][0]!=-10 && patty[1][1]==2 && patty[1][2]==-10 && patty[2][0]!=-10 && patty[2][1]==2 &&patty[2][2]==-10 && patty[3][0]!=-10 && patty[3][1]==1 && patty[3][2]!=-10 ){
		
		if(isValid(x-1,y+1)){
			if(!set.contains(getList(x-1,y+1))){
				set.add(getList(x-1,y+1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+1),theiry(x-1,y+1)));
				
			}
		}
		
		if(isValid(x,y+1)){
			if(!set.contains(getList(x,y+1))){
				set.add(getList(x,y+1));
				q.add(new Action(ACTION.FLAG,theirx(x,y+1),theiry(x,y+1)));
				
			}
		}
		
		if(isValid(x+1,y+1)){
			if(!set.contains(getList(x+1,y+1))){
				set.add(getList(x+1,y+1));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y+1),theiry(x+1,y+1)));
				
			}
		}
	}
	
if(patty[0][0]==-10 && patty[0][1]==1 && patty[0][2]!=-10 && patty[1][0]==-10 && patty[1][1]==2 && patty[1][2]!=-10 && patty[2][0]==-10 && patty[2][1]==2 &&patty[2][2]!=-10 && patty[3][0]!=-10 && patty[3][1]==1 && patty[3][2]!=-10 ){
		
		if(isValid(x-1,y-1)){
			if(!set.contains(getList(x-1,y-1))){
				set.add(getList(x-1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
				
			}
		}
		
		if(isValid(x,y-1)){
			if(!set.contains(getList(x,y-1))){
				set.add(getList(x,y-1));
				q.add(new Action(ACTION.FLAG,theirx(x,y-1),theiry(x,y-1)));
				
			}
		}
		
		if(isValid(x+1,y-1)){
			if(!set.contains(getList(x+1,y-1))){
				set.add(getList(x+1,y-1));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y-1),theiry(x+1,y-1)));
				
			}
		}
	}
	
	
	
}
	
	
	
public void checkinverted12Pattern(int x, int y){
	
	int[][] patty=new int[4][3];
	for(int i=-1;i<3;i++){
		for(int j=-1;j<2;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	if(patty[0][0]!=-10 && patty[0][1]!=-10 && patty[0][2]==-10 && patty[1][0]!=-10 && patty[1][1]==1 && patty[1][2]==-10 && patty[2][0]!=-10 && patty[2][1]==2 &&patty[2][2]==-10 && patty[3][0]!=-10 && patty[3][1]!=-10 && patty[3][2]==-10 ){
		if(isValid(x-1,y+1)){
			if(!set.contains(getList(x-1,y+1))){
				set.add(getList(x-1,y+1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+1),theiry(x-1,y+1)));
				
			}
		}
		
		if(isValid(x+2,y+1)){
			if(!set.contains(getList(x+2,y+1))){
				set.add(getList(x+2,y+1));
				q.add(new Action(ACTION.FLAG,theirx(x+2,y+1),theiry(x+2,y+1)));
				
			}
		}
	}
	
	if(patty[0][0]==-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[1][0]==-10 && patty[1][1]==1 && patty[1][2]!=-10 && patty[2][0]==-10 && patty[2][1]==2 &&patty[2][2]!=-10 && patty[3][0]==-10 && patty[3][1]!=-10 && patty[3][2]!=-10 ){
		if(isValid(x-1,y-1)){
			if(!set.contains(getList(x-1,y-1))){
				set.add(getList(x-1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
				
			}
		}
		
		if(isValid(x+2,y-1)){
			if(!set.contains(getList(x+2,y-1))){
				set.add(getList(x+2,y-1));
				q.add(new Action(ACTION.FLAG,theirx(x+2,y-1),theiry(x+2,y-1)));
				
			}
		}
	}
	
}
	
	
public void checkCubePattern(int x, int y){
	
	int[][] patty=new int[4][4];
	for(int i=-1;i<3;i++){
		for(int j=-1;j<3;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
			}
		}
	}

	if(patty[0][0]!=-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[0][3]!=-10 && patty[1][0]!=-10 && patty[1][1]==1 && patty[1][2]==2 && patty[1][3]==1 &&patty[2][0]!=-10 && patty[2][1]==1 && patty[2][2]==-10 && patty[2][3]==-10 && patty[3][0]!=-10 && patty[3][1]==1 && patty[3][2]==-10 && patty[3][3]==-10 ){
		if(isValid(x+1,y+1)){
			if(!set.contains(getList(x+1,y+1))){
				set.add(getList(x+1,y+1));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y+1),theiry(x+1,y+1)));
				
			}
		}
	}
}
	
	
	
public void check121Pattern(int x, int y){
	
	int[][] patty=new int[3][5];
	for(int i=-1;i<2;i++){
		for(int j=-1;j<4;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	if(patty[0][0]==-10 && patty[0][1]==-10 && patty[0][2]==-10 && patty[0][3]==-10 && patty[0][4]==-10 && patty[1][0]==-10 && patty[1][1]==1 && patty[1][2]==2 && patty[1][3]==1 && patty[1][4]==-10 &&patty[2][0]==-10 && patty[2][1]!=-10 && patty[2][2]!=-10 && patty[2][3]!=-10 && patty[2][4]==-10 ){
		
		if(isValid(x-1,y-1)){
			if(!set.contains(getList(x-1,y-1))){
				set.add(getList(x-1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
			}
		}
		if(isValid(x,y-1)){
			if(!set.contains(getList(x,y-1))){
				set.add(getList(x,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y-1),theiry(x,y-1)));
			}
		}
		if(isValid(x+1,y-1)){
			if(!set.contains(getList(x+1,y-1))){
				set.add(getList(x+1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
			}
		}
		if(isValid(x-1,y+1)){
			if(!set.contains(getList(x-1,y+1))){
				set.add(getList(x-1,y+1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+1),theiry(x-1,y+1)));
			}
		}
		if(isValid(x-1,y+3)){
			if(!set.contains(getList(x-1,y+3))){
				set.add(getList(x-1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+3),theiry(x-1,y+3)));
			}
		}
		if(isValid(x,y+3)){
			if(!set.contains(getList(x,y+3))){
				set.add(getList(x,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y+3),theiry(x,y+3)));
			}
		}
		if(isValid(x+1,y+3)){
			if(!set.contains(getList(x+1,y+3))){
				set.add(getList(x+1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+3),theiry(x+1,y+3)));
			}
		}
		if(isValid(x-1,y)){
			if(!set.contains(getList(x-1,y))){
				set.add(getList(x-1,y));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y),theiry(x-1,y)));
				
			}
		}
		if(isValid(x-1,y+2)){
			if(!set.contains(getList(x-1,y+2))){
				set.add(getList(x-1,y+2));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y+2),theiry(x-1,y+2)));
				
			}
		}
		
	}
	
if(patty[0][0]==-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[0][3]!=-10 && patty[0][4]==-10 && patty[1][0]==-10 && patty[1][1]==1 && patty[1][2]==2 && patty[1][3]==1 && patty[1][4]==-10 &&patty[2][0]==-10 && patty[2][1]==-10 && patty[2][2]==-10 && patty[2][3]==-10 && patty[2][4]==-10 ){			
		if(isValid(x-1,y-1)){
			if(!set.contains(getList(x-1,y-1))){
				set.add(getList(x-1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
			}
		}
		if(isValid(x,y-1)){
			if(!set.contains(getList(x,y-1))){
				set.add(getList(x,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y-1),theiry(x,y-1)));
			}
		}
		if(isValid(x+1,y-1)){
			if(!set.contains(getList(x+1,y-1))){
				set.add(getList(x+1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
			}
		}
		if(isValid(x+1,y+1)){
			if(!set.contains(getList(x+1,y+1))){
				set.add(getList(x+1,y+1));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+1),theiry(x+1,y+1)));
			}
		}
		if(isValid(x-1,y+3)){
			if(!set.contains(getList(x-1,y+3))){
				set.add(getList(x-1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+3),theiry(x-1,y+3)));
			}
		}
		if(isValid(x,y+3)){
			if(!set.contains(getList(x,y+3))){
				set.add(getList(x,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y+3),theiry(x,y+3)));
			}
		}
		if(isValid(x+1,y+3)){
			if(!set.contains(getList(x+1,y+3))){
				set.add(getList(x+1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+3),theiry(x+1,y+3)));
			}
		}
		if(isValid(x+1,y)){
			if(!set.contains(getList(x+1,y))){
				set.add(getList(x+1,y));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y),theiry(x+1,y)));
				
			}
		}
		if(isValid(x+1,y+2)){
			if(!set.contains(getList(x+1,y+2))){
				set.add(getList(x+1,y+2));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y+2),theiry(x+1,y+2)));
				
			}
		}
		
	}
	
}

public void check323Pattern(int x, int y){
	
	int[][] patty=new int[3][5];
	for(int i=-1;i<2;i++){
		for(int j=-1;j<4;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	
	if(patty[0][0]==-10 && patty[0][1]==-10 && patty[0][2]==-10 && patty[0][3]==-10 && patty[0][4]==-10 && patty[1][0]==-10 && patty[1][1]==3 && patty[1][2]==2 && patty[1][3]==3 && patty[1][4]==-10 &&patty[2][0]==-10 && patty[2][1]!=-10 && patty[2][2]!=-10 && patty[2][3]!=-10 && patty[2][4]==-10 ){
		
		if(isValid(x-1,y-1)){
			if(!set.contains(getList(x-1,y-1))){
				set.add(getList(x-1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
			}
		}
		if(isValid(x,y-1)){
			if(!set.contains(getList(x,y-1))){
				set.add(getList(x,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y-1),theiry(x,y-1)));
			}
		}
		if(isValid(x+1,y-1)){
			if(!set.contains(getList(x+1,y-1))){
				set.add(getList(x+1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
			}
		}
		if(isValid(x-1,y+1)){
			if(!set.contains(getList(x-1,y+1))){
				set.add(getList(x-1,y+1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+1),theiry(x-1,y+1)));
			}
		}
		if(isValid(x-1,y+3)){
			if(!set.contains(getList(x-1,y+3))){
				set.add(getList(x-1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+3),theiry(x-1,y+3)));
			}
		}
		if(isValid(x,y+3)){
			if(!set.contains(getList(x,y+3))){
				set.add(getList(x,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y+3),theiry(x,y+3)));
			}
		}
		if(isValid(x+1,y+3)){
			if(!set.contains(getList(x+1,y+3))){
				set.add(getList(x+1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+3),theiry(x+1,y+3)));
			}
		}
		if(isValid(x-1,y)){
			if(!set.contains(getList(x-1,y))){
				set.add(getList(x-1,y));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y),theiry(x-1,y)));
				
			}
		}
		if(isValid(x-1,y+2)){
			if(!set.contains(getList(x-1,y+2))){
				set.add(getList(x-1,y+2));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y+2),theiry(x-1,y+2)));
				
			}
		}
		
	}
	
if(patty[0][0]==-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[0][3]!=-10 && patty[0][4]==-10 && patty[1][0]==-10 && patty[1][1]==3 && patty[1][2]==2 && patty[1][3]==3 && patty[1][4]==-10 &&patty[2][0]==-10 && patty[2][1]==-10 && patty[2][2]==-10 && patty[2][3]==-10 && patty[2][4]==-10 ){			
		if(isValid(x-1,y-1)){
			if(!set.contains(getList(x-1,y-1))){
				set.add(getList(x-1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
			}
		}
		if(isValid(x,y-1)){
			if(!set.contains(getList(x,y-1))){
				set.add(getList(x,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y-1),theiry(x,y-1)));
			}
		}
		if(isValid(x+1,y-1)){
			if(!set.contains(getList(x+1,y-1))){
				set.add(getList(x+1,y-1));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
			}
		}
		if(isValid(x+1,y+1)){
			if(!set.contains(getList(x+1,y+1))){
				set.add(getList(x+1,y+1));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+1),theiry(x+1,y+1)));
			}
		}
		if(isValid(x-1,y+3)){
			if(!set.contains(getList(x-1,y+3))){
				set.add(getList(x-1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+3),theiry(x-1,y+3)));
			}
		}
		if(isValid(x,y+3)){
			if(!set.contains(getList(x,y+3))){
				set.add(getList(x,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x,y+3),theiry(x,y+3)));
			}
		}
		if(isValid(x+1,y+3)){
			if(!set.contains(getList(x+1,y+3))){
				set.add(getList(x+1,y+3));
				q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+3),theiry(x+1,y+3)));
			}
		}
		if(isValid(x+1,y)){
			if(!set.contains(getList(x+1,y))){
				set.add(getList(x+1,y));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y),theiry(x+1,y)));
				
			}
		}
		if(isValid(x+1,y+2)){
			if(!set.contains(getList(x+1,y+2))){
				set.add(getList(x+1,y+2));
				q.add(new Action(ACTION.FLAG,theirx(x+1,y+2),theiry(x+1,y+2)));
				
			}
		}
		
	}
	
}



public void check212Pattern(int x, int y){
	
	int[][] patty=new int[3][5];
	for(int i=-1;i<2;i++){
		for(int j=-1;j<4;j++){
			if(isValid(x+i,y+j)){
				patty[i+1][j+1]=Tiles[x+i][y+j].number;
				
			}
		}
	}
	if(isValid(x-1,y)){
		if(!set.contains(getList(x-1,y))){
			set.add(getList(x-1,y));
			q.add(new Action(ACTION.UNCOVER,theirx(x-1,y),theiry(x-1,y)));
		}
	}
	if(isValid(x-1,y+2)){
		if(!set.contains(getList(x-1,y+2))){
			set.add(getList(x-1,y+2));
			q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+2),theiry(x-1,y+2)));
		}
	}
	if(patty[0][0]==-10 && patty[0][1]==-10 && patty[0][2]==-10 && patty[0][3]==-10 && patty[0][4]==-10 && patty[1][0]!=-10 && patty[1][1]==2 && patty[1][2]==1 && patty[1][3]==2 && patty[1][4]!=-10 &&patty[2][0]!=-10 && patty[2][1]!=-10 && patty[2][2]!=-10 && patty[2][3]!=-10 && patty[2][4]!=-10 ){
		if(isValid(x-1,y-1)){
			if(!set.contains(getList(x-1,y-1))){
				set.add(getList(x-1,y-1));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
				
			}
		}
		if(isValid(x-1,y+1)){
			if(!set.contains(getList(x-1,y+1))){
				set.add(getList(x-1,y+1));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
				
			}
		}
		if(isValid(x-1,y+3)){
			if(!set.contains(getList(x-1,y+3))){
				set.add(getList(x-1,y+3));
				q.add(new Action(ACTION.FLAG,theirx(x-1,y+3),theiry(x-1,y+3)));
			}
		}
		
		
		
	}
	
}
	
	
	public void check12Pattern(int x, int y){
		
		int[][] patty=new int[3][4];
		for(int i=-1;i<2;i++){
			for(int j=-1;j<3;j++){
				if(isValid(x+i,y+j)){
					patty[i+1][j+1]=Tiles[x+i][y+j].number;
					
				}
			}
		}
		
		if(patty[0][0]==-10 && patty[0][1]==-10 && patty[0][2]==-10 && patty[0][3]==-10 && patty[1][0]!=-10 && patty[1][1]==1 && patty[1][2]==2 && patty[1][3]!=-10 && patty[2][0]!=-10 && patty[2][1]!=-10 && patty[2][2]!=-10 && patty[2][3]!=-10 ){
			if(isValid(x-1,y-1)){
				if(!set.contains(getList(x-1,y-1))){
					set.add(getList(x-1,y-1));
					q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
				}
			}
			
			if(isValid(x-1,y+2)){
				if(!set.contains(getList(x-1,y+2))){
					set.add(getList(x-1,y+2));
					q.add(new Action(ACTION.FLAG,theirx(x-1,y+2),theiry(x-1,y+2)));
					
				}
			}
			
			
		}
		
		if(patty[0][0]!=-10 && patty[0][1]!=-10 && patty[0][2]!=-10 && patty[0][3]!=-10 && patty[1][0]!=-10 && patty[1][1]==1 && patty[1][2]==2 && patty[1][3]!=-10 && patty[2][0]==-10 && patty[2][1]==-10 && patty[2][2]==-10 && patty[2][3]==-10 ){
			if(isValid(x+1,y-1)){
				if(!set.contains(getList(x+1,y-1))){
					set.add(getList(x+1,y-1));
					q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
				}
			}
			if(isValid(x+1,y+2)){
				if(!set.contains(getList(x+1,y+2))){
					set.add(getList(x+1,y+2));
					q.add(new Action(ACTION.FLAG,theirx(x+1,y+2),theiry(x+1,y+2)));
					
				}
			}
			
			
		}
		
	}
	
//	
//public void checkMiddlePatterns(int x, int y){
//	Queue<ArrayList<Integer>> notuncvr=new LinkedList<>();
//    for(int i=-1;i<2;i++){
//    	for(int j=-1;j<2;j++){
//    		if(isValid(x+i,y+j)){
//    			pat[i+1][j+1]=Tiles[x+i][y+j].number;
//            }
//        }
//    }
//    for(int i=-1;i<2;i++){
//    	for(int j=-1;j<2;j++){
//    		if(isValid(x+i,y+j)){
//    			pat2[i+1][j+1]=Tiles[x+i][y+j].uncover;
//                if((Tiles[x+i][y+j].uncover)!=true && set.contains(getList(x+i,y+j))){
//                	ArrayList<Integer> l=new ArrayList<Integer>();
//                    int x2=theirx(x+i,y+j);
//                    int y2=theiry(x+i,y+j);
//                    l.add(x2);
//                    l.add(y2);
//                    notuncvr.add(l);
//                }
//           }
//       }
//   }
//   if(pat2[0][2]==true && pat2[1][2]==true && pat2[2][2]==true && pat2[2][1]==true &&  pat2[2][0]==true && pat[1][1]==1 && pat[0][1]>=1 && pat[1][0]>=1){
//	   if(pat2[0][0]!=true){
//		   if(isValid(x-1,y-1)){
//			   if(!set.contains(getList(x-1,y-1))){
//				   q.add(new Action(ACTION.FLAG, theirx(x-1, y-1), theiry(x-1,y-1)));
//				   set.add(getList(x-1,y-1));
//        	   }
//		   }
//       }
//  }    
//else if(pat2[1][0]==true && pat2[0][0]==true && pat2[2][2]==true && pat2[2][1]==true &&  pat2[2][0]==true && pat[1][1]==1 && pat[0][1]>=1 && pat[1][2]>=1){
//	if(pat2[0][2]!=true){
//		if(isValid(x-1,y+1)){
//			if(!set.contains(getList(x-1,y+1))){
//				q.add(new Action(ACTION.FLAG, theirx(x-1, y+1), theiry(x-1,y+1)));
//                set.add(getList(x-1,y+1));
//            }
//        }
//    }
//}
//
//else if (pat2[0][0]==true && pat2[1][2]==true && pat2[0][2]==true && pat2[0][1]==true &&  pat2[2][2]==true && pat[1][1]==1 && pat[2][1]>=1 && pat[1][0]>=1){
//	if(pat2[2][0]!=true){
//		if(isValid(x+1,y-1)){
//			if(!set.contains(getList(x+1,y-1))){
//				q.add(new Action(ACTION.FLAG, theirx(x+1, y-1), theiry(x+1,y-1)));
//                set.add(getList(x+1,y-1));
//            }
//        }
//    }                 
//}
//else if (pat2[2][0]==true && pat2[0][1]==true && pat2[0][2]==true && pat2[0][0]==true &&  pat2[1][0]==true && pat[1][1]==1 && pat[2][1]>=1 && pat[1][2]>=1){
//	if(pat2[2][2]!=true){
//		if(isValid(x+1,y+1)){
//			if(!set.contains(getList(x+1,y+1))){
//				q.add(new Action(ACTION.FLAG, theirx(x+1, y+1), theiry(x+1,y+1)));
//                set.add(getList(x+1,y+1));
//            }
//        }
//     }
//}
//else if(pat[1][1]==2 && notuncvr.size()==2){
//	for(ArrayList<Integer> lis: notuncvr){
//		int m=(Integer) lis.get(0);
//        int n=(Integer) lis.get(1);
//        q.add(new Action(ACTION.FLAG,m,n));
//        set.add(lis);
//    }
//}
//else if(pat[1][1]==3 && notuncvr.size()==3){
//	for(ArrayList<Integer> lis: notuncvr){
//		int m=(Integer) lis.get(0);
//        int n=(Integer) lis.get(1);
//        q.add(new Action(ACTION.FLAG,m,n));
//        set.add(lis);
//    }
//}
//else if(pat[1][1]==1 && notuncvr.size()==1){
//	for(ArrayList<Integer> lis: notuncvr){
//	    int m=(Integer) lis.get(0);
//	    int n=(Integer) lis.get(1);
//	    q.add(new Action(ACTION.FLAG,m,n));
//	    set.add(lis);
//    }
//}
//}    
        
	
	
	
	
public void checkCornerPatterns(String corner, int x, int y){
		Queue<ArrayList<Integer>> notuncvr=new LinkedList<>();
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat[i+1][j+1]=Tiles[x+i][y+j].number;
            	}
            }
         }
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat2[i+1][j+1]=Tiles[x+i][y+j].uncover;
                     if((Tiles[x+i][y+j].uncover)!=true && set.contains(getList(x+i,y+j))){
                         ArrayList<Integer> l=new ArrayList<Integer>();
                         int x2=theirx(x+i,y+j);
                         int y2=theiry(x+i,y+j);
                         l.add(x2);
                         l.add(y2);
                         notuncvr.add(l);
                     }
            	}
            }
         }
		if(corner.equals("bl") && pat2[0][0] == true && pat2[0][1] == true && pat2[0][2] == true && pat2[1][0] == false && pat2[1][1] == false && pat2[1][2] == true && pat2[2][0] == false && pat2[2][1] == false && pat2[2][2] == true ){
			if((pat[1][2] == 1 || pat[2][2] == 1) && (pat[0][0] == 1 || pat[0][1] == 1) && this.minesLeft == 2){
				if(isValid(x,y-1)){
					if(!set.contains(getList(x,y-1))){
						q.add(new Action(ACTION.FLAG, theirx(x, y-1),theiry(x,y-1)));
						set.add(getList(x,y-1));
					}
				}
				if(isValid(x+1,y)){
					if(!set.contains(getList(x+1,y))){
						q.add(new Action(ACTION.FLAG, theirx(x+1, y), theiry(x+1,y)));
						set.add(getList(x+1,y));

					}
				}
			}
			else{
				if(isValid(x,y)){
					if(!set.contains(getList(x,y))){
						q.add(new Action(ACTION.FLAG, theirx(x, y), theiry(x,y)));
						set.add(getList(x,y));

					}
				}
			}
		}

		if(corner.equals("tr") && pat2[0][0] == true && pat2[0][1] == false && pat2[0][2] == false && pat2[1][0] == true && pat2[1][1] == false && pat2[1][2] == false && pat2[2][0] == true && pat2[2][1] == true && pat2[2][2] == true ){
                        if((pat[2][1] == 1 || pat[2][2] == 1) && (pat[0][0] == 1 || pat[1][0] == 1) && this.minesLeft == 2){
                        	if(isValid(x-1,y)){
            					if(!set.contains(getList(x-1,y))){
            						q.add(new Action(ACTION.FLAG, theirx(x-1, y), theiry(x-1,y)));
            						set.add(getList(x-1,y));
            					}
            				}   
                        	if(isValid(x,y+1)){
            					if(!set.contains(getList(x,y+1))){
            						q.add(new Action(ACTION.FLAG, theirx(x, y+1), theiry(x,y+1)));
            						set.add(getList(x,y+1));

            					}
            				}
                        }
                        else{
                        		if(isValid(x,y)){
                        			if(!set.contains(getList(x,y))){
                        				q.add(new Action(ACTION.FLAG, theirx(x, y), theiry(x,y)));
                						set.add(getList(x,y));

                        			}
                        		}
                           	}
                }
		if(corner.equals("br") && pat2[0][0] == true && pat2[0][1] == true && pat2[0][2] == true && pat2[1][0] == true && pat2[1][1] == false && pat2[1][2] == false && pat2[2][0] == true && pat2[2][1] == false && pat2[2][2] == false){
                        if((pat[1][0] == 1 || pat[2][0] == 1) && (pat[0][1] == 1 || pat[0][2] == 1) && this.minesLeft == 2){
                        	if(isValid(x+1,y)){
            					if(!set.contains(getList(x+1,y))){
            						q.add(new Action(ACTION.FLAG, theirx(x+1, y), theiry(x+1,y)));
            						set.add(getList(x+1,y));
            					}
            				}   
                        	if(isValid(x,y+1)){
            					if(!set.contains(getList(x,y+1))){
            						set.add(getList(x,y+1));
            						q.add(new Action(ACTION.FLAG, theirx(x, y+1), theiry(x,y+1)));
            					}
            				}
                        }
                        else{
                        	if(isValid(x,y)){
                        		if(!set.contains(getList(x,y))){
                        			q.add(new Action(ACTION.FLAG, theirx(x, y), theiry(x,y)));
            						set.add(getList(x,y));

                        		}
                        	}
                        }
                }
		if(corner.equals("tl") && pat2[0][0] == false && pat2[0][1] == false && pat2[0][2] == true && pat2[1][0] == false && pat2[1][1] == false && pat2[1][2] == true && pat2[2][0] == true && pat2[2][1] == true && pat2[2][2] == true){
                        if((pat[1][2] == 1 || pat[0][2] == 1) && (pat[2][0] == 1 || pat[2][1] == 1) && this.minesLeft == 2){
                        	if(isValid(x+1,y)){
            					if(!set.contains(getList(x+1,y))){
            						set.add(getList(x+1,y));
            						q.add(new Action(ACTION.FLAG, theirx(x+1, y), theiry(x+1,y)));
            					}
            				}   
                        	if(isValid(x,y-1)){
            					if(!set.contains(getList(x,y-1))){
            						q.add(new Action(ACTION.FLAG, theirx(x, y-1), theiry(x,y-1)));
            						set.add(getList(x,y-1));
            					}
            				} 
                        	
                        }
                        else{
                        	if(isValid(x,y)){
                        		if(!set.contains(getList(x,y))){
                        			q.add(new Action(ACTION.FLAG, theirx(x, y), theiry(x,y)));
            						set.add(getList(x,y));
                        		}
                        	}
                        }
                }
	}
	
	public void checkTopPatterns(int x, int y){
		Queue<ArrayList<Integer>> notuncvr=new LinkedList<>();
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat[i+1][j+1]=Tiles[x+i][y+j].number;
            	}
            }
         }
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat2[i+1][j+1]=Tiles[x+i][y+j].uncover;
                     if((Tiles[x+i][y+j].uncover)!=true && set.contains(getList(x+i,y+j))){
                         ArrayList<Integer> l=new ArrayList<Integer>();
                         int x2=theirx(x+i,y+j);
                         int y2=theiry(x+i,y+j);
                         l.add(x2);
                         l.add(y2);
                         notuncvr.add(l);
                     }
            	}
            }
         }
        
        if(pat[0][2]==-10 && pat[1][2]==-10 &&pat[2][2]==-10 && pat[1][1]==1 && pat[0][1]==1 && pat[1][0]!=-10 && pat[0][0]!=-10 && pat[2][0]!=-10 && pat[2][1]!=-10 ){
	    	if(isValid(x+1,y+1)){
	    		if(!set.contains(getList(x+1,y+1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+1),theiry(x+1,y+1)));
	    			set.add(getList(x+1,y+1));
	    		}
	    	}
	    }
        
        else if(pat[0][0]==-10 && pat[0][1]==1 &&pat[0][2]!=-10 && pat[1][0]==-10 && pat[1][1]==1 && pat[1][2]!=-10 && pat[2][0]==-10 && pat[2][1]!=-10 && pat[2][2]!=-10 ){
	    	if(isValid(x+1,y-1)){
	    		if(!set.contains(getList(x+1,y-1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
	    			set.add(getList(x+1,y-1));
	    		}
	    	}
	    }
        
        else if (pat2[0][0]==false && pat2[1][0]==false && pat2[2][0]==false && pat2[0][2]==true && pat2[1][2]==true && pat2[2][2]==true &&  pat[0][1]==1 && pat[1][1]==2 && pat[2][1]==1 ){
        	if(isValid(x,y-1)){
	        	if(!set.contains(getList(x,y-1))){
	        		 q.add(new Action(ACTION.UNCOVER,theirx(x,y-1),theiry(x,y-1)));
	        		 set.add(getList(x,y-1));
	           	}
        	}
        	if(isValid(x-1,y-1)){
	        	if(!set.contains(getList(x-1,y-1))){
	        		 q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
	        		 set.add(getList(x-1,y-1));
	           	}
        	}
        	if(isValid(x+1,y-1)){
	        	if(!set.contains(getList(x+1,y-1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x+1,y-1),theiry(x+1,y-1)));
	        		set.add(getList(x+1,y-1));
	        	}
        	}
        }
        // 2 cases for number < -99
        else if (pat[1][1]==2 && pat[1][2]==-10 && pat[0][1]==2 && pat[0][2]==-10 ){
        	if(isValid(x-1,y+1)){
	        	if(!set.contains(getList(x-1,y+1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
	        		set.add(getList(x-1,y+1));
	        	}
        	}
        	if(isValid(x,y+1)){
	        	if(!set.contains(getList(x,y+1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x,y+1),theiry(x,y+1)));
	        		set.add(getList(x,y+1));
	        	}
        	}
        }
        else if (pat[1][1]==2 && pat[1][0]==-10 && pat[0][1]==2 && pat[0][0]==-10 &&  pat2[2][0]==true && pat2[2][1]==true && pat2[2][2]==true && pat2[0][2]==true && pat2[1][2]==true && pat2[2][2]==true){
        	if(isValid(x-1,y-1)){
	        	if(!set.contains(getList(x-1,y-1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
	        		set.add(getList(x-1,y-1));
	        	}
        	}
        	if(isValid(x,y-1)){
	        	if(!set.contains(getList(x,y-1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x,y-1),theiry(x,y-1)));
	        		set.add(getList(x,y-1));
	        	}
        	}
        }
        
        else if (pat[0][1]==2 && pat[1][1]==2 && pat[2][1] != -10 && pat[0][2]==-10 && pat[1][2]==-10 && pat[2][2] == -10){
        	if(isValid(x-1,y+1)){
	        	if(!set.contains(getList(x-1,y+1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
	        		set.add(getList(x-1,y+1));
	        	}
        	}
        	if(isValid(x,y+1)){
	        	if(!set.contains(getList(x,y+1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x,y+1),theiry(x,y+1)));
	        		set.add(getList(x,y+1));
	        	}
        	}
       }
        else if (pat[1][0]==1 && pat[1][1]==2 && pat[1][2]==1 && pat2[0][0]==false && pat2[0][1]==false && pat2[0][2]==false && pat2[2][0]==true && pat2[2][1]==true && pat2[2][2]==true ){
        	if(isValid(x-1,y-1)){
	        	if(!set.contains(getList(x-1,y-1))){
	            	q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
	            	set.add(getList(x-1,y-1));
	            }
        	}
        	if(isValid(x-1,y+1)){
	            if(!set.contains(getList(x-1,y+1))){
	            	q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
	            	set.add(getList(x-1,y+1));
	            }
        	}
       }
        
        else if (pat[0][0]==-10 && pat[1][0]==-10 && pat[2][0]==-10 && pat[0][1]==1 && pat[1][1]==2 && pat[2][1]==2 && pat[0][2]!=-10 && pat[1][2]!=-10 && pat[2][2]!=10 ){
        	if(isValid(x+1,y-1)){
	        	if(!set.contains(getList(x+1,y-1))){
	        		q.add(new Action(ACTION.FLAG,theirx(x+1,y-1),theiry(x+1,y-1)));
	        		set.add(getList(x+1,y-1));
	        	}
        	}
        	//one more flag? x, y-1
        }	
        	 else if (pat[1][0]==1 && pat[1][1]==2 && pat[1][2]==2 && pat[0][0]==1 &&  pat[0][1]==-10 && pat[0][2]==-10 ){
        		 if(isValid(x-1,y+1)){
	        		 if(!set.contains(getList(x-1,y+1))){
	                	 q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
	                	 set.add(getList(x-1,y+1));
	                 }
        		 }
        		 if(isValid(x-1,y)){
	        		 if(!set.contains(getList(x-1,y))){
	        			 q.add(new Action(ACTION.FLAG,theirx(x-1,y),theiry(x-1,y)));
	        			 set.add(getList(x-1,y));
	        		 }
        		 }
        	 }
        //redundant
        	 else if(pat[1][1]==1 && notuncvr.size()==1){
                 for(ArrayList<Integer> lis: notuncvr){
                         int m=(Integer) lis.get(0);
                         int n=(Integer) lis.get(1);
                         q.add(new Action(ACTION.FLAG,m,n));
                 }
        	 }
        	 else if(pat[1][1]==2 && notuncvr.size()==2){
                 for(ArrayList<Integer> lis: notuncvr){
                         int m=(Integer) lis.get(0);
                         int n=(Integer) lis.get(1);
                         q.add(new Action(ACTION.FLAG,m,n));
                 }
        	 }
        	 else if(pat[1][1]==3 && notuncvr.size()==3){
                 for(ArrayList<Integer> lis: notuncvr){
                         int m=(Integer) lis.get(0);
                         int n=(Integer) lis.get(1);
                         q.add(new Action(ACTION.FLAG,m,n));
                 }
        	 }   	 

	}
	
	
	public void checkLeftPatterns(int x,int y){
		Queue<ArrayList<Integer>> notuncvr=new LinkedList<>();
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat[i+1][j+1]=Tiles[x+i][y+j].number;
            	}
            }
         }
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat2[i+1][j+1]=Tiles[x+i][y+j].uncover;
                     if((Tiles[x+i][y+j].uncover)!=true && !set.contains(getList(x+i,y+j))){
                         ArrayList<Integer> l=new ArrayList<Integer>();
                         int x2=theirx(x+i,y+j);
                         int y2=theiry(x+i,y+j);
                         l.add(x2);
                         l.add(y2);
                         notuncvr.add(l);
                     }
            	}
            }
         }
        //currently
        if(pat[0][0]==-10 && pat[0][1]==-10 &&pat[0][2]==-10 && pat[1][1]==1 && pat[1][0]==1 && pat[1][2]!=-10 && pat[2][0]!=-10 && pat[2][1]!=-10 && pat[2][2]!=-10 ){
	    	if(isValid(x-1,y+1)){
	    		if(!set.contains(getList(x-1,y+1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+1),theiry(x-1,y+1)));
	    			set.add(getList(x-1,y+1));
	    		}
	    	}
	    }
        
        if(pat[0][0]!=-10 && pat[0][1]!=-10 &&pat[0][2]!=-10 && pat[1][1]==1 && pat[1][0]==1 && pat[1][2]!=-10 && pat[2][0]==-10 && pat[2][1]==-10 && pat[2][2]==-10 ){
	    	if(isValid(x+1,y+1)){
	    		if(!set.contains(getList(x+1,y+1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+1),theiry(x+1,y+1)));
	    			set.add(getList(x+1,y+1));
	    		}
	    	}
	    }
        
        
        if (pat2[0][0]==false && pat2[1][0]==false && pat2[2][0]==false && pat[0][1]==1 &&  pat[1][1]==2 && pat[2][1]==1 && pat2[0][2]==true && pat2[1][2]==true && pat2[2][2]==true ){
        	if(isValid(x,y-1)){
        		if(!set.contains(getList(x,y-1))){
            		q.add(new Action(ACTION.UNCOVER,theirx(x,y-1),theiry(x,y-1)));
            		set.add(getList(x,y-1));
               }
        	}
        	if(isValid(x-1,y-1)){
        		if(!set.contains(getList(x-1,y-1))){
            		q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
            		set.add(getList(x-1,y-1));
               }
        	}
        	if(isValid(x+1,y-1)){
        		if(!set.contains(getList(x+1,y-1))){
            		q.add(new Action(ACTION.FLAG,theirx(x+1,y-1), theiry(x+1,y-1)));
            		set.add(getList(x+1,y-1));
            	}
        	}
         }
        else if (pat2[2][0]==false && pat2[2][1]==false && pat2[2][2]==false && pat2[0][0]==true &&  pat2[0][1]==true && pat2[0][2]==true && pat[1][0]==1 && pat[1][1]==1 && pat[1][2]==1 ){
        	if(isValid(x+1,y+1)){
        		if(!set.contains(getList(x+1,y+1))){
                	q.add(new Action(ACTION.UNCOVER,theirx(x+1,y+1), theiry(x+1,y+1)));
                	set.add(getList(x+1,y+1));
                }
        	}
        }
}
	
	
	public void checkRightPatterns(int x,int y){
		Queue<ArrayList<Integer>> notuncvr=new LinkedList<>();
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat[i+1][j+1]=Tiles[x+i][y+j].number;
            	}
            }
         }
        for(int i=-1;i<2;i++){
            for(int j=-1;j<2;j++){
            	if(isValid(x+i,y+j)){
            		 pat2[i+1][j+1]=Tiles[x+i][y+j].uncover;
                     if((Tiles[x+i][y+j].uncover)!=true && !set.contains(getList(x+i,y+j))){
                         ArrayList<Integer> l=new ArrayList<Integer>();
                         int x2=theirx(x+i,y+j);
                         int y2=theiry(x+i,y+j);
                         l.add(x2);
                         l.add(y2);
                         notuncvr.add(l);
                     }
            	}
            }
         }
        
        if(pat[0][0]==-10 && pat[0][1]==-10 &&pat[0][2]==-10 && pat[1][1]==1 && pat[1][2]==1 && pat[1][0]!=-10 && pat[2][0]!=-10 && pat[2][1]!=-10 && pat[2][2]!=-10 ){
	    	if(isValid(x-1,y-1)){
	    		if(!set.contains(getList(x-1,y-1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
	    			set.add(getList(x-1,y-1));
	    		}
	    	}
	    }
        
        if(pat[0][0]!=-10 && pat[0][1]!=-10 &&pat[0][2]!=-10 && pat[1][1]==1 && pat[1][2]==1 && pat[1][0]!=-10 && pat[2][0]==-10 && pat[2][1]==-10 && pat[2][2]==-10 ){
	    	if(isValid(x+1,y-1)){
	    		if(!set.contains(getList(x+1,y-1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
	    			set.add(getList(x+1,y-1));
	    		}
	    	}
	    }
        
	    if (pat2[2][0]==false && pat2[2][1]==false && pat2[2][2]==false && pat2[0][0]==true &&  pat2[0][1]==true && pat2[0][2]==true && pat[1][0]==1 && pat[1][1]==2 && pat[1][2]==1 ){
	    	if(isValid(x+1,y)){
	    		if(!set.contains(getList(x+1, y))){
			    	q.add(new Action(ACTION.UNCOVER,theirx(x+1, y), theiry(x+1, y)));
			    	set.add(getList(x+1,y));
		    	}
	     	}
	    	if(isValid(x+1,y-1)){
	    		if(!set.contains(getList(x+1, y-1))){
			    	q.add(new Action(ACTION.FLAG,theirx(x+1, y-1), theiry(x+1, y-1)));
			    	set.add(getList(x+1,y-1));
		    	}
	     	}
	    	if(isValid(x+1,y+1)){
	    		if(!set.contains(getList(x+1, y+1))){
		    		q.add(new Action(ACTION.FLAG,theirx(x+1,y+1),theiry(x+1,y+1)));
		    		set.add(getList(x+1,y+1));
		    	}
	    	}
        }  
        else if (pat2[2][0]==true && pat2[2][1]==true && pat2[2][2]==true && pat2[0][0]==false &&  pat2[0][1]==false && pat2[0][2]==false &&( pat[1][0]==1 && pat[1][1]==1 && pat[1][2]==1) || (pat[1][0]==2 && pat[1][1]==1 && pat[1][2]==1) ){
        	if(isValid(x-1,y-1)){
        		if(!set.contains(getList(x-1, y-1))){
            		q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
            		set.add(getList(x-1,y-1));
            	}
        	}
        }
        else if (pat2[2][0]==false && pat2[2][1]==false && pat2[2][2]==false && pat2[0][0]==true &&  pat2[0][1]==true && pat2[0][2]==true &&( pat[1][0]==1 && pat[1][1]==1 && pat[1][2]==1) || (pat[1][0]==2 && pat[1][1]==1 && pat[1][2]==1) ){
        	if(isValid(x+1,y-1)){
        		if(!set.contains(getList(x+1,y-1))){
            		q.add(new Action(ACTION.UNCOVER,theirx(x+1,y-1),theiry(x+1,y-1)));
            		set.add(getList(x+1,y-1));
            	}
        	}
        }		 
        else if (pat2[2][0]==true && pat2[2][1]==true && pat2[2][2]==true && pat2[0][0]==false &&  pat2[0][1]==false && pat2[0][2]==false &&( pat[1][0]==1 && pat[1][1]==2 && pat[1][2]==1) ){
        	if(isValid(x-1,y-1)){
        		if(!set.contains(getList(x-1,y-1))){
            		q.add(new Action(ACTION.FLAG,theirx(x-1,y-1),theiry(x-1,y-1)));
            		set.add(getList(x-1,y-1));
            	}
        	}
        	if(isValid(x-1,y+1)){
        		if(!set.contains(getList(x-1,y+1))){
    	        	q.add(new Action(ACTION.FLAG,theirx(x-1,y+1),theiry(x-1,y+1)));
    	        	set.add(getList(x-1,y+1));
            	}
        	}
        }			  
		else if (pat2[2][0]==false && pat2[2][1]==false && pat2[2][2]==false && pat2[0][0]==true &&  pat2[0][1]==true && pat2[0][2]==true &&( pat[1][0]==1 && pat[1][1]==2 && pat[1][2]==1) ){
			if(isValid(x+1,y-1)){
				if(!set.contains(getList(x+1,y-1))){
					q.add(new Action(ACTION.FLAG,theirx(x+1,y-1),theiry(x+1,y-1)));
					set.add(getList(x+1,y-1));
				}
			}
			if(isValid(x+1,y+1)){
				if(!set.contains(getList(x+1,y+1))){
		            q.add(new Action(ACTION.FLAG,theirx(x+1,y+1),theiry(x+1,y+1)));
		            set.add(getList(x+1,y+1));
				}
			}
        }
}

	
	
	public void checkBottomPatterns(int x, int y){
		//System.out.println("identifyPatterns start");
		Queue<ArrayList<Integer>> notuncvr=new LinkedList<>();
	    for(int i = -1; i < 2; i++){
	    	for(int j = -1; j < 2; j++){
	    		if(isValid(x+i,y+j))
	    			pat[i+1][j+1]=this.Tiles[x+i][y+j].number;
	         }
	    }
	    for(int i=-1;i<2;i++){
	       	for(int j=-1;j<2;j++){
	       		if(isValid(x+i,y+j)){
		            pat2[i+1][j+1]=this.Tiles[x+i][y+j].uncover;
		         	if((this.Tiles[x+i][y+j].uncover)!=true && !set.contains(getList(x+i,y+j))){
						//ours to theirs x y+1 and y rows-x
		         		int x2=theirx(x+i,y+j);
		         		int y2=theiry(x+i,y+j);
		         		ArrayList<Integer> l=new ArrayList<Integer>();		        
		         		l.add(x2);
		         		l.add(y2);
		         		notuncvr.add(list);
		          	}
	       		}
			}
		}          
	    
	    if(pat[0][0]!=-10 && pat[1][0]!=-10 &&pat[2][0]!=-10 && pat[1][1]==1 && pat[2][1]==1 && pat[0][1]!=-10 && pat[0][2]==-10 && pat[1][2]==-10 && pat[2][2]==-10 ){
	    	if(isValid(x-1,y+1)){
	    		if(!set.contains(getList(x-1,y+1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x-1,y+1),theiry(x-1,y+1)));
	    			set.add(getList(x-1,y+1));
	    		}
	    	}
	    }
	    
	    if(pat[0][0]==-10 && pat[1][0]==-10 &&pat[2][0]==-10 && pat[1][1]==1 && pat[2][1]==1 && pat[0][1]!=-10 && pat[0][2]!=-10 && pat[1][2]!=-10 && pat[2][2]!=-10 ){
	    	if(isValid(x-1,y+1)){
	    		if(!set.contains(getList(x-1,y-1))){
	    			//System.out.println("Adding from new pat");
	    			q.add(new Action(ACTION.UNCOVER,theirx(x-1,y-1),theiry(x-1,y-1)));
	    			set.add(getList(x-1,y-1));
	    		}
	    	}
	    }
	//Unsure. check again    
	    if (pat[1][1] == 1 && pat[1][0] == -10 && pat[2][1] == 1 && pat[2][0] == -10){
	    	if(isValid(x-1,y-1)){
	    		ArrayList<Integer> l= new ArrayList<Integer>();	
	    		//ours to theirs x y+1 and y rows-x
	    		int x2=theirx(x-1,y-1);
         		int y2=theiry(x-1,y-1);
         		l.add(x2); //x-1
         		l.add(y2); //y-1
		    	if (this.Tiles[x - 1][y - 1].uncover != true && !set.contains(l)){
		    			q.add(new Action(ACTION.UNCOVER,x2,y2));
		    			set.add(l);
		        }
	    	}
	    }
	   //redundant. also, first uncover then flag
	    else if (pat[1][0] ==1 && pat[1][1]==2 && pat[1][2]==1 && pat2[2][0] == false && pat2[2][1] == false  && pat2[2][2] == false ){
	    	ArrayList<Integer> l= new ArrayList<Integer>();		  
    		//ours to theirs x y+1 and y rows-x
	    	int x2=theirx(x+1,y+1);
     		int y2=theiry(x+1,y+1);
     		l.add(x2); //x+1
     		l.add(y2); //y+1
	    	if(isValid(x+1,y+1) && !set.contains(l)){
	    		q.add(new Action(ACTION.FLAG,x2,y2));
	    		set.add(l);
	    	}
	    	ArrayList<Integer> li= new ArrayList<Integer>();	
    		//ours to theirs x y+1 and y rows-x
	    	 x2=theirx(x+1,y-1);
     		 y2=theiry(x+1,y-1);
     		li.add(x2); //x+1
     		li.add(y2); //y-1
	    	if(isValid(x+1,y-1) && !set.contains(li) ){
	    		q.add(new Action(ACTION.FLAG,x2,y2));
	    		set.add(li);
	    	}
	    }
	    //here
	 	else if (pat[1][1] ==2 && pat[1][2]==-10 && pat[2][1]==2 && pat[2][2]==-10 ){
	 		ArrayList<Integer> l= new ArrayList<Integer>();		
    		//ours to theirs x y+1 and y rows-x
	 		int x2=theirx(x,y+1);
    		int y2=theiry(x,y+1);
     		l.add(x2); //x+1
     		l.add(y2); //y+1
	 		if(isValid(x+1,y+1) && !set.contains(l)){
		 		if(this.Tiles[x+1][y+1].uncover!=true){
		 				q.add(new Action(ACTION.FLAG,x2,y2));
		 				set.add(l);
				}
	 		}
	 		ArrayList<Integer> li= new ArrayList<Integer>();	
    		//ours to theirs x y+1 and y rows-x
	 		x2=theirx(x+1,y+1);
    		y2=theiry(x+1,y+1);
     		li.add(x2); //x-1
     		li.add(y2); //y-1
	 		if(isValid(x+1,y+1) && !set.contains(li)){
				if(Tiles[x+1][y+1].uncover!=true){
						q.add(new Action(ACTION.FLAG,x2,y2));
						set.add(li);
				}
	 		}
	    }
		else if (pat[1][1] ==2 && pat[1][0]==-10 && pat[2][1]==2 && pat[2][2]==-10 ){
			ArrayList<Integer> l= new ArrayList<Integer>();		        
    		//ours to theirs x y+1 and y rows-x
			int x2=theirx(x+1,y-1);
    		int y2=theiry(x+1,y-1);
			l.add(x2); //x+1
     		l.add(y2); //y-1
			if(isValid(x+1,y-1) && !set.contains(l)){
				if(Tiles[x+1][y-1].uncover!=true){
						q.add(new Action(ACTION.FLAG,x2,y2));
						set.add(l);
		        }
			}
			ArrayList<Integer> li= new ArrayList<Integer>();	
			x2=theirx(x,y-1);
    		y2=theiry(x,y-1);
     		li.add(x2);
     		li.add(y2);
			if(isValid(x,y-1) && !set.contains(li)){
		        if(Tiles[x][y-1].uncover!=true){
		        		q.add(new Action(ACTION.FLAG,x2,y2));
		        		set.add(li);
		        }
			}
	   }	

}
	
	public ArrayList<Integer> getList(int x1,int y1){
		ArrayList<Integer> li= new ArrayList<Integer>();	
		int x2=theirx(x1,y1);
		int y2=theiry(x1,y1);
 		li.add(x2);
 		li.add(y2);
 		return li;
	}
	
	
	
	public Action popq(Queue<Action> q){
		//if(q.size() > 0){
			Action o = q.remove();
			//int x1 = ourx(o.x,o.y);
			//int y1 = oury(o.x,o.y);
			//if(isValid(x1,y1)){
			return o;
			//}	
		//}
	}
	
	public boolean scanBoard()
	{   
		//System.out.println("scan called");
		boolean var=true;
		for(int i=0;i < this.rows;i++){
			for(int j=0;j < this.cols;j++){
				if(isValid(i,j)){
					var=checkNeighbors(i,j);
				}
			}
		}
		return var;
	}
	
	public int getKnownCount(int x, int y){
		int count=0;
		for(int i=x-1;i<=x+1;i++){
			for(int j=y-1;j<=y+1;j++){
				if( i!=x && j!=y && isValid(i,j) && this.Tiles[i][j].number>0){
					count++;
				}
			}
		}
		//System.out.println("\n returning x "+x+" y "+y+"count "+ count);
		return count;
	}
	
	public boolean reval(){
		voteq.clear();
		int max = -100;
		int min=Integer.MAX_VALUE;
		ArrayList<Integer> xmax = new ArrayList<>();
		ArrayList<Integer> ymax = new ArrayList<>();
		ArrayList<Integer> xmin = new ArrayList<>();
		ArrayList<Integer> ymin = new ArrayList<>();
		for(int i = 0; i < this.rows; i++){
			for(int j = 0; j < this.cols; j++){
				if(this.Tiles[i][j].number != -10)
					continue;
				int c=getKnownCount(i,j);
				if(this.Tiles[i][j].uncover!=true && c>0){
				
					if(this.Tiles[i][j].voteNumber>0 && this.Tiles[i][j].voteNumber*c<min ){
						min = this.Tiles[i][j].voteNumber*c;
						xmin=new ArrayList<>();
						ymin = new ArrayList<>();
						xmin.add(i);
						ymin.add(j);
					}
					else if(this.Tiles[i][j].voteNumber>0 && this.Tiles[i][j].voteNumber*c == min  ){
						xmin.add(i);
						ymin.add(j);
					}
				}
				else if(this.Tiles[i][j].voteNumber > max){
					max = this.Tiles[i][j].voteNumber;
					xmax=new ArrayList<>();
					ymax = new ArrayList<>();
					xmax.add(i);
					ymax.add(j);
				}
				else if(this.Tiles[i][j].voteNumber == max){
					xmax.add(i);
					ymax.add(j);
				}
				
			}
		}
		
		for(int i=0;i<xmin.size();i++){
			q.add(new Action(ACTION.UNCOVER, theirx(xmin.get(i), ymin.get(i)), theiry(xmin.get(i), ymin.get(i))));
			break;
		}
//		for(int i = 0; i < xmax.size(); i++){
//			voteq.add(new Action(ACTION.FLAG, theirx(xmax.get(i), ymax.get(i)), theiry(xmax.get(i), ymax.get(i))));
//			break;
//		}
		
		if(q.size() > 0)
			return true;
		else
			return false;
	}
	
	public boolean isValid(int x, int y){
		return x < this.rows && y < this.cols && x >= 0 && y >= 0;
	}
	
	
}

