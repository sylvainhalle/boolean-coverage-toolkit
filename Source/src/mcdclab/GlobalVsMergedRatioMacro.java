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

import java.util.HashSet;
import java.util.Set;

import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.NumberHelper;
import ca.uqac.lif.labpal.macro.MacroScalar;
import mcdclab.experiment.CriterionFusionExperiment;

public class GlobalVsMergedRatioMacro extends MacroScalar
{
	/**
	 * The first parameter to read in an experiment.
	 */
	protected String m_param1;

	/**
	 * The second parameter to read in an experiment.
	 */
	protected String m_param2;

	/**
	 * A set of experiments to compare.
	 */
	protected Set<CriterionFusionExperiment> m_experiments;

	/**
	 * Creates a new instance of the macro.
	 * @param lab The lab this macro is associated with
	 * @param name The name of the macro
	 * @param param1 The first parameter to read in an experiment
	 * @param param2 The second parameter to read in an experiment
	 * @param description A textual description of the macro
	 */
	public GlobalVsMergedRatioMacro(Laboratory lab, String name, String param1, String param2, String description)
	{
		super(lab, name, description);
		m_param1 = param1;
		m_param2 = param2;
		m_experiments = new HashSet<CriterionFusionExperiment>();
	}

	public GlobalVsMergedRatioMacro add(CriterionFusionExperiment e)
	{
		m_experiments.add(e);
		return this;
	}

	@Override
	public JsonNumber getValue()
	{
		float total = 0, nb = 0;
		for (CriterionFusionExperiment e : m_experiments)
		{
			JsonNumber je1 = (JsonNumber) e.read(m_param1);
			JsonNumber je2 = (JsonNumber) e.read(m_param2);
			if (je1 != null && je2 != null)
			{
				float v1 = ((JsonNumber) e.read(m_param1)).numberValue().floatValue();
				float v2 = ((JsonNumber) e.read(m_param2)).numberValue().floatValue();
				if (v2 != 0)
				{
					float ratio = v1 / v2;
					total += ratio;
					nb++;
				}
			}
		}
		if (nb == 0)
		{
			return new JsonNumber(0);
		}
		return new JsonNumber(NumberHelper.roundToSignificantFigures(total / nb, 3));
	}

}
