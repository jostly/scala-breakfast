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

  def actorRefFactory = context
  def receive = runRoute(phraseRoute)
}
