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

import java.util.Map;

import ca.uqac.lif.json.JsonElement;
import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.macro.MacroMap;
import ca.uqac.lif.mtnp.table.TableEntry;
import ca.uqac.lif.mtnp.table.TempTable;
import mcdclab.experiment.TestGenerationExperiment;
import mcdclab.table.CoverageTable;

/**
 * Computes the minimum and maximum ratio in the second column of a
 * {@link CoverageTable}, and exports these values as two macros.
 */
public class MinMaxCoverage extends MacroMap
{
	/**
	 * The table from which to fetch the coverage ratios
	 */
	protected transient CoverageTable m_table;
	
	/**
	 * The suffix to append to the macros produced
	 */
	protected transient String m_suffix;
	
	/**
	 * Creates a new instance of the macro.
	 * @param lab The lab this macro is associated with
	 * @param suffix The suffix to append to the macros produced
	 * @param table The table from which to fetch the coverage ratios
	 */
	public MinMaxCoverage(Laboratory lab, String suffix, CoverageTable table)
	{
		super(lab);
		m_suffix = suffix;
		m_table = table;
		add("minCoverage" + suffix, "The minimum coverage ratio obtained on a formula and a given criterion for method " + suffix);
		add("maxCoverage" + suffix, "The maximum coverage ratio obtained on a formula and a given criterion for method " + suffix);
	}

	@Override
	public void computeValues(Map<String, JsonElement> paramMap)
	{
		float min = -1, max = -1;
		TempTable tt = m_table.getDataTable();
		for (TableEntry te : tt.getEntries())
		{
			float c = te.get(TestGenerationExperiment.COVERAGE).numberValue().floatValue();
			if (min < 0 || c < min)
			{
				min = c;
			}
			max = Math.max(max, c);
		}
		paramMap.put("minCoverage" + m_suffix, new JsonNumber(min));
		paramMap.put("maxCoverage" + m_suffix, new JsonNumber(max));
	}

}
