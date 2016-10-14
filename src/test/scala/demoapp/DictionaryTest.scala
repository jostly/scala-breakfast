package demoapp

import org.scalatest.FunSuite
import org.scalatest.concurrent.Eventually

class DictionaryTest extends FunSuite with Eventually {
/*
  test("pick a random word from a dictionary of one word") {
    val dict = Dictionary(Set("cat"))
    assert(dict.random() === "cat")
  }
  */
/*
  test("pick a random word from a dictionary of two words") {
    val dict = Dictionary(Set("cat", "bear"))
    eventually {
      assert(dict.random() === "cat")
    }
    eventually {
      assert(dict.random() === "bear")
    }
  }
*/
/*
  test("load a dictionary with one word from a file") {
    assert(Dictionary.from("one_animal.txt") === Dictionary(Set("cat")))
  }
*/
/*
  test("load a dictionary with three word from a file") {
    assert(Dictionary.from("3_animals.txt") === Dictionary(Set("cat", "bear", "ape")))
  }
*/
/*
  test("load a dictionary with multiple words, headers, and blank lines") {
    assert(Dictionary.from("words_with_headers.txt") === Dictionary(Set("banana", "pineapple", "raspberry")))
  }
*/
/*
  test("pick random word of specific character from Dictionary") {
    val dict = Dictionary(Set("cat", "bear"))
    for (i <- 0 to 100) {
      assert(dict.randomOf('c') === Some("cat"))
      assert(dict.randomOf('b') === Some("bear"))
      assert(dict.randomOf('a') === None)
    }
  }
*/

/*
  test("pick a random word from a dictionary of one word") {
    val dict = Dictionary(Set("cat"))
    assert(dict.random() === Some("cat"))
  }
  */
/*
  test("pick a random word from a dictionary of two words") {
    val dict = Dictionary(Set("cat", "bear"))
    eventually {
      assert(dict.random() === Some("cat"))
    }
    eventually {
      assert(dict.random() === Some("bear"))
    }
  }
*/

}
