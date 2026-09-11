package views

import play.twirl.api.{Html, HtmlFormat}
import views.behaviours.ViewBehaviours
import views.html.MainTemplate

import scala.jdk.CollectionConverters._

class MainTemplateViewSpec extends ViewBehaviours  {

  val title = "Test page"
  val mainContent = Html("<p id='main-content'>hello</p>")

  private val view: MainTemplate = injector.instanceOf[MainTemplate]

  "MainTemplate" must {

    "render via apply with only the required arguments" in {
      val html = view(title)(mainContent)(fakeRequest, messages)
      html.toString must include("hello")
    }

    "render the page title in the <title> tag" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      doc.select("title").first.text must include(title)
    }

    "render the report-technical-issue link" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      assertRenderedByCssSelector(doc, "a.hmrc-report-technical-issue")
    }

    "render the service navigation component" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      assertRenderedByCssSelector(doc, ".govuk-service-navigation")
    }

    "request the service navigation component on every generated link to a shared PlatUI page" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))

      val sharedPagePaths = Seq(
        "/accessibility-statement/",
        "/contact/report-technical-problem",
        "/help/cookies",
        "/help/privacy",
        "/help/terms-and-conditions"
      )

      sharedPagePaths.foreach { path =>
        withClue(s"links to $path: ") {
          val hrefs = doc.select(s"""a[href*="$path"]""").eachAttr("href").asScala.toSeq
          hrefs must not be empty
          hrefs.foreach(_ must include("useServiceNavigation"))
        }
      }
    }

    "not render the back link when backLink is None" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      assertNotRenderedById(doc, "back")
    }

    "render the language toggle in the header (Welsh translation available)" in {
      val doc = asDocument(view(title)(mainContent)(fakeRequest, messages))
      val txt = doc.text()
      assert(txt.contains("ENG"))
      assert(txt.contains("CYM"))
    }

    "honour timeoutEnabled = false (no timeout dialog meta tag)" in {
      val doc = asDocument(view(title, timeoutEnabled = false)(mainContent)(fakeRequest, messages))
      doc.select("meta[name=hmrc-timeout-dialog]").isEmpty mustBe true
    }
  }
}