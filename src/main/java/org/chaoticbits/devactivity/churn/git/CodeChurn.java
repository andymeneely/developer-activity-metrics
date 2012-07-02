package org.chaoticbits.devactivity.churn.git;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.lib.AnyObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;

/**
 * A class that computes code churn using JGit
 * 
 * @author andy
 * 
 */
public class CodeChurn {

	private final Repository repo;
	private final Git git;
	private final LogCommand log;
	private final CollabChurn collabChurn = new CollabChurn();
	private String path = "";

	public CodeChurn(Repository gitRepo) {
		this.repo = gitRepo;
		git = new Git(repo);
		log = git.log();
	}

	public Git git() {
		return git;
	}

	/**
	 * Filter the log command to only look at a specific path. Delegates to: @link LogCommand#addPath(String)
	 * @param path
	 * @return
	 */
	public CodeChurn addPath(String path) {
		log.addPath(path);
		this.path = path;
		return this;
	}

	/**
	 * Filter the log command to start at the given commit. JGit ignores the given commit, and starts after
	 * it. Delegates to: @link LogCommand#add(AnyObjectId)
	 * @param start
	 * @return
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws JGitInternalException
	 */
	public CodeChurn add(AnyObjectId start) throws MissingObjectException, IncorrectObjectTypeException,
			JGitInternalException {
		log.add(start);
		return this;
	}

	/**
	 * Filter the log command to start at the given commit and end at another commit. Delegates to:
	 * @link LogCommand#addRange(AnyObjectId, AnyObjectId)
	 * @param start
	 * @param until
	 * @return
	 * @throws MissingObjectException
	 * @throws IncorrectObjectTypeException
	 * @throws JGitInternalException
	 */
	public CodeChurn addRange(AnyObjectId since, AnyObjectId until) throws MissingObjectException,
			IncorrectObjectTypeException, JGitInternalException {
		log.addRange(since, until);
		return this;
	}

	/**
	 * Executes the call. Creates a list of commits, formats the diffs, and counts the pluses and minuses for
	 * changed lines in the diff
	 * @return
	 * @throws GitAPIException
	 * @throws IOException
	 */
	public ChurnResult call() throws GitAPIException, IOException {
		ChurnResult result = new ChurnResult();
		Iterable<RevCommit> commits = log.call();
		for (RevCommit commit : commits) {
			for (int parentIndex = 0; parentIndex < commit.getParentCount(); parentIndex++) {
				RevCommit parent = commit.getParent(parentIndex);
				ByteArrayOutputStream diff = new ByteArrayOutputStream();
				DiffFormatter formatter = new DiffFormatter(diff);
				formatter.setRepository(repo);
				formatter.setContext(0); // no context around the diffs
				formatter.setPathFilter(PathFilter.create(path)); // filter the same way we did before
				formatter.format(parent, commit); // from parent to current
				Set<String> authorsAffected = new HashSet<String>();
				Scanner scanner = new Scanner(diff.toString());
				while (scanner.hasNextLine()) { // scan the entire diff
					String line = scanner.nextLine();
					System.out.println(line);
					if (line.startsWith("-") && !line.startsWith("---")) {
						incrementLinesAdded(result);
					} else if (line.startsWith("+") && !line.startsWith("+++")) {
						incrementLinesDeleted(result);
					} else if (line.startsWith("@@")) {

						BlameCommand blame = git.blame();
						// blame.setStartCommit(commit);
						blame.setFilePath(path); // the given file path
						blame.setFollowFileRenames(true); // try to follow renames

						collabChurn.compute(line, result, commit, blame, authorsAffected);
					}
				}
				// double-counts across commits
				result.setAuthorsAffected(result.getAuthorsAffected() + authorsAffected.size());
			}
		}
		return result;
	}

	private void incrementLinesAdded(ChurnResult result) {
		result.setLinesAdded(result.getLinesAdded() + 1);
	}

	private void incrementLinesDeleted(ChurnResult result) {
		result.setLinesDeleted(result.getLinesDeleted() + 1);
	}

}
