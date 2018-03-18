package tree;

import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.dllearner.core.owl.DatatypePropertyHierarchy;
import org.dllearner.core.owl.Hierarchy;
import org.dllearner.core.owl.ObjectPropertyHierarchy;
import org.dllearner.core.owl.fuzzydll.FuzzyIndividual;
import org.dllearner.utilities.datastructures.SortedSetTuple;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataRange;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLProperty;
import org.semanticweb.owlapi.reasoner.NodeSet;

public interface Reasoners {
	/**
	 * Returns all named classes, which are not satisfiable, i.e. cannot 
	 * have instances.
	 * @return The set of inconsistent classes.
	 */
	Set<OWLClass> getInconsistentClasses();
	
	/**
	 * Returns the domain of this object property. (Theoretically, there could
	 * be more than one domain axiom. However, this can be considered a modelling
	 * error.)
	 * @param objectProperty An object property in the knowledge base.
	 * @return The rdfs:domain of <code>objectProperty</code>
	 */
	OWLClassExpression getDomain(OWLObjectProperty objectProperty);
	
	/**
	 * Returns the domain of this data property.
	 * @param datatypeProperty An data property in the knowledge base.
	 * @return The rdfs:domain of <code>datatypeProperty</code>
	 */
	OWLClassExpression getDomain(OWLDataProperty datatypeProperty);
	
	/**
	 * Returns the range of this object property.
	 * @param objectProperty An object property in the knowledge base.
	 * @return The rdfs:range of <code>objectProperty</code>
	 */
	OWLClassExpression getRange(OWLObjectProperty objectProperty);
	
	/**
	 * Returns the range of this data property.
	 * @param datatypeProperty An data property in the knowledge base.
	 * @return The rdfs:range of <code>datatypeProperty</code>
	 */
	OWLDataRange getRange(OWLDataProperty datatypeProperty);
	
	/**
	 * Checks whether <code>superClass</code> is a super class of <code>subClass</code>.
	 * @param superClass The (supposed) super class.
	 * @param subClass The (supposed) sub class.
	 * @return Whether <code>superClass</code> is a super class of <code>subClass</code>.
	 */
	boolean isSuperClassOf(OWLClassExpression superClass, OWLClassExpression subClass);
	
	/**
	 * Checks whether <code>class1</code> is equivalent to <code>class2</code>.
	 * @param class1 The first class.
	 * @param class2 The second class2.
	 * @return Whether <code>class1</code> is equivalent to <code>class2</code>.
	 */
	boolean isEquivalentClass(OWLClassExpression class1, OWLClassExpression class2);
	
	/**
	 * Checks whether <code>class1</code> is disjoint with <code>class2</code>.
	 * @param class1 The first class.
	 * @param class2 The second class2.
	 * @return Whether <code>class1</code> is disjoint with <code>class2</code>.
	 */
	boolean isDisjoint(OWLClass class1, OWLClass class2);
		
	/**
	 * Returns all asserted owl:equivalence class axioms for the given class.
	 * @param namedClass A named class in the background knowledge.
	 * @return A set of descriptions asserted to be equal to the named class.
	 */
	Set<OWLClassExpression> getAssertedDefinitions(OWLClass namedClass);
	
	/**
	 * Checks which of <code>superClasses</code> are super classes of <code>subClass</code>
	 * @param superClasses A set of (supposed) super classes.
	 * @param subClasses The (supposed) sub class.
	 * @return The subset of <code>superClasses</code>, which satisfy the superclass-subclass relationship.
	 */
	Set<OWLClassExpression> isSuperClassOf(Set<OWLClassExpression> superClasses, OWLClassExpression subClasses);

	/**
	 * Computes and returns the class hierarchy of the knowledge base.
	 *
	 * @return The subsumption hierarchy of this knowledge base.
	 */
	ClassHierarchyT getClassHierarchy();
	
	/**
	 * Returns direct super classes in the class hierarchy.
	 * 
	 * @param description
	 *            Atomic concept, top, or bottom.
	 * @return A set of more general concepts.
	 */
	NodeSet<OWLClass> getSuperClasses(OWLClassExpression description);

