package masterformat.squarefoot;

public enum BuildingType {
	APARTMENTSLOWRISE("Apartments Low Rise (1 to 3 story)"), //Apartments Low Rise (1 to 3 story)
	APARTMENTSMIDRISE("Apartments Mid Rise (4 to 7 story)"), //Apartments Mid Rise (4 to 7 story)
	APARTMENTSHIGHRISE("Apartments High Rise (8 to 24 story)"), //Apartments High Rise (8 to 24 story)
	BANKS("Banks"), // Banks
	FIRESTATIONS("Fire Stations"), //Fire Stations
	HOSPITALS("Hospitals"), //Hospitals
	SENIORHOUSING("Housing For the Elderly"), // Housing For the Elderly
	LIBRARIES("Library"),// Libraries
	OFFICELOWRISE("Offices Low Rise (1 to 4 story)"), //Offices Low Rise (1 to 4 story)
	OFFICEMIDRISE("Offices Mid Rise (5 to 10 story)"), //Offices Mid Rise (5 to 10 story)
	OFFICEHIGHRISE("Offices High Rise (11 to 20 story)"), //Offices High Rise (11 to 20 story)
	RESTAURANTS("Restaurants"), //Restaurants
	ELEMENTARYSCHOOLS("School Elementary"), // Schools Elementary
	MIDDLESCHOOLS("Schools Junior High &  Middle"), //Schools Junior High &  Middle
	HIGHSCHOOLS("Schools Senior High"), // Schools Senior High
	WAREHOUSESSTORAGE("Warehouses & Storage Buildings"); //Warehouses & Storage Buildings
	
	private String type;
	
	private BuildingType(String type){
	    this.type = type;
	}
	
	public String getTypes(){
	    return type;
	}
	
	@Override
	public String toString(){
	    return type;
	}
}
