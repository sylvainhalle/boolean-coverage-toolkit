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

import ca.uqac.lif.labpal.CliParser;
import ca.uqac.lif.labpal.CliParser.Argument;
import ca.uqac.lif.labpal.CliParser.ArgumentMap;
import ca.uqac.lif.labpal.Group;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.LatexNamer;
import ca.uqac.lif.labpal.Region;
import ca.uqac.lif.labpal.server.WebCallback;
import ca.uqac.lif.labpal.table.ExperimentTable;
import ca.uqac.lif.mcdc.ObjectIdentifier;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mtnp.plot.TwoDimensionalPlot.Axis;
import ca.uqac.lif.mtnp.plot.gnuplot.ClusteredHistogram;
import ca.uqac.lif.mtnp.plot.gnuplot.Scatterplot;
import ca.uqac.lif.mtnp.table.ExpandAsColumns;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TransformedTable;
import ca.uqac.lif.mtnp.util.FileHelper;
import ca.uqac.lif.synthia.random.RandomBoolean;
import ca.uqac.lif.synthia.random.RandomFloat;
import ca.uqac.lif.synthia.random.RandomInteger;
import mcdclab.benchmark.DnfOperatorPicker;
import mcdclab.benchmark.FaaBenchmark;
import mcdclab.benchmark.TCASBenchmarkDNF;
import mcdclab.experiment.ActsTestGenerationExperiment;
import mcdclab.experiment.Apsec99TestGenerationExperiment;
import mcdclab.experiment.CriterionFusionExperiment;
import mcdclab.experiment.CriterionFusionExperimentFactory;
import mcdclab.experiment.HittingSetTestGenerationExperiment;
import mcdclab.experiment.MCDCTestGenerationExperiment;
import mcdclab.experiment.RandomTestGenerationExperiment;
import mcdclab.experiment.Safecomp18TestGenerationExperiment;
import mcdclab.experiment.Stvr06TestGenerationExperiment;
import mcdclab.experiment.TestGenerationExperiment;
import mcdclab.experiment.TestSuiteGenerationFactory;
import mcdclab.macro.ExhaustivePercentageMacro;
import mcdclab.macro.FormulaStats;
import mcdclab.macro.HypergraphStats;
import mcdclab.macro.MinMaxCoverage;
import mcdclab.macro.NumRerunsMacro;
import mcdclab.macro.RatioMacro;
import mcdclab.plot.SpacedHistogram;
import mcdclab.server.AllFormulasCallback;
import mcdclab.server.HologramViewCallback;
import mcdclab.server.LabStats;
import mcdclab.table.CoverageTable;
import mcdclab.table.CriteriaRatioTable;
import mcdclab.table.FilterLines;
import mcdclab.table.FilterLines.FilterCondition;
import mcdclab.table.HittingSetExperimentTable;
import mcdclab.table.HypergraphMultiBinDistribution;
import mcdclab.table.MultiComparisonTable;

import static mcdclab.experiment.CriterionFusionExperiment.CRITERIA;
import static mcdclab.experiment.CriterionFusionExperiment.SIZE_GLOBAL;
import static mcdclab.experiment.CriterionFusionExperiment.SIZE_MERGED;
import static mcdclab.experiment.CriterionFusionExperiment.TIME_GLOBAL;
import static mcdclab.experiment.CriterionFusionExperiment.TIME_MERGED;
import static mcdclab.experiment.FormulaBasedExperiment.FORMULA;
import static mcdclab.experiment.FormulaBasedExperiment.FORMULA_SIZE;
import static mcdclab.experiment.FormulaBasedExperiment.NUM_VARS;
import static mcdclab.experiment.HittingSetTestGenerationExperiment.NUM_EDGES;
import static mcdclab.experiment.HittingSetTestGenerationExperiment.TIME_GENERATION;
import static mcdclab.experiment.HittingSetTestGenerationExperiment.TIME_SOLVING;
import static mcdclab.experiment.TestGenerationExperiment.COVERAGE;
import static mcdclab.experiment.TestGenerationExperiment.CRITERION;
import static mcdclab.experiment.TestGenerationExperiment.METHOD;
import static mcdclab.experiment.TestGenerationExperiment.SIZE;
import static mcdclab.experiment.TestGenerationExperiment.TIME;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * The main laboratory.
 */
