package masterformat.listener;

import java.util.HashMap;

/**
 * square meter cost model's listener, it listens to the model and updates the
 * charts based on the newly generated database
 * 
 * @author Weili
 *
 */
public interface SquareMeterCostModelListener {

    public void costInfoUpdated(HashMap<String, double[]> costInfo);

}
