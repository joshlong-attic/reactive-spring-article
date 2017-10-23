# Reactive Spring

- the need for async, reactive IO
- the missing idiom
- the reactive streams initiative
- towards higher order operators w/ Reactor
- the reactive Spring ecosystem
- writing some sample data  w/ reactor and dependent calls
- data access
- spring mvc style controllers
- functional reactive endpoints
- security
- testing w/ the webtest client


   

## observability with Spring Boot 2's Actuator
// todo talk about how were chunking the results and so on
// intoduce the idea that u could handle all sorts of itnteractions ith the client in the same fashion since, at the end of the day, everything is a publisher. a publisher can handle websockets. it can handle server sent events. it can do everything.

Alright! Not bad. We've got a rest api, three different ways! canw e go to  production yet? Not yet. There are a number of concerns that need to be addressed in this. one is observability. observability is the insight that, no matter what technical stack ur using, no matter which business vertical ur software serves, no matter where ur buildign software, one thing is surprisingly consistnet: when the pager (or pagerduty, as much as antyhing these days) goes off, _somebody_ gets roused to a computer and has to get things back online. the goal is not root cause analysi, but to salve the wound. when the system is down, every second counts and we need to  support the remediation rpoccess with aas much insight into the operation of the system as possible. diffferent levels of the runtime will aid you here: a smart platform,  like Cloud Founddry, can gie ua  lot of insigth about the container running thw applicatoin and the runtime itself. the applicatoin is n the best situation to articalate its own state. we can support that articulatoin with the spring boot actuator.

the spring boot actuator isnt uniquely reactive. its been in spring boot from the beginning. the good news here is that, as of spring boot 2.0, it has been divorced frm any particualr web runtime. it no longer requires Spring MVC. it can work with Spring WebFlux, spring mvc, or jax-rs. specify that it export the http web endpoints with a bit of confiuration.

.configuration to expose the Actuator endpoints for http access
<!--  -->

w/ teh actuaot endpoints in place u can access all the usual suspects including the `applicatoin/metrics` endpoint and the `/applicatoin/health` endpoints.

.the health endponit
<!--  -->

