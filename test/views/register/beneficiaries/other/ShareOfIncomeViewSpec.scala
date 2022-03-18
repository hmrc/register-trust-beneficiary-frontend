/*
 * Copyright 2022 HM Revenue & Customs
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

package views.register.beneficiaries.other

import forms.IncomePercentageFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.IntViewBehaviours
import views.html.register.beneficiaries.other.ShareOfIncomeView

class ShareOfIncomeViewSpec extends IntViewBehaviours {

  private val index = 0
  private val messageKeyPrefix = "otherBeneficiary.shareOfIncome"
  private val description = "Other"

  override val form: Form[Int] = new IncomePercentageFormProvider().withPrefix(messageKeyPrefix)

  "ShareOfIncome view" must {
    val view = viewFor[ShareOfIncomeView](Some(emptyUserAnswers))

    def applyView(form: Form[_]): HtmlFormat.Appendable =
      view.apply(form, description, index, draftId)(fakeRequest, messages)

    behave like dynamicTitlePage(applyView(form), messageKeyPrefix, description)

    behave like pageWithBackLink(applyView(form))

    behave like intPage(form, applyView, messageKeyPrefix, description)

    behave like pageWithASubmitButton(applyView(form))
  }

}
