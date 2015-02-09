package masterformat.listener;

import java.util.HashMap;

public interface SquareMeterCostModelListener {
    
    public void costInfoUpdated(HashMap<String, double[]> costInfo);

}
