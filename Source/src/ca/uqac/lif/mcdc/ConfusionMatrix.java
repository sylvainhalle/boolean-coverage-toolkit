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

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfusionMatrix extends TruncationMetric
{
	/**
	 * A map keeping the count of valuations for each pair of hologram
	 * tuples
	 */
	protected Map<TupleTuple,Integer> m_entries;

	/**
	 * The formula on which the matrix is computed
	 */
	protected Operator m_formula;
	
	/**
	 * An object identifier for a first set of numeric tuples
	 */
	protected ObjectIdentifier<NumericTuple> m_tupleIdentifier1;
	
	/**
	 * An object identifier for a second set of numeric tuples
	 */
	protected ObjectIdentifier<NumericTuple> m_tupleIdentifier2;

	/**
	 * Creates a new confusion matrix.
	 * @param formula The formula to evaluate
	 * @param criterion1 The first coverage criterion, represented as a
	 * collection of tree transformations
	 * @param criterion2 The second coverage criterion, represented as a
	 * collection of tree transformations
	 */
	public ConfusionMatrix(Operator formula, Collection<Truncation> criterion1, Collection<Truncation> criterion2)
	{
		super();
		m_tupleIdentifier1 = new ObjectIdentifier<NumericTuple>();
		m_tupleIdentifier2 = new ObjectIdentifier<NumericTuple>();
		m_formula = formula;
		m_entries = new HashMap<TupleTuple,Integer>();
		List<Truncation> l_criterion1 = setToList(criterion1);
		List<Truncation> l_criterion2 = setToList(criterion2);
		ValuationIterator it = new ValuationIterator(formula.getSortedVariables());
		while (it.hasNext())
		{
			Valuation v = it.next();
			HologramNode original = formula.evaluate(v);
			NumericTuple nt1 = getTuple(original, l_criterion1);
			NumericTuple nt2 = getTuple(original, l_criterion2);
			TupleTuple tt = new TupleTuple(m_tupleIdentifier1.getObjectId(nt1), m_tupleIdentifier2.getObjectId(nt2));
			if (m_entries.containsKey(tt))
			{
				m_entries.put(tt, m_entries.get(tt) + 1);
			}
			else
			{
				m_entries.put(tt, 1);
			}
		}
	}

	/**
	 * Gets the content of the table as bi-dimensional array of integers.
	 * @return The array
	 */
	public int[][] getArray()
	{
		int x = m_tupleIdentifier1.countDistinctObjects();
		int y = m_tupleIdentifier2.countDistinctObjects();
		int[][] array = new int[x][y];
		for (int i = 1; i <= x; i++)
		{
			for (int j = 1; j <= y; j++)
			{
				TupleTuple tt = new TupleTuple(i, j);
				int count = 0;
				if (m_entries.containsKey(tt))
				{
					count = m_entries.get(tt);
				}
				array[i - 1][j - 1] = count;
			}
		}
		return array;
	}

	/**
	 * Gets the content of the table as bi-dimensional array of integers.
	 * @return The array
	 */
	public double[][] getNormalizedArray()
	{
		int x = m_tupleIdentifier1.countDistinctObjects();
		int y = m_tupleIdentifier2.countDistinctObjects();
		int[][] array = getArray();
		double[][] n_array = new double[x][y];
		double total = Math.pow(2, m_formula.getVariables().size());
		for (int i = 0; i < x; i++)
		{
			for (int j = 0; j < y; j++)
			{

				n_array[i][j] = array[i][j] / total;
			}
		}
		return n_array;
	}

	/**
	 * Prints a two-dimensional array.
	 * @param array The array to print
	 * @param ps The {@link PrintStream} where to print the array
	 */
	public static void printArray(double[][] array, PrintStream ps)
	{
		for (int i = 0; i < array.length; i++)
		{
			for (int j = 0; j < array[i].length; j++)
			{
				if (j > 0)
				{
					ps.print("\t");
				}
				ps.print(array[i][j]);
			}
			ps.println();
		}
	}

	/**
	 * Gets the average entropy of each line of a matrix
	 * @param array The matrix
	 * @return The average entropy
	 */
	public static double getAverageEntropyX(double[][] array)
	{
		double v = 0;
		for (int i = 0; i < array.length; i++)
		{
			double line = 0, sub_total = 0;
			for (int j = 0; j < array[i].length; j++)
			{
				sub_total += array[i][j];
			}
			if (sub_total == 0)
			{
				continue;
			}
			for (int j = 0; j < array[i].length; j++)
			{
				double p = array[i][j] / sub_total;
				if (p == 0)
				{
					continue;
				}
				line += -(p * (Math.log(p) / LN_2));
			}
			v += line;
		}
		return v / (double) array.length;
	}
	
	/**
	 * Gets the average entropy of each column of a matrix
	 * @param array The matrix
	 * @return The average entropy
	 */
	public static double getAverageEntropyY(double[][] array)
	{
		double v = 0;
		for (int j = 0; j < array[0].length; j++)
		{
			double line = 0, sub_total = 0;
			for (int i = 0; i < array.length; i++)
			{
				sub_total += array[i][j];
			}
			if (sub_total == 0)
			{
				continue;
			}
			for (int i = 0; i < array.length; i++)
			{
				double p = array[i][j] / sub_total;
				if (p == 0)
				{
					continue;
				}
				line += -(p * lg2(p));
			}
			v += line;
		}
		return v / (double) array[0].length;
	}
	
	protected static class TupleTuple extends NumericTuple
	{
		public TupleTuple(int x, int y)
		{
			super();
			add(x);
			add(y);
		}
	}
}
