#Appendix REST API examples#

##Providers<a name="providers"></a>##

###Create a provider###

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/providers

	POST /api/providers HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 117
	<?xml version="1.0" encoding="UTF-8"?><provider>    <uuid>provid
	er01</uuid>    <name>provider01name</name></provider>

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:06:51 GMT
	Location: http://localhost:8080/api/providers/provider01
	Content-Type: application/xml
	Content-Length: 246
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The provider has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/api/providers/provider01" elementId="provider01"/>

---

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider02.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/providers

	POST /api/providers HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 117
	<?xml version="1.0" encoding="UTF-8"?><provider>    <uuid>provid
	er02</uuid>    <name>provider02name</name></provider>

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:06:51 GMT
	Location: http://localhost:8080/api/providers/provider02
	Content-Type: application/xml
	Content-Length: 246
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The provider has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/api/providers/provider02" elementId="provider02"/>

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider03.json -X POST -H Content-type:application/json -H Accept:application/json http://localhost:8080/api/providers

	POST /api/providers HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/json
	Accept:application/json
	Content-Length: 45
	{"uuid":"provider03","name":"provider03name"}

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:06:52 GMT
	Location: http://localhost:8080/api/providers/provider03
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	BA
	{"code":201,"message":"The provider has been stored successfully
	 in the SLA Repository Database. It has location http://localhos
	t:8080/api/providers/provider03","elementId":"provider03"}

---

Provider exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/provider02.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/providers

	POST /api/providers HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 117
	<?xml version="1.0" encoding="UTF-8"?><provider>    <uuid>provid
	er02</uuid>    <name>provider02name</name></provider>

	HTTP/1.1 409 Conflict
	Date: Wed, 07 Sep 2016 11:06:53 GMT
	Content-Type: application/xml
	Content-Length: 181
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Provider with id:provider02 or name:provider02
	name already exists in the SLA Repository Database"/>

###Get a provider###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/providers/provider02?

	GET /api/providers/provider02? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:06:54 GMT
	Content-Type: application/xml
	Content-Length: 126
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><provider
	><uuid>provider02</uuid><name>provider02name</name></provider>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/providers/provider02?

	GET /api/providers/provider02? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:06:55 GMT
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	2D
	{"uuid":"provider02","name":"provider02name"}

---

Provider not exists.
Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/providers/notexists?

	GET /api/providers/notexists? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Date: Wed, 07 Sep 2016 11:06:56 GMT
	Content-Type: application/xml
	Content-Length: 156
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="404" message="There is no provider with uuid notexists in th
	e SLA Repository Database"/>

###Get all the providers###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/providers?

	GET /api/providers? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:06:57 GMT
	Content-Type: application/xml
	Content-Length: 291
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><provider
	s><provider><uuid>provider01</uuid><name>provider01name</name></
	provider><provider><uuid>provider02</uuid><name>provider02name</
	name></provider><provider><uuid>provider03</uuid><name>provider0
	3name</name></provider></providers>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/providers?

	GET /api/providers? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:06:58 GMT
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	8B
	[{"uuid":"provider01","name":"provider01name"},{"uuid":"provider
	02","name":"provider02name"},{"uuid":"provider03","name":"provid
	er03name"}]

###Delete a provider###



	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/api/providers/provider03

	DELETE /api/providers/provider03 HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:06:59 GMT
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	3A
	The provider with uuid provider03 was deleted successfully

---

Provider not exists


	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/api/providers/notexists

	DELETE /api/providers/notexists HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Date: Wed, 07 Sep 2016 11:06:59 GMT
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	9E
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<error c
	ode="404" message="There is no provider with uuid notexists in t
	he SLA Repository Database"/>.

##Templates<a name="templates"></a>##

###Create a template###

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/templates

	POST /api/templates HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2531

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:07:00 GMT
	Location: http://localhost:8080/api/templates/template01
	Content-Type: application/xml
	Content-Length: 246
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The template has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/api/templates/template01" elementId="template01"/>

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/template02.json -X POST -H Content-type:application/json -H Accept:application/json http://localhost:8080/api/templates

	POST /api/templates HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/json
	Accept:application/json
	Content-Length: 721
	{"templateId":"template02","context":{"agreementInitiator":"prov
	ider02","agreementResponder":null,"serviceProvider":"AgreementIn
	itiator","templateId":"template02","service":"service02","expira
	tionTime":"2014-03-07T12:00:00+01:00"},"name":"ExampleTemplate",
	"terms":{"allTerms":{"serviceDescriptionTerm":{"name":null,"serv
	iceName":null},"serviceProperties":[{"name":null,"serviceName":n
	ull,"variableSet":null},{"name":null,"serviceName":null,"variabl
	eSet":null}],"guaranteeTerms":[{"name":"FastReaction","serviceSc
	ope":{"serviceName":"GPS0001","value":"               http://www
	.gps.com/coordsservice/getcoords            "},"serviceLevelObje
	tive":{"kpitarget":{"kpiName":"FastResponseTime","customServiceL
	evel":null}}}]}}}

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:07:01 GMT
	Location: http://localhost:8080/api/templates/template02
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	BA
	{"code":201,"message":"The template has been stored successfully
	 in the SLA Repository Database. It has location http://localhos
	t:8080/api/templates/template02","elementId":"template02"}

---

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template02b.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/templates

	POST /api/templates HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2533

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:07:03 GMT
	Location: http://localhost:8080/api/templates/template02b
	Content-Type: application/xml
	Content-Length: 248
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The template has been stored successfully in
	 the SLA Repository Database. It has location http://localhost:8
	080/api/templates/template02b" elementId="template02b"/>

---

Template exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/templates

	POST /api/templates HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2531

	HTTP/1.1 409 Conflict
	Date: Wed, 07 Sep 2016 11:07:03 GMT
	Content-Type: application/xml
	Content-Length: 157
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Element with id:template01 already exists in t
	he SLA Repository Database"/>

---

Linked provider not exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/template03.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/templates

	POST /api/templates HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2531

	HTTP/1.1 409 Conflict
	Date: Wed, 07 Sep 2016 11:07:04 GMT
	Content-Type: application/xml
	Content-Length: 144
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Provider with UUID provider03 doesn't exist in
	 the database"/>

###Get a template###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/templates/template02?

	GET /api/templates/template02? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:04 GMT
	Content-Type: application/xml
	Content-Length: 1278
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<wsag:Te
	mplate wsag:TemplateId="template02" xmlns:wsag="http://www.ggf.o
	rg/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">.    
	<wsag:Name>ExampleTemplate</wsag:Name>.    <wsag:Context>.      
	  <wsag:AgreementInitiator>provider02</wsag:AgreementInitiator>.
	        <wsag:ServiceProvider>AgreementInitiator</wsag:ServicePr
	ovider>.        <wsag:ExpirationTime>2014-03-07T12:00:00+01:00</
	wsag:ExpirationTime>.        <wsag:TemplateId>template02</wsag:T
	emplateId>.        <sla:Service>service02</sla:Service>.    </ws
	ag:Context>.    <wsag:Terms>.        <wsag:All>.            <wsa
	g:ServiceDescriptionTerm/>.            <wsag:ServiceProperties/>
	.            <wsag:ServiceProperties/>.            <wsag:Guarant
	eeTerm wsag:Name="FastReaction">.                <wsag:ServiceSc
	ope wsag:ServiceName="GPS0001">               http://www.gps.com
	/coordsservice/getcoords            </wsag:ServiceScope>.       
	         <wsag:ServiceLevelObjective>.                    <wsag:
	KPITarget>.                        <wsag:KPIName>FastResponseTim
	e</wsag:KPIName>.                    </wsag:KPITarget>.         
	       </wsag:ServiceLevelObjective>.            </wsag:Guarante
	eTerm>.        </wsag:All>.    </wsag:Terms>.</wsag:Template>.

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/templates/template02?

	GET /api/templates/template02? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:05 GMT
	Content-Type: application/json
	Content-Length: 886
	Server: Jetty(9.2.10.v20150310)
	{"templateId":"template02","name":"ExampleTemplate","context":{"
	agreementInitiator":"provider02","agreementResponder":null,"serv
	iceProvider":"AgreementInitiator","expirationTime":"2014-03-07T1
	2:00:00+01:00","templateId":"template02","service":"service02","
	any":[]},"terms":{"allTerms":{"serviceDescriptionTerm":{"name":n
	ull,"serviceName":null,"any":[]},"serviceProperties":[{"name":nu
	ll,"serviceName":null,"variableSet":null},{"name":null,"serviceN
	ame":null,"variableSet":null}],"guaranteeTerms":[{"name":"FastRe
	action","serviceScope":{"serviceName":"GPS0001","value":"       
	        http://www.gps.com/coordsservice/getcoords            "}
	,"serviceLevelObjective":{"kpitarget":{"kpiName":"FastResponseTi
	me","customServiceLevel":null}},"qualifyingCondition":null,"busi
	nessValueList":null,"serviceLevelObjetive":{"kpitarget":{"kpiNam
	e":"FastResponseTime","customServiceLevel":null}}}]}}}

---

Template not exists.
Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/templates/notexists?

	GET /api/templates/notexists? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Date: Wed, 07 Sep 2016 11:07:05 GMT
	Content-Type: application/xml
	Content-Length: 154
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="404" message="There is no template with id notexists in the 
	SLA Repository Database"/>

###Get all the templates###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/templates?

	GET /api/templates? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:06 GMT
	Content-Type: application/xml
	Content-Length: 5613
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><template
	s>.<wsag:Template wsag:TemplateId="template01" xmlns:wsag="http:
	//www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.ato
	s.eu">.    <wsag:Name>ExampleTemplate01</wsag:Name>.    <wsag:Co
	ntext>.        <wsag:AgreementResponder>provider01</wsag:Agreeme
	ntResponder>.        <wsag:ServiceProvider>AgreementResponder</w
	sag:ServiceProvider>.        <wsag:ExpirationTime>2014-03-07T13:
	00:00+01:00</wsag:ExpirationTime>.        <sla:Service>service02
	</sla:Service>.    </wsag:Context>.    <wsag:Terms>.        <wsa
	g:All>.            <wsag:ServiceDescriptionTerm wsag:Name="SDTNa
	me2" wsag:ServiceName="ServiceName">....DSL expression...</wsag:
	ServiceDescriptionTerm>.            <wsag:ServiceProperties wsag
	:Name="NonFunctional" wsag:ServiceName="ServiceName"/>.         
	   <wsag:GuaranteeTerm wsag:Name="GT_ResponseTime">.            
	    <wsag:ServiceScope>ServiceName</wsag:ServiceScope>.         
	       <wsag:ServiceLevelObjective>.                    <wsag:KP
	ITarget>.                        <wsag:KPIName>ResponseTime</wsa
	g:KPIName>.                        <wsag:CustomServiceLevel>{"co
	nstraint" : "ResponseTime LT qos:ResponseTime"}</wsag:CustomServ
	iceLevel>.                    </wsag:KPITarget>.                
	</wsag:ServiceLevelObjective>.            </wsag:GuaranteeTerm>.
	            <wsag:GuaranteeTerm wsag:Name="GT_Performance">.    
	            <wsag:ServiceScope>ServiceName</wsag:ServiceScope>. 
	               <wsag:ServiceLevelObjective>.                    
	<wsag:KPITarget>.                        <wsag:KPIName>Performan
	ce</wsag:KPIName>.                        <wsag:CustomServiceLev
	el>{"constraint" : "Performance GT qos:Performance"}</wsag:Custo
	mServiceLevel>.                    </wsag:KPITarget>.           
	     </wsag:ServiceLevelObjective>.                <wsag:Busines
	sValueList>.                    <wsag:CustomBusinessValue count=
	"1">.                        <sla:description></sla:description>
	.                    </wsag:CustomBusinessValue>.               
	     <wsag:Importance>3</wsag:Importance>.                </wsag
	:BusinessValueList>.            </wsag:GuaranteeTerm>.        </
	wsag:All>.    </wsag:Terms>.</wsag:Template>..<wsag:Template wsa
	g:TemplateId="template02" xmlns:wsag="http://www.ggf.org/namespa
	ces/ws-agreement" xmlns:sla="http://sla.atos.eu">.    <wsag:Name
	>ExampleTemplate</wsag:Name>.    <wsag:Context>.        <wsag:Ag
	reementInitiator>provider02</wsag:AgreementInitiator>.        <w
	sag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>.  
	      <wsag:ExpirationTime>2014-03-07T12:00:00+01:00</wsag:Expir
	ationTime>.        <wsag:TemplateId>template02</wsag:TemplateId>
	.        <sla:Service>service02</sla:Service>.    </wsag:Context
	>.    <wsag:Terms>.        <wsag:All>.            <wsag:ServiceD
	escriptionTerm/>.            <wsag:ServiceProperties/>.         
	   <wsag:ServiceProperties/>.            <wsag:GuaranteeTerm wsa
	g:Name="FastReaction">.                <wsag:ServiceScope wsag:S
	erviceName="GPS0001">               http://www.gps.com/coordsser
	vice/getcoords            </wsag:ServiceScope>.                <
	wsag:ServiceLevelObjective>.                    <wsag:KPITarget>
	.                        <wsag:KPIName>FastResponseTime</wsag:KP
	IName>.                    </wsag:KPITarget>.                </w
	sag:ServiceLevelObjective>.            </wsag:GuaranteeTerm>.   
	     </wsag:All>.    </wsag:Terms>.</wsag:Template>..<wsag:Templ
	ate wsag:TemplateId="template02b" xmlns:wsag="http://www.ggf.org
	/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">.    <w
	sag:Name>ExampleTemplate02b</wsag:Name>.    <wsag:Context>.     
	   <wsag:AgreementResponder>provider02</wsag:AgreementResponder>
	.        <wsag:ServiceProvider>AgreementResponder</wsag:ServiceP
	rovider>.        <wsag:ExpirationTime>2014-03-07T13:00:00+01:00<
	/wsag:ExpirationTime>.        <sla:Service>service02</sla:Servic
	e>.    </wsag:Context>.    <wsag:Terms>.        <wsag:All>.     
	       <wsag:ServiceDescriptionTerm wsag:Name="SDTName2" wsag:Se
	rviceName="ServiceName">....DSL expression...</wsag:ServiceDescr
	iptionTerm>.            <wsag:ServiceProperties wsag:Name="NonFu
	nctional" wsag:ServiceName="ServiceName"/>.            <wsag:Gua
	ranteeTerm wsag:Name="GT_ResponseTime">.                <wsag:Se
	rviceScope>ServiceName</wsag:ServiceScope>.                <wsag
	:ServiceLevelObjective>.                    <wsag:KPITarget>.   
	                     <wsag:KPIName>ResponseTime</wsag:KPIName>. 
	                       <wsag:CustomServiceLevel>{"constraint" : 
	"ResponseTime LT qos:ResponseTime"}</wsag:CustomServiceLevel>.  
	                  </wsag:KPITarget>.                </wsag:Servi
	ceLevelObjective>.            </wsag:GuaranteeTerm>.            
	<wsag:GuaranteeTerm wsag:Name="GT_Performance">.                
	<wsag:ServiceScope>ServiceName</wsag:ServiceScope>.             
	   <wsag:ServiceLevelObjective>.                    <wsag:KPITar
	get>.                        <wsag:KPIName>Performance</wsag:KPI
	Name>.                        <wsag:CustomServiceLevel>{"constra
	int" : "Performance GT qos:Performance"}</wsag:CustomServiceLeve
	l>.                    </wsag:KPITarget>.                </wsag:
	ServiceLevelObjective>.                <wsag:BusinessValueList>.
	                    <wsag:CustomBusinessValue count="1">.       
	                 <sla:description></sla:description>.           
	         </wsag:CustomBusinessValue>.                    <wsag:I
	mportance>3</wsag:Importance>.                </wsag:BusinessVal
	ueList>.            </wsag:GuaranteeTerm>.        </wsag:All>.  
	  </wsag:Terms>.</wsag:Template>.</templates>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/templates?

	GET /api/templates? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:07 GMT
	Content-Type: application/json
	Content-Length: 3912
	Server: Jetty(9.2.10.v20150310)
	[{"templateId":"template01","name":"ExampleTemplate01","context"
	:{"agreementInitiator":null,"agreementResponder":"provider01","s
	erviceProvider":"AgreementResponder","expirationTime":"2014-03-0
	7T13:00:00+01:00","templateId":null,"service":"service02","any":
	[]},"terms":{"allTerms":{"serviceDescriptionTerm":{"name":"SDTNa
	me2","serviceName":"ServiceName","any":["\t\t\t\tDSL expression\
	t\t\t"]},"serviceProperties":[{"name":"NonFunctional","serviceNa
	me":"ServiceName","variableSet":null}],"guaranteeTerms":[{"name"
	:"GT_ResponseTime","serviceScope":{"serviceName":null,"value":"S
	erviceName"},"serviceLevelObjective":{"kpitarget":{"kpiName":"Re
	sponseTime","customServiceLevel":{"any":["{\"constraint\" : \"Re
	sponseTime LT qos:ResponseTime\"}"]}}},"qualifyingCondition":nul
	l,"businessValueList":null,"serviceLevelObjetive":{"kpitarget":{
	"kpiName":"ResponseTime","customServiceLevel":{"any":["{\"constr
	aint\" : \"ResponseTime LT qos:ResponseTime\"}"]}}}},{"name":"GT
	_Performance","serviceScope":{"serviceName":null,"value":"Servic
	eName"},"serviceLevelObjective":{"kpitarget":{"kpiName":"Perform
	ance","customServiceLevel":{"any":["{\"constraint\" : \"Performa
	nce GT qos:Performance\"}"]}}},"qualifyingCondition":null,"busin
	essValueList":{"customBusinessValue":[{"count":1,"duration":"197
	0-01-01T00:00:00.000+0000","penalties":[],"description":""}],"im
	portance":3},"serviceLevelObjetive":{"kpitarget":{"kpiName":"Per
	formance","customServiceLevel":{"any":["{\"constraint\" : \"Perf
	ormance GT qos:Performance\"}"]}}}}]}}},{"templateId":"template0
	2","name":"ExampleTemplate","context":{"agreementInitiator":"pro
	vider02","agreementResponder":null,"serviceProvider":"AgreementI
	nitiator","expirationTime":"2014-03-07T12:00:00+01:00","template
	Id":"template02","service":"service02","any":[]},"terms":{"allTe
	rms":{"serviceDescriptionTerm":{"name":null,"serviceName":null,"
	any":[]},"serviceProperties":[{"name":null,"serviceName":null,"v
	ariableSet":null},{"name":null,"serviceName":null,"variableSet":
	null}],"guaranteeTerms":[{"name":"FastReaction","serviceScope":{
	"serviceName":"GPS0001","value":"               http://www.gps.c
	om/coordsservice/getcoords            "},"serviceLevelObjective"
	:{"kpitarget":{"kpiName":"FastResponseTime","customServiceLevel"
	:null}},"qualifyingCondition":null,"businessValueList":null,"ser
	viceLevelObjetive":{"kpitarget":{"kpiName":"FastResponseTime","c
	ustomServiceLevel":null}}}]}}},{"templateId":"template02b","name
	":"ExampleTemplate02b","context":{"agreementInitiator":null,"agr
	eementResponder":"provider02","serviceProvider":"AgreementRespon
	der","expirationTime":"2014-03-07T13:00:00+01:00","templateId":n
	ull,"service":"service02","any":[]},"terms":{"allTerms":{"servic
	eDescriptionTerm":{"name":"SDTName2","serviceName":"ServiceName"
	,"any":["\t\t\t\tDSL expression\t\t\t"]},"serviceProperties":[{"
	name":"NonFunctional","serviceName":"ServiceName","variableSet":
	null}],"guaranteeTerms":[{"name":"GT_ResponseTime","serviceScope
	":{"serviceName":null,"value":"ServiceName"},"serviceLevelObject
	ive":{"kpitarget":{"kpiName":"ResponseTime","customServiceLevel"
	:{"any":["{\"constraint\" : \"ResponseTime LT qos:ResponseTime\"
	}"]}}},"qualifyingCondition":null,"businessValueList":null,"serv
	iceLevelObjetive":{"kpitarget":{"kpiName":"ResponseTime","custom
	ServiceLevel":{"any":["{\"constraint\" : \"ResponseTime LT qos:R
	esponseTime\"}"]}}}},{"name":"GT_Performance","serviceScope":{"s
	erviceName":null,"value":"ServiceName"},"serviceLevelObjective":
	{"kpitarget":{"kpiName":"Performance","customServiceLevel":{"any
	":["{\"constraint\" : \"Performance GT qos:Performance\"}"]}}},"
	qualifyingCondition":null,"businessValueList":{"customBusinessVa
	lue":[{"count":1,"duration":"1970-01-01T00:00:00.000+0000","pena
	lties":[],"description":""}],"importance":3},"serviceLevelObjeti
	ve":{"kpitarget":{"kpiName":"Performance","customServiceLevel":{
	"any":["{\"constraint\" : \"Performance GT qos:Performance\"}"]}
	}}}]}}}]

###Delete a template###



	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/api/templates/template02b

	DELETE /api/templates/template02b HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:08 GMT
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	37
	Template with uuid template02b was deleted successfully

---

Template not exists


	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/api/templates/notexists

	DELETE /api/templates/notexists HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Date: Wed, 07 Sep 2016 11:07:09 GMT
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	9E
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<error c
	ode="404" message="There is no template with uuid notexists in t
	he SLA Repository Database"/>.

##Agremeents<a name="agreements"></a>##

###Create an agreement###

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/agreements

	POST /api/agreements HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2500

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:07:10 GMT
	Location: http://localhost:8080/api/agreements/agreement01
	Content-Type: application/xml
	Content-Length: 250
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The agreement has been stored successfully i
	n the SLA Repository Database. It has location http://localhost:
	8080/api/agreements/agreement01" elementId="agreement01"/>

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement02.json -X POST -H Content-type:application/json -H Accept:application/json http://localhost:8080/api/agreements

	POST /api/agreements HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/json
	Accept:application/json
	Content-Length: 3312

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:07:11 GMT
	Location: http://localhost:8080/api/agreements/agreement02
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	BE
	{"code":201,"message":"The agreement has been stored successfull
	y in the SLA Repository Database. It has location http://localho
	st:8080/api/agreements/agreement02","elementId":"agreement02"}

---

Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement02b.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/agreements

	POST /api/agreements HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2744

	HTTP/1.1 201 Created
	Date: Wed, 07 Sep 2016 11:07:13 GMT
	Location: http://localhost:8080/api/agreements/agreement02b
	Content-Type: application/xml
	Content-Length: 252
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><message 
	code="201" message="The agreement has been stored successfully i
	n the SLA Repository Database. It has location http://localhost:
	8080/api/agreements/agreement02b" elementId="agreement02b"/>

---

Linked provider not exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement03.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/agreements

	POST /api/agreements HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2743

	HTTP/1.1 409 Conflict
	Date: Wed, 07 Sep 2016 11:07:14 GMT
	Content-Type: application/xml
	Content-Length: 150
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Provider with id:provider03 doesn't exist SLA 
	Repository Database"/>

---

Linked template not exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement04.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/agreements

	POST /api/agreements HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2749

	HTTP/1.1 409 Conflict
	Date: Wed, 07 Sep 2016 11:07:15 GMT
	Content-Type: application/xml
	Content-Length: 150
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Template with id:template04 doesn't exist SLA 
	Repository Database"/>

---

Agreement exists.
Content type: application/xml

	$ /usr/bin/curl -u user:password -d@samples/appendix/agreement01.xml -X POST -H Content-type:application/xml -H Accept:application/xml http://localhost:8080/api/agreements

	POST /api/agreements HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Content-type:application/xml
	Accept:application/xml
	Content-Length: 2500

	HTTP/1.1 409 Conflict
	Date: Wed, 07 Sep 2016 11:07:16 GMT
	Content-Type: application/xml
	Content-Length: 160
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><error co
	de="409" message="Agreement with id:agreement01 already exists i
	n the SLA Repository Database"/>

###Get an agreement###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/agreements/agreement01?

	GET /api/agreements/agreement01? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:16 GMT
	Content-Type: application/xml
	Content-Length: 2961
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<wsag:Ag
	reement wsag:AgreementId="agreement01" xmlns:wsag="http://www.gg
	f.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">. 
	   <wsag:Name>ExampleAgreement</wsag:Name>.    <wsag:Context>.  
	      <wsag:AgreementInitiator>RandomClient</wsag:AgreementIniti
	ator>.        <wsag:AgreementResponder>provider01</wsag:Agreemen
	tResponder>.        <wsag:ServiceProvider>AgreementResponder</ws
	ag:ServiceProvider>.        <wsag:ExpirationTime>2014-03-07T13:0
	0:00+01:00</wsag:ExpirationTime>.        <wsag:TemplateId>templa
	te01</wsag:TemplateId>.        <sla:Service>service01</sla:Servi
	ce>.    </wsag:Context>.    <wsag:Terms>.        <wsag:All>.    
	        <wsag:ServiceDescriptionTerm wsag:Name="SDTName2" wsag:S
	erviceName="ServiceName">....DSL expression...</wsag:ServiceDesc
	riptionTerm>.            <wsag:ServiceProperties wsag:Name="NonF
	unctional" wsag:ServiceName="ServiceName">.                <wsag
	:VariableSet>.                    <wsag:Variable wsag:Name="Resp
	onseTime" wsag:Metric="xs:double">.                        <wsag
	:Location>qos:ResponseTime</wsag:Location>.                    <
	/wsag:Variable>.                    <wsag:Variable wsag:Name="Pe
	rformance" wsag:Metric="xs:double">.                        <wsa
	g:Location>qos:Performance</wsag:Location>.                    <
	/wsag:Variable>.                </wsag:VariableSet>.            
	</wsag:ServiceProperties>.            <wsag:GuaranteeTerm wsag:N
	ame="GT_ResponseTime">.                <wsag:ServiceScope wsag:S
	erviceName="ServiceName">ScopeName1</wsag:ServiceScope>.        
	        <wsag:ServiceLevelObjective>.                    <wsag:K
	PITarget>.                        <wsag:KPIName>ResponseTime</ws
	ag:KPIName>.                        <wsag:CustomServiceLevel>{"c
	onstraint" : "ResponseTime LT 0.9"}</wsag:CustomServiceLevel>.  
	                  </wsag:KPITarget>.                </wsag:Servi
	ceLevelObjective>.            </wsag:GuaranteeTerm>.            
	<wsag:GuaranteeTerm wsag:Name="GT_Performance">.                
	<wsag:ServiceScope wsag:ServiceName="ServiceName">ScopeName2</ws
	ag:ServiceScope>.                <wsag:ServiceLevelObjective>.  
	                  <wsag:KPITarget>.                        <wsag
	:KPIName>Performance</wsag:KPIName>.                        <wsa
	g:CustomServiceLevel>{"constraint" : "Performance GT 0.1"}</wsag
	:CustomServiceLevel>.                    </wsag:KPITarget>.     
	           </wsag:ServiceLevelObjective>.                <wsag:B
	usinessValueList>.                    <wsag:CustomBusinessValue 
	count="1">.                        <sla:Penalty type="discount" 
	expression="" unit="euro" validity=""/>.                        
	<sla:description></sla:description>.                    </wsag:C
	ustomBusinessValue>.                    <wsag:Importance>3</wsag
	:Importance>.                </wsag:BusinessValueList>.         
	   </wsag:GuaranteeTerm>.        </wsag:All>.    </wsag:Terms>.<
	/wsag:Agreement>.

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/agreements/agreement01?

	GET /api/agreements/agreement01? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:17 GMT
	Content-Type: application/json
	Content-Length: 1717
	Server: Jetty(9.2.10.v20150310)
	{"agreementId":"agreement01","name":"ExampleAgreement","context"
	:{"agreementInitiator":"RandomClient","agreementResponder":"prov
	ider01","serviceProvider":"AgreementResponder","expirationTime":
	"2014-03-07T13:00:00+01:00","templateId":"template01","service":
	"service01","any":[]},"terms":{"allTerms":{"serviceDescriptionTe
	rm":{"name":"SDTName2","serviceName":"ServiceName","any":["\t\t\
	t\tDSL expression\t\t\t"]},"serviceProperties":[{"name":"NonFunc
	tional","serviceName":"ServiceName","variableSet":{"variables":[
	{"name":"ResponseTime","metric":"xs:double","location":"qos:Resp
	onseTime"},{"name":"Performance","metric":"xs:double","location"
	:"qos:Performance"}]}}],"guaranteeTerms":[{"name":"GT_ResponseTi
	me","serviceScope":{"serviceName":"ServiceName","value":"ScopeNa
	me1"},"serviceLevelObjective":{"kpitarget":{"kpiName":"ResponseT
	ime","customServiceLevel":{"any":["{\"constraint\" : \"ResponseT
	ime LT 0.9\"}"]}}},"qualifyingCondition":null,"businessValueList
	":null,"serviceLevelObjetive":{"kpitarget":{"kpiName":"ResponseT
	ime","customServiceLevel":{"any":["{\"constraint\" : \"ResponseT
	ime LT 0.9\"}"]}}}},{"name":"GT_Performance","serviceScope":{"se
	rviceName":"ServiceName","value":"ScopeName2"},"serviceLevelObje
	ctive":{"kpitarget":{"kpiName":"Performance","customServiceLevel
	":{"any":["{\"constraint\" : \"Performance GT 0.1\"}"]}}},"quali
	fyingCondition":null,"businessValueList":{"customBusinessValue":
	[{"count":1,"duration":"1970-01-01T00:00:00.000+0000","penalties
	":[{"type":"discount","expression":"","unit":"euro","validity":"
	"}],"description":""}],"importance":3},"serviceLevelObjetive":{"
	kpitarget":{"kpiName":"Performance","customServiceLevel":{"any":
	["{\"constraint\" : \"Performance GT 0.1\"}"]}}}}]}}}

