package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Try, Success, Failure}
import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.libs.ws._
import java.util.UUID
import org.joda.time.DateTime

import net.ruippeixotog.scalascraper.browser.Browser
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import net.ruippeixotog.scalascraper.dsl.DSL.Parse._

object Feed extends Controller {

	def url(url: String) = Action.async {
		val browser = new Browser
		val nb = 10 // Number of entries in the feed

		// Fetch the URL
		val holder = Try(WS.url(url).get)
		holder match {
			case Success(h) => h.map {
				case res if res.status == 200 => {
					// Parse title and body
					val doc = browser.parseString(res.body)
					val title: Option[String] = doc >?> text("title")
					val body: String = (doc >?> elements("body")).map(_.html) getOrElse res.body
		
					// Get or create a page for this url
					val page = models.Page.findByUrl(url) getOrElse {
						val p = new models.Page(UUID.randomUUID, url)
						models.Page.create(p)
						p
					}
					
					// Create a content
					val content = new models.Content(UUID.randomUUID, page.id, title, body, res.body)
					if (content.isNew) {
						models.Content.create(content)
					}
		
					// Get contents
					val contents = models.Content.getLastsByPage(page.id, nb)
		
					// Generate feed
					val entries = contents.map(c => models.Entry(c.id, c.title getOrElse page.url, c.fetched_at, ""))
					val feed_title: String = page.title getOrElse page.url
					val feed_updated: DateTime = contents.headOption.map(_.fetched_at) getOrElse page.created_at
					val feed = models.Feed(page.id, feed_title, page.created_at, feed_updated, page.url, entries)
		
					// Update page
					val newPage = page.copy(hits = page.hits + 1, last_fetch = content.fetched_at)
					models.Page.update(newPage)
		
					Ok(views.xml.atomFeed(feed, feed.entries.size < nb)).as("application/atom+xml")
				}
				case res => {
					Status(res.status)(res.statusText)
				}
			}
			case Failure(e) => Future(BadRequest(e.getMessage))
		}
	}

}