the metrics endpoint is eworked in spring boot 2. its now backed by a new project  called [micrometer](http://micrometer.io). micrometer is an abstraction for dimensional metrics publication and accumulatoin. it integrates with time series databases like    Prometheus, Netflix Atlas, and Datadog. Support for InfluxDB, statsd, and Graphite are coming.  we designed micrometer to be framework agnostic, so u could use it independent of SPring Boot. that said, we hope youll agreeits even more compelling with.

.the metrics endpoint
<!--  -->

## Spring Security

I know what youre thinking, but its *still* nto quite ready fro production. we need to address security. We'll use Spring Security 5.0. spring security has been the bedrock for application security since its inception, providing a rich set of integrations with all manner of identity providers. it supports authentication and authorization. Spring Security supports authentication by propagating a security context so that application level code - method invocations, http requests, etc. - have easy access to the context. the context has, historically, been implemented in terms of a threadlocal, which makes a lot of sense in a non-reactive world. in a reactive world, however, things arent's so simple. thankfully, Reactor provides a  `Context` object, which acts as a sort of dictionary. Sspring Seecurity has been reworked to propagate its security context using this. additionally, parraellel, reactive type hierarchies  have been introduced that support non-blocking interaction with the security apparata for ur application.  

you dont have to worry about much of this nuance. i just think its super cool. all u need to know is tat in the reactive world, authentication is handled by an object of type `ReactieAuthenticationManager` which has a simple job: given an `Authentication` attempt, return a `Mono<Authentication>` indicating whether the atnentication attempt succeeded or throw an exception. one implementation of the `ReactieAuthenticationManager` supports delegating to a user-provied object called a `UserDetailsRepository`. you provide a  `UserDetailsRepository`  whe you want to integeate authentication with your custom username and password store. Yo might for example have a database table called `USERS`, or just a hardcoded Map of users. for the sake of simplicity, we'll take this approach and use some hardcoded users to lock down ou application.

.authenticatio witha hardcoded map of users
<!--  -->

lets try making an http basic call to  the service:

.curl the requests as both jlong and as rwinch
<!--  -->

by default, spring secrity locks down the whole application and installs http basic authentication. any attempt at calling any endpoint will fail unless we provide credentials. the problem is that, right onw, all authenticated users can access all endpoints. lets suppose that we want only want to allow users with therole 'user' to interact w/ the books they created, but those with the role `admin` to read _all_ books. this requires authorization. .

.configuraing authorizatoin with Sspring Security
<!--  -->

lets try it out by making some more authenticated requests

.making some more curl command sto talk to the endpdonits
<!--  -->

## Deployment

Our application is secure and observable. now we can deploy it. this is a naatural thing to run in a cloud provider. i happen to be a big fan of Cloud Foundry, an opensource Apache2 licensed cloud platform thats optomized for the continuous management  fo applications. It sits at a level (or two) above the infrasturcture. it is infrasturcture agnostic, running on local cloud providers like OpenStack and vSphere or o public cloud providers like Amazon Web Services, Googe Cloud, Microsoft Azure and, yes, [Oracle Cloud](https://blogs.oracle.com/developers/cloud-foundry-arrives-on-oracle-cloud). No matter where Cloud Foundry is insalled, its use is basically the same. You authenticate and then tell the platform about your application workload using the `cf` CLI.

.using cf ClI to push the application
<!--  -->

Once the paplication is up and running you can hit the application at its public http endpoint, as per normal. You  can provision backing services - message queue, databases, caches, etc. - using `cf create-service`. You cna scale the applicatio up to multiple load-balanced instances using `cf scale`. There ar a numbe rof other things you can do but suffice it to say, the application is now avalable and clients can talk to it.

"What client?," i hear u saying..

## The Client

weve just stood up a rest api. we need to connect a client to the service. we could use the `RestTemplate`, the general workhorse HTTP client  that has served us well for the better part of a decade. The `RestTemplate`, however, is not particualrly suited to potentially unlimited streams of data. It expects to be able to convert all response payloads into something by waiting for the end of the response. this isnt going to work if, for example, the client is using serer sent events, or even just a really large JSON response. Instead, well use the new `WebClient`, which is a Reactive Streams capable client. Its purpose builtfo what were trying to do. lets ocnfigure a new WebClient and use it to exercise our HTTP service, with authentication and all.

.calling the REST API
<!--  -->

## Subscriber#onNext

Whats next? In this article weve looked   briefly at building a web service with Spring Boot. We looked at Reactor, Spring Data Kay, Spring Framework 5 and Spring WebFlux, Spring Security 5, and Spring Boot 2.0.  

Spring Boot 2 sets the satage for Spring Cloud Finchley. Spring Cloud Finchley builds on Spring Boot 2.0, and updates a number of different APIs, where appropriate, to support the reactive paradigm. things like sevice registration and discovery work in   Sspring webflux based applications.   spring cloud commons supports client-side load-balancing across services registered in a service registry (like Apache Zookeepere,  Hashicorp Consul and Netflix Eureka) for the Spring Framework `WebClient`, the new reactive HTTP client. Spring cloud netlix Hystrix circuit breakers have always worked naturally with RxJava, which in turn can interop with RS `Publisher<T>`s. This interop is even easier now. Spring Cloud Stream supports working with pubishers to describe how messages arrive and are sent to messaging subsraits like RabbitMQ, Apache Kafka or Redis. Spring Cloud Gateway is a brand new reactive API gateway project that supports HTTP and websocket request proxying, rewriting, load-balancing, circuitbreaking, rate limiting and much more. Sspring Cloud Sleuth has been updated to support distributed tracing across reactive boundaries. The list goes on and on.

The `Future<Spring>` is.. reactive. Begin your journey building producction-worthy, agile and reactive applications and services with Spring Boot at the [Spring Initializr](http://start.spring.io). If you have questions, [find me on Twitter (@starbuxman)](http://twitter.com/starbuxman) or [email (josh@joshlong.com)](mailto://josh@joshlong.com).
<!--
  TODO:;
    - mention that there are a zillion other already possible things inlcuding using Reactor in Sprinng INtegraton
    - there's RSocket, which has a lot of possibility as well
    - theres websocket support
    - theres SSE support (thoug, the sse endpoints in this example should be used in the exampel above)
-->
