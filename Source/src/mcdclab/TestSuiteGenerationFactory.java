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
package mcdclab;

import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.mcdc.KeepIfDetermines;
import ca.uqac.lif.mcdc.KeepIfMultipleNearFalsePoint;
import ca.uqac.lif.mcdc.KeepIfMultipleUniqueTruePoint;
import ca.uqac.lif.mcdc.KeepNthClause;
import ca.uqac.lif.mcdc.KeepValuesOf;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Truncation;

import static mcdclab.TestGenerationExperiment.CRITERION;
import static mcdclab.TestGenerationExperiment.FORMULA;
import static mcdclab.TestGenerationExperiment.METHOD;

import java.util.HashSet;
import java.util.Set;

/**
 * Generates experiments that compute a test suite based on a technique and a
 * particular coverage criterion.
 */
public class TestSuiteGenerationFactory extends FormulaBasedExperimentFactory<TestGenerationExperiment>
{	
	/**
	 * The name of criterion "predicate coverage"
	 */
	public static final transient String C_PREDICATE = "Predicate";
	
	/**
	 * The name of criterion "clause coverage"
	 */
	public static final transient String C_CLAUSE = "Clause";
	
	/**
	 * The name of criterion "MC/DC coverage"
	 */
	public static final transient String C_MCDC = "MC-DC";
	
	/**
	 * The name of criterion "2-way"
	 */
	public static final transient String C_2WAY = "2-way";
	
	/**
	 * The name of criterion "3-way"
	 */
	public static final transient String C_3WAY = "3-way";
	
	/**
	 * The name of criterion "MUTP coverage"
	 */
	public static final transient String C_MUTP = "MUTP";
	
	/**
	 * The name of criterion "MUMCUT coverage"
	 */
	public static final transient String C_MUMCUT = "MUMCUT";
	
	/**
	 * Creates a new experiment factory.
	 * @param lab The lab to which the experiments will be added
	 * @param provider A provider for formulas 
	 */
	public TestSuiteGenerationFactory(MyLaboratory lab, OperatorProvider provider)
	{
		super(lab, TestGenerationExperiment.class, provider);
	}
	
	public static Set<Truncation> getTruncations(Operator formula, String criterion)
	{
		Set<Truncation> out_set = new HashSet<Truncation>();
		if (criterion.compareTo(C_MCDC) == 0)
		{
			Set<String> vars = formula.getVariables();
			for (String v : vars)
			{
				out_set.add(new KeepIfDetermines(v));
			}
		}
		else if (criterion.compareTo(C_PREDICATE) == 0)
		{
			// Predicate coverage = 1-way
			out_set.addAll(KeepValuesOf.generateTWay(1, formula));
		}
		else if (criterion.compareTo(C_CLAUSE) == 0)
		{
			out_set.addAll(KeepNthClause.generateClauseCoverage(formula));
		}
		else if (criterion.endsWith("-way"))
		{
			// Combinatorial criterion
			int t = Integer.parseInt(criterion.substring(0, 1));
			out_set.addAll(KeepValuesOf.generateTWay(t, formula));
		}
		else if (criterion.compareTo(C_MUTP) == 0)
		{
			out_set.addAll(KeepValuesOf.generateTWay(1, formula));
		}
		else if (criterion.compareTo(C_MUMCUT) == 0)
		{
			out_set.addAll(KeepIfMultipleUniqueTruePoint.generateMUTPCoverage(formula));
			out_set.addAll(KeepIfMultipleNearFalsePoint.generateMNFPCoverage(formula));
		}
		return out_set;
	}
	
	@Override
	protected TestGenerationExperiment createExperiment(Region r)
	{
		String formula_name = r.getString(FORMULA);
		Operator op = m_provider.getFormula(r.getString(FORMULA));
		if (op == null)
		{
			return null;
		}
		String criterion = r.getString(CRITERION);
		if (criterion.endsWith("-way"))
		{
			int t = Integer.parseInt(criterion.substring(0, 1));
			if (op.getVariables().size() < t)
			{
				// Cannot calculate t-way with fewer than t variables
				return null;
			}
		}
		String method = r.getString(METHOD);
		if (method.compareTo(HittingSetTestGenerationExperiment.NAME) == 0)
		{
			return getHittingSetExperiment(op, formula_name, r.getString(CRITERION));
		}
		if (method.compareTo(RandomTestGenerationExperiment.NAME) == 0)
		{
			return getRandomExperiment(op, formula_name, r.getString(CRITERION));
		}
		if (method.compareTo(ActsTestGenerationExperiment.NAME) == 0)
		{
			return getActsExperiment(op, formula_name, r.getString(CRITERION));
		}
		if (method.compareTo(MCDCTestGenerationExperiment.NAME) == 0)
		{
			return getMCDCExperiment(op, formula_name, r.getString(CRITERION));
		}
		if (method.compareTo(Apsec99TestGenerationExperiment.NAME) == 0)
		{
			return getApsec99Experiment(op, formula_name, r.getString(CRITERION));
		}
		return null;
	}

	protected static HittingSetTestGenerationExperiment getHittingSetExperiment(Operator formula, String formula_name, String criterion)
	{
		Set<Truncation> truncations = getTruncations(formula, criterion);
		HittingSetTestGenerationExperiment e = new HittingSetTestGenerationExperiment(formula, formula_name, truncations);
		e.setInput(CRITERION, criterion);
		return e;
	}
	
	protected static RandomTestGenerationExperiment getRandomExperiment(Operator formula, String formula_name, String criterion)
	{
		Set<Truncation> truncations = getTruncations(formula, criterion);
		RandomTestGenerationExperiment e = new RandomTestGenerationExperiment(formula, formula_name, truncations);
		e.setInput(CRITERION, criterion);
		return e;
	}
	
	protected static Apsec99TestGenerationExperiment getApsec99Experiment(Operator formula, String formula_name, String criterion)
	{
		// Don't care returning something; since all APSEC experiments are
		// created beforehand, this method is not supposed to be called
		return null;
	}
	
	protected static MCDCTestGenerationExperiment getMCDCExperiment(Operator formula, String formula_name, String criterion)
	{
		if (!criterion.endsWith("DC"))
		{
			// mcdc only works for MC/DC coverage
			return null;
		}
		Set<Truncation> truncations = getTruncations(formula, criterion);
		MCDCTestGenerationExperiment e = new MCDCTestGenerationExperiment(formula, formula_name, truncations);
		e.setInput(CRITERION, criterion);
		return e;
	}
	
	protected static ActsTestGenerationExperiment getActsExperiment(Operator formula, String formula_name, String criterion)
	{
		if (!criterion.endsWith("-way"))
		{
			// ACTS only works for t-way coverage
			return null;
		}
		int t = Integer.parseInt(criterion.substring(0, 1));
		ActsTestGenerationExperiment e = new ActsTestGenerationExperiment(formula, formula_name, t);
		e.setInput(CRITERION, criterion);
		return e;
	}
}
