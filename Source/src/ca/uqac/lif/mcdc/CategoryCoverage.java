package ca.uqac.lif.mcdc;

import java.util.Set;

public class CategoryCoverage extends TruncationMetric
{
	protected Truncation[] m_truncations;
	
	public CategoryCoverage(Truncation ... truncations)
	{
		super();
		m_truncations = truncations;
	}
	
	public float getCoverage(Operator formula, Set<Valuation> test_suite)
	{
		ObjectIdentifier<HologramNode> identifier_all = new ObjectIdentifier<HologramNode>();
		ObjectIdentifier<HologramNode> identifier_suite = new ObjectIdentifier<HologramNode>();
		ValuationIterator it = new ValuationIterator(formula.getSortedVariables());
		while (it.hasNext())
		{
			Valuation v = it.next();
			HologramNode original = formula.evaluate(v);
			for (Truncation t : m_truncations)
			{
				HologramNode n = t.applyTo(original);
				identifier_all.seenBefore(n);
				if (test_suite.contains(v)) 
				{
					identifier_suite.seenBefore(n);
				}
			}
		}
		return (float) identifier_suite.countDistinctObjects() / (float) identifier_all.countDistinctObjects();
	}
}