	/**
	 * Returns direct sub classes in the class hierarchy.
	 * 
	 * @param description
	 *            Atomic concept, top, or bottom.
	 * @return A set of more special concepts.
	 */
	NodeSet<OWLClass> getSubClasses(OWLClassExpression description);

	/**
	 * Computes and returns the object property hierarchy of the knowledge base.
	 * @return The object property hierarchy of the knowlege base.
	 */
	ObjectPropertyHierarchyT getObjectPropertyHierarchy();
	
	/**
	 * Returns more general concepts in the subsumption hierarchy.
	 * 
	 * @see ObjectPropertyHierarchy#getMoreGeneralRoles(OWLObjectProperty)
	 * @param objectProperty
	 *            Atomic concept, top, or bottom.
	 * @return A set of more general concepts.
	 */
	SortedSet<OWLObjectProperty> getSuperProperties(OWLObjectProperty objectProperty);

	/**
	 * Returns more special concepts in the subsumption hierarchy.
	 * 
	 * @see ObjectPropertyHierarchy#getMoreSpecialRoles(OWLObjectProperty)
	 * @param objectProperty
	 *            Atomic concept, top, or bottom.
	 * @return A set of more special concepts.
	 */
	SortedSet<OWLObjectProperty> getSubProperties(OWLObjectProperty objectProperty);

	/**
	 * TODO Outdated in OWL 2, because the universal role is the most general.
	 * @see ObjectPropertyHierarchy#getMostGeneralRoles()
	 * @return The most general roles.
	 */
	SortedSet<OWLObjectProperty> getMostGeneralProperties();

	/**
	 * TODO Outdated in OWL, because the bottom role is the most specific.
	 * @see ObjectPropertyHierarchy#getMostSpecialRoles()
	 * @return The most special roles.
	 */
	SortedSet<OWLObjectProperty> getMostSpecialProperties();

	/**
	 * Computes and returns the data property hierarchy of the knowledge base.
	 * @return The data property hierarchy of the knowlege base.
	 */
	tree.DatatypePropertyHierarchy getDatatypePropertyHierarchy();
	
	/**
	 * Returns more general concepts in the subsumption hierarchy.
	 * 
	 * @see DatatypePropertyHierarchy#getMoreGeneralRoles(OWLDataProperty)
	 * @param dataProperty
	 *            Atomic concept, top, or bottom.
	 * @return A set of more general concepts.
	 */
	SortedSet<OWLDataProperty> getSuperProperties(OWLDataProperty dataProperty);

	/**
	 * Returns more special concepts in the subsumption hierarchy.
	 * 
	 * @see DatatypePropertyHierarchy#getMoreSpecialRoles(OWLDataProperty)
	 * @param dataProperty
	 *            Atomic concept, top, or bottom.
	 * @return A set of more special concepts.
	 */
	SortedSet<OWLDataProperty> getSubProperties(OWLDataProperty dataProperty);

	/**
	 * @see DatatypePropertyHierarchy#getMostGeneralRoles()
	 * @return The most general roles.
	 */
	SortedSet<OWLDataProperty> getMostGeneralDatatypeProperties();

	/**
	 * @see DatatypePropertyHierarchy#getMostSpecialRoles()
	 * @return The most special roles.
	 */
	SortedSet<OWLDataProperty> getMostSpecialDatatypeProperties();

	/**
	 * Computes all super properties for the given property.
	 * @param property the property
	 * @return all super properties
	 */
	<T extends OWLProperty> SortedSet<T> getSuperProperties(T property);

	/**
	 * Computes all sub properties for the given property.
	 * @param property the property
	 * @return all sub properties
	 */
	<T extends OWLProperty> SortedSet<T> getSubProperties(T property);
	/**
	 * Checks the fuzzy membership degree of <code>individual</code> over <code>description</code>.
	 * For instance, "Peter" may be an instance of "TallPerson" with fuzzy membership degree = 0.8.
	 * individual
	 * @param description An OWL class description.
	 * @param individual An individual.
	 * @return fuzzy membership degree of <code>individual</code> satisfying <code>description</code> [0-1].
	 */
	double hasTypeFuzzyMembership(OWLClassExpression description, FuzzyIndividual individual);
	
