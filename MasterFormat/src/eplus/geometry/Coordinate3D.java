package eplus.geometry;

public class Coordinate3D {
	private double x;
	private double y;
	private double z;
	
	public Coordinate3D(){
		x = 0; y = 0; z = 0;
	}
	
	public Coordinate3D(double x, double y, double z){
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
	    this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}
	
	public Coordinate3D duplicate(){
		return new Coordinate3D(x, y, z);
	}
	
	public void update(Coordinate3D p){
		x = p.getX();
		y = p.getY();
		z = p.getZ();
	}
	
	public double vectorLen(){
		return Math.sqrt(
				Math.pow(this.x, 2)+Math.pow(this.y, 2)+Math.pow(this.z, 2)
			);
	}
	
	@Override
	public String toString(){
		return "{"+x+", "+y+", "+z+"}";
	}
}
