package spotify4s.algebras

import spotify4s.SPResponse
import spotify4s.domain.Track

trait Tracks[F[_]] {
  def listTracks(
                  ids: List[String],
                  market: Option[String],
                  headers: Map[String, String] = Map()
                ): F[SPResponse[Track]]
}
