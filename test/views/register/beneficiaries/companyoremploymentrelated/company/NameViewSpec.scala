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

package views.register.beneficiaries.companyoremploymentrelated.company

import forms.StringFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.StringViewBehaviours
import views.html.register.beneficiaries.companyoremploymentrelated.company.NameView

class NameViewSpec extends StringViewBehaviours {

  val messageKeyPrefix = "companyBeneficiary.name"

  val form: Form[String] = new StringFormProvider().withPrefix(messageKeyPrefix, 105)
  val view: NameView = viewFor[NameView](Some(emptyUserAnswers))
  val index = 0

  "Name view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, NormalMode, index, draftId)(fakeRequest, messages)

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like stringPage(form, applyView, messageKeyPrefix)

    behave like pageWithASubmitButton(applyView(form))
  }

}
