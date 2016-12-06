#!/bin/bash
#!/bin/bash
# Author: Rodrigo Kuroda <rodrigokuroda@alunos.utfpr.edu.br>
export MYSQL_URL="jdbc:mysql://${MYSQL_PORT_3306_TCP_ADDR}:${MYSQL_PORT_3306_TCP_PORT}/?autoReconnect=true&verifyServerCertificate=false&useSSL=false&requireSSL=false"
java -Xmx1024m -Xms256m -Xss256k -jar /opt/recominer/recominer-web-0.7.jar --database.url=${MYSQL_URL}
