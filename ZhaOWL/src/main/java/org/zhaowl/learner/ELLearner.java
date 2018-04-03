package org.zhaowl.learner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.ShortFormProvider;
import org.zhaowl.tree.ELEdge;
import org.zhaowl.tree.ELNode;
import org.zhaowl.tree.ELTree;
import org.zhaowl.userInterface.ELEngine;
import org.zhaowl.userInterface.ELInterface;

public class ELLearner {

	public OWLReasoner reasonerForH;
	public ShortFormProvider shortFormProvider;
	public OWLOntology ontologyH;
	public OWLOntology ontology;
	public ELEngine engineForT;
	public ELInterface elinterface;
	
	
	public ELLearner(OWLReasoner reasoner, ShortFormProvider shortForm, OWLOntology ontology, OWLOntology ontologyH, ELEngine engineT, ELInterface elinterface) {
		this.reasonerForH = reasoner;
		this.shortFormProvider = shortForm;
		this.ontology = ontology;
		this.ontologyH = ontologyH;
		this.engineForT = engineT;
		this.elinterface = elinterface;
	}
	
	public OWLReasoner createReasoner(final OWLOntology rootOntology) {
		return new Reasoner.ReasonerFactory().createReasoner(rootOntology);
	}
	
	public OWLClassExpression unsaturateLeft(OWLAxiom ax) throws Exception {
		OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
		OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
		ELTree tree = null;
		tree = new ELTree(left);
		reasonerForH = createReasoner(ontologyH);
		Set<ELNode> nodes = null;

		int sizeToCheck = 0;

		// @foundSomething
		// this flag is used to create a new set of elements to iterate over,
		// in order to find if a proper combination of concepts that a node needs
		// in order to make the CI valid

		boolean foundSomething = false;

		for (int i = 0; i < tree.getMaxLevel(); i++) {
			nodes = tree.getNodesOnLevel(i + 1);
			for (ELNode nod : nodes) {

				while (!foundSomething) {
					// size of power set
					sizeToCheck++;
					// set to be used when building a power set of concepts
					Set<OWLClass> toBuildPS = new HashSet<OWLClass>();

					// populate set
					for (OWLClass cl : nod.label)
						toBuildPS.add(cl);

					// set of sets of concepts as power set
					Set<Set<OWLClass>> conceptSet = new HashSet<Set<OWLClass>>();

					// populate set of sets of concepts
					// @sizeToCheck is the number of concepts in the set
					// @sizeToCheck = 1, returns single concepts in power set (ps) [A,B,C,D]
					// @sizeToCheck = 2, returns ps size 2 of concepts [(A,B), (A,C), (A,D), (B,C),
					// (B,D), (C,D)]
					// and so on ...
					// this is done in order to check which is the minimal concept(s) set required
					// to satisfy the node
					// and at the same time, the CI
					conceptSet = powerSetBySize(toBuildPS, sizeToCheck);

					// loop through concept set
					for (Set<OWLClass> clSet : conceptSet) {

						nod.label = new TreeSet<OWLClass>();

						for (OWLClass cl : clSet)
							nod.label.add(cl);

						elinterface.membCount++;

						// System.out.println(tree.toDescriptionString());
						if (engineForT.entailed(engineForT
								.parseToOWLSubClassOfAxiom(tree.toDescriptionString(), (new ELTree(right))
										.toDescriptionString())))/*
																	 * && !engineForH.entailed(ELQueryEngineForT.
																	 * parseToOWLSubClassOfAxiom(
																	 * tree.toDescriptionString(), (new
																	 * ELTree(right)).toDescriptionString())))
																	 */ {
							foundSomething = true;
							try {
								elinterface.addHypothesis(engineForT.getSubClassAxiom(
										engineForT.parseClassExpression(tree.toDescriptionString()), right));
								elinterface.hypoField.setText(elinterface.showHypothesis());

							} catch (Exception e2) {
								e2.printStackTrace();
							}
							// System.out.println("this one is good: " + cl);

						} else {
							// System.out.println("This one is useless: " + cl);
							// nod.label = new TreeSet<OWLClass>();
							continue;

						}

					}

				}
				// reset power set size to check
				foundSomething = false;
				sizeToCheck = 0;
			}
		} 
		return engineForT.parseClassExpression(tree.toDescriptionString());
	}

