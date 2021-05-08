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
package ca.uqac.lif.mcdc.test;

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.mcdc.ConfusionMatrix;
import ca.uqac.lif.mcdc.KeepIfMultipleUniqueTruePoint;
import ca.uqac.lif.mcdc.KeepNthClause;
import ca.uqac.lif.mcdc.KeepValuesOf;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;
import ca.uqac.lif.mcdc.TruncationEntropy;
import mcdclab.benchmark.TCASBenchmark;

import static ca.uqac.lif.mcdc.Conjunction.And;
import static ca.uqac.lif.mcdc.Disjunction.Or;
import static ca.uqac.lif.mcdc.Negation.Not;
import static org.junit.Assert.*;

public class ConfusionMatrixTest 
{
	protected static final Atom a = new Atom("a");
	protected static final Atom b = new Atom("b");
	protected static final Atom c = new Atom("c");
	protected static final Atom d = new Atom("d");
	
	@Test
	@Ignore
	public void testIdentity1()
	{
		// If we use the same criterion for both axes, the confusion matrix
		// is the identity matrix
		Operator op = And(a, b, c);
		Set<Truncation> criterion1 = KeepValuesOf.generateTWay(1, op);
		Set<Truncation> criterion2 = KeepValuesOf.generateTWay(1, op);
		ConfusionMatrix cm = new ConfusionMatrix(op, criterion1, criterion2);
		int[][] contents = cm.getArray();
		assertNotNull(contents);
		assertEquals(8, contents.length);
		for (int i = 0; i < 8; i++)
		{
			assertEquals(8, contents[i].length);
			for (int j = 0; j < 8; j++)
			{
				assertTrue(contents[i][j] == 0 || contents[i][j] == 1);
			}
		}
	}
	
	@Test
	public void testMatrix1()
	{
		TCASBenchmark benchmark = new TCASBenchmark();
		Operator op = benchmark.getFormula(5); //Or(And(a, b, c), And(Not(b), c), And(Not(a), Not(b)));
		Set<Truncation> criterion1 = KeepValuesOf.generateTWay(2, op);
		//Set<Truncation> criterion1 = toSet(new KeepValuesOf("a"), new KeepValuesOf("b"));
		//Set<Truncation> criterion2 = toSet(new KeepValuesOf("a"));
		//Set<Truncation> criterion2 = KeepNthClause.generateClauseCoverage(op);
		Set<Truncation> criterion2 = KeepIfMultipleUniqueTruePoint.generateMUTPCoverage(op);
		ConfusionMatrix cm = new ConfusionMatrix(op, criterion1, criterion2);
		double[][] contents = cm.getNormalizedArray();
		//ConfusionMatrix.printArray(contents, System.out);
		System.out.println(ConfusionMatrix.getAverageEntropyX(contents));
		System.out.println(ConfusionMatrix.getAverageEntropyY(contents));
		System.out.println(TruncationEntropy.calculate(op, criterion1));
		System.out.println(TruncationEntropy.calculate(op, criterion2));
	}
	
	public static Set<Truncation> toSet(Truncation ... truncations)
	{
		Set<Truncation> set = new HashSet<Truncation>(truncations.length);
		for (Truncation t : truncations)
		{
			set.add(t);
		}
		return set;
	}
}
