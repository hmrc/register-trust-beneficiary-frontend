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

package views.register.beneficiaries.other.mld5

import forms.InternationalAddressFormProvider
import models.core.pages.InternationalAddress
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.NonUkAddressViewBehaviours
import views.html.register.beneficiaries.companyoremploymentrelated.company.NonUkAddressView

class InternationalAddressViewSpec extends NonUkAddressViewBehaviours {

  private val index = 0
  private val messageKeyPrefix = "site.address.international"
  private val name: String = "Company"

  override val form: Form[InternationalAddress] = new InternationalAddressFormProvider().apply()

  "NonUkAddressView" must {

    val view = viewFor[NonUkAddressView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, name, index, draftId)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like nonUkAddressPage(
      applyView,
      Some(messageKeyPrefix),
      name
    )

    behave like pageWithASubmitButton(applyView(form))
  }
}
