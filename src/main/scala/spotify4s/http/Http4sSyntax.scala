package spotify4s.http

import io.circe.syntax._
import io.circe.{Encoder, Json, Printer}
import org.http4s._
import org.http4s.headers.`Content-Type`
import spotify4s.SpotifyConfig

object Http4sSyntax {

  implicit class RequestOps[F[_]](self: Request[F]) {
    def withJsonBody[T](maybeData: Option[T])(implicit enc: Encoder[T]): Request[F] =
      maybeData.fold(self)(data =>
        self
          .withContentType(`Content-Type`(MediaType.application.json))
          .withEntity(data.asJson.noSpaceNorNull)
      )
  }

  implicit class JsonOps(val self: Json) extends AnyVal {
    def noSpaceNorNull: String = Printer.noSpaces.copy(dropNullValues = true).print(self)
  }

  implicit class HeadersOps(self: Headers) {
    def toMap: Map[String, String] =
      self.toList.map(header => (header.name.value, header.value)).toMap
  }

  implicit class RequestBuilderOps[R](val self: RequestBuilder[R]) extends AnyVal {

    def toHeaderList: List[Header] =
      (self.headers.map(kv => Header(kv._1, kv._2)) ++
        self.authHeader.map(kv => Header(kv._1, kv._2))).toList

    def toUri(config: SpotifyConfig): Uri =
      Uri.fromString(self.url).getOrElse(Uri.unsafeFromString(config.baseUrl)) =?
        self.params.map(kv => (kv._1, List(kv._2)))

  }

}

