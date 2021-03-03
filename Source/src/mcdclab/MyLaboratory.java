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

import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.LatexNamer;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mcdc.ObjectIdentifier;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TransformedTable;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static mcdclab.CriterionFusionExperiment.CRITERIA;
import static mcdclab.CriterionFusionExperiment.SIZE_GLOBAL;
import static mcdclab.CriterionFusionExperiment.SIZE_MERGED;
import static mcdclab.CriterionFusionExperiment.TIME_GLOBAL;
import static mcdclab.CriterionFusionExperiment.TIME_MERGED;
import static mcdclab.FormulaBasedExperiment.FORMULA;
import static mcdclab.FormulaBasedExperiment.FORMULA_SIZE;
import static mcdclab.FormulaBasedExperiment.NUM_VARS;
import static mcdclab.TestGenerationExperiment.COVERAGE;
import static mcdclab.TestGenerationExperiment.CRITERION;
import static mcdclab.TestGenerationExperiment.METHOD;
import static mcdclab.TestGenerationExperiment.SIZE;
import static mcdclab.TestGenerationExperiment.TIME;

/**
 * The main laboratory.
 */
public class MyLaboratory extends Laboratory
{
	@Override
	public void setup()
	{
		boolean include_random = true;
		boolean include_safecomp = false;
		boolean include_comparison = false;
		boolean include_acts = true;

		// Basic stats about all formulas in the benchmark
		{
			OperatorProvider oprov = new OperatorProvider();
			addFormulas(oprov);
			add(new FormulaStats(this, oprov));
		}

		// Coverage for the random technique on all formulas
		if (include_random)
		{
			Group g = new Group("Random test suite generation experiments");
			add(g);

			CoverageTable et_coverage_random = new CoverageTable(CRITERION, COVERAGE);
			et_coverage_random.setTitle("Coverage of the random technique on all formulas");
			et_coverage_random.setNickname("tRandomCoverage");
			add(et_coverage_random);
			MinMaxCoverage mmc = new MinMaxCoverage(this, "Random", et_coverage_random);
			add(mmc);

			Region big_r = new Region();
			big_r.add(METHOD, RandomTestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_CLAUSE, TestSuiteGenerationFactory.C_PREDICATE, TestSuiteGenerationFactory.C_MCDC, TestSuiteGenerationFactory.C_2WAY, TestSuiteGenerationFactory.C_3WAY);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider);
			big_r.add(FORMULA, op_provider.getNames());

			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider);

