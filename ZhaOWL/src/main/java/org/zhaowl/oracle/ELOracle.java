package org.zhaowl.oracle;

import java.util.ArrayList;
import java.util.HashSet;
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
import org.zhaowl.tree.ELNode;
import org.zhaowl.tree.ELTree;
import org.zhaowl.userInterface.ELEngine;
import org.zhaowl.userInterface.ELInterface;

public class ELOracle {
	public OWLReasoner reasonerForH;
	public ShortFormProvider shortFormProvider;
	public OWLOntology ontologyH;
	public OWLOntology ontology;
	public ELEngine engineForT;
	public ELInterface elinterface;
	
	public ELOracle(OWLReasoner reasoner, ShortFormProvider shortForm, OWLOntology ontology, OWLOntology ontologyH, ELEngine engineT, ELInterface elinterface) {
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
	
	public OWLClassExpression oracleSiblingMerge(OWLClassExpression left, OWLClassExpression right) throws Exception {
		// the oracle must do sibling merging (if possible)
		// on the left hand side
		ELTree tree = new ELTree(left);
		Set<ELNode> nodes = null;
		// System.out.println(tree.toDescriptionString());
		reasonerForH = createReasoner(ontologyH);
		ELEngine engineH = new ELEngine(reasonerForH, shortFormProvider);
		OWLClassExpression oldTree = engineForT.parseClassExpression(tree.toDescriptionString());
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
									if (engineH.entailed(engineForT.getSubClassAxiom(left,
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
	public OWLAxiom saturateWithTreeLeft(OWLSubClassOfAxiom axiom) throws Exception {
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
						OWLClassExpression newEx = engineForT.parseClassExpression(tree.toDescriptionString());
						// System.out.println("After saturation step: " + tree.toDescriptionString());
						OWLAxiom newAx = engineForT.getSubClassAxiom(newEx, sup);
						elinterface.membCount++;
						if (ELQueryEngineH.entailed(newAx)) {
							tree = new ELTree(sub);
						} else {
							sub = engineForT.parseClassExpression(tree.toDescriptionString());
						}

					}

				}
		}
		// System.out.println("Tree: " + tree.getRootNode());
		// System.out.println("Aux Tree: " + auxTree.getRootNode());
		// System.out.println("Final after saturation: " + sub);
		return engineForT.getSubClassAxiom(engineForT.parseClassExpression(tree.toDescriptionString()),
				sup);
	}
	public OWLClassExpression unsaturateRight(OWLAxiom ax) throws Exception {
		OWLClassExpression left = ((OWLSubClassOfAxiom) ax).getSubClass();
		OWLClassExpression right = ((OWLSubClassOfAxiom) ax).getSuperClass();
		ELTree tree = null;
		tree = new ELTree(right);

		Set<ELNode> nodes = null;
		reasonerForH = createReasoner(ontologyH);
		ELEngine engineH = new ELEngine(reasonerForH, shortFormProvider);

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
						if (engineH.entailed(engineForT.parseToOWLSubClassOfAxiom(
								(new ELTree(left)).toDescriptionString(), tree.toDescriptionString()))) {
							try {
								elinterface.addHypothesis(engineForT.parseToOWLSubClassOfAxiom(
										(new ELTree(left).toDescriptionString()), tree.toDescriptionString()));
								elinterface.hypoField.setText(elinterface.showHypothesis());
							} catch (Exception e2) {
								e2.printStackTrace();
							}
							// System.out.println("this one: " + cl);

							// nod.label = new TreeSet<OWLClass>();
						} else {
							// System.out.println("This one is bad: " + cl);
							continue;

						}

					}

				}
				// reset power set size to check
				foundSomething = false;
				sizeToCheck = 0;
			}
		}
		elinterface.showQueryCount();
		return engineForT.parseClassExpression(tree.toDescriptionString());
	}

}
