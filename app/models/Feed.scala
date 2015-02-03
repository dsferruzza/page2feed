package models

import java.util.UUID
import org.joda.time.DateTime

case class Entry(id: UUID, title: String, updated: DateTime, content: String)
case class Feed(id: UUID, title: String, created: DateTime, updated: DateTime, link: String, entries: List[Entry])

object Feed {
	/** Generate a feed for a given page */
	def generate(page: Page, nb: Int): Feed = {
		// Get contents
		val contents = Content.getLastsByPage(page.id, nb)

		// Generate feed
		val entries = contents.map(c => Entry(c.id, c.title getOrElse page.url, c.fetched_at, ""))
		val feed_title: String = page.title getOrElse page.url
		val feed_updated: DateTime = contents.headOption.map(_.fetched_at) getOrElse page.created_at
		Feed(page.id, feed_title, page.created_at, feed_updated, page.url, entries)
	}
}