	SortedSet<FuzzyIndividual> getFuzzyIndividuals(OWLClassExpression concept);
	/**
	 * Checks consistency of the knowledge.
	 * @return True if the knowledge base is consistent and false otherwise.
	 */
	boolean isSatisfiable();
	
	/**
	 * Checks whether adding the specified axiom leads to an inconsistency.
	 * @param axiom The axiom to be added to the knowledge base.
	 * @return True of the knowledge base including the axiom is satisfiable. False otherwise.
	 */
	boolean remainsSatisfiable(OWLAxiom axiom);
	
	/**
	 * Gets all named classes in the knowledge base, e.g. Person, City, Car.
	 * @return All named classes in KB.
	 */
	Set<OWLClass> getClasses();
	
	/**
	 * Gets all object properties in the knowledge base, e.g. hasChild, isCapitalOf, hasEngine.
	 * @return All object properties in KB.
	 */
	Set<OWLObjectProperty> getObjectProperties();
	
	/**
	 * Gets all data properties in the knowledge base, e.g. hasIncome, height.
	 * @return All data properties in KB.
	 */
	Set<OWLDataProperty> getDatatypeProperties();
	
	/**
	 * Gets all data properties with range xsd:boolean.
	 * @return Boolean data properties in KB.
	 */
	Set<OWLDataProperty> getBooleanDatatypeProperties();
	
	/**
	 * Gets all data properties with a range that describes floating point values, i.e. 
	 * xsd:float, xsd:double and xsd:decimal.
	 * @return Floating point data properties in KB.
	 */
	Set<OWLDataProperty> getDoubleDatatypeProperties();
	
	/**
	 * Gets all data properties with a numeric range 
	 * @return Numeric data properties in KB.
	 */
	Set<OWLDataProperty> getNumericDataProperties();
	
	/**
	 * Gets all integer type data properties, i.e. with range 
	 * xsd:byte, xsd:short, xsd:int, xsd:integer, 
	 * xsd:negativeInteger, xsd:nonNegativeInteger,
	 * xsd:positiveInteger, xsd:nonPositiveInteger.
	 * @see org.dllearner.utilities.OWLAPIUtils#intDatatypes
	 * @return Integer data properties in KB.
	 */
	Set<OWLDataProperty> getIntDatatypeProperties();
	
	/**
	 * Gets all data properties with range xsd:string.
	 * TODO We could extend this to all types, which can be parsed into
	 * strings and even include the properties without any specified datatype.
	 * @see OWLDataProperty
	 * @return String data properties in KB.
	 */
	Set<OWLDataProperty> getStringDatatypeProperties();
	
	/**
	 * Gets all individuals in the knowledge base, e.g. Eric, London, Car829. 
	 * @return All individuals in KB.
	 */
	SortedSet<OWLIndividual> getIndividuals();

	/**
	 * Returns the base URI of the knowledge base. If several knowledge sources are
	 * used, we only pick one of their base URIs.
	 * @return The base URI, e.g. http://dbpedia.org/resource/.
	 */
	String getBaseURI();
	
	/**
	 * Returns the prefixes used in the knowledge base, e.g. foaf for
	 * foaf: <http://xmlns.com/foaf/0.1/>. If several knowledge sources are used,
	 * their prefixes are merged. (In case a prefix is defined twice with different
	 * values, we pick one of those.)
	 * @return The prefix mapping.
	 */
	Map<String, String> getPrefixes();
	
	/**
	 * Returns the RDFS labels of an entity.
	 * @param entity An entity, e.g. Machine.
	 * @return All values of rdfs:label for the entity, e.g. {"Machine"@en, "Maschine"@de}. 
	 */
	Set<OWLLiteral> getLabel(OWLEntity entity);
	/**
	 * Returns types of an individual, i.e. those classes where the individual
	 * is instance of. For instance, the individual eric could have type Person. 
	 * 
	 * @param individual An individual in the knowledge base.
	 * @return Types this individual is instance of.
	 */
	Set<OWLClass> getTypes(OWLIndividual individual);
	
