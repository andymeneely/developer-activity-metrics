package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.chaoticbits.devactivity.analysis.hmm.IHMMTrainingSymbol;
import org.chaoticbits.devactivity.analysis.hmm.builtin.SimpleHMMTrainingSymbol;

public class TrainingSetParser {

	public List<IHMMTrainingSymbol<ChurnSignal>> parse(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file);
		List<NumDevsState> states = new NumDevsStateParser().readLine(scanner.nextLine());
		List<ChurnSignal> symbols = new ChurnSignalParser().readLine(scanner.nextLine());
		scanner.close();
		return collate(symbols, states);
	}

	private List<IHMMTrainingSymbol<ChurnSignal>> collate(List<ChurnSignal> symbols, List<NumDevsState> states) {
		List<IHMMTrainingSymbol<ChurnSignal>> list = new ArrayList<IHMMTrainingSymbol<ChurnSignal>>();
		for (int i = 0; i < symbols.size(); i++)
			list.add(new SimpleHMMTrainingSymbol<ChurnSignal>(states.get(i), symbols.get(i)));
		return list;
	}

}
