# Calcite Git Adapter

This is a [Calcite](https://calcite.apache.org/) adapter for [Git](https://git-scm.com/) that uses
the [JGit](https://www.eclipse.org/jgit/) library.

> JGit is a lightweight, pure Java library implementing the Git version control system

This is adapter is similar in spirit to [GitCommitsTableFunction](https://github.com/apache/calcite/blob/d9a81b88ad561e7e4cedae93e805e0d7a53a7f1a/plus/src/main/java/org/apache/calcite/adapter/os/GitCommitsTableFunction.java)
which his included in the Calcite repository, however by using JGit, the goal is to be able to expose more powerful
planning optimizations, and simpler code.

## Demo

A helpful `exec:java` is included that will start up [sqlline](https://github.com/julianhyde/sqlline) with the
proper driver loaded pointing to the current directory.

```bash
❯ mvn exec:java
[INFO] Scanning for projects...
[INFO] 
[INFO] -------------------< io.github.fzakaria:calcite-git >-------------------
[INFO] Building calcite-git 1.0-SNAPSHOT
[INFO] --------------------------------[ jar ]---------------------------------
[INFO] 
[INFO] --- exec-maven-plugin:1.6.0:java (default-cli) @ calcite-git ---
15:52:12.237 [sqlline.SqlLine.main()] WARN  i.g.f.calcite.adapter.git.GitDriver - No directory provided, defaulting to /home/fmzakari/code/github.com/fzakaria/calcite-git
Transaction isolation level TRANSACTION_REPEATABLE_READ is not supported. Default (TRANSACTION_NONE) will be used instead.
sqlline version 1.9.0
0: jdbc:git:> select id, author_name, author_date from commits;
+------------------------------------------+---------------+-------------------+
|                    ID                    |  AUTHOR_NAME  |      AUTHOR_DATE  |
+------------------------------------------+---------------+-------------------+
| cbd71f6421597c2e1c1a4dbe098c653fdd11628a | Farid Zakaria | 2021-02-07 23:51: |
| ccd0020a85f2b719ae89d37b5cd059321fbcb98d | Farid Zakaria | 2021-02-07 23:43: |
| 7b3eb02c4f7cb7a91f82726d56add680c7e80d28 | Farid Zakaria | 2021-02-07 23:40: |
| ad6c8068e70f98ae7c41d779b0b1e3cf006788e2 | Farid Zakaria | 2021-02-07 21:12: |
| 504132ce9bf52d7ce5d1d507f6ac8368590b78e5 | Farid Zakaria | 2021-02-07 21:12: |
+------------------------------------------+---------------+-------------------+
5 rows selected (1.24 seconds)
0: jdbc:git:> 
```

If you would like to use it directly in your code with _jdbc_ here is a minimal example.
```java
Properties info = new Properties();
try (Connection connection = DriverManager.getConnection("jdbc:git:", info)) {
    try (Statement statement = connection.createStatement()) {
        final ResultSet resultSet = statement.executeQuery("select * from commits limit 10");
        final List<Commit> commits = allCommitsFromResultSet(resultSet);
        assertThat(commits).isNotEmpty();
    }
}
```

Please consult the tests for additional examples.

