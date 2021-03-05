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

import java.util.Map;

import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.macro.MacroMap;

/**
 * Computes statistics about the Boolean formulas included in the lab.
 */
public class FormulaStats extends MacroMap
{
	/**
	 * A provider that contains all the lab's formulas.
	 */
	protected OperatorProvider m_provider;
	
	/**
	 * Creates a new instance of the macro.
	 * @param lab The lab to which this macro is associated
	 * @param provider A provider that contains all the lab's formulas
	 */
	public FormulaStats(Laboratory lab, OperatorProvider provider)
	{
		super(lab);
		add("maxformuladepth", "The maximum nesting depth of all formulas in the lab");
		add("maxoperators", "The maximum number of operators in all formulas in the lab");
		add("maxvariables", "The maximum number of variables in all formulas in the lab");
		add("numformulas", "The number of distinct Boolean formulas contained in the lab");
		m_provider = provider;
	}

	@Override
	public void computeValues(Map<String, JsonElement> paramMap)
	{
		paramMap.put("maxformuladepth", new JsonNumber(m_provider.getMaxDepth()));
		paramMap.put("maxoperators", new JsonNumber(m_provider.getMaxSize()));
		paramMap.put("maxvariables", new JsonNumber(m_provider.getMaxVariables()));
		paramMap.put("numformulas", new JsonNumber(m_provider.countFormulas()));
	}
}
