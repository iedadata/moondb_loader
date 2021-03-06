package org.moondb.parser;

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.moondb.dao.UtilityDao;
import org.moondb.model.Dataset;
import org.moondb.model.Datasets;
import org.moondb.model.Method;
import org.moondb.model.Methods;
import org.moondb.model.MoonDBType;
import org.moondb.model.RowCellPos;
import org.moondb.model.SampleResult;
import org.moondb.model.SampleResults;
import org.moondb.model.ChemistryResult;

public class SampleDataParser {
	
	private static int getBeginCellNum (HSSFSheet sheet) {
		Integer beginCellNum = null;
		switch (sheet.getSheetName()){
		case "ROCKS":
			beginCellNum = RowCellPos.ROCKS_VMUCD_CELL_B.getValue();
			break;
		case "MINERALS":
			beginCellNum = RowCellPos.MINERALS_VMUCD_CELL_B.getValue();
			break;
		case "INCLUSIONS":
			beginCellNum = RowCellPos.INCLUSIONS_VMUCD_CELL_B.getValue();
			break;
		}
		
		return beginCellNum;
	}
	
	public static int[] getVariableNums (HSSFWorkbook workbook, String sheetName) {
		HSSFSheet sheet = workbook.getSheet(sheetName);
	
		Integer beginCellNum = getBeginCellNum(sheet);
		
		/*
		 * The ending cell number of data
		 */
		int lastCellNum = XlsParser.getLastColNum(sheet,RowCellPos.VARIABLE_BEGIN_ROW_NUM.getValue());
		/*
		 * The size of array for variableNums,methodNums,unitNums and chemicalData
		 */
        int dataEntrySize = lastCellNum-beginCellNum;
        /*
         * Initializing array of variableNums
         */
		int[] variableNums = new int[dataEntrySize];
		
		Row row = sheet.getRow(RowCellPos.VARIABLE_BEGIN_ROW_NUM.getValue());
		/*
		 * Set variableNums
		 */
		for(int i=beginCellNum; i<lastCellNum; i++) {
			String varCode = XlsParser.getCellValueString(row.getCell(i));
			if(varCode.contains("[")) {
				varCode = varCode.substring(0, varCode.indexOf('[')); //remove type notation like [TE]
			}
			int varTypeNum = MoonDBType.VARIABLE_TYPE_MV.getValue();
			variableNums[i-beginCellNum] = UtilityDao.getVariableNum(varCode, varTypeNum);
		}
		
		return variableNums;
	}
	
	public static int[] getUnitNums (HSSFWorkbook workbook, String sheetName) {
		HSSFSheet sheet = workbook.getSheet(sheetName);
		
		Integer beginCellNum = getBeginCellNum(sheet);
		
		/*
		 * The ending cell number of data
		 */
		int lastCellNum = XlsParser.getLastColNum(sheet,RowCellPos.VARIABLE_BEGIN_ROW_NUM.getValue());
		/*
		 * The size of array for variableNums,methodNums,unitNums and chemicalData
		 */
        int dataEntrySize = lastCellNum-beginCellNum;
        /*
         * Initializing array of unitNums
         */
		int[] unitNums = new int[dataEntrySize];
		
		Row row = sheet.getRow(RowCellPos.UNIT_ROW_B.getValue());
		/*
		 * Set variableNums
		 */
		for(int i=beginCellNum; i<lastCellNum; i++) {
			String unitCode = XlsParser.getCellValueString(row.getCell(i));
			unitNums[i-beginCellNum] = UtilityDao.getUnitNum(unitCode);
		}
		
		return unitNums;
	}
	
