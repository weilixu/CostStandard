package eplus.geometry;

import java.util.List;

public class EplusWindow extends Polygon{
    
	private String name;
	private String id;
	

	
	public EplusWindow(List<Coordinate3D> coords, String name, String id) {
		super(coords);
		this.name = name;
		this.id = id;
		
	}
	
	public String getId(){
	    return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	


}
