# Introduction

This is a small example of building a simple HTTP service using Scala and Spray.
It is intended as a summary, or aide–mémoire, for accompanying talk. Therefore the
text is light on explanations on *why* things are done, instead focusing on *how*
they are done.

# Tools

## sbt

sbt is the interactive build tool for Scala. Install it from [scala-sbt.org](http://www.scala-sbt.org/download.html)

## Scala

Normally the Scala compiler is downloaded and executed by sbt.
If you wish to install it separately, you can do so using the instructions on [scala-lang.org](http://www.scala-lang.org/download/)

## HTTPie

HTTPie is a helpful command line tool to access HTTP resources. It is not required
for this example, but it makes manual testing of the finished service much easier.
You can install it from [httpie.org](https://httpie.org/)

# Libraries

## ScalaTest

This example uses ScalaTest for testing. It is installed by sbt as part of the
dependencies. For documentation on using the library, see [scalatest.org](http://www.scalatest.org/)

## Spray

The web service library used is Spray, which is installed by sbt as part of the
dependencies. Read the documentation on [spray.io](http://spray.io/)

It should be noted that Spray is being deprecated in favour of [Akka HTTP](http://doc.akka.io/docs/akka/2.4/scala/http/index.html). This example uses Spray because the author is more familiar with that library; replacing Spray with Akka HTTP is left as an exercise to the reader. The APIs are quite similar, so lessons learnt using Spray will still be valid for Akka HTTP.

# Instructions

## 1. Create app directory and initial sbt file.

```bash
$ mkdir demoapp
$ cd demoapp
$ echo 'scalaVersion := "2.11.8"' > build.sbt
$ mkdir -p src/main/scala/demoapp
$ mkdir -p src/test/scala/demoapp
$ sbt
```

## 2. Hello, world

Create `src/main/scala/demoapp/Boot.scala`
```scala
package demoapp

object Boot extends App {
  println("Hello, world!")
}
```

Run application: `sbt run`

## 3. Test-drive Dictionary

### 3a. Add ScalaTest dependency

Edit `build.sbt`
```sbt
scalaVersion := "2.11.8"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0"
```

### 3b. Dictionary of one word

Enable continuous testing with `sbt ~test`

Create `src/test/scala/demoapp/DictionaryTest.scala`
```scala
package demoapp

import org.scalatest.FunSuite

class DictionaryTest extends FunSuite {
  test("pick a random word from a dictionary of one word") {
    val dict = Dictionary(Set("cat"))
    assert(dict.random() === "cat")
  }
}

case class Dictionary(words: Set[String]) {
  def random(): String = "cat"
}
```

### 3b. Dictionary of two words

Edit `src/test/scala/demoapp/DictionaryTest.scala`
```scala

import org.scalatest.concurrent.Eventually

class DictionaryTest extends FunSuite with Eventually {
  test("pick a random word from a dictionary of two words") {
    val dict = Dictionary(Set("cat", "bear"))
    eventually {
      assert(dict.random() === "cat")      
    }
    eventually {
      assert(dict.random() === "bear")
    }
  }
}

case class Dictionary(words: Set[String]) {
  def random(): String = words.maxBy(_ => math.random)
}
```

### 3c. Load dictionary from file

Extract resources to project: `tar -xzf demoapp.tgz`

Edit `src/test/scala/demoapp/DictionaryTest.scala`
```scala

class DictionaryTest extends FunSuite with Eventually {
  test("load a dictionary with one word from a file") {
    assert(Dictionary.from("one_animal.txt") === Dictionary(Set("cat")))
  }

  test("load a dictionary with three word from a file") {
    assert(Dictionary.from("3_animals.txt") === Dictionary(Set("cat", "bear", "ape")))
  }  
}

object Dictionary {
  def from(resource: String): Dictionary = {
    val words = io.Source
      .fromInputStream(getClass.getResourceAsStream(s"/$resource"))
      .getLines
      .toSet
    Dictionary(words)
  }    
}
```

### 3d. Load dictionary from file with headers and blank rows

Edit `src/test/scala/demoapp/DictionaryTest.scala`
```scala
class DictionaryTest extends FunSuite with Eventually {
  test("load a dictionary with multiple words, headers, and blank lines") {
    assert(Dictionary.from("words_with_headers.txt") === Dictionary(Set("banana", "pineapple", "raspberry")))
  }
}

object Dictionary {
  def from(resource: String): Dictionary = {
    def isHeader(s: String) = s.matches("[A-Z]")

    val words = io.Source
      .fromInputStream(getClass.getResourceAsStream(s"/$resource"))
      .getLines
      .filterNot(isHeader)
      .filterNot(_.isEmpty)
      .toSet
    Dictionary(words)
  }    
}
```

### 3e. Move implementation

Create `src/main/scala/demoapp/Dictionary.scala`
```scala
package demoapp

case class Dictionary(words: Set[String]) {
  def random(): String = words.maxBy(_ => math.random)
}

object Dictionary {
  def from(resource: String): Dictionary = {
    def isHeader(s: String) = s.matches("[A-Z]")

    val words = io.Source
      .fromInputStream(getClass.getResourceAsStream(s"/$resource"))
      .getLines
      .filterNot(isHeader)
      .filterNot(_.isEmpty)
      .toSet
    Dictionary(words)
  }    
}
```

Edit `src/test/scala/demoapp/DictionaryTest.scala`
```scala
package demoapp

import org.scalatest.FunSuite
import org.scalatest.concurrent.Eventually

class DictionaryTest extends FunSuite with Eventually {
  test("pick a random word from a dictionary of one word") {
    val dict = Dictionary(Set("cat"))
    assert(dict.random() === "cat")
  }

  test("pick a random word from a dictionary of two words") {
    val dict = Dictionary(Set("cat", "bear"))
    eventually {
      assert(dict.random() === "cat")      
    }
    eventually {
      assert(dict.random() === "bear")
    }
  }

  test("load a dictionary with one word from a file") {
    assert(Dictionary.from("one_animal.txt") === Dictionary(Set("cat")))
  }

  test("load a dictionary with three word from a file") {
    assert(Dictionary.from("3_animals.txt") === Dictionary(Set("cat", "bear", "ape")))
  }  

  test("load a dictionary with multiple words, headers, and blank lines") {
    assert(Dictionary.from("words_with_headers") === Dictionary(Set("banana", "pineapple", "raspberry")))
  }
}
```
## 4. Put it together in a command line app

Edit `src/main/scala/demoapp/Boot.scala`
```scala
object Boot extends App {
  val adjectives = Dictionary.from("adjectives.txt")
  val animals = Dictionary.from("animals.txt")

  println(s"${adjectives.random()} ${animals.random()}")
}
```

Run with `sbt run`

## 5. New requirement: alliterative phrase

### 5a. Pick random word with specific starting character

Edit `src/test/scala/demoapp/DictionaryTest.scala`
```scala
class DictionaryTest extends FunSuite with Eventually {
  test("pick random word of specific character from Dictionary") {
    val dict = Dictionary(Set("cat", "bear"))
    for (i <- 0 to 100) {
      assert(dict.randomOf('c') === "cat")
      assert(dict.randomOf('b') === "bear")
    }    
  }
}
```

Edit `src/main/scala/demoapp/Dictionary.scala`
```scala
case class Dictionary(words: Set[String]) {
  ...
  def randomOf(c: Char): String = words
    .filter(_.head == c)
    .maxBy(_ => math.random)
}
```

### 5b. Handle case when no word is available

Edit `src/test/scala/demoapp/DictionaryTest.scala`
```scala
class DictionaryTest extends FunSuite with Eventually {
  test("pick random word of specific character from Dictionary") {
    val dict = Dictionary(Set("cat", "bear"))
    for (i <- 0 to 100) {
      assert(dict.randomOf('c') === Some("cat"))
      assert(dict.randomOf('b') === Some("bear"))
      assert(dict.randomOf('a') === None)
    }    
  }
}
```

Edit `src/main/scala/demoapp/Dictionary.scala`
```scala
case class Dictionary(words: Set[String]) {
  ...
  def randomOf(c: Char): Option[String] = {
    val filtered = words.filter(_.head == c)

    if (filtered.nonEmpty) Some(filtered.maxBy(_ => math.random))
    else None
  }
}
```

### 5c. Update application

Edit `src/main/scala/demoapp/Boot.scala`
```scala
object Boot extends App {
  val adjectives = Dictionary.from("adjectives.txt")
  val animals = Dictionary.from("animals.txt")

  val adjective = adjectives.random()
  val animal = animals.randomOf(adjective.head)

  if (animal.isDefined) println(s"${adjective} ${animal.get}")
}
```

Run with `sbt run`

### 5d. Refactor to use for-comprehension

Edit `src/main/scala/demoapp/Boot.scala`
```scala
object Boot extends App {
  val adjectives = Dictionary.from("adjectives.txt")
  val animals = Dictionary.from("animals.txt")

  for {
    adjective <- Some(adjectives.random())
    animal <- animals.randomOf(adjective.head)
  } println(s"${adjective} ${animal}")
}
```

Run with `sbt run`

### 5e. Make random uniform with randomOf

Edit `src/test/scala/demoapp/DictionaryTest.scala`
Change all expectations of the random() method from `=== "cat"` to `=== Some("cat")`
and likewise for other animals.

Add test:
```scala
class DictionaryTest extends FunSuite with Eventually {
  test("pick random word from empty dictionary") {
    val dict = Dictionary(Set.empty)
    assert(dict.random() === None)
  }
}
```

Edit `src/main/scala/demoapp/Dictionary.scala`
```scala
case class Dictionary(words: Set[String]) {
  def random(): Option[String] = pickFrom(words)
  def randomOf(c: Char): Option[String] = pickFrom(words.filter(_.head == c))

  private def pickFrom(w: Set[String]): Option[String] = {
    if (w.nonEmpty) Some(w.maxBy(_ => math.random))
    else None  
  }
}
```

Edit `src/main/scala/demoapp/Boot.scala`
```scala
object Boot extends App {
  ...

  for {
    adjective <- adjectives.random()
    animal <- animals.randomOf(adjective.head)
  } println(s"${adjective} ${animal}")
}
```

## 6. Create http endpoint

### 6a. Add dependencies to spray

Edit `build.sbt`
```sbt
scalaVersion := "2.11.8"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-httpx"   % sprayV,
    "io.spray"            %%  "spray-json"    % sprayV,
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.scalatest"       %%  "scalatest"     % "3.0.0" % "test"
  )
}

Revolver.settings

// Make re-start run tests before restarting
reStart <<= reStart dependsOn (test in Test)
```
Create `project/plugins.sbt`
```sbt
addSbtPlugin("io.spray" % "sbt-revolver" % "0.8.0")
```

Reload if you are in sbt: `reload`

### 6b. Create spray resource

Test with `sbt ~test`

Create `src/test/scala/demoapp/PhraseResourceTest.scala`
```scala
package demoapp

import org.scalatest.{FunSuite, Matchers}
import spray.testkit.ScalatestRouteTest

class PhraseResourceTest extends FunSuite with Matchers
  with ScalatestRouteTest with PhraseResource {

  def actorRefFactory = system

  override val adjectives = Dictionary.from("3_adjectives.txt")
  override val animals = Dictionary.from("3_animals.txt")

  test("asking for an alliterative phrase") {
    Get() ~> phraseRoute ~> check {
      val Array(adjective, animal) = responseAs[String].split(" ")
      List(adjective) should contain oneElementOf(adjectives.words)
      List(animal) should contain oneElementOf(animals.words)
      animal.head shouldBe adjective.head
    }
  }
}
```

Create `src/main/scala/demoapp/PhraseResource.scala`
```scala
package demoapp

import spray.routing.HttpService

trait PhraseResource extends HttpService {
  val animals: Dictionary
  val adjectives: Dictionary

  val phraseRoute =
    get {
      path("") {
        complete {
          for {
            adjective <- adjectives.random()
            animal <- animals.randomOf(adjective.head)
          } yield s"$adjective $animal"
        }
      }
    }
}
```

### 6c. Create spray application

Edit `src/main/scala/demoapp/Boot.scala`
```scala
package demoapp

import akka.actor.{Actor, ActorSystem, Props}
import akka.io.IO
import akka.pattern.ask
import akka.util.Timeout
import spray.can.Http

import scala.concurrent.duration._

object Boot extends App {

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("demoapp")

  // create and start our service actor
  val service = system.actorOf(Props[PhraseActor], "phrase-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = "localhost", port = 8080)
}

class PhraseActor extends Actor with PhraseResource {

  override val adjectives = Dictionary.from("adjectives.txt")
  override val animals = Dictionary.from("animals.txt")

  def actorRefFactory = context
  def receive = runRoute(phraseRoute)  
}
```

Test and deploy with `sbt ~re-start`
Will recompile and test if any file changes.
Will automatically redeploy server if tests pass.

Check server using [HTTPie](https://httpie.org/) with `http :8080`

## 7. Enable JSON response

### 7a. Create API class

Create `src/main/scala/demoapp/api/Phrase.scala`
```scala
package demoapp.api

case class Phrase(adjective: String, animal: String)

object Phrase {
  import spray.http.MediaTypes
  import spray.httpx.marshalling.{Marshaller, ToResponseMarshaller}
  import spray.httpx.unmarshalling.{Unmarshaller, FromResponseUnmarshaller}
  import spray.json.DefaultJsonProtocol._
  import spray.httpx.SprayJsonSupport._

  val jsonMarshaller: Marshaller[Phrase] = jsonFormat2(Phrase.apply)
  val textMarshaller: Marshaller[Phrase] =
    Marshaller.delegate[Phrase, String](MediaTypes.`text/plain`) { phrase =>
      s"${phrase.adjective} ${phrase.animal}"
    }

  implicit val phraseMarshaller =
    ToResponseMarshaller.oneOf(MediaTypes.`application/json`, MediaTypes.`text/plain`) (jsonMarshaller, textMarshaller)

  implicit val phraseUnmarshaller =
    jsonFormat2(Phrase.apply)
}
```

### 7b. Update tests

Edit `src/test/scala/demoapp/PhraseResourceTest.scala`
```scala

import demoapp.api.Phrase
import spray.httpx.SprayJsonSupport._

class PhraseResourceTest extends FunSuite with Matchers
  with ScalatestRouteTest with PhraseResource {

  test("asking for an alliterative phrase") {
    Get() ~> addHeader("Accept", "text/plain") ~> phraseRoute ~> check {
      ...
    }
  }

  test("asking for an alliterative phrase, returning JSON") {
    Get() ~> addHeader("Accept", "application/json") ~> phraseRoute ~> check {
      val Phrase(adjective, animal) = responseAs[Phrase]
      List(adjective) should contain oneElementOf(adjectives.words)
      List(animal) should contain oneElementOf(animals.words)
      animal.head shouldBe adjective.head
    }    
  }
}
```

### 7c. Update resource

Edit `src/main/scala/demoapp/PhraseResource.scala`
```scala
import demoapp.api.Phrase

trait PhraseResource extends HttpService {
  ...

  val phraseRoute =
    get {
      path("") {
        complete {
          for {
            adjective <- adjectives.random()
            animal <- animals.randomOf(adjective.head)
          } yield Phrase(adjective, animal)
        }
      }
    }  
}
```

The formats in the companion object of Phrase take care of serialization to JSON and text
respectively.