	public static int[] getMethodNums (HSSFWorkbook workbook, String sheetName, Methods methods) {
		HSSFSheet sheet = workbook.getSheet(sheetName);
		
		Integer beginCellNum = getBeginCellNum(sheet);
		
		/*
		 * The ending cell number of data
		 */
		int lastCellNum = XlsParser.getLastColNum(sheet,RowCellPos.VARIABLE_BEGIN_ROW_NUM.getValue());
		/*
		 * The size of array for variableNums,methodNums,unitNums and chemicalData
		 */
        int dataEntrySize = lastCellNum-beginCellNum;
        /*
         * Initializing array of methodNums
         */
		int[] methodNums = new int[dataEntrySize];
		
		Row row = sheet.getRow(RowCellPos.METHOD_CODE_BEGIN_ROW_NUM.getValue());
		
		List<Method> methodList = methods.getMethods();
		for(int i=beginCellNum; i<lastCellNum; i++) {
			String methodCode = XlsParser.formatString(XlsParser.getCellValueString(row.getCell(i)));
			
			for(Method method: methodList) {

				if (methodCode.equals(method.getMethodCode())) {
					methodNums[i-beginCellNum] = UtilityDao.getMethodNum(method.getMethodTechnique(),method.getMethodLabNum(),method.getMethodComment());
				}
			}					
		}
		
		return methodNums;
	}
	
