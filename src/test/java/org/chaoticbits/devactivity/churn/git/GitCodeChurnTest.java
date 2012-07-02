package org.chaoticbits.devactivity.churn.git;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

public class GitCodeChurnTest {

	public static final File testdata = new File("testdata/testgitrepo/.git");
	public static final File blamerepo = new File("testdata/blame-repo/.git");

	@Test
	public void simpleCodeChurn() throws Exception {
		Repository repo = new FileRepositoryBuilder().setGitDir(testdata).readEnvironment().findGitDir().build();
		ChurnResult result = new CodeChurn(repo).addPath("mancala/player/GreedyPlayer.java").call();
		assertEquals("total code churn for the file", 4, result.getTotalChurn());
	}

	@Test
	public void startCommit() throws Exception {
		Repository repo = new FileRepositoryBuilder().setGitDir(testdata).readEnvironment().findGitDir().build();
		ChurnResult result = new CodeChurn(repo).addPath("mancala/player/GreedyPlayer.java")
				.add(repo.resolve("04c7a5689d91ee33ff37efff01ad6fc3e80229e4")).call();
		assertEquals("total code churn for the file", 2, result.getTotalChurn());
	}

	@Test
	public void simpleSelfChurn() throws Exception {
		Repository repo = new FileRepositoryBuilder().setGitDir(blamerepo).readEnvironment().findGitDir().build();
		ChurnResult result = new CodeChurn(repo).addPath("a.txt").call();
		assertEquals("total churn for the file", 9, result.getTotalChurn());
		assertEquals("self churn for the file over two revisions ", 1, result.getSelfChurn());
		assertEquals("non-self churn for the file over two revisions ", 1, result.getNonSelfChurn());
		assertEquals("non-self churn + self churn = lines-deleted", 2, result.getLinesDeleted());
	}
}