###Get all the agreements###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/agreements?

	GET /api/agreements? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:18 GMT
	Content-Type: application/xml
	Content-Length: 9523
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><agreemen
	ts>.<wsag:Agreement wsag:AgreementId="agreement01" xmlns:wsag="h
	ttp://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla
	.atos.eu">.    <wsag:Name>ExampleAgreement</wsag:Name>.    <wsag
	:Context>.        <wsag:AgreementInitiator>RandomClient</wsag:Ag
	reementInitiator>.        <wsag:AgreementResponder>provider01</w
	sag:AgreementResponder>.        <wsag:ServiceProvider>AgreementR
	esponder</wsag:ServiceProvider>.        <wsag:ExpirationTime>201
	4-03-07T13:00:00+01:00</wsag:ExpirationTime>.        <wsag:Templ
	ateId>template01</wsag:TemplateId>.        <sla:Service>service0
	1</sla:Service>.    </wsag:Context>.    <wsag:Terms>.        <ws
	ag:All>.            <wsag:ServiceDescriptionTerm wsag:Name="SDTN
	ame2" wsag:ServiceName="ServiceName">....DSL expression...</wsag
	:ServiceDescriptionTerm>.            <wsag:ServiceProperties wsa
	g:Name="NonFunctional" wsag:ServiceName="ServiceName">.         
	       <wsag:VariableSet>.                    <wsag:Variable wsa
	g:Name="ResponseTime" wsag:Metric="xs:double">.                 
	       <wsag:Location>qos:ResponseTime</wsag:Location>.         
	           </wsag:Variable>.                    <wsag:Variable w
	sag:Name="Performance" wsag:Metric="xs:double">.                
	        <wsag:Location>qos:Performance</wsag:Location>.         
	           </wsag:Variable>.                </wsag:VariableSet>.
	            </wsag:ServiceProperties>.            <wsag:Guarante
	eTerm wsag:Name="GT_ResponseTime">.                <wsag:Service
	Scope wsag:ServiceName="ServiceName">ScopeName1</wsag:ServiceSco
	pe>.                <wsag:ServiceLevelObjective>.               
	     <wsag:KPITarget>.                        <wsag:KPIName>Resp
	onseTime</wsag:KPIName>.                        <wsag:CustomServ
	iceLevel>{"constraint" : "ResponseTime LT 0.9"}</wsag:CustomServ
	iceLevel>.                    </wsag:KPITarget>.                
	</wsag:ServiceLevelObjective>.            </wsag:GuaranteeTerm>.
	            <wsag:GuaranteeTerm wsag:Name="GT_Performance">.    
	            <wsag:ServiceScope wsag:ServiceName="ServiceName">Sc
	opeName2</wsag:ServiceScope>.                <wsag:ServiceLevelO
	bjective>.                    <wsag:KPITarget>.                 
	       <wsag:KPIName>Performance</wsag:KPIName>.                
	        <wsag:CustomServiceLevel>{"constraint" : "Performance GT
	 0.1"}</wsag:CustomServiceLevel>.                    </wsag:KPIT
	arget>.                </wsag:ServiceLevelObjective>.           
	     <wsag:BusinessValueList>.                    <wsag:CustomBu
	sinessValue count="1">.                        <sla:Penalty type
	="discount" expression="" unit="euro" validity=""/>.            
	            <sla:description></sla:description>.                
	    </wsag:CustomBusinessValue>.                    <wsag:Import
	ance>3</wsag:Importance>.                </wsag:BusinessValueLis
	t>.            </wsag:GuaranteeTerm>.        </wsag:All>.    </w
	sag:Terms>.</wsag:Agreement>..<wsag:Agreement wsag:AgreementId="
	agreement02" xmlns:wsag="http://www.ggf.org/namespaces/ws-agreem
	ent" xmlns:sla="http://sla.atos.eu">.    <wsag:Name>ExampleAgree
	ment</wsag:Name>.    <wsag:Context>.        <wsag:AgreementIniti
	ator>client-prueba</wsag:AgreementInitiator>.        <wsag:Agree
	mentResponder>provider02</wsag:AgreementResponder>.        <wsag
	:ServiceProvider>AgreementResponder</wsag:ServiceProvider>.     
	   <wsag:ExpirationTime>2014-03-07T13:00:00+01:00</wsag:Expirati
	onTime>.        <wsag:TemplateId>template02</wsag:TemplateId>.  
	      <sla:Service>service02</sla:Service>.    </wsag:Context>. 
	   <wsag:Terms>.        <wsag:All>.            <wsag:ServiceProp
	erties wsag:Name="ServiceProperties" wsag:ServiceName="ServiceNa
	me">.                <wsag:VariableSet>.                    <wsa
	g:Variable wsag:Name="metric1" wsag:Metric="xs:double">.        
	                <wsag:Location>metric1</wsag:Location>.         
	           </wsag:Variable>.                    <wsag:Variable w
	sag:Name="metric2" wsag:Metric="xs:double">.                    
	    <wsag:Location>metric2</wsag:Location>.                    <
	/wsag:Variable>.                    <wsag:Variable wsag:Name="me
	tric3" wsag:Metric="xs:double">.                        <wsag:Lo
	cation>metric3</wsag:Location>.                    </wsag:Variab
	le>.                    <wsag:Variable wsag:Name="metric4" wsag:
	Metric="xs:double">.                        <wsag:Location>metri
	c4</wsag:Location>.                    </wsag:Variable>.        
	        </wsag:VariableSet>.            </wsag:ServiceProperties
	>.            <wsag:GuaranteeTerm wsag:Name="GT_Metric1">.      
	          <wsag:ServiceScope wsag:ServiceName="ServiceName"></ws
	ag:ServiceScope>.                <wsag:ServiceLevelObjective>.  
	                  <wsag:KPITarget>.                        <wsag
	:KPIName>metric1</wsag:KPIName>.                        <wsag:Cu
	stomServiceLevel>{"constraint" : "metric1 BETWEEN (0.05, 1)"}</w
	sag:CustomServiceLevel>.                    </wsag:KPITarget>.  
	              </wsag:ServiceLevelObjective>.            </wsag:G
	uaranteeTerm>.            <wsag:GuaranteeTerm wsag:Name="GT_Metr
	ic2">.                <wsag:ServiceScope wsag:ServiceName="Servi
	ceName"></wsag:ServiceScope>.                <wsag:ServiceLevelO
	bjective>.                    <wsag:KPITarget>.                 
	       <wsag:KPIName>metric2</wsag:KPIName>.                    
	    <wsag:CustomServiceLevel>{"constraint" : "metric2 BETWEEN (0
	.1, 1)"}</wsag:CustomServiceLevel>.                    </wsag:KP
	ITarget>.                </wsag:ServiceLevelObjective>.         
	   </wsag:GuaranteeTerm>.            <wsag:GuaranteeTerm wsag:Na
	me="GT_Metric3">.                <wsag:ServiceScope wsag:Service
	Name="ServiceName"></wsag:ServiceScope>.                <wsag:Se
	rviceLevelObjective>.                    <wsag:KPITarget>.      
	                  <wsag:KPIName>metric3</wsag:KPIName>.         
	               <wsag:CustomServiceLevel>{"constraint" : "metric3
	 BETWEEN (0.15, 1)"}</wsag:CustomServiceLevel>.                 
	   </wsag:KPITarget>.                </wsag:ServiceLevelObjectiv
	e>.            </wsag:GuaranteeTerm>.            <wsag:Guarantee
	Term wsag:Name="GT_Metric4">.                <wsag:ServiceScope 
	wsag:ServiceName="ServiceName"></wsag:ServiceScope>.            
	    <wsag:ServiceLevelObjective>.                    <wsag:KPITa
	rget>.                        <wsag:KPIName>metric4</wsag:KPINam
	e>.                        <wsag:CustomServiceLevel>{"constraint
	" : "metric4 BETWEEN (0.2, 1)"}</wsag:CustomServiceLevel>.      
	              </wsag:KPITarget>.                </wsag:ServiceLe
	velObjective>.            </wsag:GuaranteeTerm>.        </wsag:A
	ll>.    </wsag:Terms>.</wsag:Agreement>..<wsag:Agreement wsag:Ag
	reementId="agreement02b" xmlns:wsag="http://www.ggf.org/namespac
	es/ws-agreement" xmlns:sla="http://sla.atos.eu">.    <wsag:Name>
	ExampleAgreement</wsag:Name>.    <wsag:Context>.        <wsag:Ag
	reementInitiator>RandomClient</wsag:AgreementInitiator>.        
	<wsag:AgreementResponder>provider02</wsag:AgreementResponder>.  
	      <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProv
	ider>.        <wsag:ExpirationTime>2014-03-07T13:00:00+01:00</ws
	ag:ExpirationTime>.        <wsag:TemplateId>template02</wsag:Tem
	plateId>.        <sla:Service>service02</sla:Service>.    </wsag
	:Context>.    <wsag:Terms>.        <wsag:All>.            <wsag:
	ServiceDescriptionTerm wsag:Name="SDTName2" wsag:ServiceName="Se
	rviceName">....DSL expression...</wsag:ServiceDescriptionTerm>. 
	           <wsag:ServiceProperties wsag:Name="NonFunctional" wsa
	g:ServiceName="ServiceName">.                <wsag:VariableSet>.
	                    <wsag:Variable wsag:Name="ResponseTime" wsag
	:Metric="xs:double">.                        <wsag:Location>qos:
	ResponseTime</wsag:Location>.                    </wsag:Variable
	>.                    <wsag:Variable wsag:Name="Performance" wsa
	g:Metric="xs:double">.                        <wsag:Location>qos
	:Performance</wsag:Location>.                    </wsag:Variable
	>.                </wsag:VariableSet>.            </wsag:Service
	Properties>.            <wsag:GuaranteeTerm wsag:Name="GT_Respon
	seTime">.                <wsag:ServiceScope wsag:ServiceName="Se
	rviceName">ScopeName1</wsag:ServiceScope>.                <wsag:
	ServiceLevelObjective>.                    <wsag:KPITarget>.    
	                    <wsag:KPIName>ResponseTime</wsag:KPIName>.  
	                      <wsag:CustomServiceLevel>{"constraint" : "
	ResponseTime LT 0.9"}</wsag:CustomServiceLevel>.                
	    </wsag:KPITarget>.                </wsag:ServiceLevelObjecti
	ve>.            </wsag:GuaranteeTerm>.            <wsag:Guarante
	eTerm wsag:Name="GT_Performance">.                <wsag:ServiceS
	cope wsag:ServiceName="ServiceName">ScopeName2</wsag:ServiceScop
	e>.                <wsag:ServiceLevelObjective>.                
	    <wsag:KPITarget>.                        <wsag:KPIName>Perfo
	rmance</wsag:KPIName>.                        <wsag:CustomServic
	eLevel>{"constraint" : "Performance GT 0.1"}</wsag:CustomService
	Level>.                    </wsag:KPITarget>.                </w
	sag:ServiceLevelObjective>.                <wsag:BusinessValueLi
	st>.                    <wsag:CustomBusinessValue count="1">.   
	                     <sla:description></sla:description>.       
	             </wsag:CustomBusinessValue>.                    <ws
	ag:Importance>3</wsag:Importance>.                </wsag:Busines
	sValueList>.            </wsag:GuaranteeTerm>.        </wsag:All
	>.    </wsag:Terms>.</wsag:Agreement>.</agreements>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/agreements?

	GET /api/agreements? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:19 GMT
	Content-Type: application/json
	Content-Length: 5728
	Server: Jetty(9.2.10.v20150310)
	[{"agreementId":"agreement01","name":"ExampleAgreement","context
	":{"agreementInitiator":"RandomClient","agreementResponder":"pro
	vider01","serviceProvider":"AgreementResponder","expirationTime"
	:"2014-03-07T13:00:00+01:00","templateId":"template01","service"
	:"service01","any":[]},"terms":{"allTerms":{"serviceDescriptionT
	erm":{"name":"SDTName2","serviceName":"ServiceName","any":["\t\t
	\t\tDSL expression\t\t\t"]},"serviceProperties":[{"name":"NonFun
	ctional","serviceName":"ServiceName","variableSet":{"variables":
	[{"name":"ResponseTime","metric":"xs:double","location":"qos:Res
	ponseTime"},{"name":"Performance","metric":"xs:double","location
	":"qos:Performance"}]}}],"guaranteeTerms":[{"name":"GT_ResponseT
	ime","serviceScope":{"serviceName":"ServiceName","value":"ScopeN
	ame1"},"serviceLevelObjective":{"kpitarget":{"kpiName":"Response
	Time","customServiceLevel":{"any":["{\"constraint\" : \"Response
	Time LT 0.9\"}"]}}},"qualifyingCondition":null,"businessValueLis
	t":null,"serviceLevelObjetive":{"kpitarget":{"kpiName":"Response
	Time","customServiceLevel":{"any":["{\"constraint\" : \"Response
	Time LT 0.9\"}"]}}}},{"name":"GT_Performance","serviceScope":{"s
	erviceName":"ServiceName","value":"ScopeName2"},"serviceLevelObj
	ective":{"kpitarget":{"kpiName":"Performance","customServiceLeve
	l":{"any":["{\"constraint\" : \"Performance GT 0.1\"}"]}}},"qual
	ifyingCondition":null,"businessValueList":{"customBusinessValue"
	:[{"count":1,"duration":"1970-01-01T00:00:00.000+0000","penaltie
	s":[{"type":"discount","expression":"","unit":"euro","validity":
	""}],"description":""}],"importance":3},"serviceLevelObjetive":{
	"kpitarget":{"kpiName":"Performance","customServiceLevel":{"any"
	:["{\"constraint\" : \"Performance GT 0.1\"}"]}}}}]}}},{"agreeme
	ntId":"agreement02","name":"ExampleAgreement","context":{"agreem
	entInitiator":"client-prueba","agreementResponder":"provider02",
	"serviceProvider":"AgreementResponder","expirationTime":"2014-03
	-07T13:00:00+01:00","templateId":"template02","service":"service
	02","any":[]},"terms":{"allTerms":{"serviceDescriptionTerm":null
	,"serviceProperties":[{"name":"ServiceProperties","serviceName":
	"ServiceName","variableSet":{"variables":[{"name":"metric1","met
	ric":"xs:double","location":"metric1"},{"name":"metric2","metric
	":"xs:double","location":"metric2"},{"name":"metric3","metric":"
	xs:double","location":"metric3"},{"name":"metric4","metric":"xs:
	double","location":"metric4"}]}}],"guaranteeTerms":[{"name":"GT_
	Metric1","serviceScope":{"serviceName":"ServiceName","value":""}
	,"serviceLevelObjective":{"kpitarget":{"kpiName":"metric1","cust
	omServiceLevel":{"any":["{\"constraint\" : \"metric1 BETWEEN (0.
	05, 1)\"}"]}}},"qualifyingCondition":null,"businessValueList":nu
	ll,"serviceLevelObjetive":{"kpitarget":{"kpiName":"metric1","cus
	tomServiceLevel":{"any":["{\"constraint\" : \"metric1 BETWEEN (0
	.05, 1)\"}"]}}}},{"name":"GT_Metric2","serviceScope":{"serviceNa
	me":"ServiceName","value":""},"serviceLevelObjective":{"kpitarge
	t":{"kpiName":"metric2","customServiceLevel":{"any":["{\"constra
	int\" : \"metric2 BETWEEN (0.1, 1)\"}"]}}},"qualifyingCondition"
	:null,"businessValueList":null,"serviceLevelObjetive":{"kpitarge
	t":{"kpiName":"metric2","customServiceLevel":{"any":["{\"constra
	int\" : \"metric2 BETWEEN (0.1, 1)\"}"]}}}},{"name":"GT_Metric3"
	,"serviceScope":{"serviceName":"ServiceName","value":""},"servic
	eLevelObjective":{"kpitarget":{"kpiName":"metric3","customServic
	eLevel":{"any":["{\"constraint\" : \"metric3 BETWEEN (0.15, 1)\"
	}"]}}},"qualifyingCondition":null,"businessValueList":null,"serv
	iceLevelObjetive":{"kpitarget":{"kpiName":"metric3","customServi
	ceLevel":{"any":["{\"constraint\" : \"metric3 BETWEEN (0.15, 1)\
	"}"]}}}},{"name":"GT_Metric4","serviceScope":{"serviceName":"Ser
	viceName","value":""},"serviceLevelObjective":{"kpitarget":{"kpi
	Name":"metric4","customServiceLevel":{"any":["{\"constraint\" : 
	\"metric4 BETWEEN (0.2, 1)\"}"]}}},"qualifyingCondition":null,"b
	usinessValueList":null,"serviceLevelObjetive":{"kpitarget":{"kpi
	Name":"metric4","customServiceLevel":{"any":["{\"constraint\" : 
	\"metric4 BETWEEN (0.2, 1)\"}"]}}}}]}}},{"agreementId":"agreemen
	t02b","name":"ExampleAgreement","context":{"agreementInitiator":
	"RandomClient","agreementResponder":"provider02","serviceProvide
	r":"AgreementResponder","expirationTime":"2014-03-07T13:00:00+01
	:00","templateId":"template02","service":"service02","any":[]},"
	terms":{"allTerms":{"serviceDescriptionTerm":{"name":"SDTName2",
	"serviceName":"ServiceName","any":["\t\t\t\tDSL expression\t\t\t
	"]},"serviceProperties":[{"name":"NonFunctional","serviceName":"
	ServiceName","variableSet":{"variables":[{"name":"ResponseTime",
	"metric":"xs:double","location":"qos:ResponseTime"},{"name":"Per
	formance","metric":"xs:double","location":"qos:Performance"}]}}]
	,"guaranteeTerms":[{"name":"GT_ResponseTime","serviceScope":{"se
	rviceName":"ServiceName","value":"ScopeName1"},"serviceLevelObje
	ctive":{"kpitarget":{"kpiName":"ResponseTime","customServiceLeve
	l":{"any":["{\"constraint\" : \"ResponseTime LT 0.9\"}"]}}},"qua
	lifyingCondition":null,"businessValueList":null,"serviceLevelObj
	etive":{"kpitarget":{"kpiName":"ResponseTime","customServiceLeve
	l":{"any":["{\"constraint\" : \"ResponseTime LT 0.9\"}"]}}}},{"n
	ame":"GT_Performance","serviceScope":{"serviceName":"ServiceName
	","value":"ScopeName2"},"serviceLevelObjective":{"kpitarget":{"k
	piName":"Performance","customServiceLevel":{"any":["{\"constrain
	t\" : \"Performance GT 0.1\"}"]}}},"qualifyingCondition":null,"b
	usinessValueList":{"customBusinessValue":[{"count":1,"duration":
	"1970-01-01T00:00:00.000+0000","penalties":[],"description":""}]
	,"importance":3},"serviceLevelObjetive":{"kpitarget":{"kpiName":
	"Performance","customServiceLevel":{"any":["{\"constraint\" : \"
	Performance GT 0.1\"}"]}}}}]}}}]

