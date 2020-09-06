package spotify4s.http

import cats.data.EitherT
import cats.effect.{Resource, Sync}
import cats.instances.string._
import cats.syntax.either._
import cats.syntax.functor._
import io.circe.{Decoder, Encoder}
import org.http4s.circe.CirceEntityDecoder._
import org.http4s.circe.jsonOf
import org.http4s.client.Client
import org.http4s.{EntityDecoder, Request, Response, Status}
import spotify4s.SPError.{JsonParsingError, UnhandledResponseError}
import spotify4s.domain.Pagination
import spotify4s.{SPError, SPResponse, SpotifyConfig}
import spotify4s.http.Http4sSyntax._

class HttpClient[F[_] : Sync](client: Client[F], val config: SpotifyConfig) {

  import HttpClient._

  def get[Res: Decoder](
                         accessToken: Option[String] = None,
                         method: String,
                         headers: Map[String, String] = Map.empty,
                         params: Map[String, String] = Map.empty,
                         pagination: Option[Pagination] = None
                       ): F[SPResponse[Res]] =
    run[Unit, Res](
      RequestBuilder(url = buildURL(method))
        .withAuth(accessToken)
        .withHeaders(headers)
        .withParams(
          params ++ pagination.fold(Map.empty[String, String])(p =>
            p.limit.fold(Map.empty[String, String])(l => Map("limit" -> l.toString))
              ++
              p.offset.fold(Map.empty[String, String])(o => Map("offset" -> o.toString))
          )
        )
    )

  def getWithoutResponse(
                          accessToken: Option[String] = None,
                          url: String,
                          headers: Map[String, String] = Map.empty
                        ): F[SPResponse[Unit]] =
    runWithoutResponse[Unit](
      RequestBuilder(buildURL(url)).withHeaders(headers).withAuth(accessToken)
    )

  def patch[Req: Encoder, Res: Decoder](
                                         accessToken: Option[String] = None,
                                         method: String,
                                         headers: Map[String, String] = Map.empty,
                                         data: Req
                                       ): F[SPResponse[Res]] =
    run[Req, Res](
      RequestBuilder(buildURL(method)).patchMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data)
    )

  def put[Req: Encoder, Res: Decoder](
                                       accessToken: Option[String] = None,
                                       url: String,
                                       headers: Map[String, String] = Map(),
                                       data: Req
                                     ): F[SPResponse[Res]] =
    run[Req, Res](
      RequestBuilder(buildURL(url)).putMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data)
    )

  def post[Req: Encoder, Res: Decoder](
                                        accessToken: Option[String] = None,
                                        url: String,
                                        headers: Map[String, String] = Map.empty,
                                        data: Req
                                      ): F[SPResponse[Res]] =
    run[Req, Res](
      RequestBuilder(buildURL(url)).postMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data)
    )

  def postAuth[Req: Encoder, Res: Decoder](
                                            method: String,
                                            headers: Map[String, String] = Map.empty,
                                            data: Req
                                          ): F[SPResponse[Res]] =
    run[Req, Res](RequestBuilder(buildURL(method)).postMethod.withHeaders(headers).withData(data))

  def postOAuth[Res: Decoder](
                               url: String,
                               headers: Map[String, String] = Map.empty,
                               params: Map[String, String] = Map.empty
                             ): F[SPResponse[Res]] =
    run[Unit, Res](
      RequestBuilder(url).postMethod
        .withHeaders(Map("Accept" -> "application/json") ++ headers)
        .withParams(params)
    )

  def delete(
              accessToken: Option[String] = None,
              url: String,
              headers: Map[String, String] = Map.empty
            ): F[SPResponse[Unit]] =
    run[Unit, Unit](
      RequestBuilder(buildURL(url)).deleteMethod.withHeaders(headers).withAuth(accessToken)
    )

  def deleteWithResponse[Res: Decoder](
                                        accessToken: Option[String] = None,
                                        url: String,
                                        headers: Map[String, String] = Map.empty
                                      ): F[SPResponse[Res]] =
    run[Unit, Res](
      RequestBuilder(buildURL(url)).deleteMethod
        .withAuth(accessToken)
        .withHeaders(headers)
    )

  def deleteWithBody[Req: Encoder, Res: Decoder](
                                                  accessToken: Option[String] = None,
                                                  url: String,
                                                  headers: Map[String, String] = Map.empty,
                                                  data: Req
                                                ): F[SPResponse[Res]] =
    run[Req, Res](
      RequestBuilder(buildURL(url)).deleteMethod
        .withAuth(accessToken)
        .withHeaders(headers)
        .withData(data)
    )

  private def buildURL(method: String): String = s"${config.baseUrl}$method"

  private def run[Req: Encoder, Res: Decoder](request: RequestBuilder[Req]): F[SPResponse[Res]] =
    runRequest(request)
      .use { response =>
        buildResponse(response).map(SPResponse(_, response.status.code, response.headers.toMap))
      }

  private def runWithoutResponse[Req: Encoder](request: RequestBuilder[Req]): F[SPResponse[Unit]] =
    runRequest(
      request
    ).use { response =>
      buildResponseFromEmpty(response).map(
        SPResponse(_, response.status.code, response.headers.toMap)
      )
    }

  private def runRequest[Req: Encoder](request: RequestBuilder[Req]): Resource[F, Response[F]] =
    client
      .run(
        Request[F]()
          .withMethod(request.httpVerb)
          .withUri(request.toUri(config))
          .withHeaders((config.toHeaderList ++ request.toHeaderList): _*)
          .withJsonBody(request.data)
      )
}

object HttpClient {
  // the GitHub API sometimes returns [[BasicError]] when 404.
  /*private[github4s] val notFoundDecoder: Decoder[SPError] =
    implicitly[Decoder[NotFoundError]].widen.or(BasicError.basicErrorDecoder.widen)

  private def notFoundEntityDecoder[F[_] : Sync]: EntityDecoder[F, SPError] =
    jsonOf(implicitly, notFoundDecoder)*/

  private[spotify4s] def buildResponse[F[_] : Sync, A: Decoder](
                                                                response: Response[F]
                                                              ): F[Either[SPError, A]] =
    (response.status.code match {
      case i if Status(i).isSuccess => response.attemptAs[A].map(_.asRight)
      /*case 400 => response.attemptAs[BadRequestError].map(_.asLeft)
      case 401 => response.attemptAs[UnauthorizedError].map(_.asLeft)
      case 403 => response.attemptAs[ForbiddenError].map(_.asLeft)
      case 404 => response.attemptAs[GHError](notFoundEntityDecoder).map(_.asLeft)
      case 422 => response.attemptAs[UnprocessableEntityError].map(_.asLeft)
      case 423 => response.attemptAs[RateLimitExceededError].map(_.asLeft)
      case 500 => response.attemptAs[UberError].map(_.asLeft)*/
      case _ =>
        EitherT
          .right(responseBody(response))
          .map(s =>
            UnhandledResponseError(s"Unhandled status code ${response.status.code}", s).asLeft
          )
    }).fold(
      e => (JsonParsingError(e): SPError).asLeft,
      _.leftMap[SPError](identity)
    )

  private[spotify4s] def buildResponseFromEmpty[F[_] : Sync](
                                                             response: Response[F]
                                                           ): F[Either[SPError, Unit]] =
    response.status.code match {
      case i if Status(i).isSuccess => Sync[F].pure(().asRight)
      case _ => buildResponse[F, Unit](response)
    }

  private def responseBody[F[_] : Sync](response: Response[F]): F[String] =
    response.bodyText.compile.foldMonoid
}

