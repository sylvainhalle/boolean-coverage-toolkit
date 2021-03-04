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

import java.util.Scanner;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.Operator;

/**
 * Experiment that replays pre-recorded results from an existing publication. 
 */
public class WriteInExperiment extends TestGenerationExperiment
{
	public WriteInExperiment(Operator op, String operator_name, float size, String method, String criterion)
	{
		super(op, operator_name, Status.DONE);
		write(SIZE, size);
		setInput(METHOD, method);
		setInput(CRITERION, criterion);
	}

	@Override
	public void execute() throws ExperimentException, InterruptedException
	{
		// Nothing to do
	}
}
