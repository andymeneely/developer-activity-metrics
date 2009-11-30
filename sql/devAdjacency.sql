DROP TABLE IF EXISTS NetworkRepoLog;
DROP VIEW IF EXISTS ZUngroupedDevAdjacency;
DROP TABLE IF EXISTS DevAdjacency;

/* Creating the Developer Network Stuff here */
CREATE TABLE NetworkRepoLog AS 
	SELECT filepath, authorname, authordate	FROM repolog;
	
CREATE INDEX NetworkRepoLogAuthor USING BTREE ON NetworkRepoLog(authorname);
CREATE INDEX NetworkRepoLogFile USING BTREE ON NetworkRepoLog(filepath);
OPTIMIZE TABLE NetworkRepoLog;

CREATE VIEW ZUngroupedDevAdjacency AS
	SELECT DISTINCT c1.authorname AS dev1,
					c2.authorname AS dev2,
					c1.filepath AS file1,
					c2.filepath AS file2
		FROM NetworkRepoLog c1,NetworkRepoLog c2
		WHERE c1.filepath=c2.filepath
			AND c1.authorname<c2.authorname
			AND abs(datediff(c1.authordate, c2.authordate)) < 30
			;

CREATE TABLE DevAdjacency AS 
	SELECT dev1, dev2, 
			COUNT(*) AS num, 
			GROUP_CONCAT(DISTINCT file1 ORDER BY file1 ASC SEPARATOR '\n') AS files
	 FROM ZUngroupedDevAdjacency
	GROUP BY dev1,dev2;
	
DROP TABLE IF EXISTS NetworkRepoLog;
DROP VIEW IF EXISTS ZUngroupedDevAdjacency;