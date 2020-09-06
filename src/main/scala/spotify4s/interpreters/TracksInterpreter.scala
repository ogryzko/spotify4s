package spotify4s.interpreters

import spotify4s.SPResponse
import spotify4s.algebras.Tracks
import spotify4s.domain.Track
import spotify4s.http.HttpClient
import spotify4s.Decoders._

class TracksInterpreter[F[_]](implicit client: HttpClient[F], accessToken: Option[String])
  extends Tracks[F] {
  override def listTracks(ids: List[String],
                          market: Option[String],
                          headers: Map[String, String]): F[SPResponse[Track]] = {
    //TODO how to make this line shorter?
    val params: Map[String, String] = Map("ids" -> ids.mkString(","))++market.fold(Map.empty[String, String])(m => (Map("market" -> m)))
    client.get[Track](accessToken, "tracks", headers, params)
  }
}
