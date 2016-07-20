[![Coverage Status](https://coveralls.io/repos/github/Stratio/Common-utils/badge.svg?branch=master)](https://coveralls.io/github/Stratio/Common-utils?branch=master)

# Stratio Common Library

This scala repository is a common library developed in Scala for use libraries with common features, this can be used
in all Stratio Modules.
Now is possible to create some generic modules:

- Repositories
- Logger
- Configuration

Appart from these modules, a functional package has been added in order to provide some extended functionality
regarding scala collections and functional utilities.

Repositories
============

- Zookeeper:
  
  Public interface and functions for use Zookeeper as a Repository Component
  
  
Logger
============

- Slf4j:
  
  Public interface and functions for use SLF4J library as a Logger Component
  

- Spark:
  
  Public interface and functions for use the Spark Logger interface as a Logger Component, internally use SLF4J library.
  
  
Config
============

- TypeSafe:
  
  Public interface and functions for use Typesafe configuration library as a Config Component
  
Metrics - time
==============

It allows measuring the time that took executing certain block code.
You can instantiate it by extending an implementation of ```TimeComponent```:

```scala
object MyModule extends SystemClockTimeComponent
```

You can measure the time and get the result of evaluating some ```T``` expression:

```scala
import MyModule._
val (result, timeItTook) = time(2 + 2)
```

Or just evaluate the time, ignoring the result:

```scala
import MyModule._
val duration = justTime {
  println(2 + 2)
}
```

Functional utilities
====================

Anonymous iterators
-------------------

These functional package iterator functions allow defining anonymous iterators with higher kinded functions.

A ```StatefulIterator``` keeps track of its internal state, and allows defining the way to retrieve a new element
from the iterator based on the internal state:

```scala
import com.stratio.common.utils.functional._
val ite: StatefulIterator[String,Int] =
  iterator(
    0,
    _ < 10,
    state => (state + 1, Random.nextString(state)))
 ```

Have in mind that the signature implies defining:
* the initial internal state
* the ```hasNext``` function regarding the internal current state
* the ```next``` function that, besides returning an element, mutates the iterator internal state.

You can also create a stateful iterator with the initial state and a ```next``` function that optionally might make
the iterator stop:

```scala
import com.stratio.common.utils.functional._
val ite: StatefulIterator[String,Int] =
  iterator(
    0,
    state => Option((state + 1, Random.nextString(state)).find(_ => state < 10))
```

A ```StatelessIterator```  allows defining the way to retrieve a new element from the iterator. It's actually an
```Iterator[Option[T]]``` with some apply helpers. As well as ```StatefulIterator``` worked, you can define both
```hasNext``` and ```next``` functions:

```scala
import com.stratio.common.utils.functional._
val ite: StatelessIterator[String] =
  iterator(
    database.hasResults(),
    database.read())
```
...or just define a single function that optionally might make the iterator stop:

```scala
import com.stratio.common.utils.functional._
val ite: StatelessIterator[String] =
  iterator(
    if (database.hasResults()) Option(database.read())
    else None)
```

Reflect type utilities
----------------------

It provides a fancy DSL (so an easy way) to determine type equality in Scala.

I.e.:

```scala
import scala.reflect.classTag
import com.stratio.common.utils.functional._

class Kid
class Daddy extends Kid
class Grandpa extends Daddy

classTag[Kid].isA[Kid] //true
classTag[Kid].isA[Daddy] //false
classTag[Daddy].isA[Grandpa] //false
classTag[Grandpa].isA[Kid] //true

classTag[Kid].isExactlyA[Kid] //true
classTag[Kid].isExactlyA[Daddy] //false
classTag[Daddy].isExactlyA[Grandpa] //false
classTag[Grandpa].isExactlyA[Grandpa] //true
```

It also works providing the ```Class``` with ```classOf[Kid]``` instead of the ```ClassTag```.


Monad Transformers utilities
----------------------

Using Monad Transformers you can work with complex types easily. Currently, there are two types
supported: ```Try[List[A]]``` and ```Try[Option[A]]```. Also, you can use ```Either[Throwable, List[A]]```
and ```Either[Throwable, Option[A]]``` equivalently.

The way to work with this functionality is using for comprehensions. With monad transformers we can iterate
through two levels of types. For example, if we have a ```Try[List[A]]```, we can iterate the list keeping the
type ```Try```. The same with the type ```Try[Option[A]]```.

Method to uses Monad Transformers with list is called ```get values```. On the other hand, using ```get value```
you could use it with an ```Option``` type.

I.e.:

```scala

  import com.stratio.common.utils.functional.MonadTransformerDSL._
  import TryFunctorConversionUtils._

  (for {
    x <- get values Try(List("a", "b"))
  } yield (x, x)).run  //Try(List(("a", "a"), ("b", "b")))

  (for {
    x <- get value Try(Option("a"))
  } yield (x, x)).run  //Try(Option(("a", "a")))

```

Have in mind that to undone the monad transformer and keep working with the original types, it is necessary a call to the
```run``` method.

To complement the monad transformers with ```Option``` types, it's also provided some extra functionality:

- ```orThrow```: throw an exception if the ```Option``` is ```None```.

```scala

  (for {
    x <- get value Try(Option("a")) orThrow new Exception("Exception message")
  } yield (x, x)).run  //Try(Option(("a", "a")))

  (for {
    x <- get value Try(None) orThrow new Exception("Exception message")
  } yield (x, x)).run  //An exception is thrown

```

- ```flatten```: it realizes a flatten. The ```Optio```n is removed but keeping the ```Try```.
An exception is thrown if the ```Option``` is ```None```.

```scala

  (for {
    x <- get value Try(Option("a"))
  } yield (x, x)).run.flatten  //Try(("a", "a"))

  (for {
    x <- get value Try(None)
  } yield (x, x)).run.flatten  //An exception is thrown

```

- ```flattenOr```: same functionality as flatten but providing an alternative if the ```Option``` is ```None```.

```scala

  (for {
    x <- get value Try(Option("a"))
  } yield (x, x)).run.flattenOr(("b", "b"))  //Try(("a", "a"))

  (for {
    x <- get value Try(None)
  } yield (x, x)).run.flattenOr(("b", "b"))  //Try(("b", "b"))

```

Concurrent utilities
====================

Future.option
-------------

Despite of not having a way a to invoke ```Future.sequence``` with an ```Option``` , the concurrent utilities provide
  a nice ```Option[Future[T]] => Future[Option[T]]``` function with the ```Future.option``` method:

```scala
import com.stratio.common.utils.concurrent._

val someFuture = Option(Future(1))
val result: Future[Option[Int]] = Future.option(someFuture)
```

Akka high level Keep-Alive heartbeat and monitor
------------------------------------------------

This tool set provides a mechanism implementing a dead man switch
in Akka not relying on low level transport features and this way being
compatible with both remote and local modes. It relies on the use of a
configurable **id** for monitored services identification.

It is composed by two parts:

* `MonitoredActor` trait which automatically makes the mixer actor a heart beater for the set id.
* The `KeepAliveMaster` is an actor whereby dead monitored services (identified by `id`) are detected. It offers a messaging interface.

This is an example of a rather silly monitored actor:

    class MonitoredActor(override val keepAliveId: Int, override val master: ActorRef) extends LiveMan[Int] {
        override val period: FiniteDuration = 100 milliseconds

        override def receive: Receive = PartialFunction.empty
    }
    
    val liveMan: ActorRef = system.actorOf(Props(new MonitoredActor(42, testActor)))
    
    
Silly for its only purpose is to be monitored but it could perform whatever task any regular actor can.
In this case, the service identifying `id` is and integer and is provided at build time: `override val keepAliveId: Int`

To set a monitor and subscribe it to our silly actor heartbeats you can just do as follows:


    val master: ActorRef = system.actorOf(KeepAliveMaster.props[Int](testActor)) // This creates the monitor actor...
    
    master ! DoCheck(42, 200 milliseconds) // And it is told to monitor the service with id = 42
    
 
`testActor` in the snippet above is the `ActorRef` of the actor to be notified when a monitored service has fallen.
  
`DoCheck` has 3 parameters:
 
1. `id` being monitored. The monitor will not care about which actor confirmed and `id`. It'll just be concerned on the `id` being confirmed. This enables control over high availability services as it abstract the underlying provider.
2. `period` for which heartbeats, from a specific id, are expected to be received at least once before raising miss alarms for that id.
3. `continueMonitoring` (OPTIONAL, default: `false`) is a flag telling whether an `id` should still be monitored after its service has fallen (`true`) or not (`false`, default).
  
`testActor` will receive `HeartbeatLost(fallenId)` when the by-that-id identified service has stopped its heartbeat.


  