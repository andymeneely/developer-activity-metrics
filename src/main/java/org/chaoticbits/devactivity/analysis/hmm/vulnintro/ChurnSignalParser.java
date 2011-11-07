package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import java.util.ArrayList;
import java.util.List;

public class ChurnSignalParser {

	public List<ChurnSignal> readLine(String input) {
		List<ChurnSignal> list = new ArrayList<ChurnSignal>();
		String[] tokens = input.split(" ");
		for (String token : tokens) {
			list.add(ChurnSignal.parse(token));
		}
		return list;
	}

}
