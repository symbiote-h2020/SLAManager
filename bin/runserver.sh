#
# Usage: $0 [port]
#   port: listening port; defaults to 8200
#
PORT=${1:-8200}
java -jar -Xdebug -agentlib:jdwp=transport=dt_socket,address=9999,server=y,suspend=n sla-service/target/dependency/jetty-runner.jar --port $PORT --path / sla-service/target/sla-service.war
