package demoapp

import org.scalatest.concurrent.Eventually
import org.scalatest.{FunSuite, Inside, Matchers}
import spray.testkit.ScalatestRouteTest

class PhraseResourceTest extends FunSuite with Matchers with Inside with Eventually
  with PhraseResource with ScalatestRouteTest {

  val PhraseRegex = "(.*) (.*)".r
  def actorRefFactory = system
/*
  override val adjectives = Dictionary.from("3_adjectives.txt")
  override val animals = Dictionary.from("3_animals.txt")


  */
/*
  test("asking for an alliterative phrase") {
    Get() ~> addHeader("Accept", "text/plain") ~> namingRoute ~> check {
      inside(responseAs[String]) {
        case PhraseRegex(adjective, animal) =>
          List(adjective) should contain oneElementOf(adjectives.words)
          List(animal) should contain oneElementOf(animals.words)
          animal.head shouldBe adjective.head
      }
    }
  }
  */
/*
  test("asking for an alliterative phrase in json") {
    import spray.httpx.SprayJsonSupport._
    import demoapp.api.Phrase

    Get() ~> namingRoute ~> check {
      inside(responseAs[Phrase]) {
        case Phrase(adjective, animal) =>
          List(adjective) should contain oneElementOf(adjectives.words)
          List(animal) should contain oneElementOf(animals.words)
          animal.head shouldBe adjective.head
      }
    }
  }
  */
/*
  test("asking for a phrase of specific letter") {
    Get("/c") ~> addHeader("Accept", "text/plain") ~> namingRoute ~> check {
      responseAs[String] shouldBe "curious cat"
    }
  }
  test("asking for a phrase of specific letter in json") {
    import spray.httpx.SprayJsonSupport._
    import demoapp.api.Phrase

    Get("/c") ~> namingRoute ~> check {
      responseAs[Phrase] shouldBe Phrase("curious", "cat")
    }
  }
*/
}
