package spotify4s.domain



case class Track(
                  album: Album,
                  artists: List[Artists],
                  available_markets: List[String],
                  disc_number: Double,
                  duration_ms: Double,
                  explicit: Boolean,
                  external_ids: Map[String, String],
                  external_urls: Map[String, String],
                  href: String,
                  id: String,
                  is_local: Boolean,
                  name: String,
                  popularity: Double,
                  preview_url: String,
                  track_number: Double,
                  `type`: String,
                  uri: String
                )




