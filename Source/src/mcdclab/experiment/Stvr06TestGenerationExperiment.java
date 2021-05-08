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

import java.util.Scanner;

import ca.uqac.lif.mcdc.Operator;
import mcdclab.MyLaboratory;
import mcdclab.OperatorProvider;

/**
 * Experiment whose results are taken from Chen et al., APSEC 1999.
 */
public class Stvr06TestGenerationExperiment extends WriteInExperiment
{
	/**
	 * The name of this "tool"
	 */
	public static final transient String NAME = "G-CUN";
	
	public Stvr06TestGenerationExperiment(Operator op, String operator_name, float size)
	{
		super(op, operator_name, size, NAME, TestSuiteGenerationFactory.C_MUMCUT);
	}
	
	public static void addToLab(MyLaboratory lab, Scanner scanner, OperatorProvider provider)
	{
		while (scanner.hasNextLine())
		{
			String line = scanner.nextLine().trim();
			if (line.isEmpty() || line.startsWith("#"))
			{
				continue;
			}
			String[] parts = line.split("\\t+");
			String formula_name = parts[0].trim();
			float size = Float.parseFloat(parts[1].trim());
			Operator op = provider.getFormula(formula_name);
			if (op == null)
			{
				continue;
			}
			lab.add(new Stvr06TestGenerationExperiment(op, formula_name, size));
		}
	}
}
