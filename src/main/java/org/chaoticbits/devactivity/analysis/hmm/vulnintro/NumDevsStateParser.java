package org.chaoticbits.devactivity.analysis.hmm.vulnintro;

import java.util.ArrayList;
import java.util.List;

public class NumDevsStateParser {

	public List<NumDevsState> readLine(String input) {
		String[] tokens = input.trim().split(" ");
		List<NumDevsState> list = new ArrayList<NumDevsState>(tokens.length);
		for (String token : tokens) {
			VulnerabilityState state = fromChar(token.charAt(0));
			int numDevs = Integer.valueOf(token.substring(1));
			list.add(new NumDevsState(state, numDevs));
		}
		return list;
	}

	private VulnerabilityState fromChar(char charAt) {
		if (charAt == 'v')
			return VulnerabilityState.VULNERABLE;
		else if (charAt == 'n')
			return VulnerabilityState.NEUTRAL;
		else
			throw new IllegalArgumentException("Unrecognized vulnerability state: " + charAt);
	}

}
