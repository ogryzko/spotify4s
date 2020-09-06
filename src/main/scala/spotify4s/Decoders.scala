package spotify4s

import cats.data.NonEmptyList
import io.circe.Decoder.Result
import io.circe.{Decoder, DecodingFailure, HCursor}
import cats.syntax.list._
import cats.instances.either._
import io.circe.generic.auto._
import io.circe.generic.semiauto.deriveDecoder
import spotify4s.domain.{Album, Track, Tracks}

object Decoders {
  implicit val decodeTrack: Decoder[Track] = deriveDecoder[Track]
  implicit val decodeAlbum: Decoder[Album] = deriveDecoder[Album]
  implicit val decodeTrackList: Decoder[Tracks] = deriveDecoder[Tracks]

  implicit def decodeNonEmptyList[T](implicit D: Decoder[T]): Decoder[NonEmptyList[T]] = {

    def decodeCursors(cursors: List[HCursor]): Result[NonEmptyList[T]] =
      cursors.toNel
        .toRight(DecodingFailure("Empty Response", Nil))
        .flatMap(nelCursors => nelCursors.traverse(_.as[T]))

    Decoder.instance { c =>
      c.as[T] match {
        case Right(r) => Right(NonEmptyList(r, Nil))
        case Left(_)  => c.as[List[HCursor]] flatMap decodeCursors
      }
    }
  }
}
