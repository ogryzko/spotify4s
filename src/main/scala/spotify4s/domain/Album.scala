package spotify4s.domain

case class Album(
                  album_type: String,
                  artists: List[Artists],
                  available_markets: List[String],
                  external_urls: Map[String, String],
                  href: String,
                  id: String,
                  images: List[Images],
                  name: String,
                  release_date: String,
                  release_date_precision: String,
                  `type`: String,
                  uri: String
                )
