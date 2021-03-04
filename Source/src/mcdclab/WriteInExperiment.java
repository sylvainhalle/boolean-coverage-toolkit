package mcdclab;

import java.util.Scanner;

import ca.uqac.lif.labpal.ExperimentException;
import ca.uqac.lif.mcdc.Operator;

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

	public static void addToLab(MyLaboratory lab, Scanner scanner, OperatorProvider provider, String method, String criterion)
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
			lab.add(new WriteInExperiment(op, formula_name, size, method, criterion));
		}
	}
}
