package controllers

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.concurrent.duration._
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
		val nb = 10 // Number of entries in the feed
		val minimumFetchPeriod = 10.second//1.minute

		// If page exists in DB, check if it should be fetched
		val existingPage = models.Page.findByUrl(url)
		val shouldBeFetched = existingPage.filter(p => (p.last_fetch.plus(minimumFetchPeriod.toMillis) compareTo new DateTime) >= 0).isEmpty

		// Fetch and render, or render only
		if (shouldBeFetched) {
			fetchPage(url, existingPage, nb)
		}
		else {
			Future(renderFeed(existingPage.get, nb))
		}
	}

	/** Fetch a page, store the result and then render the feed */
	private def fetchPage(url: String, existingPage: Option[models.Page], nb: Int): Future[Result] = {
		// Fetch the URL
		val holder = Try(WS.url(url).get)
		holder match {
			case Success(h) => h.map {
				case res if res.status == 200 => {
					// Parse title and body
					val browser = new Browser
					val doc = browser.parseString(res.body)
					val title: Option[String] = doc >?> text("title")
					val body: String = (doc >?> elements("body")).map(_.html) getOrElse res.body
		
					// Get or create a page for this url
					val page = existingPage getOrElse {
						val p = new models.Page(UUID.randomUUID, url)
						models.Page.create(p)
						p
					}
					
					// Create a content
					val content = new models.Content(UUID.randomUUID, page.id, title, body, res.body)
					if (content.isNew) {
						models.Content.create(content)
					}
		
					// Update page
					val newPage = page.copy(hits = page.hits + 1, last_fetch = content.fetched_at)
					models.Page.update(newPage)
		
					// Render feed
					renderFeed(page, nb)
				}
				case res => {
					Status(res.status)(res.statusText)
				}
			}
			case Failure(e) => Future(BadRequest(e.getMessage))
		}
	}

	/** Render the feed */
	private def renderFeed(page: models.Page, nb: Int): Result = {
		val feed = models.Feed.generate(page, nb)
		Ok(views.xml.atomFeed(feed, feed.entries.size < nb)).as("application/atom+xml")
	}

}
