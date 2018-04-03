package org.zhaowl.userInterface;

import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.coode.owlapi.manchesterowlsyntax.ManchesterOWLSyntaxOntologyFormat;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLException;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyCreationException;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.semanticweb.owlapi.util.SimpleShortFormProvider;
import org.zhaowl.learner.ELLearner;
import org.zhaowl.oracle.ELOracle;
import org.zhaowl.utils.SimpleClass;

import uk.ac.manchester.cs.owl.owlapi.mansyntaxrenderer.ManchesterOWLSyntaxOWLObjectRendererImpl;

public class ELInterface extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ELInterface frame = new ELInterface();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */

	// ************ START FRAME SPECIFIC VARIABLES ********************* //
	public JList list = new JList();
	public JTextArea hypoField = new JTextArea();
	public JLabel memberCount = new JLabel("Total membership queries: 0");
	public JLabel equivalenceCount = new JLabel("Total equivalence queries: 0");

	public JLabel entailed = new JLabel("Entailed: ----");
	public JLabel loadedOnto = new JLabel("No ontology loaded");
	public int membCount = 0;
	public int equivCount = 0;
	private JTextField conc2;
	private JTextField conc1;
	public Boolean win = false;
	public Boolean wePlayin = false;

	public JList list_1 = new JList();
	public JCheckBox oracleSaturate = new JCheckBox("Oracle saturation");
	public JCheckBox oracleMerge;
	public JCheckBox oracleBranch;
	public JCheckBox learnerSat;
	public JCheckBox learnerMerge;
	public JCheckBox learnerDecomp;
	public JCheckBox learnerUnsat;
	public JCheckBox learnerBranch;

	public JCheckBox ezBox;
	public JCheckBox autoBox = new JCheckBox("Auto learn [might take some moments]");
	public JCheckBox fileLoad;
	private final JScrollPane scrollPane = new JScrollPane();
	private final JScrollPane scrollPane_1 = new JScrollPane();
	private JTextField filePath;

	// ************ END FRAME SPECIFIC VARIABLES ********************* //

	// ************ START OWL SPECIFIC VARIABLES ********************* //.
	public OWLOntologyManager manager;
	public ManchesterOWLSyntaxOWLObjectRendererImpl rendering;

	public OWLReasoner reasonerForT;
	public Set<OWLAxiom> axiomsT;
	public ELEngine ELQueryEngineForT;
	public String ontologyFolder;
	public String ontologyName;
	public File hypoFile;
	public File newFile;

	public ArrayList<String> concepts = new ArrayList<String>();
	public ArrayList<String> roles = new ArrayList<String>();

	public Set<OWLClass> cIo = null;

	public OWLReasoner reasonerForH;
	public ShortFormProvider shortFormProvider;
	public Set<OWLAxiom> axiomsH;
	public String ontologyFolderH;
	public OWLOntology ontology;
	public OWLOntology ontologyH;
	public OWLAxiom lastCE = null;
	private JLabel averageCI;
	private JLabel smallestCI;

	public OWLAxiom smallestOne = null;
	public int smallestSize = 0;
	// ************ END OWL SPECIFIC VARIABLES ********************* //
	public long timeStart = 0;
	public long timeEnd = 0;
	
	
	public ELInterface() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 787, 523);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JButton btnNewButton = new JButton("Exit");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.runFinalization();
				System.exit(0);
			}
		});
		btnNewButton.setBounds(672, 450, 89, 23);
		contentPane.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("Load");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					loadOntology();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		btnNewButton_1.setBounds(10, 450, 131, 23);
		contentPane.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("Membership query");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (!conc1.getText().isEmpty() && !conc2.getText().isEmpty()) {
					Boolean memb = false;
					try {
						memb = membershipQuery(conc1.getText(), conc2.getText());
					} catch (Exception e1) {
						System.out.println("Error in membership query");
					}
					if (memb) {
						entailed.setText("Entailed: Yes");
						membCount++;
						memberCount.setText("Total member queries: " + membCount);
						try {
							hypoField.setText(showHypothesis());
						} catch (Exception e2) {
							e2.printStackTrace();
						}
					} else {
						entailed.setText("Entailed: No");
						membCount++;
						memberCount.setText("Total member queries: " + membCount);
					}
				} else {
					JOptionPane.showMessageDialog(null, "Fields can't be empty!", "Alert",
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		btnNewButton_2.setBounds(10, 45, 171, 23);
		contentPane.add(btnNewButton_2);

		JButton btnNewButton_3 = new JButton("Equivalence query");
		btnNewButton_3.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (ezBox.isSelected() && !win) {
					if (autoBox.isSelected()) {
						while (!win)
							ezEq();
					} else
						ezEq();
				} else {
					equivalenceCheck();
					equivalenceCount.setText("Total equivalence queries: " + equivCount);
				}
			}
		});
		btnNewButton_3.setBounds(10, 269, 171, 23);
		contentPane.add(btnNewButton_3);

		JLabel lblNewLabel = new JLabel("SubClassOf");
		lblNewLabel.setBounds(202, 14, 121, 14);
		contentPane.add(lblNewLabel);

		loadedOnto.setBounds(151, 454, 107, 14);
		contentPane.add(loadedOnto);

		entailed.setBounds(191, 49, 121, 14);
		contentPane.add(entailed);

		memberCount.setBounds(497, 223, 225, 14);
		contentPane.add(memberCount);

		equivalenceCount.setBounds(497, 248, 225, 14);
		contentPane.add(equivalenceCount);

		conc2 = new JTextField();
		conc2.setBounds(303, 11, 183, 20);
		contentPane.add(conc2);
		conc2.setColumns(10);

		conc1 = new JTextField();
		conc1.setBounds(10, 11, 171, 20);
		contentPane.add(conc1);
		conc1.setColumns(10);

		JButton btnNewButton_4 = new JButton("[DEBUG] Show CIs in T");
		btnNewButton_4.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("TBox size = " + axiomsT.size());
				int i = 0;
				for (OWLAxiom axe : ontology.getAxioms()) {

					if(axe.toString().contains("Thing"))
						continue;
					if (axe.toString().contains("SubClassOf") || axe.toString().contains("Equivalent")) {
						System.out.println("TBox CI element #" + (i + 1) + " = " + rendering.render(axe));
						// tCount++;
						i++;
					}
				}
				// get sizes of inclusions
				showCISizes(ontology.getAxioms());
				// showCISizes(ontologyH.getAxioms());

			}
		});
		btnNewButton_4.setBounds(548, 14, 213, 23);
		contentPane.add(btnNewButton_4);

		autoBox.setBounds(120, 363, 366, 23);
		contentPane.add(autoBox);
		oracleSaturate.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				if (ezBox.isSelected())
					ezBox.setSelected(false);
			}
		});

		oracleSaturate.setBounds(299, 269, 206, 23);
		contentPane.add(oracleSaturate);
		scrollPane.setBounds(10, 79, 476, 179);

		contentPane.add(scrollPane);

		scrollPane.setViewportView(hypoField);
		scrollPane_1.setBounds(10, 335, 105, 104);

		contentPane.add(scrollPane_1);
		scrollPane_1.setViewportView(list);
		list.setModel(new AbstractListModel() {
			String[] values = new String[] { "animals", "football", "generations", "university", "EX" };

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});
		list.setSelectedIndex(0);

		filePath = new JTextField();
		filePath.setEditable(false);
		filePath.setBounds(123, 419, 189, 20);
		contentPane.add(filePath);
		filePath.setColumns(10);

		JButton btnNewButton_5 = new JButton("Select OWL file");
		btnNewButton_5.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fc = new JFileChooser(new File("/src/main/resources/ontologies"));

				fc.setDialogTitle("Open class File");
				fc.setApproveButtonText("Open");
				fc.setAcceptAllFileFilterUsed(false);
				fc.addChoosableFileFilter(new FileNameExtensionFilter("Ontology File (*.owl)", "owl"));
				int returnVal = fc.showOpenDialog(fc);

				File file = null;
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					file = fc.getSelectedFile();
					// System.out.println(file.getPath());
					filePath.setText(file.getPath());

				}

			}
		});
		btnNewButton_5.setBounds(322, 421, 164, 23);
		contentPane.add(btnNewButton_5);

		JButton btnNewButton_7 = new JButton("[DEBUG] Show CIs in H");
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("H-TBox size = " + ontologyH.getAxiomCount());
				int i = 0;
				for (OWLAxiom axe : ontologyH.getAxioms()) {

					if (axe.toString().contains("SubClassOf") || axe.toString().contains("Equivalent")) {

						System.out.println("HBox CI element #" + (i + 1) + " = " + rendering.render(axe));

						i++;
					}
				}
				showCISizes(ontologyH.getAxioms());
			}
		});
		btnNewButton_7.setBounds(548, 45, 213, 23);
		contentPane.add(btnNewButton_7);

		JButton btnNewButton_8 = new JButton("Try Learner [1 step]");
		btnNewButton_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					timeStart = System.currentTimeMillis();
					learner();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					System.out.println("Error in learner: " + e);
				}
			}
		});
		btnNewButton_8.setBounds(535, 269, 226, 23);
		contentPane.add(btnNewButton_8);

		fileLoad = new JCheckBox("Load From File");
		fileLoad.setBounds(120, 389, 97, 23);
		contentPane.add(fileLoad);

		ezBox = new JCheckBox("Ez mode [returns 1 direct inclusion from target T]");
		ezBox.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (oracleSaturate.isSelected())
					oracleSaturate.setSelected(false);
			}
		});
		ezBox.setBounds(120, 337, 366, 23);
		contentPane.add(ezBox);

		oracleMerge = new JCheckBox("Oracle Merge");
		oracleMerge.setBounds(250, 295, 107, 23);
		contentPane.add(oracleMerge);

		learnerSat = new JCheckBox("Saturate");
		learnerSat.setBounds(548, 333, 108, 23);
		contentPane.add(learnerSat);

		learnerMerge = new JCheckBox("Sibling Merge");
		learnerMerge.setBounds(548, 363, 97, 23);
		contentPane.add(learnerMerge);

		learnerDecomp = new JCheckBox("Learner decomposition");
		learnerDecomp.setBounds(563, 389, 202, 23);
		contentPane.add(learnerDecomp);

		learnerUnsat = new JCheckBox("Unsaturate");
		learnerUnsat.setBounds(658, 333, 97, 23);
		contentPane.add(learnerUnsat);

		learnerBranch = new JCheckBox("Branch");
		learnerBranch.setBounds(658, 363, 103, 23);
		contentPane.add(learnerBranch);

		JLabel lblNewLabel_1 = new JLabel("Learner skills");
		lblNewLabel_1.setBounds(601, 312, 154, 14);
		contentPane.add(lblNewLabel_1);

		oracleBranch = new JCheckBox("Oracle Branch");
		oracleBranch.setBounds(359, 295, 127, 23);
		contentPane.add(oracleBranch);

		averageCI = new JLabel("Target average CI size: 0");
		averageCI.setBounds(497, 120, 264, 14);
		contentPane.add(averageCI);

		smallestCI = new JLabel("Target smallest CI size: 0");
		smallestCI.setBounds(497, 145, 264, 14);
		contentPane.add(smallestCI);

	}

	public void showCISizes(Set<OWLAxiom> axSet) {
		int avgSize = 0;
		int sumSize = 0;
		smallestSize = 0;
		smallestOne = null;
		int totalSize = 0;
		for (OWLAxiom axe : axSet) {

			String inclusion = rendering.render(axe);
			inclusion = inclusion.replaceAll(" and ", " ");
			inclusion = inclusion.replaceAll(" some ", " ");
			if (axe.toString().contains("SubClassOf"))
				inclusion = inclusion.replaceAll("SubClassOf", "");
			else
				inclusion = inclusion.replaceAll("EquivalentTo", "");
			inclusion = inclusion.replaceAll(" and ", "");
			// ==System.out.println(inclusion);
			String[] arrIncl = inclusion.split(" ");
			  totalSize = 0;
			for (int i = 0; i < arrIncl.length; i++)
				if (arrIncl[i].length() > 1)
					totalSize++;
			// for(int i = 0; i < arrIncl.length; i++)
			// System.out.println(arrIncl[i] + "=====" +arrIncl[i].length());

			// System.out.println(totalSize);
			if (smallestOne == null) {
				smallestOne = axe;
				smallestSize = totalSize;
			} else {
				if (smallestSize >= totalSize) {
					smallestOne = axe;
					smallestSize = totalSize;
				}
			}

			sumSize += totalSize;
			// System.out.println("Size of : " + rendering.render(axe) + "." + totalSize);
			// System.out.println("Size of : " + inclusion + "." + totalSize);
		}
		System.out.println("Smallest logical axiom: " + rendering.render(smallestOne));
		System.out.println("Size is: " + smallestSize);
		System.out.println("Avg: " + sumSize / axSet.size());

	}

	public void getOntologyName() {

		int con = 0;
		for (int i = 0; i < ontology.getOntologyID().toString().length(); i++)
			if (ontology.getOntologyID().toString().charAt(i) == '/')
				con = i;
		ontologyName = ontology.getOntologyID().toString().substring(con + 1,
				ontology.getOntologyID().toString().length());
		ontologyName = ontologyName.substring(0, ontologyName.length() - 3);
		if (!ontologyName.contains(".owl"))
			ontologyName = ontologyName + ".owl";
		ontologyFolder += ontologyName;
		ontologyFolderH += "hypo_" + ontologyName;
	}

	
	//public int globalDecompose = 5;
	
	
	
	
	
	public void learner() throws Throwable {
		ELLearner learner = new ELLearner(reasonerForH, shortFormProvider, ontology, ontologyH, ELQueryEngineForT, this);
		
		// we get a counter example from oracle
		// while () {
		if (autoBox.isSelected()) {
			showQueryCount();
			hypoField.setText(showHypothesis());
			if (equivalenceQuery()) {
				victory();
				timeEnd = System.currentTimeMillis();
				System.out.println("Total time (ms): " + (timeEnd - timeStart));
				return;
			} else if (ezBox.isSelected())
				ezEq();
			else
				doCE();
			System.out.println(rendering.render(lastCE));
	 
			OWLClassExpression left = null;
			OWLClassExpression right = null;
			// lastCE is last counter example provided by oracle, unsaturate and saturate
			if (lastCE.isOfType(AxiomType.SUBCLASS_OF)) {
				left = ((OWLSubClassOfAxiom) lastCE).getSubClass();
				right = ((OWLSubClassOfAxiom) lastCE).getSuperClass();
			} else {
				learner();
				return;
			}
			lastCE = ELQueryEngineForT.getSubClassAxiom(left, right);
			// check if complex side is left
			if (checkLeft(lastCE)) {

				// decompose tries to find underlying inclusions inside the left hand side
				// by recursively breaking the left expression and adding new inclusions to the
				// hypothesis
				if (learnerDecomp.isSelected())
				{
					//System.out.println("lhs decomp");
					learner.decompose(left, right); 
				}
				// branch edges on left side of the inclusion (if possible) to make it logically
				// stronger (more general)
				if (learnerBranch.isSelected()) {
					//System.out.println("lhs branch");
					left = learner.branchLeft(left, right);
				}

				// unsaturate removes useless concepts from nodes in the inclusion
				if (learnerUnsat.isSelected()) {
					//System.out.println("lhs unsaturate");
				
					left = learner.unsaturateLeft(lastCE);
				}
				lastCE = ELQueryEngineForT.getSubClassAxiom(left, right);
				try {
					addHypothesis(lastCE);
					hypoField.setText(showHypothesis());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else {
				// decompose tries to find underlying inclusions inside the right hand side
				// by recursively breaking the left expression and adding new inclusions to the
				// hypothesis
				if (learnerDecomp.isSelected())
				{
					//System.out.println("rhs decomp");
					learner.decompose(left, right);
				}
				// merge edges on right side of the inclusion (if possible) to make it logically
				// stronger (more general)
				if (learnerMerge.isSelected())
				{
					//System.out.println("rhs merge");
					right = learner.learnerSiblingMerge(left, right);
				}
				// rebuild inclusion for final step
				lastCE = ELQueryEngineForT.getSubClassAxiom(left, right);
				if (learnerSat.isSelected())
				{
					//System.out.println("rhs saturate");
					lastCE = learner.saturateWithTreeRight(lastCE);
				}
				left = ((OWLSubClassOfAxiom) lastCE).getSubClass();
				right = ((OWLSubClassOfAxiom) lastCE).getSuperClass();
				try {
					addHypothesis(lastCE);
					hypoField.setText(showHypothesis());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			learner();
		}
		else
		{
			if (equivalenceQuery()) {
				victory();
				return;
			} else if (ezBox.isSelected())
				ezEq();
			else
				doCE();
 
			OWLClassExpression left = null;
			OWLClassExpression right = null;
			// lastCE is last counter example provided by oracle, unsaturate and saturate
			if (lastCE.isOfType(AxiomType.SUBCLASS_OF)) {
				left = ((OWLSubClassOfAxiom) lastCE).getSubClass();
				right = ((OWLSubClassOfAxiom) lastCE).getSuperClass();
			} else {
				return;

			}
			lastCE = ELQueryEngineForT.getSubClassAxiom(left, right);
			// check if complex side is left
			if (checkLeft(lastCE)) {

				// decompose tries to find underlying inclusions inside the left hand side
				// by recursively breaking the left expression and adding new inclusions to the
				// hypothesis
				if (learnerDecomp.isSelected())
					learner.decompose(left, right);

				// branch edges on left side of the inclusion (if possible) to make it logically
				// stronger (more general)
				if (learnerMerge.isSelected())
					left = learner.branchLeft(left, right);

				// unsaturate removes useless concepts from nodes in the inclusion
				if (learnerUnsat.isSelected())
					left = learner.unsaturateLeft(lastCE);

				lastCE = ELQueryEngineForT.getSubClassAxiom(left, right);
				try {
					addHypothesis(lastCE);
					hypoField.setText(showHypothesis());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			} else {
				// decompose tries to find underlying inclusions inside the right hand side
				// by recursively breaking the left expression and adding new inclusions to the
				// hypothesis
				if (learnerDecomp.isSelected())
					learner.decompose(left, right);

				// merge edges on right side of the inclusion (if possible) to make it logically
				// stronger (more general)
				if (learnerMerge.isSelected())
					right = learner.learnerSiblingMerge(left, right);

				// rebuild inclusion for final step
				lastCE = ELQueryEngineForT.getSubClassAxiom(left, right);
				if (learnerSat.isSelected())
					lastCE = learner.saturateWithTreeRight(lastCE);

				left = ((OWLSubClassOfAxiom) lastCE).getSubClass();
				right = ((OWLSubClassOfAxiom) lastCE).getSuperClass();
				try {
					addHypothesis(lastCE);
					hypoField.setText(showHypothesis());
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
		}
		// }
		showQueryCount();

	}

	
	public ArrayList<String> getPS(ArrayList<String> strings) {
		Set<String> inputSet = new HashSet<String>(strings);

		List<Set<String>> subSets = new ArrayList<Set<String>>();
		for (String addToSets : inputSet) {
			List<Set<String>> newSets = new ArrayList<Set<String>>();
			for (Set<String> curSet : subSets) {
				Set<String> copyPlusNew = new HashSet<String>();
				copyPlusNew.addAll(curSet);
				copyPlusNew.add(addToSets);
				newSets.add(copyPlusNew);
			}
			Set<String> newValSet = new HashSet<String>();
			newValSet.add(addToSets);
			newSets.add(newValSet);
			subSets.addAll(newSets);
		}
		ArrayList<String> allSet = new ArrayList<String>();
		for (Set<String> setT : subSets) {

			String str = "";
			for (String setEntry : setT) {
				// System.out.print(setEntry + " ");
				str += setEntry + " and ";
			}
			allSet.add(str.substring(0, str.length() - 5));
		}
		System.out.println("Total combinations for concepts [no empty set]: " + subSets.size());
		return allSet;
	}

	public boolean checkLeft(OWLAxiom axiom) {

		String left = rendering.render(((OWLSubClassOfAxiom) axiom).getSubClass());
		String right = rendering.render(((OWLSubClassOfAxiom) axiom).getSuperClass());
		for (String rol : roles) {
			if (left.contains(rol)) {
				// System.out.println("complex is left");
				return true;
			} else if (right.contains(rol)) {
				// System.out.println("complex is right");
				return false;
			} else
				continue;
		}
		// System.out.println("default, saturating left");
		return true;
	}

	
	
	public String cleanStr(String str) {
		return str.replaceAll("\n", "").replaceAll(",", "").replaceAll("\\[\\]", "");
	}

	public ArrayList<String> getConcepts(String concept) {
		ArrayList<String> splitConcept = new ArrayList<String>();
		for (String str : concept.split(" "))
			if (concepts.contains(str))
				splitConcept.add(str);
		return splitConcept;
	}

	public String fixAxioms(OWLAxiom axiom) {
		if (axiom.toString().contains("SubClassOf")) {
			String auxStr = axiom.toString();
			auxStr = auxStr.replaceAll(">", "");
			int startPos = auxStr.indexOf("<");
			int hashPos = auxStr.indexOf("#");
			auxStr = auxStr.substring(0, startPos) + auxStr.substring(hashPos + 1);
			while (auxStr.contains("#")) {
				// System.out.println(auxStr);
				startPos = auxStr.indexOf("<");
				hashPos = auxStr.indexOf("#");
				auxStr = auxStr.substring(0, startPos) + auxStr.substring(hashPos + 1);
				// System.out.println(auxStr);
			}
			// System.out.println(auxStr);
			return auxStr;
		} else
			return null;
	}

	public String fixAxioms(OWLClassExpression axiom) {
		String auxStr = axiom.toString();
		auxStr = auxStr.replaceAll(">", "");
		int startPos = auxStr.indexOf("<");
		int hashPos = auxStr.indexOf("#");
		auxStr = auxStr.substring(0, startPos) + auxStr.substring(hashPos + 1);
		while (auxStr.contains("#")) {
			// System.out.println(auxStr);
			startPos = auxStr.indexOf("<");
			hashPos = auxStr.indexOf("#");
			auxStr = auxStr.substring(0, startPos) + auxStr.substring(hashPos + 1);
			// System.out.println(auxStr);
		}
		// System.out.println(auxStr);
		return auxStr;
	}

	public void equivalenceCheck() {

		int x = 0;
		if (!wePlayin)
			JOptionPane.showMessageDialog(null, "No Ontology loaded yet, please load an Ontology to start playing!",
					"Alert", JOptionPane.INFORMATION_MESSAGE);
		else {
			if (autoBox.isSelected()) {
				System.gc();
				boolean check = equivalenceQuery();
				do {
					equivCount++;
					x++;
					if (check) {
						// victory
						victory();
						System.out.println("It took: " + x);
					} else {
						// generate counter example
						doCE();
					}
				} while (!equivalenceQuery());
			} else {
				boolean check = equivalenceQuery();
				equivCount++;
				if (check) {
					// victory
					victory();
				} else {
					// generate counter example
					doCE();
				}
			}
		}

	}

	public void doCE() {
		String counterEx = "";
		System.out.println("Generating counter example... ");
		try {
			counterEx = getCounterExample();

		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			hypoField.setText(showHypothesis());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(counterEx);
	}

	public void victory() {
		win = true;
		System.out.println("You dun did it!!!");
		showQueryCount();
	}

	public void showQueryCount() {
		memberCount.setText("Total membership queries: " + membCount);

		equivalenceCount.setText("Total equivalence queries: " + equivCount);
	}

	public void loadOntology() throws InterruptedException {
		win = false;
		if (!fileLoad.isSelected()) {
			try {
				equivCount = 0;
				membCount = 0;
				hypoField.setText("");
				memberCount.setText("Total membership queries: 0");
				equivalenceCount.setText("Total equivalence queries: 0");
				manager = OWLManager.createOWLOntologyManager();
				if (list.getSelectedIndex() == 0)
					ontology = manager
							.loadOntologyFromOntologyDocument(new File("src/main/resources/ontologies/animals.owl"));
				else if (list.getSelectedIndex() == 1)
					ontology = manager
							.loadOntologyFromOntologyDocument(new File("src/main/resources/ontologies/football.owl"));
				else if (list.getSelectedIndex() == 2)
					ontology = manager.loadOntologyFromOntologyDocument(
							new File("src/main/resources/ontologies/generations.owl"));
				else if (list.getSelectedIndex() == 3)
					ontology = manager
							.loadOntologyFromOntologyDocument(new File("src/main/resources/ontologies/university.owl"));
				else if (list.getSelectedIndex() == 4)
					ontology = manager.loadOntologyFromOntologyDocument(
							new File("src/main/resources/ontologies/football_reverse.owl"));

				rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

				reasonerForT = createReasoner(ontology);
				shortFormProvider = new SimpleShortFormProvider();
				axiomsT = new HashSet<OWLAxiom>();
				for (OWLAxiom axe : ontology.getAxioms())
					if (!axe.toString().contains("Thing") && axe.isOfType(AxiomType.SUBCLASS_OF)
							|| axe.isOfType(AxiomType.EQUIVALENT_CLASSES))
						axiomsT.add(axe);

				lastCE = null;

				ELQueryEngineForT = new ELEngine(reasonerForT, shortFormProvider);
				// transfer Origin ontology to ManchesterOWLSyntaxOntologyFormat
				OWLOntologyFormat format = manager.getOntologyFormat(ontology);
				ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
				if (format.isPrefixOWLOntologyFormat()) {
					manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				}
				ontologyFolderH = "src/main/resources/tmp/";
				ontologyFolder = "src/main/resources/tmp/";
				ontologyName = "";
				getOntologyName();
				newFile = new File(ontologyFolder);
				hypoFile = new File(ontologyFolderH);
				// save owl file as a new file in different location
				if (newFile.exists()) {
					newFile.delete();
				}
				newFile.createNewFile();
				manager.saveOntology(ontology, manSyntaxFormat, IRI.create(newFile.toURI()));

				// Create OWL Ontology Manager for hypothesis and load hypothesis file
				if (hypoFile.exists()) {
					hypoFile.delete();
				}
				hypoFile.createNewFile();

				ontologyH = manager.loadOntologyFromOntologyDocument(hypoFile);
				shortFormProvider = new SimpleShortFormProvider();
				axiomsH = ontologyH.getAxioms();
				loadedOnto.setText("Ontology loaded.");
				wePlayin = true;

				System.out.println(ontology);
				System.out.println("Loaded successfully.");
				System.out.println();

				concepts = getSuggestionNames("concept");
				roles = getSuggestionNames("role");

				System.out.println("Total number of concepts is: " + concepts.size());

				SimpleClass simpleObject = new SimpleClass(rendering);
				int[] mins = simpleObject.showCISizes(axiomsT);
				smallestSize = mins[0];
				//System.out.println(mins[0]);
				//System.out.println(smallestSize);
				//showCISizes(axiomsT);
				smallestCI.setText("Target smallest CI size: " + smallestSize);
				averageCI.setText("Target average CI size: " + mins[1]);

				mins = null;

			} catch (OWLOntologyCreationException e) {
				System.out.println("Could not load ontology: " + e.getMessage());
			} catch (OWLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				equivCount = 0;
				membCount = 0;
				hypoField.setText("");
				memberCount.setText("Total membership queries: 0");
				equivalenceCount.setText("Total equivalence queries: 0");
				manager = OWLManager.createOWLOntologyManager();

				ontology = manager.loadOntologyFromOntologyDocument(new File(filePath.getText()));

				rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

				reasonerForT = createReasoner(ontology);
				shortFormProvider = new SimpleShortFormProvider();
				axiomsT = new HashSet<OWLAxiom>();
				for (OWLAxiom axe : ontology.getAxioms())
					if (!axe.toString().contains("Thing") && axe.isOfType(AxiomType.SUBCLASS_OF)
							|| axe.isOfType(AxiomType.EQUIVALENT_CLASSES))
						axiomsT.add(axe);

				lastCE = null;
				ELQueryEngineForT = new ELEngine(reasonerForT, shortFormProvider);
				// transfer Origin ontology to ManchesterOWLSyntaxOntologyFormat
				OWLOntologyFormat format = manager.getOntologyFormat(ontology);
				ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
				if (format.isPrefixOWLOntologyFormat()) {
					manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				}
				ontologyFolderH = "src/main/resources/tmp/";
				ontologyFolder = "src/main/resources/tmp/";
				ontologyName = "";
				getOntologyName();
				newFile = new File(ontologyFolder);
				hypoFile = new File(ontologyFolderH);
				// save owl file as a new file in different location
				if (newFile.exists()) {
					newFile.delete();
				}
				newFile.createNewFile();
				manager.saveOntology(ontology, manSyntaxFormat, IRI.create(newFile.toURI()));

				// Create OWL Ontology Manager for hypothesis and load hypothesis file
				if (hypoFile.exists()) {
					hypoFile.delete();
				}
				hypoFile.createNewFile();

				ontologyH = manager.loadOntologyFromOntologyDocument(hypoFile);
				shortFormProvider = new SimpleShortFormProvider();
				axiomsH = ontologyH.getAxioms();
				loadedOnto.setText("Ontology loaded.");
				wePlayin = true;

				System.out.println(ontology);
				System.out.println("Loaded successfully.");
				System.out.println();
				concepts = getSuggestionNames("concept");
				roles = getSuggestionNames("role");
				System.out.println("Total number of concepts is: " + concepts.size());
				SimpleClass simpleObject = new SimpleClass(rendering);

				int[] mins = simpleObject.showCISizes(axiomsT);
				smallestSize = mins[0];
				smallestCI.setText("Target smallest CI size: " + mins[0]);
				averageCI.setText("Target average CI size: " + mins[1]);
				mins = null;
			} catch (OWLOntologyCreationException e) {
				System.out.println("Could not load ontology: " + e.getMessage());
			} catch (OWLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public OWLReasoner createReasoner(final OWLOntology rootOntology) {
		return new Reasoner.ReasonerFactory().createReasoner(rootOntology);
	}

	public Boolean membershipQuery(String concept1String, String concept2String) throws Exception {
		Boolean queryAns = false;
		if (!wePlayin)
			JOptionPane.showMessageDialog(null, "No Ontology loaded yet, please load an Ontology to start playing!",
					"Alert", JOptionPane.INFORMATION_MESSAGE);
		else {
			OWLAxiom subclassAxiom = ELQueryEngineForT.parseToOWLSubClassOfAxiom(concept1String, concept2String);
			queryAns = ELQueryEngineForT.entailed(subclassAxiom);
			if (queryAns) {
				Boolean queryAddedHypo = ontologyH.containsAxiom(subclassAxiom);
				if (!queryAddedHypo) {
					addHypothesis(subclassAxiom);
				} else {
					String message = "The inclusion [" + rendering.render(subclassAxiom)
							+ "] has been queried before.\n" + "Therefore, it will not be added into the hypothesis.";
					membCount--;
					System.out.println(message);
					// JOptionPane.showMessageDialog(null, message, "Alert",
					// JOptionPane.INFORMATION_MESSAGE);
				}
			}
		}
		return queryAns;
	}

	public Boolean equivalenceQuery() {

		reasonerForH = createReasoner(ontologyH);
		ELEngine ELQueryEngineForH = new ELEngine(reasonerForH, shortFormProvider);
		Boolean queryAns = ELQueryEngineForH.entailed(axiomsT);

		return queryAns;
	}

	public String getCounterExample() throws Exception {
		reasonerForH = createReasoner(ontologyH);
		ELEngine ELQueryEngineForH = new ELEngine(reasonerForH, shortFormProvider);

		ELOracle oracle = new ELOracle(reasonerForH, shortFormProvider, ontology, ontologyH, ELQueryEngineForT, this);

		Iterator<OWLAxiom> iteratorT = axiomsT.iterator();
		while (iteratorT.hasNext()) {
			OWLAxiom selectedAxiom = iteratorT.next();
			selectedAxiom.getAxiomType();

			// first get CounterExample from an axiom with the type SUBCLASS_OF
			if (selectedAxiom.isOfType(AxiomType.SUBCLASS_OF)) {
				Boolean queryAns = ELQueryEngineForH.entailed(selectedAxiom);
				// if hypothesis does NOT entail the CI
				if (!queryAns) {

					OWLSubClassOfAxiom counterexample = (OWLSubClassOfAxiom) selectedAxiom;
					OWLClassExpression subclass = counterexample.getSubClass();
					OWLClassExpression superclass = counterexample.getSuperClass();

					// create new counter example from the subclass and superclass
					// of axiom NOT entailed by H

					OWLAxiom newCounterexampleAxiom = getCounterExamplefromSubClassAxiom(subclass, superclass);
					if (newCounterexampleAxiom != null) {
						// if we actually got something, we use it as new counter example

						// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
						// ADD SATURATION FOR newCounterexampleAxiom HERE

						if (checkLeft(newCounterexampleAxiom) && oracleMerge.isSelected()) {
							OWLClassExpression ex = null;
							// System.out.println(newCounterexampleAxiom);
							// if (checkLeft(newCounterexampleAxiom)) {
							ex = oracle.oracleSiblingMerge(((OWLSubClassOfAxiom) newCounterexampleAxiom).getSubClass(),
									((OWLSubClassOfAxiom) newCounterexampleAxiom).getSuperClass());
							newCounterexampleAxiom = ELQueryEngineForT.getSubClassAxiom(ex, superclass);
						}
						if (oracleSaturate.isSelected())
							newCounterexampleAxiom = oracle.saturateWithTreeLeft((OWLSubClassOfAxiom) newCounterexampleAxiom);

						/*
						 * } else { ex = siblingMerge(((OWLSubClassOfAxiom)
						 * newCounterexampleAxiom).getSuperClass()); newCounterexampleAxiom =
						 * saturateWithTreeRight( ELQueryEngineForT.getSubClassAxiom(subclass, ex)); }
						 */

						// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
						lastCE = newCounterexampleAxiom;
						return addHypothesis(newCounterexampleAxiom);
					}
				}
			}

			// get CounterExample from an axiom with the type EQUIVALENT_CLASSES
			if (selectedAxiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {

				OWLEquivalentClassesAxiom counterexample = (OWLEquivalentClassesAxiom) selectedAxiom;
				Set<OWLSubClassOfAxiom> eqsubclassaxioms = counterexample.asOWLSubClassOfAxioms();
				Iterator<OWLSubClassOfAxiom> iterator = eqsubclassaxioms.iterator();

				while (iterator.hasNext()) {
					OWLSubClassOfAxiom subClassAxiom = iterator.next();

					OWLClassExpression subclass = subClassAxiom.getSubClass();

					Set<OWLClass> superclasses = ELQueryEngineForT.getSuperClasses(subclass, true);
					if (!superclasses.isEmpty()) {
						Iterator<OWLClass> iteratorSuperClass = superclasses.iterator();
						while (iteratorSuperClass.hasNext()) {
							OWLClassExpression SuperclassInSet = iteratorSuperClass.next();
							OWLAxiom newCounterexampleAxiom = ELQueryEngineForT.getSubClassAxiom(subclass,
									SuperclassInSet);
							Boolean querySubClass = ELQueryEngineForH.entailed(newCounterexampleAxiom);
							Boolean querySubClassforT = ELQueryEngineForT.entailed(newCounterexampleAxiom);
							if (!querySubClass && querySubClassforT) {

								// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
								// ADD SATURATION FOR newCounterexampleAxiom HERE
								if (checkLeft(newCounterexampleAxiom) && oracleMerge.isSelected()) {
									OWLClassExpression ex = null;
									// System.out.println(newCounterexampleAxiom);
									// if (checkLeft(newCounterexampleAxiom)) {
									ex = oracle.oracleSiblingMerge(((OWLSubClassOfAxiom) newCounterexampleAxiom).getSubClass(),
											((OWLSubClassOfAxiom) newCounterexampleAxiom).getSuperClass());
									newCounterexampleAxiom = ELQueryEngineForT.getSubClassAxiom(ex,
											((OWLSubClassOfAxiom) newCounterexampleAxiom).getSuperClass());
								}
								if (oracleSaturate.isSelected())
									newCounterexampleAxiom = oracle.saturateWithTreeLeft(
											(OWLSubClassOfAxiom) newCounterexampleAxiom);

								// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
								lastCE = newCounterexampleAxiom;
								return addHypothesis(newCounterexampleAxiom);
							}
						}
					}

				}
			}
		}

		Iterator<OWLAxiom> iterator = axiomsT.iterator();

		while (iterator.hasNext()) {
			OWLAxiom Axiom = iterator.next();

			Axiom.getAxiomType();
			if ((Axiom.isOfType(AxiomType.SUBCLASS_OF)) || (Axiom.isOfType(AxiomType.EQUIVALENT_CLASSES))) {

				Axiom.getAxiomType();
				if (Axiom.isOfType(AxiomType.SUBCLASS_OF)) {
					OWLSubClassOfAxiom selectedAxiom = (OWLSubClassOfAxiom) Axiom;
					Boolean queryAns = ELQueryEngineForH.entailed(selectedAxiom);

					if (!queryAns) {
						lastCE = selectedAxiom;

						// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
						// ADD SATURATION FOR Axiom HERE
						if (checkLeft(selectedAxiom) && oracleMerge.isSelected()) {
							OWLClassExpression ex = null;
							// System.out.println(newCounterexampleAxiom);
							// if (checkLeft(newCounterexampleAxiom)) {
							ex = oracle.oracleSiblingMerge(((OWLSubClassOfAxiom) selectedAxiom).getSubClass(),
									((OWLSubClassOfAxiom) selectedAxiom).getSuperClass());
							Axiom = ELQueryEngineForT.getSubClassAxiom(ex,
									((OWLSubClassOfAxiom) selectedAxiom).getSuperClass());
						}
						if (oracleSaturate.isSelected())
							selectedAxiom = (OWLSubClassOfAxiom) oracle.saturateWithTreeLeft(selectedAxiom);

						// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
						lastCE = selectedAxiom;
						return addHypothesis((OWLSubClassOfAxiom) selectedAxiom);
					}
				}

				if (Axiom.isOfType(AxiomType.EQUIVALENT_CLASSES)) {
					OWLEquivalentClassesAxiom counterexample = (OWLEquivalentClassesAxiom) Axiom;

					Set<OWLSubClassOfAxiom> eqsubclassaxioms = counterexample.asOWLSubClassOfAxioms();
					Iterator<OWLSubClassOfAxiom> iteratorAsSub = eqsubclassaxioms.iterator();
					while (iteratorAsSub.hasNext()) {
						OWLSubClassOfAxiom subClassAxiom = iteratorAsSub.next();
						Boolean queryAns = ELQueryEngineForH.entailed(subClassAxiom);
						if (!queryAns) {
							lastCE = subClassAxiom;

							// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
							// ADD SATURATION FOR subClassAxiom HERE
							if (checkLeft(subClassAxiom) && oracleMerge.isSelected()) {
								OWLClassExpression ex = null;
								// System.out.println(newCounterexampleAxiom);
								// if (checkLeft(newCounterexampleAxiom)) {
								ex = oracle.oracleSiblingMerge(((OWLSubClassOfAxiom) subClassAxiom).getSubClass(),
										((OWLSubClassOfAxiom) subClassAxiom).getSuperClass());
								Axiom = ELQueryEngineForT.getSubClassAxiom(ex,
										((OWLSubClassOfAxiom) subClassAxiom).getSuperClass());
							}
							if (oracleSaturate.isSelected())
								Axiom = oracle.saturateWithTreeLeft(subClassAxiom);

							// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*?*-*-*-*-*-*-*-*
							lastCE = subClassAxiom;
							return addHypothesis(subClassAxiom);
						}
					}
				}
			}
		}
		System.out.println("no more CIs");
		return null;
	}

	private OWLAxiom getCounterExamplefromSubClassAxiom(OWLClassExpression subclass, OWLClassExpression superclass) {
		reasonerForH = createReasoner(ontologyH);
		ELEngine ELQueryEngineForH = new ELEngine(reasonerForH, shortFormProvider);

		Set<OWLClass> superclasses = ELQueryEngineForT.getSuperClasses(superclass, false);
		Set<OWLClass> subclasses = ELQueryEngineForT.getSubClasses(subclass, false);

		if (!subclasses.isEmpty()) {

			Iterator<OWLClass> iteratorSubClass = subclasses.iterator();
			while (iteratorSubClass.hasNext()) {
				OWLClassExpression SubclassInSet = iteratorSubClass.next();
				OWLAxiom newCounterexampleAxiom = ELQueryEngineForT.getSubClassAxiom(SubclassInSet, superclass);
				Boolean querySubClass = ELQueryEngineForH.entailed(newCounterexampleAxiom);
				Boolean querySubClassforT = ELQueryEngineForT.entailed(newCounterexampleAxiom);
				if (!querySubClass && querySubClassforT) {
					return newCounterexampleAxiom;
				}
			}
		}
		if (!superclasses.isEmpty()) {

			Iterator<OWLClass> iteratorSuperClass = superclasses.iterator();
			while (iteratorSuperClass.hasNext()) {
				OWLClassExpression SuperclassInSet = iteratorSuperClass.next();
				OWLAxiom newCounterexampleAxiom = ELQueryEngineForT.getSubClassAxiom(subclass, SuperclassInSet);
				Boolean querySubClass = ELQueryEngineForH.entailed(newCounterexampleAxiom);
				Boolean querySubClassforT = ELQueryEngineForT.entailed(newCounterexampleAxiom);
				if (!querySubClass && querySubClassforT) {
					return newCounterexampleAxiom;
				}
			}
		}
		return null;
	}

	public String addHypothesis(OWLAxiom addedAxiom) throws Exception {
		String StringAxiom = rendering.render(addedAxiom);

		AddAxiom newAxiomInH = new AddAxiom(ontologyH, addedAxiom);
		manager.applyChange(newAxiomInH);

		saveOWLFile(ontologyH, hypoFile);

		// minimize hypothesis
		ontologyH = MinHypothesis(ontologyH, addedAxiom);
		saveOWLFile(ontologyH, hypoFile);

		return StringAxiom;
	}

	public void ezEq() {
		equivCount++;
		if (equivalenceQuery()) {
			victory();
			return;
		}
		
		for (OWLAxiom ax : axiomsT) {
			if (ax.toString().contains("Thing"))
				continue;
			if (!axiomsH.contains(ax)) {
				try {
					addHypothesis(ax);
					lastCE = ax;
					hypoField.setText(showHypothesis());
					axiomsT.remove(ax);
					axiomsH.add(ax);
					break;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		showQueryCount();
	}

	private OWLOntology MinHypothesis(OWLOntology hypoOntology, OWLAxiom addedAxiom) {
		Set<OWLAxiom> tmpaxiomsH = hypoOntology.getAxioms();
		Iterator<OWLAxiom> ineratorMinH = tmpaxiomsH.iterator();
		Set<OWLAxiom> checkedAxiomsSet = new HashSet<OWLAxiom>();
		String removedstring = "";
		Boolean flag = false;
		if (tmpaxiomsH.size() > 1) {
			while (ineratorMinH.hasNext()) {
				OWLAxiom checkedAxiom = ineratorMinH.next();
				if (!checkedAxiomsSet.contains(checkedAxiom)) {
					checkedAxiomsSet.add(checkedAxiom);

					OWLOntology tmpOntologyH = hypoOntology;
					RemoveAxiom removedAxiom = new RemoveAxiom(tmpOntologyH, checkedAxiom);
					manager.applyChange(removedAxiom);

					OWLReasoner tmpreasoner = createReasoner(tmpOntologyH);
					ELEngine tmpELQueryEngine = new ELEngine(tmpreasoner, shortFormProvider);
					Boolean queryAns = tmpELQueryEngine.entailed(checkedAxiom);

					if (queryAns) {
						RemoveAxiom removedAxiomFromH = new RemoveAxiom(hypoOntology, checkedAxiom);
						manager.applyChange(removedAxiomFromH);
						removedstring = "\t[" + rendering.render(checkedAxiom) + "]\n";
						if (checkedAxiom.equals(addedAxiom)) {
							flag = true;
						}
					} else {
						AddAxiom addAxiomtoH = new AddAxiom(hypoOntology, checkedAxiom);
						manager.applyChange(addAxiomtoH);

					}
				}
			}
			if (!removedstring.equals("")) {
				String message;
				if (flag) {
					message = "The axiom [" + rendering.render(addedAxiom) + "] will not be added to the hypothesis\n"
							+ "since it can be replaced by some axiom(s) that already exist in the hypothesis.";
				} else {
					message = "The axiom [" + removedstring + "]" + "will be removed after adding: \n["
							+ rendering.render(addedAxiom) + "]";
				}
				System.out.println(message);
				// JOptionPane.showMessageDialog(null, message, "Alert",
				// JOptionPane.INFORMATION_MESSAGE);
			}
		}
		return hypoOntology;
	}

	private void saveOWLFile(OWLOntology ontology, File file) throws Exception {

		OWLOntologyFormat format = manager.getOntologyFormat(ontology);
		ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
		if (format.isPrefixOWLOntologyFormat()) {
			// need to remove prefixes
			manSyntaxFormat.clearPrefixes();
		}
		manager.saveOntology(ontology, manSyntaxFormat, IRI.create(file.toURI()));
	}

	public String showHypothesis() throws Exception {

		Set<OWLAxiom> axiomsInH = ontologyH.getAxioms();
		String hypoInManchester = "";
		for (OWLAxiom axiom : axiomsInH) {
			hypoInManchester = hypoInManchester + rendering.render(axiom) + "\n";
		}
		return hypoInManchester;
	}

	public ArrayList<String> getSuggestionNames(String s) throws IOException {

		ArrayList<String> names = new ArrayList<String>();
		FileInputStream in = new FileInputStream(newFile);
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));

		String line = reader.readLine();
		if (s.equals("concept")) {
			while (line != null) {
				if (line.startsWith("Class:")) {
					String conceptName = line.substring(7);
					if (!conceptName.equals("owl:Thing")) {
						names.add(conceptName);
					}
				}
				line = reader.readLine();
			}
		} else if (s.equals("role")) {
			while (line != null) {
				if (line.startsWith("ObjectProperty:")) {
					String roleName = line.substring(16);
					names.add(roleName);

				}

				line = reader.readLine();
			}
		}
		return names;
	}
}
