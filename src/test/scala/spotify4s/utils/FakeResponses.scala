package spotify4s.utils

import cats.effect.{IO, Resource}

import scala.io.Source

trait FakeResponses {
  lazy val listTracksResponse:  String = readResponseBody("listTracksResponse.json")
  lazy val getTrackResponse: String = readResponseBody("getTrackResponse.json")

  private def readResponseBody(fileName:String):String = {

    val acquire = IO {
      Source.fromResource(s"responses/${fileName}")
    }

    Resource.fromAutoCloseable(acquire).use(source => IO(source.mkString)).unsafeRunSync()

  }
}
