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

package views.register.beneficiaries.companyoremploymentrelated.employmentRelated

import controllers.register.beneficiaries.companyoremploymentrelated.employmentRelated.routes
import forms.EmploymentRelatedBeneficiaryDescriptionFormProvider
import models.NormalMode
import models.core.pages.Description
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.register.beneficiaries.companyoremploymentrelated.employmentRelated.DescriptionView

class DescriptionViewSpec extends QuestionViewBehaviours[Description] {

  val messageKeyPrefix = "employmentRelatedBeneficiary.description"

  val form: Form[Description] = new EmploymentRelatedBeneficiaryDescriptionFormProvider().withPrefix(messageKeyPrefix)
  val view: DescriptionView = viewFor[DescriptionView](Some(emptyUserAnswers))
  val index = 0

  "Description view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, index, draftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithHint(form, applyView, messageKeyPrefix + ".hint")

    behave like pageWithTextFields(
      form,
      applyView,
      messageKeyPrefix,
      Seq(("description",None), ("description1",None), ("description2",None), ("description3",None), ("description4",None))
    )

    behave like pageWithASubmitButton(applyView(form))
  }

}
