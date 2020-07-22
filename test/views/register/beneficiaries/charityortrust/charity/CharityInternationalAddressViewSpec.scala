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

package views.register.beneficiaries.charityortrust.charity

import controllers.register.beneficiaries.charityOrTrust.charity.routes
import forms.InternationalAddressFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.InternationalAddressViewBehaviours
import views.html.register.beneficiaries.charityortrust.charity.CharityInternationalAddressView

class CharityInternationalAddressViewSpec extends InternationalAddressViewBehaviours {

  val prefix = "charity.internationalAddress"
  val charityName = "Test"

  val index: Int = 0

  override val form = new InternationalAddressFormProvider()()

  "PropertyOrLandInternationalAddressView" must {

    val view = viewFor[CharityInternationalAddressView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, NormalMode, fakeDraftId, index, charityName)(fakeRequest, messages)

    behave like pageWithBackLink(applyView(form))

    behave like internationalAddress(
      applyView,
      Some(prefix),
      routes.CharityInternationalAddressController.onSubmit(NormalMode, index, fakeDraftId).url,
      charityName
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