public class MyLaboratory extends Laboratory
{
	/** 
	 * By setting this parameter to true, only "small" problem instances
	 * will be added to the lab (fewer variables, etc.). This is used to
	 * debug and test the lab and should be set to false for the final run. 
	 */
	protected boolean m_isSmall = false;
	
	/**
	 * The timeout used to cancel experiments, in milliseconds
	 */
	protected long m_timeout = 300000;

	@Override
	public void setup()
	{
		// Metadata
		setAuthor("ANONYMOUS"); // Put name back on public disclosure
		setTitle("Boolean condition coverage with evaluation trees");

		/* Set to true to include experiments with random test suites. */
		boolean include_random = false;

		/* Set to true to include experiments for MC/DC coverage. */
		boolean include_mcdc = false;

		/* Set to true to include experiments for combinatorial coverage. */
		boolean include_combinatorial = false;

		/* Set to true to include experiments for MUMCUT coverage. */
		boolean include_mumcut = false;

		/* Set to true to include experiments for criteria merging. */
		boolean include_merging = true;

		/* Set to true to include only hypergraph experiments. */
		boolean only_hypergraph = false;

		/* If set to true, sets of experiments that are excluded from the lab
		   will still create the instances of tables, plots and macros
		   associated to these experiments, even though the experiments
		   themselves are not added to the lab. This makes it possible to
		   export artifacts normally (i.e. without creating LaTeX compilation
		   errors) when running the lab on a subset of all experiments. */
		boolean placeholders = false;

		// Read command line arguments
		{
			ArgumentMap c_line = getCliArguments();
			boolean specific = false;
			if (c_line.hasOption("small"))
			{
				m_isSmall = true;
			}
			if (c_line.hasOption("timeout"))
			{
				m_timeout = Integer.parseInt(c_line.getOptionValue("timeout").trim()) * 1000;
			}
			if (c_line.hasOption("mcdc"))
			{
				specific = true;
				include_mcdc = true;
			}
			if (c_line.hasOption("mumcut"))
			{
				specific = true;
				include_mumcut = true;
			}
			if (c_line.hasOption("merging"))
			{
				specific = true;
				include_merging = true;
			}
			if (c_line.hasOption("tway"))
			{
				specific = true;
				include_combinatorial = true;
			}
			if (c_line.hasOption("random"))
			{
				specific = true;
				include_random = true;
			}
			if (!specific)
			{
				include_mcdc = true;
				include_mumcut = true;
				include_merging = true;
				include_combinatorial = true;
				include_random = true;
			}
			if (c_line.hasOption("only-hypergraph"))
			{
				only_hypergraph = true;
				System.out.println("Including only hypergraph experiments");
			}
		}

		// Basic stats about all formulas in the benchmark
		{
			OperatorProvider oprov = new OperatorProvider();
			addFormulas(oprov, m_isSmall);
			add(new FormulaStats(this, oprov));
		}

		// Global tables, macros and plots
		add(new LabStats(this));
		add(new NumRerunsMacro(this));
		add(new HypergraphStats(this));
		HittingSetExperimentTable t_gen_solving = new HittingSetExperimentTable(TIME_GENERATION, TIME_SOLVING);
		t_gen_solving.setTitle("Generation vs. solving time for hypergraph experiments");
		t_gen_solving.setNickname("tGenVsSolvingHypergraph");
		add(t_gen_solving);
		{
			Scatterplot plot = new Scatterplot(t_gen_solving);
			plot.setNickname("pGenVsSolvingHypergraph");
			plot.withLines(false);
			plot.setCaption(Axis.X, "Generation time (ms)");
			plot.setCaption(Axis.Y, "Solving time (ms)");
			plot.setLogscale(Axis.X).setLogscale(Axis.Y);
			plot.setTitle(t_gen_solving.getTitle());
			add(plot);
		}
		HittingSetExperimentTable t_gen_size = new HittingSetExperimentTable(NUM_EDGES, TIME_SOLVING);
		t_gen_size.setTitle("Solving time with respect to hypergraph size");
		t_gen_size.setNickname("tSolvingTimeVsSizeHypergraph");
		add(t_gen_size);
		{
			Scatterplot plot = new Scatterplot(t_gen_solving);
			plot.setNickname("pSolvingTimeVsSizeHypergraph");
			plot.withLines(false);
			plot.setCaption(Axis.X, "Number of hyperedges");
			plot.setCaption(Axis.Y, "Solving time (ms)");
			plot.setLogscale(Axis.X);
			plot.setTitle(t_gen_size.getTitle());
			add(plot);
		}
		HypergraphMultiBinDistribution t_hypergraph_size_distro = new HypergraphMultiBinDistribution(false, 0, 10, 25, 50, 100, 250, 500, 1000, 2500, 5000);
		t_hypergraph_size_distro.setTitle("Hypergraph size distribution");
		t_hypergraph_size_distro.setNickname("tHypergraphSizeDistro");
		add(t_hypergraph_size_distro);
		{
			ClusteredHistogram plot = new ClusteredHistogram(t_hypergraph_size_distro);
			add(plot);
		}

		// Coverage for the random technique on all formulas
		if (placeholders || include_random)
		{
			Group g = new Group("Random test suite generation experiments");
			if (include_random)
			{
				add(g);
			}
			CoverageTable et_coverage_random = new CoverageTable(CRITERION, COVERAGE);
			et_coverage_random.setTitle("Coverage of the random technique on all formulas");
			et_coverage_random.setNickname("tRandomCoverage");
			add(et_coverage_random);
			MinMaxCoverage mmc = new MinMaxCoverage(this, "Random", et_coverage_random);
			add(mmc);

			Region big_r = new Region();
			big_r.add(METHOD, RandomTestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_CLAUSE, TestSuiteGenerationFactory.C_PREDICATE, TestSuiteGenerationFactory.C_MCDC, TestSuiteGenerationFactory.C_2WAY, TestSuiteGenerationFactory.C_3WAY, TestSuiteGenerationFactory.C_MUMCUT);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider, m_isSmall);
			big_r.add(FORMULA, op_provider.getNames());

			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider, only_hypergraph, m_timeout);