	/**
	 * Checks whether <code>individual</code> is instance of <code>description</code>.
	 * For instance, "Leipzig" may be an instance of "City".
	 * 
	 * @param description An OWL class description.
	 * @param individual An individual.
	 * @return True if the instance has the OWLClassExpression as type and false otherwise.
	 */
	boolean hasType(OWLClassExpression description, OWLIndividual individual);
	
	/**
	 * Performs instance checks on a set of instances (reasoners might be more
	 * efficient than handling each check separately).
	 * @param description An OWL class description.
	 * @param individuals An individual.
	 * @return The subset of those instances, which have the given type.
	 */
	SortedSet<OWLIndividual> hasType(OWLClassExpression description, Set<OWLIndividual> individuals);
	
	/**
	 * Gets all instances of a given class expression in the knowledge base.
	 * @param description An OWL class description.
	 * @return All instances of the class description.
	 */
	SortedSet<OWLIndividual> getIndividuals(OWLClassExpression description);
	
	/**
	 * Performs a query for all instances of the given class expression and
	 * its negation. (Note that in OWL it is possible that the reasoner can
	 * neither deduce that an individual is instance of a class nor its 
	 * negation.) This method might be more efficient that performing a 
	 * two retrievals.
	 * 
	 * @param description An OWL class description.
	 * @return All instances of the class OWLClassExpression and its negation.
	 */
	SortedSetTuple<OWLIndividual> doubleRetrieval(OWLClassExpression description);
	
	/**
	 * Returns the set of individuals, which are connect to the given individual
	 * with the specified object property.
	 * @param individual An individual, e.g. eric.
	 * @param objectProperty An object property, e.g. hasChild.
	 * @return A set of individuals, e.g. {anna, maria}.
	 */
	Set<OWLIndividual> getRelatedIndividuals(OWLIndividual individual,
											 OWLObjectProperty objectProperty);
	
	/**
	 * Returns the set of individuals, which are connect to the given individual
	 * with the specified data property.
	 * @param individual An individual, e.g. eric.
	 * @param datatypeProperty A data property, e.g. hasIncome.
	 * @return A set of individuals, e.g. {48000^^xsd:int}.
	 */
	Set<OWLLiteral> getRelatedValues(OWLIndividual individual, OWLDataProperty datatypeProperty);
	
	/**
	 * A map of properties related to an individual, e.g. 
	 * {hasChild => {eric,anna}, hasSibling => {sebastian}}.
	 * 
	 * @param individual An individual.
	 * @return A map of of properties connected to the individual as keys and the individuals
	 * they point to as values.
	 */
	Map<OWLObjectProperty,Set<OWLIndividual>> getObjectPropertyRelationships(OWLIndividual individual);
	
	/**
	 * Computes and returns all connections between individuals through the specified
	 * property, e.g. {eric => {maria, anna}, anna => {eric}}.
	 * @param objectProperty An object property.
	 * @return The mapping of individuals to other individuals through this object property.
	 */
	Map<OWLIndividual, SortedSet<OWLIndividual>> getPropertyMembers(OWLObjectProperty objectProperty);

	/**
	 * Computes and returns all connections between individuals and values through the
	 * specified property, e.g. {eric => {48000^^xsd:int}, sarah => {56000^^xsd:int}}.
	 * @param datatypeProperty  A data property.
	 * @return The mapping between individuals and values through the given property.
	 */
	Map<OWLIndividual, SortedSet<OWLLiteral>> getDatatypeMembers(OWLDataProperty datatypeProperty);
	
	/**
	 * Convenience method, which can be used if it is known that the property has 
	 * values which can be parsed as double.
	 * @see #getDatatypeMembers(OWLDataProperty)
	 * @see Double#valueOf(String)
	 * @param datatypeProperty A data property.
	 * @return The mapping between individuals and double values through the given property.
	 */
	Map<OWLIndividual, SortedSet<Double>> getDoubleDatatypeMembers(OWLDataProperty datatypeProperty);
	