###Get agreement status###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/agreements/agreement02/guaranteestatus?

	GET /api/agreements/agreement02/guaranteestatus? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:20 GMT
	Content-Type: application/xml
	Content-Length: 391
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><guarante
	estatus AgreementId="agreement02" value="NON_DETERMINED"><guaran
	teetermstatus name="GT_Metric1" value="NON_DETERMINED"/><guarant
	eetermstatus name="GT_Metric2" value="NON_DETERMINED"/><guarante
	etermstatus name="GT_Metric3" value="NON_DETERMINED"/><guarantee
	termstatus name="GT_Metric4" value="NON_DETERMINED"/></guarantee
	status>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/agreements/agreement02/guaranteestatus?

	GET /api/agreements/agreement02/guaranteestatus? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:21 GMT
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	113
	{"AgreementId":"agreement02","guaranteestatus":"NON_DETERMINED",
	"guaranteeterms":[{"name":"GT_Metric1","status":"NON_DETERMINED"
	},{"name":"GT_Metric2","status":"NON_DETERMINED"},{"name":"GT_Me
	tric3","status":"NON_DETERMINED"},{"name":"GT_Metric4","status":
	"NON_DETERMINED"}]}

###Delete an agreement###



	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/api/agreements/agreement02b

	DELETE /api/agreements/agreement02b HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:21 GMT
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	4F
	The agreement id agreement02bwith it's enforcement job was succe
	ssfully deleted

