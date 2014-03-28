Table Task
+-----------+-------------+------+-----+---------+-------+
| Field     | Type        | Null | Key | Default | Extra |
+-----------+-------------+------+-----+---------+-------+
| task_id   | bigint(20)  | NO   | PRI | 0       |       |
| type      | varchar(25) | YES  |     | NULL    |       |
| input     | text        | YES  |     | NULL    |       |
| output    | text        | YES  |     | NULL    |       |
| timetaken | bigint(20)  | YES  |     | NULL    |       |
+-----------+-------------+------+-----+---------+-------+

Table Event
+------------------+-------------+------+-----+---------+-------+
| Field            | Type        | Null | Key | Default | Extra |
+------------------+-------------+------+-----+---------+-------+
| task_id          | bigint(20)  | NO   |     | NULL    |       |
| status           | varchar(50) | YES  |     | NULL    |       |
| input            | text        | YES  |     | NULL    |       |
| output           | text        | YES  |     | NULL    |       |
| timestamp        | bigint(20)  | NO   | PRI | NULL    |       |
| ip               | varchar(16) | NO   | PRI | NULL    |       |
| percentageMemory | tinyint(4)  | YES  |     | NULL    |       |
| cpuUsage         | tinyint(4)  | YES  |     | NULL    |       |
+------------------+-------------+------+-----+---------+-------+

Step 1: Create mysql tables as above
Step 2: Install node in site directory
Step 3: Run compile.sh
Step 4: Run runMan.sh
Step 5: Execute createClient.sh in multiple terminals (locally) or on multiple clients (networked)
Step 6: Use "node app.js" to start the web app
Step 7: Connect with localhost:3000 
