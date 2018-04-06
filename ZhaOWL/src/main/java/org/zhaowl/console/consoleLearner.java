package org.zhaowl.console;

import org.zhaowl.userInterface.ELInterface;
import org.zhaowl.settings.*;
public class consoleLearner {

	public   String ontologyPath;
	public   ELInterface ZhaOWLInterface;
	public String[] values = new String[7];
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		/*
		 * ----- program is set to try EZ (naive teacher) by default, since console is
		 * set for ----- large ontologies, which should not use oracle skills at all 7
		 * parameters args[0] = ontology path args[1:6] = learner skills [1] = decompose
		 * left [2] = branch left [3] = unsaturate left [4] = decompose right [5] =
		 * merge right [6] = saturate right
		 * 
		 * ----- OUTPUT aside from some console metrics (number of equivalence queries
		 * and some other info) a new ontology file will be created in the folder of
		 * ontology input this new ontology will be the hypothesis learned by the
		 * program
		 * 
		 */
		
		consoleLearner maker = new consoleLearner();  
		//maker.setValues(args);
		maker.doIt(args);
		

	}
	  
	public void setValues(String[] vals)
	{
		values[0] = vals[0];
		values[1] = vals[1];
		values[2] = vals[2];
		values[3] = vals[3];
		values[4] = vals[4];
		values[5] = vals[5];
		values[6] = vals[6];
	}
	
	public   void doIt(String[] args) {  
		//System.out.println("Comin in as : " + args[0]);
		/*args[1] = values[1];
		args[2] = values[2];
		args[3] = values[3];
		args[4] = values[4];
		args[5] = values[5];
		args[6] = values[6];
		*/
		args[1] = "";
		args[2] = "";
		args[3] = "";
		args[4] = "";
		args[5] = "";
		args[6] = "t";
		try {
			System.out.println("other paths is : " +this.getClass().getResource("football.owl"));
			ZhaOWLInterface = new ELInterface();
			ZhaOWLInterface.consoleLoad = false;
			
			
			// naive learner + auto learn
			ZhaOWLInterface.autoBox.setSelected(true);
			ZhaOWLInterface.ezBox.setSelected(true);
			ZhaOWLInterface.consoleLoad = true;
			ZhaOWLInterface.consoleOntologyPath = args[0];
			// ontology path
			// for test try
			// consoleOntologies/ontologyName.owl
			
			
			
			
			// learner specific skills
			setLearnerSkills(args);
			ontologyPath = args[0];
			//ontologyPath ="src/main/resources/ontologies/university.owl";
			
			/*ZhaOWLInterface.filePath.setText(ontologyPath);
			
			System.out.println("Path is: " + ZhaOWLInterface.filePath.getText());
			ZhaOWLInterface.fileLoad.setSelected(true);
			System.out.println("the file we want to load is: " + ZhaOWLInterface.filePath.getText());
			*/
			ZhaOWLInterface.loadOntology(); 
			
			
			try {
				ZhaOWLInterface.timeStart = System.currentTimeMillis();
				ZhaOWLInterface.learner();
				System.out.println("Total membership queries: " + ZhaOWLInterface.membCount); 
				System.out.println("Total equivalence queries: " + ZhaOWLInterface.equivCount);
				System.out.println("Target TBox size: " + ZhaOWLInterface.axiomsT.size());
				System.out.println("Hypothesis TBox size: " + ZhaOWLInterface.ontologyH.getAxioms().size());
				
			} catch (Throwable e) {
				System.out.println("error in learner call ----- " + e);
			}
			
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			System.out.println("error in doIt --- " + e);
		}
	
	}

	public   void setLearnerSkills(String[] args) {
		if (args[1].equals("t"))
			ZhaOWLInterface.learnerDecompL.setSelected(true);
		else
			ZhaOWLInterface.learnerDecompL.setSelected(false);

		if (args[2].equals("t"))
			ZhaOWLInterface.learnerBranch.setSelected(true);
		else
			ZhaOWLInterface.learnerBranch.setSelected(false);

		if (args[3].equals("t"))
			ZhaOWLInterface.learnerUnsat.setSelected(true);
		else
			ZhaOWLInterface.learnerUnsat.setSelected(false);

		if (args[4].equals("t"))
			ZhaOWLInterface.learnerDecompR.setSelected(true);
		else
			ZhaOWLInterface.learnerDecompR.setSelected(false);

		if (args[5].equals("t"))
			ZhaOWLInterface.learnerMerge.setSelected(true);
		else
			ZhaOWLInterface.learnerMerge.setSelected(false);

		if (args[6].equals("t"))
			ZhaOWLInterface.learnerSat.setSelected(true);
		else
			ZhaOWLInterface.learnerSat.setSelected(false);

	}
}
