/*
 * Copyright 2021 HM Revenue & Customs
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

package views.register.beneficiaries.charityortrust.charity

import views.behaviours.ViewBehaviours
import views.html.register.beneficiaries.charityortrust.charity.CharityAnswersView

class CharityAnswersViewSpec extends ViewBehaviours {
  val index = 0

  "CharityAnswers view" must {

    val view = viewFor[CharityAnswersView](Some(emptyUserAnswers))

    val applyView = view.apply(index, fakeDraftId, Nil)(fakeRequest, messages)

    behave like normalPage(applyView, "charity.answers")

    behave like pageWithBackLink(applyView)
  }
}
