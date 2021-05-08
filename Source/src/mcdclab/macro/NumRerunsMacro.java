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

import ca.uqac.lif.json.JsonNumber;
import ca.uqac.lif.labpal.Laboratory;
import ca.uqac.lif.labpal.macro.MacroScalar;
import mcdclab.experiment.RandomTestGenerationExperiment;

/**
 * Macro that tracks the number of times the random generation algorithm
 * is re-run.
 */
public class NumRerunsMacro extends MacroScalar
{
	/**
	 * Creates a new instance of the macro.
	 * @param lab The lab from which to fetch the values
	 */
	public NumRerunsMacro(Laboratory lab) 
	{
		super(lab, "numreruns", "The number of times the random generation algorithm is re-run");
	}

	@Override
	public JsonNumber getValue()
	{
		return new JsonNumber(RandomTestGenerationExperiment.NUM_RERUNS);
	}
}
