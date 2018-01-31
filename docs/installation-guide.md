# SLA Core Installation Guide #

* [Requirements](#requirements)
* [Installation](#installation)
	* [Download the project](#download)
	* [Creating the mysql database](#database)
	* [Importing the code into eclipse](#importeclipse)
* [Configuration](#configuration)
* [Running](#running)
* [Testing](#testing)
* [Additional configuration](#advancedconfig)

## <a name="requirements"> Requirements </a> ##

The requirements to install a working copy of the sla core are:

* Oracle JDK >=1.7
* Database to install the database schema for the service: Mysql>=5.0
* Maven >= 3.0

## <a name="installation"> Installation </a> ##

All commands shown here are ready to be executed from the 
root directory of the project (i.e., the one with the 
_configuration.properties_ file) 

###1. <a name="download"> Download the project </a> ###

Download the project using a git client: `git clone <url>`

###2. <a name="database"> Creating the mysql database </a> ###

From mysql command tool, create a database (with a user with sufficient 
privileges, as root):

	$ mysql -p -u root 
	
	mysql> CREATE DATABASE atossla;

Create a user:

	mysql> CREATE USER atossla@localhost IDENTIFIED BY '_atossla_';
	mysql> GRANT ALL PRIVILEGES ON atossla.* TO atossla@localhost; -- * optional WITH GRANT OPTION;

Create the database schema executing the following script (this runs the sql file in sla-repository/src/main/resources/atossla.sql):

	$ bin/restoreDatabase.sh

The names used here are the default values of the sla core. See 
[configuration](#configuration) to know how to change the values.

###3. <a name="importeclipse"> Importing the code into eclipse </a> ###

The core of the ATOSSLA has been developed using the Eclipse Java IDE, 
although others Java editors could be used, here we only provide the 
instructions about how to import the code into Eclipse.

The first step is to tell Maven to create the necessary Eclipse project 
files executing this:

	$ mvn eclipse:eclipse

The previous command is going to generate the eclipse project files: 
.settings, .classpath, and .project. Again, please never upload those 
files to the svn, it is going to deconfigure the eclipse of other 
developers (it is easy to fix, just an annoying waste of time).

After it, from your eclipse you can import the project. Go to 
"import project from file", go to the trunk folder, and you should 
see the "ATOSSLA" project ready to be imported in your Eclipse. 

## <a name="configuration"> Configuration </a> ##

The parameters on the SLA Framework can be classified into:
 
* compile-time values: these specify static behaviour of the SLA core 
  (e.g. the class of the Monitoring adapter)
* runtime values: these specify runtime configuration and its value should be set 
  with an env var (e.g. the address of a KairosDB instance)

All compile-time configuration is done through the Spring context files, which build
all the configurable objects. Several of these objects declared in the context files 
takes their parameters from the file _configuration.properties_. 

Several parameters (of both types) can be configured through this file.

1. db.\* allows to configure the database username, password and name in case it has been changed from the proposed 
   one in the section [Creating the mysql database](#database). It can be selected if queries from hibernate must be 
   shown or not. These parameters can be overriden at runtime time through the use of environment variables 
   (see section [Running](#running)),
1. enforcement.\* several parameters from the enforcement can be customized,
1. service.basicsecurity.\* basic security is enabled
   These parameters can be used to set the user name and password to access to the rest services.
1. converter.\* values specify how WSAG documents are parsed and extended.

The file _configuration.properties_ initially contains default values for the project.
For runtime parameters, the values in the file are intended to be used ONLY in development, and
they should be overriden at deployment time using env vars.

Additional implementations may require the modification of the context files. In that case, it is 
recommended to modify `sla-personalization/src/main/resources/personalization-context.xml`.

Check the section [Additional configuration](#advancedconfig) for specific configuration.

## <a name="compiling"> Compiling </a> ##
	
	$ mvn install
	
If you want to skip tests (do not skip tests the first time):
	
	$ mvn install -Dmaven.test.skip=true
	
The result of the command is a war in _sla-service/target_.

## <a name="running"> Running </a> ##

runserver.sh script runs the sla-core server using jetty runner on port 8080 and / as context path.

	$ bin/runserver.sh
	
Some configuration parameters can be overriden using environment variables or jdk variables. The list of
overridable parameters is:

* `DB_DRIVER`; default value is `com.mysql.jdbc.Driver`
* `DB_URL`; default value is `jdbc:mysql://${db.host}:${db.port}/${db.name}`
* `DB_USERNAME`; default value is `${db.username}`
* `DB_PASSWORD`; default value is `${db.password}`
* `DB_SHOWSQL`; default value is `${db.showSQL}`

F.e., to use a different database configuration:

	$ export DB_URL=jdbc:mysql://localhost:3306/sla
	$ export DB_USERNAME=sla
	$ export DB_PASSWORD=<secret>
	$ bin/runserver.sh 

### <a name="logging"> Logging </a> ###

By default, sla-core uses slf4j for logging, using log4j as backend.
The default `log4.properties` file logs to stdout, and is stored in sla-service in 
`sla-service/src/main/resources`.

If you want to use another log4j configuration, you can pass a different properties file to the JRE using 
`-Dlog4j.configuration=file:{file path}`.


## <a name="testing"> Testing </a> ##

Check that everything is working:

	$ curl http://localhost:8080/api/providers

Time to check the User Guide and the Developer Guide!

## <a name="advancedconfig"> Additional configuration </a> ##

### RestNotifier ###

RestNotifier needs additional parameters to what is provided by default in `configuration.properties`.
For this reason, it is recommended to modify 
`sla-personalization/src/main/resources/personalization-context.xml`
to add the construction of the RestNotifier. The following excerpt will create the RestNotifier taking
`ACCOUNTING_URL`, `ACCOUNTING_URL_USER` and `ACCOUNTING_URL_PASSWORD` as parameters.

    <bean id="notificationManager"  class="eu.atos.sla.notification.NotifierManager" primary="true">
        <property name="agreementEnforcementNotifier">
            <bean class="eu.atos.sla.notification.RestNotifier">
                <constructor-arg value="${ACCOUNTING_URL}" />
                <constructor-arg value="application/json" />
                <constructor-arg value="${ACCOUNTING_URL_USER}" />
                <constructor-arg value="${ACCOUNTING_URL_PASSWORD}" />
            </bean>
         </property>
    </bean>


The parameters are defined in `sla-personalization/src/main/resources/sla-env.properties`
as empty by default, so use env vars to set them.

F.e.:

    $ export ACCOUNTING_URL=http://localhost:9000
    $ bin/runserver.sh
    