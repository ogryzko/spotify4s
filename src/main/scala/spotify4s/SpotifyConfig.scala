package spotify4s

import org.http4s.Header

final case class SpotifyConfig(
                               baseUrl: String,
                               authorizeUrl: String,
                               accessTokenUrl: String,
                               headers: Map[String, String]
                             ) {
  def toHeaderList: List[Header] = headers.map { case (k, v) => Header(k, v) }.toList
}
