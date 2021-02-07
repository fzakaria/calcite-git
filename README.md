# Calcite Git Adapter

This is a [Calcite](https://calcite.apache.org/) adapter for [Git](https://git-scm.com/) that uses
the [JGit](https://www.eclipse.org/jgit/) library.

> JGit is an EDL (new-style BSD) licensed, lightweight, pure Java library implementing the Git version control system

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
15:36:57.853 [sqlline.SqlLine.main()] WARN  i.g.f.calcite.adapter.git.GitDriver - No directory provided, defaulting to /home/fmzakari/code/github.com/fzakaria/calcite-git
Transaction isolation level TRANSACTION_REPEATABLE_READ is not supported. Default (TRANSACTION_NONE) will be used instead.
sqlline version 1.9.0
0: jdbc:git:> select * from commits;
.
..
...
+------------------------------------------+------------------+-------------------+--------------+
|                    id                    |     message      |      summary      |  author_name |
+------------------------------------------+------------------+-------------------+--------------+
| ad6c8068e70f98ae7c41d779b0b1e3cf006788e2 | Initial commit   | Initial commit
   | Farid Zakari |
| 504132ce9bf52d7ce5d1d507f6ac8368590b78e5 | Added git ignore | Added git ignore
 | Farid Zakari |
+------------------------------------------+------------------+-------------------+--------------+
2 rows selected (1.093 seconds)
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