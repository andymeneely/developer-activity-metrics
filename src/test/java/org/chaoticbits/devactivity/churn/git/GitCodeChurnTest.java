package org.chaoticbits.devactivity.churn.git;

import static org.junit.Assert.*;

import java.io.File;

import org.chaoticbits.devactivity.churn.git.CodeChurn;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.junit.Test;

public class GitCodeChurnTest {

	public static final File repoDir = new File("testdata/testgitrepo/.git");

	@Test
	public void simpleCodeChurn() throws Exception {
		Repository gitRepo = new FileRepositoryBuilder().setGitDir(repoDir)
				.readEnvironment().findGitDir().build();
		int churn = new CodeChurn(gitRepo)
				.compute("mancala/player/GreedyPlayer.java");
		assertEquals(4, churn);
	}
}
