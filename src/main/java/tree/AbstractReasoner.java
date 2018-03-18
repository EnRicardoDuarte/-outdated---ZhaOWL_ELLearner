package tree;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.jena.reasoner.Reasoner;
import org.dllearner.core.KnowledgeSource;
import org.dllearner.core.ReasoningMethodUnsupportedException;
import org.dllearner.core.owl.fuzzydll.FuzzyIndividual;
import org.dllearner.utilities.OWLAPIUtils;
import org.dllearner.utilities.datastructures.SortedSetTuple;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;

import uk.ac.manchester.cs.owl.owlapi.OWLDataFactoryImpl;

public    class AbstractReasoner implements Reasoners{
	
	public static final NumberFormat numberFormat = NumberFormat.getInstance();
	 
	public boolean useInstanceChecks = false;

	// statistical data for particular reasoning operations
	public long instanceCheckReasoningTimeNs = 0;
	public int nrOfInstanceChecks = 0;
	public int nrOfMultiInstanceChecks = 0;
	public long retrievalReasoningTimeNs = 0;
	public int nrOfRetrievals = 0;
	public long subsumptionReasoningTimeNs = 0;
	public int nrOfSubsumptionChecks = 0;
	public int nrOfMultiSubsumptionChecks = 0;
	public int nrOfSubsumptionHierarchyQueries = 0;

	// rest of reasoning time
	public long otherReasoningTimeNs = 0;

	// time for all reasoning requests (usually longer than the sum of all
	// above)
	public long overallReasoningTimeNs = 0;

	// temporary variables (moved here for performance reasons)
	public long reasoningStartTimeTmp;
	public long reasoningDurationTmp;

	// list view
	public List<OWLClass> atomicConceptsList;
	public List<OWLObjectProperty> atomicRolesList;

	// hierarchies (they are computed the first time they are needed)
	 
	public OWLReasoner LiyiReasoner;
	public ClassHierarchyT subsumptionHierarchy = null; 
	public ObjectPropertyHierarchyT roleHierarchy = null; 
	public DatatypePropertyHierarchy datatypePropertyHierarchy = null;
 
	public boolean precomputeClassHierarchy = true; 
	public boolean precomputeObjectPropertyHierarchy = true; 
	public boolean precomputeDataPropertyHierarchy = true;
	
	public OWLDataFactory df = new OWLDataFactoryImpl();
	public OWLOntology ontology;
	public Multimap<OWLDatatype, OWLDataProperty> datatype2Properties = HashMultimap.create();
	public Map<OWLDataProperty, OWLDatatype> dataproperty2datatype = new HashMap<OWLDataProperty, OWLDatatype>();

	public boolean precomputePropertyDomains = true;
	public Map<OWLProperty, OWLClassExpression> propertyDomains = new HashMap<OWLProperty, OWLClassExpression>();

	 
	public boolean precomputeObjectPropertyRanges = true;
	public Map<OWLObjectProperty, OWLClassExpression> objectPropertyRanges = new HashMap<OWLObjectProperty, OWLClassExpression>();

	/**
	 * The underlying knowledge sources.
	 */
	 
	public Set<KnowledgeSource> sources;

    public AbstractReasoner(){

    }
    public AbstractReasoner(OWLDataFactory df, OWLOntology onto, OWLReasoner reasoner){
    	this.df = df;  
    	this.ontology = onto;
    	this.LiyiReasoner = reasoner;
    }
	/**
	 * Constructs a new reasoner component.
	 * 
	 * @param sources
	 *            The underlying knowledge sources.
	 */
	public AbstractReasoner(Set<KnowledgeSource> sources) {
		this.sources = sources;
	}
	
	public AbstractReasoner(KnowledgeSource source) {
		this(Collections.singleton(source));
	}

	/**
	 * Gets the knowledge sources used by this reasoner.
	 * 
	 * @return The underlying knowledge sources.
	 */
	public Set<KnowledgeSource> getSources() {
		return sources;
	}

	@Autowired
    public void setSources(Set<KnowledgeSource> sources){
        this.sources = sources;
    }
    
	@Autowired
    public void setSources(KnowledgeSource... sources) {
    	this.sources = new HashSet<>(Arrays.asList(sources));
    }

	/**
	 * Method to exchange the reasoner underlying the learning problem.
	 * Implementations, which do not only use the provided sources class
	 * variable, must make sure that a call to this method indeed changes them.
	 * 
	 * @param sources
	 *            The new knowledge sources.
	 */
	public void changeSources(Set<KnowledgeSource> sources) {
		this.sources = sources;
	}

	/**
	 * Gets the type of the underlying reasoner. Although rarely necessary,
	 * applications can use this to adapt their behaviour to the reasoner.
	 * 
	 * @return The reasoner type.
	 */
	 
	/**
	 * Reset all statistics. Usually, you do not need to call this. However, if
	 * you e.g. perform benchmarks of learning algorithms and performing
	 * reasoning operations, such as a consistency check, before starting the
	 * algorithm, you can use this method to reset all statistical values.
	 */
	public void resetStatistics() {
		instanceCheckReasoningTimeNs = 0;
		nrOfInstanceChecks = 0;
		retrievalReasoningTimeNs = 0;
		nrOfRetrievals = 0;
		subsumptionReasoningTimeNs = 0;
		nrOfSubsumptionChecks = 0;
		// subsumptionHierarchyTimeNs = 0;
		nrOfSubsumptionHierarchyQueries = 0;
		otherReasoningTimeNs = 0;
		overallReasoningTimeNs = 0;
	}

