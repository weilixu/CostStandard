package eplus.HVAC;

import java.util.ArrayList;
import java.util.HashMap;

import baseline.generator.EplusObject;

public interface HVACSystem {
    public HashMap<String, ArrayList<EplusObject>> getSystemData();
}
