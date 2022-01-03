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
package mcdclab.experiment;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.Atom;
import ca.uqac.lif.mcdc.Conjunction;
import ca.uqac.lif.mcdc.Disjunction;
import ca.uqac.lif.mcdc.Negation;
import ca.uqac.lif.mcdc.Operator;
import ca.uqac.lif.mcdc.Valuation;
import ca.uqac.lif.mtnp.util.FileHelper;

/**
 * Generates test suites for MC/DC coverage by calling the external tool
 * <a href="https://github.com/sylvainhalle/MCDC">MCDC</a>.
 */
public class MCDCTestGenerationExperiment extends TestGenerationExperiment
{
	/**
	 * The name of this tool
	 */
	public static final transient String NAME = "MCDC";

	/**
	 * The name of the external command to be called.
	 */
	protected static final transient String s_appName = "/usr/local/bin/mcdc";

	/**
	 * The regex pattern to parse the contents of the test suite.
	 */
	protected static final transient Pattern s_testPattern = Pattern.compile("\\s+\\d+:\\s+(.*?)\\s+\\(\\d+\\)");

	/**
	 * The regex pattern to extract the value of each variable in a test
	 * suite.
	 */
	protected static final transient Pattern s_valuePattern = Pattern.compile("(.)=(\\d)");

	/**
	 * A flag that checks if the tool used to generate test suites is present at
	 * the expected location
	 */
	protected static boolean s_toolPresent = checkTool();
	
	/**
	 * Empty constructor for deserialization.
	 */
	protected MCDCTestGenerationExperiment() 
	{
		this(null, null);
	}

	public MCDCTestGenerationExperiment(Operator formula, String formula_name)
	{
		super(formula, formula_name);
		setInput(METHOD, NAME);
		setDescription(formula.toString());
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException 
	{
		if (!s_toolPresent)
		{
			// External tool not present; fail
			throw new ExperimentException("External tool could not be executed");
		}
		TimeoutCommandRunner runner = new TimeoutCommandRunner(new String[] {s_appName, "-umdnf", "-s", getExpression(getFormula())});
		runner.setTimeout(getMaxDuration());
		long start = System.currentTimeMillis();
		runner.run();
		long end = System.currentTimeMillis();
		if (runner.getErrorCode() == Integer.MIN_VALUE)
		{
			throw new ExperimentException("Timed out");
		}
		write(TIME, end - start);
		String tool_output = new String(runner.getBytes());
		writeData(tool_output);
	}

	protected void writeData(String tool_output)
	{
		Matcher mat = s_testPattern.matcher(tool_output);
		int num_tests = 0;
		Set<Valuation> suite = new HashSet<Valuation>();
		while (mat.find())
		{
			String line = mat.group(1);
			Matcher l_mat = s_valuePattern.matcher(line);
			Valuation v = new Valuation();
			while (l_mat.matches())
			{
				if (l_mat.group(2).compareTo("0") == 0)
				{
					v.set(l_mat.group(1), false);
				}
				else
				{
					v.set(l_mat.group(1), true);
				}
			}
			suite.add(v);
			num_tests++;
		}
		write(SIZE, num_tests);
	}

	/**
	 * Gets a Boolean formula in the format expected by the external tool.
	 * @param op The formula to print
	 * @return The string corresponding to the formula
	 */
	protected static String getExpression(Operator op)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(baos);
		printExpression(ps, op);
		return baos.toString();
	}

	/**
	 * Prints a Boolean formula in the format expected by the external
	 * tool.
	 * @param ps The print stream into which the formula should be printed
	 * @param op The formula to print
	 */
	protected static void printExpression(PrintStream ps, Operator op)
	{
		if (op instanceof Atom)
		{
			ps.print(((Atom) op).getName());
		}
		if (op instanceof Negation)
		{
			Negation n = (Negation) op;
			Operator operand = n.getOperand();
			if (operand instanceof Atom)
			{
				ps.print("!");
				printExpression(ps, operand);
			}
			else
			{
				ps.print("!(");
				printExpression(ps, operand);
				ps.print(")");
			}
		}
		if (op instanceof Disjunction)
		{
			ps.print("(");
			List<Operator> operands = ((Disjunction) op).getOperands();
			for (int i = 0; i < operands.size(); i++)
			{
				if (i > 0)
				{
					ps.print("+");
				}
				printExpression(ps, operands.get(i));
			}
			ps.print(")");
		}
		if (op instanceof Conjunction)
		{
			ps.print("(");
			List<Operator> operands = ((Conjunction) op).getOperands();
			for (int i = 0; i < operands.size(); i++)
			{
				printExpression(ps, operands.get(i));
			}
			ps.print(")");
		}
	}

	/**
	 * Looks for the presence of the external tool.
	 * @return <tt>true</tt> if the tool has been found, <tt>false</tt>
	 * otherwise
	 */
	protected static boolean checkTool()
	{
		return FileHelper.fileExists(s_appName);
	}

	/**
	 * Produces an error message if the external tool on which this experiment
	 * relies cannot be found.
	 * @return The error message, or <tt>null</tt> if the tool is present
	 */
	public static String isEnvironmentOk()
	{
		if (s_toolPresent)
		{
			return null; // OK
		}
		return "The executable " + s_appName + " is not found in the system. Experiments using this tool will not work.";
	}
}
