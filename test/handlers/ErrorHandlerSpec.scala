/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package handlers

import base.SpecBase
import play.api.i18n.{Messages, MessagesApi}
import views.html.{ErrorTemplate, PageNotFoundView}

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

class ErrorHandlerSpec extends SpecBase {

  private val messageApi: MessagesApi                 = app.injector.instanceOf[MessagesApi]
  private val errorTemplate: ErrorTemplate            = app.injector.instanceOf[ErrorTemplate]
  private val errorNotFoundTemplate: PageNotFoundView = app.injector.instanceOf[PageNotFoundView]
  private val errorHandler: ErrorHandler              = new ErrorHandler(messageApi, errorTemplate, errorNotFoundTemplate)

  "ErrorHandler" must {

    "return an error page" in {
      val result =
        Await.result(errorHandler.standardErrorTemplate("title", "heading", "message")(fakeRequest), 1.seconds)

      result.body must include("title")
      result.body must include("message")
    }

    "return a not found template" in {
      val result = Await.result(errorHandler.notFoundTemplate(fakeRequest), 1.seconds)

      val pageNotFoundTitle    = Messages("pageNotFound.title")(messages)
      val pageNotFoundHeading  = Messages("pageNotFound.heading")(messages)
      val pageNotFoundMessage1 = Messages("pageNotFound.p1")(messages)
      val pageNotFoundMessage2 = Messages("pageNotFound.p2")(messages)

      result.body must include(pageNotFoundTitle)
      result.body must include(pageNotFoundHeading)
      result.body must include(pageNotFoundMessage1)
      result.body must include(pageNotFoundMessage2)
    }

  }

}
