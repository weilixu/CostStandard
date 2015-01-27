package thermalmoistureprotection;

public class RigidInsulation extends AbstractThermalMoistureProtection {
    


    public RigidInsulation() {
	hierarchy = "070000 Thermal & Moisture Protection|072100 Thermal Insulation|072113 Board Insulation|072113.10 Rigid Insulation";
	vertical = true;
	initializeData();
    }

    @Override
    protected void initializeData() {
	//the cost matrix indicates: material, labor, equipment, total, total incl O&P, R-value
	Double[][] costsMatrix = {{0.27,0.38,0.00,0.65,0.88,1.2},
		{0.40,0.38,0.00,0.78,1.02,1.82},
		{0.45,0.38,0.00,0.83,1.08,2.43},
		{0.56,0.47,0.00,1.03,1.34,3.63},
		{0.52,0.38,0.00,0.90,1.15,1.26},
		{0.78,0.38,0.00,1.16,1.44,1.90},
		{1.05,0.42,0.00,1.47,1.81,2.55},
		{1.10,0.47,0.00,1.57,1.93,3.19},
		{1.59,0.47,0.00,2.06,2.47,3.81},
		{0.90,0.38,0.00,1.28,1.57,1.26},
		{1.35,0.38,0.00,1.73,2.07,1.90},
		{1.69,0.42,0.00,2.11,2.51,2.55},
		{1.98,0.47,0.00,2.45,2.90,3.19},
		{2.18,0.47,0.00,2.65,3.12,3.81},
		{0.42,0.47,0.00,0.89,1.18,0.81},
		{0.75,0.51,0.00,1.26,1.62,1.63},
		{0.53,0.47,0.00,1.00,1.30,1.47},
		{1.04,0.51,0.00,1.55,1.93,2.93},
		{1.50,0.51,0.00,2.01,2.44,4.40},
		{0.25,0.47,0.00,0.72,1.00,1.13},
		{0.50,0.51,0.00,1.01,1.34,2.25},
		{0.75,0.51,0.00,1.25,1.62,3.37},   
	};
    }

    @Override
    public double getInsulationRValue() {
	// TODO Auto-generated method stub
	return 0;
    }



}
