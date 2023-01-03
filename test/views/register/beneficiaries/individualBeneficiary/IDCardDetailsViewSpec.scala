/*
 * Copyright 2023 HM Revenue & Customs
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

package views.register.beneficiaries.individualBeneficiary

import forms.PassportOrIdCardFormProvider
import models.core.pages.FullName
import models.registration.pages.PassportOrIdCardDetails
import play.api.data.Form
import play.twirl.api.HtmlFormat
import utils.InputOption
import utils.countryOptions.CountryOptionsNonUK
import views.behaviours.QuestionViewBehaviours
import views.html.register.beneficiaries.individualBeneficiary.IDCardDetailsView

class IDCardDetailsViewSpec extends QuestionViewBehaviours[PassportOrIdCardDetails] {

  private val messageKeyPrefix = "individualBeneficiaryIDCardDetails"
  private val index = 0
  private val name = FullName("First", Some("Middle"), "Last")

  override val form = new PassportOrIdCardFormProvider(frontendAppConfig)("individualBeneficiaryIDCardDetails")

  "IDCardDetailsView" must {

    val view = viewFor[IDCardDetailsView](Some(emptyUserAnswers))

    val countryOptions: Seq[InputOption] = app.injector.instanceOf[CountryOptionsNonUK].options

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, countryOptions, fakeDraftId, index, name)(fakeRequest, messages)

    val applyViewF = (form : Form[_]) => applyView(form)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name.toString)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithPassportOrIDCardDetailsFields(
      form,
      applyViewF,
      messageKeyPrefix,
      Seq(("country", None), ("number", None)),
      "expiryDate",
      name.toString
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