	/**
	 * Notify the reasoner component that the underlying knowledge base has
	 * changed and all caches (for named classes, subsumption hierarchies, etc.)
	 * should be invalidaded. TODO Currently, nothing is done to behave
	 * correctly after updates.
	 */
	 public void setUpdated() {
		// TODO currently, nothing is done to behave correctly after updates
	}

	/**
	 * Call this method to release the knowledge base. Not calling the method
	 * may (depending on the underlying reasoner) result in resources for this
	 * knowledge base not being freed, which can cause memory leaks.
	 */ 

	// we cannot expect callers of reasoning methods to reliably recover if
	// certain reasoning methods are not implemented by the backend; we also
	// should not require callers to build catch clauses each time they make
	// a reasoner request => for this reasoner, we throw a runtime exception
	// here
	public void handleExceptions(ReasoningMethodUnsupportedException e) {
		e.printStackTrace();
		throw new RuntimeException("Reasoning method not supported.", e);
	}

	@Override
	public final Set<OWLClass> getTypes(OWLIndividual individual) {
		Set<OWLClass> types = null;
		try {
			types = getTypesImpl(individual);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}
		return types;
	}

	public Set<OWLClass> getTypesImpl(OWLIndividual individual)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException(
				"Reasoner does not support to determine type of individual.");
	}

	@Override
	public final boolean isSuperClassOf(OWLClassExpression superClass, OWLClassExpression subClass) {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result = false;
		if(precomputeClassHierarchy) {
			if(superClass.isAnonymous() || subClass.isAnonymous()) {
				try {
					result = isSuperClassOfImpl(superClass, subClass);
				} catch (ReasoningMethodUnsupportedException e) {
					e.printStackTrace();
				}
			} else {
				return getClassHierarchy().isSubclassOf(subClass, superClass);
			}
		} else {
			try {
				result = isSuperClassOfImpl(superClass, subClass);
			} catch (ReasoningMethodUnsupportedException e) {
				e.printStackTrace();
			}
		}
		nrOfSubsumptionChecks++;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		subsumptionReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		
		return result;
	}

	public boolean isSuperClassOfImpl(OWLClassExpression superConcept, OWLClassExpression subConcept)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final boolean isEquivalentClass(OWLClassExpression class1, OWLClassExpression class2) {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result = false;
		try {
			result = isEquivalentClassImpl(class1, class2);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}
		nrOfSubsumptionChecks+=2;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		subsumptionReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		
		return result;
	}

	public boolean isEquivalentClassImpl(OWLClassExpression class1, OWLClassExpression class2) throws ReasoningMethodUnsupportedException {
		return isSuperClassOfImpl(class1,class2) && isSuperClassOfImpl(class2,class1);
	}	
	
	@Override
	public final boolean isDisjoint(OWLClass class1, OWLClass class2) {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result = false;
		try {
			result = isDisjointImpl(class1, class2);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}
		nrOfSubsumptionChecks++;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		subsumptionReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		
		return result;
	}

	public boolean isDisjointImpl(OWLClass superConcept, OWLClass subConcept)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	@Override
	public Set<OWLClassExpression> getAssertedDefinitions(OWLClass namedClass) {
		try {
			return getAssertedDefinitionsImpl(namedClass);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}
	
	public Set<OWLClassExpression> getAssertedDefinitionsImpl(OWLClass namedClass)
		throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}	
	
	@Override
	public final Set<OWLClassExpression> isSuperClassOf(Set<OWLClassExpression> superConcepts,
			OWLClassExpression subConcept) {
		reasoningStartTimeTmp = System.nanoTime();
		Set<OWLClassExpression> result = null;
		try {
			result = isSuperClassOfImpl(superConcepts, subConcept);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}
		nrOfSubsumptionChecks += superConcepts.size();
		nrOfMultiSubsumptionChecks++;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		subsumptionReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		return result;
	}

	public Set<OWLClassExpression> isSuperClassOfImpl(Set<OWLClassExpression> superConcepts,
			OWLClassExpression subConcept) throws ReasoningMethodUnsupportedException {
		Set<OWLClassExpression> returnSet = superConcepts.stream()
				.filter(superConcept -> isSuperClassOf(superConcept, subConcept))
				.collect(Collectors.toSet());
		return returnSet;
	}

	 
 

	@Override
	public final SortedSet<OWLIndividual> getIndividuals(OWLClassExpression concept) {
		reasoningStartTimeTmp = System.nanoTime();
		SortedSet<OWLIndividual> result;
		try {
			result = getIndividualsImpl(concept);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
		nrOfRetrievals++;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		retrievalReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		 
		return result;
	}

	public SortedSet<OWLIndividual> getIndividualsImpl(OWLClassExpression concept)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	 

	@Override
	public final boolean hasType(OWLClassExpression concept, OWLIndividual s) {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result = false;
		try {
			result = hasTypeImpl(concept, s);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}
		nrOfInstanceChecks++;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		instanceCheckReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		return result;
	}

	public boolean hasTypeImpl(OWLClassExpression concept, OWLIndividual individual)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final SortedSet<OWLIndividual> hasType(OWLClassExpression concept, Set<OWLIndividual> s) {
		// logger.debug("instanceCheck "+concept.toKBSyntaxString());
		reasoningStartTimeTmp = System.nanoTime();
		SortedSet<OWLIndividual> result = null;
		try {
			result = hasTypeImpl(concept, s);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}
		nrOfInstanceChecks += s.size();
		nrOfMultiInstanceChecks++;
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		instanceCheckReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		// logger.debug("instanceCheck done");
		return result;
	}

	public SortedSet<OWLIndividual> hasTypeImpl(OWLClassExpression concept, Set<OWLIndividual> individuals) throws ReasoningMethodUnsupportedException {
		SortedSet<OWLIndividual> returnSet = individuals.stream()
				.filter(individual -> hasType(concept, individual))
				.collect(Collectors.toCollection(TreeSet::new));
		return returnSet;
	}

	@Override
	public final Set<OWLClass> getInconsistentClasses() {
		try {
			return getInconsistentClassesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLClass> getInconsistentClassesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final boolean isSatisfiable() {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result;
		try {
			result = isSatisfiableImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return false;
		}
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		otherReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		return result;
	}

	public boolean isSatisfiableImpl() throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final boolean remainsSatisfiable(OWLAxiom axiom) {
		reasoningStartTimeTmp = System.nanoTime();
		boolean result;
		try {
			result = remainsSatisfiableImpl(axiom);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return false;
		}
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		otherReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		return result;
	}

	public boolean remainsSatisfiableImpl(OWLAxiom axiom) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}	
	
	@Override
	public final Map<OWLObjectProperty,Set<OWLIndividual>> getObjectPropertyRelationships(OWLIndividual individual) {
		try {
			return getObjectPropertyRelationshipsImpl(individual);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}		
	}
	
	public Map<OWLObjectProperty,Set<OWLIndividual>> getObjectPropertyRelationshipsImpl(OWLIndividual individual) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	@Override
	public final Map<OWLDataProperty, Set<OWLLiteral>> getDataPropertyRelationships(OWLIndividual individual) {
		try {
			return getDataPropertyRelationshipsImpl(individual);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Map<OWLDataProperty, Set<OWLLiteral>> getDataPropertyRelationshipsImpl(OWLIndividual individual)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
			
	
	@Override
	public final Set<OWLIndividual> getRelatedIndividuals(OWLIndividual individual,
			OWLObjectProperty objectProperty) {
		try {
			return getRelatedIndividualsImpl(individual, objectProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLIndividual> getRelatedIndividualsImpl(OWLIndividual individual,
			OWLObjectProperty objectProperty) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Set<OWLLiteral> getRelatedValues(OWLIndividual individual,
			OWLDataProperty datatypeProperty) {
		try {
			return getRelatedValuesImpl(individual, datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLLiteral> getRelatedValuesImpl(OWLIndividual individual,
			OWLDataProperty datatypeProperty) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Set<OWLLiteral> getLabel(OWLEntity entity) {
		try {
			return getLabelImpl(entity);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLLiteral> getLabelImpl(OWLEntity entity) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Map<OWLIndividual, SortedSet<OWLIndividual>> getPropertyMembers(OWLObjectProperty atomicRole) {
		reasoningStartTimeTmp = System.nanoTime();
		Map<OWLIndividual, SortedSet<OWLIndividual>> result;
		try {
			result = getPropertyMembersImpl(atomicRole);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
		reasoningDurationTmp = System.nanoTime() - reasoningStartTimeTmp;
		otherReasoningTimeNs += reasoningDurationTmp;
		overallReasoningTimeNs += reasoningDurationTmp;
		return result;
	}

	public Map<OWLIndividual, SortedSet<OWLIndividual>> getPropertyMembersImpl(
			OWLObjectProperty atomicRole) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Map<OWLIndividual, SortedSet<OWLLiteral>> getDatatypeMembers(
			OWLDataProperty datatypeProperty) {
		try {
			return getDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Map<OWLIndividual, SortedSet<OWLLiteral>> getDatatypeMembersImpl(
			OWLDataProperty datatypeProperty) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Map<OWLIndividual, SortedSet<Double>> getDoubleDatatypeMembers(
			OWLDataProperty datatypeProperty) {
		try {
			return getDoubleDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Map<OWLIndividual, SortedSet<Double>> getDoubleDatatypeMembersImpl(
			OWLDataProperty datatypeProperty) throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		Map<OWLIndividual, SortedSet<Double>> ret = new TreeMap<>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> e : mapping.entrySet()) {
			SortedSet<OWLLiteral> values = e.getValue();
			SortedSet<Double> valuesDouble = values.stream()
					.filter(lit -> OWLAPIUtils.floatDatatypes.contains(lit.getDatatype()))
					.map(lit -> Double.parseDouble(lit.getLiteral()))
					.collect(Collectors.toCollection(TreeSet::new));
			ret.put(e.getKey(), valuesDouble);
		}
		return ret;
	}
	
	@Override
	public final <T extends Number> Map<OWLIndividual, SortedSet<T>> getNumericDatatypeMembers(
			OWLDataProperty datatypeProperty, Class<T> clazz) {
		try {
			return getNumericDatatypeMembersImpl(datatypeProperty, clazz);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}
	
	public <T extends Number> Map<OWLIndividual, SortedSet<T>> getNumericDatatypeMembersImpl(
			OWLDataProperty datatypeProperty, Class<T> clazz) throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		Map<OWLIndividual, SortedSet<T>> ret = new TreeMap<>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> e : mapping.entrySet()) {
			SortedSet<OWLLiteral> values = e.getValue();
			SortedSet<T> numericValues = new TreeSet<>();
			for (OWLLiteral lit : values) {
				try {
					numericValues.add((T) numberFormat.parse(lit.getLiteral()));
				} catch (ParseException e1) {
					e1.printStackTrace();
				}
			}
			ret.put(e.getKey(), numericValues);
		}
		return ret;
	}
	
	@Override
	public final <T extends Number & Comparable<T>> Map<OWLIndividual, SortedSet<T>> getNumericDatatypeMembers(
			OWLDataProperty datatypeProperty) {
		try {
			return getNumericDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}
	
	public <T extends Number & Comparable<T>> Map<OWLIndividual, SortedSet<T>> getNumericDatatypeMembersImpl(
			OWLDataProperty datatypeProperty) throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		Map<OWLIndividual, SortedSet<T>> ret = new TreeMap<>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> entry : mapping.entrySet()) {
			OWLIndividual ind = entry.getKey();
			SortedSet<OWLLiteral> values = entry.getValue();
			SortedSet<T> numericValues = new TreeSet<>();
			for (OWLLiteral lit : values) {
				if(OWLAPIUtils.isIntegerDatatype(lit)) {
					numericValues.add((T) Integer.valueOf(lit.parseInteger()));
				} else {
					try {
						Number number;
						String litStr = lit.getLiteral();
						if(litStr.equalsIgnoreCase("NAN")) {
							number = Double.NaN;
						} else {
							number = numberFormat.parse(litStr);
							if(number instanceof Long) {
								number = Double.valueOf(number.toString());
							}
						}
						numericValues.add((T) (number) );
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
				
			}
			ret.put(ind, numericValues);
		}
		return ret;
	}

	@Override
	public final Map<OWLIndividual, SortedSet<Integer>> getIntDatatypeMembers(
			OWLDataProperty datatypeProperty) {
		try {
			return getIntDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Map<OWLIndividual, SortedSet<Integer>> getIntDatatypeMembersImpl(
			OWLDataProperty datatypeProperty) throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		Map<OWLIndividual, SortedSet<Integer>> ret = new TreeMap<OWLIndividual, SortedSet<Integer>>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> e : mapping.entrySet()) {
			SortedSet<OWLLiteral> values = e.getValue();
			SortedSet<Integer> valuesInt = values.stream()
					.filter(lit -> OWLAPIUtils.isIntegerDatatype(lit))
					.map((Function<OWLLiteral, Integer>) OWLLiteral::parseInteger)
					.collect(Collectors.toCollection(TreeSet::new));
			ret.put(e.getKey(), valuesInt);
		}
		return ret;
	}

	public final Map<OWLIndividual, SortedSet<Boolean>> getBooleanDatatypeMembers(
			OWLDataProperty datatypeProperty) {
		try {
			return getBooleanDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Map<OWLIndividual, SortedSet<Boolean>> getBooleanDatatypeMembersImpl(
			OWLDataProperty datatypeProperty) throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		Map<OWLIndividual, SortedSet<Boolean>> ret = new TreeMap<OWLIndividual, SortedSet<Boolean>>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> e : mapping.entrySet()) {
			SortedSet<OWLLiteral> values = e.getValue();
			SortedSet<Boolean> valuesBoolean = new TreeSet<>();
			for (OWLLiteral c : values) {
				String s = c.getLiteral();
				if (s.equalsIgnoreCase("true")) {
					valuesBoolean.add(true);
				} else if (s.equalsIgnoreCase("false")) {
					valuesBoolean.add(false);
				} else {
					System.out.println("Requested to parse boolean value of property " + datatypeProperty
							+ ", but " + c + " could not be parsed successfully.");
				}
			}
			ret.put(e.getKey(), valuesBoolean);
		}
		return ret;
	}

	@Override
	public final SortedSet<OWLIndividual> getTrueDatatypeMembers(OWLDataProperty datatypeProperty) {
		try {
			return getTrueDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public SortedSet<OWLIndividual> getTrueDatatypeMembersImpl(OWLDataProperty datatypeProperty)
			throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		SortedSet<OWLIndividual> ret = new TreeSet<OWLIndividual>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> e : mapping.entrySet()) {
			SortedSet<OWLLiteral> values = e.getValue();
			if (values.size() > 1) {
				System.out.println("Property " + datatypeProperty + " has more than one value " + e.getValue()
						+ " for individual " + e.getKey() + ". We ignore the value.");			
			} else {
				if (values.first().getLiteral().equalsIgnoreCase("true")) {
					ret.add(e.getKey());
				}
			}
		}
		return ret;
	}

	@Override
	public final SortedSet<OWLIndividual> getFalseDatatypeMembers(OWLDataProperty datatypeProperty) {
		try {
			return getFalseDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public SortedSet<OWLIndividual> getFalseDatatypeMembersImpl(OWLDataProperty datatypeProperty)
			throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		SortedSet<OWLIndividual> ret = new TreeSet<OWLIndividual>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> e : mapping.entrySet()) {
			SortedSet<OWLLiteral> values = e.getValue();
			if (values.size() > 1) {
				System.out.println("Property " + datatypeProperty + " has value " + e.getValue()
						+ ". Cannot determine whether it is false.");
			} else {
				if (values.first().getLiteral().equalsIgnoreCase("false")) {
					ret.add(e.getKey());
				}
			}
		}
		return ret;
	}

	@Override
	public final Map<OWLIndividual, SortedSet<String>> getStringDatatypeMembers(
			OWLDataProperty datatypeProperty) {
		try {
			return getStringDatatypeMembersImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Map<OWLIndividual, SortedSet<String>> getStringDatatypeMembersImpl(
			OWLDataProperty datatypeProperty) throws ReasoningMethodUnsupportedException {
		Map<OWLIndividual, SortedSet<OWLLiteral>> mapping = getDatatypeMembersImpl(datatypeProperty);
		Map<OWLIndividual, SortedSet<String>> ret = new TreeMap<>();
		for (Entry<OWLIndividual, SortedSet<OWLLiteral>> e : mapping.entrySet()) {
			SortedSet<OWLLiteral> values = e.getValue();
			SortedSet<String> valuesString = values.stream()
					.map(OWLLiteral::getLiteral)
					.collect(Collectors.toCollection(TreeSet::new));
			ret.put(e.getKey(), valuesString);
		}
		return ret;
	}
	
	@Override
	public final Set<OWLObjectProperty> getObjectProperties() {
		try {
			return getObjectPropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLObjectProperty> getObjectPropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	@Override
	public final Set<OWLDataProperty> getDatatypeProperties() {
		try {
			return getDatatypePropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLDataProperty> getDatatypePropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Set<OWLDataProperty> getBooleanDatatypeProperties() {
		try {
			return getBooleanDatatypePropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	// TODO Even if there is a small performance penalty, we could implement
	// the method right here by iterating over all data properties and
	// querying their ranges. At least, this should be done once we have a
	// reasoner independent of OWL API with datatype support.
	public Set<OWLDataProperty> getBooleanDatatypePropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	@Override
	public final Set<OWLDataProperty> getNumericDataProperties() {
		try {
			return getNumericDataPropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLDataProperty> getNumericDataPropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		return Sets.union(getIntDatatypePropertiesImpl(), getDoubleDatatypePropertiesImpl());
	}

	@Override
	public final Set<OWLDataProperty> getIntDatatypeProperties() {
		try {
			return getIntDatatypePropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLDataProperty> getIntDatatypePropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Set<OWLDataProperty> getDoubleDatatypeProperties() {
		try {
			return getDoubleDatatypePropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLDataProperty> getDoubleDatatypePropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final Set<OWLDataProperty> getStringDatatypeProperties() {
		try {
			return getStringDatatypePropertiesImpl();
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public Set<OWLDataProperty> getStringDatatypePropertiesImpl()
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}	
	
	@Override
	public final OWLClassExpression getDomain(OWLObjectProperty objectProperty) {
		try {
			return getDomainImpl(objectProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public OWLClassExpression getDomainImpl(OWLObjectProperty objectProperty)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final OWLClassExpression getDomain(OWLDataProperty datatypeProperty) {
		try {
			return getDomainImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public OWLClassExpression getDomainImpl(OWLDataProperty datatypeProperty)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final OWLClassExpression getRange(OWLObjectProperty objectProperty) {
		try {
			return getRangeImpl(objectProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public OWLClassExpression getRangeImpl(OWLObjectProperty objectProperty)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}

	@Override
	public final OWLDataRange getRange(OWLDataProperty datatypeProperty) {
		try {
			return getRangeImpl(datatypeProperty);
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
			return null;
		}
	}

	public OWLDataRange getRangeImpl(OWLDataProperty datatypeProperty)
			throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
 
	public  NodeSet<OWLClass> getSuperClasses(OWLClassExpression concept) { 
			return LiyiReasoner.getSuperClasses(concept, true);
		 
	}

	public NodeSet<OWLClass> getSuperClassesImpl(OWLClassExpression concept) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	public NodeSet<OWLClass> getSubClasses(OWLClassExpression concept) {
		return LiyiReasoner.getSubClasses(concept, true);
		
	}

	public NodeSet<OWLClass> getSubClassesImpl(OWLClassExpression concept) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}
	
	public final SortedSet<OWLClassExpression> getEquivalentClasses(OWLClassExpression concept) {
		return new TreeSet<OWLClassExpression>(Sets.intersection(getClassHierarchy().getSubClasses(concept), getClassHierarchy().getSuperClasses(concept)));
	}
	
	@Override
	public final <T extends OWLProperty> SortedSet<T> getSuperProperties(T role) {
		if(OWLObjectProperty.class.isInstance(role) && precomputeObjectPropertyHierarchy) {
			return (SortedSet<T>) getObjectPropertyHierarchy().getMoreGeneralRoles((OWLObjectProperty) role);
		} else if(OWLDataProperty.class.isInstance(role) && precomputeDataPropertyHierarchy) {
			return (SortedSet<T>) getDatatypePropertyHierarchy().getMoreGeneralRoles((OWLDataProperty) role);
		} else {
			try {
				return getSuperPropertiesImpl(role);
			} catch (ReasoningMethodUnsupportedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public <T extends OWLProperty> SortedSet<T> getSuperPropertiesImpl(T role) throws ReasoningMethodUnsupportedException {
		if(OWLObjectProperty.class.isInstance(role)) {
			return (SortedSet<T>) getSuperPropertiesImpl((OWLObjectProperty) role);
		} else if(OWLDataProperty.class.isInstance(role)) {
			return (SortedSet<T>) getSuperPropertiesImpl((OWLDataProperty) role);
		}
		throw new ReasoningMethodUnsupportedException();
	}
	
	@Override
	public final <T extends OWLProperty> SortedSet<T> getSubProperties(T role) {
		if(OWLObjectProperty.class.isInstance(role) && precomputeObjectPropertyHierarchy) {
			return (SortedSet<T>) getObjectPropertyHierarchy().getMoreSpecialRoles((OWLObjectProperty) role);
		} else if(OWLDataProperty.class.isInstance(role) && precomputeDataPropertyHierarchy) {
			return (SortedSet<T>) getDatatypePropertyHierarchy().getMoreSpecialRoles((OWLDataProperty) role);
		} else {
			try {
				return getSubPropertiesImpl(role);
			} catch (ReasoningMethodUnsupportedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public <T extends OWLProperty> SortedSet<T> getSubPropertiesImpl(T role) throws ReasoningMethodUnsupportedException {
		if(OWLObjectProperty.class.isInstance(role)) {
			return (SortedSet<T>) getSubPropertiesImpl((OWLObjectProperty) role);
		} else if(OWLDataProperty.class.isInstance(role)) {
			return (SortedSet<T>) getSubPropertiesImpl((OWLDataProperty) role);
		}
		throw new ReasoningMethodUnsupportedException();
	}

	public <T extends OWLProperty> OWLClassExpression getDomain(T role) {
		if(precomputePropertyDomains) {
			return propertyDomains.get(role);
		} else {
			try {
				return getDomainImpl(role);
			} catch (ReasoningMethodUnsupportedException e) {
				e.printStackTrace();
			}
		}
		throw null;
	}

	public <T extends OWLProperty> OWLClassExpression getDomainImpl(T role) throws ReasoningMethodUnsupportedException {
		if(OWLObjectProperty.class.isInstance(role)) {
			return getDomainImpl((OWLObjectProperty) role);
		} else if(OWLDataProperty.class.isInstance(role)) {
			return getDomainImpl((OWLDataProperty) role);
		}
		throw new ReasoningMethodUnsupportedException();
	}
	
	
	@Override
	public final SortedSet<OWLObjectProperty> getSuperProperties(OWLObjectProperty role) {
		if(precomputeObjectPropertyHierarchy) {
			return getObjectPropertyHierarchy().getMoreGeneralRoles(role);
		} else {
			try {
				return getSuperPropertiesImpl(role);
			} catch (ReasoningMethodUnsupportedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public SortedSet<OWLObjectProperty> getSuperPropertiesImpl(OWLObjectProperty role) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}	
	
	@Override
	public final SortedSet<OWLObjectProperty> getSubProperties(OWLObjectProperty role) {
		if(precomputeObjectPropertyHierarchy) {
			return getObjectPropertyHierarchy().getMoreSpecialRoles(role);
		} else {
			try {
				return getSuperPropertiesImpl(role);
			} catch (ReasoningMethodUnsupportedException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public SortedSet<OWLObjectProperty> getSubPropertiesImpl(OWLObjectProperty role) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}	
	
	@Override
	public final SortedSet<OWLObjectProperty> getMostGeneralProperties() {
		return getObjectPropertyHierarchy().getMostGeneralRoles();
	}

//	public SortedSet<OWLObjectProperty> getMostGeneralPropertiesImpl(OWLOWLObjectProperty role) throws ReasoningMethodUnsupportedException {
//		throw new ReasoningMethodUnsupportedException();
//	}	
	
	@Override
	public final SortedSet<OWLObjectProperty> getMostSpecialProperties() {
		return getObjectPropertyHierarchy().getMostSpecialRoles();
	}

//	public SortedSet<OWLObjectProperty> getMostSpecialPropertiesImpl(OWLOWLObjectProperty role) throws ReasoningMethodUnsupportedException {
//		throw new ReasoningMethodUnsupportedException();
//	}
	
	@Override
	public final SortedSet<OWLDataProperty> getSuperProperties(OWLDataProperty role) {
		return getDatatypePropertyHierarchy().getMoreGeneralRoles(role);
	}

	public SortedSet<OWLDataProperty> getSuperPropertiesImpl(OWLDataProperty role) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}		
	
	@Override
	public final SortedSet<OWLDataProperty> getSubProperties(OWLDataProperty role) {
		return getDatatypePropertyHierarchy().getMoreSpecialRoles(role);
	}

	public SortedSet<OWLDataProperty> getSubPropertiesImpl(OWLDataProperty role) throws ReasoningMethodUnsupportedException {
		throw new ReasoningMethodUnsupportedException();
	}		
	
	@Override
	public final SortedSet<OWLDataProperty> getMostGeneralDatatypeProperties() {
		return getDatatypePropertyHierarchy().getMostGeneralRoles();
	}

	@Override
	public final SortedSet<OWLDataProperty> getMostSpecialDatatypeProperties() {
		return getDatatypePropertyHierarchy().getMostSpecialRoles();
	}

	/**
	 * Creates the class hierarchy. Invoking this method is optional (if not
	 * called explicitly, it is called the first time, it is needed).
	 * 
	 * @return The class hierarchy.
	 * @throws ReasoningMethodUnsupportedException If any method needed to
	 * create the hierarchy is not supported by the underlying reasoner.
	 */
	public ClassHierarchyT prepareSubsumptionHierarchy() throws ReasoningMethodUnsupportedException {
		TreeMap<OWLClassExpression, SortedSet<OWLClassExpression>> subsumptionHierarchyUp = new TreeMap<OWLClassExpression, SortedSet<OWLClassExpression>>(
		);
		TreeMap<OWLClassExpression, SortedSet<OWLClassExpression>> subsumptionHierarchyDown = new TreeMap<OWLClassExpression, SortedSet<OWLClassExpression>>(
		);

		// parents/children of top ...
		Set<OWLClass> tmp = LiyiReasoner.getSubClasses(df.getOWLThing(),true).getFlattened();
		System.out.println("The subs are: " + tmp);
		Iterator<OWLClass> it = tmp.iterator();
		while(it.hasNext())
			System.out.println("Inside loop: " + it.next());
			
		SortedSet<OWLClassExpression> tmp2 = null;
		tmp2.addAll(tmp);
		
		System.out.println("Sorted: " + tmp2);
		
		subsumptionHierarchyUp.put(df.getOWLThing(), new TreeSet<OWLClassExpression>());
		subsumptionHierarchyDown.put(df.getOWLThing(), tmp2);
		
		
		// ... bottom ...
		tmp = LiyiReasoner.getSuperClasses(df.getOWLThing(),true).getFlattened();
		tmp2 = null;
		tmp2.addAll(tmp);
		subsumptionHierarchyUp.put(df.getOWLNothing(), tmp2);
		subsumptionHierarchyDown.put(df.getOWLNothing(), new TreeSet<OWLClassExpression>());
		
		// ... and named classes
		Set<OWLClass> atomicConcepts = ontology.getClassesInSignature();
		for (OWLClass atom : atomicConcepts) {
			tmp = LiyiReasoner.getSubClasses(atom, true).getFlattened();
			// quality control: we explicitly check that no reasoner implementation returns null here
			tmp2 = null;
			tmp2.addAll(tmp);
			
			if(tmp == null) {
				System.out.println("Class hierarchy: getSubClasses returned null instead of empty set."); 
			}			
			subsumptionHierarchyDown.put(atom, tmp2);

			tmp = LiyiReasoner.getSuperClasses(atom, true).getFlattened();
			// quality control: we explicitly check that no reasoner implementation returns null here
			if(tmp == null) {
				System.out.println("Class hierarchy: getSuperClasses returned null instead of empty set."); 
			}	
			tmp2 = null;
			tmp2.addAll(tmp);
			subsumptionHierarchyUp.put(atom, tmp2);
		}		

		 return new ClassHierarchyT(subsumptionHierarchyUp, subsumptionHierarchyDown);
	}
 
	public   ClassHierarchyT getClassHierarchy() {
		// class hierarchy is created on first invocation
		if (subsumptionHierarchy == null) {
			try {
				subsumptionHierarchy = prepareSubsumptionHierarchy();
			} catch (ReasoningMethodUnsupportedException e) {
				handleExceptions(e);
			}
		}
		return subsumptionHierarchy;
	}

	/**
	 * Creates the object property hierarchy. Invoking this method is optional
	 * (if not called explicitly, it is called the first time, it is needed).
	 * 
	 * @return The object property hierarchy.
	 * @throws ReasoningMethodUnsupportedException
	 *             Thrown if a reasoning method for object property 
	 *             hierarchy creation is not supported by the reasoner.
	 */
	public ObjectPropertyHierarchyT prepareObjectPropertyHierarchy()
			throws ReasoningMethodUnsupportedException {
		
		TreeMap<OWLObjectProperty, SortedSet<OWLObjectProperty>> roleHierarchyUp = new TreeMap<>(
		);
		TreeMap<OWLObjectProperty, SortedSet<OWLObjectProperty>> roleHierarchyDown = new TreeMap<>(
		);
 
		Set<OWLObjectProperty> atomicRoles = ontology.getObjectPropertiesInSignature();
		for (OWLObjectProperty role : atomicRoles) {
			roleHierarchyDown.put(role, getSubPropertiesImpl(role));
			roleHierarchyUp.put(role, getSuperPropertiesImpl(role));
		}
		roleHierarchy = new ObjectPropertyHierarchyT(roleHierarchyUp, roleHierarchyDown);
		return roleHierarchy;		
	}

	public ObjectPropertyHierarchyT getObjectPropertyHierarchy() {
		try {
			if (roleHierarchy == null) {
				roleHierarchy = prepareObjectPropertyHierarchy();
			}
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}

		return roleHierarchy;
	}
	
	public boolean isSubPropertyOf(OWLProperty subProperty, OWLProperty superProperty){
		if(subProperty.isOWLObjectProperty() && superProperty.isOWLObjectProperty()){
			return getObjectPropertyHierarchy().isSubpropertyOf((OWLObjectProperty)subProperty, (OWLObjectProperty)superProperty);
		} else if(subProperty.isOWLDataProperty() && superProperty.isOWLDataProperty()){
			return getDatatypePropertyHierarchy().isSubpropertyOf((OWLDataProperty)subProperty, (OWLDataProperty)superProperty);
		}
		return false;
	}

	/**
	 * Creates the data property hierarchy. Invoking this method is optional (if
	 * not called explicitly, it is called the first time, it is needed).
	 * 
	 * @return The data property hierarchy.
	 * @throws ReasoningMethodUnsupportedException
	 *             Thrown if data property hierarchy creation is not supported
	 *             by the reasoner.
	 */
	public DatatypePropertyHierarchy prepareDatatypePropertyHierarchy()
			throws ReasoningMethodUnsupportedException {
	
		TreeMap<OWLDataProperty, SortedSet<OWLDataProperty>> datatypePropertyHierarchyUp = new TreeMap<OWLDataProperty, SortedSet<OWLDataProperty>>(
		);
		TreeMap<OWLDataProperty, SortedSet<OWLDataProperty>> datatypePropertyHierarchyDown = new TreeMap<OWLDataProperty, SortedSet<OWLDataProperty>>(
		);
 
		Set<OWLDataProperty> datatypeProperties = ontology.getDataPropertiesInSignature();
		for (OWLDataProperty role : datatypeProperties) {
			datatypePropertyHierarchyDown.put(role, getSubPropertiesImpl(role));
			datatypePropertyHierarchyUp.put(role, getSuperPropertiesImpl(role));
		}

		return new DatatypePropertyHierarchy(datatypePropertyHierarchyUp, datatypePropertyHierarchyDown);		
	}
 
	public final DatatypePropertyHierarchy getDatatypePropertyHierarchy() {
	
		try {
			if (datatypePropertyHierarchy == null) {
				datatypePropertyHierarchy = prepareDatatypePropertyHierarchy();
			}
		} catch (ReasoningMethodUnsupportedException e) {
			handleExceptions(e);
		}

		return datatypePropertyHierarchy;
	}

	public List<OWLClass> getAtomicConceptsList() {
		if (atomicConceptsList == null)
			atomicConceptsList = new LinkedList<OWLClass>(getClasses());
		return atomicConceptsList;
	}

	public List<OWLClass> getAtomicConceptsList(boolean removeOWLThing) {
		List<OWLClass> classes = Lists.newArrayList(getAtomicConceptsList());
		if (removeOWLThing) {
			classes.remove(df.getOWLThing());
			classes.remove(df.getOWLNothing());
		}
		return classes;
	}
	
	public void setSubsumptionHierarchy(ClassHierarchyT subsumptionHierarchy) {
		this.subsumptionHierarchy = subsumptionHierarchy;
	}

	public List<OWLObjectProperty> getAtomicRolesList() {
		if (atomicRolesList == null)
			atomicRolesList = new LinkedList<>(getObjectProperties());
		return atomicRolesList;
	}

	public long getInstanceCheckReasoningTimeNs() {
		return instanceCheckReasoningTimeNs;
	}

	public long getRetrievalReasoningTimeNs() {
		return retrievalReasoningTimeNs;
	}

	public int getNrOfInstanceChecks() {
		return nrOfInstanceChecks;
	}

	public int getNrOfRetrievals() {
		return nrOfRetrievals;
	}

	public int getNrOfSubsumptionChecks() {
		return nrOfSubsumptionChecks;
	}

	public long getSubsumptionReasoningTimeNs() {
		return subsumptionReasoningTimeNs;
	}

	public int getNrOfSubsumptionHierarchyQueries() {
		return nrOfSubsumptionHierarchyQueries;
	}

	public long getOverallReasoningTimeNs() {
		return overallReasoningTimeNs;
	}

	public long getTimePerRetrievalNs() {
		return retrievalReasoningTimeNs / nrOfRetrievals;
	}

	public long getTimePerInstanceCheckNs() {
		return instanceCheckReasoningTimeNs / nrOfInstanceChecks;
	}

	public long getTimePerSubsumptionCheckNs() {
		return subsumptionReasoningTimeNs / nrOfSubsumptionChecks;
	}

	public int getNrOfMultiSubsumptionChecks() {
		return nrOfMultiSubsumptionChecks;
	}

	public int getNrOfMultiInstanceChecks() {
		return nrOfMultiInstanceChecks;
	}
	
	/**
	 * @param precomputeClassHierarchy the precomputeClassHierarchy to set
	 */
	public void setPrecomputeClassHierarchy(boolean precomputeClassHierarchy) {
		this.precomputeClassHierarchy = precomputeClassHierarchy;
	}
	
	/**
	 * @param precomputeObjectPropertyHierarchy the precomputeObjectPropertyHierarchy to set
	 */
	public void setPrecomputeObjectPropertyHierarchy(boolean precomputeObjectPropertyHierarchy) {
		this.precomputeObjectPropertyHierarchy = precomputeObjectPropertyHierarchy;
	}
	
	/**
	 * @param precomputeDataPropertyHierarchy the precomputeDataPropertyHierarchy to set
	 */
	public void setPrecomputeDataPropertyHierarchy(boolean precomputeDataPropertyHierarchy) {
		this.precomputeDataPropertyHierarchy = precomputeDataPropertyHierarchy;
	}
	
	/**
	 * @return all object properties with its domains.
	 */
	public Map<OWLObjectProperty, OWLClassExpression> getObjectPropertyDomains() {
		Map<OWLObjectProperty, OWLClassExpression> result = new HashMap<>();
		
		for (OWLObjectProperty op : getObjectProperties()) {
			OWLClassExpression domain = getDomain(op);
			result.put(op, domain);
		}
		
		return result;
	}
	
	/**
	 * @return all object properties with its range.
	 */
	public Map<OWLObjectProperty, OWLClassExpression> getObjectPropertyRanges() {
		Map<OWLObjectProperty, OWLClassExpression> result = new HashMap<>();
		
		for (OWLObjectProperty op : getObjectProperties()) {
			OWLClassExpression range = getRange(op);
			result.put(op, range);
		}
		
		return result;
	}
	
	/**
	 * @return all data properties with its domains.
	 */
	public Map<OWLDataProperty, OWLClassExpression> getDataPropertyDomains() {
		Map<OWLDataProperty, OWLClassExpression> result = new HashMap<>();
		
		for (OWLDataProperty dp : getDatatypeProperties()) {
			OWLClassExpression domain = getDomain(dp);
			result.put(dp, domain);
		}
		
		return result;
	}
	
	 
	
	/**************************************************************
	 * FUZZY EXTENSIONS
	 **************************************************************/
	
	 

	
	
	/**
	 * Returns the datatype of the data property, i.e. the range if it is a datatype.
	 * @param dp the data property
	 * @return the datatype of the data property
	 */
	public OWLDatatype getDatatype(OWLDataProperty dp) {
		return null;
	}
	
	/**
	 * Enabled a synchronized mode such that all reasoner methods are supposed
	 * to be thread safe.
	 */ 

	public boolean isUseInstanceChecks() {
		return useInstanceChecks;
	}

	public void setUseInstanceChecks(boolean useInstanceChecks) {
		this.useInstanceChecks = useInstanceChecks;
	}
	@Override
	public double hasTypeFuzzyMembership(OWLClassExpression description, FuzzyIndividual individual) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public SortedSet<FuzzyIndividual> getFuzzyIndividuals(OWLClassExpression concept) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Set<OWLClass> getClasses() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SortedSet<OWLIndividual> getIndividuals() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public String getBaseURI() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public Map<String, String> getPrefixes() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public SortedSetTuple<OWLIndividual> doubleRetrieval(OWLClassExpression description) {
		// TODO Auto-generated method stub
		return null;
	}
}