			for (Region f_r : big_r.all(METHOD, CRITERION, FORMULA))
			{
				TestGenerationExperiment e = factory.get(f_r);
				if (e == null)
				{
					continue;
				}
				g.add(e);
				et_coverage_random.add(e);
			}
		}

		// Comparison with SAFECOMP 2018
		if (include_safecomp)
		{
			Group g = new Group("Test suite generation experiments (MC/DC coverage)");
			add(g);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(METHOD, HittingSetTestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_MCDC, TestSuiteGenerationFactory.C_2WAY, TestSuiteGenerationFactory.C_3WAY);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider);
			big_r.add(FORMULA, op_provider.getNames());

			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider);

			for (Region c_r : big_r.all(CRITERION))
			{
				String criterion = c_r.getString(CRITERION);
				ExperimentTable et_time = new ExperimentTable(FORMULA, METHOD, TIME);
				et_time.setShowInList(false);
				TransformedTable tt_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_time);
				tt_time.setTitle("Test generation time " + criterion);
				ExperimentTable et_size = new ExperimentTable(FORMULA, METHOD, SIZE);
				et_size.setShowInList(false);
				TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(METHOD, SIZE), et_size);
				tt_size.setTitle("Test suite size " + criterion);
				ExperimentTable et_size_vs_time = new ExperimentTable(SIZE, METHOD, TIME);
				et_size_vs_time.setShowInList(false);
				TransformedTable tt_size_vs_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_size_vs_time);
				tt_size_vs_time.setTitle("Generation time vs. test suite size " + criterion);
				tt_size_vs_time.setNickname(LatexNamer.latexify("ttTimeVsSize" + criterion));
				for (Region f_r : c_r.all(FORMULA))
				{
					TestGenerationExperiment e = factory.get(f_r);
					if (e == null)
					{
						continue;
					}
					g.add(e);
					et_time.add(e);
					et_size.add(e);
					et_size_vs_time.add(e);
				}
				add(tt_time, tt_size, tt_size_vs_time);
				Scatterplot p_size_vs_time = new Scatterplot(tt_size_vs_time);
				p_size_vs_time.withLines(false);
				add(p_size_vs_time);				
			}
		}

		// Comparison with ACTS on t-way
		if (include_acts)
		{
			// The factory to generate experiments
			Group g = new Group("Test suite generation experiments (combinatorial coverage)");
			add(g);

			RatioMacro rm_size = new RatioMacro(this, "sizeRatioACTS", SIZE, FORMULA, "Ratio between test suite size of hypergraph vs ACTS");
			RatioMacro rm_time = new RatioMacro(this, "timeRatioACTS", TIME, FORMULA, "Ratio between test suite time of hypergraph vs ACTS");
			add(rm_size, rm_time);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(METHOD, HittingSetTestGenerationExperiment.NAME, ActsTestGenerationExperiment.NAME, RandomTestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_2WAY, TestSuiteGenerationFactory.C_3WAY);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider);
			big_r.add(FORMULA, op_provider.getNames());

			ObjectIdentifier<ToolTriplet> identifier = new ObjectIdentifier<ToolTriplet>();

			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider);

			for (Region c_r : big_r.all(CRITERION))
			{
				String criterion = c_r.getString(CRITERION);
				int t = Integer.parseInt(criterion.substring(0, 1));
				ExperimentTable et_time = new ExperimentTable(NUM_VARS, METHOD, TIME);
				et_time.setShowInList(false);
				TransformedTable tt_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_time);
				tt_time.setTitle("Test generation time " + criterion);
				tt_time.setNickname(LatexNamer.latexify("ttTimeACTS" + criterion));
				ExperimentTable et_size = new ExperimentTable(NUM_VARS, METHOD, SIZE);
				et_size.setShowInList(false);
				TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(METHOD, SIZE), et_size);
				tt_size.setTitle("Test suite size " + criterion);
				tt_size.setNickname(LatexNamer.latexify("ttSizeACTS" + criterion));
				ExperimentTable et_size_vs_time = new ExperimentTable(SIZE, METHOD, TIME);
				et_size_vs_time.setShowInList(false);
				TransformedTable tt_size_vs_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_size_vs_time);
				tt_size_vs_time.setTitle("Generation time vs. test suite size " + criterion);
				tt_size_vs_time.setNickname(LatexNamer.latexify("ttSizeVsTimeACTS" + criterion));
				for (Region f_r : c_r.all(FORMULA, METHOD))
				{
					int n = op_provider.getFormula(f_r.getString(FORMULA)).getSize();
					if (identifier.seenBefore(new ToolTriplet(f_r.getString(METHOD), t, n)))
					{
						continue;
					}
					TestGenerationExperiment e = factory.get(f_r);
					if (e == null)
					{
						continue;
					}
					if (f_r.getString(METHOD).compareTo(HittingSetTestGenerationExperiment.NAME) == 0)
					{
						rm_size.addToFirstSet(e);
						rm_time.addToFirstSet(e);
					}
					if (f_r.getString(METHOD).compareTo(ActsTestGenerationExperiment.NAME) == 0)
					{
						rm_size.addToSecondSet(e);
						rm_time.addToSecondSet(e);
					}
					g.add(e);
					et_time.add(e);
					et_size.add(e);
					et_size_vs_time.add(e);
				}
				add(tt_time, tt_size, tt_size_vs_time);
				Scatterplot p_size_vs_time = new Scatterplot(tt_size_vs_time);
				p_size_vs_time.withLines(false);
				p_size_vs_time.setNickname(LatexNamer.latexify("pSizeVsTimeACTS" + criterion));
				add(p_size_vs_time);
			}
		}

		// Comparison merged vs. global
		if (include_comparison)
		{
			Group g = new Group("Merged vs. global test suite generation");
			add(g);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(CRITERIA, 
					CriterionFusionExperimentFactory.C_MCDC_PREDICATE, 
					CriterionFusionExperimentFactory.C_MCDC_2WAY,
					CriterionFusionExperimentFactory.C_CLAUSE_2WAY);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider);
			big_r.add(FORMULA, op_provider.getNames());

			// The factory to generate experiments
			CriterionFusionExperimentFactory factory = new CriterionFusionExperimentFactory(this, op_provider);

			CriteriaRatioTable crt_size = new CriteriaRatioTable(CRITERIA, "Criteria", SIZE_GLOBAL, SIZE_MERGED, "Size ratio");
			crt_size.setTitle("Size ratio between global and merged");
			crt_size.setNickname(LatexNamer.latexify("ttMergedSizeRatio"));
			CriteriaRatioTable crt_time = new CriteriaRatioTable(CRITERIA, "Criteria", TIME_GLOBAL, TIME_MERGED, "Time ratio");
			crt_time.setTitle("Time ratio between global and merged");
			crt_time.setNickname(LatexNamer.latexify("ttMergedTimeRatio"));
			add(crt_size, crt_time);
			for (Region c_r : big_r.all(CRITERIA))
			{
				String criteria = c_r.getString(CRITERIA);
				ExperimentTable et_size = new ExperimentTable(SIZE_GLOBAL, SIZE_MERGED);
				et_size.setTitle("Size comparison between global and merged " + criteria);
				et_size.setNickname(LatexNamer.latexify("ttMergedVsGlobalSize" + criteria));
				ExperimentTable et_time = new ExperimentTable(TIME_GLOBAL, TIME_MERGED);
				et_time.setTitle("Time comparison between global and merged " + criteria);
				et_size.setNickname(LatexNamer.latexify("ttMergedVsGlobalTime" + criteria));
				add(et_size, et_time);
				for (Region f_r : c_r.all(FORMULA))
				{
					CriterionFusionExperiment cfe = factory.get(f_r);
					if (cfe == null)
					{
						continue;
					}
					et_size.add(cfe);
					et_time.add(cfe);
					crt_size.add(cfe);
					crt_time.add(cfe);
					g.add(cfe);
				}
				Scatterplot p_size = new Scatterplot(et_size);
				p_size.setTitle(et_size.getTitle());
				p_size.withLines(false);
				p_size.setNickname(LatexNamer.latexify("pMergedVsGlobalSize" + criteria));
				add(p_size);
				Scatterplot p_time = new Scatterplot(et_time);
				p_time.setTitle(et_time.getTitle());
				p_time.withLines(false);
				p_time.setNickname(LatexNamer.latexify("pMergedVsGlobalTime" + criteria));
				add(p_time);
			}
		}
	}

	protected static void addFormulas(OperatorProvider provider)
	{
		int num_formulas = 10; // Normally 20, set to a smaller number for tests
		DnfOperatorPicker picker = new DnfOperatorPicker(new RandomInteger(1,10), new RandomInteger(2,20), new RandomFloat(), new RandomBoolean());
		for (int i = 1; i <= num_formulas; i++)
		{
			provider.add("TCAS " + i, TCASBenchmark.getFormula(i));
		}
		for (int i = 1; i < num_formulas; i++)
		{
			provider.add("Random " + i, picker.pick());
		}
	}

	protected static class ToolTriplet
	{
		protected int m_t;

		protected int m_n;

		protected String m_tool;

		public ToolTriplet(String tool, int t, int n)
		{
			super();
			m_tool = tool;
			m_t = t;
			m_n = n;
		}

		@Override
		public boolean equals(Object o)
		{
			ToolTriplet tt = (ToolTriplet) o;
			return tt.m_t == m_t && tt.m_n == m_n && tt.m_tool.compareTo(m_tool) == 0;
		}

		@Override
		public int hashCode()
		{
			return m_tool.hashCode() + m_n + m_t;
		}
	}

	protected static boolean seenCombination(Map<Integer,Set<Integer>> map, int t, int n)
	{
		if (!map.containsKey(t))
		{
			Set<Integer> set = new HashSet<Integer>();
			set.add(n);
			map.put(t, set);
			return false;
		}
		Set<Integer> set = map.get(t);
		boolean b = false;
		if (set.contains(n))
		{
			b = true;
		}
		else
		{
			set.add(n);
		}
		return b;
	}

	public static void main(String[] args)
	{
		// Nothing else to do here
		MyLaboratory.initialize(args, MyLaboratory.class);
	}
}
