package models

import java.util.UUID
import org.joda.time.DateTime

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

case class Entry(id: UUID, title: String, updated: DateTime, content: String)
case class Feed(id: UUID, title: String, created: DateTime, updated: DateTime, link: String, entries: List[Entry])

object Feed {
	/** Generate a feed for a given page */
	def generate(page: Page, nb: Int): Feed = {
		// Get contents
		val contents = Content.getLastsByPage(page.id, nb)

		val diffs = Diff.fromContents(contents)

		// Generate feed
		val entries = contents.filter(!_.first).map(c => Entry(c.id, c.title getOrElse page.url, c.fetched_at, diffs.get(c.id).map(_.diff) getOrElse ""))
		val feed_title: String = page.title getOrElse page.url
		val feed_updated: DateTime = contents.headOption.map(_.fetched_at) getOrElse page.created_at
		Feed(page.id, feed_title, page.created_at, feed_updated, page.url, entries)
	}
}

case class Diff(from: Content, to: Content, diff: String)
object Diff {
	/** Create diffs from a list of contents */
	def fromContents(contents: List[Content]): Map[UUID, Diff] = {
		Content.prepareForDiff(contents).map {
			case (c1, c2) => {
				val browser = new Browser
				val from = browser.parseString(c1.body)
				val to = browser.parseString(c2.body)

				val diff = OwenDiff.Diff.diff(from.html.lines.toList, to.html.lines.toList).filter {
					case OwenDiff.Equal(_, _, _, _) => false
					case _ => true
				}.mkString("\n")
				(c2.id, Diff(c1, c2, diff))
			}
		}.toMap
	}
}
