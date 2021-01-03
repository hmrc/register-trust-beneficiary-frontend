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

package views.register.beneficiaries.individualBeneficiary

import java.time.LocalDate

import forms.DateOfBirthFormProvider
import models.core.pages.FullName
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.beneficiaries.individualBeneficiary.DateOfBirthView

class DateOfBirthViewSpec extends QuestionViewBehaviours[LocalDate] {

  val messageKeyPrefix = "individualBeneficiaryDateOfBirth"
  val index = 0
  val fullName: FullName = FullName("First", None, "Last")
  val name: String = fullName.toString

  val form = new DateOfBirthFormProvider(frontendAppConfig).withPrefix("individualBeneficiaryDateOfBirth")

  "IndividualBeneficiaryDateOfBirthView view" must {

    val view = viewFor[DateOfBirthView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, fakeDraftId, fullName, index)(fakeRequest, messages)

    val applyViewF = (form : Form[_]) => applyView(form)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, name, "hint")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithDateFields(form, applyViewF,
      messageKeyPrefix,
      "value",
      name
    )

    behave like pageWithASubmitButton(applyView(form))

  }
}