---

Agreement not exists


	$ /usr/bin/curl -u user:password -X DELETE -H Accept:application/xml http://localhost:8080/api/agreements/notexists

	DELETE /api/agreements/notexists HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 404 Not Found
	Date: Wed, 07 Sep 2016 11:07:22 GMT
	Content-Type: application/xml
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	9D
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?>.<error c
	ode="404" message="There is no agreement with id notexists in th
	e SLA Repository Database"/>.

###Get agreement status###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/agreements/agreement02/guaranteestatus?

	GET /api/agreements/agreement02/guaranteestatus? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:23 GMT
	Content-Type: application/xml
	Content-Length: 391
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><guarante
	estatus AgreementId="agreement02" value="NON_DETERMINED"><guaran
	teetermstatus name="GT_Metric1" value="NON_DETERMINED"/><guarant
	eetermstatus name="GT_Metric2" value="NON_DETERMINED"/><guarante
	etermstatus name="GT_Metric3" value="NON_DETERMINED"/><guarantee
	termstatus name="GT_Metric4" value="NON_DETERMINED"/></guarantee
	status>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/agreements/agreement02/guaranteestatus?

	GET /api/agreements/agreement02/guaranteestatus? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:23 GMT
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	113
	{"AgreementId":"agreement02","guaranteestatus":"NON_DETERMINED",
	"guaranteeterms":[{"name":"GT_Metric1","status":"NON_DETERMINED"
	},{"name":"GT_Metric2","status":"NON_DETERMINED"},{"name":"GT_Me
	tric3","status":"NON_DETERMINED"},{"name":"GT_Metric4","status":
	"NON_DETERMINED"}]}

