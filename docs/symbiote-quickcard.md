# SymbIoTe Quick Card to run the SLA Manager #

## <a name="requirements"> Requirements </a> ##

The requirements to install a working copy of the sla core are:

* Oracle JDK >=1.7
* Database to install the database schema for the service: Mysql>=5.0
* Maven >= 3.0
* RabbitMQ
* SymbIoTe Monitoring component

## <a name="database"> Creating the mysql database </a> ##

From mysql command tool, create a database (with a user with sufficient 
privileges, as root):

	$ mysql -p -u root 
	
	mysql> CREATE DATABASE symbiote;

Create a user:

	mysql> CREATE USER symbiote@localhost IDENTIFIED BY '_symbiote_';
	mysql> GRANT ALL PRIVILEGES ON symbiote.* TO symbiote@localhost; -- * optional WITH GRANT OPTION;

Create the database schema executing the following script (this runs the sql file in sla-repository/src/main/resources/atossla.sql):

	$ bin/restoreDatabase.sh

The names used here are the default values of the sla core. See 
[configuration](#configuration) to know how to change the values.

## <a name="installation"> Installation </a> ##

1. Get the SLA Manager code
`git clone https://github.com/symbiote-h2020/SLAManager.git`
1. Enter SLAManager folder and provide default values for the `configuration.properties` file according to the [configuration](#configuration) section.
1. Build the WAR file with `mvn clean install` command
1. _(Optional but recommended)_ Personalize the runtime environment by setting environment variables described in [personalization](#personalization) section.
1. Deploy the WAR file to an Application or Servlet container. Alternatively, run it in an embedded Jetty instance with the command `bin/runserver.sh`

## <a name="configuration"> Configuration </a> ##

The file `configuration.properties` provides default values at build time. This are the recommended properties that might need to be changed:

* `db.host`: Hostname or IP of a running MySQL instance.
* `db.username`: Username for the aforementioned host.
* `db.password`: Password for the username above.
* `db.name`: Name of the database to hold SLA information. The provided username has to have granted all privileges on it.
* `db.port`: Port of the running MySQL instance
* `db.showSQL`: For debugging purposes. If true, it will log the SQL queries executed.

* `rabbit.host`: Hostame of an accessible RabbitMQ instance.
* `rabbit.username`: User to use in this host.
* `rabbit.password`: Password for the user.

* `platform.id`: ID of the current platform.
* `symbiote.monitoring.url`: URL to the SymbIoTe monitoring component.

## <a name="personalization"> Personalization </a> ##

Some configuration parameters can be overriden using environment variables or jdk variables. The list of
overridable parameters is:

**Mysql variables**

* `DB_DRIVER`: JDBC driver to use. Default value is `com.mysql.jdbc.Driver`
* `DB_URL`: URL to a SQL database (MySQL preferred). Default value is `jdbc:mysql://${db.host}:${db.port}/${db.name}`
* `DB_USERNAME`: Username for the aforementioned database. Default value is `${db.username}`
* `DB_PASSWORD`: Password for the username above. Default value is `${db.password}`
* `DB_SHOWSQL`: Log the SQL queries executed by the SLA manager. For debug purposes only. Default value is `${db.showSQL}`

**RabbitMQ variables**

* `RABBIT_HOST`: Hostame of an accessible RabbitMQ instance. Default value is `${rabbit.host}`
* `RABBIT_USERNAME`: User to use in this host. Default value is `${rabbit.username}`
* `RABBIT_PASSWORD`: Password for the user. Default value is `${rabbit.password}`

**SymbIoTe specific variables**

* `PLATFORM_ID`: ID of the current platform. Default value is `${platform.id}`
* `SYMBIOTE_MONTORING_URL`: URL to the SymbIoTe monitoring component. Default value is `${symbiote.monitoring.url}`

For default values, `${exp}` means `exp` value in the `configuration.properties` file at compilation time.

F.e., to use a different database configuration:

	$ export DB_URL=jdbc:mysql://localhost:3306/sla
	$ export DB_USERNAME=sla
	$ export DB_PASSWORD=<secret>
	$ bin/runserver.sh 