package masterformat.listener;
/**
 * Boiler object is depends on the click of the object to select, therefore, it
 * needs to listen to the model changes
 * 
 * @author Weili
 *
 */
public interface BoilerListener {
    
    public void onQuanatitiesUpdates();
    
    public String getName();


}
