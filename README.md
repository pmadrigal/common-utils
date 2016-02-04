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
