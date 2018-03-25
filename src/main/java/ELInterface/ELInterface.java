package ELInterface;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
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
import javax.swing.plaf.synth.SynthSeparatorUI;

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

import tree.ELEdge;
import tree.ELNode;
import tree.ELTree;
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
	public JCheckBox saturationBox = new JCheckBox("Allow oracle saturation");
	public JCheckBox chckbxNewCheckBox = new JCheckBox("Auto learn");
	public JCheckBox fileLoad;
	public List<String> auxEx = new ArrayList<String>();
	public List<String> auxEx2 = new ArrayList<String>();
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
	public String ontologyFolderH;
	public String ontologyName;
	public File hypoFile;
	public File newFile;
	public OWLOntology ontologyH;
	public ArrayList<OWLSubClassOfAxiom> axiomArray = new ArrayList<OWLSubClassOfAxiom>();

	public ArrayList<String> concepts = new ArrayList<String>();
	public ArrayList<String> roles = new ArrayList<String>();

	public Set<OWLClass> cIo = null;

	public OWLReasoner reasonerForH;
	public ShortFormProvider shortFormProvider;
	public Set<OWLAxiom> axiomsH;
	public OWLOntology ontology;
	public OWLAxiom lastCE = null;
	public ArrayList<String> baseConcepts = new ArrayList<String>();
	public ArrayList<String> allCombinations = new ArrayList<String>();
	public ArrayList<OWLSubClassOfAxiom> coExSet = new ArrayList<OWLSubClassOfAxiom>();

	// ************ END OWL SPECIFIC VARIABLES ********************* //

	
	
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
				equivalenceCheck();
				equivCount++;
				equivalenceCount.setText("Total equivalence queries: " + equivCount);
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

		memberCount.setBounds(496, 220, 225, 14);
		contentPane.add(memberCount);

		equivalenceCount.setBounds(496, 244, 225, 14);
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
				String stuff = "";

				int tCount = 0;

				DefaultListModel counterModel = new DefaultListModel();
				System.out.println("TBox size = " + ontology.getAxiomCount());
				int i = 0;
				for (OWLAxiom axe : ontology.getAxioms()) {

					if (axe.toString().contains("SubClassOf")) {
						// OWLSubClassOfAxiom tempAxe = (OWLSubClassOfAxiom) axe;
						// axiomArray.add(tempAxe);
						// System.out.println("TBox CI element #" + (i + 1) + " = " +
						// axiomArray.get(i));
						// System.out.println("Axiom is: " + axe);
						// System.out.println("-----------------");

						// stuff = stuff + fixAxioms(axe) + "\n";
						// counterModel.addElement(rendering.render((OWLSubClassOfAxiom) axe));
						System.out.println("TBox CI element #" + (i + 1) + " = " + rendering.render(axe));
						// tCount++;
						i++;
					}
				}

			}
		});
		btnNewButton_4.setBounds(496, 152, 213, 23);
		contentPane.add(btnNewButton_4);

		chckbxNewCheckBox.setBounds(120, 333, 138, 23);
		contentPane.add(chckbxNewCheckBox);

		saturationBox.setBounds(120, 307, 138, 23);
		contentPane.add(saturationBox);
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
					System.out.println(file.getPath());
					filePath.setText(file.getPath());

				}

			}
		});
		btnNewButton_5.setBounds(322, 421, 164, 23);
		contentPane.add(btnNewButton_5);

		JButton btnNewButton_7 = new JButton("[DEBUG] Show CIs in H");
		btnNewButton_7.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String stuff = "";

				int tCount = 0;

				System.out.println("H-TBox size = " + ontologyH.getAxiomCount());
				int i = 0;
				for (OWLAxiom axe : ontologyH.getAxioms()) {

					if (axe.toString().contains("SubClassOf")) {
						OWLSubClassOfAxiom tempAxe = (OWLSubClassOfAxiom) axe;
						axiomArray.add(tempAxe);
						// System.out.println("TBox CI element #" + (i + 1) + " = " +
						// axiomArray.get(i));
						// System.out.println("Axiom is: " + axe);
						// System.out.println("-----------------");

						stuff = stuff + fixAxioms(axe) + "\n";
						System.out.println("TBox CI element #" + (i + 1) + " = " + rendering.render(axe));
						tCount++;
						i++;
					}
				}
			}
		});
		btnNewButton_7.setBounds(496, 186, 213, 23);
		contentPane.add(btnNewButton_7);

		JButton btnNewButton_8 = new JButton("Try Learner [1 step]");
		btnNewButton_8.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					learner();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		btnNewButton_8.setBounds(260, 269, 226, 23);
		contentPane.add(btnNewButton_8);

		fileLoad = new JCheckBox("Load From File");
		fileLoad.setBounds(120, 389, 97, 23);
		contentPane.add(fileLoad);
	}

	
	
	public void getOntologyName()
	{
		 
		int con = 0;
		for(int i = 0; i < ontology.getOntologyID().toString().length(); i ++)
			if(ontology.getOntologyID().toString().charAt(i) == '/')
				con = i;
		ontologyName = ontology.getOntologyID().toString().substring(con + 1, ontology.getOntologyID().toString().length());
		ontologyName = ontologyName.substring(0, ontologyName.length()-3);
		if(!ontologyName.contains(".owl"))
			ontologyName = ontologyName + ".owl";
		ontologyFolder += ontologyName;
		ontologyFolderH += "hypo_" + ontologyName;
	}
	
	public OWLClassExpression unsaturateLeft(OWLAxiom ax) throws Exception {
		OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
		OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
		ELTree tree = null;
		tree = new ELTree(left);
		reasonerForH = createReasoner(ontologyH);
		ELEngine engineForH = new ELEngine(reasonerForH, shortFormProvider);
		Set<ELNode> nodes = null;
		for (int i = 0; i < tree.getMaxLevel(); i++) {
			nodes = tree.getNodesOnLevel(i + 1);
			for (ELNode nod : nodes) {
				TreeSet<OWLClass> onesBreak = new TreeSet<OWLClass>();
				TreeSet<OWLClass> itOver = new TreeSet<OWLClass>();
				TreeSet<OWLClass> oldLabel = new TreeSet<OWLClass>();
				for (OWLClass cl : nod.label)
					itOver.add(cl);
				for (OWLClass cl : nod.label)
					oldLabel.add(cl);
				for (OWLClass cl : itOver) {

					nod.label = new TreeSet<OWLClass>();
					nod.label.add(cl);
					membCount++;
 

					System.out.println(tree.toDescriptionString());
					if (ELQueryEngineForT.entailed(ELQueryEngineForT.parseToOWLSubClassOfAxiom(
							tree.toDescriptionString(), (new ELTree(right)).toDescriptionString())) )/*&& 
							!engineForH.entailed(ELQueryEngineForT.parseToOWLSubClassOfAxiom(
							tree.toDescriptionString(), (new ELTree(right)).toDescriptionString())))*/ {
						try {
							addHypothesis(ELQueryEngineForT.getSubClassAxiom(
									ELQueryEngineForT.parseClassExpression(tree.toDescriptionString()), right));
							hypoField.setText(showHypothesis());
							break;
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						// System.out.println("this one: " + cl);
						onesBreak.add(cl);
						// nod.label = new TreeSet<OWLClass>();
					} else {
						// System.out.println("This one is bad: " + cl);
						nod.label = new TreeSet<OWLClass>();
						continue;

					}

				}

				nod.label.addAll(onesBreak);

			}
		}
		showQueryCount();
		return ELQueryEngineForT.parseClassExpression(tree.toDescriptionString());
	}

	public OWLClassExpression unsaturateRight(OWLAxiom ax) throws Exception {
		OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
		OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
		ELTree tree = null;
		tree = new ELTree(right);

		Set<ELNode> nodes = null;
		for (int i = 0; i < tree.getMaxLevel(); i++) {
			nodes = tree.getNodesOnLevel(i + 1);
			for (ELNode nod : nodes) {
				TreeSet<OWLClass> onesBreak = new TreeSet<OWLClass>();
				TreeSet<OWLClass> itOver = new TreeSet<OWLClass>();
				TreeSet<OWLClass> oldLabel = new TreeSet<OWLClass>();
				for (OWLClass cl : nod.label)
					itOver.add(cl);
				for (OWLClass cl : nod.label)
					oldLabel.add(cl);
				for (OWLClass cl : itOver) {

					nod.label = new TreeSet<OWLClass>();
					nod.label.add(cl);
					membCount++;
					if (ELQueryEngineForT.entailed(ELQueryEngineForT.parseToOWLSubClassOfAxiom(
							(new ELTree(left)).toDescriptionString(), tree.toDescriptionString()))) {
						try {
							addHypothesis(ELQueryEngineForT.parseToOWLSubClassOfAxiom(
									(new ELTree(left).toDescriptionString()), tree.toDescriptionString()));
							hypoField.setText(showHypothesis());
						} catch (Exception e2) {
							e2.printStackTrace();
						}
						// System.out.println("this one: " + cl);
						onesBreak.add(cl);
						// nod.label = new TreeSet<OWLClass>();
					} else {
						// System.out.println("This one is bad: " + cl);
						nod.label = new TreeSet<OWLClass>();
						continue;

					}

				}

				nod.label.addAll(onesBreak);

			}
		}
		showQueryCount();
		return ELQueryEngineForT.parseClassExpression(tree.toDescriptionString());
	}

	public void decompose(OWLClassExpression left, OWLClassExpression right)
	{
		try {
			ELTree treeR = null;
			boolean leftSide = false;
			leftSide = checkLeft(ELQueryEngineForT.getSubClassAxiom(left, right));
			if(leftSide)
				treeR = new ELTree(left);
			else
				treeR = new ELTree(right);
			if(treeR.nodes.size() == 1)
			{
					addHypothesis(ELQueryEngineForT.getSubClassAxiom(left, right));
					hypoField.setText(showHypothesis());
					return;
			}
			Set<ELNode> nodes = null;

			OWLClassExpression auxEx = ELQueryEngineForT.parseClassExpression(treeR.toDescriptionString());
			ELTree auxTree = new ELTree(ELQueryEngineForT.parseClassExpression(treeR.toDescriptionString()));
			reasonerForH = createReasoner(ontologyH);
			ELEngine engineForH = new ELEngine(reasonerForH, shortFormProvider);
			List<ELEdge> auxEdges = new LinkedList<ELEdge>();
			
			for(int i = 0; i < treeR.maxLevel; i ++)
			{
				nodes = treeR.getNodesOnLevel(i + 1);
				for(ELNode nod : nodes)
				{
					if(nod.isRoot())
						continue;
					if(nod.label.size()<1)
						continue;
					for (String cl : concepts) {
						if (!cl.toString().contains("Thing")) {
								// System.out.println("Class: " + rendering.render(cl));
								OWLAxiom axiom = ELQueryEngineForT.getSubClassAxiom(
										ELQueryEngineForT.parseClassExpression(nod.toDescriptionString()),
										ELQueryEngineForT.parseClassExpression(cl));
								//System.out.println(axiom);
								for(int j = 0; j < nod.edges.size(); j++)
									auxEdges.add(nod.edges.get(j));
								//auxEx = ELQueryEngineForT.parseClassExpression(treeR.toDescriptionString());
								if (ELQueryEngineForT.entailed(axiom) && !engineForH.entailed(axiom)) {
									//System.out.println(nod.toDescriptionString());
									//System.out.println(cl);
									//System.out.println("	Decompose this: " + axiom);
									decompose(ELQueryEngineForT.parseClassExpression(nod.toDescriptionString()), 
													 ELQueryEngineForT.parseClassExpression(cl));
									return;
								}
								else 
								{
									nod.edges = new LinkedList<ELEdge>();
									if(!(nod.label.size() < 1))
									if(ELQueryEngineForT.entailed(ELQueryEngineForT.getSubClassAxiom(ELQueryEngineForT.parseClassExpression(treeR.toDescriptionString()), 
																									 right)) &&
											!engineForH.entailed(ELQueryEngineForT.getSubClassAxiom(ELQueryEngineForT.parseClassExpression(treeR.toDescriptionString()), 
																									right)))
									{
										System.out.println(treeR.toDescriptionString());
										decompose(ELQueryEngineForT.parseClassExpression(treeR.toDescriptionString()), right);
										return;
									}
								}
								for(int j = 0; j < auxEdges.size(); j++)
									nod.edges.add(auxEdges.get(j)); 
						}
					}
				}
			}
		}catch(Exception e)
		{
			System.out.println("Error in decompose: " + e);
			return;
		}
		
		return;
	}
	
	public OWLClassExpression branchLeft(OWLClassExpression left, OWLClassExpression right)
	{
		try {

			ELTree treeL = new ELTree(left);
			ELTree treeR = new ELTree(right);
			Set<ELNode> nodes = null;
			List<ELEdge> auxEdges = null;

			//System.out.println("we branch this one: \n" + treeL.rootNode);
			for(int i = 0; i < treeL.maxLevel; i++)
			{
				nodes = treeL.getNodesOnLevel(i + 1);
				for(ELNode nod : nodes)
				{
					if(nod.edges.isEmpty())
						continue;
					auxEdges = new LinkedList<ELEdge>(nod.edges);
					for(int j = 0; j < auxEdges.size(); j++)
					{
						if(auxEdges.get(j).node.label.size() == 1)
							continue;
						// create list of classes in target node
						List<OWLClass> classAux = new ArrayList<OWLClass>();
						// fill list with classes
						for(OWLClass cl : auxEdges.get(j).node.label)
							classAux.add(cl);
						// for each class, create a node and add class to target node
						for(int k = 0; k < classAux.size(); k++)
						{
							nod.edges.add(new ELEdge(auxEdges.get(j).label, new ELNode(new ELTree(
																			ELQueryEngineForT.parseClassExpression(fixAxioms(classAux.get(k)))
																			))
													)
										);
							// add class to new node
							nod.edges.get(nod.edges.size()-1).node.label.add(classAux.get(k));
							// remove class from old node
							auxEdges.get(j).node.label.remove(classAux.get(k));
							
						}
					}
				}
			}
			//System.out.println("branched tree : \n" + treeL.rootNode);
			//System.out.println(treeL.toDescriptionString());
			return ELQueryEngineForT.parseClassExpression(treeL.toDescriptionString());
		}catch(Exception e)
		{
			System.out.println("Error in branchNode: " + e);
		}
		return null;
	}
	
	public void learner() throws Throwable {

		// we get a counter example from oracle
		equivalenceCheck();

		ELTree leftTree = null;
		ELTree rightTree = null;
		OWLClassExpression left= null;
		OWLClassExpression right = null;
		// lastCE is last counter example provided by oracle, unsaturate and saturate
		if(lastCE.isOfType(AxiomType.SUBCLASS_OF))
		{
			left = ((OWLSubClassOfAxiom) lastCE).getSubClass();
			right = ((OWLSubClassOfAxiom) lastCE).getSuperClass();
		}
		else
		{
			return;
			/*
			OWLEquivalentClassesAxiom counterexample = (OWLEquivalentClassesAxiom) lastCE;
			Set<OWLSubClassOfAxiom> eqsubclassaxioms =  counterexample.asOWLSubClassOfAxioms();
			Iterator<OWLSubClassOfAxiom> it = eqsubclassaxioms.iterator(); 
			System.out.println("EQUIVALENT CE: " + lastCE);
			OWLSubClassOfAxiom newAx = it.next();
			left = newAx.getSubClass();
			right = newAx.getSuperClass();*/
		}
		// check if complex side is left
		if(checkLeft(lastCE))
		{
			//
			decompose(left, right);
			left = unsaturateLeft(lastCE);
			left = branchLeft(left,right);
			try {
				addHypothesis(ELQueryEngineForT.getSubClassAxiom( left,right ));
				hypoField.setText(showHypothesis());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		else
		{
			decompose(left, right);
			right = unsaturateRight(lastCE);
			right = siblingMerge(right);
			
			try {
				addHypothesis(ELQueryEngineForT.getSubClassAxiom( left,right ));
				hypoField.setText(showHypothesis());
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		
		saturateWithTreeRight(lastCE);
	}

	public <OWLClass> Set<Set<OWLClass>> powerSet(Set<OWLClass> originalSet, int size) {
		Set<Set<OWLClass>> sets = new HashSet<Set<OWLClass>>();
		if (size == 0) {
			sets.add(new HashSet<OWLClass>());
			return sets;
		}
		List<OWLClass> list = new ArrayList<OWLClass>(originalSet);

		for (int i = 0; i < list.size(); i++) {
			OWLClass head = list.get(i);
			List<OWLClass> rest = list.subList(i + 1, list.size());
			Set<Set<OWLClass>> powerRest = powerSet(new HashSet<OWLClass>(rest), size - 1);
			for (Set<OWLClass> p : powerRest) {
				HashSet<OWLClass> appendedSet = new HashSet<OWLClass>();
				appendedSet.add(head);
				appendedSet.addAll(p);
				sets.add(appendedSet);
			}
		}
		return sets;
	}

	public <OWLClass> List<Set<OWLClass>> powerSet(Set<OWLClass> originalSet) {
		List<Set<OWLClass>> sets = new ArrayList<Set<OWLClass>>();
		if (originalSet.isEmpty()) {
			sets.add(new HashSet<OWLClass>());
			return sets;
		}
		List<OWLClass> list = new ArrayList<OWLClass>(originalSet);
		OWLClass head = list.get(0);
		Set<OWLClass> rest = new HashSet<OWLClass>(list.subList(1, list.size()));
		for (Set<OWLClass> set : powerSet(rest)) {
			Set<OWLClass> newSet = new HashSet<OWLClass>();
			newSet.add(head);
			newSet.addAll(set);
			sets.add(newSet);
			sets.add(set);
		}
		return sets;
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

	public OWLClassExpression siblingMerge(OWLClassExpression ex) throws Exception {
		ELTree tree = new ELTree(ex);
		Set<ELNode> nodes = null;
		//System.out.println(tree.toDescriptionString());
		for (int i = 0; i < tree.getMaxLevel(); i++) {
			nodes = tree.getNodesOnLevel(i + 1);
			if (!nodes.isEmpty())
				for (ELNode nod : nodes) {
					// nod.label.addAll(nod.label);
					if (!nod.edges.isEmpty() && nod.edges.size() > 1) {

						for (int j = 0; j < nod.edges.size(); j++) {
							for (int k = 0; k < nod.edges.size(); k++) {
								if (j == k) {
									continue;
								}
								if (nod.edges.get(j).strLabel.equals(nod.edges.get(k).strLabel)) {

									// System.out.println("they are equal: " +
									// nod.edges.get(j).node.toDescriptionString() + " AND " +
									// nod.edges.get(k).node.toDescriptionString());
									nod.edges.get(j).node.label.addAll(nod.edges.get(k).node.label);
									if (!nod.edges.get(k).node.edges.isEmpty())
										nod.edges.get(j).node.edges.addAll(nod.edges.get(k).node.edges);
									nod.edges.remove(nod.edges.get(k));
								}
							}
						}

					}
				}
		}
		//System.out.println(tree.getRootNode());
		return ELQueryEngineForT.parseClassExpression(tree.toDescriptionString());
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

	public OWLAxiom saturateWithTreeRight(OWLAxiom axiom) throws Exception {
		OWLClassExpression sub = ((OWLSubClassOfAxiom) axiom).getSubClass();
		OWLClassExpression sup = ((OWLSubClassOfAxiom) axiom).getSuperClass();

		OWLReasoner auxReasonerForH = createReasoner(ontologyH);
		ELEngine ELQueryEngineH = new ELEngine(auxReasonerForH, shortFormProvider);

		Set<OWLClass> cIo = ontology.getClassesInSignature();
		Set<ELNode> nodes = null;

		ELTree tree = new ELTree(sup);

		for (int i = 0; i < tree.getMaxLevel(); i++) {
			nodes = tree.getNodesOnLevel(i + 1);
			if (!nodes.isEmpty())
				for (ELNode nod : nodes) {
					for (OWLClass cl : cIo) {
						// System.out.println("Node before: " + nod);
						if (!nod.label.contains(cl) && !cl.toString().contains(":Thing")) {
							nod.label.add(cl);
						}

						
						OWLClassExpression newEx = ELQueryEngineForT.parseClassExpression(tree.toDescriptionString());
						if(newEx.equals(null))
							System.out.println("is null");
						OWLAxiom newAx = ELQueryEngineForT.getSubClassAxiom(sub, newEx);
 
						// check if hypothesis entails new saturated CI
						if (ELQueryEngineH.entailed(newAx)) {
							// CI is entailed by H, roll tree back to "safe" CI
							tree = new ELTree(sup);

						} else {

							sup = ELQueryEngineForT.parseClassExpression(tree.toDescriptionString());
						}

					}

				}
		}
		// System.out.println("Tree: " + tree.getRootNode());
		// System.out.println("Aux Tree: " + auxTree.getRootNode());
		// System.out.println("Final after saturation: " + sub);
		try {
			addHypothesis(ELQueryEngineForT.getSubClassAxiom(sub, 
					ELQueryEngineForT.parseClassExpression(tree.toDescriptionString())));
			hypoField.setText(showHypothesis()); 
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return ELQueryEngineForT.getSubClassAxiom(sub, sup);
	}

	public OWLAxiom saturateWithTreeLeft(OWLAxiom axiom) throws Exception {
		OWLClassExpression sub = ((OWLSubClassOfAxiom) axiom).getSubClass();
		OWLClassExpression sup = ((OWLSubClassOfAxiom) axiom).getSuperClass();

		OWLReasoner auxReasonerForH = createReasoner(ontologyH);
		ELEngine ELQueryEngineH = new ELEngine(auxReasonerForH, shortFormProvider);

		Set<OWLClass> cIo = ontology.getClassesInSignature();
		Set<ELNode> nodes = null;

		ELTree tree = new ELTree(sub);

		for (int i = 0; i < tree.getMaxLevel(); i++) {
			nodes = tree.getNodesOnLevel(i + 1);
			if (!nodes.isEmpty())
				for (ELNode nod : nodes) {
					for (OWLClass cl : cIo) {
						// System.out.println("Node before: " + nod);
						if (!nod.label.contains(cl) && !cl.toString().contains(":Thing")) {
							nod.label.add(cl);
						}
						// System.out.println("Node after: " + nod);
						OWLClassExpression newEx = ELQueryEngineForT.parseClassExpression(tree.toDescriptionString());
						// System.out.println("After saturation step: " + tree.toDescriptionString());
						OWLAxiom newAx = ELQueryEngineForT.getSubClassAxiom(newEx, sup);
						if (ELQueryEngineH.entailed(newAx)) {
							tree = new ELTree(sub);
						} else {
							sub = ELQueryEngineForT.parseClassExpression(tree.toDescriptionString());
						}

					}

				}
		}
		// System.out.println("Tree: " + tree.getRootNode());
		// System.out.println("Aux Tree: " + auxTree.getRootNode());
		// System.out.println("Final after saturation: " + sub);
		return ELQueryEngineForT.getSubClassAxiom(ELQueryEngineForT.parseClassExpression(tree.toDescriptionString()),
				sup);
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
			String counterEx = "";
			if (chckbxNewCheckBox.isSelected()) {
				System.gc();
				boolean check = equivalenceQuery();
				do {
					x++;
					if (check) {
						// victory
						victory();
					} else {
						// generate counter example
						doCE();
					}
				} while (!equivalenceQuery());
			} else {
				boolean check = equivalenceQuery();
				if (check) {
					// victory
					victory();
				} else {
					// generate counter example
					doCE();
				}
			}
		}
		System.out.println("It took: " + x);
	}

	public void doCE() {
		String counterEx = "";
		System.out.println("NOT YET!");
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
		int i = 1;
		showQueryCount();
		DefaultListModel counterModel = new DefaultListModel();
		for (OWLSubClassOfAxiom axe : coExSet)
			counterModel.addElement(rendering.render(axe));
		list_1.setModel(counterModel);
		list_1.setSelectedIndex(0);
		// showAxioms();
	}

	public void showQueryCount() {
		memberCount.setText("Total membership queries: " + membCount);

		equivalenceCount.setText("Total equivalence queries: " + equivCount);
	}

	public void loadOntology() throws InterruptedException {
		if (!fileLoad.isSelected()) {
			try {
				equivCount = 0;
				membCount = 0;
				hypoField.setText("");
				memberCount.setText("Total member queries: 0");
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
				axiomsT = ontology.getAxioms();
				coExSet = new ArrayList<OWLSubClassOfAxiom>();

				ELQueryEngineForT = new ELEngine(reasonerForT, shortFormProvider);
				// transfer Origin ontology to ManchesterOWLSyntaxOntologyFormat
				OWLOntologyFormat format = manager.getOntologyFormat(ontology);
				ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
				if (format.isPrefixOWLOntologyFormat()) {
					manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				}
				ontologyFolderH = "src/main/resources/tmp/";
				ontologyFolder = "src/main/resources/tmp/";
				ontologyName ="";
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

				System.out.println("Loaded successfully.");
				System.out.println();
				concepts = getSuggestionNames("concept");
				roles = getSuggestionNames("role");
				System.out.println("Total number of concepts is: " + concepts.size());
				baseConcepts = new ArrayList<String>();
				for (String str : concepts)
					baseConcepts.add(str.substring(str.indexOf(" ") + 1));

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
				memberCount.setText("Total member queries: 0");
				equivalenceCount.setText("Total equivalence queries: 0");
				manager = OWLManager.createOWLOntologyManager();

				ontology = manager.loadOntologyFromOntologyDocument(new File(filePath.getText()));

				rendering = new ManchesterOWLSyntaxOWLObjectRendererImpl();

				reasonerForT = createReasoner(ontology);
				shortFormProvider = new SimpleShortFormProvider();
				axiomsT = ontology.getAxioms();
				coExSet = new ArrayList<OWLSubClassOfAxiom>();

				ELQueryEngineForT = new ELEngine(reasonerForT, shortFormProvider);
				// transfer Origin ontology to ManchesterOWLSyntaxOntologyFormat
				OWLOntologyFormat format = manager.getOntologyFormat(ontology);
				ManchesterOWLSyntaxOntologyFormat manSyntaxFormat = new ManchesterOWLSyntaxOntologyFormat();
				if (format.isPrefixOWLOntologyFormat()) {
					manSyntaxFormat.copyPrefixesFrom(format.asPrefixOWLOntologyFormat());
				}
				ontologyFolderH = "src/main/resources/tmp/";
				ontologyFolder = "src/main/resources/tmp/";
				ontologyName ="";
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

				System.out.println("Loaded successfully.");
				System.out.println();
				concepts = getSuggestionNames("concept");
				roles = getSuggestionNames("role");
				System.out.println("Total number of concepts is: " + concepts.size());
				baseConcepts = new ArrayList<String>();
				for (String str : concepts)
					baseConcepts.add(str.substring(str.indexOf(" ") + 1));

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
						if (!coExSet.contains(newCounterexampleAxiom))
							coExSet.add((OWLSubClassOfAxiom) newCounterexampleAxiom);

						// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
						// ADD SATURATION FOR newCounterexampleAxiom HERE
						if (saturationBox.isSelected()) {
							OWLClassExpression ex = null;
							// System.out.println(newCounterexampleAxiom);
							//if (checkLeft(newCounterexampleAxiom)) {
								ex = siblingMerge(((OWLSubClassOfAxiom) newCounterexampleAxiom).getSubClass());
								newCounterexampleAxiom = saturateWithTreeLeft(
										ELQueryEngineForT.getSubClassAxiom(ex, superclass));
							/*} else {
								ex = siblingMerge(((OWLSubClassOfAxiom) newCounterexampleAxiom).getSuperClass());
								newCounterexampleAxiom = saturateWithTreeRight(
										ELQueryEngineForT.getSubClassAxiom(subclass, ex));
							}*/
						}
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
								if (saturationBox.isSelected()) {
									newCounterexampleAxiom = (OWLSubClassOfAxiom) newCounterexampleAxiom;
									OWLClassExpression ex = null;
									// System.out.println(newCounterexampleAxiom);

									//if (checkLeft(newCounterexampleAxiom)) {
										ex = siblingMerge(((OWLSubClassOfAxiom) newCounterexampleAxiom).getSubClass());
										newCounterexampleAxiom = saturateWithTreeLeft(
												ELQueryEngineForT.getSubClassAxiom(ex,
														((OWLSubClassOfAxiom) newCounterexampleAxiom).getSuperClass()));

									/*} else {
										ex = siblingMerge(
												((OWLSubClassOfAxiom) newCounterexampleAxiom).getSuperClass());
										newCounterexampleAxiom = saturateWithTreeRight(
												ELQueryEngineForT.getSubClassAxiom(
														((OWLSubClassOfAxiom) newCounterexampleAxiom).getSubClass(),
														ex));
									}*/

								}
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

						// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
						// ADD SATURATION FOR Axiom HERE
						if (saturationBox.isSelected()) {
							OWLClassExpression ex = null;
							//if (checkLeft(Axiom)) {
								ex = siblingMerge(((OWLSubClassOfAxiom) Axiom).getSubClass());

								Axiom = saturateWithTreeLeft(ELQueryEngineForT.getSubClassAxiom(ex,
										((OWLSubClassOfAxiom) Axiom).getSuperClass()));
							/*} else {
								ex = siblingMerge(((OWLSubClassOfAxiom) Axiom).getSuperClass());
								Axiom = saturateWithTreeRight(ELQueryEngineForT
										.getSubClassAxiom(((OWLSubClassOfAxiom) Axiom).getSubClass(), ex));
							}*/

						}
						// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
						lastCE = Axiom;
						return addHypothesis((OWLSubClassOfAxiom) Axiom);
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

							// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
							// ADD SATURATION FOR subClassAxiom HERE
							if (saturationBox.isSelected()) {
								System.out.println("CE is : " + Axiom);
								Axiom = (OWLAxiom) subClassAxiom;
								OWLClassExpression ex = null;
								//if (checkLeft(Axiom)) {
									ex = siblingMerge(((OWLSubClassOfAxiom) Axiom).getSubClass());
									Axiom = saturateWithTreeLeft(ELQueryEngineForT.getSubClassAxiom(ex,
											((OWLSubClassOfAxiom) Axiom).getSuperClass()));
								/*} else {
									ex = siblingMerge(((OWLSubClassOfAxiom) Axiom).getSuperClass());
									Axiom = saturateWithTreeRight(ELQueryEngineForT
											.getSubClassAxiom(((OWLSubClassOfAxiom) Axiom).getSubClass(), ex));
								}*/

							}
							// *-*-*-*-*-*-*-*-*-*-*-*-*-**-*-*-*-*-*-*-*-*-*-*-*-*-*
							lastCE = Axiom;
							return addHypothesis(Axiom);
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
							+ "since it can be replaced by some axioms that have existed in the hypothesis.";
				} else {
					message = "The axiom [" + removedstring + "]" + "will be removed after adding:"
							+ rendering.render(addedAxiom);
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
