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

/**
 * An experiment that uses the corresponding
 * {@link HittingSetTestGenerationExperiment} as a reference to measure
 * coverage ratio. Running thus experiment causes first the execution
 * of that experiment. This avoids solving the same hitting set problem
 * multiple times.
 */
public abstract class DependentTestGenerationExperiment extends TestGenerationExperiment
{
	protected transient HittingSetTestGenerationExperiment m_reference;
	
	public DependentTestGenerationExperiment(HittingSetTestGenerationExperiment reference)
	{
		super(reference.m_formula, reference.readString(FORMULA));
		m_reference = reference;
	}
}
