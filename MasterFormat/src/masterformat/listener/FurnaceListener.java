package masterformat.listener;

/**
 * Furnace object is depends on the click of the object to select, therefore, it
 * needs to listen to the model changes
 * 
 * @author Weili
 *
 */
public interface FurnaceListener {
    
    public void onQuanatitiesUpdates();
    
    public String getName();

}