##Enforcement Jobs<a name="enforcements"></a>##

###Start enforcement job###

Content type: 

	$ /usr/bin/curl -u user:password -X PUT http://localhost:8080/api/enforcements/agreement02/start

	PUT /api/enforcements/agreement02/start HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept: */*

	HTTP/1.1 202 Accepted
	Date: Wed, 07 Sep 2016 11:07:24 GMT
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	3F
	The enforcement job with agreement-uuid agreement02 has started

###Stop enforcement job###

Content type: 

	$ /usr/bin/curl -u user:password -X PUT http://localhost:8080/api/enforcements/agreement02/stop

	PUT /api/enforcements/agreement02/stop HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept: */*

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:24 GMT
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	3F
	The enforcement job with agreement-uuid agreement02 has stopped

##Violations<a name="violations"></a>##

Content type: 

	$ /usr/bin/curl -u user:password -X PUT http://localhost:8080/api/enforcements/agreement01/start

	PUT /api/enforcements/agreement01/start HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept: */*

	HTTP/1.1 202 Accepted
	Date: Wed, 07 Sep 2016 11:07:25 GMT
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	3F
	The enforcement job with agreement-uuid agreement01 has started

---

Content type: application/json

	$ /usr/bin/curl -u user:password -d@samples/appendix/metric01.json -X POST -H Content-type:application/json http://localhost:8080/api/test/enforcement-test/agreement01

	POST /api/test/enforcement-test/agreement01 HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept: */*
	Content-type:application/json
	Content-Length: 86
	{."metricKey": "Performance",."metricValue": "0",."date": "2014-
	08-25T12:54:00+00:00"}

	HTTP/1.1 202 Accepted
	Date: Wed, 07 Sep 2016 11:07:25 GMT
	Content-Type: text/plain
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	10
	Metrics received

###Get all the violations###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/violations?

	GET /api/violations? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:31 GMT
	Content-Type: application/xml
	Content-Length: 381
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><violatio
	ns><violation><uuid>99f8994f-0753-46e7-9ce4-55bff8cd0d25</uuid><
	agreement_id>agreement01</agreement_id><service_name>ServiceName
	</service_name><service_scope>ScopeName2</service_scope><kpi_nam
	e>Performance</kpi_name><datetime>2014-08-25T14:54:00+02:00</dat
	etime><actual_value>0</actual_value></violation></violations>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/violations?

	GET /api/violations? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:32 GMT
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	EC
	[{"uuid":"99f8994f-0753-46e7-9ce4-55bff8cd0d25","serviceName":"S
	erviceName","serviceScope":"ScopeName2","kpiName":"Performance",
	"datetime":"2014-08-25T14:54:00+02:00","expectedValue":null,"act
	ualValue":"0","contractUuid":"agreement01"}]

##Penalties<a name="penalties"></a>##

###Get all the penalties###

Accept: application/xml

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/xml http://localhost:8080/api/penalties?

	GET /api/penalties? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/xml

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:33 GMT
	Content-Type: application/xml
	Content-Length: 386
	Server: Jetty(9.2.10.v20150310)
	<?xml version="1.0" encoding="UTF-8" standalone="yes"?><penaltie
	s><penalty xmlns:wsag="http://www.ggf.org/namespaces/ws-agreemen
	t" xmlns:sla="http://sla.atos.eu"><uuid>0b378314-b54d-4e83-b67b-
	3ae90506dedc</uuid><agreement_id>agreement01</agreement_id><date
	time>2016-09-07T13:07:26+02:00</datetime><definition type="disco
	unt" expression="" unit="euro" validity=""/></penalty></penaltie
	s>

---

Accept: application/json

	$ /usr/bin/curl -u user:password -X GET -H Accept:application/json http://localhost:8080/api/penalties?

	GET /api/penalties? HTTP/1.1
	Host: localhost:8080
	Authorization: Basic dXNlcjpwYXNzd29yZA==
	User-Agent: curl/7.47.1
	Accept:application/json

	HTTP/1.1 200 OK
	Date: Wed, 07 Sep 2016 11:07:34 GMT
	Content-Type: application/json
	Transfer-Encoding: chunked
	Server: Jetty(9.2.10.v20150310)
	C1
	[{"uuid":"0b378314-b54d-4e83-b67b-3ae90506dedc","agreementId":"a
	greement01","datetime":"2016-09-07T13:07:26+02:00","definition":
	{"type":"discount","expression":"","unit":"euro","validity":""}}
	]

