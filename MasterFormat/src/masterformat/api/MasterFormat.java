package masterformat.api;

public interface MasterFormat {
    
    //get the material price for the product
    public Double getMaterialPrice();
    
    //get labor price for the product
    public Double getLaborPrice();
    
    //get equipment price for the product
    public Double getEquipmentPrice();
    
    //get the total price for the product
    public Double getTotalPrice();
    
    //get the total price include the operation price for the product
    public Double getTotalInclOPPrice();
    
    //get the standard unit for this product
    public String getUnit();
    
}
