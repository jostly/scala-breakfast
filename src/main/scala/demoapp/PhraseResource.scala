package demoapp

import spray.routing.HttpService

trait PhraseResource extends HttpService {

  val phraseRoute =
    get {
      path("") {
        complete {
          ???
        }
      }
    }
}