			for (Region f_r : big_r.all(METHOD, CRITERION, FORMULA))
			{
				TestGenerationExperiment e = factory.get(f_r, include_random);
				if (e == null)
				{
					continue;
				}
				g.add(e);
				t_gen_solving.add(e);
				t_gen_size.add(e);
				t_hypergraph_size_distro.add(e);
				et_coverage_random.add(e);
			}
		}

		// Comparisons for MC/DC
		if (placeholders || include_mcdc)
		{
			Group g = new Group("Test suite generation experiments (MC/DC coverage)");
			if (include_mcdc)
			{
				add(g);
			}
			RatioMacro rm_size = new RatioMacro(this, "sizeRatioMCDC", SIZE, FORMULA, "Ratio between test suite size of hypergraph vs MCDC");
			RatioMacro rm_size_sat = new RatioMacro(this, "sizeRatioMCDCSAT", SIZE, FORMULA, "Ratio between test suite size of hypergraph vs SAT-based approach");
			RatioMacro rm_time = new RatioMacro(this, "timeRatioMCDC", TIME, FORMULA, "Ratio between test suite time of hypergraph vs MCDC");
			add(rm_size, rm_size_sat, rm_time);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(METHOD, HittingSetTestGenerationExperiment.NAME, MCDCTestGenerationExperiment.NAME, Safecomp18TestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_MCDC);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider, m_isSmall);
			big_r.add(FORMULA, op_provider.getNames());

			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider, only_hypergraph, m_timeout);
			{
				// SAFECOMP'18 results for MC/DC
				Scanner scanner = new Scanner(FileHelper.internalFileToStream(MyLaboratory.class, "/mcdclab/results/safecomp2018.csv"));
				Safecomp18TestGenerationExperiment.addToLab(this, scanner, op_provider);
			}

			for (Region c_r : big_r.all(CRITERION))
			{
				String criterion = c_r.getString(CRITERION);
				ExperimentTable et_time = new ExperimentTable(FORMULA, METHOD, TIME);
				et_time.setShowInList(false);
				TransformedTable tt_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_time);
				tt_time.setTitle("Test generation time " + criterion);
				tt_time.setNickname(LatexNamer.latexify("tGenTime" + criterion));
				ExperimentTable et_size = new ExperimentTable(FORMULA, METHOD, SIZE);
				et_size.setShowInList(false);
				TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(METHOD, SIZE), et_size);
				tt_size.setTitle("Test suite size " + criterion);
				tt_size.setNickname(LatexNamer.latexify("tGenSize" + criterion));
				TransformedTable tt_size_tcas = new TransformedTable(new FilterLines(new OnlyTcas()), tt_size);
				tt_size_tcas.setShowInList(false);
				ClusteredHistogram histo = new ClusteredHistogram(tt_size_tcas);
				histo.setTitle(tt_size.getTitle());
				histo.setNickname(LatexNamer.latexify("pHistoSizeTCAS" + criterion));
				histo.setCaption(Axis.X, "Formula").setCaption(Axis.Y, "Test suite size");
				add(histo);
				SpacedHistogram s_histo = new SpacedHistogram(5, tt_size);
				s_histo.setTitle(tt_size.getTitle());
				s_histo.setNickname(LatexNamer.latexify("pHistoSize" + criterion));
				s_histo.setCaption(Axis.X, "Formula").setCaption(Axis.Y, "Test suite size");
				add(s_histo);
				ExperimentTable et_size_vs_time = new ExperimentTable(SIZE, METHOD, TIME);
				et_size_vs_time.setShowInList(false);
				TransformedTable tt_size_vs_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_size_vs_time);
				tt_size_vs_time.setTitle("Generation time vs. test suite size " + criterion);
				tt_size_vs_time.setNickname(LatexNamer.latexify("ttTimeVsSize" + criterion));
				ExperimentTable et_f_size_vs_time = new ExperimentTable(FORMULA_SIZE, METHOD, TIME);
				et_f_size_vs_time.setShowInList(false);
				TransformedTable tt_f_size_vs_time = new TransformedTable(new ExpandAsColumns(METHOD, TIME), et_f_size_vs_time);
				tt_f_size_vs_time.setTitle("Generation time vs. formula size " + criterion);
				tt_f_size_vs_time.setNickname(LatexNamer.latexify("ttFormulaSizeVsSize" + criterion));
				MultiComparisonTable mt_size = new MultiComparisonTable(FORMULA, METHOD, HittingSetTestGenerationExperiment.NAME, SIZE);
				mt_size.setTitle("Test suite size of hypergraph vs. other methods for " + criterion);
				mt_size.setNickname(LatexNamer.latexify("mtSizeComparison" + criterion));
				for (Region f_r : c_r.all(FORMULA, METHOD))
				{
					TestGenerationExperiment e = factory.get(f_r, include_mcdc);
					if (e == null)
					{
						continue;
					}
					mt_size.add(e);
					if (f_r.getString(METHOD).compareTo(HittingSetTestGenerationExperiment.NAME) == 0)
					{
						rm_size.addToFirstSet(e);
						rm_size_sat.addToFirstSet(e);
						rm_time.addToFirstSet(e);
					}
					if (f_r.getString(METHOD).compareTo(MCDCTestGenerationExperiment.NAME) == 0)
					{
						rm_size.addToSecondSet(e);
						rm_time.addToSecondSet(e);
					}
					if (f_r.getString(METHOD).compareTo(Safecomp18TestGenerationExperiment.NAME) == 0)
					{
						rm_size_sat.addToSecondSet(e);
					}
					g.add(e);
					t_gen_solving.add(e);
					t_hypergraph_size_distro.add(e);
					et_time.add(e);
					et_size.add(e);
					t_gen_size.add(e);
					et_size_vs_time.add(e);
					et_f_size_vs_time.add(e);
				}
				add(tt_time, tt_size, tt_size_vs_time, tt_f_size_vs_time, tt_size_tcas, mt_size);
				Scatterplot p_size_vs_time = new Scatterplot(tt_size_vs_time);
				p_size_vs_time.withLines(false);
				add(p_size_vs_time);				
				Scatterplot p_f_size_vs_time = new Scatterplot(tt_f_size_vs_time);
				p_f_size_vs_time.withLines(false);
				add(p_f_size_vs_time);
				Scatterplot p_mt_size = new Scatterplot(mt_size);
				p_mt_size.setTitle(mt_size.getTitle());
				p_mt_size.setNickname(LatexNamer.latexify("pmtSizeComparison" + criterion));
				p_mt_size.withLines(false);
				add(p_mt_size);
			}
		}

		// Comparison with ACTS on t-way
		if (placeholders || include_combinatorial)
		{
			// The factory to generate experiments
			Group g = new Group("Test suite generation experiments (combinatorial coverage)");
			if (include_combinatorial)
			{
				add(g);
			}
			RatioMacro rm_size = new RatioMacro(this, "sizeRatioACTS", SIZE, FORMULA, "Ratio between test suite size of hypergraph vs ACTS");
			RatioMacro rm_time = new RatioMacro(this, "timeRatioACTS", TIME, FORMULA, "Ratio between test suite time of hypergraph vs ACTS");
			add(rm_size, rm_time);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(METHOD, HittingSetTestGenerationExperiment.NAME, ActsTestGenerationExperiment.NAME, RandomTestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_2WAY, TestSuiteGenerationFactory.C_3WAY);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider, m_isSmall);
			big_r.add(FORMULA, op_provider.getNames());

			ObjectIdentifier<ToolTriplet> identifier = new ObjectIdentifier<ToolTriplet>();

			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider, only_hypergraph, m_timeout);

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
				ClusteredHistogram histo = new ClusteredHistogram(tt_size);
				histo.setTitle(tt_size.getTitle());
				histo.setNickname(LatexNamer.latexify("pHistoSize" + criterion));
				histo.setCaption(Axis.X, "Formula").setCaption(Axis.Y, "Test suite size");
				add(histo);
				MultiComparisonTable mt_size = new MultiComparisonTable(FORMULA, METHOD, HittingSetTestGenerationExperiment.NAME, SIZE);
				mt_size.setTitle("Test suite size of hypergraph vs. other methods for " + criterion);
				mt_size.setNickname(LatexNamer.latexify("mtSizeComparison" + criterion));
				for (Region f_r : c_r.all(FORMULA, METHOD))
				{
					int n = op_provider.getFormula(f_r.getString(FORMULA)).getSize();
					if (identifier.seenBefore(new ToolTriplet(f_r.getString(METHOD), t, n)))
					{
						continue;
					}
					TestGenerationExperiment e = factory.get(f_r, include_combinatorial);
					if (e == null)
					{
						continue;
					}
					mt_size.add(e);
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
					t_gen_solving.add(e);
					t_gen_size.add(e);
					t_hypergraph_size_distro.add(e);
					et_time.add(e);
					et_size.add(e);
					et_size_vs_time.add(e);
				}
				add(tt_time, tt_size, tt_size_vs_time, mt_size);
				Scatterplot p_size_vs_time = new Scatterplot(tt_size_vs_time);
				p_size_vs_time.withLines(false);
				p_size_vs_time.setNickname(LatexNamer.latexify("pSizeVsTimeACTS" + criterion));
				add(p_size_vs_time);
				Scatterplot p_mt_size = new Scatterplot(mt_size);
				p_mt_size.setTitle(mt_size.getTitle());
				p_mt_size.setNickname(LatexNamer.latexify("pmtSizeComparison" + criterion));
				p_mt_size.withLines(false);
				add(p_mt_size);
			}
		}

		// Comparison with MUMCUT
		if (placeholders || include_mumcut)
		{
			Group g = new Group("Test suite generation experiments (MUMCUT coverage)");
			if (include_mumcut)
			{
				add(g);
			}
			RatioMacro rm_size_chen = new RatioMacro(this, "sizeRatioMUMCUT", SIZE, FORMULA, "Ratio between test suite size of hypergraph vs MUMCUT (Chen)");
			RatioMacro rm_size_g_cun = new RatioMacro(this, "sizeRatioMUMCUTStvr", SIZE, FORMULA, "Ratio between test suite size of hypergraph vs MUMCUT (G-CUN)");
			ExhaustivePercentageMacro epm_chen = new ExhaustivePercentageMacro(this, "epMUMCUTChen", "Chen");
			ExhaustivePercentageMacro epm_hypergraph = new ExhaustivePercentageMacro(this, "epMUMCUTHypergraph", "Hypergraph");
			ExhaustivePercentageMacro epm_g_cun = new ExhaustivePercentageMacro(this, "epMUMCUTGCUN", "G-CUN");
			add(rm_size_chen, rm_size_g_cun, epm_chen, epm_g_cun, epm_hypergraph);

			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(METHOD, HittingSetTestGenerationExperiment.NAME, Apsec99TestGenerationExperiment.NAME, Stvr06TestGenerationExperiment.NAME);
			big_r.add(CRITERION, TestSuiteGenerationFactory.C_MUMCUT);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider, m_isSmall);
			big_r.add(FORMULA, op_provider.getNames());
			{
				// APSEC'99 results for MUMCUT
				Scanner scanner = new Scanner(FileHelper.internalFileToStream(MyLaboratory.class, "/mcdclab/results/chen1999.csv"));
				Apsec99TestGenerationExperiment.addToLab(this, scanner, op_provider);
			}
			{
				// STVR'06 results for MUMCUT
				Scanner scanner = new Scanner(FileHelper.internalFileToStream(MyLaboratory.class, "/mcdclab/results/g-cun06.csv"));
				Stvr06TestGenerationExperiment.addToLab(this, scanner, op_provider);
			}
			// The factory to generate experiments
			TestSuiteGenerationFactory factory = new TestSuiteGenerationFactory(this, op_provider, only_hypergraph, m_timeout);
			for (Region c_r : big_r.all(CRITERION))
			{
				String criterion = c_r.getString(CRITERION);
				ExperimentTable et_size = new ExperimentTable(FORMULA, METHOD, SIZE);
				et_size.setShowInList(false);
				TransformedTable tt_size = new TransformedTable(new ExpandAsColumns(METHOD, SIZE), et_size);
				tt_size.setTitle("Test suite size " + criterion);
				tt_size.setNickname(LatexNamer.latexify("ttSize" + criterion));
				add(tt_size);
				ClusteredHistogram histo = new ClusteredHistogram(tt_size);
				histo.setTitle(tt_size.getTitle());
				histo.setNickname("pHistoSize" + criterion);
				histo.setCaption(Axis.X, "Formula").setCaption(Axis.Y, "Test suite size");
				add(histo);
				MultiComparisonTable mt_size = new MultiComparisonTable(FORMULA, METHOD, HittingSetTestGenerationExperiment.NAME, SIZE);
				mt_size.setTitle("Test suite size of hypergraph vs. other methods for " + criterion);
				mt_size.setNickname(LatexNamer.latexify("mtSizeComparison" + criterion));
				add(mt_size);
				for (Region f_r : big_r.all(METHOD, FORMULA))
				{
					if (!f_r.getString(FORMULA).startsWith("TCAS"))
					{
						// Can only compare on TCAS benchmark
						continue;
					}
					TestGenerationExperiment e = factory.get(f_r, include_mumcut);
					if (e == null)
					{
						continue;
					}
					mt_size.add(e);
					if (f_r.getString(METHOD).compareTo(HittingSetTestGenerationExperiment.NAME) == 0)
					{
						rm_size_chen.addToFirstSet(e);
						rm_size_g_cun.addToFirstSet(e);
						epm_hypergraph.add(e);
					}
					if (f_r.getString(METHOD).compareTo(Apsec99TestGenerationExperiment.NAME) == 0)
					{
						rm_size_chen.addToSecondSet(e);
						epm_chen.add(e);
					}
					if (f_r.getString(METHOD).compareTo(Stvr06TestGenerationExperiment.NAME) == 0)
					{
						rm_size_g_cun.addToSecondSet(e);
						epm_g_cun.add(e);
					}
					g.add(e);
					t_gen_solving.add(e);
					t_gen_size.add(e);
					t_hypergraph_size_distro.add(e);
					et_size.add(e);
					//et_coverage_random.add(e);
				}
				Scatterplot p_mt_size = new Scatterplot(mt_size);
				p_mt_size.setTitle(mt_size.getTitle());
				p_mt_size.setNickname(LatexNamer.latexify("pmtSizeComparison" + criterion));
				p_mt_size.withLines(false);
				add(p_mt_size);
			}
		}

		// Comparison merged vs. global
		if (placeholders || include_merging)
		{
			Group g = new Group("Merged vs. global test suite generation");
			if (include_merging)
			{
				add(g);
			}
			// A big region encompassing all the lab's parameters
			Region big_r = new Region();
			big_r.add(CRITERIA, 
					CriterionFusionExperimentFactory.C_MCDC_PREDICATE,
					CriterionFusionExperimentFactory.C_MCDC_MUMCUT,
					CriterionFusionExperimentFactory.C_MCDC_2WAY,
					CriterionFusionExperimentFactory.C_MCDC_3WAY,
					CriterionFusionExperimentFactory.C_MCDC_CLAUSE,
					CriterionFusionExperimentFactory.C_CLAUSE_2WAY,
					CriterionFusionExperimentFactory.C_CLAUSE_3WAY
					);
			OperatorProvider op_provider = new OperatorProvider();
			addFormulas(op_provider, m_isSmall);
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
				et_time.setNickname(LatexNamer.latexify("ttMergedVsGlobalTime" + criteria));
				add(et_size, et_time);
				GlobalVsMergedRatioMacro rm_size = new GlobalVsMergedRatioMacro(this, LatexNamer.latexify("sizeRatioMerged" + criteria), SIZE_GLOBAL, SIZE_MERGED, "Ratio between test suite size of merged vs global for " + criteria);
				GlobalVsMergedRatioMacro rm_time = new GlobalVsMergedRatioMacro(this, LatexNamer.latexify("timeRatioMerged" + criteria), TIME_GLOBAL, TIME_MERGED, "Ratio between generation time of merged vs global for " + criteria);
				add(rm_size, rm_time);
				for (Region f_r : c_r.all(FORMULA))
				{
					CriterionFusionExperiment cfe = factory.get(f_r, include_merging);
					if (cfe == null)
					{
						continue;
					}
					et_size.add(cfe);
					et_time.add(cfe);
					crt_size.add(cfe);
					crt_time.add(cfe);
					rm_size.add(cfe);
					rm_time.add(cfe);
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

	/**
	 * Adds a set of randomly-generated Boolean formulas to the list of
	 * formulas to be considered in the benchmark.
	 * @param provider A provider of randomly-generated formulas
	 * @param small A Boolean flag to generate a "small" benchmark, i.e. with
	 * fewer formulas, each containing fewer variables and clauses than the
	 * "regular" benchmark. Used only for debugging, and not for producing the
	 * final results.
	 */
	protected void addFormulas(OperatorProvider provider, boolean small)
	{
		int num_formulas = 20, random_seed = getRandomSeed();
		{
			DnfOperatorPicker picker = null;
			if (small)
			{
				num_formulas = 10;
				// Smaller random formulas
				RandomInteger ri1 = new RandomInteger(2, 10);
				RandomInteger ri2 = new RandomInteger(2, 15);
				RandomFloat rf = new RandomFloat();
				RandomBoolean rb = new RandomBoolean();
				ri1.setSeed(random_seed + 1);
				ri2.setSeed(random_seed + 2);
				rf.setSeed(random_seed + 3);
				rb.setSeed(random_seed + 4);
				picker = new DnfOperatorPicker(ri1, ri2, rf, rb);
			}
			else
			{
				RandomInteger ri1 = new RandomInteger(2, 14);
				RandomInteger ri2 = new RandomInteger(2, 20);
				RandomFloat rf = new RandomFloat();
				RandomBoolean rb = new RandomBoolean();
				ri1.setSeed(random_seed + 1);
				ri2.setSeed(random_seed + 2);
				rf.setSeed(random_seed + 3);
				rb.setSeed(random_seed + 4);
				picker = new DnfOperatorPicker(ri1, ri2, rf, rb);
			}
			for (int i = 1; i < num_formulas; i++)
			{
				provider.add("Random " + i, picker.pick());
			}
		}
		{
			TCASBenchmarkDNF benchmark = new TCASBenchmarkDNF();
			for (int i = 1; i <= 20; i++) // All TCAS
			{
				Operator op = benchmark.getFormula(i);
				if (op != null && (!small || op.getVariables().size() < 10))
				{
					provider.add("TCAS " + i, op);
				}
			}
		}
		{
			FaaBenchmark benchmark = new FaaBenchmark();
			for (int i = 1; i <= num_formulas; i++) // All FAA
			{
				Operator op = benchmark.getFormula(i);
				if (op != null && (!small || op.getVariables().size() < 10))
				{
					provider.add("FAA " + i, op);
				}
			}
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

	@Override
	public void setupCli(CliParser parser)
	{
		parser.addArgument(new Argument().withLongName("small").withDescription("Run on small instances only"));
		parser.addArgument(new Argument().withLongName("mumcut").withDescription("Run MUMCUT experiments"));
		parser.addArgument(new Argument().withLongName("mcdc").withDescription("Run MC/DC experiments"));
		parser.addArgument(new Argument().withLongName("random").withDescription("Run random experiments"));
		parser.addArgument(new Argument().withLongName("tway").withDescription("Run combinatorial experiments"));
		parser.addArgument(new Argument().withLongName("merging").withDescription("Run criterion merging experiments"));
		parser.addArgument(new Argument().withLongName("only-hypergraph").withDescription("Run only hypergraph experiments"));
		parser.addArgument(new Argument().withLongName("timeout").withDescription("Timeout experiments after x sec").withArgument("x"));
	}

	@Override
	public String isEnvironmentOk()
	{
		return MCDCTestGenerationExperiment.isEnvironmentOk();
	}

	@Override
	public void setupCallbacks(List<WebCallback> callbacks)
	{
		OperatorProvider oprov = new OperatorProvider();
		addFormulas(oprov, m_isSmall);
		callbacks.add(new AllFormulasCallback(this, oprov));
		callbacks.add(new HologramViewCallback(this));
	}

	public static void main(String[] args)
	{
		// Nothing else to do here
		MyLaboratory.initialize(args, MyLaboratory.class);
	}
	
	/**
	 * Condition used to keep only the lines of the TCAS benchmark.
	 */
	protected static class OnlyTcas implements FilterCondition
	{
		@Override
		public boolean include(TableEntry te)
		{
			return te.get(FORMULA).toString().contains("TCAS");
		}
	}
}