	public static SampleResults parseSampleData (HSSFWorkbook workbook, String sheetName, Datasets datasets, String moonDBNum, int[] variableNums, int[] unitNums, int[] methodNums, int baseNum) {
		SampleResults sampleResults = new SampleResults();
		

		HSSFSheet sheet = workbook.getSheet(sheetName);
		
		Integer beginCellNum = getBeginCellNum(sheet);
		Integer spotIDCellNum = null;
		Integer replicateCellNum = null;
		Integer avgeCellNum = null;
		Integer sfTypeNum = MoonDBType.SAMPLING_FEATURE_TYPE_SPECIMEN.getValue();
		
		switch (sheetName){
		case "ROCKS":
			replicateCellNum = RowCellPos.ROCKS_REPLICATE_COL_NUM.getValue();
			avgeCellNum = RowCellPos.ROCKS_AVGE_COL_NUM.getValue();
			break;
		case "MINERALS":
			spotIDCellNum = RowCellPos.MINERALS_SPOTID_COL_NUM.getValue();
			replicateCellNum = RowCellPos.MINERALS_REPLICATE_COL_NUM.getValue();
			avgeCellNum = RowCellPos.MINERALS_AVGE_COL_NUM.getValue();
			break;
		case "INCLUSIONS":
			spotIDCellNum = RowCellPos.INCLUSIONS_SPOTID_COL_NUM.getValue();
			replicateCellNum = RowCellPos.INCLUSIONS_REPLICATE_COL_NUM.getValue();
			avgeCellNum = RowCellPos.INCLUSIONS_AVGE_COL_NUM.getValue();
			break;
	}

		//The ending cell number of data
		int lastCellNum = XlsParser.getLastColNum(sheet,RowCellPos.VARIABLE_BEGIN_ROW_NUM.getValue());
	           
		ArrayList<SampleResult> sampleResultList = new ArrayList<SampleResult>();
		int beginRowNum = RowCellPos.RMI_DATA_BEGIN_ROW_NUM.getValue();
		int endSymbolColNum = RowCellPos.RMI_DATA_END_SYMBOL_COL_NUM.getValue();
		int lastRowNum = XlsParser.getLastRowNum(workbook, sheetName,beginRowNum,endSymbolColNum);
		
		for(int i = beginRowNum; i < lastRowNum; i++) {
			HSSFRow row = sheet.getRow(i);
	    	SampleResult sampleResult = new SampleResult();
	    	int analysisNum = i - beginRowNum + 1 + baseNum;

	    	//Set datasetNum
			String tabInRef = XlsParser.getCellValueString(row.getCell(1));    //TAB_IN_REF in sheet ROCKS, MINERALS and INCLUSIONS
			Integer datasetNum = null;
			List<Dataset> dsList = datasets.getDatasets();
			for(Dataset ds : dsList) {

				if(tabInRef.equals(ds.getTableCode())) {
					datasetNum = UtilityDao.getDatasetNum(ds.getDatasetCode());
					break;
				}
			}

			sampleResult.setDatasetNum(datasetNum);

			//Set samplingFeatureNum
			String sampleName = XlsParser.formatString(XlsParser.getCellValueString(row.getCell(2)));  // SAMPLE NAME in sheet ROCKS
			String sfCode = sampleName + "#" + analysisNum + "#" + moonDBNum;
			
			Integer samplingFeatureNum = UtilityDao.getSamplingFeatureNum(sfCode,sfTypeNum);

			sampleResult.setSamplingFeatureNum(samplingFeatureNum);

			//Set analysisComment
			String analysisComment = null;
			if(sheetName != "INCLUSIONS") {
				analysisComment = XlsParser.getCellValueString(row.getCell(3)); //ANALYSIS COMMENT in sheet ROCKS
			}
			sampleResult.setAnalysisComment(analysisComment);
			
			//Set numberOfReplicates
			String numberOfReplicates = XlsParser.getCellValueString(row.getCell(replicateCellNum)); 
			sampleResult.setReplicatesCount(numberOfReplicates);
			
			// set calcAvge 
			String calcAvge = XlsParser.getCellValueString(row.getCell(avgeCellNum)); 
			sampleResult.setCalcAvge(calcAvge);
			
			if (sheetName == "ROCKS") {
				String material = XlsParser.getCellValueString(row.getCell(6)); //MATERIAL in sheet ROCKS
				sampleResult.setMaterial(material);
			}
			
			if (sheetName == "MINERALS") {
				String mineral = XlsParser.getCellValueString(row.getCell(7));
				sampleResult.setMineral(mineral);
				
				String grain = XlsParser.getCellValueString(row.getCell(8));
				sampleResult.setGrain(grain);
				
				String rimCore = XlsParser.getCellValueString(row.getCell(9));
				sampleResult.setRimCore(rimCore);
				
				String minerialSize = XlsParser.getCellValueString(row.getCell(10));
				sampleResult.setMineralSize(minerialSize);					
				
				String spotId = XlsParser.formatString(XlsParser.getCellValueString(row.getCell(spotIDCellNum)));
				sampleResult.setSpotId(spotId);
			}
			
			if (sheetName == "INCLUSIONS") {
				String spotId = XlsParser.formatString(XlsParser.getCellValueString(row.getCell(spotIDCellNum)));
				sampleResult.setSpotId(spotId);
				
				String inclusionType = XlsParser.getCellValueString(row.getCell(6));
				sampleResult.setInclusionType(inclusionType);
				
				String inclusionMineral = XlsParser.getCellValueString(row.getCell(7));
				sampleResult.setInclusionMineral(inclusionMineral);
				
				String hostMineral = XlsParser.getCellValueString(row.getCell(8));
				sampleResult.setHostMineral(hostMineral);
				
				String hostRock = XlsParser.getCellValueString(row.getCell(9));
				sampleResult.setHostRock(hostRock);
				
				String inclusionSize = XlsParser.getCellValueString(row.getCell(10));
				sampleResult.setInclusionSize(inclusionSize);
				
				String rimCore = XlsParser.getCellValueString(row.getCell(11));
				sampleResult.setRimCore(rimCore);
				
				String heated = XlsParser.getCellValueString(row.getCell(12));
				sampleResult.setHeated(heated);
				
				String temperature = XlsParser.getCellValueString(row.getCell(13));
				sampleResult.setTemperature(temperature);
			}
			
			//Reading chemical data		
			ArrayList<ChemistryResult> chemistryResultList = new ArrayList<ChemistryResult>();
			for(int j=beginCellNum; j<lastCellNum; j++) {
				ChemistryResult chemistryResult = new ChemistryResult();
				String value = XlsParser.getCellValueString(row.getCell(j));
				//System.out.println("value is:" + value);
				if(value != null) {     //skip null value
					if(value.contains(",")) {
						value = value.substring(0, value.indexOf(',')).trim();							
					}
					if(value.contains(".")) {
						if (value.indexOf('.') == 0) {
							value = "0" + value;
						}
					}
					chemistryResult.setMethodNum(methodNums[j-beginCellNum]);
					chemistryResult.setUnitNum(unitNums[j-beginCellNum]);
					chemistryResult.setVariableNum(variableNums[j-beginCellNum]);
					chemistryResult.setVaule(Double.parseDouble(value));
					chemistryResultList.add(chemistryResult);
				}
			}
			sampleResult.setChemistryResults(chemistryResultList);
	    	sampleResultList.add(sampleResult);
		}

		sampleResults.setSampleResults(sampleResultList);
		return sampleResults;				
	}
}
