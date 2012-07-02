package org.chaoticbits.devactivity.churn.git;

import java.util.Set;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.revwalk.RevCommit;

public class CollabChurn {

	/**
	 * Given a diff hunk line and a commit, update the self and non-self churn
	 * 
	 * For example:
	 * @@ -a,b +c,d @@
	 * @@ -a +c,3 @@ when b is 1
	 * 
	 *    Note that this can be somewhat slow as git blame is run every time this is called
	 * 
	 * @param line
	 * @param result
	 * @param commit
	 * @param blame
	 * @throws GitAPIException
	 */
	public void compute(String line, ChurnResult result, RevCommit commit, BlameCommand blame,
			Set<String> authorsAffected) throws GitAPIException {
		// @@ -a,b +c,d @@
		// @@ -a +c,3 @@ when b is 1
		String[] split = line.split(" ");
		String[] split2 = split[1].split(",");
		int numLines = 1;
		if (split2.length > 1) {
			numLines = Integer.valueOf(split2[1]);
		}
		int startLine = Integer.valueOf(split2[0]) * -1;
		System.out.println("Starting line: " + startLine);
		System.out.println("Num lines: " + numLines);
		// TODO Set the revision range on the blame call
		// FIXME JGit's blame call seems to be broken...
		// BlameResult blameResult = blame.call();
		// System.out.println("Total number of lines: " + blameResult.getResultContents().size());
		// for (int lineNum = startLine; lineNum < startLine + numLines; lineNum++) {
		// String lineAuthorEmail = blameResult.getSourceAuthor(lineNum).getEmailAddress();
		// if (lineAuthorEmail.equals(commit.getAuthorIdent().getEmailAddress())) {
		// incrementSelfChurn(result);
		// } else {
		// incrementNonSelfChurn(result);
		// authorsAffected.add(lineAuthorEmail);
		// }
		// }
	}

	private void incrementSelfChurn(ChurnResult result) {
		result.setSelfLinesAdded(result.getSelfLinesAdded() + 1);
	}

	private void incrementNonSelfChurn(ChurnResult result) {
		result.setNonSelfLinesAdded(result.getNonSelfLinesAdded() + 1);
	}
}
