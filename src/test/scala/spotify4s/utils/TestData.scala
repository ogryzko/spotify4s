package spotify4s.utils

import spotify4s.domain.{Album, Artists, Track}

trait TestData {

  val okStatusCode           = 200
  val sampleToken: Option[String]          = Some("token")

  val album: Album = ???

  val track: Track = ???
}
