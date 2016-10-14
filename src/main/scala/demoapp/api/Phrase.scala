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
