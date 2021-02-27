/*
    Graph-based MC/DC testing
    Copyright (C) 2021 Sylvain Hallé

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Lesser General Public License as published
    by the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Lesser General Public License for more details.

    You should have received a copy of the GNU Lesser General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ca.uqac.lif.mcdc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TruncationEntropy extends TruncationMetric
{
	/**
	 * A map counting the number of occurrences of each hologram
	 */
	protected Map<Integer,Integer> m_entries;
	
	public TruncationEntropy()
	{
		super();
		m_entries = new HashMap<Integer,Integer>();
	}
	
	public static double calculate(Operator formula, Set<Truncation> criterion)
	{
		TruncationEntropy te = new TruncationEntropy();
		return te.getEntropy(formula, criterion);
	}
	
	public double getEntropy(Operator formula, Set<Truncation> criterion)
	{
		List<Truncation> l_criterion = setToList(criterion);
		ObjectIdentifier<NumericTuple> tuple_identifier = new ObjectIdentifier<NumericTuple>();
		ValuationIterator it = new ValuationIterator(formula.getSortedVariables());
		int total = 0;
		while (it.hasNext())
		{
			Valuation v = it.next();
			HologramNode original = formula.evaluate(v);
			int hnt = tuple_identifier.getObjectId(getTuple(original, l_criterion));
			if (m_entries.containsKey(hnt))
			{
				m_entries.put(hnt, m_entries.get(hnt) + 1);
			}
			else
			{
				m_entries.put(hnt, 1);
			}
			total++;
		}
		double entropy = 0;
		for (Map.Entry<Integer,Integer> e : m_entries.entrySet())
		{
			double p = (double) e.getValue() / (double) total;
			if (p == 0)
			{
				continue;
			}
			entropy += -(p * lg2(p));
		}
		return entropy;
	}
}