	public void decompose(OWLClassExpression left, OWLClassExpression right) {

		// decomposition
		// creates a tree and loops through it to find "hidden" inclusions in it
		// take tree as: A and B and r.(B and C and s.(D and E))
		// tree with 2 nodes = [A,B] -r-> [B,C] -s-> [D,E]
		// decomposition separates tree and recursively checks based on 2 conditions
		// if non root node [B and C s.(D and E)] has some C' in concept names, such
		// that [B and C s.(D and E)] subClassOf C'
		// decompose C'
		// else if non root node without a branch as T-b [A and B and r.(B and C)] has
		// some C' in concept names,
		// such that [A and B and r.(B and C)] subClassOf C'
		// decompose C'
		// if C' turns out to be a single node, i.e. no branches in C', add the last
		// valid subClassOf relation
		// either [B and C s.(D and E)] subClassOf C' or
		// [A and B and r.(B and C)] subClassOf C' to the hypothesis

		try {
			ELTree treeR = null;
			ELTree treeL = null;
			boolean leftSide = false;
			// leftSide = checkLeft(ELQueryEngineForT.getSubClassAxiom(left, right));

			treeL = new ELTree(left);
			if (treeL.nodes.size() == 1) {
				elinterface.addHypothesis(engineForT.getSubClassAxiom(left, right));
				elinterface.hypoField.setText(elinterface.showHypothesis());
				// globalDecompose = 5;
				return;
			} else
				leftSide = true;

			treeR = new ELTree(right);
			if (treeR.nodes.size() == 1) {
				elinterface.addHypothesis(engineForT.getSubClassAxiom(left, right));
				elinterface.hypoField.setText(elinterface.showHypothesis());
				// globalDecompose = 5;
				return;
			}
			Set<ELNode> nodes = null;

			reasonerForH = createReasoner(ontologyH);
			ELEngine engineForH = new ELEngine(reasonerForH, shortFormProvider);
			List<ELEdge> auxEdges = new LinkedList<ELEdge>();
			if (leftSide)
				treeR = new ELTree(left);
			for (int i = 0; i < treeR.maxLevel; i++) {
				nodes = treeR.getNodesOnLevel(i + 1);
				for (ELNode nod : nodes) {
					if (nod.isRoot())
						continue;
					if (nod.label.size() < 1)
						continue;
					for (String cl : elinterface.concepts) {
						if (!cl.toString().contains("Thing")) {
							if (cl.contains("10.0"))
								continue;
							// System.out.println("Class: " + rendering.render(cl));
							OWLAxiom axiom = engineForT.getSubClassAxiom(
									engineForT.parseClassExpression(nod.toDescriptionString()),
									engineForT.parseClassExpression(cl));
							// System.out.println(axiom);
							for (int j = 0; j < nod.edges.size(); j++)
								auxEdges.add(nod.edges.get(j));
							// auxEx = ELQueryEngineForT.parseClassExpression(treeR.toDescriptionString());
							elinterface.membCount++;
							if (engineForT.entailed(axiom) && !engineForH.entailed(axiom)) {
								// System.out.println(nod.toDescriptionString());
								// System.out.println(cl);
								System.out.println(" Decompose this: " + axiom);
								decompose(engineForT.parseClassExpression(nod.toDescriptionString()),
										engineForT.parseClassExpression(cl));
								return;
							} else {
								nod.edges = new LinkedList<ELEdge>();
								if (!(nod.label.size() < 1)) {
									elinterface.membCount++;
									if (engineForT.entailed(engineForT.getSubClassAxiom(
											engineForT.parseClassExpression(treeR.toDescriptionString()), right))
											//&& !engineForH.entailed(engineForT.getSubClassAxiom(
											//		engineForT.parseClassExpression(treeR.toDescriptionString()),
											//		right))) {
											) {
										// System.out.println(treeR.toDescriptionString());
										decompose(engineForT.parseClassExpression(treeR.toDescriptionString()),
												right);
										return;
									}
								}
							}
							for (int j = 0; j < auxEdges.size(); j++)
								nod.edges.add(auxEdges.get(j));
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Error in decompose: " + e);
			return;
		}

		return;
	}

	public OWLAxiom saturateWithTreeRight(OWLAxiom axiom) throws Exception {
		OWLClassExpression sub = ((OWLSubClassOfAxiom) axiom).getSubClass();
		OWLClassExpression sup = ((OWLSubClassOfAxiom) axiom).getSuperClass();

		OWLReasoner auxReasonerForH = createReasoner(ontologyH);
		Set<OWLClass> cIo = ontology.getClassesInSignature();
		Set<ELNode> nodes = null;

		ELTree tree = new ELTree(sup);

		for (int i = 0; i < tree.getMaxLevel(); i++) {
			nodes = tree.getNodesOnLevel(i + 1);
			if (!nodes.isEmpty())
				for (ELNode nod : nodes) {
					// maxSaturate = 3;
					for (OWLClass cl : cIo) {
						// System.out.println("Node before: " + nod);
						// if(maxSaturate == 0)
						// break;
						if (!nod.label.contains(cl) && !cl.toString().contains(":Thing")) {
							nod.label.add(cl);
						}

						OWLClassExpression newEx = engineForT.parseClassExpression(tree.toDescriptionString());
						if (newEx.equals(null))
							System.out.println("is null");
						OWLAxiom newAx = engineForT.getSubClassAxiom(sub, newEx);

						// check if hypothesis entails new saturated CI
						elinterface.membCount++;
						if (engineForT.entailed(newAx)) {
							// CI is entailed by H, roll tree back to "safe" CI
							tree = new ELTree(sup);

						} else {
							sup = engineForT.parseClassExpression(tree.toDescriptionString());
						}

					}

				}
		}
		// System.out.println("Tree: " + tree.getRootNode());
		// System.out.println("Aux Tree: " + auxTree.getRootNode());
		// System.out.println("Final after saturation: " + sub);
		try {
			elinterface.addHypothesis(engineForT.getSubClassAxiom(sub,
					engineForT.parseClassExpression(tree.toDescriptionString())));
			elinterface.hypoField.setText(elinterface.showHypothesis());
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		return engineForT.getSubClassAxiom(sub, sup);
	}

	public OWLClassExpression learnerSiblingMerge(OWLClassExpression left, OWLClassExpression right) throws Exception {

		/*
		 * the learner must do sibling merging (if possible) on the right hand side
		 */

		ELTree tree = new ELTree(right);
		Set<ELNode> nodes = null;
		// System.out.println(tree.toDescriptionString());
		OWLClassExpression oldTree = engineForT.parseClassExpression(tree.toDescriptionString());
		System.out.println(tree.getRootNode());
		System.out.println(tree.toDescriptionString());
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
									// check if new merged tree is entailed by T
									if (engineForT.entailed(engineForT.getSubClassAxiom(left,
											engineForT.parseClassExpression(tree.toDescriptionString())))) {
										oldTree = engineForT.parseClassExpression(tree.toDescriptionString());
									} else {
										tree = new ELTree(oldTree);
									}
								}
							}
						}

					}
				}
		}
		// System.out.println(tree.getRootNode());
		return engineForT.parseClassExpression(tree.toDescriptionString());
	}
	
	public OWLClassExpression branchLeft(OWLClassExpression left, OWLClassExpression right) {
		try {

			ELTree treeL = new ELTree(left);
			Set<ELNode> nodes = null;
			List<ELEdge> auxEdges = null;
			// System.out.println("we branch this one: \n" + treeL.rootNode);
			for (int i = 0; i < treeL.maxLevel; i++) {
				nodes = treeL.getNodesOnLevel(i + 1);
				for (ELNode nod : nodes) {
					if (nod.edges.isEmpty())
						continue;
					auxEdges = new LinkedList<ELEdge>(nod.edges);
					for (int j = 0; j < auxEdges.size(); j++) {
						if (auxEdges.get(j).node.label.size() == 1)
							continue;
						// create list of classes in target node
						List<OWLClass> classAux = new ArrayList<OWLClass>();
						// fill list with classes
						for (OWLClass cl : auxEdges.get(j).node.label)
							classAux.add(cl);
						// for each class, create a node and add class to target node
						for (int k = 0; k < classAux.size(); k++) {
							nod.edges.add(new ELEdge(auxEdges.get(j).label, new ELNode(
									new ELTree(engineForT.parseClassExpression(elinterface.fixAxioms(classAux.get(k)))))));
							// add class to new node
							nod.edges.get(nod.edges.size() - 1).node.label.add(classAux.get(k));
							// remove class from old node
							auxEdges.get(j).node.label.remove(classAux.get(k));

						}
					}
				}
			}
			// System.out.println("branched tree : \n" + treeL.rootNode);
			// System.out.println(treeL.toDescriptionString());
			return engineForT.parseClassExpression(treeL.toDescriptionString());
		} catch (Exception e) {
			System.out.println("Error in branchNode: " + e);
		}
		return null;
	}

	public Set<Set<OWLClass>> powerSetBySize(Set<OWLClass> originalSet, int size) {
		Set<Set<OWLClass>> sets = new HashSet<Set<OWLClass>>();
		if (size == 0) {
			sets.add(new HashSet<OWLClass>());
			return sets;
		}
		List<OWLClass> list = new ArrayList<OWLClass>(originalSet);

		for (int i = 0; i < list.size(); i++) {
			OWLClass head = list.get(i);
			List<OWLClass> rest = list.subList(i + 1, list.size());
			Set<Set<OWLClass>> powerRest = powerSetBySize(new HashSet<OWLClass>(rest), size - 1);
			for (Set<OWLClass> p : powerRest) {
				HashSet<OWLClass> appendedSet = new HashSet<OWLClass>();
				appendedSet.add(head);
				appendedSet.addAll(p);
				sets.add(appendedSet);
			}
		}
		return sets;
	}

}
