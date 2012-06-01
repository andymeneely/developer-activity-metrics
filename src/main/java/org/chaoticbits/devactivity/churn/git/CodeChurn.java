package org.chaoticbits.devactivity.churn.git;

import java.io.IOException;

import org.eclipse.jgit.api.DiffCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

public class CodeChurn {

	private final Repository repo;

	public CodeChurn(Repository gitRepo) {
		this.repo = gitRepo;
	}

	public int compute(String path) {
		Git git = new Git(repo);
		BlameResult call = git.blame().setFilePath(path).setFollowFileRenames(true).call();
		
		try {
			Iterable<RevCommit> commits = git.log().addPath(path).call();
			
			for (RevCommit revCommit : commits) {
				String fullMessage = revCommit.getFullMessage();
				DiffCommand diff = git.diff();
				AbstractTreeIterator newTree = new CanonicalTreeParser();
				
				AbstractTreeIterator oldTree = new CanonicalTreeParser();
				diff.setNewTree(newTree).setOldTree(oldTree).call();
			}
		} catch (NoHeadException e) {
			e.printStackTrace();
		} catch (JGitInternalException e) {
			e.printStackTrace();
		} catch (GitAPIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		throw new IllegalStateException("unimplemented!");
	}

}
