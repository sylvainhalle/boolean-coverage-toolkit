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
package mcdclab.macro;

import java.util.HashMap;
import java.util.Map;

import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Experiment;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.NumberHelper;
import ca.uqac.lif.labpal.macro.MacroScalar;

public class RatioMacro extends MacroScalar
{
	/**
	 * The first set of experiments
	 */
	protected transient Map<String,Experiment> m_firstSet;
	
	/**
	 * The second set of experiments
	 */
	protected transient Map<String,Experiment> m_secondSet;
	
	/**
	 * The parameter to read in each experiment
	 */
	protected transient String m_parameter;
	
	/**
	 * The key used to associate experiments in both sets
	 */
	protected transient String m_key;
	
	/**
	 * Creates a new instance of the macro.
	 * @param lab The lab this macro is associated with
	 * @param name The name of the macro
	 * @param parameter The parameter to read in each experiment
	 * @param key The key used to associate experiments in both sets
	 * @param description A textual description of the macro
	 */
	public RatioMacro(Laboratory lab, String name, String parameter, String key, String description)
	{
		super(lab, name, description);
		m_firstSet = new HashMap<String,Experiment>();
		m_secondSet = new HashMap<String,Experiment>();
		m_parameter = parameter;
		m_key = key;
	}
	
	/**
	 * Adds an experiment to the first set of experiments.
	 * @param e The experiment to add
	 */
	public void addToFirstSet(Experiment e)
	{
		m_firstSet.put(e.readString(m_key), e);
	}
	
	/**
	 * Adds an experiment to the second set of experiments.
	 * @param e The experiment to add
	 */
	public void addToSecondSet(Experiment e)
	{
		m_secondSet.put(e.readString(m_key), e);
	}
	
	@Override
	public JsonNumber getValue()
	{
		float total = 0, n = 0;
		for (Map.Entry<String,Experiment> entry : m_firstSet.entrySet())
		{
			Experiment e1 = entry.getValue();
			Experiment e2 = m_secondSet.get(entry.getKey());
			float v1 = e1.readInt(m_parameter);
			float v2 = e2.readInt(m_parameter);
			if (v2 != 0)
			{
				float ratio = v1 / v2;
				total += ratio;
				n++;
			}
		}
		if (n > 0)
		{
			return new JsonNumber(NumberHelper.roundToSignificantFigures(total / n, 3));
		}
		return new JsonNumber(0);
	}
}
