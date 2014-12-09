package org.sitenv.vocabularies.loader.icd9;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.sitenv.vocabularies.constants.VocabularyConstants;
import org.sitenv.vocabularies.data.Vocabulary;
import org.sitenv.vocabularies.data.VocabularyDataStore;
import org.sitenv.vocabularies.loader.Loader;
import org.sitenv.vocabularies.loader.LoaderManager;
import org.sitenv.vocabularies.model.impl.Icd10CmModel;
import org.sitenv.vocabularies.model.impl.Icd10PcsModel;
import org.sitenv.vocabularies.model.impl.Icd9CmDxModel;
import org.sitenv.vocabularies.model.impl.SnomedModel;

import com.orientechnologies.orient.core.sql.OCommandSQL;
import com.orientechnologies.orient.object.db.OObjectDatabaseTx;

public class Icd9CmDxLoader implements Loader {

	private static Logger logger = Logger.getLogger(Icd9CmDxLoader.class);
	

	static {
		LoaderManager.getInstance()
				.registerLoader(VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME, Icd9CmDxLoader.class);
		System.out.println("Loaded: " + VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME + "(" + VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM + ")");
	}

	public Vocabulary load(File file, OObjectDatabaseTx dbConnection) {
		
		Vocabulary icd9CmDx = new Vocabulary(Icd9CmDxModel.class, this.getCodeSystem());

		logger.debug("Loading ICD9CM_DX File: " + file.getName());

		BufferedReader br = null;
		
		try {
			logger.info("Truncating Icd9CmDxModel Datastore...");
			
			VocabularyDataStore.truncateModel(dbConnection, Icd9CmDxModel.class);
			
			logger.info("Icd9CmDxModel Datastore Truncated... records remaining: " + VocabularyDataStore.getRecordCount(dbConnection, Icd9CmDxModel.class));
			

			
			int count = 0;

			br = new BufferedReader(new FileReader(file));
			String available;
			while ((available = br.readLine()) != null) {
				if (count++ == 0) {
					continue; // skip header row
				} else {

					String[] line = available.split("\t");
					
					Icd9CmDxModel model = dbConnection.newInstance(Icd9CmDxModel.class);;
					
					model.setCode(line[0].toUpperCase());
					model.setDisplayName(line[1].toUpperCase());
					
					dbConnection.save(model);
				}


			}
			
			
			VocabularyDataStore.updateIndexProperties(dbConnection, Icd9CmDxModel.class);
			
			
			logger.info("Icd9CmDxModel Loading complete... records existing: " + VocabularyDataStore.getRecordCount(dbConnection, Icd9CmDxModel.class));
			
		} catch (FileNotFoundException e) {
			// TODO: log4j
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Runtime r = Runtime.getRuntime();
			r.gc();
		}

		return icd9CmDx;
	}
	
	public String getCodeName() {
		return VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_NAME;
	}

	public String getCodeSystem() {
		return VocabularyConstants.ICD9CM_DIAGNOSIS_CODE_SYSTEM;
	}

}