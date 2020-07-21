/*
 * Copyright 2020 HM Revenue & Customs
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

package views.register.beneficiaries.CharityOrTrust

import forms.CharityOrTrustFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.{ViewBehaviours, YesNoViewBehaviours}
import views.html.register.beneficiaries.charityOrTrust.CharityOrTrustView

class CharityOrTrustViewSpec extends ViewBehaviours {

  val messageKeyPrefix = "charityOrTrust"
  val index = 0

  val form = new CharityOrTrustFormProvider()()


  "CharityOrTrust view" must {

    val view = viewFor[CharityOrTrustView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, fakeDraftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithASubmitButton(applyView(form))
  }
}
