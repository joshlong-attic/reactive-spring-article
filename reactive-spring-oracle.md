# Reactive Spring

- the need for async, reactive IO
- the missing idiom
- the reactive streams initiative
- the reactive Spring ecosystem
- towards higher order operators w/ Reactor
- writing some sample data  w/ reactor and dependent calls
- data access
- spring mvc style controllers
- functional reactive endpoints
- security
- testing w/ the webtest client

# The Need for Async IO
At pivtoal we see a lot of organizations moving to miccroservices. the arhcitecture emphasizes small, singly focused, independently deplotable -piecs of functinoality. Theyre small because small implies a smaller workforcec working on the codebase. theyre singly focused becuase its easier to stay small that way. theyre independenly deploabble because tht means youre never blocked by other teams in the organization. the architecture has a lot of benefits; thye support organiztional agility. that said, they do have some drawbacks. the approach impies network distribution. the diestribution in turn invites architectural complexity and things get difficult when dependencies fails. things get difficul t when networks fail. and, specifically for our purposes in this discusion, things get diffiult when the conduction of data between nodes necoems overwhelming.

the overhwelming condduction of data between nodes is a harder problem to solve than most, espeically on the JVM, wehre the abstractions supporing IO  are   blocking in the original design in JDK 1.0. when we say blocking we mean that for a client to read a byte from a file or a socket or whatever, it needs to wait for the byte to arrvie. the thread in which the read is happeing is idle, but unusable. this is a pity because it doesnt need to be thiis way. at lower levels in the OS, network IO is typiccally asynchronous. POSIX complpian OSes have supported the ability to poll filedesritptors ro IO activity and dispatch when there is someting to do.   the classic `select(fd*)` call supports this.

JDK 1.4 saw the introduction of NIO, the _new_ IO packages. NIO gaves us channels which allow asyncchronous IO. this sia marked improvement but youll notice that it hasnt exactly become the pervasive metaphor for computation. these ddays almost alll of the APIS typical of enterpriee  computing are blocking by default and you probabyl dont even realize it. hopefully, most of us arent wriing custom database drivers, http sweb stacks, and security technologies ourselves. were relying on tried and true implementations that sit below the value line, below the line of things that we feel the need to expend cognitive effort trying to understand. our business application i swhat matters, and so most frameworks let us think about the world in terms of our business domain, relegating IO concersn to the murky depths of the of the framework and app stack.

wee think about the world in terms of instances of ourdomain model entities, `T`, or collections of `T`. those are synchronous views on data. the `CompletableFuture<T>` gives us asyncchronous views of data. it lets us say that, eventually, we'll have a reference to a `t`. But we havent had for the longest time a way to say that we have a potentially infinite collection of type `T` that may or may not arrive asynchronously.

Such a metaphor would need to be coupled with a way to gate the production and consumption of values. In the world of finite data sets, producers may occasionally overwhelm consumers but a consumer has various knobs and levers it can pull to buffer extra work, allowing it to eventually get to it. This works because the buffer is sufficient to cocntain the overwhelming demand. But what happens if the production is infinite? eventually, as with a floodded boat whose occupants are desperately trying to bail the ocean's worth of water,   the consumer will sink. This is called flow control, and it needs to be a first class feature of any computational metaphor  we choose.

# The Missing Metaphor

Not for want of trying, of course.. a lot of differnent orgs have tried to address this gap. microsoft kocked things off w the RX extensions for .NET. netflix ported that to java and created rxjava. (rxjava has in turn inspired countless lcones in othe rlanguages and platforms like Swift, NOde.js, and Ruby.) the spring team launched a project called Reactor. Lightbend (ne' Typesafe) tried to support this space in Akka and the Akka Streamsproject. Tim Fox, first at VMWare, and then at RedHat, launched the vert.x project. all of these attempt to address the same usecases: fill in the gap in our idioms and then, tow whatever extn possible, develop an ecosyste of tools on top of the new idiom that suppots modern application development concerns.

# The Reactive Streams Initiative
theres common groun dacross these different approaches. so the aforementioned vendors worked together to develop a de-facto standard, the [reactive streams initiative](http://www.reactive-streams.org/). The reactive streams initative defines four types:

the `Publisher<T>` is the computational metaphor we've been looking for. it defines the missing piece; a producer of values that may eventuallt arrive. a publisher produces values of type `T`

.`Publisher<T>``
<!--  -->

the `Subscriber` susbscribes to a Publisher, receiving notifications on any new values f type `T`

.`Subscriber`
<!--  -->

When a `Subscriber` subscribes to a `Publisher`, it results in a `Subscription<T>`

.`Subscription<T>`
<!--  -->

A `Publisher<T>` that is also a `Subscriber<T>` is called a `Processor<T>`

.`Processor<T>`
<!--  -->

// todo
The `Publisher` and `Subscriber` invert the normal dynamic of computation. instead of pulling data from a source, a subscriber asks for the next record.
.3.5. Backpressure

<!--  the following text is from the reactor documentation  -->
Propagating signals upstream is also used to implement backpressure, which we described in the assembly line analogy as a feedback signal sent up the line when a workstation processes more slowly than an upstream workstation.
The real mechanism defined by the Reactive Streams specification is pretty close to the analogy: a subscriber can work in unbounded mode and let the source push all the data at its fastest achievable rate or it can use the request mechanism to signal the source that it is ready to process at most n elements.
Intermediate operators can also change the request in-transit. Imagine a buffer operator that groups elements in batches of 10. If the subscriber requests 1 buffer, then it is acceptable for the source to produce 10 elements. Prefetching strategies can also be applied, if producing the elements before they are requested is not too costly.
This transforms the push model into a push-pull hybrid where the downstream can pull n elements from upstream if they are readily available, but, if the elements are not ready, then they will get pushed by the upstream whenever they are produced.


<!--  -->

the RS spec serves as a foundation for interoperability. it provides a common way to address this missing idiom. the specificatio is not mean tot be a prescription for the implementation APIs, it instead defines types for interoperability. Implementors can either buiild their implementations on top of the types or at least be able to consume and producer references to the types as sort of adapters.

The Reactive Streams types eventually found their way into Java 9 as 1:1 semantically equivalent interfaces in the `java.util.concurrent.Flow` class. The Reactive Streams initiative, for the moment, remains a sepeate set of types but the expectation is that all implementations will also support Java 9 as soon as possible.



# Reactor

Are the types in the Reactive Streams initiative enough? I'd say _no_! The reactive streams specification is a bit like the plain vanille array in the JDK. It provides a basis on which to support higher order computations. Most of us don't use arrays, we use something that extends from `java.util.Collection`. The same is true for the basic reactive streams types. In orer to support filering, transforming, and iteration, youll need DSLS and thats where one of the RS implementations can really help.

Pivotal's Reactor project is a good choice here. its built on top of the RS spec. It provides two specializations of the `Publisher<T>` type. The first, `Flux<T>`, is a Publisher that produces 0 or more values. It's unbounded.
