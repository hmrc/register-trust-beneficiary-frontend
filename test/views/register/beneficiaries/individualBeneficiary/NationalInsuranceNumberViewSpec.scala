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

import forms.NationalInsuranceNumberFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.beneficiaries.individualBeneficiary.NationalInsuranceNumberView

class NationalInsuranceNumberViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "individualBeneficiaryNationalInsuranceNumber"
  val index = 0
  val existingSettlorNinos = Seq("")
  val fullName: FullName = FullName("First", None, "Last")
  val name: String = fullName.toString

  val form: Form[String] =
    new NationalInsuranceNumberFormProvider().withPrefix("individualBeneficiaryNationalInsuranceNumber", emptyUserAnswers, index, existingSettlorNinos)

  "IndividualBeneficiaryNationalInsuranceNumberView view" must {

    val view = viewFor[NationalInsuranceNumberView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, fullName, index)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name)

    behave like pageWithBackLink(applyView(form))

    behave like stringPageWithDynamicTitle(form, applyView, messageKeyPrefix, name, Some(s"$messageKeyPrefix.hint"))

    behave like pageWithASubmitButton(applyView(form))

  }
}
