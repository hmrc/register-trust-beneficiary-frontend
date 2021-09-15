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

import forms.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.register.beneficiaries.charityortrust.charity.CountryOfResidenceYesNoView

class CountryOfResidenceYesNoViewSpec extends YesNoViewBehaviours {

  val prefix = "charity.countryOfResidenceYesNo"
  val index = 0
  val charityName = "Test"

  val form: Form[Boolean] = new YesNoFormProvider().withPrefix(prefix)

  "countryOfResidenceYesNo view" must {

    val view = viewFor[CountryOfResidenceYesNoView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, index, charityName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, charityName)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, prefix, Some(prefix), Seq(charityName))

    behave like pageWithASubmitButton(applyView(form))
  }
}