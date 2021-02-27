package ca.uqac.lif.mcdc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TruncationMetric 
{
	/**
	 * The value of ln(2)
	 */
	protected static final transient double LN_2 = Math.log(2);

	/**
	 * An identifier for the holograms manipulated by the metric.
	 */
	protected transient ObjectIdentifier<HologramNode> m_hologramIdentifier;
	
	/**
	 * Creates a new truncation metric.
	 */
	public TruncationMetric()
	{
		super();
		m_hologramIdentifier = new ObjectIdentifier<HologramNode>();
	}
	/**
	 * Converts a collection of elements into a list by imposing an arbitrary
	 * order on its elements.
	 * @param coll The collection
	 * @return The list
	 */
	protected static List<Truncation> setToList(Collection<Truncation> coll)
	{
		List<Truncation> list = new ArrayList<Truncation>(coll.size());
		for (Truncation t: coll)
		{
			list.add(t);
		}
		return list;
	}

	protected NumericTuple getTuple(HologramNode original, List<Truncation> criterion)
	{
		NumericTuple tuple = new NumericTuple();
		for (Truncation t : criterion)
		{
			HologramNode n = t.applyTo(original);
			tuple.add(m_hologramIdentifier.getObjectId(n));
		}
		return tuple;
	}
	
	/**
	 * Utility method to compute the base-2 logarithm of a number.
	 * @param x The number
	 * @return log_2(x)
	 */
	protected static double lg2(double x)
	{
		return Math.log(x) / LN_2;
	}
}
