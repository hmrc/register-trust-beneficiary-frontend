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

import forms.CountryFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.SelectCountryViewBehaviours
import views.html.register.beneficiaries.charityortrust.charity.CountryOfResidenceView

class CountryOfResidenceViewSpec extends SelectCountryViewBehaviours {

  val prefix = "charity.countryOfResidence"
  val index = 0
  val charityName = "Test"

  val form = new CountryFormProvider().withPrefix(prefix)

  "countryOfResidence view" must {

    val view = viewFor[CountryOfResidenceView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, fakeDraftId, index, charityName)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), prefix, charityName)

    behave like pageWithBackLink(applyView(form))

    behave like selectCountryPage(form, applyView, prefix, charityName)

    behave like pageWithASubmitButton(applyView(form))
  }
}