	/**
	 * Convenience method, which can be used if it is known that the property has 
	 * values which can be parsed as integer.
	 * @see #getDatatypeMembers(OWLDataProperty)
	 * @see Integer#valueOf(String)
	 * @param datatypeProperty A data property.
	 * @return The mapping between individuals and integer values through the given property.
	 */
	Map<OWLIndividual, SortedSet<Integer>> getIntDatatypeMembers(OWLDataProperty datatypeProperty);
	
	/**
	 * Convenience method, which can be used if it is known that the property has 
	 * values which can be parsed as given Number class.
	 * @see #getDatatypeMembers(OWLDataProperty)
	 * @param datatypeProperty A data property.
	 * @param clazz a Java Number subtype.
	 * @return The mapping between individuals and numeric values of given type through the given property.
	 */
	<T extends Number> Map<OWLIndividual, SortedSet<T>> getNumericDatatypeMembers(OWLDataProperty datatypeProperty, Class<T> clazz);

	/**
	 * Computes and returns all connections between individuals and numeric values through the
	 * specified property, e.g. {eric => {48000^^xsd:int}, sarah => {56000^^xsd:int}}.
	 * @param datatypeProperty  A data property.
	 * @return The mapping between individuals and numeric values through the given property.
	 */
	<T extends Number & Comparable<T>> Map<OWLIndividual, SortedSet<T>> getNumericDatatypeMembers(OWLDataProperty datatypeProperty);

	/**
	 * Convenience method, which can be used if it is known that the property has 
	 * values which can be parsed as boolean value. Only "true" or "false" are 
	 * accepted. If other values occur, a warning will be issued.
	 * @see #getDatatypeMembers(OWLDataProperty)
	 * @param datatypeProperty A data property.
	 * @return The mapping between individuals and boolean values through the given property.
	 */
	Map<OWLIndividual, SortedSet<Boolean>> getBooleanDatatypeMembers(OWLDataProperty datatypeProperty);

	/**
	 * Convenience method, which can be used to get all individuals, which have value
	 * "true" for the given property. Usually, data properties can have several values
	 * for a given individual, but this method will throw a runtime exception if this
	 * is the case (i.e. the set of values is {"true", "false"}). 
	 * @see #getDatatypeMembers(OWLDataProperty)
	 * @param datatypeProperty A data property.
	 * @return The set of individuals for which the boolean property holds.
	 */
	SortedSet<OWLIndividual> getTrueDatatypeMembers(OWLDataProperty datatypeProperty);
	
	/**
	 * Convenience method, which can be used to get all individuals, which have value
	 * "false" for the given property. Usually, data properties can have several values
	 * for a given individual, but this method will throw a runtime exception if this
	 * is the case (i.e. the set of values is {"true", "false"}).
	 * @see #getDatatypeMembers(OWLDataProperty)
	 * @param datatypeProperty A data property.
	 * @return The set of individuals for which the boolean property does not hold.
	 */
	SortedSet<OWLIndividual> getFalseDatatypeMembers(OWLDataProperty datatypeProperty);

	/**
	 * Convenience method, which can be used which returns the property values as
	 * strings (note that any literal can be represented as string, even numbers).
	 * @see #getDatatypeMembers(OWLDataProperty)
	 * @param datatypeProperty A data property.
	 * @return The mapping between individuals and string values through the given property.
	 */
	Map<OWLIndividual, SortedSet<String>> getStringDatatypeMembers(OWLDataProperty datatypeProperty);

	/**
	 * A map of data properties related to values, e.g.
	 * {birthDate => {eric, "1980-10-10"^^xsd:date}, height => {Mount_Everest, 8880}}.
	 *
	 * @param individual An individual.
	 * @return A map of of data properties connected to the individual as keys and the literals
	 * they point to as values.
	 */
	Map<OWLDataProperty, Set<OWLLiteral>> getDataPropertyRelationships(OWLIndividual individual);

}
