package org.moondb.model;

//Specific row number and cell number in sheets of data template
public enum RowCellPos {
	/*
	 * Beginning row number in sheet TABLE_TITLES
	 */
	TABLE_TITLES_ROW_B(1),             
	/*
	 * Beginning row number in sheet SAMPLES
	 */
	SAMPLES_ROW_B(2),	        
	/*
	 * Beginning row number in sheet METHODS
	 */
	METHODS_ROW_B(1),	 
	/*
	 * Beginning row number of variable in sheet ROCKS, MINERALS and INCLUSIONS
	 */
	VARIABLE_ROW_B(2),    
	/*
	 * Beginning row number of method in sheet ROCKS, MINERALS and INCLUSIONS
	 */
	METHOD_CODE_ROW_B(3),  
	/*
	 * Beginning row number of unit in sheet ROCKS, MINERALS and INCLUSIONS
	 */
	UNIT_ROW_B(4), 
	/*
	 * Beginning cell number of variable, method, unit and chemical data in Sheet ROCKS
	 */
	ROCKS_VMUCD_CELL_B(7),  
	/*
	 * Ending cell number of variable, method, unit and sample data in Sheet ROCKS
	 */
	ROCKS_VMUSD_CELL_E(6),   
	/*
	 * Beginning cell number of variable, method, unit and chemical data in Sheet MINERALS
	 */
	MINERALS_VMUCD_CELL_B(11), 
	/*
	 * Ending cell number of variable, method, unit and sample data in Sheet MINERALS
	 */
	MINERALS_VMUSD_CELL_E(10), 
	/*
	 * Beginning cell number of variable, method, unit and chemical data in Sheet INCLUSIONS
	 */
	INCLUSIONS_VMUCD_CELL_B(15),
	/*
	 * Ending cell number of variable, method, unit and sample data in Sheet INCLUSIONS
	 */
	INCLUSIONS_VMUSD_CELL_E(14),   
	/*
	 * Beginning row number of data in sheet ROCKS, MINERALS and INCLUSIONS
	 */
	DATA_ROW_B(8),         
	
	/*
	 * Column number of SPOT_ID in sheet MINERALS
	 */
	MINERALS_SPOTID_COL_NUM(4),
	
	/*
	 * Column number of SPOT_ID in sheet INCLUSIONS
	 */
	INCLUSIONS_SPOTID_COL_NUM(3);

	private final int value;
	
	private RowCellPos(int value) {
		this.value = value;
	}
	
	public int getValue() {
		return value;
	}

}
